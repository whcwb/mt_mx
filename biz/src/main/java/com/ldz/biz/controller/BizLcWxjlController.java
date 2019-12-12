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
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ApiResponse<String> save(BizLcWxjl entity) {
        return service.saveEntity(entity);
    }

    /**
     * 获取教练列表
     *
     * @param page
     * @return
     */
    @GetMapping("/getWxJl")
    public ApiResponse<String> getWxjl(Page<BizLcWxjl> page) {
        return service.getWxjl(page);
    }

    /**
     * 绑定卡号
     *
     * @param id
     * @return
     */
    @PostMapping("/bindCardNo")
    public ApiResponse<String> bindCarNo(String id) {
        return service.bindCardNo(id);
    }

    /**
     * 修改密码
     *
     * @param cardNo
     * @param old
     * @param newPwd
     * @param newPwd1
     * @return
     */
    @PostMapping("/updatePwd")
    public ApiResponse<String> updatePwd(String cardNo, String old, String newPwd, String newPwd1) {
        return service.updatePwd(cardNo, old, newPwd, newPwd1);
    }

    /**
     * 重置密码
     *
     * @param cardNo
     * @return
     */
    @PostMapping("/resetPwd")
    public ApiResponse<String> resetPwd(String cardNo) {
        return service.resetPwd(cardNo);
    }

    /**
     * 充值明细
     *
     * @param pageNum
     * @param pageSize
     * @param id
     * @param lx
     * @return
     */
    @PostMapping("/czmx")
    public ApiResponse<String> czmx(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "8") int pageSize, String id, String lx) {
        return service.czmx(pageNum, pageSize, id, lx);
    }

    /**
     * 获取票据编号(打印用)
     *
     * @return
     */
    @PostMapping("/getPjbh")
    public ApiResponse<String> getPjbh() {
        return service.getPjbh();
    }

    /**
     * 使用票据编号后保存
     *
     * @param id
     * @param pjbh
     * @return
     */
    @PostMapping("/savePjbh")
    public ApiResponse<String> savePjbh(String id, String pjbh) {
        return service.savePjbh(id, pjbh);
    }

    /**
     * 更新教练信息
     *
     * @param entity
     * @return
     */
    @Override
    @PostMapping("/update")
    public ApiResponse<String> update(BizLcWxjl entity) {
        return service.updateEntity(entity);
    }

    /**
     * 删除教练信息
     *
     * @param id
     * @return
     */
    @Override
    @PostMapping("/remove/{pkid}")
    public ApiResponse<String> remove(@PathVariable("pkid") String id) {
        return service.removeEntity(id);
    }

    /**
     * 解绑卡号
     *
     * @param id
     * @return
     */
    @PostMapping("/unbindCardNo")
    public ApiResponse<String> unbindCardNo(String id) {
        return service.unbindCardNo(id);
    }

    @PostMapping("/updateZhye")
    public ApiResponse<String> updateZhye(String id) {
        return service.updateZhye(id);
    }


}