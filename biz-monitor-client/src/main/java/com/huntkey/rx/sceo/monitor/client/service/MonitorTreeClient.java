package com.huntkey.rx.sceo.monitor.client.service;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.hystrix.MonitorTreeClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/11.
 */
@FeignClient(value = "biz-monitor-provider",fallback = MonitorTreeClientFallback.class)
public interface MonitorTreeClient {

    @RequestMapping(value = "/monitors/trees/nodes",method = RequestMethod.GET)
    Result getMonitorTreeNodes(@RequestParam(value = "edmcNameEn") String edmcNameEn,
                               @RequestParam(value = "searchDate") String searchDate,
                               @RequestParam(required = false,value = "rootNodeId") String rootNodeId);


    @RequestMapping(value = "/monitors",method = RequestMethod.GET)
    Result getMonitors(@RequestParam(value = "treeName", required = false) String treeName,
                              @RequestParam(value = "beginTime") String beginTime,
                              @RequestParam(value = "endTime") String endTime);

    @RequestMapping(value = "/monitors/trees",method = RequestMethod.GET)
    Result getMonitorTrees(@RequestParam(value = "treeName", required = false) String treeName,
                           @RequestParam(value = "edmcNameEn") String edmcNameEn,
                           @RequestParam(value = "beginTime", required = false) String beginTime,
                           @RequestParam(value = "endTime", required = false) String endTime);


    @RequestMapping(value = "/monitors/trees/resources",method = RequestMethod.GET)
    Result getNodeResources(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "nodes") List<String> nodes,
                            @RequestParam(value = "edmcId") String edmcId);

    @RequestMapping("/monitors/conproperties")
    Result getConProperties(@RequestParam(value = "edmcNameEn") String edmcNameEn,
                            @RequestParam(value = "enable",defaultValue = "true") boolean enable);

    @RequestMapping("/monitors/newDate")
    Result getNewMonitorTreeStartDate(@RequestParam(value = "edmcNameEn") String edmcNameEn,@RequestParam(value = "classId") String classId);
}
