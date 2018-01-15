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
import java.util.Calendar;
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
import com.huntkey.rx.edm.entity.MoniMoniHisSetaEntity;
import com.huntkey.rx.edm.entity.MoniMoniHresSetbEntity;
import com.huntkey.rx.edm.entity.MoniMoniResSetaEntity;
import com.huntkey.rx.edm.entity.MonitorEntity;
import com.huntkey.rx.edm.entity.MonitortreeEntity;
import com.huntkey.rx.edm.entity.MonitortreeorderEntity;
import com.huntkey.rx.edm.entity.MtorMtorNodeSetaEntity;
import com.huntkey.rx.edm.entity.MtorMtorResSetbEntity;
import com.huntkey.rx.edm.entity.ResourceEntity;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.model.CurrentSessionEntity;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.service.BizFormService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.orm.common.model.OrmParam;
import com.huntkey.rx.sceo.orm.common.type.SQLCurdEnum;
import com.huntkey.rx.sceo.orm.common.type.SQLSortEnum;
import com.huntkey.rx.sceo.orm.common.type.SQLSymbolEnum;
import com.huntkey.rx.sceo.orm.common.util.EdmUtil;
import com.huntkey.rx.sceo.orm.service.OrmService;

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
    private ModelerClient edmClient;
    
    @Autowired
    private MonitorService service;
    
    @Autowired
    private MonitorTreeService mService;
    
    @Autowired
    private OrmService ormService;
    
    @Resource(name="redisTemplate")
    private HashOperations<String,String,NodeTo> hashOps;
    
    @Resource(name="redisTemplate")
    private ListOperations<String, RevokedTo> listOps;
    
    @Autowired
    private BizFormService formService;
    
    @Override
    public JSONObject queryNotUsingResource(String key, String lvlCode, int currentPage, int pageSize) throws Exception{
        
        JSONObject datas = new JSONObject();
        
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中表单["+ key +"]" + ErrorMessage._60005.getMsg());
        
        NodeTo node = hashOps.get(key, lvlCode);
        
        if(node == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中节点层级编码["+ lvlCode +"]" + ErrorMessage._60005.getMsg());
            
        Result resourceEdmClass = edmClient.getPropertyValue(key.split(Constant.KEY_SEP)[1], Constant.EDMPCODE);
        
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
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        @SuppressWarnings("rawtypes")
        Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(resourceEdmcNameEn));
        
        @SuppressWarnings("unchecked")
        List<? extends ResourceEntity> allResource = ormService.selectBeanList(cls, param);
        
        if(allResource == null || allResource.isEmpty())
            return null;
        
        // 查询在当前节点的时间区间内已被使用的资源信息
        Date begin = getDate(node.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
        Date end = getDate(node.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS);
        
        Set<String> usedResources = new HashSet<String>();
        
        for(String field : hashOps.keys(key)){
            
            NodeTo to = hashOps.get(key, field);
            Date b_date = getDate(to.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
            Date e_date = getDate(to.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS);
            
            if(to.getType() == ChangeType.INVALID.getValue() || !b_date.before(end) || !e_date.after(begin))
                continue;
            
            List<ResourceTo> usedRes = to.getResources();
            if(usedRes != null && !usedRes.isEmpty())
                usedResources.addAll(usedRes.stream().map(ResourceTo::getResId).collect(Collectors.toSet()));
        }
        
        // 过滤掉已使用的资源
        List<Object> notUsedResources = allResource.stream().filter(re -> !usedResources.contains(re.getId()))
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
        
        if(filterRes == null || filterRes.isEmpty())
            datas.put("data", null);
        else{
            // 拼接资源的text信息 - 最终资源信息
            JSONArray textRes = getResourceText(JSONArray.parseArray(JSON.toJSONString(filterRes)), "", resourceEdmId);
            datas.put("data", textRes);
        }
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
            
            Result resourceEdmClass = edmClient.getPropertyValue(classId, Constant.EDMPCODE);
            
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
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "资源无法呈现！");
                
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
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"资源无法呈现！");
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
            
            String super_level_code = lvlCode.substring(0,lvlCode.substring(0, lvlCode.length()-1).lastIndexOf(Constant.LVSPLIT)+1);
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
                
                if(to.getType() == ChangeType.INVALID.getValue() || lvlCode.equals(to.getLvlCode())
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
           
           if(to.getLvl() > level && to.getLvlCode().startsWith(lvlCode) && to.getType() != ChangeType.INVALID.getValue()){
               
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
    public List<?> queryAvailableResource(String key) throws Exception {
       
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中 键值[" + key + "]"+ErrorMessage._60005.getMsg());
        
        Result resourceEdmClass = edmClient.getPropertyValue(key.split(Constant.KEY_SEP)[1], Constant.EDMPCODE);
        
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
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        @SuppressWarnings("rawtypes")
        Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(resourceEdmcNameEn));
        
        @SuppressWarnings("unchecked")
        List<? extends ResourceEntity> allResource = ormService.selectBeanList(cls, param);
        
        if(allResource == null || allResource.isEmpty())
            return null;
        
        // 已经被使用的资源信息
        Set<String> usedResourcesIds = new HashSet<String>();

        for(String field : hashOps.keys(key)){
            
            NodeTo to = hashOps.get(key, field);
            
            if(to.getType() == ChangeType.INVALID.getValue())
                continue;
                
            List<ResourceTo> usedRes = to.getResources();
            if(usedRes != null && !usedRes.isEmpty())
                usedResourcesIds.addAll(usedRes.stream().map(ResourceTo::getResId).collect(Collectors.toSet()));
        }
        
        if(usedResourcesIds.isEmpty())
            return allResource;
        
        return allResource.stream().filter(re -> !usedResourcesIds.contains(re.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public String addOtherNode(String key) throws Exception{
        
        if(hashOps.size(key) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中 临时单[" + key + "]"+ ErrorMessage._60005.getMsg());
        
        List<?> unusedResources = queryAvailableResource(key);
        
        if(unusedResources == null || unusedResources.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60020.getCode(), ErrorMessage._60020.getMsg());
        
        // 取出根节点
        NodeTo rootNode = hashOps.get(key, Constant.ROOT_LVL_CODE);
        
        if(rootNode == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"redis中临时单根节点"+ ErrorMessage._60005.getMsg());
        
        // 取出二级节点
        List<String> childKeys = new ArrayList<>();
        
        hashOps.keys(key).stream().forEach(s->{
            if(s.split(Constant.LVSPLIT).length == 2 && s.startsWith(Constant.ROOT_LVL_CODE))
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
        
        JSONArray resourceTxt = getResourceText(JSONArray.parseArray(JSON.toJSONString(unusedResources)), key.split(Constant.KEY_SEP)[1], "");
        
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
        
        to.setLvlCode(Constant.ROOT_LVL_CODE + (int)to.getSeq() + Constant.LVSPLIT);
        
        hashOps.put(key, to.getLvlCode(), to);
        
        return key;
    }
    
    @Override
    public RevokedTo revoke(String key) {
        
        String revoke_key = key+ Constant.REVOKE_KEY;
        
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
    public String save(String key) throws Exception {
        
        // 检查临时单是否存在
        MonitortreeorderEntity order = ormService.load(MonitortreeorderEntity.class, key.split(Constant.KEY_SEP)[0]);
        
        if(order == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "单据表中临时单["+key.split(Constant.KEY_SEP)[0]+"]"+ ErrorMessage._60005.getMsg());
        
        List<NodeTo> nodes = hashOps.values(key);
        
        // 检查节点合法性
        checkNodeLegal(key.split(Constant.KEY_SEP)[1],nodes);
        
        if(nodes == null || nodes.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "redis中节点" + ErrorMessage._60005.getMsg());
        
        List<MtorMtorNodeSetaEntity> allNodes = order.loadMtor_node_set();
        
        List<String> resIds = new ArrayList<>(); 
        List<String> nodeIds = new ArrayList<>();
        for(MtorMtorNodeSetaEntity ee : allNodes){
            List<MtorMtorResSetbEntity> res = ee.loadMtor_res_set();
            if(res != null && !res.isEmpty())
                res.stream().forEach(s->{
                    resIds.add(s.getId());
                });
            nodeIds.add(ee.getId());
        }
        
        OrmParam param = new OrmParam();
        if(resIds != null && !resIds.isEmpty()){
            param.setWhereExp(param.getInXML(Constant.ID, resIds.toArray()));
            ormService.delete(MtorMtorResSetbEntity.class, param);
        }
        
        param.reset();
        if(nodeIds != null && !nodeIds.isEmpty()){
            param.setWhereExp(param.getInXML(Constant.ID, nodeIds.toArray()));
            ormService.delete(MtorMtorNodeSetaEntity.class, param);
        }
        
        // 新增资源 和 节点信息
        List<MtorMtorNodeSetaEntity> m_list = JSONArray.parseArray(setValues(key, nodes).toJSONString(), MtorMtorNodeSetaEntity.class);
        
        CurrentSessionEntity session = formService.getCurrentSessionInfo();
        for(MtorMtorNodeSetaEntity n : m_list){
            //主表 - 临时单节点集合
            n.setCreuser(session.getEmployeeId());
            n.setClassName(EdmUtil.getEdmClassName(MonitortreeorderEntity.class));
            String id = ormService.insertSelective(n).toString();
            n.setId(id);
            //属性集 - 临时单节点的资源集合
            List<MtorMtorResSetbEntity> res = n.getMtor_res_set();
            if(res != null && !res.isEmpty()){
                EdmUtil.setPropertyBaseEntitiesSysColumns(MonitortreeorderEntity.class, n, res, SQLCurdEnum.INSERT);
                ormService.insert(res);
            }
        }
        return key;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String store(String orderId) throws Exception {
        
        orderId = orderId.split(Constant.KEY_SEP)[0];
        // 取出临时单信息
        MonitortreeorderEntity order = ormService.load(MonitortreeorderEntity.class, orderId);
       
        if(order == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "单据表中临时单["+orderId.split(Constant.KEY_SEP)[0]+"]"+ ErrorMessage._60005.getMsg());
        
        String classId = order.getMtor_cls_id();
        String rootNodeId = order.getMtor_order_root();
        ChangeType type = ChangeType.valueOf(Integer.valueOf(order.getMtor_order_type()));
        
        // 根据classId 查询监管类信息
        Result edmRet = edmClient.getEdmByid(classId);
        String edmName = null;
        
        if(edmRet.getRetCode() == Result.RECODE_SUCCESS){
            if(edmRet.getData() != null)
                edmName = JSONObject.parseObject(JSON.toJSONString(edmRet.getData())).getString("edmcNameEn");
        }else
            throw new ServiceException(edmRet.getErrMsg());
        
        if(StringUtil.isNullOrEmpty(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), "Modeler中" + ErrorMessage._60003.getMsg());
        
        //查询出临时单中的节点信息 - 失效节点在此处已被过滤
        List<NodeTo> o_nodes = service.tempTree(orderId, "", 1, true);
        
        if(o_nodes == null || o_nodes.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "节点" + ErrorMessage._60005.getMsg());
        
        // 检查节点合法性
        checkNodeLegal(classId,o_nodes);
        
        // 查看知识-监管树版本表
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        param.setWhereExp(param.getEqualXML("motr_edm_id", classId));
        param.addOrderExpElement(SQLSortEnum.DESC, "motr_end");
        
        List<MonitortreeEntity> versions = ormService.selectBeanList(MonitortreeEntity.class, param);
        
        // 维护状态的临时单 必须存在版本标识信息
        if(type == ChangeType.UPDATE && (versions == null || versions.isEmpty()))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "维护的版本" + ErrorMessage._60005.getMsg());
       
        // 取到临时单节点集合的根节点
        NodeTo rootNode = null;
        
        if(o_nodes.stream().anyMatch(s->Constant.ROOT_LVL_CODE.equals(s.getLvlCode())))
            rootNode = o_nodes.stream().filter(s->Constant.ROOT_LVL_CODE.equals(s.getLvlCode())).findFirst().get();
        
        if(rootNode == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "根节点" + ErrorMessage._60005.getMsg());
        
        @SuppressWarnings("rawtypes")
        Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(edmName));
        
        CurrentSessionEntity session = formService.getCurrentSessionInfo();

        switch(type){
            
            case ADD :  // 单据 - 新增类型
                // 所有的节点信息 转换成目标表的数据信息
                List<? extends MonitorEntity> nn = JSONArray.parseArray(setMoni(o_nodes, edmName).toJSONString(), cls);
                
                for(MonitorEntity me : nn){
                    me.setCreuser(session.getEmployeeId());
                    String id = ormService.insertSelective(me).toString();
                    me.setId(id);
                    if(me.getMoni_lvl() == 1)
                        rootNodeId = id;
                    
                    //属性集 - 临时单节点的资源集合
                    List<MoniMoniResSetaEntity> _res = me.getMoni_res_set();
                    
                    if(_res != null && !_res.isEmpty()){
                        EdmUtil.setPropertyBaseEntitiesSysColumns(cls, me, _res, SQLCurdEnum.INSERT);
                        ormService.insert(_res);
                    }
                }
                
                String verNo = null;
                if(versions == null || versions.isEmpty())
                    verNo = Constant.PRE_VERSION + "1";
                else{
                    verNo = Constant.PRE_VERSION + (versions.size() + 1);
                }
                
                MonitortreeEntity vv = new MonitortreeEntity();
                vv.setMotr_beg(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).parse(rootNode.getBegin()));
                vv.setMotr_end(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).parse(rootNode.getEnd()));
                vv.setMotr_ver_code(verNo);
                vv.setMotr_edm_id(classId);
                vv.setMotr_root_id(rootNodeId);
                ormService.insertSelective(vv);
                
                break;
                
            case UPDATE: // 维护类型
                
                if(StringUtil.isNullOrEmpty(rootNodeId) || !rootNodeId.equals(rootNode.getRelateId()))
                    ApplicationException.throwCodeMesg(ErrorMessage._60014.getCode(), "根节点["+ rootNodeId +"]" + ErrorMessage._60014.getMsg());
                
                MonitortreeEntity r_v = null;
                
                for(int i = 0; i < versions.size(); i++){
                    MonitortreeEntity release = versions.get(i);
                    if(rootNodeId.equals(release.getMotr_root_id())){
                        r_v = release;
                        break;
                    }
                }
                
                if(StringUtil.isNullOrEmpty(r_v))
                    ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), "版本数据" + ErrorMessage._60003.getMsg());
                
                
                MonitorEntity tRootNode = (MonitorEntity)ormService.load(cls, rootNodeId);
                
                if(tRootNode == null)
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "监管类根节点[" + rootNodeId + "]"+ErrorMessage._60005.getMsg());
                
                String t_begin = new SimpleDateFormat(Constant.YYYY_MM_DD).format(tRootNode.getMoni_beg());
                String t_end = new SimpleDateFormat(Constant.YYYY_MM_DD).format(tRootNode.getMoni_end());
                
                // 先更新版本表信息
                if(t_end.startsWith(Constant.MAXINVALIDDATE) && 
                        !rootNode.getEnd().startsWith(Constant.MAXINVALIDDATE)){
                    
                    MonitortreeEntity u_v = new MonitortreeEntity();
                    u_v.setMotr_end(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).parse(rootNode.getEnd()));
                    u_v.setId(r_v.getId());
                    u_v.setModuser(session.getEmployeeId());
                    
                    ormService.updateSelective(u_v);
                }
                
                JSONObject monitors = mService.getMonitorTreeNodes(edmName, t_begin, t_end, rootNodeId);
                
                JSONArray tNodes = monitors.getJSONArray("nodes");
                
                
                if(tNodes == null || tNodes.isEmpty())
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "查询节点" + ErrorMessage._60005.getCode());
                
                List<String> nodeIds = new ArrayList<String>();
                for (int i = 0; i < tNodes.size(); i++)
                    nodeIds.add(tNodes.getJSONObject(i).getString(Constant.ID));
                
                JSONArray resources = mService.getNodeResources(null, nodeIds, classId, edmName,1);
                
                // 临时单的节点 需入库的节点信息
                JSONArray ttNodes = setMoni(o_nodes, edmName);
                
                // 删除正式表中没有修改的节点信息
                List<String> d_nodes = new ArrayList<String>();
                for(String id : nodeIds){
                    
                    boolean exist = false;
                    for(int j = 0; j < ttNodes.size(); j++){
                        JSONObject bb = ttNodes.getJSONObject(j);
                        String nn_id = bb.getString(Constant.ID);
                        if(!StringUtil.isNullOrEmpty(nn_id) && nn_id.equals(id)){
                            exist = true;
                            break;
                        }
                    }
                    if(exist)
                        continue;
                    d_nodes.add(id);
                }
                
                if(!d_nodes.isEmpty()){
                    param.reset();
                    param.setWhereExp(param.getInXML(Constant.ID, d_nodes.toArray()));
                    ormService.delete(cls, param);
                }
                
                // 删除监管树表下所有的资源信息
                if(resources !=null && !resources.isEmpty()){
                    List<String> resIds = new ArrayList<String>();
                    for(int k = 0; k < resources.size(); k++){
                        JSONObject res = resources.getJSONObject(k);
                        resIds.add(res.getString(Constant.OID));
                    }
                    param.reset();
                    param.setWhereExp(param.getInXML(Constant.ID, resIds.toArray()));
                    ormService.delete(MoniMoniResSetaEntity.class, param);
                }
                
                // 将o_nodes数据插入到正式表中 (这些节点只可能是正在生效 和 未来节点)
                JSONArray addNodes = new JSONArray();
                JSONArray updateNodes = new JSONArray();
                JSONArray addRes = new JSONArray();
                
                Date now = getDate(new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date()) + Constant.STARTTIME,
                        Constant.YYYY_MM_DD_HH_MM_SS);
                
                for(int i = 0; i < ttNodes.size(); i++){
                    JSONObject node = ttNodes.getJSONObject(i);
                    Date begin = getDate(node.getString("moni_beg"), Constant.YYYY_MM_DD_HH_MM_SS);
                    
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
                    List<? extends MonitorEntity> datas = JSONArray.parseArray(addNodes.toJSONString(), cls);
                    
                    for(MonitorEntity me : datas){
                        me.setCreuser(session.getEmployeeId());
                        String id = ormService.insertSelective(me).toString();
                        me.setId(id);
                        
                        //属性集 - 临时单节点的资源集合
                        List<MoniMoniResSetaEntity> _res = me.getMoni_res_set();
                        
                        if(_res != null && !_res.isEmpty()){
                            EdmUtil.setPropertyBaseEntitiesSysColumns(cls, me, _res, SQLCurdEnum.INSERT);
                            ormService.insert(_res);
                        }
                    }
                }
                
                if(!updateNodes.isEmpty()){
                    List<? extends MonitorEntity> datas = JSONArray.parseArray(updateNodes.toJSONString(), cls);
                    for(MonitorEntity me : datas){
                        me.setModuser(session.getEmployeeId());
                        ormService.updateSelective(me);
                    }
                }
                
                if(!addRes.isEmpty()){
                    List<MoniMoniResSetaEntity> datas = JSONArray.parseArray(addRes.toJSONString(), MoniMoniResSetaEntity.class);
                    for(MoniMoniResSetaEntity me : datas){
                        me.setCreuser(session.getEmployeeId());
                        me.setClassName(EdmUtil.getEdmClassName(cls));
                        ormService.insertSelective(me);
                    }
                }
                
                // 生效日期和入库日期相同的 - 不写历史
                if(now.equals(getDate(t_begin, Constant.YYYY_MM_DD)))
                    break;
                    
                // 未来树不写历史
                if(tRootNode.getMoni_beg().after(new Date())) 
                    break;
                
               // 将tNodes 和 resources 加入到历史集中
               addNodes.clear();
                
                for(int k = 0; k < tNodes.size(); k++){
                    JSONObject node = tNodes.getJSONObject(k);
                    Date nn_beg = new Date(node.getLong("moni_beg"));
                    Date nn_end = new Date(node.getLong("moni_end"));
                    
                    if(nn_beg.after(now)) // 未来节点 - 未使用过
                        continue;
                    if(!(nn_beg.after(now) || nn_end.before(now))){
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        cal.add(Calendar.DATE, -1);
                        String str = (new SimpleDateFormat(Constant.YYYY_MM_DD)
                                .format(cal.getTime())) + Constant.ENDTIME;
                        Date ss = new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).parse(str);
                        node.put("moni_end", ss.getTime());
                    }else
                        node.put("moni_end", nn_end.getTime());
                    
                    String id = node.getString(Constant.ID);
                    node.put(Constant.PID, id);
                    node.remove(Constant.ID);
                    
                    JSONArray nodeRes = new JSONArray();
                    // 当前节点关联的资源
                    if(resources != null && !resources.isEmpty()){
                        for(int j = 0; j < resources.size(); j++){
                            JSONObject reObj = resources.getJSONObject(j);
                            if(!id.equals(reObj.getString("nodeId")))
                                continue;
                            JSONObject res = new JSONObject();
                            res.put("moni_res_id", reObj.getString(Constant.ID));
                            nodeRes.add(res);
                        }
                    }
                    
                    if(!nodeRes.isEmpty())
                        node.put("moni_res_set", nodeRes);
                    addNodes.add(setHis(node));
                }
                
                // 新增历史集
                if(!addNodes.isEmpty()){
                    
                    List<MoniMoniHisSetaEntity> datas = JSONArray.parseArray(addNodes.toJSONString(), MoniMoniHisSetaEntity.class);
                    
                    for(MoniMoniHisSetaEntity me : datas){
                        me.setCreuser(session.getEmployeeId());
                        me.setClassName(EdmUtil.getEdmClassName(cls));
                        String id = ormService.insertSelective(me).toString();
                        me.setId(id);
                        
                        //属性集 - 临时单节点的资源集合
                        List<MoniMoniHresSetbEntity> _res = me.getMoni_hres_set();
                        
                        if(_res != null && !_res.isEmpty()){
                            EdmUtil.setPropertyBaseEntitiesSysColumns(cls, me, _res, SQLCurdEnum.INSERT);
                            ormService.insert(_res);
                        }
                    }
                }
                
                break;
                
            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(),"临时单变更类型" + ErrorMessage._60000.getMsg());
        }
        
