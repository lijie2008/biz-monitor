package com.huntkey.rx.sceo.monitor.provider.service;


import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;

/**
 * Created by zhaomj on 2017/8/9.
 */
public interface MonitorTreeService {
    
    Result getEntityByVersionAndEnglishName(String treeName, String beginTime, String endTime);
    
    JSONArray getMonitorTrees(String treeName, String edmcNameEn, String edmId, String beginTime, String endTime);

    JSONObject getMonitorTreeNodes(String rootEdmcNameEn, String searchDate, String rootNodeId);

    JSONArray getNodeResources(String name, List<String> nodes, String edmId, String edmName,int type);

    JSONArray getConProperties(String edmcNameEn, boolean enable);

    JSONObject getNewMonitorTreeStartDate(String edmcNameEn);
    
    JSONArray getChileNodes(String nodeId ,String edmcNameEn);
    
    JSONArray searchResourceObj(String resourceClassId,String resourceValue);
}
