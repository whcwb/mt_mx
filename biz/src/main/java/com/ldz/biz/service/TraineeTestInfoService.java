package com.ldz.biz.service;

import com.ldz.biz.model.TraineeInformation;
import com.ldz.biz.model.TraineeTestInfo;
import com.ldz.sys.base.BaseService;
import com.ldz.sys.model.SysYh;
import com.ldz.util.bean.ApiResponse;

import java.util.List;
import java.util.Map;

public interface TraineeTestInfoService extends BaseService<TraineeTestInfo, String> {

    List<TraineeTestInfo> getInfo(String jgdm, String startTime, String endTime, String km);

}