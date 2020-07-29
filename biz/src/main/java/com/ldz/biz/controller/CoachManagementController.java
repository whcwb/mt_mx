package com.ldz.biz.controller;

import com.ldz.biz.model.CoachManagement;
import com.ldz.biz.service.CoachManagementService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教练员信息管理
 */
@RestController
@RequestMapping("/api/coachmanagement")
public class CoachManagementController extends BaseController<CoachManagement, String> {
    @Autowired
    private CoachManagementService service;

    @Override
    protected BaseService<CoachManagement, String> getBaseService() {
        return service;
    }

    @Override
    @PostMapping("/save")
    public ApiResponse<String> save(CoachManagement entity) {
        return service.validAndSave(entity);
    }

    @Override
    @PostMapping("/update")
    public ApiResponse<String> update(CoachManagement entity) {
        return service.updateEntity(entity);
    }

    @Override
    @PostMapping("/remove/{pkid}")
    public ApiResponse<String> remove(@PathVariable("pkid") String id) {
        return service.deleteEntity(id);
    }


}