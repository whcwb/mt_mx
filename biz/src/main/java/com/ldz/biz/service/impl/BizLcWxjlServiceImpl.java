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
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

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
//		RuntimeCheck.ifBlank(entity.getJlJx(), "驾校名称不能为空");
		RuntimeCheck.ifBlank(entity.getJlXm(), "教练姓名不能为空");
		RuntimeCheck.ifBlank(entity.getJlLxdh(), "教练联系电话不能为空");
		List<BizLcWxjl> wxjls = findEq(BizLcWxjl.InnerColumn.jlLxdh, entity.getJlLxdh());
		RuntimeCheck.ifTrue( CollectionUtils.isNotEmpty(wxjls), "此号码已经绑定 " + wxjls.get(0).getJlJx() + "-" + wxjls.get(0).getJlXm() + " , 请勿重复绑定");
		entity.setId(genId());
		entity.setCjr(user.getZh()+"-"+user.getXm());
		entity.setCjsj(DateUtils.getNowTime());
		if(StringUtils.isBlank(entity.getJlJx())){
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
		RuntimeCheck.ifTrue(StringUtils.isNotBlank(wxjl.getCardNo()) , "教练员已经绑定卡号, 请勿重复操作");
		int maxNo = baseMapper.getMaxNo();
		String cardNo = genCardNo(maxNo);
		List<BizLcWxjl> eq = findEq(BizLcWxjl.InnerColumn.cardNo, cardNo);
		while (CollectionUtils.isNotEmpty(eq)){
			int anInt = Integer.parseInt(cardNo) + 1;
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
		RuntimeCheck.ifEmpty(wxjls,  "未找到卡号信息");
		BizLcWxjl wxjl = wxjls.get(0);
		wxjl.setPwd(EncryptUtil.encryptUserPwd("123456"));
		update(wxjl);
		return ApiResponse.success();
	}

	@Override
	public ApiResponse<String> czmx(int pageNum, int pageSize, String id) {
		RuntimeCheck.ifBlank(id, "请选择记录");
		SimpleCondition condition = new SimpleCondition(BizJlCz.class);
		condition.eq(BizJlCz.InnerColumn.jlId, id);
		PageInfo<BizJlCz> info = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> czMapper.selectByExample(condition));
		ApiResponse<String> res = new ApiResponse<>();
		res.setPage(info);
		return res;
	}

	private String genCardNo(int maxNo){
		StringBuilder s = new StringBuilder(maxNo++ + "");
		int length = s.length();

		if(length == 6 ){
			return maxNo +"";
		}
		for(int i = 0 ; i < 6 - length; i ++ ){
			s.insert(0, "0");
		}
		return s.toString();
	}
}