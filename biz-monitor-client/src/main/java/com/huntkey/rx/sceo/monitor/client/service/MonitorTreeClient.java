package com.huntkey.rx.sceo.monitor.client.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.hystrix.MonitorTreeClientFallback;

/**
 * Created by zhaomj on 2017/8/11.
 */
@FeignClient(value = "biz-monitor-provider",fallback = MonitorTreeClientFallback.class)
public interface MonitorTreeClient {

    @RequestMapping(value = "/monitors/trees/nodes",method = RequestMethod.GET)
    Result getMonitorTreeNodes(@RequestParam(value = "rootEdmcNameEn") String rootEdmcNameEn,
                               @RequestParam(value = "searchDate") String searchDate,
                               @RequestParam(required = false,value = "rootNodeId") String rootNodeId,
                               @RequestParam(value="edmId") String edmId,
                               @RequestParam(value="flag", defaultValue = "false") boolean flag);


    @RequestMapping(value = "/monitors",method = RequestMethod.GET)
    Result getMonitors(@RequestParam(value = "treeName", required = false) String treeName,
                              @RequestParam(value = "beginTime") String beginTime,
                              @RequestParam(value = "endTime") String endTime);

    @RequestMapping(value = "/monitors/trees",method = RequestMethod.GET)
    Result getMonitorTrees(@RequestParam(value = "treeName", required = false) String treeName,
                           @RequestParam(value = "edmcNameEn") String edmcNameEn,
                           @RequestParam (value = "edmId") String edmId,
                           @RequestParam(value = "beginTime", required = false) String beginTime,
                           @RequestParam(value = "endTime", required = false) String endTime);

    @RequestMapping("/monitors/conproperties")
    Result getConProperties(@RequestParam(value = "edmcNameEn") String edmcNameEn,
                            @RequestParam(value = "enable",defaultValue = "true") boolean enable);

    @RequestMapping("/monitors/newDate")
    Result getNewMonitorTreeStartDate(@RequestParam(value = "edmcNameEn") String edmcNameEn);
    
    @RequestMapping("/monitors/search")
    Result searchResourceObj(@RequestParam(value = "resourceClassId") String resourceClassId,
    		@RequestParam(value = "resourceValue") String resourceValue);

}
