package com.ldz.biz.controller;

import com.ldz.biz.model.BizLcTc;
import com.ldz.biz.service.TcService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tc")
public class TcController extends BaseController<BizLcTc, String> {

    @Autowired
    private TcService service;

    @Override
    protected BaseService<BizLcTc, String> getBaseService() {
        return service;
    }

    @PostMapping("/save")
    @Override
    public ApiResponse<String> save(BizLcTc entity) {
        return service.saveEntity(entity);
    }

    @PostMapping("/update")
    public ApiResponse<String> update(BizLcTc entity) {
        return service.updateEntity(entity);
    }

}
