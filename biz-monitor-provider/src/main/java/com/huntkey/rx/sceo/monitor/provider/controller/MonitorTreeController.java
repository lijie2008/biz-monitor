package com.huntkey.rx.sceo.monitor.provider.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaomj on 2017/8/9.
 */

@RestController
@RequestMapping("/monitors")
public class MonitorTreeController {

    @Autowired
    MonitorTreeService monitorTreeService;

    @GetMapping("/trees/nodes")
    public Result getMonitorTreeNodes(@RequestParam String edmcNameEn,
                                      @RequestParam String searchDate,
                                      @RequestParam(required = false,defaultValue = "") String rootNodeId){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getMonitorTreeNodes(edmcNameEn,searchDate,rootNodeId));
        return result;
    }

    @GetMapping
    public Result getMonitors(@RequestParam(required = false) String treeName,
                              @RequestParam String beginTime,
                              @RequestParam String endTime){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getEntityByVersionAndEnglishName(treeName,beginTime,endTime));
        return result;
    }
}
