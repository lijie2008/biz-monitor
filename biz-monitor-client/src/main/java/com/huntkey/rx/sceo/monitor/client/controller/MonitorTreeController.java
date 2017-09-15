package com.huntkey.rx.sceo.monitor.client.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeClient;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/11.
 */
@RestController
@RequestMapping("/v1/monitors")
@Validated
public class MonitorTreeController {

    @Autowired
    MonitorTreeClient treeClient;
    @GetMapping("/trees/nodes")
    public Result getMonitorTreeNodes(@RequestParam @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                      @RequestParam @NotBlank(message = "查询日期不能为空") String searchDate,
                                      @RequestParam(required = false, defaultValue = "") String rootNodeId) {
        Result result = treeClient.getMonitorTreeNodes(edmcNameEn, searchDate, rootNodeId);
        return result;
    }

    @GetMapping
    public Result getMonitors(@RequestParam(required = false) String treeName,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime) {
        Result result = treeClient.getMonitors(treeName, beginTime, endTime);
        return result;
    }

    @GetMapping("/trees")
    public Result getMonitorTrees(@RequestParam(required = false) String treeName,
                                  @RequestParam @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                  @RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime) {
        Result result = treeClient.getMonitorTrees(treeName, edmcNameEn, beginTime, endTime);
        return result;
    }

    @GetMapping("/trees/resources")
    public Result getNodeResources(@RequestParam(required = false) String name,
                                   @RequestParam @NotEmpty(message = "树节点集合不能为空") List<String> nodes,
                                   @RequestParam @NotBlank(message = "监管类ID不能为空") String edmcId) {
        Result result = treeClient.getNodeResources(name, nodes, edmcId);
        return result;
    }

    @GetMapping("/conproperties")
    public Result getConProperties(@RequestParam(value = "edmcNameEn") @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                   @RequestParam(value = "enable",defaultValue = "true") boolean enable){
        Result result = treeClient.getConProperties(edmcNameEn,enable);
        return result;
    }

    @GetMapping("/newDate")
    public Result getNewMonitorTreeStartDate(@RequestParam(value = "edmcNameEn") @NotBlank(message = "类英文名不能为空") String edmcNameEn,
    		@RequestParam(value="classId") @NotBlank(message="监管类ID不能为空") String classId){
        Result result = treeClient.getNewMonitorTreeStartDate(edmcNameEn,classId);
        return result;
    }
    @GetMapping("/search")
    public Result searchResourceObj(@RequestParam(value = "resourceClassId") @NotBlank(message = "资源类ID不能为空") String resourceClassId,
                                    @RequestParam(value = "resourceValue") @NotBlank(message = "资源对象值不能为空") String resourceValue){
        return treeClient.searchResourceObj(resourceClassId,resourceValue);
    }
    
    /**
     * 查询目标表所有的节点和资源信息
     * @param edmcNameEn
     * @param searchDate
     * @param rootNodeId
     * @param edmcId
     * @return
     */
    @GetMapping("/trees/nodesAndResources")
    public Result getMonitorTreeNodesAndResource(@RequestParam(value="edmcNameEn") @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                      @RequestParam(value="searchDate") @NotBlank(message = "查询日期不能为空") String searchDate,
                                      @RequestParam(value = "rootNodeId",required = false,defaultValue = "") String rootNodeId,
                                      @RequestParam(value = "edmcId") @NotBlank(message = "监管类ID不能为空") String edmcId){
        return treeClient.getMonitorTreeNodesAndResource(edmcNameEn,searchDate,rootNodeId,edmcId);
    }
}
