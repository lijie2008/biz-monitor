/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.business.service.impl
 * Date:2017年8月8日上午11:06:46
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service.impl;

import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.MONITORTREEORDER;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.model.BackTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.SortType;
import com.huntkey.rx.sceo.serviceCenter.common.model.MergeParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SortNode;

/**
 * ClassName:MonitorTreeOrderServiceImpl 临时单Impl
 * Date:     2017年8月8日 上午11:06:46
 * @author   lijie
 * @version  
 * @see 	   
 */
@Component
public class MonitorTreeOrderServiceImpl implements MonitorTreeOrderService{
    
    private static final String CREUSER = "admin";
    private static final String MODUSER = "admin";
    
    private static final int INVALID = 3;
    private static final String ROOT_LVL_CODE = "1,";
    private static final String SEP = ",";
    private static final String REVOKE_KEY = "REVOKE";
    private static final String KEY_SEP = "-";
    private static final String MTOR_NODES_EDM = "monitortreeorder.mtor_node_set";
    private static final String PRE_VERSION = "V";
    
    @Autowired
    private ServiceCenterClient client;
    
    @Autowired
    private ModelerClient edmClient;
    
    @Autowired
    private MonitorService service;
    
    @Autowired
    private MonitorTreeService mService;
    
    @Resource(name="redisTemplate")
    private HashOperations<String,String,NodeTo> hashOps;
    
