package com.ldz.biz.controller;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.ldz.biz.constant.Status;
import com.ldz.biz.model.StudentAllModel;
import com.ldz.biz.model.TraineeInformation;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.biz.service.DataStaService;
import com.ldz.biz.service.TraineeInformationService;
import com.ldz.sys.mapper.SysZdxmMapper;
import com.ldz.sys.model.SysZdxm;
import com.ldz.sys.service.JgService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 此控制层不需要用户信息验证
 *
 * @author Lee
 * @date 2017年8月12日
 */
@RestController
@RequestMapping("/pub")
public class BizMainController {
    @Value("${staticPath}")
    private String path;

    @Autowired
    private StringRedisTemplate redisDao;

    @Autowired
    private TraineeInformationService informationService;

    @Autowired
    private BizLcJlService jlService;

    @Autowired
    private DataStaService staService;

    @Autowired
    private SysZdxmMapper zdxmMapper;

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private JgService jgService;

    @Autowired
    private BizLcWxjlService wxjlService;

    @GetMapping("/saveJx")
    public ApiResponse<String> saveJx() throws IOException {

        File f = new File("C://jx.txt");
        List<String> list = FileUtils.readLines(f, "UTF-8");
        list.forEach(s -> {
            if (StringUtils.isNotBlank(s)) {
                String[] split = s.split(",");
                SysZdxm sysZdxm = new SysZdxm();
                sysZdxm.setZdId(idWorker.nextId() + "");
                sysZdxm.setZdlmdm("ZDCLK1017");
                sysZdxm.setZddm(split[0]);
                sysZdxm.setZdmc(split[1]);
                sysZdxm.setCjsj(new Date());
                sysZdxm.setCjr("admini-超级管理员");
                zdxmMapper.insert(sysZdxm);
            }
        });

        return ApiResponse.success();
    }


