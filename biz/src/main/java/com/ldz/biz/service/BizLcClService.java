package com.ldz.biz.service;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcCl;
import com.ldz.sys.base.BaseService;
import com.ldz.sys.model.SysJg;
import com.ldz.util.bean.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

public interface BizLcClService extends BaseService<BizLcCl, String> {

    ApiResponse<String> saveEntity(BizLcCl entity);

    ApiResponse<String> getCar(Page<BizLcCl> page) throws ParseException;

    /**
     * 清除车辆分配数据
     * @return
     */
    ApiResponse<String> clearCarAllot();

    ApiResponse<String> updateEntity(BizLcCl entity);

    void clearClZt();

    ApiResponse<String> updateCardNo(String id, String cardNo, String th);

    ApiResponse<BizLcCl> getCarInfo(String cardNo, String km);

    ApiResponse<List<SysJg>> getJgsByOrgCode(HttpServletRequest request);

    ApiResponse<List<SysJg>> getNextJg(String jgdm);
}