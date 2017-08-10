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
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerProviderClient;
import com.huntkey.rx.sceo.monitor.provider.orm.dao.EdmPropertyGroupDataMapper;

/**
 * ClassName:EdmPropertyGroupBizImpl
 * Function: TODO ADD FUNCTION
 * Date:     2017年8月10日 上午8:53:48
 * @author   caozhenx
 * @version  
 * @see 	 
 */
@Service("edmPropertyGroupBiz")
public class EdmPropertyGroupBizImpl implements EdmPropertyGroupBiz {
    
    @Value("${edm.edmMonitorId}")
    private String edmMonitorId ;

    @Autowired
    EdmPropertyGroupDataMapper edmPropertyGroupDataMapper;
    
    @Autowired
    ModelerProviderClient modelerProviderClient;

    @Override
    public Result getMonitorIds(JSONObject jsonObject) {

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
            if(groupList != null && !groupList.isEmpty()){
                JSONArray jsonArray = new JSONArray();
                for(Map<String, Object> m : list){
                    Object o = m.get("edpg_edmc_id");
                    if(o != null){
                        String edpgEdmcId = (String)o;
                        Result r = modelerProviderClient.checkIsChileNode(edmMonitorId,edpgEdmcId);
                        if(r.getRetCode() == Result.RECODE_SUCCESS){
                            Boolean bool = (Boolean) r.getData();
                            //v结果为true时 排除查询条件自身
                            if(bool && !edpgEdmcId.equals(jsonObject.get("edpg_edmc_id"))){
                                jsonArray.add(JsonUtil.getJson(m));
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

}