    @RequestMapping(value = "/getTime", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<String> getTime(String type) {
        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("操作成功");
        res.setResult(DateUtils.getDateStr(new Date(), type));
        res.setCode(200);
        return res;
    }

    /**
     * 获取报号API的 token
     *
     * @return
     */
    @RequestMapping(value = "/getvoicetkey", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<String> getVoicetKey() {
        String access_token = "";
        try {
            String result = redisDao.boundValueOps("getvoicetkey_").get();
            if (StringUtils.isBlank(result)) {
                String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id=NfM00GIoLVzHoFDyKiRP85Vq&client_secret=Xxatb330uVgXGue7EYkh05LKGEX15WTe";
                result = HttpUtil.get(url);
                if (StringUtils.isNotBlank(result)) {
                    redisDao.boundValueOps("getvoicetkey_").set(result, 15, TimeUnit.DAYS);//设备邀请码，为1天过期
                }
            }
            System.out.println("--------------\n" + result);
            Map<?, ?> bean = JsonUtil.toBean(result, Map.class);
            //        "access_token": "24.54f59fd63ed8b30c76483560ae6ef23b.2592000.1548657983.282335-10267277",
            try {
                access_token = (String) bean.get("access_token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(access_token)) {
            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("操作成功");
            res.setResult(access_token);
            res.setCode(200);
            return res;
        } else {
            return ApiResponse.fail("获取token失败");
        }
    }

    /**
     * 服务器当前时间的时间戳
     *
     * @return
     */
    @RequestMapping(value = "/getTimeMillis", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Long> getDateTime() {
        long time = System.currentTimeMillis();
        return ApiResponse.success(time);
    }

    /**
     * 导出结果
     *
     * @param key      需要下载的key值
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/exportResult/{key}", method = {RequestMethod.GET})
    public void export(@PathVariable("key") String key, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String data = redisDao.boundValueOps(key).get();
        String fileName = redisDao.boundValueOps(key + "_name").get();

        if (org.apache.commons.lang3.StringUtils.isEmpty(data)) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            OutputStream ps = response.getOutputStream();
            ps.write("您好,您所导出结果只有一天的有效期，请确认您的地址是否过期".getBytes("UTF-8"));
            return;
        }
        List<Map<Integer, String>> dataList = JsonUtil.toList(data, Map.class);

        if (org.apache.commons.lang3.StringUtils.isEmpty(fileName)) {
            fileName = DateUtils.getToday("yyyyMMddHHmmss");
        }
//        fileName += "处理结果";
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, "结果导出", dataList);
    }

    /**
     * 导出结果
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/exportResult", method = {RequestMethod.GET})
    public void export(HttpServletRequest request, HttpServletResponse response, Page<TraineeInformation> pager) throws IOException {


        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(Status.SIGN_UP, "报名中");
        statusMap.put("00", "受理中");
        statusMap.put("0", "受理中");
        statusMap.put("10", "科目一学习中");
        statusMap.put("20", "科目二学习中");
        statusMap.put("30", "科目三学习中");
        statusMap.put("40", "科目四学习中");
        statusMap.put("50", "结业");
        statusMap.put("60", "退学");

        pager.setPageSize(99999);
        ApiResponse<List<TraineeInformation>> pagerList = informationService.pager(pager);
        Map<String, String> tableName = Maps.newLinkedHashMap();
        tableName.put("name", "姓名");
        tableName.put("idCardNo", "证件号码");
        tableName.put("jgmc", "报名点");
        tableName.put("serialNum", "流水号");
        tableName.put("gender", "性别");
        tableName.put("source", "本地/外地");
        tableName.put("carType", "车型");
        tableName.put("registrationTime", "报名时间");
        tableName.put("status", "状态");
        tableName.put("recordLib", "档案存放");
        tableName.put("indateEndTime", "有效期");
        tableName.put("secondSubjectCoach", "科二教练员");
        tableName.put("thirdSubjectCoach", "科三教练员");
        tableName.put("chargeStatus", "报名费");
        tableName.put("firType", "科一初考费");
        tableName.put("secType", "科二初考费");
        tableName.put("thirdType", "科三初考费");
        tableName.put("firSub", "科一状态");
        tableName.put("firSubTestTime", "科一考试时间");
        tableName.put("firSubTestNum", "科一考试次数");
        tableName.put("secSub", "科二状态");
        tableName.put("secSubTestTime", "科二考试时间");
        tableName.put("secSubTestNum", "科二考试次数");
        tableName.put("thirdSub", "科三状态");
        tableName.put("thirdSubTestTime", "科三考试时间");
        tableName.put("thirdSubTestNum", "科三考试次数");
        tableName.put("forthSub", "科四状态");
        tableName.put("remark", "备注");
        List<Map<String, String>> data = new ArrayList<>();
        if (pagerList != null && pagerList.isSuccess()) {
            List<TraineeInformation> dataList = pagerList.getPage().getList();
            int seq = 1;
            for (TraineeInformation l : dataList) {
                Map<String, String> m = new HashMap<>();
                m.put("seq", seq + "");//序列
                m.put("name", l.getName());//姓名
                m.put("idCardNo", l.getIdCardNo());//证件号码
                m.put("jgmc", l.getJgmc());//报名点
                String serialNum = l.getSerialNum();
                m.put("serialNum", StringUtils.isBlank(serialNum) ? "待受理" : serialNum);//流水号
                String gender = l.getGender();
                m.put("gender", StringUtils.equals(gender, "10") ? "男" : "女");//性别
                String source = l.getSource();
                m.put("source", StringUtils.equals(source, "00") ? "本地" : "外地");//本地/外地
                String carType = l.getCarType();
                if (StringUtils.isEmpty(carType)) {
                    carType = "-";
                }
                m.put("carType", carType);//车型
                m.put("registrationTime", l.getRegistrationTime());//l.getRegistrationTime()   报名时间
                String status = l.getStatus();
                m.put("status", StringUtils.isNotEmpty(status) ? statusMap.get(status) : "-");
                m.put("recordLib", l.getRecordLib());
                m.put("indateEndTime", l.getIndateEndTime());//有效期
                m.put("secondSubjectCoach", l.getSecondSubjectCoach());//科二教练员
                m.put("thirdSubjectCoach", l.getThirdSubjectCoach());//科三教练员
                String chargeStatus = l.getChargeStatus();
                String chargeName = "未结清";
                if (StringUtils.isNotEmpty(chargeStatus)) {
                    if (StringUtils.equals(chargeStatus, "10") && StringUtils.equals(l.getArrearage(), "00")) {
                        chargeName = "已结清";
                    }
                } else {
                    chargeName = "-";
                }
                m.put("chargeStatus", chargeName);//报名费
                String firTypeName = "已缴";
                if (StringUtils.equals(l.getAcceptStatus(), "10") && StringUtils.isEmpty(l.getFirSubPaymentTime())) {  //todo
                    firTypeName = "待缴";
                }
                m.put("firType", firTypeName);//科一初考费
                String secTypeName = "已缴";
                if (StringUtils.equals(l.getFirSub(), "40") && StringUtils.isEmpty(l.getSecSubPaymentTime())) {
                    secTypeName = "待缴";
                }
                m.put("secType", secTypeName);//科二初考费
                String thirdTypeName = "已缴";
                if (StringUtils.equals(l.getSecSub(), "40") && StringUtils.isEmpty(l.getThirdSubPaymentTime())) {
                    thirdTypeName = "待缴";
                }
                m.put("thirdType", thirdTypeName);//科三初考费
                String firSubName = "-";
                if (StringUtils.equals(l.getFirSub(), "00")) {
                    firSubName = "已缴费";
                } else if (StringUtils.equals(l.getFirSub(), "10")) {
                    firSubName = "学习中";
                } else if (StringUtils.equals(l.getFirSub(), "20")) {
                    firSubName = "已约考";
                } else if (StringUtils.equals(l.getFirSub(), "30")) {
                    firSubName = "不合格";
                } else if (StringUtils.equals(l.getFirSub(), "40")) {
                    firSubName = "合格";
                }
                m.put("firSub", firSubName);//科一状态
                String firSubTestTime = l.getFirSubTestTime();
                if (StringUtils.isEmpty(firSubTestTime)) {
                    firSubTestTime = "-";
                }
                m.put("firSubTestTime", firSubTestTime);//科一考试时间
                m.put("firSubTestNum", l.getFirSubTestNum() == null ? 0 + "" : l.getFirSubTestNum() + "");//科一考试次数
                String secSubName = "-";
                if (StringUtils.equals(l.getSecSub(), "00")) {
                    secSubName = "已缴费";
                } else if (StringUtils.equals(l.getSecSub(), "10")) {
                    secSubName = "学习中";
                } else if (StringUtils.equals(l.getSecSub(), "20")) {
                    secSubName = "已约考";
                } else if (StringUtils.equals(l.getSecSub(), "30")) {
                    secSubName = "不合格";
                } else if (StringUtils.equals(l.getSecSub(), "40")) {
                    secSubName = "合格";
                }

                m.put("secSub", secSubName);//科二状态
                String secSubTestTime = l.getSecSubTestTime();
                if (StringUtils.isEmpty(secSubTestTime)) {
                    secSubTestTime = "-";
                }
                m.put("secSubTestTime", secSubTestTime);//科二考试时间
                m.put("secSubTestNum", l.getSecSubTestNum() == null ? 0 + "" : l.getSecSubTestNum() + "");//科二考试次数
                String thirdSubName = "-";
                if (StringUtils.equals(l.getThirdSub(), "00")) {
                    thirdSubName = "已缴费";
                } else if (StringUtils.equals(l.getThirdSub(), "10")) {
                    thirdSubName = "学习中";
                } else if (StringUtils.equals(l.getThirdSub(), "20")) {
                    thirdSubName = "已约考";
                } else if (StringUtils.equals(l.getThirdSub(), "30")) {
                    thirdSubName = "不合格";
                } else if (StringUtils.equals(l.getThirdSub(), "40")) {
                    thirdSubName = "合格";
                }
                m.put("thirdSub", thirdSubName);//科三状态
                String thirdSubTestTime = l.getThirdSubTestTime();
                if (StringUtils.isEmpty(thirdSubTestTime)) {
                    thirdSubTestTime = "-";
                }
                m.put("thirdSubTestTime", thirdSubTestTime);//科三考试时间
                m.put("thirdSubTestNum", l.getThirdSubTestNum() == null ? 0 + "0" : l.getThirdSubTestNum() + "");//科三考试次数
                String forthSubName = "-";
                if (StringUtils.equals(l.getForthSub(), "00")) {
                    forthSubName = "已约考";
                } else if (StringUtils.equals(l.getForthSub(), "20")) {
                    forthSubName = "合格";

                }
                m.put("forthSub", forthSubName);//科四状态
                m.put("remark", l.getRemark());//备注

                data.add(m);
                seq++;
            }
        }


//        为excel表格增加选项卡
        List<Map<String, Object>> sheetList = new ArrayList<>();
//        全部选项卡
        Map<String, Object> sheelMap = new HashMap<>();
        sheelMap.put("name", "全部");
        sheelMap.put("sheelMap", tableName);
        sheetList.add(sheelMap);
//        基础选项卡
        Map<String, Object> sheelMapInfo = new HashMap<>();
        Map<String, String> tableNameInfo = Maps.newLinkedHashMap();
        tableNameInfo.put("name", "姓名");
        tableNameInfo.put("idCardNo", "证件号码");
        tableNameInfo.put("jgmc", "报名点");
        tableNameInfo.put("serialNum", "流水号");
        tableNameInfo.put("gender", "性别");
        tableNameInfo.put("source", "本地/外地");
        tableNameInfo.put("carType", "车型");
        tableNameInfo.put("registrationTime", "报名时间");
        tableNameInfo.put("status", "状态");
        tableNameInfo.put("recordLib", "档案存放");
        tableNameInfo.put("indateEndTime", "有效期");
        tableNameInfo.put("secondSubjectCoach", "科二教练员");
        tableNameInfo.put("thirdSubjectCoach", "科三教练员");
        tableNameInfo.put("remark", "备注");
        sheelMapInfo.put("name", "基本信息");
        sheelMapInfo.put("sheelMap", tableNameInfo);
        sheetList.add(sheelMapInfo);

//        缴费数据
        Map<String, Object> sheelMapPay = new HashMap<>();
        Map<String, String> tableNamePay = Maps.newLinkedHashMap();
        tableNamePay.put("seq", "序列");
        tableNamePay.put("name", "姓名");
        tableNamePay.put("idCardNo", "证件号码");
        tableNamePay.put("jgmc", "报名点");
        tableNamePay.put("serialNum", "流水号");
        tableNamePay.put("gender", "性别");
        tableNamePay.put("source", "本地/外地");
        tableNamePay.put("carType", "车型");
        tableNamePay.put("chargeStatus", "报名费");
        tableNamePay.put("firType", "科一初考费");
        tableNamePay.put("secType", "科二初考费");
        tableNamePay.put("thirdType", "科三初考费");

        tableNamePay.put("remark", "备注");
        sheelMapPay.put("name", "缴费数据");
        sheelMapPay.put("sheelMap", tableNamePay);
        sheetList.add(sheelMapPay);

        //        考试数据
        Map<String, Object> sheelMapExa = new HashMap<>();
        Map<String, String> tableNameExa = Maps.newLinkedHashMap();
        tableNameExa.put("name", "姓名");
        tableNameExa.put("idCardNo", "证件号码");
        tableNameExa.put("jgmc", "报名点");
        tableNameExa.put("serialNum", "流水号");
        tableNameExa.put("gender", "性别");
        tableNameExa.put("source", "本地/外地");
        tableNameExa.put("carType", "车型");

        tableNameExa.put("firSub", "科一状态");
        tableNameExa.put("firSubTestTime", "科一考试时间");
        tableNameExa.put("firSubTestNum", "科一考试次数");
        tableNameExa.put("secSub", "科二状态");
        tableNameExa.put("secSubTestTime", "科二考试时间");
        tableNameExa.put("secSubTestNum", "科二考试次数");
        tableNameExa.put("thirdSub", "科三状态");
        tableNameExa.put("thirdSubTestTime", "科三考试时间");
        tableNameExa.put("thirdSubTestNum", "科三考试次数");
        tableNameExa.put("forthSub", "科四状态");
        tableNameExa.put("remark", "备注");


        sheelMapExa.put("name", "考试数据");
        sheelMapExa.put("sheelMap", tableNameExa);
        sheetList.add(sheelMapExa);


        String fileName = java.net.URLEncoder.encode(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls", "UTF-8");
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheetArray(out, data, sheetList);
    }


    /**
     * 科目待缴费学员导出
     */
    @GetMapping("/exportTestExcel")
    public void exportTestExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SimpleCondition condition = new SimpleCondition(TraineeInformation.class);
        String sign = request.getParameter("sign");
        String jgmcLike = request.getParameter("jgmcLike");
        String idCardNoLike = request.getParameter("idCardNoLike");
        String jgdm = request.getParameter("jgdm");
        String firSubTestTimeLike = request.getParameter("firSubTestTimeLike");
        String secSubTestTimeLike = request.getParameter("secSubTestTimeLike");
        String thirdSubTestTimeLike = request.getParameter("thirdSubTestTimeLike");
        if (StringUtils.isNotBlank(jgmcLike)) {
            condition.like(TraineeInformation.InnerColumn.jgmc, jgmcLike);
        }
        if (StringUtils.isNotBlank(jgdm)) {
            condition.eq(TraineeInformation.InnerColumn.jgdm, jgdm);
        }
        if (StringUtils.isNotBlank(idCardNoLike)) {
            condition.like(TraineeInformation.InnerColumn.idCardNo, idCardNoLike);
        }
        List<Map<Integer, String>> data = new ArrayList<>();
        if (StringUtils.equals(sign, "7")) {
            condition.and().andCondition(" (status='10' and (fir_sub_test_num =0 or fir_sub_test_num =1)  and (fir_sub is null or fir_sub = '')) or fir_sub='30' and fir_sub='40'");
            condition.and().andCondition("fir_sub_payment_time is  null or fir_sub_payment_time = '' ");
            condition.eq(TraineeInformation.InnerColumn.acceptStatus, "20");
            if (StringUtils.isNotBlank(firSubTestTimeLike)) {
                condition.like(TraineeInformation.InnerColumn.firSubTestTime, firSubTestTimeLike);
            }
        } else if (StringUtils.equals(sign, "2")) {
            condition.and().andCondition(" (sec_sub = '10' and status='20' and sec_sub_test_num = 1) or sec_sub='30' or sec_sub = '40'");
            condition.and().andCondition("sec_sub_payment_time is null or sec_sub_payment_time = ''");
            if (StringUtils.isNotBlank(secSubTestTimeLike)) {
                condition.like(TraineeInformation.InnerColumn.secSubTestTime, secSubTestTimeLike);
            }
        } else if (StringUtils.equals(sign, "3")) {
            condition.and().andCondition(" (third_sub_test_num =1 and third_sub = '10' and status='30') or third_sub='30' or third_sub='40'");
            condition.and().andCondition("third_sub_payment_time is null or third_sub_payment_time = ''"); // 考试费未缴纳
            if (StringUtils.isNotBlank(thirdSubTestTimeLike)) {
                condition.like(TraineeInformation.InnerColumn.thirdSubTestTime, thirdSubTestTimeLike);
            }
        }
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "序号");
        map.put(1, "姓名");
        map.put(2, "证件号码");
        map.put(3, "车型");
        map.put(4, "报名点");
        map.put(5, "金额");
        map.put(6, "考试科目");
        map.put(7, "考试时间");
        map.put(8, "机构代码");
        data.add(map);
        List<Map<Integer, String>> coll = new ArrayList<>();
        List<TraineeInformation> informations = informationService.findByCondition(condition);
        for (int i = 0; i < informations.size(); i++) {
            TraineeInformation information = informations.get(i);
            Map<Integer, String> m = new HashMap<>();
            m.put(0, i + 1 + "");
            m.put(1, information.getName());
            m.put(2, information.getIdCardNo());
            m.put(3, information.getCarType());
            m.put(4, information.getJgmc());
            m.put(8, information.getJgdm());
            if (StringUtils.equals(sign, "7")) {
                m.put(5, "120");
                m.put(6, "科目一");
                if (StringUtils.isNotBlank(information.getFirSubTestTime())) {
                    if (DateUtils.getDateStr(new Date(), "yyyy-MM-dd").compareTo(information.getFirSubTestTime().substring(0, 10)) <= 0) {
                        m.put(7, information.getFirSubTestTime());
                    } else {
                        m.put(7, "");
                    }
                } else {
                    m.put(7, "");
                }
            } else if (StringUtils.equals(sign, "2")) {
                m.put(5, "150");
                m.put(6, "科目二");
                if (StringUtils.isNotBlank(information.getSecSubTestTime())) {
                    if (DateUtils.getDateStr(new Date(), "yyyy-MM-dd").compareTo(information.getSecSubTestTime().substring(0, 10)) <= 0) {
                        m.put(7, information.getSecSubTestTime());
                    } else {
                        m.put(7, "");
                    }
                } else {
                    m.put(7, "");
                }
            } else if (StringUtils.equals(sign, "3")) {
                m.put(5, "232");
                m.put(6, "科目三");
                if (StringUtils.isNotBlank(information.getThirdSubTestTime())) {
                    if (DateUtils.getDateStr(new Date(), "yyyy-MM-dd").compareTo(information.getThirdSubTestTime().substring(0, 10)) <= 0) {
                        m.put(7, information.getThirdSubTestTime());
                    } else {
                        m.put(7, "");
                    }
                } else {
                    m.put(7, "");
                }
            }

            coll.add(m);


        }
        List<Map<Integer, String>> collect = coll.stream().sorted((o1, o2) -> o1.get(8).compareTo(o2.get(8))).collect(Collectors.toList());
        data.addAll(collect);
        String fileName = java.net.URLEncoder.encode(("7".equals(sign) ? "科目一" : "2".equals(sign) ? "科目二" : "3".equals(sign) ? "科目三" : "") + "考试待缴", "UTF-8");
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, ("7".equals(sign) ? "科目一" : "2".equals(sign) ? "科目二" : "3".equals(sign) ? "科目三" : "") + "考试代缴", data);
    }

    @GetMapping("/exportBigCarExcel")
    public void exportBigCarExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> m = new HashMap<>();
        m.put("s00", "待受理");
        m.put("s10", "科目一");
        m.put("s20", "科目二");
        m.put("s30", "科目三");
        m.put("s40", "科目四");
        m.put("100", "已缴费");
        m.put("110", "已缴费未约考");
        m.put("120", "已约考");
        m.put("130", "不合格");
        m.put("140", "合格");
        m.put("200", "未约考");
        m.put("210", "已约考");
        m.put("220", "约考并缴费");
        m.put("230", "不合格");
        m.put("240", "合格");
        m.put("400", "已约考");
        m.put("410", "不合格");
        m.put("420", "合格");

        SimpleCondition condition = new SimpleCondition(TraineeInformation.class);
        condition.in(TraineeInformation.InnerColumn.carType, "A1", "A2", "A3", "B2");
        condition.in(TraineeInformation.InnerColumn.status, "00", "10", "20", "30", "40");
        condition.setOrderByClause(" status asc");
        List<TraineeInformation> list = informationService.findByCondition(condition);
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "队号");
        map.put(1, "姓名");
        map.put(2, "身份证号");
        map.put(3, "车型");
        map.put(4, "流水号");
        map.put(5, "当前状态");
        map.put(6, "科一");
        map.put(7, "科二");
        map.put(8, "科三");
        map.put(9, "科四");
        data.add(map);
        for (TraineeInformation traineeInformation : list) {
            Map<Integer, String> dataMap = new HashMap<>();
            dataMap.put(0, traineeInformation.getJgmc());
            dataMap.put(1, traineeInformation.getName());
            dataMap.put(2, traineeInformation.getIdCardNo());
            dataMap.put(3, traineeInformation.getCarType());
            dataMap.put(4, traineeInformation.getSerialNum());
            dataMap.put(5, m.get("s" + traineeInformation.getStatus()));
            dataMap.put(6, StringUtils.isBlank(traineeInformation.getFirSub()) ? "无数据" : m.get("1" + traineeInformation.getFirSub()));
            dataMap.put(7, StringUtils.isBlank(traineeInformation.getSecSub()) ? "无数据" : m.get("2" + traineeInformation.getSecSub()));
            dataMap.put(8, StringUtils.isBlank(traineeInformation.getThirdSub()) ? "无数据" : m.get("2" + traineeInformation.getThirdSub()));
            dataMap.put(9, StringUtils.isBlank(traineeInformation.getForthSub()) ? "无数据" : m.get("4" + traineeInformation.getForthSub()));
            data.add(dataMap);
        }

        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String("大车".getBytes("utf-8"), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, "大车情况", data);
    }

