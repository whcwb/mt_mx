package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizJlCz;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.LcJlModel;
import com.ldz.sys.base.BaseService;
import com.ldz.sys.model.SysZdxm;
import com.ldz.util.bean.ApiResponse;
import jxl.write.WriteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    ApiResponse<String> revokeJl(String id);

    ApiResponse<String> getCarEnd();

    void exportXymx(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException;

    ApiResponse<List<String>> statisSec(String start, String end);

    ApiResponse<Map<String, Integer>> statisMain();

    void exportSec(String start, String end, HttpServletRequest request, HttpServletResponse response) throws WriteException, IOException;

    void exportKm3(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void pagerExcelAll(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException;

    ApiResponse<Integer> getKfDj(String jlId);
}