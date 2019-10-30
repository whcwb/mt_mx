package com.ldz.biz.service.impl;

import com.ldz.biz.mapper.TraineeInformationMapper;
import com.ldz.biz.model.TraineeInformation;
import com.ldz.biz.service.TraineeInformationService;
import com.ldz.sys.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

@Service
public class TraineeInformationServiceImpl extends BaseServiceImpl<TraineeInformation, String> implements TraineeInformationService {

    @Autowired
    private TraineeInformationMapper baseMapper;


    //	学员图片地址  学员图片地址 headImg--头像   elecSign--电子签名  cardFront-身份证正面  cardBack-身份证反面
    @Value("${staticPath}")
    private String traineeImgFileUrl;
    @Value("${qqj.time}")
    private String time;
    @Value("${qqj.rs}")
    private Integer rs;
    @Value("${qqj.jg}")
    private Integer qqJg;

    @Override
    protected Mapper<TraineeInformation> getBaseMapper() {
        return baseMapper;
    }





}