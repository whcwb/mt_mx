package com.ldz.biz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizJlCzMapper;
import com.ldz.biz.mapper.BizLcWxjlMapper;
import com.ldz.biz.model.BizJlCz;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.sys.model.SysYh;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.commonUtil.EncryptUtil;
import com.ldz.util.commonUtil.ExcelUtil;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BizLcWxjlServiceImpl extends BaseServiceImpl<BizLcWxjl, String> implements BizLcWxjlService {

    @Autowired
    private BizLcWxjlMapper baseMapper;
    @Autowired
    private BizJlCzMapper czMapper;

    @Override
    protected Mapper<BizLcWxjl> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public ApiResponse<String> saveEntity(BizLcWxjl entity) {
        SysYh user = getCurrentUser();
        RuntimeCheck.ifBlank(entity.getJlXm(), "教练姓名不能为空");
        RuntimeCheck.ifBlank(entity.getJlLxdh(), "教练联系电话不能为空");
        List<BizLcWxjl> wxjls = findEq(BizLcWxjl.InnerColumn.jlLxdh, entity.getJlLxdh());
        if (CollectionUtils.isNotEmpty(wxjls)) {
            return ApiResponse.fail( wxjls.get(0).getJlJx() + "-" + wxjls.get(0).getJlXm() + "在系统中已存在该手机号码(" + entity.getJlLxdh() +  ") , 请勿重复绑定");
        }
        entity.setId(genId());
        entity.setCjr(user.getZh() + "-" + user.getXm());
        entity.setCjsj(DateUtils.getNowTime());
        if (StringUtils.isBlank(entity.getJlJx())) {
            entity.setJlJx("无驾校");
        }
        save(entity);
        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("操作成功");
        res.setResult(entity.getId());
        return res;
    }

    @Override
    public ApiResponse<String> getWxjl(Page<BizLcWxjl> page) {
        LimitedCondition queryCondition = getQueryCondition();
        queryCondition.and().andIsNotNull(BizLcWxjl.InnerColumn.cardNo.name());
        PageInfo<BizLcWxjl> info = findPage(page, queryCondition);
        ApiResponse<String> res = new ApiResponse<>();
        res.setPage(info);
        return res;
    }

    @Override
    public ApiResponse<String> bindCardNo(String id) {
        BizLcWxjl wxjl = findById(id);
        RuntimeCheck.ifTrue(StringUtils.isNotBlank(wxjl.getCardNo()), "教练员已经绑定卡号, 请勿重复操作");
        String maxNo = baseMapper.getMaxNo();
        String cardNo = genCardNo(Integer.parseInt(maxNo.replaceAll("VIP","")));
        List<BizLcWxjl> eq = findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
        while (CollectionUtils.isNotEmpty(eq)) {
            int anInt = Integer.parseInt(cardNo.replaceAll("VIP","")) + 1;
            cardNo = genCardNo(anInt);
            eq = findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
        }
        wxjl.setCardNo(cardNo);
        wxjl.setPwd(EncryptUtil.encryptUserPwd("123456"));
        update(wxjl);
        return ApiResponse.success(cardNo);
    }

    @Override
    public ApiResponse<String> updatePwd(String cardNo, String old, String newPwd, String newPwd1) {
        RuntimeCheck.ifBlank(cardNo, "请上传卡号");
        RuntimeCheck.ifBlank(old, "请输入旧密码");
        RuntimeCheck.ifBlank(newPwd, "请输入新密码");
        RuntimeCheck.ifBlank(newPwd1, "请再次输入新密码");
        RuntimeCheck.ifFalse(StringUtils.equals(newPwd1, newPwd), "两次密码不一致");
        RuntimeCheck.ifTrue(StringUtils.equals(old, newPwd), "新密码与旧密码不能一致");

        List<BizLcWxjl> wxjlList = findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
        RuntimeCheck.ifEmpty(wxjlList, "未找到卡片信息");
        BizLcWxjl wxjl = wxjlList.get(0);
        String pwd = wxjl.getPwd();
        String userPwd = EncryptUtil.encryptUserPwd(old);
        RuntimeCheck.ifFalse(StringUtils.equals(pwd, userPwd), "原始密码错误");
        wxjl.setPwd(EncryptUtil.encryptUserPwd(newPwd));
        update(wxjl);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> resetPwd(String cardNo) {

        RuntimeCheck.ifBlank(cardNo, "请选择要重置的卡号");
        List<BizLcWxjl> wxjls = findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
        RuntimeCheck.ifEmpty(wxjls, "未找到卡号信息");
        BizLcWxjl wxjl = wxjls.get(0);
        wxjl.setPwd(EncryptUtil.encryptUserPwd("123456"));
        update(wxjl);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> czmx(int pageNum, int pageSize, String id, String lx) {
        RuntimeCheck.ifBlank(id, "请选择记录");
        SimpleCondition condition = new SimpleCondition(BizJlCz.class);
        condition.setOrderByClause(" cjsj desc");
        if(StringUtils.equals(lx, "kfr")){
            condition.in(BizJlCz.InnerColumn.type, Arrays.asList("00" ,"30"));
        }else if(StringUtils.equals(lx, "cz")){
            condition.in(BizJlCz.InnerColumn.type, Arrays.asList("10","20"));
        }
        condition.eq(BizJlCz.InnerColumn.jlId, id);
        PageInfo<BizJlCz> info = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> czMapper.selectByExample(condition));
        if (CollectionUtils.isNotEmpty(info.getList())) {
            Set<String> set = info.getList().stream().map(BizJlCz::getJlId).collect(Collectors.toSet());
            List<BizLcWxjl> wxjlList = findByIds(set);
            Map<String, BizLcWxjl> map = wxjlList.stream().collect(Collectors.toMap(BizLcWxjl::getId, p -> p));
            info.getList().forEach(bizJlCz -> {
                BizLcWxjl wxjl = map.get(bizJlCz.getJlId());
                bizJlCz.setXm(wxjl.getJlXm());
                bizJlCz.setJx(wxjl.getJlJx());
            });
        }
        ApiResponse<String> res = new ApiResponse<>();
        res.setPage(info);
        return res;
    }

    @Override
    public ApiResponse<String> getPjbh() {
        String time = DateTime.now().toString("yyyyMMdd");
        SimpleCondition condition = new SimpleCondition(BizJlCz.class);
        condition.startWith(BizJlCz.InnerColumn.pjbh, time);
        condition.setOrderByClause(" pjbh desc ");
        List<BizJlCz> czs = czMapper.selectByExample(condition);
        if (CollectionUtils.isEmpty(czs)) {
            return ApiResponse.success(time + "-0001");
        }
        int anInt = Integer.parseInt(czs.get(0).getPjbh().split("-")[1]) + 1;
        if (anInt < 10) {
            return ApiResponse.success(time + "-000" + anInt);
        } else if (anInt < 100) {
            return ApiResponse.success(time + "-00" + anInt);
        } else if (anInt < 1000) {
            return ApiResponse.success(time + "-0" + anInt);
        } else {
            return ApiResponse.success(time + "-" + anInt);
        }
    }

    @Override
    public ApiResponse<String> savePjbh(String id, String pjbh) {
        SimpleCondition condition = new SimpleCondition(BizJlCz.class);
        condition.eq(BizJlCz.InnerColumn.id, id);
        BizJlCz cz = czMapper.selectOneByExample(condition);
        RuntimeCheck.ifNull(cz, "请选择要打印的充值记录");
        cz.setPjbh(pjbh);
        czMapper.updateByPrimaryKeySelective(cz);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> updateEntity(BizLcWxjl entity) {
        List<BizLcWxjl> wxjls = findEq(BizLcWxjl.InnerColumn.jlLxdh, entity.getJlLxdh());
        if (CollectionUtils.isNotEmpty(wxjls)) {
            BizLcWxjl lcWxjl = wxjls.get(0);
            RuntimeCheck.ifTrue(!StringUtils.equals(lcWxjl.getId(), entity.getId()), "手机号码已绑定" + lcWxjl.getJlJx() + "-" + lcWxjl.getJlXm());
        }
        BizLcWxjl wxjl = findById(entity.getId());
        if(!StringUtils.equals(wxjl.getJlLx(), entity.getJlLx())){
            baseMapper.updateAllJl(entity.getId(), entity.getJlLx());
        }
        update(entity);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> removeEntity(String id) {
        BizLcWxjl wxjl = findById(id);
        RuntimeCheck.ifTrue(wxjl.getYe() > 0, "教练余额不为0 , 不能删除");
        RuntimeCheck.ifTrue(wxjl.getCardJe() > 0, "教练账户余额不为0 , 不能删除 ");
        remove(id);
        return ApiResponse.success();
    }


    @Override
    public ApiResponse<String> updateZhye(String id) {
        RuntimeCheck.ifBlank(id, "请选择教练");
        String[] split = id.split(",");
        List<BizLcWxjl> wxjls = findByIds(Arrays.asList(split));
        SysYh sysYh = getCurrentUser();
        wxjls.forEach(bizLcWxjl -> {
            if (bizLcWxjl.getYe() > 0) {
                BizJlCz cz = new BizJlCz();
                cz.setId(genId());
                cz.setXm(bizLcWxjl.getJlXm());
                cz.setType("30");
                cz.setSfje(0);
                cz.setJx(bizLcWxjl.getJlJx());
                cz.setJlId(bizLcWxjl.getId());
                cz.setJe(bizLcWxjl.getYe());
                cz.setCzhje(0);
                cz.setCjsj(DateUtils.getNowTime());
                cz.setBz(sysYh.getXm() + "-" + sysYh.getZh() + " 余额清零");
                cz.setCzqje(bizLcWxjl.getYe());
                cz.setZy("余额清零");
                czMapper.insert(cz);
                bizLcWxjl.setYe(0);
                update(bizLcWxjl);
            }
        });
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> unbindCardNo(String id) {
        RuntimeCheck.ifBlank(id, "请选择教练");
        BizLcWxjl wxjl = findById(id);
        RuntimeCheck.ifTrue(wxjl.getCardJe() > 0, "教练账户余额不为0 , 不能取消绑定");
        wxjl.setCardNo(null);
        wxjl.setPwd(null);
        baseMapper.updateByPrimaryKey(wxjl);
        return ApiResponse.success();
    }

    @Override
    public void exportWxjl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LimitedCondition condition = getQueryCondition();
        List<BizLcWxjl> jls = findByCondition(condition);
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> titleMap = new HashMap<>();
        titleMap.put(0, "序号");
        titleMap.put(1, "驾校");
        titleMap.put(2, "教练员");
        titleMap.put(3, "教练联系电话");
        titleMap.put(4, "本/外校");
        titleMap.put(5, "队号");
        titleMap.put(6, "卡号");
        titleMap.put(7, "充值卡余额");
        titleMap.put(8, "开放日余额");
        data.add(titleMap);
        if (CollectionUtils.isNotEmpty(jls)) {
            for (int i = 0, jlsSize = jls.size(); i < jlsSize; i++) {
                BizLcWxjl bizLcWxjl = jls.get(i);
                Map<Integer, String> dataMap = new HashMap<>();
                dataMap.put(0, i + 1 + "");
                dataMap.put(1, bizLcWxjl.getJlJx());
                dataMap.put(2, bizLcWxjl.getJlXm());
                dataMap.put(3, bizLcWxjl.getJlLxdh());
                dataMap.put(4, "00".equals(bizLcWxjl.getJlLx()) ? "本校" : "外校");
                dataMap.put(5, bizLcWxjl.getDm());
                dataMap.put(6, bizLcWxjl.getCardNo());
                dataMap.put(7, bizLcWxjl.getCardJe() + "");
                dataMap.put(8, bizLcWxjl.getYe() + "");
                data.add(dataMap);
            }
        }
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/msexcel");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment;filename=" + new String("教练信息".getBytes(StandardCharsets.UTF_8), "iso-8859-1") + ".xls");
        ServletOutputStream stream = response.getOutputStream();
        ExcelUtil.createSheet(stream, "教练信息", data);
    }

    private String genCardNo(int maxNo) {
        StringBuilder s = new StringBuilder(maxNo++ + "");
        int length = s.length();

        if (length == 4) {
            return "VIP" + maxNo + "";
        }
        for (int i = 0; i < 4 - length; i++) {
            s.insert(0, "0");
        }
        return "VIP" + s.toString();
    }
}