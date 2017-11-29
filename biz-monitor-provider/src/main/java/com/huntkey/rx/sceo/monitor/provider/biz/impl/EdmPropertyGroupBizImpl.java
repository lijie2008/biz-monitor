/**
 * Project Name:biz-monitor-provider
 * File Name:EdmPropertyGroupBizImpl.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.biz.impl
 * Date:2017年8月10日上午8:53:48
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.biz.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.biz.EdmPropertyGroupBiz;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.orm.dao.EdmPropertyGroupDataMapper;

/**
 * ClassName:EdmPropertyGroupBizImpl
 * Function: edm属性信息查询 业务逻辑层
 * Date:     2017年8月10日 上午8:53:48
 * @author   caozhenx
 * @version  
 * @see 	 
 */
//@Service("edmPropertyGroupBiz")
public class EdmPropertyGroupBizImpl implements EdmPropertyGroupBiz {

    private String dot_split = ".";

    private String relate_feild = "depttree.moni015";

    @Value("${edm.edmMonitorId}")
    private String edmMonitorId;

    @Autowired
    EdmPropertyGroupDataMapper edmPropertyGroupDataMapper;

    @Autowired
    ModelerClient modelerClient;

    @Override
    public Result getMonitorInfo(JSONObject jsonObject) {

        Result result = new Result();

        if (jsonObject == null || jsonObject.isEmpty()) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("查询条件不可为空");
            return result;
        }

        Object edpgEdmcIdObj = jsonObject.get("edpg_edmc_id");//监管类id
        Object edpgEdmpIdObj = jsonObject.get("edpg_edmp_id");//监管属性id

        if (edpgEdmcIdObj != null && edpgEdmpIdObj != null) {

            //类id
            String edpgEdmcId = (String) edpgEdmcIdObj;
            //属性id
            String edpgEdmpId = (String) edpgEdmpIdObj;

            //根据类id判断是否为监管类的子孙节点
            Result isChileNodeResult = modelerClient.checkIsChileNode(edmMonitorId, edpgEdmcId);
            if (isChileNodeResult.getRetCode() == Result.RECODE_SUCCESS) {
                
                Boolean bool = (Boolean) isChileNodeResult.getData();
                if (bool) {//是监管类的子孙类时  添加返回结果

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("edpgEdmcId", edpgEdmcId);
                    jsonObj.put("edpgEdmpId", edpgEdmpId);

                    //根据id查询edm类信息
                    Result edmClassResult = modelerClient.queryEdmClassById(edpgEdmcId);
                    if (edmClassResult.getRetCode() == Result.RECODE_SUCCESS && edmClassResult.getData() != null) {

                        JSONObject js = JsonUtil.getJson(edmClassResult.getData());
                        jsonObj.put("edmcNameEn", js.getString("edmcNameEn"));

                        String edmcShortName = js.getString("edmcShortName");
                        String tableName = getTableName(relate_feild, edmcShortName);
                        jsonObj.put("tableName", tableName);
                    }

                    //根据属性id查询属性公式
                    Result propertyFormulaResult = modelerClient.getPropertyFormula(edpgEdmpId);
                    if (propertyFormulaResult.getRetCode() == Result.RECODE_SUCCESS) {
                        jsonObj.put("formula", propertyFormulaResult.getData());
                    }

                    result.setData(jsonObj);
                    result.setRetCode(Result.RECODE_SUCCESS);
                } else {
                    result.setErrMsg("传入参数非监管类id,edpgEdmcId:" + edpgEdmcId);
                    result.setRetCode(Result.RECODE_ERROR);
                }
            }else{
                result.setErrMsg("检验id是否为监管类子孙节点出错，" + isChileNodeResult.getErrMsg());
                result.setRetCode(Result.RECODE_ERROR);
            }

        }else{
            result.setErrMsg("监管类id，或属性id为null");
            result.setRetCode(Result.RECODE_ERROR);
        }

        return result;
    }

    private String getTableName(String edmName, String shortName) {
        if (StringUtils.isBlank(edmName)) {
            return null;
        }

        // 去掉前后空格
        edmName = edmName.trim();
        while (edmName.startsWith(" ")) {
            edmName = edmName.substring(1);
        }

        //        // 如果参数不包含“.”, 说明传入的就是类名，因为约定，类名就是表名，直接返回；
        //        if (!edmName.contains(dot_split)) {
        //            // 如果以 _link结尾的，表示操作关联表
        //            if (edmName.endsWith("_link")) {
        //                return shortName + suffix;
        //            } else {
        //                return edmName;
        //            }
        //        }

        // 有效性校验，1. 不用用'.'打头或者结尾， 2. 不能包含二个或以上连续的'.',
        if (edmName.startsWith(dot_split) || edmName.endsWith(dot_split) || edmName.contains("..")) {
            throw new IllegalArgumentException("参数edm名称'" + edmName + "'无效!");
        }

        String[] subs = edmName.split("\\.");
        int size = subs.length;
        char c = (char) (95 + size);

        String tableName = shortName + "_" + subs[size - 1] + c;

        return tableName;
    }

}
