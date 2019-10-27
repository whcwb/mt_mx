package com.ldz.biz.service;

import com.ldz.biz.model.CoachManagement;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;

public interface CoachManagementService extends BaseService<CoachManagement, String> {

    ApiResponse<String> updateEntity(CoachManagement entity);

    ApiResponse<String> deleteEntity(String id);

    void updateCoachTraineeCount();

}