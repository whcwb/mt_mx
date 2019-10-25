package com.ldz.biz.service;

import com.ldz.biz.model.BizExceptionConfig;
import com.ldz.sys.base.BaseService;

public interface BizExceptionConfigService extends BaseService<BizExceptionConfig, Long> {

	public String getExpNameByCode(String code);
}