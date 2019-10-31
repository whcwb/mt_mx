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
     * @param entity
     * @return
     */
    @RequestMapping(value="/save", method={RequestMethod.POST})
    public ApiResponse<String> save(BizLcWxjl entity){
        return service.saveEntity(entity);
    }


    @GetMapping("/getWxJl")
    public ApiResponse<String> getWxjl(Page<BizLcWxjl> page){
        return service.getWxjl(page);
    }

    @PostMapping("/bindCardNo")
    public ApiResponse<String> bindCarNo(String id){
        return service.bindCardNo(id);
    }

    @PostMapping("/updatePwd")
    public ApiResponse<String> updatePwd(String cardNo , String old, String newPwd, String newPwd1){
        return service.updatePwd(cardNo, old, newPwd, newPwd1);
    }

    @PostMapping("/resetPwd")
    public ApiResponse<String> resetPwd(String cardNo){
        return service.resetPwd(cardNo);
    }
}