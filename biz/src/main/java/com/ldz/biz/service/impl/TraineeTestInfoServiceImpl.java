package com.ldz.biz.service.impl;

import com.google.common.collect.Maps;
import com.ldz.biz.mapper.TraineeTestInfoMapper;
import com.ldz.biz.model.*;
import com.ldz.biz.service.*;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.model.SysMessage;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.service.SysMessageService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.commonUtil.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.common.Mapper;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TraineeTestInfoServiceImpl extends BaseServiceImpl<TraineeTestInfo, String> implements TraineeTestInfoService {

    @Autowired
    private StringRedisTemplate redisDao;
    @Autowired
    private TraineeTestInfoMapper baseMapper;
    @Autowired
    private TraineeInformationService traineeInformationService;//学员列表
    @Autowired
    private TraineeStatusService traineeStatusService;//学员状态表

    @Autowired
    private CoachManagementService coachManagementService;//教练表


    @Autowired
    private SysMessageService messageService;//消息下发

    @Override
    protected Mapper<TraineeTestInfo> getBaseMapper() {
        return baseMapper;
    }


    @Override
    public List<TraineeTestInfo> getInfo(String jgdm, String startTime, String endTime, String km) {
        return baseMapper.getInfo(jgdm, startTime, endTime, km);
    }


    public String appendZero(String data) {

        String[] split = data.split("-");
        if (split.length < 3) {
            return data;
        }

        if (split[1].length() < 2) {
            split[1] = "0" + split[1];
        }

        if (split[2].length() < 2) {
            split[2] = "0" + split[2];
        }

        return split[0] + "-" + split[1] + "-" + split[2];
    }



}