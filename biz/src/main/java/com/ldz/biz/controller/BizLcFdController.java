package com.ldz.biz.controller;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.service.BizLcFdService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bizlcfd")
public class BizLcFdController extends BaseController<BizLcFd, String> {
    @Autowired
    private BizLcFdService service;

    @Override
    protected BaseService<BizLcFd, String> getBaseService() {
        return service;
    }

    @PostMapping("/updateZt")
    public ApiResponse<BizLcFds> updateZt(String id, String bz){
        return service.updateZt(id, bz);
    }

    @PostMapping("/getPj")
    public ApiResponse<String> getPj(String id){
        return service.getPj(id);
    }

    @RequestMapping(value="/getPager", method={RequestMethod.POST, RequestMethod.GET})
    public ApiResponse<String> getPager(Page<BizLcFd> pager) {
        return service.getPager(pager);
    }


}