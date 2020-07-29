package com.ldz.biz.controller;


import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcCl;
import com.ldz.biz.service.BizLcClService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * 练车车辆相关接口
 */
@RestController
@RequestMapping("/api/lccl")
public class BizLcClController extends BaseController<BizLcCl, String> {
    @Autowired
    private BizLcClService service;

    @Override
    protected BaseService<BizLcCl, String> getBaseService() {
        return service;
    }

    /**
     * 保存车辆
     *
     * @param entity
     * @return
     */
    @Override
    @PostMapping("/save")
    public ApiResponse<String> save(BizLcCl entity) {
        return service.saveEntity(entity);
    }

    /**
     * 获取车辆列表
     *
     * @param page
     * @return
     * @throws ParseException
     */
    @PostMapping("/getCar")
    public ApiResponse<String> getCar(Page<BizLcCl> page) throws ParseException {
        return service.getCar(page);
    }

    /**
     * 更新车辆
     *
     * @param entity
     * @return
     */
    @Override
    @PostMapping("/update")
    public ApiResponse<String> update(BizLcCl entity) {
        return service.updateEntity(entity);
    }

    /**
     * 更改车辆绑定卡号
     *
     * @param id
     * @param cardNo
     * @param th
     * @return
     */
    @PostMapping("/updateCardNo")
    public ApiResponse<String> updateCardNo(String id, String cardNo, @RequestParam(required = false) String th) {
        return service.updateCardNo(id, cardNo, th);
    }

    /**
     * 根据卡号获取车辆信息
     *
     * @param cardNo
     * @param km
     * @return
     */
    @PostMapping("/carInfo")
    public ApiResponse<BizLcCl> getCarInfo(String cardNo, String km) {
        return service.getCarInfo(cardNo, km);
    }

}