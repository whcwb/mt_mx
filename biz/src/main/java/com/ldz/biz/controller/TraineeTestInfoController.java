package com.ldz.biz.controller;

import com.ldz.biz.model.TraineeTestInfo;
import com.ldz.biz.service.TraineeTestInfoService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.commonUtil.ExcelReader;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学员考试信息
 */
@RestController
@RequestMapping("/api/traineetestinfo")
public class TraineeTestInfoController extends BaseController<TraineeTestInfo,String> {
    @Autowired
    private TraineeTestInfoService service;

    @Autowired
    private StringRedisTemplate redisDao;



    @Override
    protected BaseService<TraineeTestInfo, String> getBaseService() {
        return service;
    }


}