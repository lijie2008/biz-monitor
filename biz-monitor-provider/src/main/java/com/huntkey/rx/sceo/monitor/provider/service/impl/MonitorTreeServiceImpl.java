package com.huntkey.rx.sceo.monitor.provider.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.model.ConditionParam;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
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
    private String edmcNameEn;

    @Override
    public Result getEntityByVersionAndEnglishName(String treeName, String beginTime, String endTime) {


        Result monitorClassesResult = serviceCenterClient.getMonitorClasses(treeName, beginTime, endTime, edmdVer, edmcNameEn);
        if (monitorClassesResult.getRetCode() != Result.RECODE_SUCCESS) {
            throw new ServiceException(monitorClassesResult.getErrMsg());
        }
        return monitorClassesResult;
    }

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
}
