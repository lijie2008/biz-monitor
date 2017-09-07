/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.business.service.impl
 * Date:2017年8月8日上午11:06:46
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.CharacterAndFormatTo;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.MonitorTreeOrderController;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.OperatorType;
import com.huntkey.rx.sceo.serviceCenter.common.emun.ReferanceType;
import com.huntkey.rx.sceo.serviceCenter.common.model.ConditionNode;
import com.huntkey.rx.sceo.serviceCenter.common.model.LoadParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.MergeParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;

/**
 * ClassName:MonitorTreeOrderServiceImpl 临时单Impl
 * Date:     2017年8月8日 上午11:06:46
 * @author   lijie
 * @version  
 * @see 	   
 */
@Component
public class MonitorTreeOrderServiceImpl implements MonitorTreeOrderService{
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorTreeOrderController.class);
    
    @Autowired
    private ServiceCenterClient client;
    
    @Autowired
    private ModelerClient edmClient;
    
    @Override
    public NodeTo queryNode(String nodeId) {
        SearchParam param = new SearchParam(Constant.MTOR005);
        param.addCondition(new ConditionNode(Constant.ID, OperatorType.Equals, nodeId));
        Result result = client.find(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("queryNode 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
        }
        return null;
    }

    @Override
    public List<ResourceTo> queryResource(String nodeId) {
        SearchParam param = new SearchParam(Constant.MTOR019);
        param.addCondition(new ConditionNode(Constant.PID, OperatorType.Equals, nodeId));
        Result result = client.find(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("queryResource 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getList(dataset, ResourceTo.class);
        }
        return null;
    }
    
    @Override
    public MonitorTreeOrderTo queryOrder(String orderId){
        SearchParam param = new SearchParam(Constant.MONITORTREEORDER);
        param.addCondition(new ConditionNode(Constant.ID, OperatorType.Equals, orderId));
        Result result = client.find(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("queryOrder 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), MonitorTreeOrderTo.class);
        }
        return null;
    }
    
    @Override
    public List<ResourceTo> queryTreeNodeUsingResource(String orderId, String startDate, 
                                                       String endDate, String excNodeId, Boolean invalid) {
        Result result = client.queryTreeNodeResource(orderId, startDate, endDate,excNodeId, invalid);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("queryTreeNodeUsingResource 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        List<ResourceTo> data = JSON.parseArray(JsonUtil.getJsonArrayString(result.getData()), ResourceTo.class);
        if(data == null || data.size() == 0)
            return null;
        return  new ArrayList<>(new HashSet<>(data));
    }
    
    @Override
    public EdmClassTo getEdmClass(String classId, String edmpCode) {
        if(JsonUtil.isEmpity(classId) || JsonUtil.isEmpity(edmpCode))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        Result result = edmClient.getEdmcNameEn(classId, edmpCode);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info(" getEdmClass 调用EDM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject value = JsonUtil.getJson(result.getData()).getJSONObject(Constant.VALUE);
            return JsonUtil.getObject(value.toJSONString(), EdmClassTo.class);
        }
        return null;
    }
    
    @Override
    public JSONArray getAllResource(String edmName) {
        if(JsonUtil.isEmpity(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        SearchParam param = new SearchParam(edmName);
        Result result = client.find(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("getAllResource 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData()))
            return JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
        return null;
    }
    
    @Override
    public NodeTo queryRootNode(String orderId) {
        SearchParam param = new SearchParam(Constant.MTOR005);
        param.addCondition(new ConditionNode(Constant.ID, OperatorType.Equals, orderId));
        param.addCondition(new ConditionNode(Constant.MTOR013, OperatorType.Equals, Constant.NULL));
        Result result = client.find(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info(" queryRootNode 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
        }
        return null;
    }

    @Override
    public NodeTo queryRootChildrenNode(String orderId, String rootNodeId) {
        SearchParam param = new SearchParam(Constant.MTOR005);
        param.addCondition(new ConditionNode(Constant.PID, OperatorType.Equals, orderId));
        param.addCondition(new ConditionNode(Constant.MTOR013, OperatorType.Equals, rootNodeId));
        param.addCondition(new ConditionNode(Constant.MTOR016, OperatorType.Equals, Constant.NULL));
        Result result = client.find(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("queryRootChildrenNode 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
        }
        return null;
    }
    
    @Override
    public List<NodeTo> queryTreeNode(String orderId) {
        SearchParam param = new SearchParam(Constant.MTOR005);
        param.addCondition(new ConditionNode(Constant.PID, OperatorType.Equals, orderId));
        Result result = client.find(JSON.toJSONString(param.build()));
        logger.info("查询所有的节点信息queryTreeNode ： " + JsonUtil.getJsonString(result));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info(" queryTreeNode 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getList(dataset, NodeTo.class);
        }
        return null;
    }

    @Override
    public String queryEdmClassName(String id) {
        if(JsonUtil.isEmpity(id))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        Result result = edmClient.queryEdmClassById(id);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("queryEdmClassName 调用EDM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData()))
            return JsonUtil.isEmpity(JsonUtil.getJson(result.getData())) ? null : JsonUtil.getJson(result.getData()).getString("edmcNameEn");
        return null;
    }

    @Override
    public void batchUpdate(String edmName, JSONArray nodes) {
        MergeParam param = new MergeParam(edmName);
        param.addAllData(nodes);
        Result result = client.update(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("batchUpdate 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> batchAdd(String edmName, JSONArray nodes) {
        logger.info("新增的数据为：" + nodes.toJSONString());
        MergeParam param = new MergeParam(edmName);
        param.addAllData(nodes);
        Result result = client.add(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("batchAdd 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg()+result.getErrMsg());
        }
        if(!JsonUtil.isEmpity(result.getData()))
            return (List<String>)result.getData();
        return null;
    }

    @Override
    public void updateNodeAndResource(String edmName, NodeDetailTo to) {
        List<ResourceTo> resources = queryResource(to.getId());
        if(!JsonUtil.isEmpity(resources)){
            List<String> resourceIds = resources.stream().map(ResourceTo::getId).collect(Collectors.toList());
            batchDeleteResource(Constant.MTOR019, resourceIds);
            resources = to.getMtor019();
        }
        to.setMtor019(null);
        // 更新节点
        updateNode(edmName,to);
        // 新增资源信息
        if(resources == null || resources.size() == 0)
            return;
        resources.stream().forEach(s->{
            s.setId(null);
            s.setAdduser("admin");
        });
        batchAdd(Constant.MTOR019, JSONArray.parseArray(JSON.toJSONString(resources)));
    }

    @Override
    public void batchDeleteResource(String edmName, List<String> ids) {
        MergeParam param = new MergeParam(edmName);
        ids.stream().forEach(id->{
            JSONObject obj = new JSONObject();
            obj.put(Constant.ID, id);
            param.addData(obj);
        });
        Result result = client.delete(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("batchDeleteResource 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
    }

    @Override
    public void updateNode(String edmName, NodeDetailTo to) {
        MergeParam param = new MergeParam(edmName);
        JSONObject obj = JSON.parseObject(JSON.toJSONString(to));
        obj.remove("adduser");
        param.addData(obj);
        Result result = client.update(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info(" updateNode 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
    }

    @Override
    public List<NodeDetailTo> load(String edmName, List<String> ids) {
        LoadParam param = new LoadParam(edmName);
        param.addLoadType(ReferanceType.Base);
        param.addIDs(ids);
        Result result = client.load(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("load 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JSON.parseArray(NodeDetailTo.parseArrayMapper(dataset).toJSONString(), NodeDetailTo.class);
        }
        return null;
    }

    @Override
    public List<NodeDetailTo> getAllNodesAndResource(String orderId) {
        List<NodeTo> treeNodes = queryTreeNode(orderId);
        if(JsonUtil.isEmpity(treeNodes))
            return null;
        // 资源信息
        List<ResourceTo> allResource = queryTreeNodeUsingResource(orderId, null, null, null,false);
        Map<String, List<ResourceTo>> groupResource = JsonUtil.isEmpity(allResource) ? new HashMap<>():
            allResource.stream().collect(Collectors.groupingBy(ResourceTo::getPid));
        List<NodeDetailTo> nodes = new ArrayList<NodeDetailTo>();
        treeNodes.stream().forEach(s->{
            NodeDetailTo nodeDetail = JsonUtil.getObject(JsonUtil.getJsonString(s), NodeDetailTo.class);
            nodeDetail.setMtor019(groupResource.get(nodeDetail.getId()));
            nodes.add(nodeDetail);
        });
        return nodes;
    }

    @Override
    public void deleteOrder(String orderId) {
        MergeParam param = new MergeParam(Constant.MONITORTREEORDER);
        JSONObject obj = new JSONObject();
        obj.put(Constant.ID, orderId);
        param.addData(obj);
        Result result = client.delete(JSON.toJSONString(param.build()));
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("deleteOrder 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        }
    }

    @Override
    public CharacterAndFormatTo getCharacterAndFormat(String classId) {
        Result result = edmClient.getCharacterAndFormat(classId);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
            logger.info("getCharacterAndFormat 调用EDM异常，异常消息: " + result == null ? null : result.getErrMsg());
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        }
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject value = JsonUtil.getJson(result.getData());
            return JsonUtil.getObject(value.toJSONString(), CharacterAndFormatTo.class);
        }
        return null;
    }

    @Override
    public List<Object> queryAvailableResource(String orderId) {
       MonitorTreeOrderTo order = queryOrder(orderId);
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
        String mtor003 = order.getMtor003();
        EdmClassTo edmClass = getEdmClass(mtor003, Constant.EDMPCODE);
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        String resourceEdmName = edmClass.getEdmcNameEn();
        JSONArray resources = getAllResource(resourceEdmName);
        if(JsonUtil.isEmpity(resources))
            return null;
        List<ResourceTo> usedResources = queryTreeNodeUsingResource(orderId, null, null,null,true);
        if(JsonUtil.isEmpity(usedResources))
            return JsonUtil.listToJsonArray(resources);
       Set<String> usedResourceIds = usedResources.stream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
       return resources.stream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(Constant.ID)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> queryTargetResource(String edmName, List<String> ids) {
        
        List<String> tids = new ArrayList<String>();
        
        ids.stream().forEach(s->{
            
            SearchParam param = new SearchParam(edmName);
            param.addCondition(new ConditionNode(Constant.PID,OperatorType.Equals,s));
            param.addColumns(Constant.ID);
            Result result = client.find(JSON.toJSONString(param.build()));
            if(result == null || result.getRetCode() != Result.RECODE_SUCCESS){
                logger.info("queryResource 调用ORM异常，异常消息: " + result == null ? null : result.getErrMsg());
                ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
            }
            if(!JsonUtil.isEmpity(result.getData())){
                JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(Constant.DATASET);
                if(!JsonUtil.isEmpity(dataset))
                    dataset.stream().forEach(d->{
                        tids.add(((JSONObject)d).getString(Constant.ID));
                    });
            }
        });
        return tids;
    }
}

