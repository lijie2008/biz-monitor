package com.huntkey.rx.sceo.monitor.provider.service;


import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;

/**
 * Created by zhaomj on 2017/8/9.
 */
public interface MonitorTreeService {
    
    /**
     * 
     * getEntityByVersionAndEnglishName:查询监管树列表清单
     * @author lijie
     * @param treeName 树名称
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    Result getEntityByVersionAndEnglishName(String treeName, String beginTime, String endTime) throws Exception;
    
    JSONArray getMonitorTrees(String treeName, String edmcNameEn, String edmId, String beginTime, String endTime) throws Exception;

    JSONObject getMonitorTreeNodes(String rootEdmcNameEn, String startDate, String endDate, String rootNodeId) throws Exception;

    JSONArray getNodeResources(String name, List<String> nodes, String edmId, String edmName,int type) throws Exception;

    JSONArray getConProperties(String edmcNameEn, boolean enable);

    JSONObject getNewMonitorTreeStartDate(String edmcNameEn) throws Exception;
    
    JSONArray getChileNodes(String nodeId ,String edmcNameEn);
    
    JSONArray searchResourceObj(String resourceClassId,String resourceValue) throws Exception;
}
