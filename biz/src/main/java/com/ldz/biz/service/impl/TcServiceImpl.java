package com.ldz.biz.service.impl;

import com.ldz.biz.mapper.TcMapper;
import com.ldz.biz.model.BizLcTc;
import com.ldz.biz.service.TcService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.model.SysYh;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.commonUtil.DateUtils;
import com.ldz.util.exception.RuntimeCheck;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.Arrays;

@Service
public class TcServiceImpl extends BaseServiceImpl<BizLcTc, String> implements TcService {

    @Autowired
    private TcMapper mapper;

    @Override
    protected Mapper<BizLcTc> getBaseMapper() {
        return mapper;
    }

    @Override
    public ApiResponse<String> saveEntity(BizLcTc entity) {
        // 保存套餐信息
        RuntimeCheck.ifBlank(entity.getTcMc(), "套餐名称不能为空");
        RuntimeCheck.ifBlank(entity.getTcDm(), "套餐代码不能为空");
        RuntimeCheck.ifFalse(entity.getTcDj() != null && entity.getTcDj() > 0, "套餐单价不能为空");
        RuntimeCheck.ifFalse(entity.getTcSj() != null && entity.getTcSj() > 0, "套餐时长不能为空");
        RuntimeCheck.ifBlank(entity.getTcKm(), "套餐所属科目不能为空");
        RuntimeCheck.ifFalse(StringUtils.isNotBlank(entity.getTcSk()) && Arrays.asList("0", "1").contains(entity.getTcSk()), "启用刷卡参数异常");
        RuntimeCheck.ifFalse(StringUtils.isNotBlank(entity.getTcDh()) && Arrays.asList("0", "1").contains(entity.getTcDh()), "启用点火参数异常");
        RuntimeCheck.ifFalse(entity.getTcFd() != null && entity.getTcFd() > 0 && entity.getTcFd() < 1, "返点率设置异常");

        SysYh user = getCurrentUser(true);
        entity.setId(genId());
        entity.setCjr(user.getXm() + "-" + user.getZh());
        entity.setCjsj(DateUtils.getNowTime());
        save(entity);
        return ApiResponse.saveSuccess();
    }

    @Override
    public ApiResponse<String> updateEntity(BizLcTc entity) {
        RuntimeCheck.ifBlank(entity.getTcMc(), "套餐名称不能为空");
        RuntimeCheck.ifBlank(entity.getTcDm(), "套餐代码不能为空");
        RuntimeCheck.ifFalse(entity.getTcDj() != null && entity.getTcDj() > 0, "套餐单价不能为空");
        RuntimeCheck.ifFalse(entity.getTcSj() != null && entity.getTcSj() > 0, "套餐时长不能为空");
        RuntimeCheck.ifBlank(entity.getTcKm(), "套餐所属科目不能为空");
        RuntimeCheck.ifFalse(StringUtils.isNotBlank(entity.getTcSk()) && Arrays.asList("0", "1").contains(entity.getTcSk()), "启用刷卡参数异常");
        RuntimeCheck.ifFalse(StringUtils.isNotBlank(entity.getTcDh()) && Arrays.asList("0", "1").contains(entity.getTcDh()), "启用点火参数异常");
        RuntimeCheck.ifFalse(entity.getTcFd() != null && entity.getTcFd() > 0 && entity.getTcFd() < 1, "返点率设置异常");
        update(entity);
        return ApiResponse.updateSuccess();
    }


}
