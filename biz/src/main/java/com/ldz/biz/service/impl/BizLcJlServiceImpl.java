package com.ldz.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizFdConfigMapper;
import com.ldz.biz.mapper.BizJlCzMapper;
import com.ldz.biz.mapper.BizJlXfMappper;
import com.ldz.biz.mapper.BizLcJlMapper;
import com.ldz.biz.model.*;
import com.ldz.biz.service.*;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.model.SysZdxm;
import com.ldz.sys.service.ZdxmService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.commonUtil.ExcelUtil;
import com.ldz.util.commonUtil.JsonUtil;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BizLcJlServiceImpl extends BaseServiceImpl<BizLcJl, String> implements BizLcJlService {

    @Autowired
    private BizLcJlMapper baseMapper;
    @Autowired
    private BizLcWxjlService wxjlService;
    @Autowired
    private BizLcClService clService;
    @Autowired
    private ZgjbxxService zgjbxxService;
    @Autowired
    private BizLcJlXyService xyService;
    @Autowired
    private ZdxmService zdxmService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BizLcFdService fdService;
    @Autowired
    private BizFdConfigMapper fdConfigMapper;
    @Autowired
    private BizJlXfMappper xfMappper;
    @Autowired
    private BizJlCzMapper czMapper;
    @Autowired
    private StringRedisTemplate redis;

    @Override
    protected Mapper<BizLcJl> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public boolean fillPagerCondition(LimitedCondition condition) {
        condition.setOrderByClause("  jssj  desc, jl_id asc , kssj desc");
        return true;
    }

    @Override
    public void afterQuery(List<BizLcJl> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        result.forEach(bizLcJl -> {
            SimpleCondition condition = new SimpleCondition(BizLcJlXy.class);
            condition.eq(BizLcJlXy.InnerColumn.lcJlId, bizLcJl.getId());
            List<BizLcJlXy> xies = xyService.findByCondition(condition);
            bizLcJl.setXyList(xies);
        });
    }

    @Override
    public void afterPager(PageInfo<BizLcJl> pageInfo) {
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return;
        }
        // 查询所有的教练信息
        Set<String> jlIds = pageInfo.getList().stream().map(BizLcJl::getJlId).collect(Collectors.toSet());
        List<BizLcWxjl> coachs = wxjlService.findIn(BizLcWxjl.InnerColumn.id, jlIds);
        // 根据教练id 分组
        Map<String, BizLcWxjl> coachMap = coachs.stream().collect(Collectors.toMap(BizLcWxjl::getId, p -> p));
        // 拿到所有的套餐
        Set<String> zddms = pageInfo.getList().stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
        SimpleCondition djcondition = new SimpleCondition(SysZdxm.class);
        djcondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        djcondition.in(SysZdxm.InnerColumn.zddm, zddms);
        List<SysZdxm> items = zdxmService.findByCondition(djcondition);
        // 根据套餐代码分组
        Map<String, SysZdxm> map = items.stream().collect(Collectors.toMap(SysZdxm::getZddm, p -> p));

        pageInfo.getList().forEach(bizLcJl -> {
            SimpleCondition condition = new SimpleCondition(BizLcJlXy.class);
            condition.eq(BizLcJlXy.InnerColumn.lcJlId, bizLcJl.getId());
            List<BizLcJlXy> xies = xyService.findByCondition(condition);
            bizLcJl.setXyList(xies);

            // 计算下支付方式
            if(bizLcJl.getKfje() != null && bizLcJl.getKfje() > 0) {
                bizLcJl.setZffs("开放日·" + (int)(Math.ceil(bizLcJl.getKfje()*1.0 / 200)) + "人");
            }else if(bizLcJl.getCardje() != null && bizLcJl.getCardje() > 0) {
                bizLcJl.setZffs("充值卡");
            }else if(StringUtils.equals(bizLcJl.getZfzt(), "10")){
                bizLcJl.setZffs("现金");
            }

            SysZdxm zdxm = map.get(bizLcJl.getZddm());
            if (zdxm != null) {
                bizLcJl.setLcDj(Float.parseFloat(StringUtils.isBlank(zdxm.getBy4()) ? "0" : zdxm.getBy4()));
                bizLcJl.setZdxm(zdxm);
            }

            if (StringUtils.isNotEmpty(bizLcJl.getJlId())) {
                BizLcWxjl coach = coachMap.get(bizLcJl.getJlId());
                if (coach != null) {
                    bizLcJl.setJlDh(coach.getJlLxdh());
                    if (StringUtils.equals(bizLcJl.getZfzt(), "00")) {
                        bizLcJl.setKfje(coach.getYe());
                        bizLcJl.setCardje(coach.getCardJe());
                    }
                }
            }
        });
    }

    @Override
    public ApiResponse<String> saveEntity(BizLcJl entity) {

        String s = JSON.toJSONString(entity);
        String lc = redis.boundValueOps("lcjl").get();
        if (StringUtils.isNotBlank(lc)) {
            RuntimeCheck.ifTrue(StringUtils.equals(lc, s), "操作频繁,请稍后再试");
        }
        redis.boundValueOps("lcjl").set(s, 2, TimeUnit.SECONDS);
        SysYh currentUser = getCurrentUser();
        //  判断所有必填字段
        RuntimeCheck.ifBlank(entity.getJlId(), "请选择教练");
        BizLcWxjl wxjl = wxjlService.findById(entity.getJlId());
        // 本校外校没有了   jlLx 这个字段暂时没用 , 全部默认为外校
        entity.setJlLx("10");
        entity.setId(genId());
        RuntimeCheck.ifBlank(entity.getZddm(), "请选择套餐");
        SimpleCondition condition = new SimpleCondition(SysZdxm.class);
        condition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition.eq(SysZdxm.InnerColumn.zddm, entity.getZddm());
        List<SysZdxm> zdxms = zdxmService.findByCondition(condition);
        RuntimeCheck.ifEmpty(zdxms, "未找到套餐信息");
        SysZdxm zdxm = zdxms.get(0);
        BizLcCl lcCl = null;
        if (StringUtils.isNotBlank(entity.getLcClId())) {
            lcCl = clService.findById(entity.getLcClId());
            RuntimeCheck.ifTrue(lcCl.getClZt().equalsIgnoreCase("01"), lcCl.getClBh() + "号车辆已经在训练中");
            if (StringUtils.isNotBlank(entity.getKm())) {
                String mess = "此车辆已绑定科目" + (lcCl.getClKm().equals("2") ? "二" : "三") + "-" + lcCl.getClBh() + "车,不能在科目" + (entity.getKm().equals("2") ? "二" : "三") + "窗口发车";
                RuntimeCheck.ifFalse(lcCl.getClKm().equalsIgnoreCase(entity.getKm()), mess);
            }
            entity.setLcClId(lcCl.getId());
            entity.setClBh(lcCl.getClBh());

            entity.setLcKm(lcCl.getClKm());
            entity.setJlCx(lcCl.getClCx());
            // 安全员信息
            entity.setKc(lcCl.getClKc());
            lcCl.setClZt("01");
            lcCl.setXgr(currentUser.getZh() + "-" + currentUser.getXm());
            lcCl.setXgsj(DateUtils.getNowTime());
            lcCl.setZgId(entity.getZgId());
        }
        if (StringUtils.equals(zdxm.getBy2(), "1")) {
            RuntimeCheck.ifNull(lcCl, "请选择车辆");
            // 0 为不启用打卡 1 为启用打卡
            RuntimeCheck.ifBlank(entity.getCardNo(), " 请刷卡");
            // 查询此卡是否为在训状态
            condition = new SimpleCondition(BizLcJl.class);
            condition.eq(BizLcJl.InnerColumn.cardNo, entity.getCardNo());
            condition.and().andCondition(" jssj is null or jssj = ''");
            List<BizLcJl> jls = findByCondition(condition);
            RuntimeCheck.ifTrue(CollectionUtils.isNotEmpty(jls), "此卡正在训练中!");
            // 此套餐是否需要充值卡绑定车辆
            if (StringUtils.equals(zdxm.getBy6(), "1")) {
                List<BizLcCl> cls = clService.findEq(BizLcCl.InnerColumn.cardNo, entity.getCardNo());
                RuntimeCheck.ifEmpty(cls, "此卡未绑定车辆");
                String clBh = cls.get(0).getClBh();
                RuntimeCheck.ifFalse(StringUtils.equals(lcCl.getCardNo(),entity.getCardNo()), "卡号与车号不匹配,该卡已绑定" +clBh + "号车!" );
            }
        }
        entity.setLcLx(zdxm.getBy5());
        entity.setJlJx(wxjl.getJlJx());
        if (!StringUtils.equals(entity.getLcLx(), "30") && !(StringUtils.equals(entity.getLcKm(), "2") && StringUtils.equals(entity.getLcLx(), "20"))) {
            RuntimeCheck.ifNull(lcCl, "请选择车辆");
            // 查询卡号记录 判断是否在训练中
            SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
            jlCondition.eq(BizLcJl.InnerColumn.lcClId, entity.getLcClId());
            jlCondition.and().andCondition(" jssj is null or jssj = ''");
            jlCondition.setOrderByClause(" kssj desc ");
            jlCondition.and().andCondition(" kssj is not null ");
            List<BizLcJl> jls = findByCondition(jlCondition);
            RuntimeCheck.ifTrue(CollectionUtils.isNotEmpty(jls), "当前车辆已在训练中,请勿重复操作");
            // 先获取绑定车辆  卡号没有绑定车辆需要绑定
            // 修改车辆为使用中
            clService.update(lcCl);
        }
        if (StringUtils.equals(entity.getLcKm(), "3") || StringUtils.isNotBlank(entity.getZgId())) {
            // 科目三需要选定安全员
            RuntimeCheck.ifBlank(entity.getZgId(), "请选择安全员");
            SimpleCondition aqyCondition = new SimpleCondition(BizLcJl.class);
            aqyCondition.eq(BizLcJl.InnerColumn.zgId, entity.getZgId());
            aqyCondition.and().andCondition(" jssj is null or jssj = ''");
            List<BizLcJl> lcJls = findByCondition(aqyCondition);
            RuntimeCheck.ifTrue(CollectionUtils.isNotEmpty(lcJls), "当前安全员正在训练中 , 请选择其他安全员");
            Zgjbxx zgjbxx = zgjbxxService.findById(entity.getZgId());
            RuntimeCheck.ifTrue(ObjectUtils.isEmpty(zgjbxx), "没有找到该安全员的信息");
            entity.setZgXm(zgjbxx.getXm());
        }

        entity.setLcLx(StringUtils.isBlank(entity.getLcLx()) ? "00" : entity.getLcLx());
        LocalDateTime now = LocalDateTime.now();
        String nowTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        entity.setCjsj(nowTime);
        entity.setKssj(nowTime);


        if (!StringUtils.equals(entity.getLcLx(), "00") && StringUtils.isNotBlank(entity.getLcLx())) {
            entity.setPz(entity.getId());
            // 套餐类型不是计时培训 需要在记录的时候就将费用计算出来 , 有返点的直接记录返点
            String jg = zdxm.getZdmc();
            if (StringUtils.equals(entity.getLcLx(), "20")) {
                // 培优价格为本身价格
                entity.setZfzt("10");
                if (entity.getLcKm().equals("2")) {
                    RuntimeCheck.ifBlank(entity.getXyXm(), "请输入学员信息");
                    // 根据学员信息计算学员的数量
                    String[] split = entity.getXyXm().split(",");
                    RuntimeCheck.ifTrue(split.length <= 0, "请输入学员信息");
                    int xySl = split.length;
                    entity.setXySl(xySl);
                    entity.setLcFy(Integer.parseInt(jg) * xySl);
                    entity.setYfJe(Integer.parseInt(jg) * xySl);
                    entity.setXjje(entity.getLcFy());
                } else {
                    RuntimeCheck.ifBlank(entity.getXyXm(), "请输入学员信息");
                    // 根据学员信息计算学员的数量
                    String[] split = entity.getXyXm().split(",");
                    RuntimeCheck.ifTrue(split.length <= 0, "请输入学员信息");
                    int xySl = split.length;
                    entity.setXySl(xySl);
                    entity.setLcFy(Integer.parseInt(jg) * xySl);
                    entity.setYfJe(entity.getLcFy());
                    entity.setXjje(entity.getLcFy());
                }
                if(StringUtils.equals(entity.getLcKm(),"2")){
                    entity.setJssj(nowTime);
                }
            } else if (StringUtils.equals(entity.getLcLx(), "30")) {
                // 练车费用为 人数 乘以 价格
                entity.setZfzt("10");
                RuntimeCheck.ifNull(entity.getXySl(), "请填写学员数量");
                RuntimeCheck.ifTrue(entity.getXySl() <= 0, "学员数量必须大于0");
                entity.setYfJe(Integer.parseInt(jg) * entity.getXySl());
                entity.setLcFy(Integer.parseInt(jg) * entity.getXySl());
            } else if (StringUtils.equals(entity.getLcLx(), "10")) {
                // 按把训练也需要学员数量
                entity.setZfzt("10");
                RuntimeCheck.ifNull(entity.getXySl(), "请填写学员数量");
                RuntimeCheck.ifTrue(entity.getXySl() <= 0, "学员数量必须大于0");
                entity.setYfJe(Integer.parseInt(jg) * entity.getXySl());
                entity.setLcFy(Integer.parseInt(jg) * entity.getXySl());
                entity.setXjje(entity.getLcFy());
            }
            // 科目二开放日类型直接返点
            if (StringUtils.equals(entity.getLcLx(), "30") && StringUtils.equals(entity.getLcKm(), "2")) {
                entity.setXjje(entity.getYfJe());
                // 开放日返点金额
                int fdje = Integer.parseInt(zdxm.getBy3()) * entity.getXySl();
                entity.setJssj(nowTime);
                // 类型为开放练习 此时需要直接返点
                RuntimeCheck.ifFalse(entity.getXySl() > 0, "开放训练必须填学员人数");

                // 充值余额
                int czje = entity.getLcFy() - fdje;
                // 新增充值余额记录
                BizJlCz jlCz = new BizJlCz();
                jlCz.setCjsj(nowTime);
                jlCz.setCzqje(wxjl.getYe());
                // 教练添加余额
                wxjl.setYe(czje + wxjl.getYe());
                jlCz.setCzhje(wxjl.getYe());
                jlCz.setId(genId());
                jlCz.setJe(czje);
                jlCz.setJlId(wxjl.getId());
                jlCz.setType("00");
                // 新增充值记录
                czMapper.insert(jlCz);

                if (fdje > 0) {
                    // 新增返点记录
                    BizLcFd lcFd = new BizLcFd();
                    lcFd.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
                    lcFd.setCjsj(nowTime);
                    lcFd.setFdje(fdje);
                    lcFd.setId(entity.getId());
                    lcFd.setJlId(wxjl.getId());
                    lcFd.setJlXm(wxjl.getJlXm());
                    lcFd.setLcId(entity.getId());
                    lcFd.setFdlx(entity.getLcLx());
                    lcFd.setLcFy(entity.getLcFy());
                    lcFd.setLcKm(entity.getLcKm());
                    lcFd.setSc(0);
                    lcFd.setBz("(" + entity.getXySl() + " * " + zdxm.getBy3() + ")");
                    fdService.save(lcFd);
                    entity.setPz(lcFd.getId());
                }
                wxjlService.update(wxjl);
            }
            //  科目三 按把练车需要返点
            if (StringUtils.equals(entity.getLcKm(), "3") && StringUtils.equals(entity.getLcLx(), "10")) {
                // 不同于开放日 , 没有余额充值, 直接返点就行
                // 此处根据人数来确定返点金额
                int fdje = Integer.parseInt(zdxm.getBy3());
                // 计算返点总金额
                int zfd = fdje * entity.getXySl();
                if (zfd > 0) {
                    // 新增返点记录
                    BizLcFd lcFd = new BizLcFd();
                    lcFd.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
                    lcFd.setCjsj(nowTime);
                    lcFd.setFdje(zfd);
                    lcFd.setId(entity.getId());
                    lcFd.setJlId(wxjl.getId());
                    lcFd.setJlXm(wxjl.getJlXm());
                    lcFd.setLcId(entity.getId());
                    lcFd.setFdlx(entity.getLcLx());
                    lcFd.setLcFy(entity.getLcFy());
                    lcFd.setLcKm(entity.getLcKm());
                    lcFd.setSc(0);
                    lcFd.setBz("(" + entity.getXySl() + " * " + fdje + ")");
                    fdService.save(lcFd);
                    entity.setPz(lcFd.getId());
                }
            }
        }

        entity.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
        entity.setLcYj(entity.getLcYj() == null ? 0 : entity.getLcYj());

        entity.setJlXm(wxjl.getJlXm());
        entity.setJlJx(wxjl.getJlJx());
        entity.setJlDh(wxjl.getJlLxdh());
        entity.setFdZt("30");
        if(!StringUtils.equals(entity.getLcLx(), "00") && StringUtils.equals(entity.getLcKm(), "2")){
            entity.setJlCx("");
        }
        save(entity);
        if(StringUtils.equals(entity.getLcLx(), "20")){
            entity.setClBh("培优");
        }else if(StringUtils.equals(entity.getLcLx(), "30")){
            entity.setClBh("开放日");
            entity.setBz(entity.getXySl() +  "人");
        }
        if(StringUtils.isBlank(entity.getZgXm())){
            entity.setZgXm(" ");
        }
        return ApiResponse.success(JsonUtil.toJson(entity));
    }

    @Override
    public ApiResponse<BizLcJl> updateJssj(String id, String cardNo, String km) throws ParseException {

        // 定义三个 数字
        int kfje = 0;
        int card = 0;
        int xjje = 0;
        SysYh yh = getCurrentUser();
        SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
        if (StringUtils.isNotBlank(id)) {
            jlCondition.eq(BizLcJl.InnerColumn.id, id);
        }
        if (StringUtils.isNotBlank(cardNo)) {
            jlCondition.eq(BizLcJl.InnerColumn.cardNo, cardNo);
        }
        jlCondition.and().andCondition(" card_no = '" + id + "' or id = '" + id + "'");
        jlCondition.and().andCondition("( jssj is null or jssj = '') and kssj is not null  ");
        jlCondition.eq(BizLcJl.InnerColumn.lcKm, km);
        jlCondition.setOrderByClause(" kssj desc ");
        List<BizLcJl> jls = findByCondition(jlCondition);
        RuntimeCheck.ifTrue(CollectionUtils.isEmpty(jls), StringUtils.isNotBlank(cardNo) ? "发车卡与结束卡不一致!" : "未找到练车记录");
        BizLcJl lcJl = jls.get(0);

        // 计算练车费用
        SimpleCondition condition = new SimpleCondition(SysZdxm.class);
        condition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition.eq(SysZdxm.InnerColumn.zddm, lcJl.getZddm());
//            condition.eq(SysZdxm.InnerColumn.by2, lcJl.getJlCx());
        List<SysZdxm> items = zdxmService.findByCondition(condition);
        RuntimeCheck.ifTrue(CollectionUtils.isEmpty(items), "此套餐未设置费用 , 请先设置套餐费用");
        SysZdxm management = items.get(0);
        if (StringUtils.equals(management.getBy2(), "1")) {
            RuntimeCheck.ifBlank(cardNo, "此记录需要刷卡结束");
        }
        String s = DateUtils.getNowTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date end = dateFormat.parse(s);
        String kssj = lcJl.getKssj();
        Date start = dateFormat.parse(kssj);
        // 计算练车时长
        String str = "";
        int kfye = 0;
        int cardye = 0;
        String fdr = "";
        if (StringUtils.equals(lcJl.getLcLx(), "00")) {
            RuntimeCheck.ifTrue(StringUtils.equals(km, "2") && !StringUtils.equals(km, lcJl.getLcKm()), "请前往科目二窗口还车");
            RuntimeCheck.ifTrue(StringUtils.equals(km, "3") && !StringUtils.equals(km, lcJl.getLcKm()), "请前往科目三窗口还车");
            // 计算实际时长  (所有车辆免费前五分钟)
            int lcSc = Math.max((int) ((end.getTime() - start.getTime()) / (1000 * 60)), 0);
            int v;
            if (StringUtils.equals(lcJl.getLcKm(), "2") && StringUtils.equals(lcJl.getZddm(), "K2JS-S")) {
                // 科目二的 190 35分钟 , 超出时间按照 8.33计算
                String by3 = management.getBy3();
                // 190
                String hour = management.getZdmc();
                if (lcSc > management.getQz()) {
                    v = (int) Math.ceil((lcSc - management.getQz()) * Float.parseFloat(by3)) + Integer.parseInt(hour);
                } else {
                    v = Integer.parseInt(hour);
                }
            } else {
                // 每小时的费用
                String hour = management.getZdmc();
                String by3 = management.getBy3();
                int h = lcSc / 60;
                int m = lcSc % 60;
                // 小时能除尽的按小时计费
                float hv = Float.parseFloat(hour) * h;
                // 不能除尽的按分钟算
                float mv = m * Float.parseFloat(by3);
                // 总费用
                v = (int) Math.ceil(hv + mv);
            }
            lcJl.setLcDj(Float.parseFloat(management.getZdmc()));
            lcJl.setLcFy(v);
            // 计算应付金额
            BizLcWxjl wxjl = wxjlService.findById(lcJl.getJlId());
            kfye = wxjl.getYe();
            cardye = wxjl.getCardJe();
            // 先扣款 开放日余额
            int ye = wxjl.getYe();
            // 科目三不进行开放日扣款
            if (StringUtils.equals(lcJl.getLcKm(), "3")) {
                ye = 0;
            }
            if (ye > 0) {
                int sfje = ye - v;
                // 判断此时实付金额为多少
                if (sfje < 0) {
                    // 如果实付金额小于 0 , 还需要现金
                    kfje = ye;
                    xjje = Math.abs(sfje);
                } else {
                    // 此时已经支付完成
                    kfje = v;
                }
            } else {
                int cardJe = wxjl.getCardJe();
                if (cardJe > 0) {
                    int sfje = cardJe - v;
                    if (sfje < 0) {
                        card = cardJe;
                        xjje = Math.abs(sfje);
                    } else {
                        card = v;
                    }
                } else {
                    xjje = v;
                }
            }
            if (kfje > 0) {
               fdr = "3";
            }
            if (card > 0) {
                fdr = "2";
            }
            if (StringUtils.isBlank(fdr)) {
                fdr = "1";
            }
            str += " 应收现金" + xjje + "元";
        } else {
            fdr = "3";
            str = " 应收现金: " + lcJl.getLcFy() + "元";
        }

        // 更新这辆车的状态
        if (StringUtils.equals(lcJl.getLcKm(), "3") || StringUtils.isNotBlank(lcJl.getLcClId())) {
            BizLcCl lcCl = clService.findById(lcJl.getLcClId());
            if (StringUtils.equals(lcCl.getClZt(), "01")) {
                lcCl.setClZt("00");
                lcCl.setZgId(null);
                lcCl.setZgXm(null);
                lcCl.setXgr(yh.getZh() + "-" + yh.getXm());
                lcCl.setXgsj(DateUtils.getNowTime());
                clService.update(lcCl);
            }
        }

        lcJl.setSc((int) (end.getTime() - start.getTime()) / (1000 * 60));
        lcJl.setJssj(s);
        lcJl.setXgr(yh.getZh() + "-" + yh.getXm());
        lcJl.setXgsj(DateUtils.getNowTime());
        if (StringUtils.equals(lcJl.getLcLx(), "00") && !StringUtils.equals(lcJl.getJlXm(), "自学")) {
            lcJl.setFdZt("00");
        } else {
            lcJl.setFdZt("40");
        }
        if (StringUtils.equals(lcJl.getLcLx(), "00")) {
            lcJl.setZfzt("00");
            update(lcJl);
            lcJl.setXjje(xjje);
            lcJl.setCardje(cardye);
            lcJl.setKfje(kfye);
            lcJl.setYfJe(xjje);
            lcJl.setBz(str);
            lcJl.setFdr(fdr);
            // 查询该教练所有未支付的练车记录
            condition = new SimpleCondition(BizLcJl.class);
            condition.eq(BizLcJl.InnerColumn.jlId, lcJl.getJlId());
            condition.eq(BizLcJl.InnerColumn.zfzt, "00");
            condition.eq(BizLcJl.InnerColumn.lcKm, lcJl.getLcKm());
            List<BizLcJl> lcJls = findByCondition(condition);
            String s1 = lcJls.stream().map(BizLcJl::getId).collect(Collectors.joining(","));
            ApiResponse<BizLcJl> response = getBatchPay(s1);
            BizLcJl result = response.getResult();
            result.setJlJx(lcJl.getJlJx());
            result.setJssj(lcJl.getJssj());
            result.setLcLx(lcJl.getLcLx());
            condition = new SimpleCondition(BizLcJl.class);
            condition.eq(BizLcJl.InnerColumn.jlId, lcJl.getJlId());
            condition.and().andCondition(" kssj like '%" + DateTime.now().toString("yyyy-MM-dd") + "%' or zfzt = '00'");
            condition.and().andNotEqualTo(BizLcJl.InnerColumn.lcLx.name(), "30");
            condition.eq(BizLcJl.InnerColumn.lcKm, lcJl.getLcKm());
            List<BizLcJl> bizLcJls = findByCondition(condition);
            Set<String> set = bizLcJls.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
            SimpleCondition condition1 = new SimpleCondition(SysZdxm.class);
            condition1.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
            condition1.in(SysZdxm.InnerColumn.zddm, set);
//        condition1.eq(SysZdxm.InnerColumn.by5, bizLcJl.getLcLx());
            List<SysZdxm> zdxms = zdxmService.findByCondition(condition1);
            Map<String, List<SysZdxm>> map = zdxms.stream().collect(Collectors.groupingBy(SysZdxm::getZddm));

            bizLcJls.forEach(bizLcJl -> {
                if (StringUtils.equals(bizLcJl.getLcLx(), "00")) {
                    // 如果已经结束 直接查看时长和总价
                    if (StringUtils.isBlank(bizLcJl.getJssj())) {
                        // 计算时长和费用
                        Date date = new Date();
                        Date ksDate = DateTime.parse(bizLcJl.getKssj(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
                        long sc = (date.getTime() - ksDate.getTime()) / (60 * 1000);
                        List<SysZdxm> zdxmList = map.get(bizLcJl.getZddm());
                        SysZdxm zdxm = zdxmList.get(0);
                        long lcfy = (long) Math.ceil(Double.parseDouble(zdxm.getBy3()) * sc);
                        bizLcJl.setSc((int) sc);
                        bizLcJl.setLcFy((int) lcfy);
                    }
                }
            });
            result.setJls(bizLcJls);
            return ApiResponse.success(result);
        } else {
            update(lcJl);
            return ApiResponse.success(lcJl);
        }
    }

    @Override
    public List<Map<String, Object>> drivingSchoolStatistics() {
        String lcKm = getRequestParamterAsString("lcKm");
        RuntimeCheck.ifBlank(lcKm, "请选择科目");
        String tjsj = getRequestParamterAsString("tjsj");
        if (StringUtils.isEmpty(tjsj)) tjsj = DateUtils.getToday() + " 00:00:00," + DateUtils.getToday() + " 23:59:59";
        String[] sj = tjsj.split(",");
        String kssj = sj[0];
        String jssj = sj[1];
        String sql = "SELECT jl_jx,sum(sc) as sc,sum(lc_fy) as fy  from BIZ_LC_JL where 1=1 ";
        sql += " and kssj >= '" + kssj + "' and jssj <= '" + jssj + "'";
        sql += " and lc_km ='" + lcKm + "'";
        sql += " GROUP BY jl_jx ";
        System.out.println(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> retList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Map m : list) {
                Map<String, Object> retMap = new HashMap<>();
                retMap.put("jlJx", m.get("jl_jx"));
                String sc = m.get("sc").toString();
                long l = 0;
                try {
                    l = Long.parseLong(sc);
                } catch (Exception ignored) {
                }
                if (l / 60 == 0) {
                    retMap.put("lcSc", l + "分");
                } else {
                    retMap.put("lcSc", (l / 60) + "时" + (l % 60) + "分");
                }
                retMap.put("lcFy", m.get("fy"));
                retList.add(retMap);
            }
        }
        return retList;
    }

    @Override
    public List<Map<String, Object>> statistics() {
        String cjsjIn = getRequestParamterAsString("cjsjIn");
        String clId = getRequestParamterAsString("clId");
        if (StringUtils.isEmpty(cjsjIn))
            cjsjIn = DateUtils.getToday() + " 00:00:00," + DateUtils.getToday() + " 23:59:59";
        String[] sj = cjsjIn.split(",");
        String kssj = sj[0];
        String jssj = sj[1];
        String sql = "select lc_cl_id,cl_bh,sum(xy_sl) sum_xysl,sum(sc) sum_sc,sum(lc_fy) sum_lcfy from biz_lc_jl where 1=1 ";
        if (StringUtils.isNotEmpty(clId)) {
            sql += " and lc_cl_id = '" + clId + "'";
        }
        sql += " and cjsj >= '" + kssj + "' and cjsj <= '" + jssj + "'";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public ApiResponse<BizLcJl> getJl(String cardNo) {
        SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
        jlCondition.eq(BizLcJl.InnerColumn.cardNo, cardNo);
        jlCondition.and().andCondition(" jssj is null or jssj = ''");
        jlCondition.setOrderByClause(" kssj desc ");
        List<BizLcJl> jls = findByCondition(jlCondition);
        RuntimeCheck.ifTrue(CollectionUtils.isEmpty(jls), "当前卡号没有练车记录");
        BizLcJl lcJl = jls.get(0);
        String lcClId = lcJl.getLcClId();
        BizLcCl cl = clService.findById(lcClId);
        lcJl.setLcCl(cl);

        return ApiResponse.success(lcJl);
    }

    @Override
    public ApiResponse<String> saveJl(BizLcJl entity, String appoint) {
        SysYh currentUser = getCurrentUser();
        RuntimeCheck.ifBlank(appoint, "请填写预约信息");
        String[] split = appoint.split("-");
        RuntimeCheck.ifTrue(split.length <= 0, "请选择预约车型");
        //  判断所有必填字段
        RuntimeCheck.ifBlank(entity.getJlId(), "请选择教练");
        for (String s : split) {
            List<String> appoints = Arrays.asList(s.split(",", -1));
            RuntimeCheck.ifTrue(appoints.size() < 2, "请完整填写预约信息");
            String num = appoints.get(1);
            String cx = appoints.get(0);
            String xySl = "0";
            if (appoints.size() == 3 && StringUtils.isNotBlank(appoints.get(2))) {
                xySl = appoints.get(2);
            }
            if (StringUtils.isBlank(cx)) {
                continue;
            }
            entity.setJlLx("10");
            if (!(StringUtils.isBlank(num) || StringUtils.equals(num, "null"))) {
                int anInt = Integer.parseInt(num);
                for (int i = 0; i < anInt; i++) {
                    saveAppoint(entity, currentUser, cx, xySl);
                }
            }
        }
        return ApiResponse.success();
    }

    private void saveAppoint(BizLcJl entity, SysYh currentUser, String cx, String xySl) {
        LocalDateTime now = LocalDateTime.now();
        String nowTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        entity.setId(genId());
        entity.setZgId(null);
        entity.setCjsj(nowTime);
        entity.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
        entity.setLcLx(StringUtils.isBlank(entity.getLcLx()) ? "00" : entity.getLcLx());
        // 教练信息
        BizLcWxjl wxjl = wxjlService.findById(entity.getJlId());
        entity.setJlXm(wxjl.getJlXm());
        entity.setJlJx(wxjl.getJlJx());
        entity.setJlCx(cx);
        entity.setXySl(Integer.parseInt(xySl));
        // 查询是否有关联到学员
        if (StringUtils.isNotBlank(entity.getXyIds())) {
            List<BizLcJlXy> xies = new ArrayList<>();
            // 关联l到学员
            List<String> list = Arrays.asList(entity.getXyIds().split(","));

            String xyDh = getRequestParamterAsString("xyDh");
            List<String> xyDhs = new ArrayList<>();
            if (StringUtils.isNotBlank(xyDh)) {
                xyDhs = Arrays.asList(xyDh.split(","));
            }

            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                BizLcJlXy jlXy = new BizLcJlXy();
                if (CollectionUtils.isNotEmpty(xyDhs)) {
                    jlXy.setXyDh(xyDhs.get(i));
                }
                jlXy.setXyJx(wxjl.getJlJx());
                jlXy.setId(genId());
                jlXy.setXyXm(s);
                jlXy.setLcJlId(entity.getId());
                jlXy.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
                jlXy.setCjsj(DateUtils.getNowTime());
                jlXy.setId(genId());
                xyService.save(jlXy);
            }

            if (CollectionUtils.isNotEmpty(xies)) {
                if (entity.getXySl() == null || entity.getXySl() == 0) {
                    entity.setXySl(xies.size());
                }
            }
        }
        entity.setFdZt("30");
        save(entity);
    }


    /**
     * 开始练车的备用接口
     */
    @Override
    public ApiResponse<String> kslc(BizLcJl entity) {
        SysYh currentUser = getCurrentUser();
        BizLcJl lcJl = findById(entity.getId());
        RuntimeCheck.ifNull(lcJl, "未找到预约记录");
        //  判断所有必填字段
        RuntimeCheck.ifBlank(entity.getJlId(), "请选择教练");
        RuntimeCheck.ifBlank(entity.getCardNo(), "请刷卡");
        entity.setJlLx("10");

        SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
        jlCondition.eq(BizLcJl.InnerColumn.cardNo, entity.getCardNo());
        jlCondition.and().andCondition(" jssj is null or jssj = ''");
        jlCondition.setOrderByClause(" kssj desc ");
        List<BizLcJl> jls = findByCondition(jlCondition);
        if (CollectionUtils.isNotEmpty(jls)) {
            return ApiResponse.fail("当前卡号已在训练中,请勿重复刷卡");
        }

        // 先获取绑定车辆
        List<BizLcCl> lcCls = clService.findEq(BizLcCl.InnerColumn.id, entity.getLcClId());
        BizLcCl lcCl;
        if (CollectionUtils.isNotEmpty(lcCls)) {
            lcCl = lcCls.get(0);
        } else {
            RuntimeCheck.ifBlank(entity.getLcClId(), "未找到车辆信息");
            // 练车车辆的详细信息
            lcCl = clService.findById(entity.getLcClId());
        }
        RuntimeCheck.ifTrue(lcCl.getClZt().equalsIgnoreCase("01"), "当前车辆已经在训练中");
        if (StringUtils.isNotBlank(entity.getKm())) {
            RuntimeCheck.ifFalse(lcCl.getClKm().equalsIgnoreCase(entity.getKm()), "此卡号已绑定科目" + (lcCl.getClKm().equals("2") ? "二" : "三") + " - " + lcCl.getClBh() + "车,不能在科目" + (entity.getKm().equals("2") ? "二" : "三") + "窗口发车");
        }
        RuntimeCheck.ifFalse(StringUtils.equals(lcCl.getClCx(), lcJl.getJlCx()), "所选车辆车型与预约车型不一致");
        entity.setClBh(lcCl.getClBh());
        entity.setLcClId(lcCl.getId());

        entity.setLcLx(StringUtils.isBlank(entity.getLcLx()) ? "00" : entity.getLcLx());


        LocalDateTime now = LocalDateTime.now();
        String nowTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        entity.setKssj(nowTime);
        entity.setLcYj(entity.getLcYj() == null ? 0 : entity.getLcYj());
        // 根据教练是本校还是外校来选择教练

        BizLcWxjl wxjl = wxjlService.findById(entity.getJlId());
        entity.setJlXm(wxjl.getJlXm());
        entity.setJlJx(wxjl.getJlJx());


        if (StringUtils.equals(lcCl.getClKm(), "3")) {
            RuntimeCheck.ifBlank(entity.getZgId(), "请选择安全员");
            SimpleCondition aqyCondition = new SimpleCondition(BizLcJl.class);
            aqyCondition.eq(BizLcJl.InnerColumn.zgId, entity.getZgId());
            aqyCondition.and().andCondition(" jssj is null or jssj = ''");
            List<BizLcJl> lcJls = findByCondition(aqyCondition);
            RuntimeCheck.ifTrue(CollectionUtils.isNotEmpty(lcJls), "当前安全员正在训练中 , 请选择其他安全员");
            Zgjbxx zgjbxx = zgjbxxService.findById(entity.getZgId());
            if (ObjectUtils.isEmpty(zgjbxx)) {
                return ApiResponse.fail("没有找到该安全员的信息");
            }
            entity.setZgXm(zgjbxx.getXm());
            lcCl.setZgXm(zgjbxx.getXm());
        }
        entity.setLcKm(lcCl.getClKm());
        entity.setJlCx(lcCl.getClCx());
        // 安全员信息
        entity.setXgsj(DateUtils.getNowTime());
        entity.setXgr(currentUser.getZh() + "-" + currentUser.getXm());
        entity.setFdZt("30");
        entity.setKc(lcCl.getClKc());
        // 修改车辆为使用中
        lcCl.setClZt("01");
        lcCl.setXgr(currentUser.getZh() + "-" + currentUser.getXm());
        lcCl.setXgsj(DateUtils.getNowTime());
        lcCl.setZgId(entity.getZgId());

        clService.update(lcCl);

        update(entity);
        return ApiResponse.success();
    }


    @Override
    public ApiResponse<String> updateFdZt(String id) {

        RuntimeCheck.ifBlank(id, "请选择已返点的记录");
        SysYh user = getCurrentUser();
        List<String> list = Arrays.asList(id.split(","));
        RuntimeCheck.ifEmpty(list, "请选择需要返点的记录");
        BizLcFd bizLcFd = new BizLcFd();
//        bizLcFd.setFdje(fdJe);
        bizLcFd.setQrr(user.getZh() + "-" + user.getXm());
        bizLcFd.setQrsj(DateUtils.getNowTime());
        bizLcFd.setFdsl(list.size());
        bizLcFd.setId(genId());
        fdService.save(bizLcFd);
        list.forEach(s -> {
            BizLcJl lcJl = findById(s);
            lcJl.setFdZt("10");
            lcJl.setFdsj(DateUtils.getNowTime());
            lcJl.setFdr(user.getZh() + "-" + user.getXm());
            lcJl.setFdId(bizLcFd.getId());
            update(lcJl);
        });

        return ApiResponse.success();
    }

    @Override
    public ApiResponse<BizLcJl> getFdZt(String id) {
        BizLcJl lcJl = findById(id);
        RuntimeCheck.ifNull(lcJl, "未找到该记录");
        if (!StringUtils.equals(lcJl.getFdZt(), "10")) {
            lcJl.setFdZt("20");
            update(lcJl);
        }
        return ApiResponse.success(lcJl);
    }

    @Override
    public ApiResponse<List<LcJlModel>> getAllLog() {
        List<LcJlModel> list = new ArrayList<>();
        LimitedCondition condition = getQueryCondition();
       /* String cjsjGte = getRequestParamterAsString("cjsjGte");
        String cjsjLte = getRequestParamterAsString("cjsjLte");
        if(StringUtils.isBlank(cjsjGte)){
            condition.gte(BizLcJl.InnerColumn.cjsj, DateUtils.getDateStr(new Date(),"yyyy-MM-dd")+ " 00:00:00");
        }
        if(StringUtils.isBlank(cjsjLte)){
            condition.lte(BizLcJl.InnerColumn.cjsj, DateUtils.getDateStr(new Date(),"yyyy-MM-dd") + " 23:59:59");
        }*/
        /*String cjsjLike = getRequestParamterAsString("cjsjLike");
        if(StringUtils.isBlank(cjsjLike)){
            condition.like(BizLcJl.InnerColumn.cjsj,DateUtils.getDateStr(new Date(), "yyyy-MM-dd"));
        }*/
        condition.and().andIsNotNull(BizLcJl.InnerColumn.lcFy.name());
        List<BizLcJl> jls = findByCondition(condition);
        if (CollectionUtils.isNotEmpty(jls)) {
            // 根据安全员分组
            Map<String, List<BizLcJl>> collect = jls.stream().filter(bizLcJl -> StringUtils.isNotBlank(bizLcJl.getJssj())).collect(Collectors.groupingBy(BizLcJl::getZgId));
            for (String s : collect.keySet()) {
                List<BizLcJl> bizLcJls = collect.get(s);
                Map<String, List<BizLcJl>> listMap = bizLcJls.stream().collect(Collectors.groupingBy(BizLcJl::getClBh));
                // 可能存在一个安全员多辆车的问题
                for (String clBh : listMap.keySet()) {
                    List<BizLcJl> jls1 = listMap.get(clBh);
                    jls1.sort(Comparator.comparing(BizLcJl::getKssj));
                    // 可能存在跑出大于6趟的情况
                    int i = jls1.size() / 6;
                    int k = jls1.size() % 6;
                    for (int j = 0; j <= i; j++) {
                        LcJlModel lcJlModel = new LcJlModel();
                        List<BizLcJl> lcJls = new ArrayList<>();
                        if (j == i) {
                            if (k == 0) {
                                break;
                            }
                            lcJls.addAll(jls1.subList(j * 6, j * 6 + k));
                        } else {
                            lcJls.addAll(jls1.subList(j * 6, (j + 1) * 6));
                        }
                        lcJlModel.setClBh(clBh);
                        lcJlModel.setJls(lcJls);
                        lcJlModel.setZgXm(listMap.get(clBh).get(0).getZgXm());
                        lcJlModel.setZj(lcJls.stream().mapToInt(BizLcJl::getLcFy).sum());
                        list.add(lcJlModel);
                    }

                }
            }
        }
        return ApiResponse.success(list);
    }

    @Override
    public ApiResponse<LcJlModel> getOneLog(String zgId) {
        RuntimeCheck.ifBlank(zgId, "请选择安全员");
        // 首先查询当天所有的安全员记录
        LimitedCondition condition = getQueryCondition();
        /*String cjsjLike = getRequestParamterAsString("cjsjLike");
        if(StringUtils.isBlank(cjsjLike)){
            condition.like(BizLcJl.InnerColumn.cjsj,DateUtils.getDateStr(new Date(), "yyyy-MM-dd"));
        }*/
       /* String cjsjGte = getRequestParamterAsString("cjsjGte");
        String cjsjLte = getRequestParamterAsString("cjsjLte");
        if(StringUtils.isBlank(cjsjGte)){
            condition.gte(BizLcJl.InnerColumn.cjsj, DateUtils.getDateStr(new Date(),"yyyy-MM-dd")+ " 00:00:00");
        }
        if(StringUtils.isBlank(cjsjLte)){
            condition.lte(BizLcJl.InnerColumn.cjsj, DateUtils.getDateStr(new Date(),"yyyy-MM-dd") + " 23:59:59");
        }*/
        condition.eq(BizLcJl.InnerColumn.zgId, zgId);
        condition.and().andIsNotNull(BizLcJl.InnerColumn.lcFy.name());
        List<BizLcJl> jls = findByCondition(condition);
        LcJlModel model = new LcJlModel();
        if (CollectionUtils.isEmpty(jls)) {
            return ApiResponse.success(model);
        }

        // 所有有记录的安全员
        // 所有的记录id
        List<String> list = jls.stream().map(BizLcJl::getId).collect(Collectors.toList());
        SimpleCondition xyCondition = new SimpleCondition(BizLcJlXy.class);
        xyCondition.in(BizLcJlXy.InnerColumn.lcJlId, list);
        List<BizLcJlXy> xies = xyService.findByCondition(xyCondition);

        model.setZgXm(jls.get(0).getZgXm());
        if (CollectionUtils.isNotEmpty(xies)) {
            xies.sort(Comparator.comparing(BizLcJlXy::getCjsj));
            model.setXyList(xies);
        }

        return ApiResponse.success(model);
    }

    @Override
    public ApiResponse<List<LcJlModel>> getJlTj() {
        List<LcJlModel> list = new ArrayList<>();
        LimitedCondition queryCondition = getQueryCondition();
        /*String cjsjGte = getRequestParamterAsString("cjsjGte");
        String cjsjLte = getRequestParamterAsString("cjsjLte");
        if(StringUtils.isBlank(cjsjGte)){
            queryCondition.gte(BizLcJl.InnerColumn.cjsj, DateUtils.getDateStr(new Date(),"yyyy-MM-dd")+ " 00:00:00");
        }
        if(StringUtils.isBlank(cjsjLte)){
            queryCondition.lte(BizLcJl.InnerColumn.cjsj, DateUtils.getDateStr(new Date(),"yyyy-MM-dd") + " 23:59:59");
        }*/
        queryCondition.and().andIsNotNull(BizLcJl.InnerColumn.lcFy.name());
        List<BizLcJl> lcJls = findByCondition(queryCondition);
        if (CollectionUtils.isNotEmpty(lcJls)) {
            Map<String, List<BizLcJl>> collect = lcJls.stream().collect(Collectors.groupingBy(BizLcJl::getJlXm));
            for (String s : collect.keySet()) {
                LcJlModel model = new LcJlModel();
                List<BizLcJl> jlList = collect.get(s);
                // 累计时长
                int sum = jlList.stream().mapToInt(BizLcJl::getSc).sum();
                // 累计费用
                int sum1 = jlList.stream().mapToInt(BizLcJl::getLcFy).sum();
                if (StringUtils.equals(jlList.get(0).getJlLx(), "00")) {
                    model.setJlXm(jlList.get(0).getJlXm() + "_" + jlList.get(0).getJlJx());
                    model.setJlJx("明涛驾校");
                } else {
                    model.setJlXm(jlList.get(0).getJlXm());
                    model.setJlJx(jlList.get(0).getJlJx());
                }
                model.setSc(sum);
                model.setZj(sum1);
                list.add(model);
            }
        }

        return ApiResponse.success(list);
    }

    @Override
    public void pagerExcel(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> lxMap = new HashMap<>();
        lxMap.put("00", "计时");
        lxMap.put("10", "按次");
        lxMap.put("20", "培优");
        lxMap.put("30", "开放日");
        String time = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        String fileName = time + "-明细统计";
        LimitedCondition condition = getQueryCondition();
        PageInfo<BizLcJl> info = findPage(page, condition);
        List<BizLcJl> list = info.getList();
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "#");
        map.put(1, "车辆编号");
        map.put(2, "开始时间");
        map.put(3, "结束时间");
        map.put(4, "安全员姓名");
        map.put(5, "教练姓名");
        map.put(6, "时长");
        map.put(7, "驾校/队号");
        map.put(8, "学员数量");
        map.put(9, "计费类型");
        map.put(10, "练车费用");
        data.add(map);
        for (int i = 0; i < list.size(); i++) {
            BizLcJl jl = list.get(i);
            Map<Integer, String> dataMap = new HashMap<>();
            dataMap.put(0, i + 1 + "");
            dataMap.put(1, jl.getClBh());
            if (StringUtils.isNotBlank(jl.getKssj())) {
                dataMap.put(2, jl.getKssj().substring(0, jl.getKssj().length() - 3));
            } else {
                dataMap.put(2, " - ");
            }
            if (StringUtils.isNotBlank(jl.getJssj())) {
                dataMap.put(3, jl.getJssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(3, "-");
            }

            dataMap.put(4, jl.getZgXm());
            dataMap.put(5, jl.getJlXm());
            dataMap.put(6, jl.getSc() + "");
            dataMap.put(7, jl.getJlJx());
            dataMap.put(8, jl.getXySl() + "");
            dataMap.put(9, lxMap.get(jl.getLcLx()));
            dataMap.put(10, jl.getLcFy() == null ? "0" : jl.getLcFy() + "");
            data.add(dataMap);
        }
        Map<Integer, String> dataMap = new HashMap<>();
        dataMap.put(0, data.size() + "");
        dataMap.put(1, "合计:");
        dataMap.put(2, "");
        dataMap.put(3, "");
        dataMap.put(4, "");
        dataMap.put(5, "");
        dataMap.put(6, "");
        dataMap.put(7, "");
        dataMap.put(8, "");
        dataMap.put(9, "");
        dataMap.put(10, list.size() == 0 ? "0" : list.stream().filter(bizLcJl -> bizLcJl.getLcFy() != null).mapToInt(BizLcJl::getLcFy).sum() + "");
        data.add(dataMap);

        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        ServletOutputStream outputStream = response.getOutputStream();
        ExcelUtil.createSheet(outputStream, "明细统计", data);
    }

    @Override
    public ApiResponse<String> updateXysl(String id, Integer xySl) {
        BizLcJl jl = findById(id);
        RuntimeCheck.ifNull(jl, "未找到练车记录");
        jl.setXySl(xySl);
        update(jl);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<BizLcJl> getLatestJl(String id) {
        RuntimeCheck.ifBlank(id, "请选择车辆");
        BizLcJl jl = baseMapper.findByClid(id);

        return ApiResponse.success(jl);

    }

    @Override
    public ApiResponse<List<SysZdxm>> getTc(String km, String by5) {
        Map<String, String> map = new HashMap<>();
        map.put("2", "科二");
        map.put("3", "科三");
        SimpleCondition condition = new SimpleCondition(SysZdxm.class);
        condition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition.eq(SysZdxm.InnerColumn.by1, map.get(km));
        if (StringUtils.isNotBlank(by5)) {
            condition.eq(SysZdxm.InnerColumn.by5, by5);
        }
//        condition.eq(SysZdxm.InnerColumn.by2, carType);
        List<SysZdxm> zdxms = zdxmService.findByCondition(condition);
        return ApiResponse.success(zdxms);
    }

    @Override
    public ApiResponse<BizJlCz> saveCz(String no, int je, int sfje) {
        RuntimeCheck.ifBlank(no, "请输入卡号");
        RuntimeCheck.ifTrue(je <= 0, "充值金额必须大于0");
        RuntimeCheck.ifTrue(sfje <= 0, "实付金额必须大于0 ");
        // 根据卡号查询教练信息
        List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.cardNo, no);
        RuntimeCheck.ifEmpty(wxjls, "未找到卡号记录");
        BizLcWxjl wxjl = wxjls.get(0);

        BizJlCz jlCz = new BizJlCz();
        jlCz.setZy("充值");
        jlCz.setType("10");
        jlCz.setJe(je);
        jlCz.setSfje(sfje);
        jlCz.setCjsj(DateUtils.getNowTime());
        jlCz.setCzqje(wxjl.getCardJe());
        wxjl.setCardJe(wxjl.getCardJe() + je);
        jlCz.setCzhje(wxjl.getCardJe());
        String bz = getRequestParamterAsString("bz");
        jlCz.setBz(bz);

        jlCz.setId(genId());
        jlCz.setJlId(wxjl.getId());
        czMapper.insert(jlCz);
        wxjlService.update(wxjl);
        jlCz.setJx(wxjl.getJlJx());
        jlCz.setXm(wxjl.getJlXm());
        return ApiResponse.success(jlCz);
    }

    @Override
    public ApiResponse<String> savePay(String id) {
        int kfje = 0;
        int card = 0;
        int xjje = 0;
        double rate = 0;
        BizLcJl lcJl = findById(id);
        RuntimeCheck.ifNull(lcJl, "未找到练车记录");
        RuntimeCheck.ifTrue(lcJl.getZfzt().equals("10"), "此订单已经支付");
        BizLcFd fd = new BizLcFd();
        String pz = genId();
        lcJl.setPz(pz);
        if (StringUtils.equals(lcJl.getLcLx(), "00")) {
            SysYh yh = getCurrentUser();
            int v = lcJl.getLcFy();
            // 计算应付金额
            BizLcWxjl wxjl = wxjlService.findById(lcJl.getJlId());
            // 先扣款 开放日余额
            // 先扣款 开放日余额
            int ye = wxjl.getYe();
            if (ye > 0) {
                // 有开放日抵扣余额 查询开放日返点率
                SimpleCondition condition = new SimpleCondition(SysZdxm.class);
                condition.eq(SysZdxm.InnerColumn.zddm, lcJl.getZddm().substring(0, 2) + "KF");
                List<SysZdxm> zdxms = zdxmService.findByCondition(condition);
                SysZdxm zdxm = zdxms.get(0);
                rate = Double.parseDouble(zdxm.getBy4());
                int sfje = ye - v;
                // 判断此时实付金额为多少
                if (sfje < 0) {
                    // 如果实付金额小于 0 , 还需要现金
                    kfje = ye;
                    xjje = Math.abs(sfje);
                    // 此处返点 0.4

                    fd.setCjr(yh.getZh() + "-" + yh.getXm());
                    fd.setCjsj(DateUtils.getNowTime());
                    fd.setFdje((int) Math.ceil(xjje * rate));
                    fd.setFdlx(lcJl.getLcLx());
                    fd.setId(pz);
                    fd.setJlXm(wxjl.getJlXm());
                    fd.setJlId(wxjl.getId());
                    fd.setLcId(lcJl.getId());
                    fd.setBz("(" + (ye / 200) + " * 200 - " + lcJl.getLcFy() + ") * " + zdxm.getBy4());
                    fdService.save(fd);
                } else {
                    // 此时已经支付完成
                    kfje = v;
                }
            } else {
                SimpleCondition condition = new SimpleCondition(SysZdxm.class);
                condition.eq(SysZdxm.InnerColumn.zddm, lcJl.getZddm());
                List<SysZdxm> zdxms = zdxmService.findByCondition(condition);
                SysZdxm zdxm = zdxms.get(0);
                rate = Double.parseDouble(zdxm.getBy4());
                int cardJe = wxjl.getCardJe();
                if (cardJe > 0) {
                    int sfje = cardJe - v;
                    if (sfje < 0) {
                        card = cardJe;
                        xjje = Math.abs(sfje);
                        fd.setCjr(yh.getZh() + "-" + yh.getXm());
                        fd.setCjsj(DateUtils.getNowTime());
                        fd.setFdje((int) Math.ceil(xjje * rate));
                        fd.setFdlx(lcJl.getLcLx());
                        fd.setId(pz);
                        fd.setJlXm(wxjl.getJlXm());
                        fd.setJlId(wxjl.getId());
                        fd.setLcId(lcJl.getId());
                        fdService.save(fd);
                    } else {
                        card = v;
                    }
                } else {
                    xjje = v;
                    fd.setCjr(yh.getZh() + "-" + yh.getXm());
                    fd.setCjsj(DateUtils.getNowTime());
                    fd.setFdje((int) Math.ceil(xjje * rate));
                    fd.setFdlx(lcJl.getLcLx());
                    fd.setId(pz);
                    fd.setJlXm(wxjl.getJlXm());
                    fd.setJlId(wxjl.getId());
                    fd.setLcId(lcJl.getId());
                    fdService.save(fd);
                }
            }

            lcJl.setYfJe(xjje);
            lcJl.setZfzt("10");
            lcJl.setXjje(xjje);
            lcJl.setKfje(kfje);
            lcJl.setCardje(card);
            update(lcJl);
            if (card > 0) {
                BizJlCz bizJlCz = new BizJlCz();
                bizJlCz.setJlId(wxjl.getId());
                bizJlCz.setSfje(card);
                bizJlCz.setId(genId());
                bizJlCz.setCzqje(wxjl.getCardJe());
                wxjl.setCardJe(wxjl.getCardJe() - card);
                bizJlCz.setCzhje(wxjl.getCardJe());
                bizJlCz.setCjsj(DateUtils.getNowTime());
                bizJlCz.setJe(card);
                bizJlCz.setType("20");
                czMapper.insert(bizJlCz);
            }
            if (kfje > 0) {
                BizJlCz jlCz = new BizJlCz();
                jlCz.setJlId(wxjl.getId());
                jlCz.setSfje(card);
                jlCz.setId(genId());
                jlCz.setCzqje(wxjl.getYe());
                wxjl.setYe(wxjl.getYe() - kfje);
                jlCz.setCzhje(wxjl.getYe());
                jlCz.setCjsj(DateUtils.getNowTime());
                jlCz.setJe(kfje);
                jlCz.setType("30");
                czMapper.insert(jlCz);
            }
            RuntimeCheck.ifTrue(wxjl.getYe() > 0, "抵扣余额未使用完 , 不支持单个订单支付");
            wxjlService.update(wxjl);
        } else {
            lcJl.setYfJe(lcJl.getLcFy());
            lcJl.setZfzt("10");
            update(lcJl);
        }

        return ApiResponse.success(fd.getId());
    }

    @Override
    public ApiResponse<BizLcJl> getBatchPay(String ids) {
        RuntimeCheck.ifBlank(ids, "请选择要支付的订单");
        List<String> list = Arrays.asList(ids.split(","));
        SimpleCondition condition = new SimpleCondition(BizLcJl.class);
        condition.in(BizLcJl.InnerColumn.id, list);
        condition.eq(BizLcJl.InnerColumn.zfzt, "00");
        List<BizLcJl> jls = findByCondition(condition);
        RuntimeCheck.ifEmpty(jls, "记录为空, 或已经支付过订单");
        List<String> set = jls.stream().map(BizLcJl::getJlId).distinct().collect(Collectors.toList());
        int size = set.size();
        RuntimeCheck.ifTrue(size > 1, "所选记录为多个教练的练车记录 ,请重新选择");
        Set<String> lckms = jls.stream().map(BizLcJl::getLcKm).collect(Collectors.toSet());
        RuntimeCheck.ifTrue(lckms.size() > 1, "请将科目二和科目三的费用分开支付");
        // 总费用
        int sum = jls.stream().map(BizLcJl::getLcFy).mapToInt(value -> value).sum();
        BizLcWxjl wxjl = wxjlService.findById(set.get(0));
        int kfje = 0;
        int card = 0;
        int xjje = 0;
        int ye = wxjl.getYe();
        int cardJe = wxjl.getCardJe();
        if (StringUtils.equals(jls.get(0).getLcKm(), "3")) {
            ye = 0;
        }
        if (ye > 0) {
            int sfje = ye - sum;
            if (sfje < 0) {
                // 扣除开发日余额后 剩下的是现金金额
                kfje = ye;
                xjje = Math.abs(sfje);

            } else {
                kfje = sum;
            }
        } else {
            if (cardJe > 0) {
                int sfje = cardJe - sum;
                if (sfje < 0) {
                    card = cardJe;
                    xjje = Math.abs(sfje);
                } else {
                    card = sum;
                }
            } else {
                xjje = sum;
            }
        }
        int sum1 = jls.stream().map(BizLcJl::getSc).mapToInt(value -> value).sum();
        String str = "";
        String fdr = "";
        if (kfje > 0) {
//            str += " 开放日余额: " + wxjl.getYe();
            fdr = "3";
        }
        if (card > 0) {
//            str += " 卡上余额: " + wxjl.getCardJe();
            fdr = "2";
        }
        if (StringUtils.isBlank(fdr)) {
            fdr = "1";
        }
        str += "应收现金" + xjje + "元";
        if (fdr.contains("2")) {
            // 计算余额
            str += ",卡上余额" + (wxjl.getCardJe() - card) + "元";
        }
        // 查询当天所有非开放日记录
        SimpleCondition condition1 = new SimpleCondition(BizLcJl.class);
        condition1.eq(BizLcJl.InnerColumn.jlId, jls.get(0).getJlId());
        condition1.and().andCondition(" kssj like '%" + DateTime.now().toString("yyyy-MM-dd") + "%' or zfzt = '00'");
        condition1.and().andNotEqualTo(BizLcJl.InnerColumn.lcLx.name(), "30");
        condition.eq(BizLcJl.InnerColumn.lcKm, jls.get(0).getLcKm());
        List<BizLcJl> lcJls = findByCondition(condition1);
        BizLcJl lcJl = new BizLcJl();
        lcJl.setId(ids);
        lcJl.setFdr(fdr);
        lcJl.setBz(str);
        lcJl.setXjje(xjje);
        lcJl.setCardje(cardJe);
        lcJl.setKfje(ye);
        lcJl.setLcFy(sum);
        lcJl.setSc(sum1);
        lcJl.setJlXm(wxjl.getJlXm());
        lcJl.setLcKm(jls.get(0).getLcKm());
        lcJl.setKm(lcJl.getLcKm());
        lcJl.setJls(lcJls);
        lcJl.setClBh(jls.stream().map(BizLcJl::getClBh).collect(Collectors.joining(",")));
        lcJl.setJlCx(jls.stream().map(BizLcJl::getJlCx).collect(Collectors.joining(",")));
        return ApiResponse.success(lcJl);
    }

    @Override
    public ApiResponse<String> saveBatch(String ids) {
        SysYh yh = getCurrentUser();
        RuntimeCheck.ifBlank(ids, "请选择要支付的订单");
        double rate = 0;
        List<String> list = Arrays.asList(ids.split(","));
        SimpleCondition condition = new SimpleCondition(BizLcJl.class);
        condition.in(BizLcJl.InnerColumn.id, list);
        condition.eq(BizLcJl.InnerColumn.zfzt, "00");
        List<BizLcJl> jls = findByCondition(condition);
        RuntimeCheck.ifEmpty(jls, "记录为空, 或已经支付过订单");
        BizLcJl lcJl = jls.get(0);
        List<String> set = jls.stream().map(BizLcJl::getJlId).distinct().collect(Collectors.toList());
        int size = set.size();
        RuntimeCheck.ifTrue(size > 1, "所选记录为多个教练的练车记录 ,请重新选择");
        Set<String> lckms = jls.stream().map(BizLcJl::getLcKm).collect(Collectors.toSet());
        RuntimeCheck.ifTrue(lckms.size() > 1, "请将科目二和科目三的费用分开支付");
        String pz = genId();
        // 总费用
        int sum = jls.stream().map(BizLcJl::getLcFy).mapToInt(value -> value).sum();
        BizLcWxjl wxjl = wxjlService.findById(set.get(0));
        int kfje = 0;
        int card = 0;
        int xjje = 0;
        int ye = wxjl.getYe();
        int cardJe = wxjl.getCardJe();
        // 科目三没有开放日余额
        if (StringUtils.equals(jls.get(0).getLcKm(), "3")) {
            ye = 0;
        }
        BizLcFd fd = new BizLcFd();
        if (ye > 0) {
            // 有开放日抵扣余额 查询开放日返点率
            SimpleCondition zdcondition = new SimpleCondition(SysZdxm.class);
            zdcondition.eq(SysZdxm.InnerColumn.zddm, lcJl.getZddm().substring(0, 2) + "KF");
            List<SysZdxm> zdxms = zdxmService.findByCondition(zdcondition);
            SysZdxm zdxm = zdxms.get(0);
            rate = Double.parseDouble(zdxm.getBy4());
            int sfje = ye - sum;
            if (sfje < 0) {
                // 扣除开发日余额后 剩下的是现金金额
                kfje = ye;
                xjje = Math.abs(sfje);
            } else {
                kfje = sum;
            }
        } else {
            if (cardJe > 0) {
                int sfje = cardJe - sum;
                if (sfje < 0) {
                    card = cardJe;
                    xjje = Math.abs(sfje);
                } else {
                    card = sum;
                }
            } else {
                xjje = sum;
            }
        }


        fd.setFdlx(jls.get(0).getLcLx());
        if (kfje > 0) {
            // 采用的是 余额加现金的情况
            // 先算出所有的余额
            int jlye = wxjl.getYe();
            int czqye = jlye;
            for (BizLcJl jl : jls) {
                if (jlye != 0) {
                    jlye = jlye - jl.getLcFy();
                    if (jlye > 0) {
                        // 余额充足
                        jl.setKfje(jl.getLcFy());
                        jl.setXjje(0);
                    } else {

                        jl.setXjje(Math.abs(jlye));
                        jl.setKfje(jl.getLcFy() + jlye);
                        jlye = 0;
                    }
                    jl.setCardje(0);
                } else {
                    jl.setXjje(jl.getLcFy());
                    jl.setCardje(0);
                    jl.setKfje(0);
                }
                jl.setYfJe(jl.getXjje());
                jl.setZfzt("10");
                jl.setPz(pz);
                update(jl);
            }
            // 使用抵扣余额 , 所有余额清0
            // 生成消费记录
            BizJlCz jlCz = new BizJlCz();
            jlCz.setJlId(wxjl.getId());
            jlCz.setSfje(card);
            jlCz.setId(genId());
            jlCz.setCzqje(czqye);
            wxjl.setYe(czqye);
            jlCz.setCzhje(0);
            jlCz.setCjsj(DateUtils.getNowTime());
            jlCz.setJe(kfje);
            jlCz.setType("30");
            czMapper.insert(jlCz);
            wxjl.setYe(0);
            wxjlService.update(wxjl);

        } else if (card > 0) {
            SimpleCondition zdcondition = new SimpleCondition(SysZdxm.class);
            zdcondition.eq(SysZdxm.InnerColumn.zddm, lcJl.getZddm());
            List<SysZdxm> zdxms = zdxmService.findByCondition(zdcondition);
            SysZdxm zdxm = zdxms.get(0);
            rate = Double.parseDouble(zdxm.getBy4());
            // 卡上余额加 现金支付
            int wxjlCardJe = wxjl.getCardJe();
            int czqye = wxjlCardJe;
            for (BizLcJl jl : jls) {
                if (wxjlCardJe != 0) {
                    wxjlCardJe = wxjlCardJe - jl.getLcFy();
                    if (wxjlCardJe > 0) {
                        // 余额充足
                        jl.setKfje(0);
                        jl.setXjje(0);
                        jl.setCardje(jl.getLcFy());
                    } else {
                        jl.setXjje(Math.abs(wxjlCardJe));
                        jl.setKfje(jl.getLcFy() + wxjlCardJe);
                        wxjlCardJe = 0;
                        jl.setCardje(0);
                    }
                } else {
                    jl.setXjje(jl.getLcFy());
                    jl.setCardje(0);
                    jl.setKfje(0);
                }
                jl.setZfzt("10");
                jl.setPz(pz);
                update(jl);
            }
            if (wxjlCardJe < 0) {
                wxjlCardJe = 0;
            }
            BizJlCz bizJlCz = new BizJlCz();
            bizJlCz.setJlId(wxjl.getId());
            bizJlCz.setSfje(card);
            bizJlCz.setId(genId());
            bizJlCz.setCzqje(czqye);
            wxjl.setCardJe(wxjlCardJe);
            bizJlCz.setCzhje(wxjlCardJe);
            bizJlCz.setCjsj(DateUtils.getNowTime());
            bizJlCz.setJe(card);
            bizJlCz.setType("20");
            czMapper.insert(bizJlCz);
            wxjlService.update(wxjl);
        } else {
            SimpleCondition zdcondition = new SimpleCondition(SysZdxm.class);
            zdcondition.eq(SysZdxm.InnerColumn.zddm, lcJl.getZddm());
            List<SysZdxm> zdxms = zdxmService.findByCondition(zdcondition);
            SysZdxm zdxm = zdxms.get(0);
            rate = Double.parseDouble(zdxm.getBy4());
            for (BizLcJl jl : jls) {
                jl.setKfje(0);
                jl.setXjje(jl.getLcFy());
                jl.setYfJe(jl.getXjje());
                jl.setCardje(0);
                jl.setZfzt("10");
                jl.setPz(pz);
                update(jl);
            }
        }

        // 返点

        fd.setLcId(jls.stream().map(BizLcJl::getId).collect(Collectors.joining(",")));
        fd.setJlId(wxjl.getId());
        fd.setJlXm(wxjl.getJlXm());
        fd.setId(pz);
        fd.setFdje((int) Math.ceil(xjje * rate));
        fd.setCjsj(DateUtils.getNowTime());
        fd.setFdsl(jls.size());
        fd.setCjr(yh.getZh() + "," + yh.getXm());
        fd.setLcFy(xjje);
        fd.setLcKm(jls.get(0).getLcKm());
        fd.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
        if (fd.getFdje() > 0) {
            if (kfje > 0) {
                fd.setBz("(" + (ye / 200) + " * 200 - " + lcJl.getLcFy() + ") * " + rate);
            } else if (card > 0) {
                fd.setBz("(" + cardJe + " - " + lcJl.getLcFy() + ") * " + rate);
            } else {
                fd.setBz("(" + xjje + " * " + rate + ")");
            }
            fdService.save(fd);
        }
        return ApiResponse.success(pz);

    }

    @Override
    public ApiResponse<BizLcJl> getByPz(String pz) {
        RuntimeCheck.ifBlank(pz, "请选择要打印的凭证");
        List<BizLcJl> jls = findEq(BizLcJl.InnerColumn.pz, pz);
        RuntimeCheck.ifEmpty(jls, "未找到记录");
        int sum = jls.stream().mapToInt(BizLcJl::getXjje).sum();
        int lcfy = jls.stream().mapToInt(BizLcJl::getLcFy).sum();
        String id = jls.get(0).getJlId();
        String zddm = jls.get(0).getZddm();
        SimpleCondition simpleCondition  = new SimpleCondition(SysZdxm.class);
        simpleCondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        simpleCondition.eq(SysZdxm.InnerColumn.zddm, zddm);
        List<SysZdxm> zdxms = zdxmService.findByCondition(simpleCondition);
        String by9 = zdxms.get(0).getBy9();
        BizLcWxjl wxjl = wxjlService.findById(id);
        BizLcJl jl = new BizLcJl();
        jl.setId(jls.get(0).getPz());
        jl.setZdmc(by9);
        jl.setZgXm(jls.get(0).getZgXm());
        jl.setLcFy(lcfy);
        jl.setJlCx(jls.stream().map(BizLcJl::getJlCx).collect(Collectors.joining(",")));
        jl.setClBh(jls.stream().map(BizLcJl::getClBh).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")));
        jl.setYfJe(sum);
        jl.setXjje(sum);
        jl.setLcLx(jls.get(0).getLcLx());
        jl.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
        jl.setKm(jls.get(0).getLcKm());
        jl.setLcKm(jls.get(0).getLcKm());
        jl.setJlJx(wxjl.getJlJx());
        jl.setJlXm(wxjl.getJlXm());
        jl.setKfje(jls.stream().filter(lcJl -> lcJl.getKfje() != null ).mapToInt(BizLcJl::getKfje).sum());
        jl.setCardje(jls.stream().filter(lcJl -> lcJl.getCardje() != null ).mapToInt(BizLcJl::getCardje).sum());
        jl.setJssj(jls.stream().map(BizLcJl::getJssj).sorted(Comparator.reverseOrder()).collect(Collectors.toList()).get(0));
        String fdr = "";
        String str = "";
        if (jl.getKfje() != null && jl.getKfje() > 0) {
            fdr += "1,";
        }
        if (jl.getCardje() != null && jl.getCardje() > 0) {
            fdr += "2,";
        }
        if (jl.getXjje() > 0) {
            fdr += "3";

        }
        str += "应付现金" + jl.getXjje() + "元";
        if (fdr.contains("2")) {
            // 计算余额
            str += ",卡上余额" + wxjl.getCardJe() + "元";
        }
        jl.setBz(str);
        jl.setFdr(fdr);
        jl.setKfje(wxjl.getYe());
        jl.setCardje(wxjl.getCardJe());
        if(StringUtils.equals(jl.getLcLx(),"30")){
            jl.setBz(jls.get(0).getXySl() + "人");
        }
        if(StringUtils.equals(jl.getLcLx(), "20")){
            // 培优展示姓名加车型
            jl.setBz(jls.stream().map(BizLcJl::getXyXm).collect(Collectors.joining(",")));
        }
        return ApiResponse.success(jl);
    }


    @Override
    public ApiResponse<BizLcJl> payCNY(String id, String zf) {

        RuntimeCheck.ifBlank(id, "请选择要支付的订单");
        RuntimeCheck.ifBlank(zf, "请选择支付方式");
        SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
        jlCondition.in(BizLcJl.InnerColumn.id, Arrays.asList(id.split(",")));
        jlCondition.eq(BizLcJl.InnerColumn.zfzt, "00");
        List<BizLcJl> jls = findByCondition(jlCondition);
        Set<String> set = jls.stream().map(BizLcJl::getLcLx).collect(Collectors.toSet());
        RuntimeCheck.ifEmpty(jls, "请选择要支付的订单");
        set.forEach(s -> {
            RuntimeCheck.ifFalse(StringUtils.equals(s, "00"), "所选订单不支持此操作");
        });

        Set<String> zddms = jls.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
        String pz = genId();
        BizLcWxjl wxjl = wxjlService.findById(jls.get(0).getJlId());
        // 查询套餐数据
        SimpleCondition condition = new SimpleCondition(SysZdxm.class);
        condition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition.in(SysZdxm.InnerColumn.zddm, zddms);
        List<SysZdxm> zdxms = zdxmService.findByCondition(condition);
        int sum = jls.stream().mapToInt(BizLcJl::getLcFy).sum();
        SysZdxm zdxm = zdxms.get(0);
        SysYh user = getCurrentUser();
        BizLcJl lcJl = new BizLcJl();
        lcJl.setId(pz);
        lcJl.setKfje(wxjl.getYe());
        lcJl.setCardje(wxjl.getCardJe());
        lcJl.setZdxm(zdxm);
        lcJl.setLcFy(sum);
        lcJl.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
        lcJl.setClBh(jls.stream().map(BizLcJl::getClBh).collect(Collectors.joining(",")));
        lcJl.setJlCx(jls.stream().map(BizLcJl::getJlCx).collect(Collectors.joining(",")));
        lcJl.setJlJx(wxjl.getJlJx());
        lcJl.setJlXm(wxjl.getJlXm());
        if (StringUtils.equals(zf, "1")) {
            for (BizLcJl jl : jls) {
                // 总费用
                Integer fy = jl.getLcFy();
                jl.setZfzt("10");
                jl.setCardje(0);
                jl.setYfJe(fy);
                jl.setXjje(fy);
                jl.setPz(pz);
                update(jl);
            }
            // 拿到返点率
            float fdl = Float.parseFloat(zdxm.getBy4());
            int fdje = (int) Math.ceil(sum * fdl);
            if (fdje > 0) {
                BizLcFd fd = new BizLcFd();
                fd.setJlJx(jls.get(0).getJlJx());
                fd.setCjr(user.getZh() + "-" + user.getXm());
                fd.setCjsj(DateUtils.getNowTime());
                fd.setFdje(fdje);
                fd.setFdlx(jls.get(0).getLcLx());
                fd.setFdsl(1);
                fd.setId(pz);
                fd.setJlId(jls.get(0).getJlId());
                fd.setJlXm(jls.get(0).getJlXm());
                fd.setLcFy(sum);
                fd.setLcId(jls.stream().map(BizLcJl::getId).collect(Collectors.joining(",")));
                fd.setLcKm(jls.get(0).getLcKm());
                fd.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
                fd.setBz("(" + sum + " * " + fdl + ")");
                fdService.save(fd);
            }
            lcJl.setBz("应收现金" + sum + "元");
        } else if (StringUtils.equals(zf, "2")) {
            // 卡上余额支付
            // 1. 先计算卡上余额是否充足 , 如果充足 则 直接扣款 , 不返点

            // 卡上余额
            int cardJe = wxjl.getCardJe();
            RuntimeCheck.ifTrue(cardJe <= 0, "卡上余额为0 , 请选择其他支付方式");
            int sfje = cardJe - sum;

            // 判断剩余余额是否小于0 , 如果小于0 就说明还需要支付现金

            int czqye = cardJe;
            for (BizLcJl jl : jls) {
                if (cardJe != 0) {
                    cardJe = cardJe - jl.getLcFy();
                    if (cardJe > 0) {
                        // 余额充足
                        jl.setKfje(0);
                        jl.setXjje(0);

                    } else {
                        jl.setXjje(Math.abs(cardJe));
                        jl.setKfje(jl.getLcFy() + cardJe);
                        cardJe = 0;
                        jl.setCardje(0);
                    }
                } else {
                    jl.setXjje(jl.getLcFy());
                    jl.setKfje(0);
                }
                if(sfje < 0 ){
                    jl.setCardje(cardJe);
                }else{
                    jl.setCardje(sum);
                }
                jl.setZfzt("10");
                jl.setPz(pz);
                update(jl);
            }
            if (cardJe < 0) {
                cardJe = 0;
            }
            BizJlCz bizJlCz = new BizJlCz();
            bizJlCz.setJlId(wxjl.getId());
            bizJlCz.setSfje(sfje < 0 ? cardJe : sum);
            bizJlCz.setId(genId());
            bizJlCz.setCzqje(czqye);
            wxjl.setCardJe(Math.max(sfje, 0));
            bizJlCz.setCzhje(Math.max(sfje, 0));
            bizJlCz.setCjsj(DateUtils.getNowTime());
            bizJlCz.setJe(sfje < 0 ? cardJe : sum);
            bizJlCz.setType("20");
            czMapper.insert(bizJlCz);
            wxjlService.update(wxjl);
            // 需要支付现金 , 此时需要返点
            if (sfje < 0) {
                int abs = Math.abs(sfje);
                float aFloat = Float.parseFloat(zdxm.getBy4());
                int v = (int) Math.ceil(abs * aFloat);
                if (v > 0) {
                    BizLcFd fd = new BizLcFd();
                    fd.setJlJx(jls.get(0).getJlJx());
                    fd.setCjr(user.getZh() + "-" + user.getXm());
                    fd.setCjsj(DateUtils.getNowTime());
                    fd.setFdje(v);
                    fd.setFdlx(jls.get(0).getLcLx());
                    fd.setFdsl(1);
                    fd.setId(jls.get(0).getId());
                    fd.setJlId(jls.get(0).getJlId());
                    fd.setJlXm(jls.get(0).getJlXm());
                    fd.setLcFy(abs);
                    fd.setLcId(jls.stream().map(BizLcJl::getId).collect(Collectors.joining(",")));
                    fd.setLcKm(jls.get(0).getLcKm());
                    fd.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
                    fd.setBz("(" + cardJe + " - " + sum + ") * " + aFloat);
                    fdService.save(fd);
                }
                lcJl.setBz("应收现金" + abs + "元,卡上余额" + wxjl.getCardJe() + "元");
            }else{
                lcJl.setBz("应收现金" + 0 + "元,卡上余额" + wxjl.getCardJe() + "元");
            }
        } else if (StringUtils.equals(zf, "3")) {
            String c = getRequestParamterAsString("c");
            RuntimeCheck.ifBlank(c, "请填写学员人数");
            // 抵扣支付 不够的按现金结算
            SimpleCondition simpleCondition = new SimpleCondition(SysZdxm.class);
            simpleCondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
            simpleCondition.eq(SysZdxm.InnerColumn.zddm, "K2KF");
            List<SysZdxm> sysZdxms = zdxmService.findByCondition(simpleCondition);
            String by4 = sysZdxms.get(0).getBy4();
            int xySl = Integer.parseInt(c);
            // 计算单个学员的保底费用
            int dkdj = Integer.parseInt(sysZdxms.get(0).getZdmc()) - Integer.parseInt(sysZdxms.get(0).getBy3());
            int i = wxjl.getYe() / dkdj;
            RuntimeCheck.ifTrue(i < xySl, "抵扣人数不能大于开放日学员人数");
            // 总抵扣额度
            int kfje = xySl * dkdj;
            // 教练总开放余额 减去开放日金额

            int syje = kfje - sum;
            int abs = 0;
            if (syje > 0) {
                // 还有金额 , 直接抵扣完 , 不产生费用
                wxjl.setYe(wxjl.getYe() - kfje);
                wxjlService.update(wxjl);
                for (BizLcJl jl : jls) {
                    jl.setZfzt("10");
                    jl.setXjje(0);
                    jl.setKfje(kfje);
                    jl.setXjje(0);
                    jl.setPz(pz);
                    update(jl);
                }
            } else {
                // 不够部分按现金计算
                wxjl.setYe(wxjl.getYe() - kfje);
                wxjlService.update(wxjl);
                abs = Math.abs(syje);
                for (BizLcJl jl : jls) {
                    if (kfje != 0) {
                        kfje = kfje - jl.getLcFy();
                        if (kfje > 0) {
                            // 余额充足
                            jl.setKfje(jl.getLcFy());
                            jl.setXjje(0);
                        } else {

                            jl.setXjje(Math.abs(kfje));
                            jl.setKfje(jl.getLcFy() + kfje);
                            kfje = 0;
                        }
                        jl.setCardje(0);
                    } else {
                        jl.setXjje(jl.getLcFy());
                        jl.setCardje(0);
                        jl.setKfje(0);
                    }
                    jl.setYfJe(jl.getXjje());
                    jl.setZfzt("10");
                    jl.setPz(pz);
                    update(jl);
                }

                // 生成返点金额
                float aFloat = Float.parseFloat(by4);
                int v = (int) Math.ceil(abs * aFloat);
                if (v > 0) {
                    BizLcFd fd = new BizLcFd();
                    fd.setJlJx(jls.get(0).getJlJx());
                    fd.setCjr(user.getZh() + "-" + user.getXm());
                    fd.setCjsj(DateUtils.getNowTime());
                    fd.setFdje(v);
                    fd.setFdlx(jls.get(0).getLcLx());
                    fd.setFdsl(1);
                    fd.setId(pz);
                    fd.setJlId(jls.get(0).getJlId());
                    fd.setJlXm(jls.get(0).getJlXm());
                    fd.setLcFy(abs);
                    fd.setLcId(jls.stream().map(BizLcJl::getId).collect(Collectors.joining(",")));
                    fd.setLcKm(jls.get(0).getLcKm());
                    fd.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
                    fd.setBz("(" + xySl + " * " + dkdj + " - " + sum + ") * " + aFloat);
                    fdService.save(fd);
                }
            }
            lcJl.setBz("应收现金" + abs + "元");

        }
        lcJl.setPz(pz);
        return ApiResponse.success(lcJl);
    }




}