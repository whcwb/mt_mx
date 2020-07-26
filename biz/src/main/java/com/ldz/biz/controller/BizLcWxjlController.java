package com.ldz.biz.controller;

import com.github.pagehelper.Page;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.util.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lcwxjl")
public class BizLcWxjlController extends BaseController<BizLcWxjl, String> {
    @Autowired
    private BizLcWxjlService service;

    @Override
    protected BaseService<BizLcWxjl, String> getBaseService() {
        return service;
    }

    /**
     * 数据新增方法
     * 如果对数据要求高，请重写该方法或是不直接继承该类，防止数据泄露
     *
     * @param entity
     * @return
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ApiResponse<String> save(BizLcWxjl entity) {
        return service.saveEntity(entity);
    }


    @GetMapping("/getWxJl")
    public ApiResponse<String> getWxjl(Page<BizLcWxjl> page) {
        return service.getWxjl(page);
    }

    @PostMapping("/bindCardNo")
    public ApiResponse<String> bindCarNo(String id) {
        return service.bindCardNo(id);
    }

    @PostMapping("/updatePwd")
    public ApiResponse<String> updatePwd(String cardNo, String old, String newPwd, String newPwd1) {
        return service.updatePwd(cardNo, old, newPwd, newPwd1);
    }

    @PostMapping("/resetPwd")
    public ApiResponse<String> resetPwd(String cardNo) {
        return service.resetPwd(cardNo);
    }

    @PostMapping("/czmx")
    public ApiResponse<String> czmx(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "8") int pageSize, String id, String lx) {
        return service.czmx(pageNum, pageSize, id, lx);
    }

    @PostMapping("/getPjbh")
    public ApiResponse<String> getPjbh() {
        return service.getPjbh();
    }

    @PostMapping("/savePjbh")
    public ApiResponse<String> savePjbh(String id, String pjbh) {
        return service.savePjbh(id, pjbh);
    }

    @Override
    @PostMapping("/update")
    public ApiResponse<String> update(BizLcWxjl entity) {
        return service.updateEntity(entity);
    }

    @Override
    @PostMapping("/remove/{pkid}")
    public ApiResponse<String> remove(@PathVariable("pkid") String id) {
        return service.removeEntity(id);
    }

    @PostMapping("/unbindCardNo")
    public ApiResponse<String> unbindCardNo(String id) {
        return service.unbindCardNo(id);
    }


}