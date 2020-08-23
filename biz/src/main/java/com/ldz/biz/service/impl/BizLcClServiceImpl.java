package com.ldz.biz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizLcClMapper;
import com.ldz.biz.model.BizLcCl;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.biz.service.BizLcClService;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.sys.mapper.SysPtjgMapper;
import com.ldz.sys.model.SysJg;
import com.ldz.sys.model.SysZdxm;
import com.ldz.sys.service.ZdxmService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BizLcClServiceImpl extends BaseServiceImpl<BizLcCl, String> implements BizLcClService {
    @Autowired
    private BizLcClMapper baseMapper;
    @Autowired
    private BizLcJlService jlService;
    @Autowired
    private ZdxmService zdxmService;
    @Autowired
    private BizLcWxjlService wxjlService;
    @Autowired
    private SysPtjgMapper ptjgMapper;

    @Override
    protected Mapper<BizLcCl> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public boolean fillPagerCondition(LimitedCondition condition) {
        return true;
    }

    @Override
    public ApiResponse<String> saveEntity(BizLcCl entity) {
        ApiResponse<String> result = new ApiResponse<>();
        RuntimeCheck.ifBlank(entity.getClBh(), "车辆编号不能为空");
        RuntimeCheck.ifBlank(entity.getClHm(), "车牌号码不能为空");
        RuntimeCheck.ifBlank(entity.getClKc(), "所属考场不能为空");
        RuntimeCheck.ifBlank(entity.getClKm(), "车辆科目不能为空");
        RuntimeCheck.ifBlank(entity.getClCx(), "车型不能为空");
        RuntimeCheck.ifBlank(entity.getJgdm(), "车辆所属考场不能为空");
        SimpleCondition condition = new SimpleCondition(BizLcCl.class);
        condition.eq(BizLcCl.InnerColumn.clBh, entity.getClBh());
        condition.eq(BizLcCl.InnerColumn.clKm, entity.getClKm());
        condition.eq(BizLcCl.InnerColumn.clCx, entity.getClCx());
        condition.startWith(BizLcCl.InnerColumn.jgdm, entity.getJgdm());
        List<BizLcCl> bizLcClList = findByCondition(condition);
        RuntimeCheck.ifTrue(CollectionUtils.isNotEmpty(bizLcClList), "该科目的车辆编号已经存在,请勿重复添加");
        if (StringUtils.isNotBlank(entity.getCardNo())) {
            SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
            jlCondition.eq(BizLcJl.InnerColumn.cardNo, entity.getCardNo());
            jlCondition.and().andCondition(" jssj is null or jssj = ''");
            jlCondition.setOrderByClause(" kssj desc ");
            List<BizLcJl> jls = jlService.findByCondition(jlCondition);
            // 判断 卡号 和 车辆 状态是否能够绑卡
            if (CollectionUtils.isNotEmpty(jls)) {
                return ApiResponse.fail("此卡正在训练中，不能绑定车辆");
            }
            List<BizLcCl> lcCls = findEq(BizLcCl.InnerColumn.cardNo, entity.getCardNo());
            if (StringUtils.isNotBlank(entity.getTh()) && CollectionUtils.isNotEmpty(lcCls)) {
                BizLcCl cl = lcCls.get(0);
                RuntimeCheck.ifTrue("01".equalsIgnoreCase(cl.getClZt()), "此卡绑定车辆正在训练中，不能修改卡号");
                baseMapper.updateCardNoToNull(cl.getId());
            } else {
                if (CollectionUtils.isNotEmpty(lcCls)) {
                    BizLcCl cl = lcCls.get(0);
                    RuntimeCheck.ifTrue("01".equalsIgnoreCase(cl.getClZt()), "此卡绑定车辆正在训练中，不能修改");
                    if (!cl.getClBh().equals(entity.getClBh())) {
                        result.setMessage("当前卡号已绑定科目" + ("2".equals(cl.getClKm()) ? "二" : "三") + " - " + cl.getClBh() + "车 , 是否替换?");
                        result.setCode(505);
                        return result;
                    } else if (cl.getClBh().equals(entity.getClBh()) && !cl.getClKm().equals(entity.getClKm())) {
                        result.setMessage("当前卡号已绑定科目" + ("2".equals(cl.getClKm()) ? "二" : "三") + " - " + cl.getClBh() + "车 , 是否替换?");
                        result.setCode(505);
                        return result;
                    }
                }
            }
        }
        entity.setClZt("00");
        entity.setId(genId());
        save(entity);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> getCar(Page<BizLcCl> page) throws ParseException {
        ApiResponse<String> result = new ApiResponse<>();
        LimitedCondition queryCondition = getQueryCondition();
        queryCondition.in(BizLcCl.InnerColumn.clZt, Arrays.asList("00", "01"));
        queryCondition.setOrderByClause(" jgdm asc , cl_bh asc ");
        queryCondition.startWith(BizLcCl.InnerColumn.jgdm, getJgdm());
        PageInfo<BizLcCl> pageInfo = findPage(page, queryCondition);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            // 根据车辆列表查询当前正在训练的所有记录
            Set<String> clIds = pageInfo.getList().stream().map(BizLcCl::getId).collect(Collectors.toSet());
            SimpleCondition condition = new SimpleCondition(BizLcJl.class);
            condition.in(BizLcJl.InnerColumn.lcClId, clIds);
            condition.and().andCondition(" jssj is null or jssj = ''");
            condition.setOrderByClause(" kssj desc");
            condition.startWith(BizLcCl.InnerColumn.jgdm, getJgdm());
            List<BizLcJl> jls = jlService.findByCondition(condition);
            if (CollectionUtils.isNotEmpty(jls)) {
                // 在训车辆 每辆车对应一条记录
                Map<String, BizLcJl> jlMap = jls.stream().collect(Collectors.toMap(BizLcJl::getLcClId, p -> p));
                for (BizLcCl bizLcCl : pageInfo.getList()) {
                    BizLcJl bizLcJl = jlMap.get(bizLcCl.getId());
                    if (bizLcJl == null) {
                        continue;
                    }
                    BizLcWxjl wxjl = wxjlService.findById(bizLcJl.getJlId());
                    bizLcCl.setLcJl(bizLcJl);
                    bizLcJl.setJlDh(wxjl.getJlLxdh());
                    // 计算下时长 所有计费向上取整
                    String kssj = bizLcJl.getKssj();
                    String sc;
                    Date ks = dateFormat.parse(kssj);
                    Date js = new Date();

                    long ksfz = ks.getTime() / (60 * 1000);
                    long jsfz = js.getTime() / (60 * 1000);
                    sc = (int) (jsfz - ksfz) + "";

                    bizLcCl.setYhsc(sc);
                    bizLcCl.setDqsc(sc + "");
                    SimpleCondition zjCondition = new SimpleCondition(SysZdxm.class);
                    zjCondition.eq(SysZdxm.InnerColumn.zdlmdm, "ZDCLK1045");
                    zjCondition.eq(SysZdxm.InnerColumn.zddm, bizLcJl.getZddm());
                    zjCondition.startWith(SysZdxm.InnerColumn.jgdm, getJgdm());
                    List<SysZdxm> items = zdxmService.findByCondition(zjCondition);
                    SysZdxm zdxm = items.get(0);
                    bizLcCl.setZdxm(zdxm);
                    if (StringUtils.equals(bizLcJl.getLcLx(), "00")) {
                        if (CollectionUtils.isNotEmpty(items)) {
                            //bizLcCl.setDj(Float.parseFloat(items.get(0).getZdmc()));
                            SysZdxm management = items.get(0);
                            // 计算费用和分钟的最大公约数
                            // 每小时的费用
                            int anInt = Integer.parseInt(management.getZdmc());
                            if (StringUtils.equals(bizLcJl.getLcLx(), "00")) {
                                int v;
                                if (StringUtils.equals(bizLcJl.getLcKm(), "2") && StringUtils.equals(bizLcJl.getZddm(), "K2JS-S")) {
                                    // 科目二的 190 35分钟 , 超出时间按照 8.33计算
                                    String by3 = management.getBy3();
                                    // 190
                                    String hour = management.getZdmc();
                                    if (Integer.parseInt(sc) > management.getQz()) {
                                        v = (int) Math.ceil((Integer.parseInt(sc) - (management.getQz() * bizLcJl.getXySl())) * Float.parseFloat(by3)) + (Integer.parseInt(hour) * bizLcJl.getXySl());
                                    } else {
                                        v = Integer.parseInt(hour) * bizLcJl.getXySl();
                                    }
                                } else {
                                    // 每小时的费用
                                    String hour = management.getZdmc();
                                    String by3 = management.getBy3();
                                    int h = Integer.parseInt(sc) / 60;
                                    int m = Integer.parseInt(sc) % 60;
                                    // 小时能除尽的按小时计费
                                    float hv = Float.parseFloat(hour) * h;
                                    // 不能除尽的按分钟算
                                    float mv = m * Float.parseFloat(by3);
                                    // 总费用
                                    v = (int) Math.ceil(hv + mv);
                                }
                                bizLcCl.setDj(anInt);
                                bizLcCl.setDj1(Float.parseFloat(management.getBy3()));
                                bizLcCl.setZj(v);
                            } else {
                                bizLcCl.setZj(Integer.parseInt(StringUtils.isBlank(management.getBy4()) ? "0" : management.getBy4()));
                                bizLcCl.setDj(Float.parseFloat(StringUtils.isBlank(management.getBy4()) ? "0" : management.getBy4()));
                            }
                        }
                    } else {
                        bizLcCl.setZj(bizLcJl.getLcFy());
                    }
                }
            }

        }
        result.setPage(pageInfo);
        return result;
    }

    /**
     * 清除车辆分配数据
     *
     * @return
     */
    @Override
    public ApiResponse<String> clearCarAllot() {
        List<BizLcCl> cars = baseMapper.selectAll();
        for (BizLcCl car : cars) {
            car.setZgId(null);
            car.setZgXm(null);
            baseMapper.updateByPrimaryKey(car);
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<String> updateEntity(BizLcCl entity) {

        BizLcCl lcCl = findById(entity.getId());

        if (StringUtils.equals(lcCl.getClZt(), "01") && !StringUtils.equals(entity.getClZt(), "01")) {
            return ApiResponse.fail("此车辆还在训练中 , 不能修改状态");
        } else if (StringUtils.equals(entity.getClZt(), "01")) {
            return ApiResponse.success("此状态不支持手动修改");
        }
        update(entity);

        return ApiResponse.success();
    }

    @Override
    public void clearClZt() {
        baseMapper.updateClZt();
    }

    @Override
    public ApiResponse<String> updateCardNo(String id, String cardNo, String th) {
        ApiResponse<String> result = new ApiResponse<>();
        BizLcCl lcCl = findById(id);
        RuntimeCheck.ifNull(lcCl, "未找到车辆信息");
        RuntimeCheck.ifTrue("01".equalsIgnoreCase(lcCl.getClZt()), "此车正在训练中，不能更换卡号");
        SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
        jlCondition.eq(BizLcJl.InnerColumn.cardNo, cardNo);
        jlCondition.and().andCondition(" jssj is null or jssj = ''");
        jlCondition.setOrderByClause(" kssj desc ");
        List<BizLcJl> jls = jlService.findByCondition(jlCondition);
        if (CollectionUtils.isNotEmpty(jls)) {
            return ApiResponse.fail("此卡正在训练中，不能绑定车辆");
        }
        List<BizLcCl> lcCls = findEq(BizLcCl.InnerColumn.cardNo, cardNo);
        if (StringUtils.isNotBlank(th) && CollectionUtils.isNotEmpty(lcCls)) {
            BizLcCl cl = lcCls.get(0);
            RuntimeCheck.ifTrue("01".equalsIgnoreCase(cl.getClZt()), "此卡绑定车辆正在训练中，不能修改");
            baseMapper.updateCardNoToNull(cl.getId());
        } else {
            if (CollectionUtils.isNotEmpty(lcCls)) {
                BizLcCl cl = lcCls.get(0);
                RuntimeCheck.ifTrue("01".equalsIgnoreCase(cl.getClZt()), "此卡绑定车辆正在训练中，不能修改");
                if (!cl.getClBh().equals(lcCl.getClBh())) {
                    result.setMessage("当前卡号已绑定科目" + ("2".equals(cl.getClKm()) ? "二" : "三") + " - " + cl.getClBh() + "车 , 是否替换?");
                    result.setCode(505);
                    return result;
                } else if (cl.getClBh().equals(lcCl.getClBh()) && !cl.getClKm().equals(lcCl.getClKm())) {
                    result.setMessage("当前卡号已绑定科目" + ("2".equals(cl.getClKm()) ? "二" : "三") + " - " + cl.getClBh() + "车 , 是否替换?");
                    result.setCode(505);
                    return result;
                }
            }
        }
        lcCl.setCardNo(cardNo);
        update(lcCl);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<BizLcCl> getCarInfo(String cardNo, String km) {
        ApiResponse<BizLcCl> response = new ApiResponse<>();
        SimpleCondition jlCondition = new SimpleCondition(BizLcJl.class);
        jlCondition.eq(BizLcJl.InnerColumn.cardNo, cardNo);
        jlCondition.and().andCondition(" jssj is null or jssj = ''");
        jlCondition.setOrderByClause(" kssj desc ");
        List<BizLcJl> jls = jlService.findByCondition(jlCondition);
        if (CollectionUtils.isNotEmpty(jls)) {
            response.setCode(501);
            return response;
        }
        SimpleCondition simpleCondition = new SimpleCondition(BizLcCl.class);
        simpleCondition.eq(BizLcCl.InnerColumn.cardNo, cardNo);
        List<BizLcCl> lcCls = findByCondition(simpleCondition);
        if (CollectionUtils.isNotEmpty(lcCls)) {
            BizLcCl lcCl = lcCls.get(0);
            if (lcCl.getClKm().equals(km)) {
                response.setCode(200);
                response.setResult(lcCl);
                return response;
            } else {
                response.setCode(505);
                response.setResult(lcCl);
                return response;
            }
        } else {
            response.setCode(200);
            return response;
        }
    }

    @Override
    public ApiResponse<List<SysJg>> getJgsByOrgCode(HttpServletRequest request) {
        String orgCode = (String) request.getAttribute("orgCode");
        SimpleCondition simpleCondition = new SimpleCondition(SysJg.class);
        if (StringUtils.length(orgCode) > 9) {
            orgCode = orgCode.substring(0, 9);
        }
        simpleCondition.startWith(SysJg.InnerColumn.jgdm, orgCode);
        simpleCondition.and().andCondition(" LENGTH(jgdm) = 9 ");
        List<SysJg> jgs = ptjgMapper.selectByExample(simpleCondition);
        return ApiResponse.success(jgs);
    }

    @Override
    public ApiResponse<List<SysJg>> getNextJg(String jgdm) {
        RuntimeCheck.ifBlank(jgdm, "机构代码不能为空");
        SimpleCondition condition = new SimpleCondition(SysJg.class);
        condition.startWith(SysJg.InnerColumn.jgdm, jgdm);
        condition.and().andNotEqualTo(SysJg.InnerColumn.jgdm.name(), jgdm);
        List<SysJg> jgs = ptjgMapper.selectByExample(condition);
        return ApiResponse.success(jgs);
    }
}