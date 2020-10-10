package com.ldz.biz.controller;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import jxl.write.WriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 已返点相关接口
 */
@RestController
@RequestMapping("/api/fds")
public class BizLcFdsController extends BaseController<BizLcFds, String> {

    @Autowired
    private BizLcFdsService service;

    @Override
    protected BaseService<BizLcFds, String> getBaseService() {
        return service;
    }

    @RequestMapping(value = "/getPager", method = {RequestMethod.POST, RequestMethod.GET})
    public ApiResponse<String> getPager(Page<BizLcFds> pager) {
        return service.getPager(pager);
    }

    @GetMapping("/fdExcel")
    public void fdExcel(Page<BizLcFds> pager, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {
        pager.setPageNum(1);
        pager.setPageSize(99999);
        service.downloadExcel(pager, request, response);
    }
}
