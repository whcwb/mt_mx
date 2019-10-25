package com.ldz.biz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.ldz.biz.constant.FeeType;
import com.ldz.biz.constant.Status;
import com.ldz.biz.mapper.TraineeInformationMapper;
import com.ldz.biz.mapper.TraineeTestInfoMapper;
import com.ldz.biz.model.*;
import com.ldz.biz.service.*;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.sys.model.SysJg;
import com.ldz.sys.model.SysMessage;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.service.JgService;
import com.ldz.sys.service.SysMessageService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.*;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TraineeInformationServiceImpl extends BaseServiceImpl<TraineeInformation, String> implements TraineeInformationService {

    @Autowired
    private StringRedisTemplate redisDao;
    @Autowired
    private ZgjbxxService zgjbxxService;
    @Autowired
    private TraineeInformationMapper baseMapper;
    @Autowired
    private JgService jgService;

    @Autowired
    private TraineeTestInfoService traineeTestInfoService;

    @Autowired
    private ChargeManagementService chargeManagementService;
    @Autowired // 学员状态表
    private TraineeStatusService traineeStatusService;
    @Autowired
    private TraineeTestInfoMapper testInfoMapper;


    //	学员图片地址  学员图片地址 headImg--头像   elecSign--电子签名  cardFront-身份证正面  cardBack-身份证反面
    @Value("${staticPath}")
    private String traineeImgFileUrl;
    @Value("${qqj.time}")
    private String time;
    @Value("${qqj.rs}")
    private Integer rs;
    @Value("${qqj.jg}")
    private Integer qqJg;
    @Autowired
    private SysMessageService messageService;//消息下发
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Override
    protected Mapper<TraineeInformation> getBaseMapper() {
        return baseMapper;
    }





}