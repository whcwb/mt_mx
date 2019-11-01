package com.ldz.biz.service;

import com.ldz.biz.model.BizLcFd;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

public interface BizLcFdService extends BaseService<BizLcFd, String> {

    ApiResponse<String> updateZt(String id);
}