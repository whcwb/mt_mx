package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

public interface BizLcWxjlService extends BaseService<BizLcWxjl, String> {

    ApiResponse<String> saveEntity(BizLcWxjl entity);

    ApiResponse<String> getWxjl(Page<BizLcWxjl> page);

    ApiResponse<String> bindCardNo(String id);

    ApiResponse<String> updatePwd(String cardNo, String old, String newPwd, String newPwd1);

    ApiResponse<String> resetPwd(String cardNo);

    ApiResponse<String> czmx(int pageNum, int pageSize, String id);
}