    @Resource(name="redisTemplate")
    private ListOperations<String, RevokedTo> listOps;
    
    
    @Override
    public JSONObject queryNotUsingResource(String key, String lvlCode, int currentPage, int pageSize) {
        
        JSONObject datas = new JSONObject();
        
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中表单["+ key +"]" + ErrorMessage._60005.getMsg());
        
        NodeTo node = hashOps.get(key, lvlCode);
        
        if(node == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中节点层级编码["+ lvlCode +"]" + ErrorMessage._60005.getMsg());
            
        Result resourceEdmClass = edmClient.getEdmcNameEn(key.split(KEY_SEP)[1], Constant.EDMPCODE);
        
        String resourceEdmcNameEn = "";
        
        if(resourceEdmClass.getRetCode() == Result.RECODE_SUCCESS){
            
            if(resourceEdmClass.getData() != null &&
                    JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData())).getJSONObject(Constant.VALUE) != null){
                resourceEdmcNameEn = JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData()))
                        .getJSONObject(Constant.VALUE)
                        .getString("edmcNameEn");
            }
        }else
            throw new ServiceException(resourceEdmClass.getErrMsg());
        
        if(StringUtil.isNullOrEmpty(resourceEdmcNameEn))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        // 当前监管类树下的所有的资源信息
        SearchParam resourceParams = new SearchParam(resourceEdmcNameEn);
        
        Result allResult = client.queryServiceCenter(resourceParams.toJSONString());
        
        JSONArray allResource = null;
        
        if(allResult.getRetCode() == Result.RECODE_SUCCESS){
            if(allResult.getData() != null)
                allResource = JSONObject.parseObject(JSONObject.toJSONString(allResult.getData()))
                              .getJSONArray(Constant.DATASET);
            
            if(allResource == null || allResource.isEmpty())
                return null;
        }else
            throw new ServiceException(allResult.getErrMsg());
        
        // 查询在当前节点的时间区间内已被使用的资源信息
        Date begin = getDate(node.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
        Date end = getDate(node.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS);
        
        Set<String> usedResources = new HashSet<String>();
        
        for(String field : hashOps.keys(key)){
            
            NodeTo to = hashOps.get(key, field);
            Date b_date = getDate(to.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
            Date e_date = getDate(to.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS);
            
            if(to.getType() == INVALID || !b_date.before(end) || !e_date.after(begin))
                continue;
            
            List<ResourceTo> usedRes = to.getResources();
            if(usedRes != null && !usedRes.isEmpty())
                usedResources.addAll(usedRes.stream().map(ResourceTo::getResId).collect(Collectors.toSet()));
        }
        
        // 过滤掉已使用的资源
        List<Object> notUsedResources = allResource.stream().filter(re -> !usedResources.contains(((JSONObject)re).getString(Constant.ID)))
                .collect(Collectors.toList());
        
        int totalSize = notUsedResources == null ? 0 : notUsedResources.size();
        
        datas.put("totalSize", totalSize);
        datas.put("currentPage", currentPage);

        // 最终资源信息 - 未加工
        List<Object> filterRes = totalSize == 0 ? null : notUsedResources.subList((currentPage-1)*pageSize < 0 ? 0 : 
                    (currentPage-1)*pageSize > totalSize?totalSize:(currentPage-1)*pageSize, 
                    (currentPage*pageSize)>totalSize?totalSize:currentPage*pageSize);
        
        String resourceEdmId = JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData()))
                .getJSONObject(Constant.VALUE)
                .getString("id");
        
        // 拼接资源的text信息 - 最终资源信息
        JSONArray textRes = getResourceText(new JSONArray(filterRes), "", resourceEdmId);
        
        datas.put("data", textRes);
        return datas;
    }
    
    private Date getDate(String str,String mat){
        DateFormat format = new SimpleDateFormat(mat);
        try {
            return format.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换错误"+ str);
        }
    }
    
    private JSONArray getResourceText(JSONArray resources, String classId, String edmId){
        
        JSONArray textRes = new JSONArray();
        
        if(resources == null || resources.isEmpty())
            return textRes;
        
        String resourceEdmId = "";
        
        if(StringUtil.isNullOrEmpty(classId) && StringUtil.isNullOrEmpty(edmId))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
        
        if(StringUtil.isNullOrEmpty(edmId)){
            
            Result resourceEdmClass = edmClient.getEdmcNameEn(classId, Constant.EDMPCODE);
            
            if(resourceEdmClass.getRetCode() == Result.RECODE_SUCCESS){
                
                if(resourceEdmClass.getData() != null &&
                        JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData())).getJSONObject(Constant.VALUE) != null){
                    resourceEdmId = JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData()))
                            .getJSONObject(Constant.VALUE)
                            .getString("id");
                }
            }else
                throw new ServiceException(resourceEdmClass.getErrMsg());
        }else
            resourceEdmId = edmId;
        
        if(StringUtil.isNullOrEmpty(resourceEdmId))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "资源EdmId" + ErrorMessage._60005.getMsg());
        
        Result formatResult = edmClient.getCharacterAndFormat(resourceEdmId);
        
        if(formatResult.getRetCode() == Result.RECODE_SUCCESS){
            
            if(formatResult.getData() != null){
                
                JSONArray character = JSONObject.parseObject(JSONObject.toJSONString(formatResult.getData())).getJSONArray("character");
                String format = JSONObject.parseObject(JSONObject.toJSONString(formatResult.getData())).getString("format");
                
                if(character == null || format == null || character.isEmpty())
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"特征值" + ErrorMessage._60005.getMsg());
                
                String[] resourceFields = new String[character.size()];
                character.toArray(resourceFields);
                
                resources.stream().forEach(s->{
                    
                    JSONObject text = new JSONObject();
                    
                    text.put("resId", ((JSONObject)s).getString(Constant.ID));
                    
                    String txt = format.toLowerCase();
                    
                    for (String fieldName : resourceFields){
                        String f_str = StringUtil.isNullOrEmpty(((JSONObject)s).getString(fieldName))?""
                                :((JSONObject)s).getString(fieldName);
                        txt = txt.replace(fieldName,f_str);
                    }
                    
                    text.put("text",txt);
                    textRes.add(text);
                });
            }else
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"特征值" + ErrorMessage._60005.getMsg());
        }else
            throw new ServiceException(formatResult.getErrMsg());
        
        return textRes;
    }
    
    @Override
    public boolean checkDate(String key, String lvlCode, String startDate, String endDate) {
        
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中 键值[" + key + "]"+ErrorMessage._60005.getMsg());
        
        Date begin = getDate(startDate + Constant.STARTTIME,Constant.YYYY_MM_DD_HH_MM_SS);
        Date end = getDate(endDate + Constant.ENDTIME,Constant.YYYY_MM_DD_HH_MM_SS);
        
        if(begin.after(end))
            ApplicationException.throwCodeMesg(ErrorMessage._60015.getCode(),ErrorMessage._60015.getMsg());
        
        NodeTo node = hashOps.get(key, lvlCode);
        
        if(node == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中节点层级编号[" + lvlCode +"]"+ ErrorMessage._60005.getMsg());
       
        int level = node.getLvl();
        
        // 校验当前节点时间区间不能超过上级节点时间区间
        if(level != 1){
            
            String super_level_code = lvlCode.substring(0,lvlCode.substring(0, lvlCode.length()-1).lastIndexOf(SEP)+1);
            NodeTo superNode = hashOps.get(key, super_level_code);
            
            if(superNode == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),
                        "redis中节点层级["+ lvlCode +"], 不存在上级节点" + ErrorMessage._60005.getMsg());
            
            Date super_begin = getDate(superNode.getBegin(), Constant.YYYY_MM_DD_HH_MM_SS);
            Date super_end = getDate(superNode.getEnd(), Constant.YYYY_MM_DD_HH_MM_SS);
            
            
            if(super_begin.after(begin) ||  end.after(super_end))
                ApplicationException.throwCodeMesg(ErrorMessage._60016.getCode(),ErrorMessage._60016.getMsg());
        }
        
        // 验证资源是否存在冲突
        List<ResourceTo> nodeResources = node.getResources();
        
        if(nodeResources != null && !nodeResources.isEmpty()){
            
            List<ResourceTo> usedResources = new ArrayList<ResourceTo>();

            for(String field : hashOps.keys(key)){
                
                NodeTo to = hashOps.get(key, field);
                
                Date t_begin = getDate(to.getBegin(), Constant.YYYY_MM_DD_HH_MM_SS);
                Date t_end = getDate(to.getEnd(), Constant.YYYY_MM_DD_HH_MM_SS);
                
                if(to.getType() == INVALID || lvlCode.equals(to.getLvlCode())
                        || !t_begin.before(end) || !t_end.after(begin))
                    continue;
                    
                List<ResourceTo> usedRes = to.getResources();
                if(usedRes != null && !usedRes.isEmpty())
                    usedResources.addAll(usedRes);
            }
            
            if(!usedResources.isEmpty()){
                
                Set<String> usedResourceIds = usedResources.stream().map(ResourceTo::getResId).collect(Collectors.toSet());
                
                if(nodeResources.stream().anyMatch(re -> usedResourceIds.contains(re.getResId())))
                    ApplicationException.throwCodeMesg(ErrorMessage._60017.getCode(),ErrorMessage._60017.getMsg());
            }
        }
       
       // 修改的父节点是否会导致 子节点失效
       for(String field : hashOps.keys(key)){
           
           NodeTo to = hashOps.get(key, field);
           
           if(to.getLvl() > level && to.getLvlCode().startsWith(lvlCode) && to.getType() != INVALID){
               
               Date t_begin = getDate(to.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
               Date t_end = getDate(to.getEnd(), Constant.YYYY_MM_DD_HH_MM_SS);
               
               // 会导致节点失效
               if(!t_end.after(begin) || !t_begin.before(end))
                   return true;
           }
       }
        return false;
    }
    
    @Override
    public JSONArray queryAvailableResource(String key) {
       
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中 键值[" + key + "]"+ErrorMessage._60005.getMsg());
        
        Result resourceEdmClass = edmClient.getEdmcNameEn(key.split(KEY_SEP)[1], Constant.EDMPCODE);
        
        String resourceEdmcNameEn = "";
        
        if(resourceEdmClass.getRetCode() == Result.RECODE_SUCCESS){
            
            if(resourceEdmClass.getData() != null &&
                    JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData())).getJSONObject(Constant.VALUE) != null){
                
                resourceEdmcNameEn = JSONObject.parseObject(JSONObject.toJSONString(resourceEdmClass.getData()))
                        .getJSONObject(Constant.VALUE)
                        .getString("edmcNameEn");
            }
        }else
            throw new ServiceException(resourceEdmClass.getErrMsg());
        
        if(StringUtil.isNullOrEmpty(resourceEdmcNameEn))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        // 当前监管类树下的所有的资源信息
        SearchParam resourceParams = new SearchParam(resourceEdmcNameEn);
        
        Result allResult = client.queryServiceCenter(resourceParams.toJSONString());
        
        JSONArray allResource = null;
        
        if(allResult.getRetCode() == Result.RECODE_SUCCESS){
            if(allResult.getData() != null)
                allResource = JSONObject.parseObject(JSONObject.toJSONString(allResult.getData()))
                              .getJSONArray(Constant.DATASET);
            
            if(allResource == null || allResource.isEmpty())
                return null;
        }else
            throw new ServiceException(allResult.getErrMsg());
        
        // 已经被使用的资源信息
        Set<String> usedResourcesIds = new HashSet<String>();

        for(String field : hashOps.keys(key)){
            
            NodeTo to = hashOps.get(key, field);
            
            if(to.getType() == INVALID)
                continue;
                
            List<ResourceTo> usedRes = to.getResources();
            if(usedRes != null && !usedRes.isEmpty())
                usedResourcesIds.addAll(usedRes.stream().map(ResourceTo::getResId).collect(Collectors.toSet()));
        }
        
        if(usedResourcesIds.isEmpty())
            return allResource;
        
       return new JSONArray(allResource.stream().filter(re -> !usedResourcesIds.contains(((JSONObject)re).getString(Constant.ID)))
               .collect(Collectors.toList()));
    }
    
    @Override
    public String addOtherNode(String key){
        
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中 临时单[" + key + "]"+ ErrorMessage._60005.getMsg());
        
        JSONArray unusedResources = queryAvailableResource(key);
        
        if(unusedResources == null || unusedResources.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60020.getCode(), ErrorMessage._60020.getMsg());
        
        // 取出根节点
        NodeTo rootNode = hashOps.get(key, ROOT_LVL_CODE);
        
        if(rootNode == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中临时单根节点"+ ErrorMessage._60005.getMsg());
        
        // 取出二级节点
        List<String> childKeys = new ArrayList<>();
        
        hashOps.keys(key).stream().forEach(s->{
            if(s.split(SEP).length == 2 && s.startsWith(ROOT_LVL_CODE))
                childKeys.add(s);
        });
        
        // 新增节点
        NodeTo to = new NodeTo();
        to.setKey(key);
        to.setBegin(rootNode.getBegin());
        to.setEnd(rootNode.getEnd());
        to.setLvl(2);
        to.setNodeDef("其他节点");
        to.setNodeName("其他节点");
        to.setNodeNo("NODE"+System.currentTimeMillis());
        to.setType(ChangeType.ADD.getValue());
        
        JSONArray resourceTxt = getResourceText(unusedResources, key.split(KEY_SEP)[1], "");
        
        List<ResourceTo> resources = new ArrayList<ResourceTo>();
        
        resourceTxt.stream().forEach(s->{
            ResourceTo re = new ResourceTo();
            re.setResId(((JSONObject)s).getString("resId"));
            re.setText(((JSONObject)s).getString("text"));
            resources.add(re);
        });
        
        if(!resources.isEmpty())
            to.setResources(resources);
        
        if(childKeys.isEmpty())
            to.setSeq(1);
        else{
            Collections.sort(childKeys);
            to.setSeq(Math.floor(hashOps.get(key, childKeys.get(childKeys.size()-1)).getSeq()) + 1);
        }
        
        to.setLvlCode(ROOT_LVL_CODE+(int)to.getSeq()+SEP);
        
        hashOps.put(key, to.getLvlCode(), to);
        
        return key;
    }
    
    @Override
    public RevokedTo revoke(String key) {
        
        String revoke_key = key+REVOKE_KEY;
        
        if (listOps.size(revoke_key) == 0) 
            ApplicationException.throwCodeMesg(ErrorMessage._60011.getCode(), ErrorMessage._60011.getMsg());
        
        RevokedTo revoke = listOps.leftPop(revoke_key);
        
        Map<String, NodeTo> nodes = new HashMap<String, NodeTo>();
        
        revoke.getNodes().stream().forEach(s->{
            nodes.put(s.getLvlCode(), s);
        });
        
        for(String field : hashOps.keys(key))
            hashOps.delete(key, field);
        
        hashOps.putAll(key, nodes);
        revoke.setNodes(null);
        return revoke;
    }
    
    @Override
    public String save(String key) {
        
        // 检查临时单是否存在
        SearchParam o_params = new SearchParam(MONITORTREEORDER);
        o_params.addCond_equals(Constant.ID, key.split(KEY_SEP)[0]);
        
        Result result = client.queryServiceCenter(o_params.toJSONString());
        
        if(result.getRetCode() == Result.RECODE_SUCCESS){
            if(result.getData() == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "单据表中临时单["+key.split(KEY_SEP)[0]+"]"+ ErrorMessage._60005.getMsg());
        }else
            throw new ServiceException(result.getErrMsg());
        
        List<NodeTo> nodes = hashOps.values(key);
        
        if(nodes == null || nodes.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "redis中节点" + ErrorMessage._60005.getMsg());
        
        //查询出所有的节点信息
        SearchParam params = new SearchParam(MTOR_NODES_EDM);
        params
        .addColumns(new String[]{Constant.ID})
        .addCond_equals(Constant.PID, key.split(KEY_SEP)[0]);
        
        Result allNodes = client.queryServiceCenter(params.toJSONString());
        
        JSONArray ids = null;
        
        if(allNodes.getRetCode() == Result.RECODE_SUCCESS){
            if(allNodes.getData() != null)
                ids = JSONObject.parseObject(JSONObject.toJSONString(allNodes.getData()))
                              .getJSONArray(Constant.DATASET);
        }else
            throw new ServiceException(allNodes.getErrMsg());
        
        MergeParam mergeParams = new MergeParam(MTOR_NODES_EDM);

        // 删除临时单中的节点信息
        if(ids != null && !ids.isEmpty()){
            mergeParams.addAllData(ids);
            Result delResult = client.delete(mergeParams.toJSONString());
            if(delResult.getRetCode() != Result.RECODE_SUCCESS)
                throw new ServiceException(delResult.getErrMsg());
        }
        
        // 新增节点 和 资源信息
        mergeParams.addAllData(setValues(key, nodes));
        
        Result addResult = client.add(mergeParams.toJSONString());
        
        if(addResult.getRetCode() != Result.RECODE_SUCCESS)
            throw new ServiceException(addResult.getErrMsg());
        
        return key;
    }
    
    @Override
    public String store(String orderId) {
        
        orderId = orderId.split(KEY_SEP)[0];
        
        // 取出临时单信息
        SearchParam param = new SearchParam(Constant.MONITORTREEORDER);
        param.addCond_equals(Constant.ID, orderId);
        Result orderRet = client.queryServiceCenter(param.toJSONString());
        JSONObject order = null;
        
        if(orderRet.getRetCode() == Result.RECODE_SUCCESS){
            if(orderRet.getData() != null){
                JSONArray arry = JSONObject.parseObject(JSONObject.toJSONString(orderRet.getData()))
                        .getJSONArray(Constant.DATASET);
                if(arry != null && arry.size() == 1)
                    order = arry.getJSONObject(0);
            }
        }else
            throw new ServiceException(orderRet.getErrMsg());
       
        if(order == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "单据表中临时单["+orderId.split(KEY_SEP)[0]+"]"+ ErrorMessage._60005.getMsg());
        
        String classId = order.getString("mtor_cls_id");
        String rootNodeId = order.getString("mtor_order_root");
        ChangeType type = ChangeType.valueOf(Integer.valueOf(order.getString("mtor_order_type")));
        
        // 根据classId 查询监管类信息
        Result edmRet = edmClient.queryEdmClassById(classId);
        String edmName = null;
        
        if(edmRet.getRetCode() == Result.RECODE_SUCCESS){
            if(edmRet.getData() != null)
                edmName = JSONObject.parseObject(JSON.toJSONString(edmRet.getData())).getString("edmcNameEn");
        }else
            throw new ServiceException(edmRet.getErrMsg());
        
        if(StringUtil.isNullOrEmpty(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), "Modeler中" + ErrorMessage._60003.getMsg());
        
        //查询出临时单中的节点信息
        List<NodeTo> o_nodes = service.tempTree(orderId, "", 1, true);
        
        if(o_nodes == null || o_nodes.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "节点" + ErrorMessage._60005.getMsg());
        
        // 查看知识-监管树版本表
        SearchParam v_param = new SearchParam("monitortree");
        v_param.addCond_equals("motr_edm_id", order.getString("mtor_cls_id"))
               .addSortParam(new SortNode("motr_end", SortType.DESC));
        
        Result versionRet = client.queryServiceCenter(v_param.toJSONString());
        
        JSONArray versions = null;
        if(versionRet.getRetCode() == Result.RECODE_SUCCESS){
            if(versionRet.getData() != null)
                versions = JSONObject.parseObject(JSONObject.toJSONString(versionRet.getData()))
                        .getJSONArray(Constant.DATASET);
        }else
            throw new ServiceException(orderRet.getErrMsg());
       
        if(type == ChangeType.UPDATE && (versions == null || versions.isEmpty()))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "维护的版本" + ErrorMessage._60005.getMsg());
    
        NodeTo rootNode = null;
        
        if( o_nodes.stream().anyMatch(s->ROOT_LVL_CODE.equals(s.getLvlCode())))
            rootNode = o_nodes.stream().filter(s->ROOT_LVL_CODE.equals(s.getLvlCode())).findFirst().get();
        
        if(rootNode == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "根节点" + ErrorMessage._60005.getMsg());

        switch(type){
            
            case ADD :  // 新增类型
                // 所有的节点信息 转换成目标表的数据信息
                MergeParam a_param = new MergeParam(edmName);
                a_param.addAllData(setMoni(o_nodes, edmName));
                Result addRet = client.add(a_param.toJSONString());
                if(addRet.getRetCode() != Result.RECODE_SUCCESS)
                    throw new ServiceException(addRet.getErrMsg());
                
                // 查询出根节点id
                SearchParam pp = new SearchParam(edmName);
                pp.addColumns(Constant.ID)
                  .addCond_equals("moni_lvl_code", ROOT_LVL_CODE)
                  .addCond_equals("moni_lvl", "1")
                  .addCond_equals("moni_beg", rootNode.getBegin())
                  .addCond_equals("moni_end", rootNode.getEnd())
                  .addCond_equals("moni_node_no", rootNode.getNodeNo());
                
                Result noRes = client.queryServiceCenter(pp.toJSONString());
                if(noRes.getRetCode() == Result.RECODE_SUCCESS)
                    rootNodeId = JSONObject.parseObject(JSONObject.toJSONString(versionRet.getData()))
                            .getJSONArray(Constant.DATASET).getJSONObject(0).getString(Constant.ID);
                else
                    throw new ServiceException(noRes.getErrMsg());
                
                String verNo = null;
                if(versions == null || versions.isEmpty())
                    verNo = PRE_VERSION + "1";
                else
                    verNo = PRE_VERSION + versions.size() + 1;
                // 插入版本
                JSONObject vv = new JSONObject();
                vv.put("motr_beg", rootNode.getBegin());
                vv.put("motr_end", rootNode.getEnd());
                vv.put("motr_ver_code", verNo);
                vv.put("motr_edm_id", classId);
                vv.put("motr_root_id", rootNodeId);
                vv.put("creuser", CREUSER);
                vv.put("moduser", MODUSER);
                
                MergeParam vv_param = new MergeParam("monitortree");
                vv_param.addData(vv);
                Result rr = client.add(vv_param.toJSONString());
                if(rr.getRetCode() != Result.RECODE_SUCCESS)
                    throw new ServiceException(rr.getErrMsg());
                break;
                
            case UPDATE: // 维护类型
                
                if(StringUtil.isNullOrEmpty(rootNodeId) || !rootNodeId.equals(rootNode.getRelateId()))
                    ApplicationException.throwCodeMesg(ErrorMessage._60014.getCode(), "根节点["+ rootNodeId +"]" + ErrorMessage._60014.getMsg());
                
                JSONObject r_v = null;
                
                for(int i = 0; i < versions.size(); i++){
                    JSONObject release = versions.getJSONObject(i);
                    if(rootNodeId != null && rootNodeId.equals(release.getString("motr_root_id"))){
                        r_v = release;
                        break;
                    }
                }
                
                if(StringUtil.isNullOrEmpty(r_v))
                    ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), "版本数据" + ErrorMessage._60003.getMsg());
                
                SearchParam r_param = new SearchParam(edmName);
                r_param.addCond_equals(Constant.ID, rootNodeId);
                
                Result tRet = client.queryServiceCenter(r_param.toJSONString());
                
                JSONObject tRootNode = null;
                if(tRet.getRetCode() == Result.RECODE_SUCCESS){
                    JSONArray arry = JSONObject.parseObject(JSONObject.toJSONString(tRet.getData()))
                            .getJSONArray(Constant.DATASET);
                    if(arry != null && arry.size() == 1)
                        tRootNode = arry.getJSONObject(0);
                }else
                    throw new ServiceException(tRet.getErrMsg());
                
                if(tRootNode == null)
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "监管类根节点[" + rootNodeId + "]"+ErrorMessage._60005.getMsg());
                
                String t_begin = new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date(tRootNode.getLong("moni_beg")));
                String t_end = new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date(tRootNode.getLong("moni_end")));
                
                if(t_end.startsWith(Constant.MAXINVALIDDATE) && 
                        !rootNode.getEnd().startsWith(Constant.MAXINVALIDDATE)){
                    MergeParam m_param = new MergeParam("monitortree");
                    JSONObject obj = new JSONObject();
                    obj.put("motr_end", rootNode.getEnd());
                    obj.put(Constant.ID, r_v.getString(Constant.ID));
                    m_param.addData(obj);
                    Result s_ret = client.update(m_param.toJSONString());
                    if(s_ret.getRetCode() != Result.RECODE_SUCCESS)
                        throw new ServiceException(s_ret.getErrMsg());
                }
                
                JSONObject monitors = mService.getMonitorTreeNodes(edmName, t_begin, t_end, rootNodeId);
                
                JSONArray tNodes = monitors.getJSONArray("nodes");
                
                if(tNodes == null || tNodes.isEmpty())
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "查询节点" + ErrorMessage._60005.getCode());
                
                List<String> nodeIds = new ArrayList<String>();
                for (int i = 0; i < tNodes.size(); i++)
                    nodeIds.add(tNodes.getJSONObject(i).getString(Constant.ID));
                
                JSONArray resources = mService.getNodeResources(null, nodeIds, classId, edmName,1);
                
                JSONArray u_nodes = new JSONArray();
                for(String id : nodeIds){
                    JSONObject obj = new JSONObject();
                    obj.put(Constant.ID, id);
                    obj.put("is_del", 1);
                    u_nodes.add(obj);
                }
                
                // 更新目标节点状态为失效状态
                MergeParam n_param = new MergeParam(edmName);
                n_param.addAllData(u_nodes);
                Result n_ret = client.update(n_param.toJSONString());
                if(n_ret.getRetCode() != Result.RECODE_SUCCESS)
                    throw new ServiceException(n_ret.getErrMsg());
                
                if(resources !=null && !resources.isEmpty()){
                    JSONArray resIds = new JSONArray();
                    for(int k = 0; k < resources.size(); k++){
                        JSONObject res = resources.getJSONObject(k);
                        JSONObject obj = new JSONObject();
                        obj.put(Constant.ID, res.getString(Constant.ID));
                        resIds.add(obj);
                    }
                    
                    // 删除目标节点下的资源为失效状态
                    MergeParam rr_param = new MergeParam(edmName+".moni_res_set");
                    n_param.addAllData(resIds);
                    Result rr_ret = client.delete(rr_param.toJSONString());
                    if(rr_ret.getRetCode() != Result.RECODE_SUCCESS)
                        throw new ServiceException(rr_ret.getErrMsg());
                }
                
                // 将o_nodes数据插入到正式表中 （这些节点只可能是正在生效 和 未来节点
                JSONArray ttNodes = setMoni(o_nodes, edmName);

                JSONArray addNodes = new JSONArray();
                JSONArray updateNodes = new JSONArray();
                JSONArray addRes = new JSONArray();
                
                for(int i = 0; i < ttNodes.size(); i++){
                    JSONObject node = ttNodes.getJSONObject(i);
                    Date begin = getDate(node.getString("moni_beg"), Constant.YYYY_MM_DD_HH_MM_SS);
                    Date now = getDate(new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date()) + Constant.STARTTIME,
                            Constant.YYYY_MM_DD_HH_MM_SS);
                    
                    String id = node.getString(Constant.ID);
                    
                    if(!begin.after(now)) // 正在生效树
                        node.put("moni_beg", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(now));
                    
                    if(StringUtil.isNullOrEmpty(id)) // 新增节点类型
                        addNodes.add(node);
                    else{ // 修改节点型 - 资源是新增
                        JSONArray resArray = node.getJSONArray("moni_res_set");
                        node.remove("moni_res_set");
                        updateNodes.add(node);
                        if(resArray != null && !resArray.isEmpty())
                            addRes.addAll(resArray);
                    }
                }
                
                if(!addNodes.isEmpty()){
                    n_param.addAllData(addNodes);
                    Result rest = client.add(n_param.toJSONString());
                    if(rest.getRetCode() != Result.RECODE_SUCCESS)
                        throw new ServiceException(rest.getErrMsg());
                }
                
                if(!updateNodes.isEmpty()){
                    n_param.addAllData(updateNodes);
                    Result rest = client.update(n_param.toJSONString());
                    if(rest.getRetCode() != Result.RECODE_SUCCESS)
                        throw new ServiceException(rest.getErrMsg());
                }
                
                if(!addRes.isEmpty()){
                    MergeParam m_edm = new MergeParam(edmName+".moni_res_set");
                    m_edm.addAllData(addRes);
                    Result rest = client.add(m_edm.toJSONString());
                    if(rest.getRetCode() != Result.RECODE_SUCCESS)
                        throw new ServiceException(rest.getErrMsg());
                }
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                if(!new Date(tRootNode.getLong("moni_beg")).after(new Date())){
                    
                }
                
                
                
                // - 拿当前时间 和 根节点的时间做比较  如果 end <= now 失效树 全部加入到历史集
                // begin >= now 未来树 （将节点的失效时间全部置为当前时间） 入历史集
                // end >= now 正在生效树 需要筛选节点 begin > now 的节点 未使用 全部移除  未使用节点
