package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface BizLcWxjlService extends BaseService<BizLcWxjl, String> {

    ApiResponse<String> saveEntity(BizLcWxjl entity);

    ApiResponse<String> getWxjl(Page<BizLcWxjl> page);

    ApiResponse<String> bindCardNo(String id);

    ApiResponse<String> updatePwd(String cardNo, String old, String newPwd, String newPwd1);

    ApiResponse<String> resetPwd(String cardNo);

    ApiResponse<String> czmx(int pageNum, int pageSize, String id, String lx);

    ApiResponse<String> getPjbh();

    ApiResponse<String> savePjbh(String id, String pjbh);

    ApiResponse<String> updateEntity(BizLcWxjl entity);

    ApiResponse<String> removeEntity(String id);

    ApiResponse<String> unbindCardNo(String id);

    void exportWxjl(HttpServletRequest request, HttpServletResponse response) throws IOException;

    ApiResponse<String> updateZhye(String id);
}