package com.ldz.biz.service.impl;


import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.ZgAqyQdLogMapper;
import com.ldz.biz.mapper.ZgjbxxMapper;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.CoachManagement;
import com.ldz.biz.model.ZgAqyQdLog;
import com.ldz.biz.model.Zgjbxx;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.biz.service.CoachManagementService;
import com.ldz.biz.service.ZgjbxxService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.service.JgService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.commonUtil.EncryptUtil;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZgjbxxServiceImpl extends BaseServiceImpl<Zgjbxx, String> implements ZgjbxxService {

    @Autowired
    private ZgjbxxMapper baseMapper;

    @Autowired
    private ZgAqyQdLogMapper aqyQdLogMapper;
      @Autowired
    private JgService jgService;
    @Autowired
    private CoachManagementService coachService;

    @Autowired
    private BizLcJlService jlService;

    @Override
    protected Mapper<Zgjbxx> getBaseMapper() {
        return baseMapper;
    }



    @Override
    public ApiResponse<String> saveEntity(Zgjbxx entity) {
        entity.setAqyQdzt("00");
        entity.setId(genId());
        entity.setZzzt("10");
        entity.setJlZt("20");
        SysYh currentUser = getCurrentUser();
        if(entity.getxRs() == null){
            entity.setxRs(0);
        }
        if(entity.getsRs()==null){
            entity.setsRs(0);
        }
        if(StringUtils.equals(entity.getJlZt(),"10")){
            // 教练
            CoachManagement coachManagement = new CoachManagement();
            coachManagement.setCjr(currentUser.getZh()+"-" + currentUser.getXm());
            coachManagement.setCjsj(DateUtils.getNowTime());
            coachManagement.setIdCardNo(entity.getSfzh());
            coachManagement.setId(genId());
            coachManagement.setCoachName(entity.getXm());
            coachManagement.setGender(entity.getXb().equals("男")? "10":"00");
            coachManagement.setDrivingType(entity.getZjcx1());
            coachManagement.setPhone(entity.getSjhm());
            coachManagement.setCoachNum(entity.getJlzh());
            coachManagement.setCoachStatus("00");
            coachManagement.setJgdm(currentUser.getJgdm());
            coachManagement.setJgmc(jgService.findByOrgCode(currentUser.getJgdm()).getJgmc());
            coachService.save(coachManagement);
            entity.setJlId(coachManagement.getId());
        }

        save(entity);
        return ApiResponse.saveSuccess();
    }

    @Override
    public ApiResponse<String> updateEntity(Zgjbxx entity) {
        SysYh currentUser = getCurrentUser();
        if(StringUtils.isNotBlank(entity.getJlId())){

            CoachManagement coach = coachService.findById(entity.getJlId());
            if(StringUtils.equals(entity.getJlZt(),"20")){
                coach.setCoachStatus("10");
            }else if(StringUtils.equals(entity.getJlZt(),"10")){
                coach.setCoachStatus("00");
            }
            coach.setIdCardNo(entity.getSfzh());
            coach.setOperater(currentUser.getZh() + "-" + currentUser.getXm());
            coach.setOperateTime(DateUtils.getNowTime());
            coach.setCoachName(entity.getXm());
            coach.setGender(entity.getXb().equals("男")?"10":"00");
            coach.setDrivingType(entity.getZjcx1());
            coach.setPhone(entity.getSjhm());
            coach.setCoachNum(entity.getJlzh());
            coach.setJgdm(entity.getJgdm());
            coach.setJgmc(entity.getJgmc());
            coach.setRecordNum(entity.getDabh1());
            coachService.update(coach);
        }else if(StringUtils.equals(entity.getJlZt(),"10")){
            CoachManagement coach = new CoachManagement();
            coach.setJgdm(entity.getJgdm());
            coach.setJgmc(entity.getJgmc());
            coach.setCoachStatus("00");
            coach.setId(genId());
            coach.setIdCardNo(entity.getSfzh());
            coach.setOperater(currentUser.getZh() + "-" + currentUser.getXm());
            coach.setOperateTime(DateUtils.getNowTime());
            coach.setCoachName(entity.getXm());
            coach.setGender(entity.getXb().equals("男")?"10":"00");
            coach.setDrivingType(entity.getZjcx1());
            coach.setPhone(entity.getSjhm());
            coach.setCoachNum(entity.getJlzh());
            coach.setRecordNum(entity.getDabh1());
            if(StringUtils.isNotBlank(coach.getIdCardNo())){
                String userPwd = EncryptUtil.encryptUserPwd(coach.getIdCardNo().substring(coach.getIdCardNo().length() - 6));
                coach.setPassword(userPwd);
            }
            coachService.save(coach);
            entity.setJlId(coach.getId());
        }
        entity.setAqyQdzt(null);
        update(entity);

        return ApiResponse.success();
    }

    @Override
    public ApiResponse<List<Zgjbxx>> getAqy() {
        String all = getRequestParamterAsString("all");
        if(StringUtils.equals(all, "1")){
            SimpleCondition condition = new SimpleCondition(Zgjbxx.class);
            condition.eq(Zgjbxx.InnerColumn.gzgw,"0005");
            condition.eq(Zgjbxx.InnerColumn.aqyQdzt,"10");
            condition.setOrderByClause(" CONVERT(xm using gbk) asc ");
            List<Zgjbxx> zgjbxxes = findByCondition(condition);
            return ApiResponse.success(zgjbxxes);
        }
        // 先找下在车上的安全员
        SimpleCondition simpleCondition = new SimpleCondition(BizLcJl.class);
        simpleCondition.and().andIsNotNull(BizLcJl.InnerColumn.zgId.name());
        simpleCondition.and().andCondition(" jssj is null or jssj = ''");
        List<BizLcJl> lcCls = jlService.findByCondition(simpleCondition);
        List<String> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(lcCls)){
            list= lcCls.stream().map(BizLcJl::getZgId).collect(Collectors.toList());
        }
        SimpleCondition condition = new SimpleCondition(Zgjbxx.class);
        if(CollectionUtils.isNotEmpty(list)){
            condition.notIn(Zgjbxx.InnerColumn.id,list);
        }
        condition.eq(Zgjbxx.InnerColumn.gzgw,"0005");
        condition.eq(Zgjbxx.InnerColumn.aqyQdzt,"10");
        condition.setOrderByClause(" CONVERT(xm using gbk) asc ");
        List<Zgjbxx> zgjbxxes = findByCondition(condition);

        return ApiResponse.success(zgjbxxes);
    }

    @Override
    public boolean fillPagerCondition(LimitedCondition condition) {
        HttpServletRequest requset = getRequset();
        String aqybx = requset.getParameter("aqybx");//是否按安全员姓名排序
        if(StringUtils.equals("1",aqybx)){
            condition.setOrderByClause( " convert(XM using gbk) ASC ");
        }else{
            condition.setOrderByClause( " dabh1 asc ");
        }
        return true;
    }

    @Override
    public void afterPager(PageInfo<Zgjbxx> resultPage) {
        if (CollectionUtils.isEmpty(resultPage.getList())) {
            return;
        }

        List<Zgjbxx> list = resultPage.getList();
        for (Zgjbxx zgjbxx : list) {
            if(zgjbxx.getsRs()==null){
                zgjbxx.setsRs(0);
            }
            if(zgjbxx.getxRs()==null){
                zgjbxx.setxRs(0);
            }
        }
    }


    /**
     * 安全员签到
     * id   员工ID
     * aqyQdzt  签到状态 (字典[aqyzt] 00休息 10上班)
     * @return
     */
    @Override
    public ApiResponse<String> setSecurityOfficerSignin(Zgjbxx entity){
        SysYh sysUser = getCurrentUser(true);

//		1、参数非空验证
        RuntimeCheck.ifBlank(entity.getId(), "请选择一位员工");
        RuntimeCheck.ifBlank(entity.getAqyQdzt(), "请确定安全员签到状态：休息、上班");
        RuntimeCheck.ifFalse(StringUtils.containsAny("00,10",entity.getAqyQdzt()), "请填写正确的签到状态：00、休息 10、上班");
//      2、有效性验证
        Zgjbxx zg=this.findById(entity.getId());
        RuntimeCheck.ifNull(zg, "请选择正确的员工");
        RuntimeCheck.ifFalse(StringUtils.equals("0005",zg.getGzgw()), "该员工不是安全员，无法做签到操作");
        RuntimeCheck.ifTrue(StringUtils.equals("20",zg.getZzzt()), "该员工已离职，无需做签到操作");
        RuntimeCheck.ifTrue(StringUtils.equals("40",zg.getZzzt()), "该员工已停职，无需做签到操作");
        String aqyQdzt= zg.getAqyQdzt();
        if(StringUtils.equals(entity.getAqyQdzt(),"00")){//未签到
            RuntimeCheck.ifTrue(StringUtils.equals("00",aqyQdzt), "该员工已休息状态，无需做休息操作");
        }else{//已签到
            RuntimeCheck.ifTrue(StringUtils.equals("10",aqyQdzt), "该员工已上班状态，无需做上班操作");
        }

//        3、修改参数回填
        zg.setAqyQdzt(entity.getAqyQdzt());
        zg.setKm(entity.getKm());
        if(StringUtils.equals(entity.getAqyQdzt(),"00")){
            zg.setAqyQdsj("");
        }else{
            zg.setAqyQdsj(DateUtils.getToday("yyyy-MM-dd HH:mm:ss"));
        }

//        写库
        update(zg);
//        写签到日志
        ZgAqyQdLog aqyQdLog=new ZgAqyQdLog();
        aqyQdLog.setId(genId());
        aqyQdLog.setXm(zg.getXm());
        aqyQdLog.setJgdm(zg.getJgdm());
        aqyQdLog.setJgmc(zg.getJgmc());
        aqyQdLog.setAqyQdzt(zg.getAqyQdzt());
        aqyQdLog.setAqyQdsj(zg.getAqyQdsj());
        aqyQdLog.setCjr(sysUser.getZh()+"-"+sysUser.getXm());
        aqyQdLog.setCjsj(DateUtils.getNowTime());
        aqyQdLog.setKm(zg.getKm());
        aqyQdLogMapper.insert(aqyQdLog);

        return ApiResponse.success();
    }

    /**
     * 定时任务，定时清除
     */
    @Override
    public void clearAqyQd(){
        baseMapper.bulkInsertLog();//批量插入日志
        baseMapper.clearSecurityOfficerType();//清除安全员状态
    }
}