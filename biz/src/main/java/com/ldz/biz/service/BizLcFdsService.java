package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcFds;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import jxl.write.WriteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface BizLcFdsService extends BaseService<BizLcFds, String> {
    ApiResponse<String> getPager(Page<BizLcFds> pager);

    void downloadExcel(Page<BizLcFds> pager, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException;
}
