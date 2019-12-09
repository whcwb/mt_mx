package com.ldz.biz.controller;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fds")
public class BizLcFdsController extends BaseController<BizLcFds,String> {

    @Autowired
    private BizLcFdsService service;

    @Override
    protected BaseService<BizLcFds, String> getBaseService() {
        return service;
    }

    @RequestMapping(value = "/getPager", method ={RequestMethod.POST,RequestMethod.GET})
    public ApiResponse<String> getPager(Page<BizLcFds> pager){
        return service.getPager(pager);
    }

}
