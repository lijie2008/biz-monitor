package com.huntkey.rx.sceo.monitor.provider.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/9.
 */
public interface MonitorTreeService {

    /**
     * 查询某个时间的指定监管树所有节点
     * 若有根节点ID则根据根节点ID查询
     * 无根节点ID则先根据时间查询出根节点ID
     *
     * @param edmcNameEn
     * @param searchDate
     * @return
     */
    JSONArray getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId);

    /**
     * 查询监管树类列表，并根据查询条件统计监管类下监管树的数量
     * @param treeName
     * @param beginTime
     * @param endTime
     * @return
     */
    Result getEntityByVersionAndEnglishName(String treeName, String beginTime, String endTime);

    JSONArray getMonitorTrees(String treeName, String edmcNameEn, String beginTime, String endTime);

    JSONArray getNodeResources(String name, List<String> nodes, String edmcId);

    JSONArray getConProperties(String edmcNameEn, boolean enable);

    JSONObject getNewMonitorTreeStartDate(String edmcNameEn);
    
    /**
     * getChileNodes:根据节点id查询其子节点信息
     * @author caozhenx
     * @param nodeId
     * @param edmcNameEn 
     * @return
     */
    public JSONArray getChileNodes(String nodeId ,String edmcNameEn);
}
