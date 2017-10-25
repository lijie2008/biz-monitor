package com.huntkey.rx.sceo.monitor.client.service;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.hystrix.MonitorClientFallback;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "biz-monitor-provider", fallback = MonitorClientFallback.class)
public interface MonitorClient {

    @RequestMapping(value = "/monitors/nodeDetail")
    Result nodeDetail(@RequestParam(value = "key") String key,
    		@RequestParam(value = "levelCode") String levelCode);

    @RequestMapping(value = "/monitors/saveNodeDetail", method = RequestMethod.POST)
    Result saveNodeDetail(@RequestBody NodeTo nodeDetail);

    @RequestMapping(value = "/monitors/deleteNodeResource")
    Result deleteNodeResource(@RequestParam(value = "key") String key,
    						  @RequestParam(value = "levelCode") String levelCode,
                              @RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = "/monitors/addResource")
    Result addResource(@RequestParam(value = "key") String key,
                       @RequestParam(value = "levelCode") String levelCode,
                       @RequestParam(value = "resourceId") String resourceId,
                       @RequestParam(value = "resourceText") String resourceText);

    @RequestMapping(value = "/monitors/addNode", method = RequestMethod.GET)
    Result addNode(@RequestParam(value = "key") String key,
    			   @RequestParam(value = "levelCode") String levelCode,
                   @RequestParam(value = "type") int type);

    @RequestMapping(value = "/monitors/deleteNode", method = RequestMethod.GET)
    Result deleteNode(@RequestParam(value = "key") String key,
    				  @RequestParam(value = "levelCode") String levelCode,
                      @RequestParam(value = "type") int type);

    @RequestMapping(value = "/monitors/moveNode", method = RequestMethod.GET)
    Result moveNode(@RequestParam(value = "key") String key,
                    @RequestParam(value = "moveLvlCode") String moveLvlCode,
                    @RequestParam(value = "desLvlCode") String desLvlCode,
                    @RequestParam(value = "type") int type
    );
}
