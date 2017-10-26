/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderServiceImpl.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.business.service.impl
 * Date:2017年8月8日上午11:06:46
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service.impl;

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
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
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
    
    private static final String ADDUSER = "admin";
    
    private static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final int INVALID = 3;
    private static final String ROOT_LVL_CODE = "1,";
    private static final String SEP = ",";
    private static final String REVOKE_KEY = "REVOKE";
    private static final String KEY_SEP = "-";
    private static final String MTOR_NODES_EDM = "monitortreeorder.mtor_node_set";
    
    @Autowired
    private ServiceCenterClient client;
    
    @Autowired
    private ModelerClient edmClient;
    
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
        Date begin = getDate(node.getBegin(),DATE_TIME);
        Date end = getDate(node.getEnd(),DATE_TIME);
        
        Set<String> usedResources = new HashSet<String>();
        
        for(String field : hashOps.keys(key)){
            
            NodeTo to = hashOps.get(key, field);
            Date b_date = getDate(to.getBegin(),DATE_TIME);
            Date e_date = getDate(to.getEnd(),DATE_TIME);
            
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
                    
                    text.put("id", ((JSONObject)s).getString("id"));
                    
                    String txt = format.toLowerCase();
                    
                    for (String fieldName : resourceFields) 
                        txt = txt.replace(fieldName,
                                ((JSONObject)s).getString(fieldName));
                    
                    
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
        
        Date begin = getDate(startDate + " 00:00:00",DATE_TIME);
        Date end = getDate(endDate + " 23:59:59",DATE_TIME);
        
        if(begin.after(end))
            ApplicationException.throwCodeMesg(ErrorMessage._60015.getCode(),ErrorMessage._60015.getMsg());
        
        NodeTo node = hashOps.get(key, lvlCode);
        
        if(node == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中节点层级编号[" + lvlCode +"]"+ ErrorMessage._60005.getMsg());
       
        int level = node.getLvl();
        
        // 校验当前节点时间区间不能超过上级节点时间区间
        if(level != 1){
            
            String super_level_code = lvlCode.substring(0,lvlCode.substring(0, lvlCode.length()-1).lastIndexOf(",")+1);
            NodeTo superNode = hashOps.get(key, super_level_code);
            
            if(superNode == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),
                        "redis中节点层级["+ lvlCode +"], 不存在上级节点" + ErrorMessage._60005.getMsg());
            
            Date super_begin = getDate(superNode.getBegin(), DATE_TIME);
            Date super_end = getDate(superNode.getEnd(), DATE_TIME);
            
            
            if(super_begin.after(begin) ||  end.after(super_end))
                ApplicationException.throwCodeMesg(ErrorMessage._60016.getCode(),ErrorMessage._60016.getMsg());
        }
        
        // 验证资源是否存在冲突
        List<ResourceTo> nodeResources = node.getResources();
        
        if(nodeResources != null && !nodeResources.isEmpty()){
            
            List<ResourceTo> usedResources = new ArrayList<ResourceTo>();

            for(String field : hashOps.keys(key)){
                
                NodeTo to = hashOps.get(key, field);
                
                Date t_begin = getDate(to.getBegin(), DATE_TIME);
                Date t_end = getDate(to.getEnd(), DATE_TIME);
                
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
               
               Date t_begin = getDate(to.getBegin(),DATE_TIME);
               Date t_end = getDate(to.getEnd(), DATE_TIME);
               
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
            return key;
        
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
        to.setBegin(rootNode.getBegin());
        to.setEnd(rootNode.getEnd());
        to.setLvl(2);
        to.setNodeDef("其他节点");
        to.setNodeName("其他节点");
        to.setNodeNo("NODE"+System.currentTimeMillis());
        to.setType(ChangeType.ADD.getValue());
        
        JSONArray resourceTxt = getResourceText(unusedResources, key.split("-")[1], "");
        
        List<ResourceTo> resources = new ArrayList<ResourceTo>();
        
        resourceTxt.stream().forEach(s->{
            ResourceTo re = new ResourceTo();
            re.setResId(((JSONObject)s).getString("id"));
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
    public String store(String key) {
        
        return key;
    }

    @Override
    public String save(String key) {
        
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
            node.put("adduser", ADDUSER);
            savaNodes.add(node);
        }
        
        return savaNodes;
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
        
        hashOps.delete(key, hashOps.keys(key));
        hashOps.putAll(key, nodes);
        revoke.setNodes(null);
        
        return revoke;
    }
}