    /**
     * 今日招生统计 Excel 导出
     */
    @GetMapping("/exportStudentCount")
    public void exportStudentCount(String startTime, String endTime, String jgdm, String[] lx, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {

        ApiResponse<List<StudentAllModel>> studentCount = staService.getStudentCount(startTime, endTime, jgdm, lx);

        List<StudentAllModel> models = studentCount.getResult();
        List<Map<Integer, String>> dataList = new ArrayList<>();

        Map<Integer, String> titleMap = new HashMap<>();
        titleMap.put(0, "报名点");
        titleMap.put(1, "招生人数");
        titleMap.put(2, "A1");
        titleMap.put(3, "A2");
        titleMap.put(4, "A3");
        titleMap.put(5, "B2");
        titleMap.put(6, "C1");
        titleMap.put(7, "C2");
        dataList.add(titleMap);
        if (CollectionUtils.isNotEmpty(models)) {
            models.forEach(studentAllModel -> {
                Map<Integer, String> dataMap = new HashMap<>();
                dataMap.put(0, studentAllModel.getJgmc());
                dataMap.put(1, studentAllModel.getAll() + "");
                dataMap.put(2, studentAllModel.getA1() + "");
                dataMap.put(3, studentAllModel.getA2() + "");
                dataMap.put(4, studentAllModel.getA3() + "");
                dataMap.put(5, studentAllModel.getB2() + "");
                dataMap.put(6, studentAllModel.getC1() + "");
                dataMap.put(7, studentAllModel.getC2() + "");
                dataList.add(dataMap);
            });
        }
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String((startTime.substring(0, 10) + "招生").getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, "今日招生", dataList);
    }

}
