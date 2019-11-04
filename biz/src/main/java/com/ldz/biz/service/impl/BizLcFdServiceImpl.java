package com.ldz.biz.service.impl;

import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizLcFdMapper;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.service.BizLcFdService;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.model.SysYh;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.Arrays;
import java.util.List;

@Service
public class BizLcFdServiceImpl extends BaseServiceImpl<BizLcFd, String> implements BizLcFdService {

	@Autowired
	private BizLcFdMapper baseMapper;
	@Autowired
	private BizLcJlService jlService;

	@Override
	protected Mapper<BizLcFd> getBaseMapper() {
		return baseMapper;
	}

	@Override
	public  void afterPager(PageInfo<BizLcFd> pageInfo){
		if(CollectionUtils.isEmpty(pageInfo.getList())){
			return;
		}
		List<BizLcFd> lcFds = pageInfo.getList();
		lcFds.forEach(bizLcFd -> {
			if(StringUtils.isNotBlank(bizLcFd.getLcId())){

				List<BizLcJl> jlList = jlService.findIn(BizLcJl.InnerColumn.id, Arrays.asList(bizLcFd.getLcId().split(",")));
				// 计算练车总费用
				int sum = jlList.stream().mapToInt(BizLcJl::getYfJe).sum();
				int zsc = jlList.stream().mapToInt(BizLcJl::getSc).sum();
				String lckm = jlList.get(0).getLcKm();
				bizLcFd.setLcFy(sum);
				bizLcFd.setSc(zsc);
				bizLcFd.setLcKm(lckm);
				bizLcFd.setJlList(jlList);
			}
		});
	}

	@Override
	public void afterQuery(List<BizLcFd> result){
		if(CollectionUtils.isEmpty(result)){
			return;
		}

		result.forEach(bizLcFd -> {
			if(StringUtils.isNotBlank(bizLcFd.getLcId())){

				List<BizLcJl> jlList = jlService.findIn(BizLcJl.InnerColumn.id, Arrays.asList(bizLcFd.getLcId().split(",")));
				// 计算练车总费用
				int sum = jlList.stream().mapToInt(BizLcJl::getYfJe).sum();
				int zsc = jlList.stream().mapToInt(BizLcJl::getSc).sum();
				String lckm = jlList.get(0).getKm();
				bizLcFd.setLcFy(sum);
				bizLcFd.setSc(zsc);
				bizLcFd.setLcKm(lckm);
				bizLcFd.setJlList(jlList);
			}
		});
	}

	@Override
	public ApiResponse<String> updateZt(String id) {
		RuntimeCheck.ifBlank(id, "请确认返点记录");
		SysYh yh = getCurrentUser();
		List<String> list = Arrays.asList(id.split(","));
		List<BizLcFd> fds = findByIds(list);
		RuntimeCheck.ifEmpty(fds, "此记录不需要返点");
		fds.forEach(fd ->{
			String[] split = fd.getLcId().split(",");
			List<BizLcJl> jls = jlService.findIn(BizLcJl.InnerColumn.id, Arrays.asList(split));
			jls.forEach(bizLcJl -> {
				bizLcJl.setFdZt("10");
				jlService.update(bizLcJl);
			});
			if(fd.getFdsl() == null || fd.getFdsl() == 0 ){
				fd.setFdsl(jls.size());
			}
			fd.setQrr(yh.getZh() + "-" + yh.getXm());
			fd.setQrsj(DateUtils.getNowTime());
			update(fd);
		});
		return ApiResponse.success();
	}
}