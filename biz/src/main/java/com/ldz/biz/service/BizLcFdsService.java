package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcFds;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

public interface BizLcFdsService extends BaseService<BizLcFds,String> {
    ApiResponse<String> getPager(Page<BizLcFds> pager);
}
