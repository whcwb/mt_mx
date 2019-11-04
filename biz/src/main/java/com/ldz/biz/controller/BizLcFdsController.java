package com.ldz.biz.controller;

import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
