/**
 * Project Name:biz-monitor-client
 * File Name:MonitorTreeOrderService.java
 * Package Name:com.huntkey.rx.sceo.monitor.client.service
 * Date:2017年8月11日下午3:57:23
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.hystrix.MonitorTreeOrderServiceFallback;

/**
 * ClassName:MonitorTreeOrderService
 * Date:     2017年8月11日 下午3:57:23
 * @author   lijie
 * @version  
 * @see 	 
 */
@FeignClient(value = "biz-monitor-provider", fallback = MonitorTreeOrderServiceFallback.class)
public interface MonitorTreeOrderService {
    
    /**
     * 
     * queryNotUsingResource:查询节点未分配资源信息
     * @author lijie
     * @param orderId 临时单id
     * @param nodeId 节点id
     * @param currentPage 当前页
     * @param pageSize 页数
     * @return
     */
    @RequestMapping(value="/monitor/queryNotUsingResource", method = RequestMethod.GET)
    public Result queryNotUsingResource(@RequestParam(value="orderId",required=true) String orderId, @RequestParam(value="nodeId",required=true) String nodeId, 
                                        @RequestParam(value="currentPage", defaultValue = "1",required=false) int currentPage, @RequestParam(value="pageSize", defaultValue="20",required=false) int pageSize);

    
    /**
     * 
     * checkNodeResource: 节点时间区间修改检查
     * @author lijie
     * @param nodeId 节点ID
     * @param startDate 生效时间
     * @param endDate 失效时间
     * @return
     */
    @RequestMapping(value="/monitor/checkNodeResource", method = RequestMethod.GET)
    public Result checkNodeResource(@RequestParam(value="nodeId",required=true) String nodeId, @RequestParam(value="startDate",required=true) String startDate,
                                    @RequestParam(value="endDate",required=true) String endDate);

    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/monitor/addOtherNode", method = RequestMethod.GET)
    public Result addOtherNode(@RequestParam(value="orderId",required=true) String orderId);
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/monitor/store", method = RequestMethod.GET)
    public Result store(@RequestParam(value="orderId",required=true) String orderId);
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    @RequestMapping(value="/monitor/revoked", method = RequestMethod.GET)
    public Result revoked(@RequestParam(value="orderId",required=true) String orderId);


    /**
     * 
     * checkAvailableResource:校验是否存在资源未分配
     * @author lijie
     * @param orderId 临时单id
     * @return
     */
    @RequestMapping(value="/monitor/resources", method = RequestMethod.GET)
    public Result checkAvailableResource(@RequestParam(value="orderId",required=true) String orderId);
    
}

