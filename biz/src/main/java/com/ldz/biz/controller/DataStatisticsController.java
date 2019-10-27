package com.ldz.biz.controller;


import com.ldz.biz.model.StudentAllModel;
import com.ldz.biz.service.DataStaService;
import com.ldz.util.bean.ApiResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationPid;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 数据统计接口
 */
@RestController
@RequestMapping("/api/data")
public class DataStatisticsController {

    @Autowired
    private DataStaService service;




    /**
     * 统计各个报名点， 年的招生人数
     */
    @PostMapping("/getAllStudentCount")
    public ApiResponse<List<StudentAllModel>> getAllStudentCount(String startTime, String endTime, @RequestParam(required = false) String jgdm, @RequestParam(required = false)String[] lx) throws ParseException {
        return service.getAllStudentCount(startTime, endTime, jgdm, lx);
    }

    /**
     * 统计各个报名点，某日的招生人数
     */
    @PostMapping("/getStudentCount")
    public ApiResponse<List<StudentAllModel>> getStudentCount(String startTime, String endTime, @RequestParam(required = false) String jgdm, @RequestParam(required = false)String[] lx) throws ParseException {
        return service.getStudentCount(startTime, endTime, jgdm, lx);
    }

}
