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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.huntkey.rx.sceo.monitor.provider.controller.client.HbaseClient;
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
    private HbaseClient client;
    
    @Autowired
    private ModelerClient edmClient;
    
    @Override
    public NodeTo queryNode(String nodeId) {
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.ID);
        cnd.setOperator("=");
        cnd.setValue(nodeId);
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONArray dataset = data.getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset)){
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
            }
        }
        return null;
    }

    @Override
    public List<ResourceTo> queryResource(String nodeId) {
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.PID);
        cnd.setOperator("=");
        cnd.setValue(nodeId);
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR019B, cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONArray dataset = data.getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset)){
                return JsonUtil.getList(dataset, ResourceTo.class);
            }
        }
        return null;
    }
    
    @Override
    public MonitorTreeOrderTo queryOrder(String orderId){
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.ID);
        cnd.setOperator("=");
        cnd.setValue(orderId);
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        cnds.add(cnd);
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MONITORTREEORDER, cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONArray dataset = data.getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset)){
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), MonitorTreeOrderTo.class);
            }
        }
        return null;
    }
    
    @Override
    public List<String> queryTreeNodeUsingResource(String orderId, String startDate, String endDate, String excNodeId) {
        
        List<ResourceTo> resources = queryTreeNodeResource(orderId, startDate, endDate, excNodeId);
        Set<String> resourceIds = new HashSet<String>();
        if(JsonUtil.isEmpity(resources))
            return null;
        resources.stream().forEach(s->resourceIds.add(s.getMtor020()));
        return  new ArrayList<String>(resourceIds);
    }
    
    /**
     * 
     * queryTreeNodeResource: 根据条件查询所有满足条件的资源信息
     * @author lijie
     * @param orderId 临时单id
     * @param startDate 生效时间
     * @param endDate 失效时间
     * @param excNodeId 去除的节点
     * @return
     */
    @Override
    public List<ResourceTo> queryTreeNodeResource(String orderId, String startDate, String endDate, String excNodeId){
        
        Result result = client.queryTreeNodeResource(orderId, startDate, endDate,excNodeId);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            String str = JsonUtil.getJsonArrayString(result.getData());
            if(!JsonUtil.isEmpity(str)){
                // 防止重复
                List<ResourceTo> list = new ArrayList<ResourceTo>();
                JSON.parseArray(str, ResourceTo.class).stream().filter(s->
                    !list.stream().anyMatch(l->s.getId().equals(l.getId()))
                ).forEach(s -> list.add(s));
                return list;
            }
        }
        return null;
    }
    
    
    @Override
    public EdmClassTo getEdmClass(String classId, String edmpCode) {
        
        if(JsonUtil.isEmpity(classId) || JsonUtil.isEmpity(edmpCode))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        
        Result result = edmClient.getEdmcNameEn(classId, edmpCode);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONObject value = data.getJSONObject(PersistanceConstant.PERSISTANCE_VALUE);
            return JsonUtil.getObject(value.toJSONString(), EdmClassTo.class);
        }
        return null;
    }
    
    @Override
    public JSONArray getAllResource(String edmName) {
        
        if(JsonUtil.isEmpity(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        
        FullInputArgument input = new FullInputArgument(queryParam(edmName, null, null, null));
        
        Result result = client.find(input.getJson().toString());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            return data.getJSONArray(PersistanceConstant.DATASET);
        }
        
        return null;
        
       
    }
    
    @Override
    public NodeTo queryRootNode(String orderId) {
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.PID);
        cnd.setOperator("=");
        cnd.setValue(orderId);
        ConditionParam cnd2 = new ConditionParam();
        cnd2.setAttr("mtor013");
        cnd2.setOperator("=");
        cnd2.setValue(null);
        cnds.add(cnd);
        cnds.add(cnd2);
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONArray dataset = data.getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset)){
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
            }
        }
        return null;
    }

    @Override
    public NodeTo queryRootChildrenNode(String orderId, String rootNodeId) {
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(Constant.PID);
        cnd.setOperator("=");
        cnd.setValue(orderId);
        cnds.add(cnd);
        ConditionParam cnd2 = new ConditionParam();
        cnd2.setAttr("mtor013");
        cnd2.setOperator("=");
        cnd2.setValue(rootNodeId);
        cnds.add(cnd2);
        ConditionParam cnd3 = new ConditionParam();
        cnd3.setAttr("mtor016");
        cnd3.setOperator("=");
        cnd3.setValue(null);
        cnds.add(cnd3);
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, cnds, null, null));
        
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONArray dataset = data.getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset)){
                return JsonUtil.getObject(dataset.getJSONObject(0).toJSONString(), NodeTo.class);
            }
        }
        return null;
    }
    
    @Override
    public List<NodeTo> queryTreeNode(String orderId) {
        
        List<ConditionParam> cnds = new ArrayList<ConditionParam>();
        ConditionParam cnd = new ConditionParam();
        cnd.setAttr(PersistanceConstant.PID);
        cnd.setOperator("=");
        cnd.setValue(orderId);
        cnds.add(cnd);
        
        FullInputArgument input = new FullInputArgument(queryParam(PersistanceConstant.MTOR_MTOR005A, cnds, null, null));
        Result result = client.find(input.getJson().toString());
        
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60002.getCode(), ErrorMessage._60002.getMsg());
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            JSONArray dataset = data.getJSONArray(PersistanceConstant.DATASET);
            if(!JsonUtil.isEmpity(dataset)){
                return JsonUtil.getList(dataset, NodeTo.class);
            }
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
        
        if(!JsonUtil.isEmpity(result.getData())){
            JSONObject data = JsonUtil.getJson(result.getData());
            return JsonUtil.isEmpity(data) ? null : data.getString("edmcNameEn");
        }
        return null;
    }

    @Override
    public void updateTargetNode(String edmName, TargetNodeTo node) {
        
        Result result = client.updateTargetNode(edmName, node);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
    }

    @Override
    public JSONArray getTargetAllChildNode(String edmName, String nodeId, String endDate) {
        
        Result result = client.getTargetAllChildNode(edmName, nodeId, endDate);
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        
        return JsonUtil.getJsonArray(JsonUtil.getJsonString(result.getData()));
    }

    @Override
    public void batchUpdateTargetNode(String edmName, JSONArray nodes) {
        
        Result result = client.update(new FullInputArgument(mergeParam(edmName, nodes)).getJson().toString());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
    }
    
    private String queryParam(String edmName, List<ConditionParam> cnds, 
                             PagenationParam pagenation, List<SortParam> sort){
        JSONObject json = new JSONObject();
        JSONObject search = new JSONObject();
        search.put(PersistanceConstant.CONDITIONS, cnds);
        search.put(PersistanceConstant.PAGENATION, pagenation);
        search.put("orderBy", sort);
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

    @Override
    public void batchAdd(String edmName, JSONArray nodes) {
        
        Result result = client.add(new FullInputArgument(mergeParam(edmName, nodes)).getJson().toString());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
    }

    @Override
    public void updateNodeAndResource(String edmName, NodeDetailTo to) {
        List<ResourceTo> resources = queryResource(to.getId());
        List<String> resourceIds = resources.parallelStream().map(ResourceTo::getId).collect(Collectors.toList());
        batchDeleteResource(PersistanceConstant.MTOR_MTOR019B, resourceIds);
        resources = to.getMtor019();
        to.setMtor019(null);
        
        // 更新节点
        updateNode(edmName,to);
        // 新增资源信息
        if(resources == null || resources.size() == 0)
            return;
        resources.parallelStream().forEach(s->{
            s.setId(null);
        });
        batchAdd(PersistanceConstant.MTOR_MTOR019B, JSONArray.parseArray(JSON.toJSONString(resources)));
    }

    @Override
    public void batchDeleteResource(String edmName, List<String> ids) {
        Result result = client.delete(new FullInputArgument( mergeParam(edmName, JSON.parseArray(JSON.toJSONString(ids)))).toString());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
    }

    @Override
    public void updateNode(String edmName, NodeDetailTo to) {
        
        JSONArray array = new JSONArray();
        array.add(to);
        // 修改节点信息
        Result result = client.update(new FullInputArgument( mergeParam(edmName, array)).toString());
        if(result == null || result.getRetCode() != Result.RECODE_SUCCESS)
            ApplicationException.throwCodeMesg(ErrorMessage._60007.getCode(), ErrorMessage._60007.getMsg());
        
    }   
}

