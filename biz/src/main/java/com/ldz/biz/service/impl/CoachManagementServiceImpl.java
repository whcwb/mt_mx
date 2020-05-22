package com.ldz.biz.service.impl;

import com.ldz.biz.mapper.CoachManagementMapper;
import com.ldz.biz.model.CoachManagement;
import com.ldz.biz.model.Zgjbxx;
import com.ldz.biz.service.CoachManagementService;
import com.ldz.biz.service.ZgjbxxService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.sys.model.SysJg;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.service.JgService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class CoachManagementServiceImpl extends BaseServiceImpl<CoachManagement, String> implements CoachManagementService {

	@Autowired
	private CoachManagementMapper baseMapper;

	@Autowired
	private ZgjbxxService zgjbxxService;

	@Autowired
	private JgService jgService;
	
	@Override
	protected Mapper<CoachManagement> getBaseMapper() {
		return baseMapper;
	}

	@Override
	public boolean fillPagerCondition(LimitedCondition condition){
		condition.eq(CoachManagement.InnerColumn.coachStatus,"00");

		HttpServletRequest requset = getRequset();
		//获取车型
		String cx = requset.getParameter("cx");
		if(StringUtils.isNotEmpty(cx)&&cx.length()<3){
//			//因为从前台转过来的字符串，需要直接拼接到SQL中执行，为了防止SQL注入的风险，所以给入参进行验证,必须是以下字符串才可以进行SQL验证
//			if(StringUtils.indexOf("A1、A2、A3、B1、B2、C1、C2、C3、C4、D、E、F、M、N、P",cx.toUpperCase())>-1){//StringUtils.substring(cx,0,1)
//				condition.and().andCondition(" STRCMP('"+cx.toUpperCase()+"', DRIVING_TYPE) > -1 ");//查询出所有满足条件车型
//			}
			condition.like(CoachManagement.InnerColumn.drivingType,cx.toUpperCase());
		}
		//coach_sub  培训科目  01:科目一 02：科目二 03：科目三 04：科目四
		String km = requset.getParameter("km");
		if(StringUtils.isNotEmpty(km)){
			condition.like(CoachManagement.InnerColumn.coachSub,km);
		}
//		//教练证有效时间，
//		condition.lte(CoachManagement.InnerColumn.coachStartTime,DateUtils.getToday());//<= new Date
//		condition.gte(CoachManagement.InnerColumn.coachEndTime,DateUtils.getToday());// endTime>= new Date
		return true;
	}

	@Override
	public boolean fillQueryCondition(LimitedCondition condition){
		condition.eq(CoachManagement.InnerColumn.coachStatus,"00");

		HttpServletRequest requset = getRequset();
		//获取车型
		String cx = requset.getParameter("cx");
		if(StringUtils.isNotEmpty(cx)&&cx.length()<3){
//			//因为从前台转过来的字符串，需要直接拼接到SQL中执行，为了防止SQL注入的风险，所以给入参进行验证,必须是以下字符串才可以进行SQL验证
//			if(StringUtils.indexOf("A1、A2、A3、B1、B2、C1、C2、C3、C4、D、E、F、M、N、P",cx.toUpperCase())>-1){//StringUtils.substring(cx,0,1)
//				condition.and().andCondition(" STRCMP('"+cx.toUpperCase()+"', DRIVING_TYPE) > -1 ");//查询出所有满足条件车型
//			}
			condition.like(CoachManagement.InnerColumn.drivingType,cx.toUpperCase());
		}
		//coach_sub  培训科目  01:科目一 02：科目二 03：科目三 04：科目四
		String km = requset.getParameter("km");
		if(StringUtils.isNotEmpty(km)){
			condition.like(CoachManagement.InnerColumn.coachSub,km);
		}
//		//教练证有效时间，
//		condition.lte(CoachManagement.InnerColumn.coachStartTime,DateUtils.getToday());//<= new Date
//		condition.gte(CoachManagement.InnerColumn.coachEndTime,DateUtils.getToday());// endTime>= new Date
		return true;
	}

	@Override
	public ApiResponse<String> validAndSave(CoachManagement entity){
		SysYh currentUser = getCurrentUser();
		CoachManagement coach = new CoachManagement();
		RuntimeCheck.ifBlank(entity.getArea(), "所属区域不能为空");
		RuntimeCheck.ifBlank(entity.getCoachName(), "姓名不能为空");
		RuntimeCheck.ifBlank(entity.getCoachSub(), "培训科目不能为空");
		RuntimeCheck.ifBlank(entity.getJgdm(), "机构代码不能为空");
		// RuntimeCheck.ifBlank(entity.getJgmc(), "机构名称不能为空");
		RuntimeCheck.ifBlank(entity.getCoachNum(), "教练证编号不能为空");
		RuntimeCheck.ifBlank(entity.getCoachStartTime(), "教练证开始时间不能为空");
		RuntimeCheck.ifBlank(entity.getCoachEndTime(), "教练证有效结束时间不能为空");
		RuntimeCheck.ifBlank(entity.getDrivingType(), "准驾类型不能为空");
		RuntimeCheck.ifBlank(entity.getPhone(), "电话号码不能为空");
		RuntimeCheck.ifTrue(entity.getDrivingYears() <= 0, "驾龄不能为0");
		RuntimeCheck.ifBlank(entity.getLicenseStartTime(), "驾驶证有效期不能为空");
		RuntimeCheck.ifBlank(entity.getLicenseEndTime(), "驾驶证有效期结束时间不能为空");
		RuntimeCheck.ifBlank(entity.getRecordNum(), "档案编号不能为空");
		RuntimeCheck.ifBlank(entity.getIdCardNo(), "身份证号不能为空");

		BeanUtils.copyProperties(entity,coach);

		SysJg sysJg = jgService.findById(coach.getJgdm());
		SysJg pjg = jgService.findById(coach.getJgdm().substring(0, coach.getJgdm().length() - 3));
		coach.setJgmc(pjg.getJgmc() + "/" + sysJg.getJgmc());
		coach.setCoachStatus("00");
		coach.setCjr(currentUser.getZh()+"-"+currentUser.getXm());
		coach.setCjsj(DateUtils.getNowTime());
		coach.setId(genId());
		save(coach);
		// RuntimeCheck.ifBlank(entity);
		return ApiResponse.saveSuccess();
	}

	@Override
	public ApiResponse<String> updateEntity(CoachManagement entity) {
		SysYh currentUser = getCurrentUser();
		CoachManagement byId = findById(entity.getId());
		RuntimeCheck.ifNull(byId, "未找到该记录");
		if(StringUtils.isNotBlank(entity.getJgdm()) && !StringUtils.equals(entity.getJgdm(),byId.getJgdm())){
			SysJg sysJg = jgService.findById(entity.getJgdm());
			SysJg jg = jgService.findById(entity.getJgdm().substring(0, entity.getJgdm().length() - 3));
			entity.setJgmc(jg.getJgmc()+ "/" + sysJg.getJgmc());
		}
		SimpleCondition condition = new SimpleCondition(Zgjbxx.class);
		condition.eq(Zgjbxx.InnerColumn.jlId, byId.getId());
		List<Zgjbxx> zgjbxxes = zgjbxxService.findByCondition(condition);
		if(CollectionUtils.isNotEmpty(zgjbxxes)){
			Zgjbxx zgjbxx = zgjbxxes.get(0);
			zgjbxx.setXm(entity.getCoachName());
			zgjbxx.setXb(entity.getGender().equalsIgnoreCase("10")?"男":"女");
			zgjbxx.setSjhm(entity.getPhone());
			zgjbxx.setSfzh(entity.getIdCardNo());
			zgjbxx.setJlzh(entity.getCoachNum());
			zgjbxx.setZjcx1(entity.getDrivingType());
			zgjbxxService.update(zgjbxx);

		}
		entity.setOperater(currentUser.getZh()+"-"+currentUser.getXm());
		entity.setOperateTime(DateUtils.getNowTime());
		update(entity);

		return ApiResponse.success();
	}

	@Override
	public ApiResponse<String> deleteEntity(String id) {
		CoachManagement management = findById(id);
		management.setCoachStatus("10");
		update(management);
		SimpleCondition condition = new SimpleCondition(Zgjbxx.class);
		condition.eq(Zgjbxx.InnerColumn.jlId,management.getId());
		List<Zgjbxx> byCondition = zgjbxxService.findByCondition(condition);
		if(CollectionUtils.isNotEmpty(byCondition)){
			Zgjbxx coachManagement = byCondition.get(0);
			coachManagement.setJlZt("20");
			zgjbxxService.update(coachManagement);
		}
		return ApiResponse.success();
	}

	@Override
	public void updateCoachTraineeCount(){
		baseMapper.updateCoachTraineeCount(DateUtils.getToday());//DateUtils.getToday()

	}
}