//                                             end >= now 将失效时间全部 置为 now 部分使用节点
//                                                 已失效的节点 end < now 直接加入
                // 相反的处理临时单节点数据
                
                
                break;
                
            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(),"临时单变更类型" + ErrorMessage._60000.getMsg());
        }
        
        //临时单中节点信息的清理 - redis中key信息的清理  - 回退信息
        SearchParam allParams = new SearchParam(MTOR_NODES_EDM);
        allParams.addCond_equals(Constant.PID, orderId);
        allParams.addColumns(new String[]{Constant.ID});
        Result allNodes = client.queryServiceCenter(allParams.toJSONString());
        
        if(allNodes.getRetCode() == Result.RECODE_SUCCESS){
            if(allNodes.getData() != null){
                JSONArray nodeIds = JSONObject.parseObject(JSONObject.toJSONString(allNodes.getData()))
                        .getJSONArray(Constant.DATASET);
                if(nodeIds != null && !nodeIds.isEmpty()){
                    // 删除临时单中的节点和资源
                    MergeParam delNode = new MergeParam(MTOR_NODES_EDM);
                    delNode.addAllData(nodeIds);
                    Result delRet = client.delete(delNode.toJSONString());
                    if(delRet.getRetCode() != Result.RECODE_SUCCESS)
                        throw new ServiceException(delRet.getErrMsg());
                    
                    // 删除临时单
                    MergeParam delOrder = new MergeParam(MONITORTREEORDER);
                    delOrder.addData(order);
                    Result delOr = client.delete(delOrder.toJSONString());
                    if(delOr.getRetCode() != Result.RECODE_SUCCESS)
                         throw new ServiceException(delOr.getErrMsg());
                    
                    hashOps.getOperations().delete(orderId+ KEY_SEP +classId);
                    hashOps.getOperations().delete(orderId+ KEY_SEP +classId + REVOKE_KEY);
                }
            }else
                ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
        }else
            throw new ServiceException(allNodes.getErrMsg());
    
        return orderId;
    }

    private JSONArray setValues(String key, List<NodeTo> nodes){
        
        JSONArray savaNodes = new JSONArray();
        
        for(NodeTo to : nodes){
            JSONObject node = new JSONObject();
            
            node.put(Constant.PID, key.split(KEY_SEP)[0]);
            node.put("mtor_node_no", to.getNodeNo());
            node.put("mtor_node_name", to.getNodeName());
            node.put("mtor_node_def", to.getNodeDef());
            node.put("mtor_major", to.getMajor());
            node.put("mtor_assit", to.getAssit());
            node.put("mtor_beg", to.getBegin());
            node.put("mtor_end", to.getEnd());
            node.put("mtor_index_conf", to.getIndexConf());
            node.put("mtor_seq", to.getSeq());
            node.put("mtor_lvl_code", to.getLvlCode());
            node.put("mtor_lvl", to.getLvl());
            node.put("mtor_enum", to.getMtorEnum());
            node.put("mtor_relate_cnd", to.getRelateCnd());
            node.put("mtor_type", to.getType());
            node.put("mtor_relate_id", to.getRelateId());
            
            if(to.getResources() != null && !to.getResources().isEmpty() ){
                JSONArray resources = new JSONArray();
                for(ResourceTo re : to.getResources()){
                    JSONObject reObj = new JSONObject();
                    reObj.put("mtor_res_id", re.getResId());
                    resources.add(reObj);
                }
                node.put("mtor_res_set", resources);
            }
            
            if(to.getBackSet() != null && !to.getBackSet().isEmpty() ){
                JSONArray bkSet = new JSONArray();
                for(BackTo bk : to.getBackSet()){
                    JSONObject bkObj = new JSONObject();
                    bkObj.put("mtor_bk1", bk.getBk1());
                    bkObj.put("mtor_bk2", bk.getBk2());
                    bkObj.put("mtor_bk3", bk.getBk3());
                    bkSet.add(bkObj);
                }
                node.put("mtor_bk_set", bkSet);
            }
            node.put("creuser", CREUSER);
            node.put("moduser", MODUSER);
            savaNodes.add(node);
        }
        
        return savaNodes;
    }
    
    
    private JSONArray setMoni(List<NodeTo> nodes,String edmName){
        
        JSONArray savaNodes = new JSONArray();
        
        for(NodeTo to : nodes){
            JSONObject node = new JSONObject();
            
            node.put("moni_node_no", to.getNodeNo());
            node.put("moni_node_name", to.getNodeName());
            node.put("moni_node_def", to.getNodeDef());
            node.put("moni_major", to.getMajor());
            node.put("moni_assit", to.getAssit());
            node.put("moni_beg", to.getBegin());
            node.put("moni_end", to.getEnd());
            node.put("moni_index_conf", to.getIndexConf());
            node.put("moni_seq", to.getSeq());
            node.put("moni_lvl_code", to.getLvlCode());
            node.put("moni_lvl", to.getLvl());
            node.put("moni_enum", to.getMtorEnum());
            node.put("moni_relate_cnd", to.getRelateCnd());
            if(!StringUtil.isNullOrEmpty(to.getRelateId())){
                node.put(Constant.ID, to.getRelateId());
                node.put("is_del", 0);
            }
            
            if(to.getResources() != null && !to.getResources().isEmpty() ){
                JSONArray resources = new JSONArray();
                for(ResourceTo re : to.getResources()){
                    JSONObject reObj = new JSONObject();
                    if(!StringUtil.isNullOrEmpty(to.getRelateId()))
                        reObj.put(Constant.PID, to.getRelateId());
                    reObj.put("moni_res_id", re.getResId());
                    resources.add(reObj);
                }
                node.put("moni_res_set", resources);
            }
            
            // 特殊树的赋值
            if(to.getBackSet() != null && !to.getBackSet().isEmpty() && edmName.endsWith("depttree")){
                node.put("mdep_beg", to.getBegin());
                node.put("mdep_end", to.getEnd());
                for(BackTo bk : to.getBackSet()){
                    node.put("mdep_leader_post", bk.getBk1());
                    break;
                }
            }
            node.put("creuser", CREUSER);
            node.put("moduser", MODUSER);
            savaNodes.add(node);
        }
        
        return savaNodes;
    }
    
}