//        // 查询出所有的节点id
//        param.reset();
//        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
//        param.setWhereExp(param.getEqualXML(Constant.PID, orderId));
//        List<MtorMtorNodeSetaEntity> n_list = ormService.selectBeanList(MtorMtorNodeSetaEntity.class, param);
//        // TODO
//        Object[] ids = n_list.stream().map(MtorMtorNodeSetaEntity::getId).toArray();
//        
//        // 删除节点
//        param.reset();
//        param.setWhereExp(param.getInXML(Constant.ID, ids));
//        ormService.delete(MtorMtorNodeSetaEntity.class, param);
//        
//        // 删除资源
//        param.reset();
//        param.setWhereExp(param.getInXML(Constant.PID, ids));
//        ormService.delete(MtorMtorResSetbEntity.class, param);
          // 删除单据
//        ormService.delete(MonitortreeorderEntity.class, orderId);
        
        // 更新临时单 - 状态为 5 完成状态
        MonitortreeorderEntity orderEntity = new MonitortreeorderEntity();
        orderEntity.setId(orderId);
        orderEntity.setOrde_status(Constant.ORDER_STATUS_COMMIT);
        ormService.updateSelective(orderEntity);
        
        hashOps.getOperations().delete(orderId+ Constant.KEY_SEP +classId);
        hashOps.getOperations().delete(orderId+ Constant.KEY_SEP +classId + Constant.REVOKE_KEY);
        
        return orderId;
    }
    
    /**
     * 
     * setHis:正式表字段转成历史表字段信息
     * @author lijie
     * @param node
     * @return
     */
    private JSONObject setHis(JSONObject node) {
        JSONObject obj = new JSONObject();
        obj.put(Constant.PID, node.getString(Constant.PID));
        obj.put("moni_hnode_no", node.getString("moni_node_no"));
        obj.put("moni_hnode_name", node.getString("moni_node_name"));
        obj.put("moni_hnode_def", node.getString("moni_node_def"));
        obj.put("moni_hmajor", node.getString("moni_major"));
        obj.put("moni_hassit", node.getString("moni_assit"));
        obj.put("moni_hbeg", node.getLong("moni_beg"));
        obj.put("moni_hend", node.getLong("moni_end"));
        obj.put("moni_hindex_conf", node.getString("moni_index_conf"));
        obj.put("moni_hseq", node.get("moni_seq"));
        obj.put("moni_hlvl_code", node.getString("moni_lvl_code"));
        obj.put("moni_hlvl", node.get("moni_lvl"));
        obj.put("moni_henum", node.getString("moni_enum"));
        obj.put("moni_hrelate_cnd", node.getString("moni_relate_cnd"));
        
        JSONArray res = node.getJSONArray("moni_res_set");
        if(res != null && !res.isEmpty() ){
            JSONArray resources = new JSONArray();
            for(int i = 0; i < res.size(); i++){
                JSONObject reObj = new JSONObject();
                reObj.put("moni_hres_id", res.getJSONObject(i).getString("moni_res_id"));
                resources.add(reObj);
            }
            obj.put("moni_hres_set", resources);
        }
        return obj;
    }

    private JSONArray setValues(String key, List<NodeTo> nodes){
        
        JSONArray savaNodes = new JSONArray();
        
        for(NodeTo to : nodes){
            JSONObject node = new JSONObject();
            
            node.put(Constant.PID, key.split(Constant.KEY_SEP)[0]);
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
            if(!StringUtil.isNullOrEmpty(to.getRelateId()))
                node.put(Constant.ID, to.getRelateId());
            
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
            savaNodes.add(node);
        }
        
        return savaNodes;
    }
   
    private void checkNodeLegal(String classId, List<NodeTo> nodes){
        
        if(StringUtil.isNullOrEmpty(classId) || nodes == null || nodes.isEmpty())
            return;
        
        // 岗位树所有节点必须包含资源 而且 一个资源
        if(classId.equals(Constant.JOBPOSITIONCLASSID)){
            for(NodeTo to : nodes){
                List<ResourceTo> resources = to.getResources();
                if(to.getType() == ChangeType.INVALID.getValue())
                    continue;
                if(resources == null || resources.size() != 1)
                    ApplicationException.throwCodeMesg(ErrorMessage._60021.getCode(), ErrorMessage._60021.getMsg());
            }
        }
    }

    @Override
    public void submitWorkFlow(String key, String orderInstanceId) throws Exception {
        
        // 更新单据状态 - 待审
        MonitortreeorderEntity order = new MonitortreeorderEntity();
        order.setId(key.split(Constant.KEY_SEP)[0]);
        order.setOrde_status(Constant.ORDER_STATUS_WAIT);
        ormService.updateSelective(order);
        
        // 提交流程
        formService.submitWorkFlow(key.split(Constant.KEY_SEP)[0], orderInstanceId);
    }
    
}

