package com.huntkey.rx.sceo.monitor.provider.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.serviceCenter.common.model.ConditionParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.PagenationParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SortParam;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;

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
    @Autowired
    ModelerClient modelerClient;
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
            ConditionParam beginDateParam = new ConditionParam("moni004","<=",searchDate);
            conditions.add(beginDateParam);

            ConditionParam endDateParam = new ConditionParam("moni005",">",searchDate);
            conditions.add(endDateParam);

            ConditionParam parentNodeParam = new ConditionParam("moni006","=","null");
            conditions.add(parentNodeParam);

        } else {
            //根据ID查询跟节点
            ConditionParam nodeIdParam = new ConditionParam("id","=",rootNodeId);
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
//                throw new ServiceException("没有找到，或找到多个监管树！");
                return null;
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

        ConditionParam nodeIdParam = new ConditionParam("moni006","=","null");
        conditions.add(nodeIdParam);

        if (!StringUtil.isNullOrEmpty(treeName)) {
            ConditionParam treeNameParam = new ConditionParam("moni002","like",treeName);
            conditions.add(treeNameParam);
        }

        //ORM暂不支持or查询，先只根据失效时间过滤
        if (!StringUtil.isNullOrEmpty(endTime)) {
            ConditionParam treeTimeParam = new ConditionParam("moni005","<=",endTime);
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

    @Override
    public JSONArray getConProperties(String edmcNameEn, boolean enable) {
        Result resourcesResult = modelerClient.getConProperties(edmdVer,edmcNameEn);
        if(resourcesResult.getRetCode()==Result.RECODE_SUCCESS){
            JSONArray allConPropertes = new JSONArray((List<Object>) resourcesResult.getData());
            allConPropertes.removeIf(o -> {
                JSONObject object = (JSONObject)JSONObject.toJSON(o);
                return object.getBoolean("isVisible")!=enable;
            });
            return allConPropertes;
        }else {
            throw new ServiceException(resourcesResult.getErrMsg());
        }
    }

    @Override
    public JSONObject getNewMonitorTreeStartDate(String edmcNameEn) {

        JSONObject resultData = new JSONObject();

        JSONObject requestParams = new JSONObject();
        JSONObject search = new JSONObject();

        JSONArray conditions = new JSONArray();

        ConditionParam nodeIdParam = new ConditionParam("moni006","=","null");
        conditions.add(nodeIdParam);

        //统计所有根节点
        search.put("conditions", conditions);

        requestParams.put("edmName", edmcNameEn);
        requestParams.put("search", search);

        Result counttreeResult = serviceCenterClient.countByConditions(requestParams.toJSONString());
        if(counttreeResult.getRetCode()==Result.RECODE_SUCCESS){
            JSONObject object = (JSONObject)JSONObject.toJSON(counttreeResult.getData());
            int count = object.getIntValue("count");
            if(count<=0){
                resultData.put("type",2);
            }else {
                //统计没有失效时间的根节点
                ConditionParam treeTimeParam = new ConditionParam("moni005","=","null");
                conditions.add(treeTimeParam);

                search.put("conditions", conditions);
                requestParams.put("search", search);

                Result countResult = serviceCenterClient.countByConditions(requestParams.toJSONString());
                if(countResult.getRetCode()==Result.RECODE_SUCCESS){
                    JSONObject maxObject = (JSONObject)JSONObject.toJSON(countResult.getData());
                    int maxCount = maxObject.getIntValue("count");
                    if(maxCount>0){
                        resultData.put("type",3);
                    }else {
                        //查询最大失效时间
                        conditions.clear();
                        conditions.add(nodeIdParam);
                        search.put("conditions", conditions);

                        SortParam sortParam = new SortParam("moni005","desc");
                        JSONArray sorts = new JSONArray();
                        sorts.add(sortParam);

                        PagenationParam page = new PagenationParam();
                        page.setRows(1);
                        page.setStartpage(1);


                        String characters[] = new String[]{"moni005"};
                        search.put("columns", characters);

                        search.put("orderby",sorts);
                        requestParams.put("search", search);
                        Result treeResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());
                        if(treeResult.getRetCode()==Result.RECODE_SUCCESS){
                            JSONObject data = (JSONObject)JSONObject.toJSON(treeResult.getData());
                            JSONArray rootArray = data.getJSONArray("dataset");
                            if(rootArray.size()>0){
                                resultData.put("type",1);
                                resultData.put("date",rootArray.getJSONObject(0).getString("moni005"));
                            }
                        }
                    }
                }
            }
        }
        return resultData;
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
