package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcFds;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

public interface BizLcFdService extends BaseService<BizLcFd, String> {

    ApiResponse<BizLcFds> updateZt(String id, String bz);

    ApiResponse<String> getPj(String id);

    ApiResponse<String> getPager(Page<BizLcFd> pager);


}