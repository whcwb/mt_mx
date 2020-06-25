package com.ldz.biz.service;

import com.ldz.biz.model.BizLcTc;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

public interface TcService extends BaseService<BizLcTc, String> {
    ApiResponse<String> saveEntity(BizLcTc entity);

    ApiResponse<String> updateEntity(BizLcTc entity);
}
