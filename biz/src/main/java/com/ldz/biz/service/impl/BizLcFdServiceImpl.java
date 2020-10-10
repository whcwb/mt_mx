package com.ldz.biz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizLcFdMapper;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.biz.service.BizLcFdService;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class BizLcFdServiceImpl extends BaseServiceImpl<BizLcFd, String> implements BizLcFdService {

	@Autowired
	private BizLcFdMapper baseMapper;
	@Autowired
	private BizLcJlService jlService;
	@Autowired
	private BizLcFdsService fdsService;
	@Autowired
	private BizLcWxjlService wxjlService;

	@Override
	protected Mapper<BizLcFd> getBaseMapper() {
		return baseMapper;
	}

	@Override
	public boolean fillPagerCondition(LimitedCondition condition) {
		String jlLX = getRequestParamterAsString("jlLx");
		if(StringUtils.isNotBlank(jlLX)){
			List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, jlLX);
			if(CollectionUtils.isEmpty(wxjls)){
				return false;
			}else {
				List<String> list = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
				condition.in(BizLcFd.InnerColumn.jlId, list);
			}
		}
		return true;
	}

	@Override
	public  void afterPager(PageInfo<BizLcFd> pageInfo){
		if(CollectionUtils.isEmpty(pageInfo.getList())){
			return;
		}
		List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, "00");
		List<String> collect = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
		List<BizLcFd> lcFds = pageInfo.getList();
		lcFds.forEach(bizLcFd -> {
			if(StringUtils.isNotBlank(bizLcFd.getLcId())){
				List<BizLcJl> jlList = jlService.findIn(BizLcJl.InnerColumn.id, Arrays.asList(bizLcFd.getLcId().split(",")));
				// 计算练车总费用
				int sum = jlList.stream().mapToInt(BizLcJl::getXjje).sum();
				int zsc = jlList.stream().mapToInt(BizLcJl::getSc).sum();

				bizLcFd.setLcFy(sum);
				bizLcFd.setSc(zsc);
				bizLcFd.setJlList(jlList);
				if(CollectionUtils.isNotEmpty(jlList)){
					String lckm = jlList.get(0).getLcKm();
					bizLcFd.setJlJx(jlList.get(0).getJlJx());
					bizLcFd.setLcKm(lckm);
				}
				int xysl = jlList.stream().mapToInt(BizLcJl::getXySl).sum();
				bizLcFd.setXySl(xysl);
				if(collect.contains(bizLcFd.getJlId())){
					bizLcFd.setJlLx("00");
				}else{
					bizLcFd.setJlLx("10");
				}
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
	public ApiResponse<BizLcFds> updateZt(String id, String bz) {
		RuntimeCheck.ifBlank(id, "请确认返点记录");
		SysYh yh = getCurrentUser();
		List<String> list = Arrays.asList(id.split(","));
		List<BizLcFd> fds = findByIds(list);

		RuntimeCheck.ifEmpty(fds, "此记录不需要返点");
		int size = fds.stream().map(BizLcFd::getJlId).collect(Collectors.toSet()).size();
		RuntimeCheck.ifTrue(size > 1, "所选记录为多个教练， 请重新选择");
		int lcFy = 0;
		int sc =0;
		AtomicReference<String> jx = new AtomicReference<>("");
		for (BizLcFd fd : fds) {
			String[] split = fd.getLcId().split(",");
			List<BizLcJl> jls = jlService.findIn(BizLcJl.InnerColumn.id, Arrays.asList(split));
			int sum = jls.stream().mapToInt(BizLcJl::getYfJe).sum();
			int s = jls.stream().mapToInt(BizLcJl::getSc).sum();
			sc+=s;
			lcFy+=sum;
			jls.forEach(bizLcJl -> {
				if(StringUtils.isBlank(jx.get())){
					jx.set(bizLcJl.getJlJx());
				}
				bizLcJl.setFdZt("10");
				jlService.update(bizLcJl);
			});
			if (fd.getFdsl() == null || fd.getFdsl() == 0) {
				fd.setFdsl(jls.size());
			}
			fd.setQrr(yh.getZh() + "-" + yh.getXm());
			fd.setQrsj(DateUtils.getNowTime());
			update(fd);
		}
		int sum = fds.stream().mapToInt(BizLcFd::getFdje).sum();

		BizLcFds lcFds = new BizLcFds();
		lcFds.setCjr(yh.getZh() + "-" + yh.getXm());
		lcFds.setCjsj(DateUtils.getNowTime());
		lcFds.setFdje(sum);
		lcFds.setFdlx(fds.get(0).getFdlx());
		lcFds.setFdsl(fds.size());
		if (CollectionUtils.size(fds) == 1) {
			lcFds.setId(list.get(0));
		} else {
			lcFds.setId(genId());
		}
		lcFds.setJlId(fds.get(0).getJlId());
		lcFds.setJlXm(fds.get(0).getJlXm());
		lcFds.setLcFy(lcFy);
		lcFds.setLcKm(fds.get(0).getLcKm());
		lcFds.setSc(sc);
		lcFds.setLcId(fds.stream().map(BizLcFd::getLcId).collect(Collectors.joining(",")));
		lcFds.setBz(bz);
		lcFds.setQrr(lcFds.getCjr());
		lcFds.setQrsj(lcFds.getCjsj());
		lcFds.setJx(jx.get());
		fdsService.save(lcFds);
		return ApiResponse.success(lcFds);
	}

	@Override
	public ApiResponse<String> getPj(String id) {
		RuntimeCheck.ifBlank(id , "请选择要打印的返点记录");
		List<String> list = Arrays.asList(id.split(","));
		if(list.size() == 1){
			return ApiResponse.success(list.get(0));
		}else{
			return ApiResponse.success(genId());
		}
	}

	@Override
	public ApiResponse<String> getPager(Page<BizLcFd> pager) {
		LimitedCondition condition = getQueryCondition();
		ApiResponse<String> res = new ApiResponse<>();
		String jlLx = getRequestParamterAsString("jlLx");
		if(StringUtils.isNotBlank(jlLx)){
			List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, jlLx);
			if(CollectionUtils.isEmpty(wxjls)){
				res.setResult("0");
				return res;
			}else {
				List<String> list = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
				condition.in(BizLcFd.InnerColumn.jlId, list);
			}
		}
		PageInfo<BizLcFd> pageInfo = findPage(pager, condition);
		List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, "00");
		List<String> collect = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
		List<BizLcFd> lcFds = pageInfo.getList();

		lcFds.forEach(bizLcFd -> {
			if(StringUtils.isNotBlank(bizLcFd.getLcId())){
				List<BizLcJl> jlList = jlService.findIn(BizLcJl.InnerColumn.id, Arrays.asList(bizLcFd.getLcId().split(",")));
				// 计算练车总费用
				int sum = jlList.stream().mapToInt(BizLcJl::getXjje).sum();
				int zsc = jlList.stream().mapToInt(BizLcJl::getSc).sum();

				bizLcFd.setLcFy(sum);
				bizLcFd.setSc(zsc);
				bizLcFd.setJlList(jlList);
				if(CollectionUtils.isNotEmpty(jlList)){
					String lckm = jlList.get(0).getLcKm();
					bizLcFd.setJlJx(jlList.get(0).getJlJx());
					bizLcFd.setLcKm(lckm);
				}
				int xysl = jlList.stream().mapToInt(BizLcJl::getXySl).sum();
				bizLcFd.setXySl(xysl);
				if(collect.contains(bizLcFd.getJlId())){
					bizLcFd.setJlLx("00");
				}else{
					bizLcFd.setJlLx("10");
				}
			}
		});
		List<BizLcFd> fds = findByCondition(condition);
		int sum = fds.stream().mapToInt(BizLcFd::getFdje).sum();
		res.setPage(pageInfo);
		res.setResult(sum+"");
		return res;
	}


}