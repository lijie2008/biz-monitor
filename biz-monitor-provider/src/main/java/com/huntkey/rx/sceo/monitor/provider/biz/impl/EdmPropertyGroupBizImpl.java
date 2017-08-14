/**
 * Project Name:biz-monitor-provider
 * File Name:EdmPropertyGroupBizImpl.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.biz.impl
 * Date:2017年8月10日上午8:53:48
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.biz.EdmPropertyGroupBiz;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.orm.dao.EdmPropertyGroupDataMapper;

/**
 * ClassName:EdmPropertyGroupBizImpl
 * Function: edm属性关联分组表查询 业务逻辑层
 * Date:     2017年8月10日 上午8:53:48
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@Service("edmPropertyGroupBiz")
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
        }

        Map<String, Object> paramMap = getParamMap(jsonObject);

        //查询传参数结果 （应该只有一条）
        List<Map<String, Object>> list = edmPropertyGroupDataMapper.select(paramMap);

        if (list == null || list.isEmpty()) {
            result.setRetCode(Result.RECODE_ERROR);
            result.setErrMsg("根据参数查询结果为空");
            return result;
        }
        //取出数据
        Map<String, Object> map = list.get(0);
        Object obj = map.get("edpg_property_group");
        if (obj != null) {
            Integer edpgPropertyGroup = (Integer) obj;
            //查询查询map
            paramMap.clear();
            //设置查询参数为 分组值
            paramMap.put("edpg_property_group", edpgPropertyGroup);
            //根据 分组值查询
            List<Map<String, Object>> groupList = edmPropertyGroupDataMapper.select(paramMap);
            if (groupList != null && !groupList.isEmpty()) {
                JSONArray jsonArray = new JSONArray();
                for (Map<String, Object> m : groupList) {
                    Object o = m.get("edpg_edmc_id");
                    if (o != null) {
                        String edpgEdmcId = (String) o;
                        Result r = modelerClient.checkIsChileNode(edmMonitorId, edpgEdmcId);
                        if (r.getRetCode() == Result.RECODE_SUCCESS) {
                            Boolean bool = (Boolean) r.getData();
                            //v结果为true时 排除查询条件自身
                            if (bool && !edpgEdmcId.equals(jsonObject.get("edpg_edmc_id"))) {
                                Result re = modelerClient.queryEdmClassById(edpgEdmcId);
                                if (re.getRetCode() == Result.RECODE_SUCCESS
                                        && re.getData() != null) {
                                    JSONObject jsonObj = new JSONObject();
                                    
                                    JSONObject js = JsonUtil.getJson(re.getData());
                                    jsonObj.put("edmcNameEn", js.getString("edmcNameEn"));
                                    
                                    String edmcShortName = js.getString("edmcShortName") ;
                                    String tableName = getTableName(relate_feild,edmcShortName);
                                    
                                    jsonObj.put("tableName", tableName);
                                    jsonArray.add(jsonObj);
                                }
                            }
                        }
                    }
                }
                result.setData(jsonArray);
                result.setRetCode(Result.RECODE_SUCCESS);
            }

        }

        return result;
    }

    private Map<String, Object> getParamMap(JSONObject jsonObject) {

        Map<String, Object> paramMap = new HashMap<String, Object>();

        String edpg_property_group = jsonObject.getString("edpg_property_group");
        if (StringUtils.isNotBlank(edpg_property_group)) {
            paramMap.put("edpg_property_group", edpg_property_group);
        }

        String edpg_edmc_id = jsonObject.getString("edpg_edmc_id");
        if (StringUtils.isNotBlank(edpg_edmc_id)) {
            paramMap.put("edpg_edmc_id", edpg_edmc_id);
        }

        String edpg_edmp_id = jsonObject.getString("edpg_edmp_id");
        if (StringUtils.isNotBlank(edpg_edmp_id)) {
            paramMap.put("edpg_edmp_id", edpg_edmp_id);
        }

        return paramMap;
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
        if (edmName.startsWith(dot_split)
                || edmName.endsWith(dot_split) || edmName.contains("..")) {
            throw new IllegalArgumentException("参数edm名称'" + edmName + "'无效!");
        }

        String[] subs = edmName.split("\\.");
        int size = subs.length;
        char c = (char) (95 + size);

        String tableName = shortName + "_" + subs[size - 1] + c;

        return tableName;
    }

}
