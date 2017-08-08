/**
 * Project Name:biz-monitor-provider
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月8日下午8:10:11
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;

/**
 * ClassName:MonitorTreeOrderController 临时单据类
 * Date:     2017年8月8日 下午8:10:11
 * @author   lijie
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/monitor")
public class MonitorTreeOrderController {
    
    @Autowired
    private MonitorTreeOrderService service;
    
    @RequestMapping(value="/queryNotUsingResource", method = RequestMethod.GET)
    public Result queryNotUsingResource(@RequestParam String orderId, @RequestParam String nodeId, @RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue="20") int pageSize){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        if(JsonUtil.isEmpity(orderId) || JsonUtil.isEmpity(nodeId))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(),ErrorMessage._60004.getMsg());

        MonitorTreeOrderTo order = service.queryOrder(orderId);
        NodeTo node = service.queryNode(nodeId);

        if(JsonUtil.isEmpity(order) || JsonUtil.isEmpity(node))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
        
        // 取到变更监管树类
        String mtor003 = order.getMtor003();
        
        // TODO 根据变更监管树类查询到 监管树的从属资源类
        
        // TODO 根据从属资源类 查询到其所有的资源类信息(全量信息) - 集合1
        
        // 查询已被节点使用的资源信息 - 集合2
        List<String> usedResourceIds = service.queryTreeNodeResource(orderId, node.getMtor011(), node.getMtor012(),null);
        
        // TODO 从全量中筛选出 符合条件的未被选择的资源集合 - 集合3 手动分页 返回给前端
        
        result.setData(null);
        
        return result;
    }
    
    @RequestMapping(value="/checkNodeResource", method = RequestMethod.GET)
    public Result checkNodeResource(@RequestParam String nodeId, @RequestParam String startDate, @RequestParam String endDate){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        if(JsonUtil.isEmpity(nodeId) || JsonUtil.isEmpity(startDate) || JsonUtil.isEmpity(endDate) )
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(),ErrorMessage._60004.getMsg());

        NodeTo node = service.queryNode(nodeId);
        if(JsonUtil.isEmpity(node) || JsonUtil.isEmpity(node.getPid()))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
    
        // 查询当前节点已经拥有的资源 - 集合1
       List<ResourceTo> nodeResources = service.queryResource(nodeId);
       
       if(JsonUtil.isEmpity(nodeResources))
           return result;
        
        // 查询已被节点使用的资源信息 - 集合2
        List<String> usedResourceIds = service.queryTreeNodeResource(node.getPid(), node.getMtor011(), node.getMtor012(), nodeId);
        if(JsonUtil.isEmpity(usedResourceIds))
            return result;
       
        // 查询  集合1 和 集合2 的id是否有重合部分
        for(ResourceTo to : nodeResources){
            if(usedResourceIds.contains(to.getMtor020()))
                ApplicationException.throwCodeMesg(ErrorMessage._60006.getCode(),ErrorMessage._60006.getMsg());
        }
        return result;
    }
}

