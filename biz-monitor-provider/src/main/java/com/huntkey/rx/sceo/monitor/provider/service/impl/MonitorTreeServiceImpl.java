package com.huntkey.rx.sceo.monitor.provider.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.model.ConditionParam;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/9.
 */
@Service
public class MonitorTreeServiceImpl implements MonitorTreeService {

    @Autowired
    ServiceCenterClient serviceCenterClient;
    
    @Value("${edm.version}")
    private String edmdVer;
    
    @Value("${edm.edmcNameEn.monitor}")
    private String monitorEdmcNameEn;

    @Override
    public Result getEntityByVersionAndEnglishName(String treeName, String beginTime, String endTime) {


        Result monitorClassesResult = serviceCenterClient.getMonitorClasses(treeName, beginTime, endTime, edmdVer, monitorEdmcNameEn);
        if (monitorClassesResult.getRetCode() != Result.RECODE_SUCCESS) {
            throw new ServiceException(monitorClassesResult.getErrMsg());
        }
        return monitorClassesResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONArray getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId) {

        //组装参数
        JSONObject requestParams = new JSONObject();

        JSONObject search = new JSONObject();

        String characters[] = new String[]{"moni001", "moni002", "moni006", "moni007", "moni008", "moni009"};
        search.put("columns", characters);

        JSONArray conditions = new JSONArray();

        if (StringUtil.isNullOrEmpty(rootNodeId)) {
            //根据时间查询根节点
            ConditionParam beginDateParam = new ConditionParam();
            beginDateParam.setAttr("moni004");
            beginDateParam.setOperator("<=");
            beginDateParam.setValue(searchDate);
            conditions.add(beginDateParam);

            ConditionParam endDateParam = new ConditionParam();
            endDateParam.setAttr("moni005");
            endDateParam.setOperator(">");
            endDateParam.setValue(searchDate);
            conditions.add(endDateParam);

            ConditionParam parentNodeParam = new ConditionParam();
            parentNodeParam.setAttr("moni006");
            parentNodeParam.setOperator("=");
            parentNodeParam.setValue("null");
            conditions.add(parentNodeParam);

        } else {
            //根据ID查询跟节点
            ConditionParam nodeIdParam = new ConditionParam();
            nodeIdParam.setAttr("id");
            nodeIdParam.setOperator("=");
            nodeIdParam.setValue(rootNodeId);

            conditions.add(nodeIdParam);
        }

        search.put("conditions", conditions);

        requestParams.put("edmName", edmcNameEn);
        requestParams.put("search", search);

        JSONObject rootNode;

        Result rootNodeResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());
        if (rootNodeResult.getRetCode() != Result.RECODE_SUCCESS) {
            throw new ServiceException(rootNodeResult.getErrMsg());
        } else {
            JSONObject rootData = JSONObject.parseObject(JSONObject.toJSONString(rootNodeResult.getData()));


            JSONArray rootArray = rootData.getJSONArray("dataset");

            if (rootArray.size() == 1) {
                rootNode = rootArray.getJSONObject(0);

                rootNodeId = rootNode.getString("id");

                //根据根节点ID，查询所有子节点
                Result childrenNpdeResult = serviceCenterClient.getMonitorTreeNodes(edmcNameEn, searchDate, rootNodeId);
                if (childrenNpdeResult.getRetCode() != Result.RECODE_SUCCESS) {
                    throw new ServiceException(childrenNpdeResult.getErrMsg());
                } else {
                    JSONArray childrenArray = new JSONArray((List<Object>) childrenNpdeResult.getData());
                    childrenArray.add(rootNode);
                    return childrenArray;
                }

            } else {
                throw new ServiceException("没有找到，或找到多个监管树！");
            }

        }
    }

    @Override
    public JSONArray getMonitorTrees(String treeName, String edmcNameEn, String beginTime, String endTime) {
        JSONArray monitorTrees = new JSONArray();

        JSONObject requestParams = new JSONObject();
        JSONObject search = new JSONObject();

        String characters[] = new String[]{"moni001", "moni002", "moni004", "moni005"};
        search.put("columns", characters);

        JSONArray conditions = new JSONArray();

        ConditionParam nodeIdParam = new ConditionParam();
        nodeIdParam.setAttr("moni006");
        nodeIdParam.setOperator("=");
        nodeIdParam.setValue("null");
        conditions.add(nodeIdParam);

        if (!StringUtil.isNullOrEmpty(treeName)) {
            ConditionParam treeNameParam = new ConditionParam();
            treeNameParam.setAttr("moni002");
            treeNameParam.setOperator("like");
            treeNameParam.setValue(treeName);
            conditions.add(treeNameParam);
        }

        //ORM暂不支持or查询，先只根据失效时间过滤
        if (!StringUtil.isNullOrEmpty(endTime)) {
            ConditionParam treeTimeParam = new ConditionParam();
            treeTimeParam.setAttr("moni005");
            treeTimeParam.setOperator("<");
            treeTimeParam.setValue(endTime);
            conditions.add(treeTimeParam);
        }

        search.put("conditions", conditions);

        requestParams.put("edmName", edmcNameEn);
        requestParams.put("search", search);

        Result treesResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());

        if (treesResult.getRetCode() == Result.RECODE_SUCCESS) {
            if (treesResult.getData() != null) {
                JSONObject treeData = JSONObject.parseObject(JSONObject.toJSONString(treesResult.getData()));
                JSONArray treeArray = treeData.getJSONArray("dataset");

                for (int i = 0; i < treeArray.size(); i++) {
                    JSONObject temp = treeArray.getJSONObject(i);
                    JSONObject tree = new JSONObject();
                    tree.put("rootNodeId", temp.getString("id"));
                    tree.put("rootNodeName", temp.getString("moni002"));
                    tree.put("beginTime", temp.getString("moni004"));
                    tree.put("endTime", temp.getString("moni005"));
                    monitorTrees.add(tree);
                }
            }
        } else {
            throw new ServiceException(treesResult.getErrMsg());
        }

        return monitorTrees;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONArray getNodeResources(String name, List<String> nodes, String edmcId) {
        Result resourcesResult = serviceCenterClient.getNodeResources(name,nodes,edmcId);
        if(resourcesResult.getRetCode()==Result.RECODE_SUCCESS){
            JSONArray childrenArray = new JSONArray((List<Object>) resourcesResult.getData());

            return childrenArray;
        }else {
            throw new ServiceException(resourcesResult.getErrMsg());
        }
    }
    
    /**
     * getChileNodes:根据节点id查询其子节点信息
     * @author caozhenx
     * @param nodeId
     * @return
     */
    public JSONArray getChileNodes(String nodeId ,String edmcNameEn){
        
        if(StringUtils.isNotBlank(nodeId)){
          //查询条件
            JSONObject json = new JSONObject();
            JSONObject search = new JSONObject();
            JSONArray conditions = new JSONArray();

            //父节点id
            if (StringUtils.isNotBlank(nodeId)) {
                JSONObject condition1 = new JSONObject();
                condition1.put(ServiceCenterConstant.ATTR, "moni006");
                condition1.put(ServiceCenterConstant.OPERATOR, ServiceCenterConstant.SYMBOL_EQUAL);
                condition1.put(ServiceCenterConstant.VALUE, nodeId);
                conditions.add(condition1);
            }

            search.put(ServiceCenterConstant.CONDITIONS, conditions);

            //edm类名称
            json.put(ServiceCenterConstant.EDM_NAME, edmcNameEn);
            json.put(ServiceCenterConstant.SEARCH, search);
            
            Result result = serviceCenterClient.queryServiceCenter(json.toJSONString());
            
            if(result != null && result.getRetCode()==Result.RECODE_SUCCESS){
                JSONObject jsonObj =  JsonUtil.getJson(result.getData());
                JSONArray jsonArray = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);

                return jsonArray;
            }else {
                throw new ServiceException(result.getErrMsg());
            }
            
        }
        
        return null;
    }
    
}
