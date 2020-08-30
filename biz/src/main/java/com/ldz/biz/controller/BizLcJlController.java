package com.ldz.biz.controller;


import com.github.pagehelper.Page;
import com.ldz.biz.model.BizJlCz;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.LcJlModel;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.sys.model.SysZdxm;
import com.ldz.util.bean.ApiResponse;
import jxl.write.WriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 练车记录接口
 */
@RestController
@RequestMapping("/api/lcjl")
public class BizLcJlController extends BaseController<BizLcJl, String> {
    @Autowired
    private BizLcJlService service;

    @Override
    protected BaseService<BizLcJl, String> getBaseService() {
        return service;
    }

    /**
     * 新增一条练车记录
     *
     * @param entity
     * @return
     */
    @Override
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ApiResponse<String> save(BizLcJl entity) {
        return service.saveEntity(entity);
    }

    /**
     * 手动结束一条练车记录
     */
    @PostMapping("/updateJssj")
    public ApiResponse<BizLcJl> updateJssj(String id, String cardNo, String km) throws ParseException {
        return service.updateJssj(id, cardNo, km);
    }

    /**
     * @return
     */
    @RequestMapping("/statistics")
    public ApiResponse<List<Map<String, Object>>> statistics() {
        return ApiResponse.success(service.statistics());
    }

    /**
     * 根据卡号查询最近的一条记录
     */
    @PostMapping("/getJl")
    public ApiResponse<BizLcJl> getJl(String cardNo) {
        return service.getJl(cardNo);
    }

    /**
     * 驾校统计
     *
     * @return
     */
    @RequestMapping("/jxtj")
    public ApiResponse<List<Map<String, Object>>> drivingSchoolStatistics() {
        return ApiResponse.success(service.drivingSchoolStatistics());
    }

    /**
     * 练车排队登记
     */
    @PostMapping("/saveJl")
    public ApiResponse<String> saveJl(BizLcJl entity, String appoint) {
        return service.saveJl(entity, appoint);
    }

    /**
     * 排队记录开始练车
     */
    @PostMapping("/kslc")
    public ApiResponse<String> kslc(BizLcJl entity) {
        return service.kslc(entity);
    }


    /**
     * 查询当前记录是否已经返点
     */
    @PostMapping("/getFdZt")
    public ApiResponse<BizLcJl> getFdZt(String id) {
        return service.getFdZt(id);
    }

    /**
     * 训练记录返点记录接口
     */
    @PostMapping("/updateFdZt")
    public ApiResponse<String> updateFdZt(String id) {
        return service.updateFdZt(id);
    }

    /**
     * 科目三模训 工作日志
     */
    @PostMapping("/getAllLog")
    public ApiResponse<List<LcJlModel>> getAllLog() {
        return service.getAllLog();
    }

    /**
     * 安全员工作日志
     */
    @PostMapping("/getOneLog")
    public ApiResponse<LcJlModel> getOneLog(String zgId) {
        return service.getOneLog(zgId);
    }

    /**
     * 教练训练统计
     */
    @PostMapping("/jlTj")
    public ApiResponse<List<LcJlModel>> getJlTj() {
        return service.getJlTj();
    }

    /**
     * 根据教练队号统计各项数据 (本校)
     */
    @PostMapping("/getDhTj")
    public ApiResponse<List<LcJlModel>> getDhTj() {
        return service.getDhTj();
    }


    @GetMapping("/exportDhTj")
    public void exportDhTj(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.exportDhTj(request, response);
    }

    @PostMapping("/updateXysl")
    public ApiResponse<String> updateXysl(String id, Integer xySl) {
        return service.updateXysl(id, xySl);
    }

    @PostMapping("/getLatestJl")
    public ApiResponse<BizLcJl> getLatestJl(String clId) {
        return service.getLatestJl(clId);
    }

    @PostMapping("/Tc")
    public ApiResponse<List<SysZdxm>> getTc(String km, String by5) {
        return service.getTc(km, by5);
    }

