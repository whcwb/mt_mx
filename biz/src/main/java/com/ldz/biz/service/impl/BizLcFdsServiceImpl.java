package com.ldz.biz.service.impl;

import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizLcFdsMapper;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.service.BizLcFdService;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.sys.base.BaseServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.Arrays;
import java.util.List;

@Service
public class BizLcFdsServiceImpl extends BaseServiceImpl<BizLcFds,String> implements BizLcFdsService {

    @Autowired
    private BizLcFdsMapper baseMapper;
    @Autowired
    private BizLcFdService fdService;

    @Override
    protected Mapper<BizLcFds> getBaseMapper() {
        return baseMapper;
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
}
