package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizJlCz;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.LcJlModel;
import com.ldz.sys.base.BaseService;
import com.ldz.sys.model.SysZdxm;
import com.ldz.util.bean.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface BizLcJlService extends BaseService<BizLcJl, String> {

    ApiResponse<String> saveEntity(BizLcJl entity);

    List<Map<String,Object>> statistics();

    ApiResponse<BizLcJl> getJl(String cardNo);

    ApiResponse<String> saveJl(BizLcJl entity, String appoint);

    ApiResponse<String> kslc(BizLcJl entity);

    ApiResponse<String> updateFdZt(String id);

    ApiResponse<BizLcJl> getFdZt(String id);

    ApiResponse<List<LcJlModel>> getAllLog();

    ApiResponse<LcJlModel> getOneLog(String zgId);

    ApiResponse<BizLcJl> updateJssj(String id,String cardNo, String km) throws ParseException;

    List<Map<String,Object>>  drivingSchoolStatistics();

    ApiResponse<List<LcJlModel>> getJlTj();

    void pagerExcel(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void pagerExcelK3(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException;

    ApiResponse<String> updateXysl(String id, Integer xySl);

    ApiResponse<BizLcJl> getLatestJl(String id);

    ApiResponse<List<SysZdxm>> getTc(String km, String carType);

    ApiResponse<BizJlCz> saveCz(String no, int je, int sfje);

    ApiResponse<String> savePay(String id);

    ApiResponse<BizLcJl> getBatchPay(String ids);

    ApiResponse<String> saveBatch(String ids);

    ApiResponse<BizLcJl> getByPz(String pz);

    ApiResponse<BizLcJl> payCNY(String id, String zf);
}