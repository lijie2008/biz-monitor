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

import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.constant.PersistanceConstant;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.CharacterAndFormatTo;
import com.huntkey.rx.sceo.monitor.commom.model.ConditionParam;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.FullInputArgument;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.PagenationParam;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.SortParam;
import com.huntkey.rx.sceo.monitor.commom.model.TargetNodeTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.MonitorTreeOrderController;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;

/**
 * ClassName:MonitorTreeOrderServiceImpl 临时单Impl
 * Date:     2017年8月8日 上午11:06:46
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class MonitorTreeOrderServiceImpl implements MonitorTreeOrderService{
    
    @Autowired
    private ServiceCenterClient client;
    
    @Autowired
    private ModelerClient edmClient;
    
    @Override
    public NodeTo queryNode(String nodeId) {
        
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        
        ConditionParam cnd = new ConditionParam(Constant.ID, "=", nodeId); 
        
        cnds.add(cnd);
        
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A,null, cnds, null, null));
        
        Result result = client.find(input.getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
            
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
            
        }
        
        return null;
    }

    @Override
    public List<ResourceTo> queryResource(String nodeId) {
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        ConditionParam cnd = new ConditionParam(Constant.PID, "=", nodeId); 
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR019B,null, cnds, null, null));
        
        Result result = client.find(input.getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getList(dataset, ResourceTo.class);
        }
        return null;
    }
    
    @Override
    public MonitorTreeOrderTo queryOrder(String orderId){
        
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        
        ConditionParam cnd = new ConditionParam(Constant.ID,"=",orderId);
        
        cnds.add(cnd);
        
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MONITORTREEORDER,null, cnds, null, null));

        Result result = client.find(input.getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
            
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), MonitorTreeOrderTo.class);
            
        }
        return null;
    }
    
    @Override
    public List<ResourceTo> queryTreeNodeUsingResource(String orderId, String startDate, 
                                                       String endDate, String excNodeId) {
        
        Result result = client.queryTreeNodeResource(orderId, startDate, endDate,excNodeId);
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
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
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject value = JsonUtil.getJson(result.getData()).getJSONObject(PersistanceConstant.PERSISTANCE_VALUE);
            return JsonUtil.getObject(value.toJSONString(), EdmClassTo.class);
        }
        return null;
    }
    
    @Override
    public JSONArray getAllResource(String edmName) {
        
        if(JsonUtil.isEmpity(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        
        FullInputArgument input = new FullInputArgument(queryParam(edmName, null, null, null, null));
        
        Result result = client.find(input.getJson());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData()))
            return JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
        return null;
    }
    
    @Override
    public NodeTo queryRootNode(String orderId) {
        
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        
        ConditionParam cnd = new ConditionParam(Constant.PID,"=",orderId);
        
        cnds.add(cnd);
        
        ConditionParam cnd2 = new ConditionParam("mtor013","=",Constant.NULL);
        
        cnds.add(cnd2);
        
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, null,cnds, null, null));
        
        Result result = client.find(input.getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
            
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
            
        }
        
        return null;
    }

    @Override
    public NodeTo queryRootChildrenNode(String orderId, String rootNodeId) {
        
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        
        ConditionParam cnd = new ConditionParam(Constant.PID,"=",orderId);
        cnds.add(cnd);
        
        ConditionParam cnd2 = new ConditionParam("mtor013","=",rootNodeId);
        cnds.add(cnd2);
        
        ConditionParam cnd3 = new ConditionParam("mtor016","=",Constant.NULL);
        cnds.add(cnd3);
        
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, null,cnds, null, null));
        
        Result result = client.find(input.getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
            
            if(!JsonUtil.isEmpity(dataset))
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
        }
        
        return null;
    }
    
    @Override
    public List<NodeTo> queryTreeNode(String orderId) {
        
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        ConditionParam cnd = new ConditionParam(PersistanceConstant.PID,"=",orderId);
        cnds.add(cnd);
        
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, null,cnds, null, null));
        Result result = client.find(input.getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
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
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData()))
            return JsonUtil.isEmpity(JsonUtil.getJson(result.getData())) ? null : JsonUtil.getJson(result.getData()).getString("edmcNameEn");
        return null;
    }

    @Override
    public void updateTargetNode(String edmName, TargetNodeTo node) {
        
        Result result = client.updateTargetNode(edmName, node);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
    }

    @Override
    public JSONArray getTargetAllChildNode(String edmName, String nodeId, String endDate) {
        
        Result result = client.getTargetAllChildNode(edmName, nodeId, endDate);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        return JsonUtil.getJsonArray(JsonUtil.getJsonString(result.getData()));
    }

    @Override
    public void batchUpdate(String edmName, JSONArray nodes) {
        logger.info("批量更新的参数信息: " + new FullInputArgument(mergeParam(edmName, nodes)).getJson());
        Result result = client.update(new FullInputArgument(mergeParam(edmName, nodes)).getJson());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
    }
    
    private String queryParam(String edmName,List<String> columns, List<ConditionParam> cnds, 
                             PagenationParam pagenation, List<SortParam> sort){
        JSONObject json = new JSONObject();
        JSONObject search = new JSONObject();
        search.put(PersistanceConstant.CONDITIONS, cnds);
        search.put(PersistanceConstant.PAGENATION, pagenation);
        search.put("orderBy", sort);
        search.put("columns", columns);
        json.put("search", search);
        json.put(PersistanceConstant.EDMNAME, edmName);
        return json.toJSONString();
    }
    
    private String mergeParam(String edmName, JSONArray params){
        JSONObject json = new JSONObject();
        json.put(PersistanceConstant.PARAMS, params);
        json.put(PersistanceConstant.EDMNAME, edmName);
        return json.toJSONString();
    }
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorTreeOrderController.class);
    
    @SuppressWarnings("unchecked")
    @Override
    public List<String> batchAdd(String edmName, JSONArray nodes) {
        logger.info("新增的数据为：" + nodes.toJSONString());
        logger.info("新增的参数为: " + new FullInputArgument(mergeParam(edmName, nodes)).getJson());
        Result result = client.add(new FullInputArgument(mergeParam(edmName, nodes)).getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData()))
            return (List<String>)result.getData();
        return null;
    }

    @Override
    public void updateNodeAndResource(String edmName, NodeDetailTo to) {
        List<ResourceTo> resources = queryResource(to.getId());
        if(!JsonUtil.isEmpity(resources)){
            List<String> resourceIds = resources.parallelStream().map(ResourceTo::getId).collect(Collectors.toList());
            batchDeleteResource(PersistanceConstant.MTOR_MTOR019B, resourceIds);
            resources = to.getMtor019();
        }
        to.setMtor019(null);
        // 更新节点
        updateNode(edmName,to);
        // 新增资源信息
        if(resources == null || resources.size() == 0)
            return;
        resources.parallelStream().forEach(s->{
            s.setId(null);
            s.setAdduser("admin");
        });
        batchAdd(PersistanceConstant.MTOR_MTOR019B, JSONArray.parseArray(JSON.toJSONString(resources)));
    }

    @Override
    public void batchDeleteResource(String edmName, List<String> ids) {
        JSONArray arry = new JSONArray();
        ids.stream().forEach(id->{
            JSONObject obj = new JSONObject();
            obj.put(PersistanceConstant.ID, id);
            arry.add(obj);
        });
        Result result = client.delete(new FullInputArgument( mergeParam(edmName, arry)).getJson());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
    }

    @Override
    public void updateNode(String edmName, NodeDetailTo to) {
        
        JSONArray array = new JSONArray();
        JSONObject obj = JSON.parseObject(JSON.toJSONString(to));
        if(JsonUtil.isEmpity(obj.get("mtor011")))
            obj.remove("mtor011");
        if(JsonUtil.isEmpity(obj.get("mtor012")))
            obj.remove("mtor012");
        obj.remove("adduser");
        array.add(obj);
        Result result = client.update(new FullInputArgument( mergeParam(edmName, array)).getJson());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
    }

    @Override
    public List<NodeDetailTo> load(String edmName, List<String> ids) {
        
        JSONObject json = new JSONObject();
        JSONObject search = new JSONObject();
        search.put("ids", ids);
        search.put("type", "base");
        json.put("search", search);
        json.put(PersistanceConstant.EDMNAME, edmName);
        logger.info("load 的参数信息： " + new FullInputArgument(json.toJSONString()).getJson());
        Result result = client.load(new FullInputArgument(json.toJSONString()).getJson());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            
            JSONArray dataset = JsonUtil.getJson(result.getData()).getJSONArray(PersistanceConstant.DATASET);
            
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
        List<ResourceTo> allResource = queryTreeNodeUsingResource(orderId, null, null, null);
        
        Map<String, List<ResourceTo>> groupResource = JsonUtil.isEmpity(allResource) ? new HashMap<>():
            allResource.parallelStream().collect(Collectors.groupingBy(ResourceTo::getPid));
        
        List<NodeDetailTo> nodes = new ArrayList<NodeDetailTo>();
        
        treeNodes.parallelStream().forEach(s->{
            NodeDetailTo nodeDetail = JsonUtil.getObject(JsonUtil.getJsonString(s), NodeDetailTo.class);
            nodeDetail.setMtor019(groupResource.get(nodeDetail.getId()));
            nodes.add(nodeDetail);
        });
        return nodes;
    }

    @Override
    public void deleteOrder(String orderId) {
        JSONArray arry = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(PersistanceConstant.ID, orderId);
        arry.add(obj);
        Result result = client.delete(new FullInputArgument( mergeParam(PersistanceConstant.MONITORTREEORDER, arry)).getJson());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
    }

    @Override
    public CharacterAndFormatTo getCharacterAndFormat(String classId) {
        
        Result result = edmClient.getCharacterAndFormat(classId);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        
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
        EdmClassTo edmClass = getEdmClass(mtor003, PersistanceConstant.EDMPCODE);
        
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        String resourceEdmName = edmClass.getEdmcNameEn();
        
        JSONArray resources = getAllResource(resourceEdmName);
        
        if(JsonUtil.isEmpity(resources))
            return null;
        
        List<ResourceTo> usedResources = queryTreeNodeUsingResource(orderId, null, null,null);
        
        if(JsonUtil.isEmpity(usedResources))
            return JsonUtil.listToJsonArray(resources);
        
       Set<String> usedResourceIds = usedResources.parallelStream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
       
       return resources.parallelStream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(PersistanceConstant.ID)))
                .collect(Collectors.toList());
    }
}

