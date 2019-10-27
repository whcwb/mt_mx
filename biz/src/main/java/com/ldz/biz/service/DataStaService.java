package com.ldz.biz.service;

import com.ldz.biz.model.StudentAllModel;
import com.ldz.util.bean.ApiResponse;

import java.text.ParseException;
import java.util.List;

public interface DataStaService {

    ApiResponse<List<StudentAllModel>> getAllStudentCount(String startTime, String endTime, String jgdm, String[] lx) throws ParseException;

    ApiResponse<List<StudentAllModel>> getStudentCount(String startTime, String endTime, String jgdm, String[] lx) throws ParseException;

}
