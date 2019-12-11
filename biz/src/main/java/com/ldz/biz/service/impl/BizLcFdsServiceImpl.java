package com.ldz.biz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizLcFdsMapper;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.biz.service.BizLcFdService;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.util.bean.ApiResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BizLcFdsServiceImpl extends BaseServiceImpl<BizLcFds,String> implements BizLcFdsService {

    @Autowired
    private BizLcFdsMapper baseMapper;
    @Autowired
    private BizLcFdService fdService;
    @Autowired
    private BizLcWxjlService wxjlService;
    @Override
    protected Mapper<BizLcFds> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public boolean fillPagerCondition(LimitedCondition condition) {
        String jlLx = getRequestParamterAsString("jlLx");
        if(StringUtils.isNotBlank(jlLx)){
            List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, jlLx);
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
    protected void afterPager(PageInfo<BizLcFds> resultPage) {
        List<BizLcFds> list = resultPage.getList();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(bizLcFds -> {
            String lcId = bizLcFds.getLcId();
            List<BizLcFd> fds = fdService.findByIds(Arrays.asList(lcId.split(",")));
            bizLcFds.setFds(fds);
        });
    }

    @Override
    public ApiResponse<String> getPager(Page<BizLcFds> pager) {
        LimitedCondition condition = getQueryCondition();
        String jlLx = getRequestParamterAsString("jlLx");
        ApiResponse<String> res = new ApiResponse<>();
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
        PageInfo<BizLcFds> pageInfo = findPage(pager, condition);
        List<BizLcFds> list = pageInfo.getList();
        list.forEach(bizLcFds -> {
            String lcId = bizLcFds.getLcId();
            List<BizLcFd> fds = fdService.findByIds(Arrays.asList(lcId.split(",")));
            bizLcFds.setFds(fds);
        });
        List<BizLcFds> fds = findByCondition(condition);
        int sum = fds.stream().mapToInt(BizLcFds::getFdje).sum();
        res.setResult("" + sum);
        res.setPage(pageInfo);
        return res;
    }
}
