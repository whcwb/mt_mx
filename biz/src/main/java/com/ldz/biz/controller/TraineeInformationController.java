package com.ldz.biz.controller;

import com.ldz.biz.model.TraineeInformation;
import com.ldz.biz.service.TraineeInformationService;
import com.ldz.sys.base.BaseController;
import com.ldz.sys.base.BaseService;
import com.ldz.sys.bean.TreeNode;
import com.ldz.sys.model.SysJg;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.service.JgService;
import com.ldz.util.bean.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 学生基本信息
 */
@RestController
@RequestMapping("/api/traineeinformation")
public class TraineeInformationController extends BaseController<TraineeInformation, String> {
    @Autowired
    private TraineeInformationService service;


    @Autowired
    private JgService jgService;

    @Override
    protected BaseService<TraineeInformation, String> getBaseService() {
        return service;
    }


    /**
     * 获取系统报名点(机构)接口 以目录树形式展现
     *
     * @return
     */
    @RequestMapping("/getCurrentOrgTree")
    public ApiResponse<List<TreeNode>> getCurrentOrgTree() {
        SysYh currentUser = getCurrentUser();
        String jgdms = currentUser.getJgdms();
        List<String> list = new ArrayList<>();
        list.add(currentUser.getJgdm());
        if (StringUtils.isNotBlank(jgdms)) {
            list.addAll(Arrays.asList(jgdms.split(",")));
        }
        List<SysJg> orgList = jgService.findAllSubOrg(list, null);
        List<TreeNode> orgNode = jgService.convertToTreeNodeList(orgList);
        List<TreeNode> orgTree = TreeNode.buildTree(orgNode);
        return ApiResponse.success(orgTree);
    }


}