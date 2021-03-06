package com.ldz.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizJlCzMapper;
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
import com.ldz.util.commonUtil.EncryptUtil;
import com.ldz.util.commonUtil.ExcelUtil;
import com.ldz.util.commonUtil.JsonUtil;
import com.ldz.util.exception.RuntimeCheck;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.format.*;
import jxl.write.*;
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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private BizJlCzMapper czMapper;
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private BizLcFdsService fdsService;

    @Override
    protected Mapper<BizLcJl> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public boolean fillPagerCondition(LimitedCondition condition) {
        String lx = getRequestParamterAsString("lx");
        if (StringUtils.isNotBlank(lx)) {
            condition.eq(BizLcJl.InnerColumn.jlLx, lx);
        }
        String zflx = getRequestParamterAsString("zflx");
        if (StringUtils.isNotBlank(zflx)) {
            if (StringUtils.equals(zflx, "1")) {
                condition.and().andCondition(" xjje > 0");
            } else if (StringUtils.equals(zflx, "2")) {
                condition.and().andCondition(" kfje > 0");
            } else if (StringUtils.equals(zflx, "3")) {
                condition.and().andCondition(" cardje > 0");
            } else {
                return false;
            }
        }
        String orgcode = getRequestParamterAsString("orgcode");
        if(StringUtils.isNotBlank(orgcode)) {
            // 根据队号查出教练员id
            List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.dh, orgcode);
            if(CollectionUtils.isEmpty(wxjls)) {
                return false;
            }
            Set<String> set = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toSet());
            condition.in(BizLcJl.InnerColumn.jlId, set);
        }
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
//            SimpleCondition condition = new SimpleCondition(BizLcJlXy.class);
//            condition.eq(BizLcJlXy.InnerColumn.lcJlId, bizLcJl.getId());
//            List<BizLcJlXy> xies = xyService.findByCondition(condition);
//            bizLcJl.setXyList(xies);

            // 计算下支付方式
            if (bizLcJl.getKfje() != null && bizLcJl.getKfje() > 0) {
                bizLcJl.setZffs("开放日·" + (int) (Math.ceil(bizLcJl.getKfje() * 1.0 / Integer.parseInt(StringUtils.isBlank(bizLcJl.getDkdj()) ? "200" : bizLcJl.getDkdj()))) + "人");
            } else if (bizLcJl.getCardje() != null && bizLcJl.getCardje() > 0) {
                bizLcJl.setZffs("充值卡");
            } else if (StringUtils.equals(bizLcJl.getZfzt(), "10")) {
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
        // 避免数据重复提交
        String lc = redis.boundValueOps("lcjl").get();
        if (StringUtils.isNotBlank(lc)) {
            RuntimeCheck.ifTrue(StringUtils.equals(lc, s), "操作频繁,请稍后再试");
        }
        redis.boundValueOps("lcjl").set(s, 2, TimeUnit.SECONDS);
        SysYh currentUser = getCurrentUser();
        //  判断所有必填字段
        RuntimeCheck.ifBlank(entity.getJlId(), "请选择教练");
        BizLcWxjl wxjl = wxjlService.findById(entity.getJlId());
        // 根据教练判断本校外校
        entity.setJlLx(wxjl.getJlLx());
        entity.setId(genId());
        // 科目二 190 套餐 ,  需要根据人数计费
        if (StringUtils.equals(entity.getZddm(), "K2JS-S")) {
            RuntimeCheck.ifNull(entity.getXySl(), "此套餐需要填写学员数量");
            RuntimeCheck.ifTrue(entity.getXySl() <= 0, "学员数量不能小于0");
        }
        RuntimeCheck.ifBlank(entity.getZddm(), "请选择套餐");
        SimpleCondition condition = new SimpleCondition(SysZdxm.class);
        condition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition.eq(SysZdxm.InnerColumn.zddm, entity.getZddm());
        List<SysZdxm> zdxms = zdxmService.findByCondition(condition);
        RuntimeCheck.ifEmpty(zdxms, "未找到套餐信息");
        // 拿到练车套餐的内容
        SysZdxm zdxm = zdxms.get(0);
        // 科目三培优需要填写学员人数
        if (StringUtils.equals(entity.getLcKm(), "3") && !StringUtils.equals(entity.getLcLx(), "20")) {
            RuntimeCheck.ifNull(entity.getXySl(), "请填写学员人数");
            RuntimeCheck.ifTrue(entity.getXySl() <= 0, "学员数量必须大于0");
        }
        BizLcCl lcCl = null;
        // 是否有选择车辆 , 如果有需要判断车辆状态
        if (StringUtils.isNotBlank(entity.getLcClId())) {
            lcCl = clService.findById(entity.getLcClId());
            RuntimeCheck.ifTrue("01".equalsIgnoreCase(lcCl.getClZt()), lcCl.getClBh() + "号车辆已经在训练中");
            if (StringUtils.isNotBlank(entity.getKm())) {
                String mess = "此车辆已绑定科目" + ("2".equals(lcCl.getClKm()) ? "二" : "三") + "-" + lcCl.getClBh() + "车,不能在科目" + ("2".equals(entity.getKm()) ? "二" : "三") + "窗口发车";
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
        // 根据套餐内容判断此次发车是否需要刷卡
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
                RuntimeCheck.ifFalse(StringUtils.equals(lcCl.getCardNo(), entity.getCardNo()), "卡号与车号不匹配,该卡已绑定" + clBh + "号车!");
            }
        }
        // 根据所选套餐判断训练类型
        entity.setLcLx(zdxm.getBy5());
        entity.setJlJx(wxjl.getJlJx());
        // 如果不是 培优 和开放日 则需要选择车辆
        if (!StringUtils.equals(entity.getLcLx(), "30") && !StringUtils.equals(entity.getLcLx(), "20")) {
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
        // 科目三发车需要选择安全员 (培优除外)
        if ((StringUtils.equals(entity.getLcKm(), "3") && !StringUtils.equals(entity.getLcLx(), "20")) || StringUtils.isNotBlank(entity.getZgId())) {
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
        // 设置发车时间
        LocalDateTime now = LocalDateTime.now();
        String nowTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        entity.setCjsj(nowTime);
        entity.setKssj(nowTime);
        // 如果不是计时类型 则直接计算费用 返点 都在生成记录时产生
        if (!StringUtils.equals(entity.getLcLx(), "00") && StringUtils.isNotBlank(entity.getLcLx())) {
            entity.setPz(entity.getId());
            // 套餐类型不是计时培训 需要在记录的时候就将费用计算出来 , 有返点的直接记录返点
            String jg = zdxm.getZdmc();
            if (StringUtils.equals(entity.getLcLx(), "20")) {
                // 培优价格为本身价格
                entity.setZfzt("10");
                if ("2".equals(entity.getLcKm())) {
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
                if (StringUtils.equals(entity.getLcKm(), "2")) {
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
                int czje = entity.getLcFy();
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
            if (StringUtils.equals(entity.getLcKm(), "2") && StringUtils.equals(entity.getLcLx(), "20")) {
                entity.setXjje(entity.getYfJe());
                // 开放日返点金额
                int fdje = Integer.parseInt(zdxm.getBy3()) * entity.getXySl();
                entity.setJssj(nowTime);
                // 类型为开放练习 此时需要直接返点
                RuntimeCheck.ifFalse(entity.getXySl() > 0, "培优需要填写学员信息");
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
        // 科三培优可能要返点
        if (StringUtils.equals(entity.getLcKm(), "3") && StringUtils.equals(entity.getLcLx(), "20")) {
            entity.setJssj(nowTime);
            double v = Double.parseDouble(zdxm.getBy4());

            int v1 = (int) Math.ceil(entity.getLcFy() * v);
            if (v1 > 0) {
                // 新增返点记录
                BizLcFd lcFd = new BizLcFd();
                lcFd.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
                lcFd.setCjsj(nowTime);
                lcFd.setFdje(v1);
                lcFd.setId(entity.getId());
                lcFd.setJlId(wxjl.getId());
                lcFd.setJlXm(wxjl.getJlXm());
                lcFd.setLcId(entity.getId());
                lcFd.setFdlx(entity.getLcLx());
                lcFd.setLcFy(entity.getLcFy());
                lcFd.setLcKm(entity.getLcKm());
                lcFd.setSc(0);
                lcFd.setBz("(" + entity.getXySl() + " * " + v1 + ")");
                fdService.save(lcFd);
                entity.setPz(lcFd.getId());
            }
        }

        entity.setCjr(currentUser.getZh() + "-" + currentUser.getXm());
        entity.setLcYj(entity.getLcYj() == null ? 0 : entity.getLcYj());

        entity.setJlXm(wxjl.getJlXm());
        entity.setJlJx(wxjl.getJlJx());
        entity.setJlDh(wxjl.getJlLxdh());
        entity.setFdZt("30");
        if (!StringUtils.equals(entity.getLcLx(), "00") && StringUtils.equals(entity.getLcKm(), "2")) {
            entity.setJlCx("");
        }
        save(entity);
        if (StringUtils.equals(entity.getLcLx(), "20")) {
            entity.setClBh("培优");
        } else if (StringUtils.equals(entity.getLcLx(), "30")) {
            entity.setClBh("开放日");
            entity.setBz(entity.getXySl() + "人");
        }
        if (StringUtils.isBlank(entity.getZgXm())) {
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
        // 计算实际时长  (所有车辆免费前五分钟)
        long ksfz = start.getTime() / (60 * 1000);
        long jsfz = end.getTime() / (60 * 1000);
        int lcSc = (int) (jsfz - ksfz);
        lcJl.setSc(lcSc);
        if (StringUtils.equals(lcJl.getLcLx(), "00")) {
            RuntimeCheck.ifTrue(StringUtils.equals(km, "2") && !StringUtils.equals(km, lcJl.getLcKm()), "请前往科目二窗口还车");
            RuntimeCheck.ifTrue(StringUtils.equals(km, "3") && !StringUtils.equals(km, lcJl.getLcKm()), "请前往科目三窗口还车");
            int v;
            if (StringUtils.equals(lcJl.getLcKm(), "2") && StringUtils.equals(lcJl.getZddm(), "K2JS-S")) {
                // 科目二的 190 35分钟 , 超出时间按照 8.33计算
                String by3 = management.getBy3();
                // 190
                String hour = management.getZdmc();
                if (lcSc > (management.getQz() * lcJl.getXySl())) {
                    v = (int) Math.ceil((lcSc - (management.getQz() * lcJl.getXySl())) * Float.parseFloat(by3)) + (Integer.parseInt(hour) * lcJl.getXySl());
                } else {
                    v = Integer.parseInt(hour) * lcJl.getXySl();
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
            lcJl.setSc(0);
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
        lcJl.setJssj(s);
        lcJl.setXgr(yh.getZh() + "-" + yh.getXm());
        lcJl.setXgsj(DateUtils.getNowTime());
        if (StringUtils.equals(lcJl.getLcLx(), "00") && !StringUtils.equals(lcJl.getJlXm(), "自学")) {
            lcJl.setFdZt("00");
        } else {
            lcJl.setFdZt("40");
        }
        ApiResponse<String> kfDj = getKfDj(lcJl.getJlId());
        RuntimeCheck.ifFalse(kfDj.getCode() == 200, "数据异常 , 请联系开发人员处理");
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
            result.setKfDj(Integer.parseInt(kfDj.getMessage().split("-")[0]));
            return ApiResponse.success(result);
        } else {
            update(lcJl);
            lcJl.setKfDj(Integer.parseInt(kfDj.getMessage().split("-")[0]));
            return ApiResponse.success(lcJl);
        }
    }

    @Override
    public List<Map<String, Object>> drivingSchoolStatistics() {
        String lcKm = getRequestParamterAsString("lcKm");
//        RuntimeCheck.ifBlank(lcKm, "请选择科目");
        String tjsj = getRequestParamterAsString("tjsj");
        if (StringUtils.isEmpty(tjsj)) {
            tjsj = DateUtils.getToday() + " 00:00:00," + DateUtils.getToday() + " 23:59:59";
        }
        String[] sj = tjsj.split(",");
        String kssj = sj[0];
        String jssj = sj[1];
        String sql = "SELECT jl_jx,sum(sc) as sc,sum(xjje) as fy  from BIZ_LC_JL where 1=1 ";
        sql += " and zfzt = '10' ";
        sql += " and kssj >= '" + kssj + "' and jssj <= '" + jssj + "'";
        if (StringUtils.isNotBlank(lcKm)) {
            sql += " and lc_km ='" + lcKm + "'";
        }
        String lx = getRequestParamterAsString("lx");
        if (StringUtils.isNotBlank(lx)) {
            List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, lx);
            if (CollectionUtils.isNotEmpty(wxjls)) {
                String ids = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.joining(","));
                sql += " and jl_id in (" + ids + ") ";
            } else {
                return new ArrayList<>();
            }
        }


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
        if (StringUtils.isEmpty(cjsjIn)) {
            cjsjIn = DateUtils.getToday() + " 00:00:00," + DateUtils.getToday() + " 23:59:59";
        }
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
        RuntimeCheck.ifTrue("01".equalsIgnoreCase(lcCl.getClZt()), "当前车辆已经在训练中");
        if (StringUtils.isNotBlank(entity.getKm())) {
            RuntimeCheck.ifFalse(lcCl.getClKm().equalsIgnoreCase(entity.getKm()), "此卡号已绑定科目" + ("2".equals(lcCl.getClKm()) ? "二" : "三") + " - " + lcCl.getClBh() + "车,不能在科目" + ("2".equals(entity.getKm()) ? "二" : "三") + "窗口发车");
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
            Map<String, List<BizLcJl>> collect = jls.stream().filter(bizLcJl -> StringUtils.isNotBlank(bizLcJl.getJssj())).filter(lcJl -> StringUtils.isNotBlank(lcJl.getZgId())).collect(Collectors.groupingBy(BizLcJl::getZgId));
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
        queryCondition.and().andIsNotNull(BizLcJl.InnerColumn.lcFy.name());
        String lx = getRequestParamterAsString("lx");
        if (StringUtils.isNotBlank(lx)) {
            List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, lx);
            if (CollectionUtils.isNotEmpty(wxjls)) {
                List<String> collect = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
                queryCondition.in(BizLcJl.InnerColumn.jlId, collect);
            } else {
                return ApiResponse.success(new ArrayList<>());
            }
        }
        List<BizLcJl> lcJls = findByCondition(queryCondition);
        if (CollectionUtils.isNotEmpty(lcJls)) {
            String range = getRequestParamterAsString("cjsjInRange");
            Set<String> jlids = lcJls.stream().map(BizLcJl::getJlId).collect(Collectors.toSet());

            SimpleCondition fd = new SimpleCondition(BizLcFd.class);
            fd.in(BizLcFd.InnerColumn.jlId, jlids);
            if (StringUtils.isNotBlank(range)) {
                String[] split = range.split(",");
                fd.gte(BizLcFd.InnerColumn.cjsj, split[0]);
                fd.lte(BizLcFd.InnerColumn.cjsj, split[1]);
            }
            List<BizLcFd> lcFds = fdService.findByCondition(fd);
            Map<String, List<BizLcFd>> fdMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(lcFds)) {
                fdMap = lcFds.stream().collect(Collectors.groupingBy(BizLcFd::getJlId));
            }

            List<BizLcWxjl> wxjls = wxjlService.findByIds(jlids);
            Map<String, BizLcWxjl> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(wxjls)) {
                map = wxjls.stream().collect(Collectors.toMap(BizLcWxjl::getId, p -> p));
            }
            Map<String, List<BizLcJl>> collect = lcJls.stream().collect(Collectors.groupingBy(BizLcJl::getJlId));
            for (String s : collect.keySet()) {
                LcJlModel model = new LcJlModel();
                List<BizLcFd> fds = fdMap.get(s);
                if (CollectionUtils.isNotEmpty(fds)) {
                    int yfd = fds.stream().filter(lcFd -> StringUtils.isNotBlank(lcFd.getQrsj())).mapToInt(BizLcFd::getFdje).sum();
                    int dfd = fds.stream().filter(lcFd -> StringUtils.isBlank(lcFd.getQrsj())).mapToInt(BizLcFd::getFdje).sum();
                    model.setYfd(yfd);
                    model.setDfd(dfd);
                    model.setZfd(yfd + dfd);
                }
                BizLcWxjl wxjl = map.get(s);
                List<BizLcJl> jlList = collect.get(s);
                // 累计时长
                int sum = jlList.stream().mapToInt(BizLcJl::getSc).sum();
                // 累计费用
                int sum1 = jlList.stream().mapToInt(BizLcJl::getXjje).sum();

                int sum2 = jlList.stream().mapToInt(BizLcJl::getLcFy).sum();
                if (StringUtils.equals(jlList.get(0).getJlLx(), "00")) {
                    model.setJlXm(jlList.get(0).getJlXm() + "_" + jlList.get(0).getJlJx());
                    model.setJlJx("明涛驾校");
                } else {
                    model.setJlXm(jlList.get(0).getJlXm());
                    model.setJlJx(jlList.get(0).getJlJx());
                }
                model.setSc(sum);
                model.setZj(sum1);
                model.setZje(sum2);
                if (wxjl != null) {
                    model.setDm(wxjl.getDm());
                }

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
        condition.and().andCondition(" jssj is not null and jssj != ''");
        PageInfo<BizLcJl> info = findPage(page, condition);
        List<BizLcJl> list = info.getList();
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "#");
        map.put(1, "驾校");
        map.put(2, "教练员");
        map.put(3, "开始时间");
        map.put(4, "结束时间");
        map.put(5, "时长");
        map.put(6, "应收");
        map.put(7, "实收");
        map.put(8, "支付方式");
        map.put(9, "类型");
        data.add(map);
        Set<String> collect = list.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
        SimpleCondition condition1 = new SimpleCondition(SysZdxm.class);
        condition1.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition1.in(SysZdxm.InnerColumn.zddm, collect);
        List<SysZdxm> zdxms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(collect)) {
            zdxms = zdxmService.findByCondition(condition1);
        }

        Map<String, String> stringMap = zdxms.stream().collect(Collectors.toMap(SysZdxm::getZddm, SysZdxm::getBy9));
        for (int i = 0; i < list.size(); i++) {
            BizLcJl jl = list.get(i);
            Map<Integer, String> dataMap = new HashMap<>();
            dataMap.put(0, i + 1 + "");
            dataMap.put(1, jl.getJlJx());
            dataMap.put(2, jl.getJlXm());
            if (StringUtils.isNotBlank(jl.getKssj())) {
                dataMap.put(3, jl.getKssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(3, "-");
            }
            if (StringUtils.isNotBlank(jl.getJssj())) {
                dataMap.put(4, jl.getJssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(4, "-");
            }


            dataMap.put(5, jl.getSc() + "");
            dataMap.put(6, jl.getLcFy() + "");
            dataMap.put(7, jl.getXjje() + "");
            // 计算下支付方式
            if (jl.getKfje() != null && jl.getKfje() > 0) {
                dataMap.put(8, "开放日·" + (int) (Math.ceil(jl.getKfje() * 1.0 / 200)) + "人");
            } else if (jl.getCardje() != null && jl.getCardje() > 0) {
                dataMap.put(8, "充值卡");
            } else if (StringUtils.equals(jl.getZfzt(), "10")) {
                dataMap.put(8, "现金");
            }

            dataMap.put(9, stringMap.get(jl.getZddm()));
            data.add(dataMap);
        }
        Map<Integer, String> dataMap = new HashMap<>();
        dataMap.put(0, data.size() + "");
        dataMap.put(1, "合计:");
        dataMap.put(2, "");
        dataMap.put(3, "");
        dataMap.put(4, "");
        dataMap.put(5, "");
        dataMap.put(7, list.stream().filter(jl -> jl.getXjje() != null).mapToInt(BizLcJl::getXjje).sum() + "");
        dataMap.put(8, "");
        dataMap.put(9, "");
        dataMap.put(6, list.size() == 0 ? "0" : list.stream().filter(bizLcJl -> bizLcJl.getLcFy() != null).mapToInt(BizLcJl::getLcFy).sum() + "");
        data.add(dataMap);

        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        ServletOutputStream outputStream = response.getOutputStream();
        ExcelUtil.createSheet(outputStream, "明细统计", data);
    }

    @Override
    public void pagerExcelK3(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String time = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        String fileName = time + "-明细统计";
        LimitedCondition condition = getQueryCondition();
        condition.and().andCondition(" jssj is not null and jssj != ''");
        PageInfo<BizLcJl> info = findPage(page, condition);
        List<BizLcJl> list = info.getList();
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "#");
        map.put(1, "驾校");
        map.put(2, "教练员");
        map.put(3, "车号");
        map.put(4, "人数");
        map.put(5, "车型");
        map.put(6, "类型");
        map.put(7, "开始时间");
        map.put(8, "结束时间");
        map.put(9, "时长");
        map.put(10, "应收");
        map.put(11, "实收");
        map.put(12, "订单状态");
        map.put(13, "支付方式");
        map.put(14, "安全员");
        data.add(map);
        Set<String> collect = list.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
        SimpleCondition condition1 = new SimpleCondition(SysZdxm.class);
        condition1.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition1.in(SysZdxm.InnerColumn.zddm, collect);
        List<SysZdxm> zdxms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(collect)) {
            zdxms = zdxmService.findByCondition(condition1);
        }

        Map<String, String> stringMap = zdxms.stream().collect(Collectors.toMap(SysZdxm::getZddm, SysZdxm::getBy9));
        for (int i = 0; i < list.size(); i++) {
            BizLcJl jl = list.get(i);
            Map<Integer, String> dataMap = new HashMap<>();
            dataMap.put(0, i + 1 + "");
            dataMap.put(1, jl.getJlJx());
            dataMap.put(2, jl.getJlXm());
            dataMap.put(3, jl.getClBh());
            dataMap.put(4, jl.getXySl() + "");
            dataMap.put(5, jl.getJlCx());
            dataMap.put(6, stringMap.get(jl.getZddm()));
            if (StringUtils.isNotBlank(jl.getKssj())) {
                dataMap.put(7, jl.getKssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(7, "-");
            }
            if (StringUtils.isNotBlank(jl.getJssj())) {
                dataMap.put(8, jl.getJssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(8, "-");
            }


            dataMap.put(9, jl.getSc() + "");
            dataMap.put(10, jl.getLcFy() + "");
            dataMap.put(11, jl.getXjje() + "");
            dataMap.put(12, StringUtils.equals(jl.getZfzt(), "10") ? "已支付" : "未支付");
            // 计算下支付方式
            if (jl.getCardje() != null && jl.getCardje() > 0) {
                dataMap.put(13, "充值卡");
            } else if (StringUtils.equals(jl.getZfzt(), "10")) {
                dataMap.put(13, "现金");
            }
            dataMap.put(14, jl.getZgXm());

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
        dataMap.put(11, list.stream().filter(jl -> jl.getXjje() != null).mapToInt(BizLcJl::getXjje).sum() + "");
        dataMap.put(8, "");
        dataMap.put(9, "");
        dataMap.put(10, list.size() == 0 ? "0" : list.stream().filter(bizLcJl -> bizLcJl.getLcFy() != null).mapToInt(BizLcJl::getLcFy).sum() + "");
        dataMap.put(12, "");
        dataMap.put(13, "");
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
    public ApiResponse<BizJlCz> saveCz(String id, int je, int sfje) {
//        RuntimeCheck.ifBlank(no, "请输入卡号");
        RuntimeCheck.ifTrue(je <= 0, "充值金额必须大于0");
        RuntimeCheck.ifTrue(sfje <= 0, "实付金额必须大于0 ");
        // 根据卡号查询教练信息
        BizLcWxjl wxjl = wxjlService.findById(id);
        //
        RuntimeCheck.ifNull(wxjl, "未找到教练信息");
        if (StringUtils.isBlank(wxjl.getCardNo())) {
            String maxNo = baseMapper.getMaxNo();
            String cardNo = genCardNo(Integer.parseInt(maxNo.replaceAll("VIP", "")));
            List<BizLcWxjl> eq = wxjlService.findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
            while (CollectionUtils.isNotEmpty(eq)) {
                int anInt = Integer.parseInt(cardNo.replaceAll("VIP", "")) + 1;
                cardNo = genCardNo(anInt);
                eq = wxjlService.findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
            }
            wxjl.setCardNo(cardNo);
            wxjl.setPwd(EncryptUtil.encryptUserPwd("123456"));
        }


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

    @Override
    public ApiResponse<String> savePay(String id) {
        int kfje = 0;
        int card = 0;
        int xjje = 0;
        double rate = 0;
        BizLcJl lcJl = findById(id);
        RuntimeCheck.ifNull(lcJl, "未找到练车记录");
        RuntimeCheck.ifTrue("10".equals(lcJl.getZfzt()), "此订单已经支付");
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
            str = "卡上余额" + (wxjl.getCardJe() - card) + "元";
        }
        // 查询当天所有非开放日记录
        SimpleCondition condition1 = new SimpleCondition(BizLcJl.class);
        condition1.eq(BizLcJl.InnerColumn.jlId, jls.get(0).getJlId());
        condition1.and().andCondition(" kssj like '%" + DateTime.now().toString("yyyy-MM-dd") + "%' or zfzt = '00'");
        condition1.and().andNotEqualTo(BizLcJl.InnerColumn.lcLx.name(), "30");
        condition1.eq(BizLcJl.InnerColumn.lcKm, jls.get(0).getLcKm());
        condition1.in(BizLcJl.InnerColumn.id, list);
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
        ApiResponse<String> kfDj = getKfDj(jls.get(0).getJlId());
        RuntimeCheck.ifFalse(kfDj.getCode() == 200, "数据异常 , 请联系开发人员处理");
        lcJl.setKfDj(Integer.parseInt(kfDj.getMessage().split("-")[0]));
        lcJl.setDksc(Integer.parseInt(kfDj.getMessage().split("-")[1]));
        return ApiResponse.success(lcJl);
    }

    @Override
    public ApiResponse<String> saveBatch(String ids) {
        String zf = getRequestParamterAsString("zf");
        RuntimeCheck.ifBlank(zf, "请选择支付方式");
        SysYh yh = getCurrentUser();
        RuntimeCheck.ifBlank(ids, "请选择要支付的订单");
        double rate = 0;
        List<String> list = Arrays.asList(ids.split(","));
        SimpleCondition condition = new SimpleCondition(BizLcJl.class);
        condition.in(BizLcJl.InnerColumn.id, list);
        condition.eq(BizLcJl.InnerColumn.zfzt, "00");
        List<BizLcJl> jls = findByCondition(condition);
        RuntimeCheck.ifEmpty(jls,
                "记录为空, 或已经支付过订单");
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
            if (cardJe > 0 && StringUtils.equals(zf, "2")) {
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
                        jl.setKfje(ye);
                        jl.setXjje(0);
                    } else {

                        jl.setXjje(Math.abs(jlye));
                        jl.setKfje(ye);
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
        SimpleCondition simpleCondition = new SimpleCondition(SysZdxm.class);
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
        jl.setJlCx(jls.stream().map(BizLcJl::getJlCx).filter(StringUtils::isNotBlank).filter(s -> !StringUtils.equals(s, "null")).collect(Collectors.joining(",")));
        jl.setClBh(jls.stream().map(BizLcJl::getClBh).filter(StringUtils::isNotBlank).collect(Collectors.joining(",")));
        jl.setYfJe(sum);
        jl.setXjje(sum);
        jl.setLcLx(jls.get(0).getLcLx());
        jl.setSc(jls.stream().mapToInt(BizLcJl::getSc).sum());
        jl.setKm(jls.get(0).getLcKm());
        jl.setLcKm(jls.get(0).getLcKm());
        jl.setJlJx(wxjl.getJlJx());
        jl.setJlXm(wxjl.getJlXm());
        jl.setKfje(jls.stream().filter(lcJl -> lcJl.getKfje() != null).mapToInt(BizLcJl::getKfje).sum());
        jl.setCardje(jls.stream().filter(lcJl -> lcJl.getCardje() != null).mapToInt(BizLcJl::getCardje).sum());
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
            str = "卡上余额" + wxjl.getCardJe() + "元";
        }
        jl.setBz(str);
        jl.setFdr(fdr);
        jl.setKfje(wxjl.getYe());
        jl.setCardje(wxjl.getCardJe());
        if (StringUtils.equals(jl.getLcLx(), "30")) {
            jl.setBz(jls.get(0).getXySl() + "人");
        }
        if (StringUtils.equals(jl.getLcLx(), "20")) {
            // 培优展示姓名加车型
            jl.setBz(jls.stream().map(BizLcJl::getXyXm).collect(Collectors.joining(",")));
        }
        return ApiResponse.success(jl);
    }


    @Override
    public ApiResponse<BizLcJl> payCNY(String id, String zf) {

        RuntimeCheck.ifBlank(id, "请选择要支付的订单");
        RuntimeCheck.ifBlank(zf, "请选择支付方式");
        String dj = getRequestParamterAsString("kfDj");
        RuntimeCheck.ifBlank(dj, "请上传当前开放日套餐单价");
        Integer kfDj = Integer.parseInt(dj);

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
                if (sfje < 0) {
                    jl.setCardje(cardJe);
                } else {
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
                lcJl.setBz("卡上余额" + wxjl.getCardJe() + "元");
            } else {
                lcJl.setBz("卡上余额" + wxjl.getCardJe() + "元");
            }
        } else if (StringUtils.equals(zf, "3")) {
            int wxjlYe = wxjl.getYe();
            String c = getRequestParamterAsString("c");
            RuntimeCheck.ifBlank(c, "请填写学员人数");
            String dksc = getRequestParamterAsString("dksc");
            RuntimeCheck.ifBlank(dksc, "请上传当前套餐的抵扣时长");
            // 抵扣支付 不够的按现金结算
            SimpleCondition simpleCondition = new SimpleCondition(SysZdxm.class);
            simpleCondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
            simpleCondition.like(SysZdxm.InnerColumn.zddm, "K2KF");
            simpleCondition.eq(SysZdxm.InnerColumn.by10, dksc);
            simpleCondition.eq(SysZdxm.InnerColumn.zdmc, kfDj + "");
            List<SysZdxm> sysZdxms = zdxmService.findByCondition(simpleCondition);
            String by4 = sysZdxms.get(0).getBy4();
            int xySl = Integer.parseInt(c);
            // 计算单个学员的保底费用
            int dkdj = Integer.parseInt(sysZdxms.get(0).getZdmc());
            int i = wxjl.getYe() / dkdj;
            RuntimeCheck.ifTrue(i < xySl, "抵扣人数不能大于开放日学员人数");
            // 总抵扣额度
            int kfje = xySl * dkdj;
            int czhje = kfje;
            // 教练总开放余额 减去开放日金额
            int je = kfje;
            // 计算出实际抵扣金额
            int sfje = (int) Math.ceil((500.0 / 60) * Double.parseDouble(dksc)) * xySl;
            int syje = sfje - sum;
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
                    jl.setDkdj(kfDj + "");
                    update(jl);
                }
            } else {
                // 不够部分按现金计算
                wxjl.setYe(wxjl.getYe() - kfje);
                wxjlService.update(wxjl);
                abs = Math.abs(syje);
                for (BizLcJl jl : jls) {
                    jl.setKfje(je);
                    if (sfje != 0) {
                        sfje = sfje - jl.getLcFy();
                        if (sfje > 0) {
                            // 余额充足
                            jl.setXjje(0);
                        } else {
                            jl.setXjje(Math.abs(sfje));
                            sfje = 0;
                        }
                    } else {
                        jl.setXjje(jl.getLcFy());
                    }
                    jl.setCardje(0);
                    jl.setYfJe(jl.getXjje());
                    jl.setZfzt("10");
                    jl.setPz(pz);
                    jl.setDkdj(kfDj + "");
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
            // 生成消费记录
            BizJlCz jlCz = new BizJlCz();
            jlCz.setJlId(wxjl.getId());
            jlCz.setSfje(0);
            jlCz.setId(genId());
            jlCz.setCzqje(wxjlYe);
            wxjl.setYe(Math.max((wxjlYe - czhje), 0));
            jlCz.setCzhje(Math.max((wxjlYe - czhje), 0));
            jlCz.setCjsj(DateUtils.getNowTime());
            jlCz.setJe(czhje);
            jlCz.setType("30");
            czMapper.insert(jlCz);
            wxjlService.update(wxjl);

            lcJl.setBz("应收现金" + abs + "元");

        }
        lcJl.setPz(pz);
        return ApiResponse.success(lcJl);
    }

    @Override
    public ApiResponse<String> revokeJl(String id) {
        RuntimeCheck.ifBlank(id, "请选择作废记录");
        BizLcJl jl = findById(id);
        jl.setZfzt("20");
        jl.setYfJe(0);
        jl.setXjje(0);
        jl.setCardje(0);
        jl.setKfje(0);
        update(jl);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> getCarEnd() {
        String clbhs = "";
        List<String> bhList = new ArrayList<>();
        List<BizLcJl> info = baseMapper.getAllInfo();
        Map<String, List<BizLcJl>> collect = info.stream().collect(Collectors.groupingBy(BizLcJl::getJlId));
        // 先过滤掉已经没有在训的教练名单 , 主要针对正在训练的教练
        List<BizLcJl> lcJls = info.stream().filter(lcJl -> StringUtils.isBlank(lcJl.getJssj())).collect(Collectors.toList());
//        Map<String, List<BizLcJl>> collect = lcJls.stream().collect(Collectors.groupingBy(BizLcJl::getJlId));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Set<String> set = lcJls.stream().map(BizLcJl::getJlId).collect(Collectors.toSet());
        SimpleCondition condition = new SimpleCondition(BizLcWxjl.class);
        condition.gt(BizLcWxjl.InnerColumn.ye, 0);
        condition.in(BizLcWxjl.InnerColumn.id, set);
        if (CollectionUtils.isEmpty(set)) {
            return ApiResponse.success("");
        }
        List<BizLcWxjl> list = wxjlService.findByCondition(condition);
        list.forEach(bizLcWxjl -> {
            List<BizLcJl> jls = collect.get(bizLcWxjl.getId());
            // 已经训练完的记录总费用
            int sum = jls.stream().filter(lcJl -> StringUtils.isNotBlank(lcJl.getJssj())).mapToInt(BizLcJl::getLcFy).sum();
            // 未训练完的当前费用
            List<BizLcJl> jlList = jls.stream().filter(lcJl -> StringUtils.isBlank(lcJl.getJssj())).collect(Collectors.toList());
            int sc = 0;
            for (BizLcJl lcJl : jlList) {
                String kssj = lcJl.getKssj();
                Date ks = null;
                try {
                    ks = dateFormat.parse(kssj);
                } catch (ParseException ignored) {
                }
                Date js = new Date();
                sc += (int) ((js.getTime() - ks.getTime()) / (60 * 1000));
            }
            int zfy = sum + (int) Math.ceil(sc * 8.33);
            int ye = bizLcWxjl.getYe();
            if ((ye - (5 * 8.33)) < zfy) {
                // 记录下所有的在训车辆 , 这些车辆即将超出费用
                List<String> strings = jlList.stream().map(BizLcJl::getClBh).collect(Collectors.toList());
                bhList.addAll(strings);
            }
        });
        bhList.sort(String::compareTo);
        clbhs = String.join(",", bhList);
        return ApiResponse.success(clbhs);
    }

    @Override
    public void exportXymx(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LimitedCondition condition = getQueryCondition();
        condition.setOrderByClause(" kssj desc");
        PageInfo<BizLcJl> info = findPage(page, condition);
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> titleMap = new HashMap<>();
        titleMap.put(0, "序号");
        titleMap.put(1, "教练员");
        titleMap.put(2, "编号");
        titleMap.put(3, "学员姓名");
        titleMap.put(4, "学员证件号码");
        titleMap.put(5, "学员联系方式");
        titleMap.put(6, "培训车型");
        titleMap.put(7, "套餐");
        data.add(titleMap);
        List<BizLcJl> list = info.getList();
        if (CollectionUtils.isNotEmpty(list)) {
            Set<String> zddms = list.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
            SimpleCondition djcondition = new SimpleCondition(SysZdxm.class);
            djcondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
            djcondition.in(SysZdxm.InnerColumn.zddm, zddms);
            List<SysZdxm> items = zdxmService.findByCondition(djcondition);
            // 根据套餐代码分组
            Map<String, SysZdxm> zdmap = items.stream().collect(Collectors.toMap(SysZdxm::getZddm, p -> p));

            int xh = 0;
            Map<String, List<BizLcJl>> map = list.stream().collect(Collectors.groupingBy(BizLcJl::getJlId));
            for (Map.Entry<String, List<BizLcJl>> entry : map.entrySet()) {
                List<BizLcJl> jls = entry.getValue();
                String jlxm = jls.get(0).getJlJx() + "_" + jls.get(0).getJlXm();
                int i = 1;
                for (BizLcJl jl : jls) {
                    String[] split = jl.getXyXm().split(",");
                    String[] dhs = jl.getXyDh().split(",");
                    String[] zjhms = jl.getXyZjhm().split(",");
                    for (int i1 = 0; i1 < split.length; i1++) {
                        xh++;
                        Map<Integer, String> dataMap = new HashMap<>();
                        dataMap.put(0, xh + "");
                        if (i == 1) {
                            dataMap.put(1, jlxm);
                        } else {
                            dataMap.put(1, "");
                        }
                        dataMap.put(2, i + "");
                        dataMap.put(3, split[i1].split("-")[0]);
                        if (i1 <= zjhms.length - 1) {
                            dataMap.put(4, zjhms[i1]);
                        } else {
                            dataMap.put(4, "");
                        }
                        if (i1 <= dhs.length - 1) {
                            dataMap.put(5, dhs[i1]);
                        } else {
                            dataMap.put(5, "");
                        }
                        dataMap.put(6, split[i1].split("-")[1]);
                        SysZdxm zdxm = zdmap.get(jl.getZddm());
                        dataMap.put(7, zdxm.getBy9() + "-" + zdxm.getBy3() + "元");
                        data.add(dataMap);
                        i++;
                    }
                }
            }
        }

        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(("培优明细").getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, "今日招生", data);

    }

    @Override
    public ApiResponse<List<String>> statisSec(String start, String end) {
        // 默认统计一周时间
        if (StringUtils.isBlank(start)) {
            start = DateTime.now().minusDays(6).toString("yyyy-MM-dd");
        }
        if (StringUtils.isBlank(end)) {
            end = DateTime.now().toString("yyyy-MM-dd");
        }
        Map<String, List<String>> mm = new HashMap<>();
        // 先算科二的吧
        SimpleCondition condition = new SimpleCondition(BizLcJl.class);
        condition.gte(BizLcJl.InnerColumn.kssj, start + " 00:00:00");
        condition.lte(BizLcJl.InnerColumn.kssj, end + " 23:59:59");
        condition.eq(BizLcJl.InnerColumn.lcKm, "2");
        condition.eq(BizLcJl.InnerColumn.zfzt, "10");
        condition.and().andCondition(" jssj is not null and jssj != ''");
        List<BizLcJl> jls = findByCondition(condition);
        // 根据时间分组
        Map<String, List<BizLcJl>> map = jls.stream().collect(Collectors.groupingBy(p -> p.getJssj().substring(0, 10)));
        List<String> between = DateUtils.getDayBetween(start, end);
        List<String> secList = new ArrayList<>();

        // 再计算科三的
        SimpleCondition k3conditon = new SimpleCondition(BizLcJl.class);
        k3conditon.gte(BizLcJl.InnerColumn.kssj, start + " 00:00:00");
        k3conditon.lte(BizLcJl.InnerColumn.kssj, end + " 23:59:59");
        k3conditon.eq(BizLcJl.InnerColumn.lcKm, "3");
        k3conditon.eq(BizLcJl.InnerColumn.zfzt, "10");
        k3conditon.and().andCondition(" jssj is not null and jssj != ''");
        List<BizLcJl> k3jls = findByCondition(k3conditon);
        Map<String, List<BizLcJl>> k3map = k3jls.stream().collect(Collectors.groupingBy(p -> p.getJssj().substring(0, 10)));


        // 再查询财务充值和返点的情况
        SimpleCondition czcondition = new SimpleCondition(BizJlCz.class);
        czcondition.gte(BizJlCz.InnerColumn.cjsj, start + " 00:00:00");
        czcondition.lte(BizJlCz.InnerColumn.cjsj, end + " 23:59:59");
        czcondition.eq(BizJlCz.InnerColumn.type, "10");
        List<BizJlCz> czs = czMapper.selectByExample(czcondition);
        Map<String, List<BizJlCz>> czmap = czs.stream().collect(Collectors.groupingBy(p -> p.getCjsj().substring(0, 10)));
        // 查询返点情况
        SimpleCondition fdcontion = new SimpleCondition(BizLcFds.class);
        fdcontion.gte(BizLcFds.InnerColumn.cjsj, start + " 00:00:00");
        fdcontion.lte(BizLcFds.InnerColumn.cjsj, end + " 23:59:59");
        List<BizLcFds> fds = fdsService.findByCondition(fdcontion);
        Map<String, List<BizLcFds>> fdmap = fds.stream().collect(Collectors.groupingBy(p -> p.getCjsj().substring(0, 10)));
        AtomicInteger k2js = new AtomicInteger();
        AtomicInteger k2py = new AtomicInteger();
        AtomicInteger k2kf = new AtomicInteger();
        AtomicInteger k2xj = new AtomicInteger();
        AtomicInteger k3js = new AtomicInteger();
        AtomicInteger k3py = new AtomicInteger();
        AtomicInteger k3ab = new AtomicInteger();
        AtomicInteger k3xj = new AtomicInteger();
        AtomicInteger cwcz = new AtomicInteger();
        AtomicInteger cwfd = new AtomicInteger();
        AtomicInteger cwzj = new AtomicInteger();
        // 根据当前时间段来区分每天的收费状况
        between.forEach(s -> {
            int zj = 0;
            // -----  科二  ----
            List<BizLcJl> lcJls = map.get(s);
            String data = s;
            if (CollectionUtils.isEmpty(lcJls)) {
                data += ",0,0,0,0";
            } else {
                // 先计算计时的总费用
                int js = lcJls.stream().filter(lcJl -> StringUtils.equals(lcJl.getLcLx(), "00")).mapToInt(BizLcJl::getXjje).sum();
                k2js.addAndGet(js);
                // 再计算培优的总费用
                int py = lcJls.stream().filter(lcjl -> StringUtils.equals(lcjl.getLcLx(), "20")).mapToInt(BizLcJl::getXjje).sum();
                k2py.addAndGet(py);
                // 再计算开放日总费用
                int kf = lcJls.stream().filter(lcJl -> StringUtils.equals(lcJl.getLcLx(), "30")).mapToInt(BizLcJl::getXjje).sum();
                k2kf.addAndGet(kf);
                // 再计算小计
                int sum = js + py + kf;
                k2xj.addAndGet(sum);
                zj += sum;
                data += "," + js + "," + py + "," + kf + "," + sum;
            }

            // --- 科三 ---
            List<BizLcJl> k3Jls = k3map.get(s);

            if (CollectionUtils.isEmpty(k3Jls)) {
                data += ",0,0,0,0";
            } else {
                // 先计算计时的总费用
                int js = k3Jls.stream().filter(lcJl -> StringUtils.equals(lcJl.getLcLx(), "00")).mapToInt(BizLcJl::getXjje).sum();
                k3js.addAndGet(js);
                // 再计算培优的总费用
                int py = k3Jls.stream().filter(lcjl -> StringUtils.equals(lcjl.getLcLx(), "20")).mapToInt(BizLcJl::getXjje).sum();
                k3py.addAndGet(py);
                // 再计算按把总费用
                int kf = k3Jls.stream().filter(lcJl -> StringUtils.equals(lcJl.getLcLx(), "10")).mapToInt(BizLcJl::getXjje).sum();
                k3ab.addAndGet(kf);
                // 再计算小计
                int sum = js + py + kf;
                k3xj.addAndGet(sum);
                zj += sum;
                data += "," + js + "," + py + "," + kf + "," + sum;
            }

            // --- 财务 充值返点 ---
            List<BizJlCz> bizJlCzs = czmap.get(s);
            List<BizLcFds> lcFds = fdmap.get(s);
            if (CollectionUtils.isEmpty(bizJlCzs)) {
                data += ",0";
                if (CollectionUtils.isEmpty(lcFds)) {
                    data += ",0";
                } else {
                    int sum = lcFds.stream().mapToInt(BizLcFds::getFdje).sum();
                    data += "," + sum;
                    cwfd.addAndGet(sum);
                    zj -= sum;
                }
            } else {
                int sum = bizJlCzs.stream().mapToInt(BizJlCz::getSfje).sum();
                cwcz.addAndGet(sum);
                data += "," + sum;
                zj += sum;
                if (CollectionUtils.isEmpty(lcFds)) {
                    data += ",0";
                } else {
                    int fdsum = lcFds.stream().mapToInt(BizLcFds::getFdje).sum();
                    zj -= fdsum;
                    cwfd.addAndGet(fdsum);
                    data += "," + fdsum;
                }
            }
            data += "," + zj;
            cwzj.addAndGet(zj);
            secList.add(data);
        });
        // 合计写在message里面吧
        String hj = k2js + "," + k2py + "," + k2kf + "," + (k2xj) + "," + k3js + "," + k3py + "," + k3ab + "," + k3xj + "," + cwcz + "," + cwfd + "," + cwzj;
        ApiResponse<List<String>> res = new ApiResponse<>();
        res.setResult(secList);
        res.setMessage(hj);
        return res;
    }

    @Override
    public ApiResponse<Map<String, Integer>> statisMain() {
        // 科目二 未支付数和已作废数
        SimpleCondition condition = new SimpleCondition(BizLcJl.class);
        condition.eq(BizLcJl.InnerColumn.lcKm, "2");
        condition.eq(BizLcJl.InnerColumn.zfzt, "00");
        int k2wzf = baseMapper.selectCountByExample(condition);
        condition = new SimpleCondition(BizLcJl.class);
        condition.eq(BizLcJl.InnerColumn.zfzt, "20");
        condition.eq(BizLcJl.InnerColumn.lcKm, "2");
        int k2yzf = baseMapper.selectCountByExample(condition);

        condition = new SimpleCondition(BizLcJl.class);
        condition.eq(BizLcJl.InnerColumn.lcKm, "3");
        condition.eq(BizLcJl.InnerColumn.zfzt, "00");
        int k3wzf = baseMapper.selectCountByExample(condition);

        condition = new SimpleCondition(BizLcJl.class);
        condition.eq(BizLcJl.InnerColumn.zfzt, "20");
        condition.eq(BizLcJl.InnerColumn.lcKm, "3");
        int k3yzf = baseMapper.selectCountByExample(condition);
        Map<String, Integer> m = new HashMap<>();
        m.put("k2wzf", k2wzf);
        m.put("k2yzf", k2yzf);
        m.put("k3wzf", k3wzf);
        m.put("k3yzf", k3yzf);
        return ApiResponse.success(m);
    }

    @Override
    public void exportSec(String start, String end, HttpServletRequest request, HttpServletResponse response) throws WriteException, IOException {
        ApiResponse<List<String>> sec = statisSec(start, end);
        // 每一行的数据
        List<String> result = sec.getResult();
        String message = sec.getMessage();
        WritableCellFormat cellFormat = new WritableCellFormat();
        cellFormat.setAlignment(Alignment.CENTRE);
        cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        cellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String((start + "-" + end + "收支统计").getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();


        WritableWorkbook workbook = Workbook.createWorkbook(out);
        WritableSheet sheet = workbook.createSheet("工作日志", 0);

        sheet.mergeCells(1, 0, 4, 0);
        sheet.mergeCells(5, 0, 8, 0);
        sheet.mergeCells(9, 0, 10, 0);
        sheet.mergeCells(11, 0, 11, 1);
        sheet.addCell(new Label(0, 0, "", cellFormat));
        sheet.addCell(new Label(1, 0, "科二", cellFormat));
        sheet.addCell(new Label(5, 0, "科三", cellFormat));
        sheet.addCell(new Label(9, 0, "财务", cellFormat));
        sheet.addCell(new Label(11, 0, "总计", cellFormat));
        sheet.addCell(new Label(0, 1, "日期", cellFormat));
        sheet.addCell(new Label(1, 1, "计时", cellFormat));
        sheet.addCell(new Label(2, 1, "培优", cellFormat));
        sheet.addCell(new Label(3, 1, "开放日", cellFormat));
        sheet.addCell(new Label(4, 1, "小计", cellFormat));
        sheet.addCell(new Label(5, 1, "计时", cellFormat));
        sheet.addCell(new Label(6, 1, "培优", cellFormat));
        sheet.addCell(new Label(7, 1, "按把", cellFormat));
        sheet.addCell(new Label(8, 1, "小计", cellFormat));
        sheet.addCell(new Label(9, 1, "充值卡", cellFormat));
        sheet.addCell(new Label(10, 1, "返点", cellFormat));
        for (int i = 0; i < result.size(); i++) {
            String s = result.get(i);
            String[] split = s.split(",");
            for (int i1 = 0; i1 < split.length; i1++) {
                sheet.addCell(new Label(i1, (i + 2), split[i1], cellFormat));
            }
        }
        String[] split = message.split(",");
        int i = result.size() + 2;
        sheet.addCell(new Label(0, i, "合计", cellFormat));
        sheet.addCell(new Label(1, i, split[0], cellFormat));
        sheet.addCell(new Label(2, i, split[1], cellFormat));
        sheet.addCell(new Label(3, i, split[2], cellFormat));
        sheet.addCell(new Label(4, i, split[3], cellFormat));
        sheet.addCell(new Label(5, i, split[4], cellFormat));
        sheet.addCell(new Label(6, i, split[5], cellFormat));
        sheet.addCell(new Label(7, i, split[6], cellFormat));
        sheet.addCell(new Label(8, i, split[7], cellFormat));
        sheet.addCell(new Label(9, i, split[8], cellFormat));
        sheet.addCell(new Label(10, i, split[9], cellFormat));
        sheet.addCell(new Label(11, i, split[10], cellFormat));
        workbook.write();
        workbook.close();
    }

    @Override
    public void exportKm3(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LimitedCondition condition = getQueryCondition();
        List<BizLcJl> jls = findByCondition(condition);
        List<Map<Integer, String>> dataList = new ArrayList<>();
        Map<Integer, String> titleMap = new HashMap<>();
        titleMap.put(0, "序号");
        titleMap.put(1, "驾校");
        titleMap.put(2, "教练员");
        titleMap.put(3, "科目");
        titleMap.put(4, "车号");
        titleMap.put(5, "人数");
        titleMap.put(6, "车型");
        titleMap.put(7, "类型");
        titleMap.put(8, "开始时间");
        titleMap.put(9, "结束时间");
        titleMap.put(10, "时长");
        titleMap.put(11, "应收");
        titleMap.put(12, "实收");
        titleMap.put(13, "订单状态");
        titleMap.put(14, "支付方式");
        titleMap.put(15, "安全员");
        titleMap.put(16, "备注");
        dataList.add(titleMap);
        String cx = getRequestParamterAsString("cx");

        List<SysZdxm> zdxms = zdxmService.findEq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        Map<String, String> map = zdxms.stream().collect(Collectors.toMap(SysZdxm::getZddm, p -> p.getBy9()));

        for (int i = 0; i < jls.size(); i++) {
            BizLcJl jl = jls.get(i);
            Map<Integer, String> dataMap = new HashMap<>();
            dataMap.put(0, (i + 1) + "");
            dataMap.put(1, jl.getJlJx());
            dataMap.put(2, jl.getJlXm());
            dataMap.put(3, StringUtils.equals(jl.getLcKm(), "2") ? "科目二" : "科目三");
            dataMap.put(4, jl.getClBh());
            dataMap.put(5, jl.getXySl() + "");
            dataMap.put(6, jl.getJlCx());
            dataMap.put(7, map.get(jl.getZddm()));
            dataMap.put(8, jl.getKssj());
            dataMap.put(9, jl.getJssj().substring(12, 17));
            dataMap.put(10, jl.getSc() + "");
            dataMap.put(11, jl.getLcFy() + "");
            dataMap.put(12, jl.getXjje() + "");
            dataMap.put(13, StringUtils.equals(jl.getZfzt(), "00") ? "未支付" : "已支付");
            if (jl.getKfje() != null && jl.getKfje() > 0) {
                dataMap.put(14, "抵扣支付");
            } else if (jl.getCardje() != null && jl.getCardje() > 0) {
                dataMap.put(14, "充值卡");
            } else {
                dataMap.put(14, "现金");
            }
            dataMap.put(15, jl.getZgXm());
            dataMap.put(16, jl.getBz());
            dataList.add(dataMap);
        }
        Map<Integer, String> hjMap = new HashMap<>();
        hjMap.put(0, "合计");
        hjMap.put(1, "");
        hjMap.put(2, "");
        hjMap.put(3, "");
        hjMap.put(4, "");
        hjMap.put(5, "");
        hjMap.put(6, "");
        hjMap.put(7, "");
        hjMap.put(8, "");
        hjMap.put(9, "");
        hjMap.put(10, "");
        hjMap.put(11, CollectionUtils.isEmpty(jls) ? "0" : (jls.stream().mapToInt(BizLcJl::getLcFy).sum() + ""));
        hjMap.put(12, CollectionUtils.isEmpty(jls) ? "0" : (jls.stream().mapToInt(BizLcJl::getXjje).sum() + ""));
        hjMap.put(13, "");
        hjMap.put(14, "");
        hjMap.put(15, "");
        hjMap.put(16, "");
        dataList.add(hjMap);


        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String("明细".getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, "明细", dataList);
    }

    @Override
    public void pagerExcelAll(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String lx = getRequestParamterAsString("lx");
        String time = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        String fileName = time + "-明细统计";
        LimitedCondition condition = getQueryCondition();
        if (StringUtils.isNotBlank(lx)) {
            condition.eq(BizLcJl.InnerColumn.jlLx, lx);
        }
        String orgcode = getRequestParamterAsString("orgcode");
        // 根据队号查出教练员id
        List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.dh, orgcode);
        if(CollectionUtils.isNotEmpty(wxjls)) {
            Set<String> set = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toSet());
            condition.in(BizLcJl.InnerColumn.jlId, set);
        }else {
            condition.eq(BizLcJl.InnerColumn.id, "1");
        }
        condition.and().andCondition(" jssj is not null and jssj != ''");
        PageInfo<BizLcJl> info = findPage(page, condition);
        List<BizLcJl> list = info.getList();
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "#");
        map.put(2, "驾校");
        map.put(3, "教练员");
        map.put(1, "科目");
        map.put(6, "人数");
        map.put(4, "车型");
        map.put(5, "类型");
        map.put(7, "开始时间");
        map.put(8, "结束时间");
        map.put(9, "时长");
        map.put(10, "应收");
        map.put(11, "实收");
        map.put(12, "支付方式");
        map.put(13, "安全员");
        data.add(map);
        Set<String> collect = list.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
        SimpleCondition condition1 = new SimpleCondition(SysZdxm.class);
        condition1.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
        condition1.in(SysZdxm.InnerColumn.zddm, collect);
        List<SysZdxm> zdxms = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(collect)) {
            zdxms = zdxmService.findByCondition(condition1);
        }

        Map<String, String> stringMap = zdxms.stream().collect(Collectors.toMap(SysZdxm::getZddm, SysZdxm::getBy9));
        for (int i = 0; i < list.size(); i++) {
            BizLcJl jl = list.get(i);
            Map<Integer, String> dataMap = new HashMap<>();
            dataMap.put(0, i + 1 + "");
            dataMap.put(2, jl.getJlJx());
            dataMap.put(3, jl.getJlXm());
            dataMap.put(1, StringUtils.equals(jl.getLcKm(), "2") ? "科目二" : "科目三");
            dataMap.put(6, jl.getXySl() + "");
            dataMap.put(4, jl.getJlCx());
            dataMap.put(5, stringMap.get(jl.getZddm()));
            if (StringUtils.isNotBlank(jl.getKssj())) {
                dataMap.put(7, jl.getKssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(7, "-");
            }
            if (StringUtils.isNotBlank(jl.getJssj())) {
                dataMap.put(8, jl.getJssj().substring(0, jl.getJssj().length() - 3));
            } else {
                dataMap.put(8, "-");
            }


            dataMap.put(9, jl.getSc() + "");
            dataMap.put(10, jl.getLcFy() + "");
            dataMap.put(11, jl.getXjje() + "");
            // 计算下支付方式
            if (jl.getCardje() != null && jl.getCardje() > 0) {
                dataMap.put(12, "充值卡");
            } else if (StringUtils.equals(jl.getZfzt(), "10")) {
                dataMap.put(12, "现金");
            }
            dataMap.put(13, jl.getZgXm());

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
        dataMap.put(11, list.stream().filter(jl -> jl.getXjje() != null).mapToInt(BizLcJl::getXjje).sum() + "");
        dataMap.put(8, "");
        dataMap.put(10, "");
        dataMap.put(9, "");
        dataMap.put(10, list.size() == 0 ? "0" : list.stream().filter(bizLcJl -> bizLcJl.getLcFy() != null).mapToInt(BizLcJl::getLcFy).sum() + "");
        dataMap.put(12, "");
        dataMap.put(13, "");
        data.add(dataMap);

        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        ServletOutputStream outputStream = response.getOutputStream();
        ExcelUtil.createSheet(outputStream, "明细统计", data);

    }

    @Override
    public ApiResponse<String> getKfDj(String jlId) {
        RuntimeCheck.ifBlank(jlId, "请上传教练id");
        SimpleCondition condition = new SimpleCondition(BizLcJl.class);
        condition.eq(BizLcJl.InnerColumn.jlId, jlId);
        condition.setOrderByClause(" id desc ");
        condition.like(BizLcJl.InnerColumn.zddm, "K2KF");
        List<BizLcJl> lcJls = findByCondition(condition);
        if (CollectionUtils.isEmpty(lcJls)) {
            return ApiResponse.success("0-0");
        }
        String zddm = lcJls.get(0).getZddm();
        List<SysZdxm> zdxms = zdxmService.findEq(SysZdxm.InnerColumn.zddm, zddm);
        if (CollectionUtils.isEmpty(zdxms)) {
            return ApiResponse.success("0-0");
        }
        return ApiResponse.success(zdxms.get(0).getZdmc() + "-" + zdxms.get(0).getBy10());
    }

    @Override
    public void exportMx(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LimitedCondition condition = getQueryCondition();
        condition.setOrderByClause(" kssj asc");
        PageInfo<BizLcJl> info = findPage(page, condition);
        List<Map<Integer, String>> data = new ArrayList<>();
        Map<Integer, String> titleMap = new HashMap<>();
        titleMap.put(0, "序号");
        titleMap.put(1, "日期");
        titleMap.put(2, "姓名");
        titleMap.put(3, "身份证号码");
        titleMap.put(4, "电话号码");
        titleMap.put(5, "车型");
        titleMap.put(6, "陪练员");
        titleMap.put(7, "陪考员");
        titleMap.put(8, "驾校名称");
        titleMap.put(9, "是否合格");
        titleMap.put(10, "培优费");
        data.add(titleMap);
        List<BizLcJl> list = info.getList();
        if (CollectionUtils.isNotEmpty(list)) {
            Set<String> zddms = list.stream().map(BizLcJl::getZddm).collect(Collectors.toSet());
            SimpleCondition djcondition = new SimpleCondition(SysZdxm.class);
            djcondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
            djcondition.in(SysZdxm.InnerColumn.zddm, zddms);
            List<SysZdxm> items = zdxmService.findByCondition(djcondition);
            // 根据套餐代码分组
            Map<String, SysZdxm> zdmap = items.stream().collect(Collectors.toMap(SysZdxm::getZddm, p -> p));

            int xh = 0;
            for (int i = 0; i < list.size(); i++) {
                BizLcJl jl = list.get(i);
                String[] split = jl.getXyXm().split(",");
                String[] dhs = jl.getXyDh().split(",");
                String[] zjhms = jl.getXyZjhm().split(",");
                for (int i1 = 0; i1 < split.length; i1++) {
                    Map<Integer, String> dataMap = new HashMap<>();
                    xh++;
                    dataMap.put(0, xh + "");
                    dataMap.put(1, jl.getKssj().substring(0, 10).replaceAll("-", "."));
                    dataMap.put(2, split[i1].split("-")[0]);
                    dataMap.put(3, zjhms[i1]);
                    if (dhs.length > i1) {
                        dataMap.put(4, dhs[i1]);
                    } else {
                        dataMap.put(4, "");
                    }
                    dataMap.put(5, split[i1].split("-")[1]);
                    dataMap.put(6, "");
                    dataMap.put(7, "");
                    dataMap.put(8, jl.getJlXm());
                    dataMap.put(9, "");
                    SysZdxm zdxm = zdmap.get(jl.getZddm());
                    dataMap.put(10, zdxm.getBy9() + "-" + zdxm.getBy3() + "元");
                    data.add(dataMap);
                }
            }
        }
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(("培优明细").getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        ExcelUtil.createSheet(out, "今日招生", data);
    }

    @Override
    public ApiResponse<List<Map<String, Object>>> newJxtj() {
        List<Map<String, Object>> result = new ArrayList<>();
        // 参数处理
        String tjsj = getRequestParamterAsString("tjsj");
        if (StringUtils.isEmpty(tjsj)) {
            tjsj = DateUtils.getToday() + " 00:00:00," + DateUtils.getToday() + " 23:59:59";
        }
        String[] sj = tjsj.split(",");
        String kssj = sj[0];
        String jssj = sj[1];

        String lx = getRequestParamterAsString("lx");

        SimpleCondition cond = new SimpleCondition(BizLcJl.class);
        if (StringUtils.isNotBlank(lx)) {
            cond.eq(BizLcJl.InnerColumn.jlLx, lx);
        }
        cond.eq(BizLcJl.InnerColumn.zfzt, "10");
        cond.gte(BizLcJl.InnerColumn.kssj, kssj);
        cond.lte(BizLcJl.InnerColumn.jssj, jssj);
        //  新驾校统计接口需要根据驾校分组
        // 1. 根据时间查询出所有的练车记录
        List<BizLcJl> lcJls = findByCondition(cond);
        if (CollectionUtils.isEmpty(lcJls)) {
            return ApiResponse.success(result);
        }
        // 2. 根据驾校开始分组
        Map<String, List<BizLcJl>> map = lcJls.stream().collect(Collectors.groupingBy(BizLcJl::getJlJx));

        // 3.根据凭证号查询出返点内容
        Set<String> set = lcJls.stream().map(BizLcJl::getPz).collect(Collectors.toSet());
        List<BizLcFd> fds = fdService.findByIds(set);
        int k2jsZj = 0;
        int k2YfdZj = 0;
        int k2DfdZj = 0;
        int k2pyZj = 0;
        int k2pyYfdZj = 0;
        int k2pyDfdZj = 0;
        int k2KfZj = 0;
        int k2KfYfdZj = 0;
        int k2KfDfdZJ = 0;
        int k2XjZj = 0;
        int k2YfdXjZj = 0;
        int k2DfdXjZj = 0;

        int k3jsZj = 0;
        int k3YfdZj = 0;
        int k3DfdZj = 0;
        int k3pyZj = 0;
        int k3pyYfdZj = 0;
        int k3pyDfdZj = 0;
        int k3AbZj = 0;
        int k3AbYfdZj = 0;
        int k3AbDfdZj = 0;
        int k3XjZj = 0;
        int k3YfdXjZj = 0;
        int k3DfdXjZj = 0;
        int zjZj = 0;
        int dfdZjZj = 0;
        int yfdZjZj = 0;

        for (Map.Entry<String, List<BizLcJl>> entry : map.entrySet()) {
            Map<String, Object> dataMap = new HashMap<>();
            List<BizLcJl> jls = entry.getValue();
            // 计算 科二计时培训收入
            int k2js = jls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcLx(), "00") && StringUtils.equals(bizLcJl.getLcKm(), "2")).mapToInt(BizLcJl::getXjje).sum();
            k2jsZj += k2js;
            // 计算 科二计时培训已返点金额 ①拿到科二计时培训 Pz
            Set<String> pzs = jls.stream().filter(jl -> StringUtils.equals(jl.getLcKm(), "2") && StringUtils.equals(jl.getLcLx(), "00")).map(BizLcJl::getPz).collect(Collectors.toSet());
            // ② 根据凭证过滤记录 并计算已返点金额
            int k2Yfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isNotBlank(fd.getQrsj()) && pzs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k2YfdZj += k2Yfd;
            // 计算科二计时待返点金额
            int k2Dfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isBlank(fd.getQrsj()) && pzs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k2DfdZj += k2Dfd;
            // 计算科二培优收入
            int k2py = jls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcLx(), "20") && StringUtils.equals(bizLcJl.getLcKm(), "2")).mapToInt(BizLcJl::getXjje).sum();
            k2pyZj += k2py;
            // 计算科二培优已返点
            Set<String> pys = jls.stream().filter(jl -> StringUtils.equals(jl.getLcKm(), "2") && StringUtils.equals(jl.getLcLx(), "20")).map(BizLcJl::getPz).collect(Collectors.toSet());
            int k2pyYfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isNotBlank(fd.getQrsj()) && pys.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k2pyYfdZj += k2pyYfd;
            // 计算科二培优待返点
            int k2pyDfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isBlank(fd.getQrsj()) && pys.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k2pyDfdZj += k2pyDfd;
            // 计算科二开放日收入
            int k2Kf = jls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcLx(), "30") && StringUtils.equals(bizLcJl.getLcKm(), "2")).mapToInt(BizLcJl::getXjje).sum();
            k2KfZj += k2Kf;
            // 计算科二开放日已返点金额
            Set<String> kfs = jls.stream().filter(jl -> StringUtils.equals(jl.getLcKm(), "2") && StringUtils.equals(jl.getLcLx(), "30")).map(BizLcJl::getPz).collect(Collectors.toSet());
            int k2KfYfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isNotBlank(fd.getQrsj()) && kfs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k2KfYfdZj += k2KfYfd;
            // 计算科二开放日待返点金额
            int k2KfDfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isBlank(fd.getQrsj()) && kfs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k2KfDfdZJ += k2KfDfd;
            // 科目二小计
            int k2Xj = k2js + k2py + k2Kf;
            k2XjZj += k2Xj;
            int k2YfdXj = k2Yfd + k2KfYfd + k2pyYfd;
            k2YfdXjZj += k2YfdXj;
            int k2DfdXj = k2Dfd + k2KfDfd + k2pyDfd;
            k2DfdXjZj += k2DfdXj;

            // --------------------计算科三-----------------------------
            // 计算 科三计时培训收入
            int k3js = jls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcLx(), "00") && StringUtils.equals(bizLcJl.getLcKm(), "3")).mapToInt(BizLcJl::getXjje).sum();
            k3jsZj += k3js;
            // 计算 科三计时培训已返点金额 ①拿到科三计时培训 Pz
            Set<String> k3pzs = jls.stream().filter(jl -> StringUtils.equals(jl.getLcKm(), "3") && StringUtils.equals(jl.getLcLx(), "00")).map(BizLcJl::getPz).collect(Collectors.toSet());
            // ② 根据凭证过滤记录 并计算已返点金额
            int k3Yfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isNotBlank(fd.getQrsj()) && k3pzs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k3YfdZj += k3Yfd;
            // 计算科三计时待返点金额
            int k3Dfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isBlank(fd.getQrsj()) && k3pzs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k3DfdZj += k3Dfd;
            // 计算科三培优收入
            int k3py = jls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcLx(), "20") && StringUtils.equals(bizLcJl.getLcKm(), "3")).mapToInt(BizLcJl::getXjje).sum();
            k3pyZj += k3py;
            // 计算科二培优已返点
            Set<String> k3pys = jls.stream().filter(jl -> StringUtils.equals(jl.getLcKm(), "3") && StringUtils.equals(jl.getLcLx(), "20")).map(BizLcJl::getPz).collect(Collectors.toSet());
            int k3pyYfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isNotBlank(fd.getQrsj()) && k3pys.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k3pyYfdZj += k3pyYfd;
            // 计算科三培优待返点
            int k3pyDfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isBlank(fd.getQrsj()) && k3pys.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k3pyDfdZj += k3pyDfd;
            // 计算科三按把收入
            int k3Ab = jls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcLx(), "10") && StringUtils.equals(bizLcJl.getLcKm(), "3")).mapToInt(BizLcJl::getXjje).sum();
            k3AbZj += k3Ab;
            // 计算科三按把已返点金额
            Set<String> k3abs = jls.stream().filter(jl -> StringUtils.equals(jl.getLcKm(), "3") && StringUtils.equals(jl.getLcLx(), "10")).map(BizLcJl::getPz).collect(Collectors.toSet());
            int k3AbYfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isNotBlank(fd.getQrsj()) && k3abs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k3AbYfdZj += k3AbYfd;
            // 计算科三按把待返点金额
            int k3AbDfd = CollectionUtils.isEmpty(fds) ? 0 : fds.stream().filter(fd -> StringUtils.isBlank(fd.getQrsj()) && k3abs.contains(fd.getId())).mapToInt(BizLcFd::getFdje).sum();
            k3AbDfdZj += k3AbDfd;
            // 科目三小计
            int k3Xj = k3js + k3py + k3Ab;
            k3XjZj += k3Xj;
            int k3YfdXj = k3Yfd + k3AbYfd + k3pyYfd;
            k3YfdXjZj += k3YfdXj;
            int k3DfdXj = k3Dfd + k3AbDfd + k3pyDfd;
            k3DfdXjZj += k3DfdXj;
            // 总计
            int zj = k2Xj + k3Xj;
            zjZj += zj;
            int yfdZj = k2YfdXj + k3YfdXj;
            yfdZjZj += yfdZj;
            int dfdZj = k2DfdXj + k3DfdXj;
            dfdZjZj += dfdZj;
            dataMap.put("jx", entry.getKey());
            dataMap.put("k2js", k2js == 0 ? "-" : k2js);
            dataMap.put("k2Yfd", k2Yfd == 0 ? "-" : k2Yfd);
            dataMap.put("k2Dfd", k2Dfd == 0 ? "-" : k2Dfd);
            dataMap.put("k2py", k2py == 0 ? "-" : k2py);
            dataMap.put("k2pyYfd", k2pyYfd == 0 ? "-" : k2pyYfd);
            dataMap.put("k2pyDfd", k2pyDfd == 0 ? "-" : k2pyDfd);
            dataMap.put("k2Kf", k2Kf == 0 ? "-" : k2Kf);
            dataMap.put("k2KfYfd", k2KfYfd == 0 ? "-" : k2KfYfd);
            dataMap.put("k2KfDfd", k2KfDfd == 0 ? "-" : k2KfDfd);
            dataMap.put("k2Xj", k2Xj == 0 ? "-" : k2Xj);
            dataMap.put("k2YfdXj", k2YfdXj == 0 ? "-" : k2YfdXj);
            dataMap.put("k2DfdXj", k2DfdXj == 0 ? "-" : k2DfdXj);

            dataMap.put("k3js", k3js == 0 ? "-" : k3js);
            dataMap.put("k3Yfd", k3Yfd == 0 ? "-" : k3Yfd);
            dataMap.put("k3Dfd", k3Dfd == 0 ? "-" : k3Dfd);
            dataMap.put("k3py", k3py == 0 ? "-" : k3py);
            dataMap.put("k3pyYfd", k3pyYfd == 0 ? "-" : k3pyYfd);
            dataMap.put("k3pyDfd", k3pyDfd == 0 ? "-" : k3pyDfd);
            dataMap.put("k3Ab", k3Ab == 0 ? "-" : k3Ab);
            dataMap.put("k3AbYfd", k3AbYfd == 0 ? "-" : k3AbYfd);
            dataMap.put("k3AbDfd", k3AbDfd == 0 ? "-" : k3AbDfd);
            dataMap.put("k3Xj", k3Xj == 0 ? "-" : k3Xj);
            dataMap.put("k3YfdXj", k3YfdXj == 0 ? "-" : k3YfdXj);
            dataMap.put("k3DfdXj", k3DfdXj == 0 ? "-" : k3DfdXj);

            dataMap.put("zj", zj);
            dataMap.put("yfdZj", yfdZj == 0 ? "-" : yfdZj);
            dataMap.put("dfdZj", dfdZj == 0 ? "-" : dfdZj);

            result.add(dataMap);
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("jx", "合计");
        dataMap.put("k2js", k2jsZj == 0 ? "-" : String.valueOf(k2jsZj));
        dataMap.put("k2Yfd", k2YfdZj == 0 ? "-" : String.valueOf(k2YfdZj));
        dataMap.put("k2Dfd", k2DfdZj == 0 ? "-" : String.valueOf(k2DfdZj));
        dataMap.put("k2py", k2pyZj == 0 ? "-" : String.valueOf(k2pyZj));
        dataMap.put("k2pyYfd", k2pyYfdZj == 0 ? "-" : String.valueOf(k2pyYfdZj));
        dataMap.put("k2pyDfd", k2pyDfdZj == 0 ? "-" : String.valueOf(k2pyDfdZj));
        dataMap.put("k2Kf", k2KfZj == 0 ? "-" : String.valueOf(k2KfZj));
        dataMap.put("k2KfYfd", k2KfYfdZj == 0 ? "-" : String.valueOf(k2KfYfdZj));
        dataMap.put("k2KfDfd", k2KfDfdZJ == 0 ? "-" : String.valueOf(k2KfDfdZJ));
        dataMap.put("k2Xj", k2XjZj == 0 ? "-" : String.valueOf(k2XjZj));
        dataMap.put("k2YfdXj", k2YfdXjZj == 0 ? "-" : String.valueOf(k2YfdXjZj));
        dataMap.put("k2DfdXj", k2DfdXjZj == 0 ? "-" : String.valueOf(k2DfdXjZj));

        dataMap.put("k3js", k3jsZj == 0 ? "-" : String.valueOf(k3jsZj));
        dataMap.put("k3Yfd", k3YfdZj == 0 ? "-" : String.valueOf(k3YfdZj));
        dataMap.put("k3Dfd", k3DfdZj == 0 ? "-" : String.valueOf(k3DfdZj));
        dataMap.put("k3py", k3pyZj == 0 ? "-" : String.valueOf(k3pyZj));
        dataMap.put("k3pyYfd", k3pyYfdZj == 0 ? "-" : String.valueOf(k3pyYfdZj));
        dataMap.put("k3pyDfd", k3pyDfdZj == 0 ? "-" : String.valueOf(k3pyDfdZj));
        dataMap.put("k3Ab", k3AbZj == 0 ? "-" : String.valueOf(k3AbZj));
        dataMap.put("k3AbYfd", k3AbYfdZj == 0 ? "-" : String.valueOf(k3AbYfdZj));
        dataMap.put("k3AbDfd", k3AbDfdZj == 0 ? "-" : String.valueOf(k3AbDfdZj));
        dataMap.put("k3Xj", k3XjZj == 0 ? "-" : String.valueOf(k3XjZj));
        dataMap.put("k3YfdXj", k3YfdXjZj == 0 ? "-" : String.valueOf(k3YfdXjZj));
        dataMap.put("k3DfdXj", k3DfdXjZj == 0 ? "-" : String.valueOf(k3DfdXjZj));

        dataMap.put("zj", String.valueOf(zjZj));
        dataMap.put("yfdZj", yfdZjZj == 0 ? "-" : String.valueOf(yfdZjZj));
        dataMap.put("dfdZj", dfdZjZj == 0 ? "-" : String.valueOf(dfdZjZj));
        result.add(dataMap);
        return ApiResponse.success(result);

    }

    @Override
    public void downloadNewJxtj(HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {

        ApiResponse<List<Map<String, Object>>> jxtj = newJxtj();
        List<Map<String, Object>> mapList = jxtj.getResult();
        // 参数处理
        String tjsj = getRequestParamterAsString("tjsj");
        if (StringUtils.isEmpty(tjsj)) {
            tjsj = DateUtils.getToday() + " 00:00:00," + DateUtils.getToday() + " 23:59:59";
        }
        String[] sj = tjsj.split(",");
        org.joda.time.format.DateTimeFormatter pattern = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime time = DateTime.parse(sj[0], pattern);
        int year = time.getYear();
        int mon = time.getMonthOfYear();
        int day = time.getDayOfMonth();
        DateTime parse = DateTime.parse(sj[1], pattern);
        String timeString = year + "年" + mon + "月" + day + "日-" + parse.getYear() + "年" + parse.getMonthOfYear() + "月" + parse.getDayOfMonth() + "日";
        String fileName = "知音考场练车统计（按驾校）";

        WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(Alignment.CENTRE);
        cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        cellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();


        WritableFont df = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
        WritableCellFormat dfcellFormat = new WritableCellFormat(df);
        dfcellFormat.setAlignment(Alignment.CENTRE);
        dfcellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        dfcellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        WritableFont yf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
        WritableCellFormat yfcellFormat = new WritableCellFormat(yf);
        yfcellFormat.setAlignment(Alignment.CENTRE);
        yfcellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        yfcellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        WritableWorkbook workbook = Workbook.createWorkbook(out);
        WritableSheet sheet = workbook.createSheet("驾校统计", 0);

        sheet.mergeCells(0, 0, 27, 0);
        sheet.mergeCells(0, 1, 27, 1);
        sheet.mergeCells(0, 2, 0, 3);
        sheet.mergeCells(1, 2, 12, 2);
        sheet.mergeCells(13, 2, 24, 2);
        sheet.mergeCells(25, 2, 25, 3);
        sheet.mergeCells(26, 2, 26, 3);
        sheet.mergeCells(27, 2, 27, 3);
//        sheet.mergeCells(13, 0, 14, 0);
//        sheet.mergeCells(15, 0, 15, 1);
        sheet.addCell(new Label(0, 0, "知音考场练车统计（按驾校）", cellFormat));
        sheet.addCell(new Label(0, 1, "时间段：" + timeString, cellFormat));
        sheet.addCell(new Label(0, 2, "驾校", cellFormat));
        sheet.addCell(new Label(1, 2, "科二", cellFormat));
        sheet.addCell(new Label(13, 2, "科三", cellFormat));
        sheet.addCell(new Label(25, 2, "总计", cellFormat));
        sheet.addCell(new Label(26, 2, "已返", yfcellFormat));
        sheet.addCell(new Label(27, 2, "待返", dfcellFormat));
        sheet.addCell(new Label(1, 3, "计时", cellFormat));
        sheet.addCell(new Label(2, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(3, 3, "待返", dfcellFormat));
        sheet.addCell(new Label(4, 3, "培优", cellFormat));
        sheet.addCell(new Label(5, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(6, 3, "待返", dfcellFormat));
        sheet.addCell(new Label(7, 3, "开放日", cellFormat));
        sheet.addCell(new Label(8, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(9, 3, "待返", dfcellFormat));
        sheet.addCell(new Label(10, 3, "小计2", cellFormat));
        sheet.addCell(new Label(11, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(12, 3, "待返", dfcellFormat));

        sheet.addCell(new Label(13, 3, "计时", cellFormat));
        sheet.addCell(new Label(14, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(15, 3, "待返", dfcellFormat));
        sheet.addCell(new Label(16, 3, "培优", cellFormat));
        sheet.addCell(new Label(17, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(18, 3, "待返", dfcellFormat));
        sheet.addCell(new Label(19, 3, "按把", cellFormat));
        sheet.addCell(new Label(20, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(21, 3, "待返", dfcellFormat));
        sheet.addCell(new Label(22, 3, "小计3", cellFormat));
        sheet.addCell(new Label(23, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(24, 3, "待返", dfcellFormat));

        sheet.addCell(new Label(25, 3, "总计", cellFormat));
        sheet.addCell(new Label(26, 3, "已返", yfcellFormat));
        sheet.addCell(new Label(27, 3, "待返", dfcellFormat));

        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            sheet.addCell(new Label(0, i + 4, map.get("jx") + "", cellFormat));
            sheet.addCell(new Label(1, i + 4, map.get("k2js") + "", cellFormat));
            sheet.addCell(new Label(2, i + 4, map.get("k2Yfd") + "", yfcellFormat));
            sheet.addCell(new Label(3, i + 4, map.get("k2Dfd") + "", dfcellFormat));
            sheet.addCell(new Label(4, i + 4, map.get("k2py") + "", cellFormat));
            sheet.addCell(new Label(5, i + 4, map.get("k2pyYfd") + "", yfcellFormat));
            sheet.addCell(new Label(6, i + 4, map.get("k2pyDfd") + "", dfcellFormat));
            sheet.addCell(new Label(7, i + 4, map.get("k2Kf") + "", cellFormat));
            sheet.addCell(new Label(8, i + 4, map.get("k2KfYfd") + "", yfcellFormat));
            sheet.addCell(new Label(9, i + 4, map.get("k2KfDfd") + "", dfcellFormat));
            sheet.addCell(new Label(10, i + 4, map.get("k2Xj") + "", cellFormat));
            sheet.addCell(new Label(11, i + 4, map.get("k2YfdXj") + "", yfcellFormat));
            sheet.addCell(new Label(12, i + 4, map.get("k2DfdXj") + "", dfcellFormat));

            sheet.addCell(new Label(13, i + 4, map.get("k3js") + "", cellFormat));
            sheet.addCell(new Label(14, i + 4, map.get("k3Yfd") + "", yfcellFormat));
            sheet.addCell(new Label(15, i + 4, map.get("k3Dfd") + "", dfcellFormat));
            sheet.addCell(new Label(16, i + 4, map.get("k3py") + "", cellFormat));
            sheet.addCell(new Label(17, i + 4, map.get("k3pyYfd") + "", yfcellFormat));
            sheet.addCell(new Label(18, i + 4, map.get("k3pyDfd") + "", dfcellFormat));
            sheet.addCell(new Label(19, i + 4, map.get("k3Ab") + "", cellFormat));
            sheet.addCell(new Label(20, i + 4, map.get("k3AbYfd") + "", yfcellFormat));
            sheet.addCell(new Label(21, i + 4, map.get("k3AbDfd") + "", dfcellFormat));
            sheet.addCell(new Label(22, i + 4, map.get("k3Xj") + "", cellFormat));
            sheet.addCell(new Label(23, i + 4, map.get("k3YfdXj") + "", yfcellFormat));
            sheet.addCell(new Label(24, i + 4, map.get("k3DfdXj") + "", dfcellFormat));

            sheet.addCell(new Label(25, i + 4, map.get("zj") + "", cellFormat));
            sheet.addCell(new Label(26, i + 4, map.get("yfdZj") + "", yfcellFormat));
            sheet.addCell(new Label(27, i + 4, map.get("dfdZj") + "", dfcellFormat));
        }
        workbook.write();
        workbook.close();

    }

    @Override
    public void exportLocalSchool(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {
//        kssjInRange
        String range = getRequestParamterAsString("kssjInRange");
        String[] split = range.split(",");
        String[] start = split[0].substring(0, 10).split("-");
        String[] end = split[1].substring(0, 10).split("-");
        String timeString = start[0] + "年" + start[1] + "月" + start[2] + "日-" + end[0] + "年" + end[1] + "月" + end[2] + "日";
        String time = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        String fileName = time + "-明细统计";
        LimitedCondition condition = getQueryCondition();
        condition.eq(BizLcJl.InnerColumn.jlLx, "00");
        condition.and().andCondition(" jssj is not null and jssj != ''");
        PageInfo<BizLcJl> info = findPage(page, condition);

        WritableCellFormat cellFormat = new WritableCellFormat();
        cellFormat.setAlignment(Alignment.CENTRE);
        cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        cellFormat.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + ".xls");
        ServletOutputStream out = response.getOutputStream();

        WritableWorkbook workbook = Workbook.createWorkbook(out);
        WritableSheet sheet = workbook.createSheet("本校队号统计", 0);


        sheet.mergeCells(0, 0, 9, 0);
        sheet.mergeCells(0, 1, 9, 1);
        sheet.mergeCells(0, 2, 0, 3);
        sheet.mergeCells(1, 2, 4, 2);
        sheet.mergeCells(5, 2, 8, 2);
        sheet.mergeCells(9,2,9,3);
//        sheet.mergeCells(13, 0, 14, 0);
//        sheet.mergeCells(15, 0, 15, 1);
        sheet.addCell(new Label(0, 0, "知音考场练车统计（本校）", cellFormat));
        sheet.addCell(new Label(0, 1, "时间段：" + timeString, cellFormat));

        sheet.addCell(new Label(0, 2, "队号", cellFormat));
        sheet.addCell(new Label(1, 2, "科二", cellFormat));
        sheet.addCell(new Label(5, 2, "科三", cellFormat));
        sheet.addCell(new Label(9, 2, "总计", cellFormat));
        sheet.addCell(new Label(1, 3, "计时", cellFormat));
        sheet.addCell(new Label(2, 3, "培优", cellFormat));
        sheet.addCell(new Label(3, 3, "开放日", cellFormat));
        sheet.addCell(new Label(4, 3, "小计2", cellFormat));

        sheet.addCell(new Label(5, 3, "计时", cellFormat));
        sheet.addCell(new Label(6, 3, "培优", cellFormat));
        sheet.addCell(new Label(7, 3, "按把", cellFormat));
        sheet.addCell(new Label(8, 3, "小计3", cellFormat));

        sheet.addCell(new Label(9, 3, "总计", cellFormat));

        List<BizLcJl> list = info.getList();
        if (CollectionUtils.isNotEmpty(list)) {
            Set<String> jlids = list.stream().map(BizLcJl::getJlId).collect(Collectors.toSet());
            List<BizLcWxjl> wxjls = wxjlService.findIn(BizLcWxjl.InnerColumn.id, jlids);
            Map<String, List<String>> dmIdMap = wxjls.stream().filter(bizLcWxjl -> StringUtils.isNotBlank(bizLcWxjl.getDh())).collect(Collectors.groupingBy(BizLcWxjl::getDm, Collectors.mapping(BizLcWxjl::getId, Collectors.toList())));
            Map<String, List<BizLcJl>> dataMap = new HashMap<>();
            dmIdMap.forEach((s, strings) -> {
                List<BizLcJl> jls = list.stream().filter(bizLcJl -> strings.contains(bizLcJl.getJlId())).collect(Collectors.toList());
                dataMap.put(s, jls);
            });
            Map<String, List<String>> dmIdMapError = wxjls.stream().filter(bizLcWxjl -> StringUtils.isBlank(bizLcWxjl.getDh())).collect(Collectors.groupingBy(BizLcWxjl::getJlXm, Collectors.mapping(BizLcWxjl::getId, Collectors.toList())));
            Map<String, List<BizLcJl>> errorMap = new HashMap<>();
            dmIdMapError.forEach((s, strings) -> {
                List<BizLcJl> jls = list.stream().filter(bizLcJl -> strings.contains(bizLcJl.getJlId())).collect(Collectors.toList());
                errorMap.put(s, jls);
            });
            int i = 4;
            List<String> collect = dataMap.keySet().stream().sorted(Comparator.comparingInt(o -> Integer.parseInt(o.replace("队", "").replace("A","-3").replace("B","-4")))).collect(Collectors.toList());
            for (String s : collect) {
                List<BizLcJl> bizLcJls = dataMap.get(s);
                Map<String, Integer> sumMap = bizLcJls.stream().collect(Collectors.groupingBy(bizLcJl -> bizLcJl.getLcLx() + bizLcJl.getLcKm(), Collectors.summingInt(BizLcJl::getXjje)));
                sheet.addCell(new Label(0, i, s, cellFormat));
                sheet.addCell(new Label(1, i,  sumMap.get("002")==null?"0": sumMap.get("002") +"", cellFormat));
                sheet.addCell(new Label(2, i, sumMap.get("202")==null?"0": sumMap.get("202") +"", cellFormat));
                sheet.addCell(new Label(3, i, sumMap.get("302")==null?"0": sumMap.get("302") +"", cellFormat));
                sheet.addCell(new Label(4, i,  bizLcJls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcKm(), "2")).mapToInt(BizLcJl::getXjje).sum()+ "", cellFormat));

                sheet.addCell(new Label(5, i, sumMap.get("003")==null? "0":sumMap.get("003") + "", cellFormat));
                sheet.addCell(new Label(6, i, sumMap.get("203")==null? "0":sumMap.get("203") + "", cellFormat));
                sheet.addCell(new Label(7, i, sumMap.get("103") ==null? "0":sumMap.get("103") + "", cellFormat));
                sheet.addCell(new Label(8, i, bizLcJls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcKm(), "3")).mapToInt(BizLcJl::getXjje).sum()+ "", cellFormat));

                sheet.addCell(new Label(9, i, bizLcJls.stream().mapToInt(BizLcJl::getXjje).sum()+ "", cellFormat));
                i++;
            }
            for (Map.Entry<String, List<BizLcJl>> entry : errorMap.entrySet()) {
                String s = entry.getKey();
                List<BizLcJl> bizLcJls = entry.getValue();
                Map<String, Integer> sumMap = bizLcJls.stream().collect(Collectors.groupingBy(bizLcJl -> bizLcJl.getLcLx() + bizLcJl.getLcKm(), Collectors.summingInt(BizLcJl::getXjje)));
                sheet.addCell(new Label(0, i, s, cellFormat));
                sheet.addCell(new Label(1, i, sumMap.get("002") == null ? "0" : sumMap.get("002") + "", cellFormat));
                sheet.addCell(new Label(2, i, sumMap.get("202") == null ? "0" : sumMap.get("202") + "", cellFormat));
                sheet.addCell(new Label(3, i, sumMap.get("302") == null ? "0" : sumMap.get("302") + "", cellFormat));
                sheet.addCell(new Label(4, i, bizLcJls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcKm(), "2")).mapToInt(BizLcJl::getXjje).sum() + "", cellFormat));

                sheet.addCell(new Label(5, i, sumMap.get("003") == null ? "0" : sumMap.get("003") + "", cellFormat));
                sheet.addCell(new Label(6, i, sumMap.get("203") == null ? "0" : sumMap.get("203") + "", cellFormat));
                sheet.addCell(new Label(7, i, sumMap.get("103") == null ? "0" : sumMap.get("103") + "", cellFormat));
                sheet.addCell(new Label(8, i, bizLcJls.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcKm(), "3")).mapToInt(BizLcJl::getXjje).sum() + "", cellFormat));

                sheet.addCell(new Label(9, i, bizLcJls.stream().mapToInt(BizLcJl::getXjje).sum() + "", cellFormat));
                i++;
            }
            Map<String, Integer> sumMap = list.stream().collect(Collectors.groupingBy(bizLcJl -> bizLcJl.getLcLx() + bizLcJl.getLcKm(), Collectors.summingInt(BizLcJl::getXjje)));
            sheet.addCell(new Label(0, i, "总计", cellFormat));
            sheet.addCell(new Label(1, i,  sumMap.get("002")==null?"0": sumMap.get("002") +"", cellFormat));
            sheet.addCell(new Label(2, i, sumMap.get("202")==null?"0": sumMap.get("202") +"", cellFormat));
            sheet.addCell(new Label(3, i, sumMap.get("302")==null?"0": sumMap.get("202") +"", cellFormat));
            sheet.addCell(new Label(4, i,  list.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcKm(), "2")).mapToInt(BizLcJl::getXjje).sum()+ "", cellFormat));

            sheet.addCell(new Label(5, i, sumMap.get("003")==null? "0":sumMap.get("003") + "", cellFormat));
            sheet.addCell(new Label(6, i, sumMap.get("103")==null? "0":sumMap.get("203") + "", cellFormat));
            sheet.addCell(new Label(7, i, sumMap.get("203")==null? "0":sumMap.get("103") + "", cellFormat));
            sheet.addCell(new Label(8, i, list.stream().filter(bizLcJl -> StringUtils.equals(bizLcJl.getLcKm(), "3")).mapToInt(BizLcJl::getXjje).sum()+ "", cellFormat));

            sheet.addCell(new Label(9, i, list.stream().mapToInt(BizLcJl::getXjje).sum()+ "", cellFormat));
        }
        workbook.write();
        workbook.close();
    }
}