    @PostMapping("/cz")
    public ApiResponse<BizJlCz> saveCz(String id, int je, int sfje) {
        return service.saveCz(id, je, sfje);
    }

    //    @PostMapping("/pay")
    public ApiResponse<String> savePay(String id) {
        return service.savePay(id);
    }

    @PostMapping("/getBatchPay")
    public ApiResponse<BizLcJl> getBatchPay(String ids) {
        return service.getBatchPay(ids);
    }

    @PostMapping("/batchPay")
    public ApiResponse<String> saveBatchPay(String ids) {
        return service.saveBatch(ids);
    }

    /**
     * 根据打印凭证 返回需要打印的所有数据
     */
    @PostMapping("/getByPz")
    public ApiResponse<BizLcJl> getByPz(String pz) {
        return service.getByPz(pz);
    }

    /**
     * 支付接口 (可选择支付方式)
     *
     * @param id
     * @param zf
     * @return
     */
    @PostMapping("/payCNY")
    public ApiResponse<BizLcJl> payCNY(String id, String zf) {
        return service.payCNY(id, zf);
    }

    /**
     * 作废记录
     *
     * @param id
     * @return
     */
    @PostMapping("/revokeJl")
    public ApiResponse<String> revokeJl(String id) {
        return service.revokeJl(id);
    }

    /**
     * 获取需要预警的车辆编号
     *
     * @return
     */
    @PostMapping("/getCarEnd")
    public ApiResponse<String> getCarEnd() {
        return service.getCarEnd();
    }

    /**
     * 收支统计
     *
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/statisSec")
    public ApiResponse<List<String>> staticSec(String start, String end) {
        return service.statisSec(start, end);
    }

    /**
     * 主页统计
     *
     * @return
     */
    @PostMapping("/statisMain")
    public ApiResponse<Map<String, Integer>> statisMain() {
        return service.statisMain();
    }

    /**
     * 收支统计excel导出
     *
     * @param start
     * @param end
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping("/exportSec")
    public void exportSec(String start, String end, HttpServletRequest request, HttpServletResponse response) throws Exception {
        service.exportSec(start, end, request, response);
    }

    /**
     * 导出科目三模训明细excel
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportKm3")
    public void exportKm3(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.exportKm3(request, response);
    }

    /**
     * 查询当前教练的开放日 套餐单价是多少
     *
     * @return
     */
    @GetMapping("/getKfDj")
    public ApiResponse<String> getKfDj(String jlId) {
        return service.getKfDj(jlId);
    }


    /**
     * 明细统计 Excel导出(科二)
     */
    @GetMapping("/pagerExcel")
    public void pagerExcel(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.pagerExcel(page, request, response);
    }

    /**
     * 驾校统计 Excel 导出
     */
    @GetMapping("/jxtjExcel")
    public void jxtjExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.jxtjExcel(request, response);
    }

    /**
     * 教练统计 Excel 导出
     */
    @GetMapping("/jlExcel")
    public void jlExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.jlExcel(request, response);

    }

    /**
     * 明细统计 Exclel导出 (科三)
     */
    @GetMapping("/pagerExcelK3")
    public void pagerExcelK3(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.pagerExcelK3(page, request, response);
    }

    /**
     * 科三安全员日志 Excel导出
     */
    @GetMapping("/aqyExcel")
    public void aqyExcel(HttpServletRequest request, HttpServletResponse response) throws WriteException, IOException {
        service.aqyExcel(request, response);
    }

    @GetMapping("/pagerExcelAll")
    public void pagerExcelAll(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.pagerExcelAll(page, request, response);
    }

    /**
     * 新增练车套餐
     *
     * @param zdxm
     * @return
     */
    @PostMapping("/saveTc")
    public ApiResponse<String> saveTc(SysZdxm zdxm) {
        return service.saveTc(zdxm);
    }

    @GetMapping("/removetc/{id}")
    public ApiResponse<String> removeTc(@PathVariable("id") String id) {
        return service.removeTc(id);
    }

    @GetMapping("/exportXymx")
    public void exportXymx(Page<BizLcJl> page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.exportXymx(page, request, response);
    }

}