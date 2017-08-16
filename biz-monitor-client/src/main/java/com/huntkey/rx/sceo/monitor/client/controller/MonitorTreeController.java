package com.huntkey.rx.sceo.monitor.client.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeClient;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/11.
 */
@RestController
@RequestMapping("/v1/monitors")
public class MonitorTreeController {

    @Autowired
    MonitorTreeClient treeClient;

    @GetMapping("/trees/nodes")
    public Result getMonitorTreeNodes(@RequestParam String edmcNameEn,
                                      @RequestParam String searchDate,
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
                                  @RequestParam String edmcNameEn,
                                  @RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime) {
        Result result = treeClient.getMonitorTrees(treeName, edmcNameEn, beginTime, endTime);
        return result;
    }

    @GetMapping("/trees/resources")
    public Result getNodeResources(@RequestParam(required = false) String name,
                                   @RequestParam List<String> nodes,
                                   @RequestParam String edmcId) {
        Result result = treeClient.getNodeResources(name, nodes, edmcId);
        return result;
    }

    @GetMapping("/conproperties")
    public Result getConProperties(@RequestParam(value = "edmcNameEn") @NotBlank String edmcNameEn,
                                   @RequestParam(value = "enable",defaultValue = "true") boolean enable){
        Result result = treeClient.getConProperties(edmcNameEn,enable);
        return result;
    }

    @GetMapping("/{edmcNameEn}/newDate")
    public Result getNewMonitorTreeStartDate(@PathVariable(value = "edmcNameEn") String edmcNameEn){
        Result result = treeClient.getNewMonitorTreeStartDate(edmcNameEn);
        return result;
    }


}
