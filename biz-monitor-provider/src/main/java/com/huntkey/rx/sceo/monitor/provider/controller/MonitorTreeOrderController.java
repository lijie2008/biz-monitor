/**
 * Project Name:biz-monitor-provider
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月8日下午8:10:11
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.PersistanceConstant;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
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
    
    @Autowired
    private MonitorService mService;
    
    /**
     * 
     * queryNotUsingResource: 查询节点未使用的资源信息
     * @author lijie
     * @param orderId 临时单ID
     * @param nodeId 节点ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return
     */
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
        
        String mtor003 = order.getMtor003();
        EdmClassTo edmClass = service.getEdmClass(mtor003, PersistanceConstant.EDMPCODE);
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        String resourceEdmName = edmClass.getEdmcNameEn();
        JSONArray resources = service.getAllResource(resourceEdmName);
        if(JsonUtil.isEmpity(resources))
            return result;
        
        List<String> usedResourceIds = service.queryTreeNodeUsingResource(orderId, node.getMtor011(), node.getMtor012(),null);
        
        List<Object> datas = resources.parallelStream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(PersistanceConstant.ID)))
                .collect(Collectors.toList());
        
        int totalSize = datas.size();
        
        JSONObject obj = new JSONObject();
        obj.put("totalSize", totalSize);
        obj.put("data", totalSize == 0 ? null : datas.subList((currentPage-1)*pageSize < 0 ? 0 : (currentPage-1)*pageSize > totalSize?totalSize:(currentPage-1)*pageSize, (currentPage*pageSize)>totalSize?totalSize:currentPage*pageSize));
        result.setData(obj);
        return result;
    }
    
    /**
     * 
     * checkNodeResource: 节点时间区间修改检查
     * @author lijie
     * @param nodeId 节点ID
     * @param startDate 生效时间
     * @param endDate 失效时间
     * @return
     */
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
        List<String> usedResourceIds = service.queryTreeNodeUsingResource(node.getPid(), startDate, endDate, nodeId);
        if(JsonUtil.isEmpity(usedResourceIds))
            return result;
       
        // 查询  集合1 和 集合2 的id是否有重合部分
        if(nodeResources.parallelStream().anyMatch(re -> usedResourceIds.contains(re.getMtor020())))
            ApplicationException.throwCodeMesg(ErrorMessage._60006.getCode(),ErrorMessage._60006.getMsg());
        return result;
    }
    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/addOtherNode", method = RequestMethod.GET)
    public Result addOtherNode(@RequestParam String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        if(JsonUtil.isEmpity(orderId))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(),ErrorMessage._60004.getMsg());
        // 查询未分配资源集合
        MonitorTreeOrderTo order = service.queryOrder(orderId);
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
        String mtor003 = order.getMtor003();
        EdmClassTo edmClass = service.getEdmClass(mtor003, PersistanceConstant.EDMPCODE);
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        String resourceEdmName = edmClass.getEdmcNameEn();
        JSONArray resources = service.getAllResource(resourceEdmName);
        if(JsonUtil.isEmpity(resources))
            return result;
        List<String> usedResourceIds = service.queryTreeNodeUsingResource(orderId, null, null,null);
        List<Object> datas = resources.parallelStream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(PersistanceConstant.ID)))
                .collect(Collectors.toList());
        if(datas.size() == 0)
            return result;

        // 查询根节点 和 最后一个子节点
        NodeTo rootNode = service.queryRootNode(orderId);
        if(JsonUtil.isEmpity(rootNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"根节点"+ErrorMessage._60005.getMsg());
        NodeTo lastRootChildNode = service.queryRootChildrenNode(orderId, rootNode.getId());
        
        // 创建其他节点
        String nodeId = null;
        if(JsonUtil.isEmpity(lastRootChildNode)){
            nodeId = mService.addNode(rootNode.getId(),"0");
        }else{
            nodeId = mService.addNode(lastRootChildNode.getId(),"2");
        }
        mService.addResource(nodeId, JsonUtil.getList(datas, NodeTo.class).parallelStream().map(NodeTo::getId).collect(Collectors.toList()).stream().toArray(String[]::new));
        return result;
    }
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/store", method = RequestMethod.GET)
    public Result store(@RequestParam String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        if(JsonUtil.isEmpity(orderId))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(),ErrorMessage._60004.getMsg());
        
        MonitorTreeOrderTo order = service.queryOrder(orderId);
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单" + ErrorMessage._60005.getMsg());
        
        // 根据EDM 取出对应的目标监管类
        String mtor003 = order.getMtor003();
        String edmCname = service.queryEdmClassName(mtor003);
        if(JsonUtil.isEmpity(edmCname))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        // 节点信息
        List<NodeTo> treeNodes = service.queryTreeNode(orderId);
        if(JsonUtil.isEmpity(treeNodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"节点" + ErrorMessage._60005.getMsg());
        
        // 资源信息
        List<ResourceTo> resources = service.queryTreeNodeResource(orderId, null, null, null);
        Map<String, List<ResourceTo>> groupResource = resources.stream().collect(Collectors.groupingBy(ResourceTo::getPid));
        
        List<NodeDetailTo> nodes = new ArrayList<NodeDetailTo>();
        treeNodes.stream().forEach(s->{
            NodeDetailTo nodeDetail = JsonUtil.getObject(JsonUtil.getJsonString(s), NodeDetailTo.class);
            nodeDetail.setMtor019(groupResource.get(nodeDetail.getId()));
            nodes.add(nodeDetail);
        });
        
        ChangeType type = ChangeType.valueOf(order.getMtor002());
        
        switch(type){
            
            case ADD:
                
                break;
                
            case UPDATE:
                // 找到根节点 - 修改根节点信息
                // 将表里的其他字段失效日期大于当天的 全部修改为 当前日期
                break;
                
            default:
                
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(),"变更标记" + ErrorMessage._60000.getMsg());
        }
        
        // 新增节点信息
        
        // JSONFiled不标记id 和 pid
        
        // 找到根节点 直接手动加上id 和 pid属性 - 修改
        
        return result;
    }
    
    
}

