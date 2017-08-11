/**
 * Project Name:biz-monitor-client
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.client.controller
 * Date:2017年8月11日下午3:48:16
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorTreeOrderService;

/**
 * ClassName:MonitorTreeOrderController
 * Date:     2017年8月11日 下午3:48:16
 * @author   lijie
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/v1/monitor")
public class MonitorTreeOrderController {
    
    @Autowired
    private MonitorTreeOrderService service;
    
    /**
     * 
     * queryNotUsingResource: 查询节点未使用的资源信息
     * @author lijie
     * @param orderId 临时单ID
     * @param nodeId 节点ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return
     */
    @RequestMapping(value="/queryNotUsingResource", method = RequestMethod.GET)
    public Result queryNotUsingResource(@RequestParam(value="orderId",required=true) String orderId, @RequestParam(value="nodeId",required=true) String nodeId, 
                                        @RequestParam(value="currentPage", defaultValue = "1",required=false) int currentPage, @RequestParam(value="pageSize", defaultValue="20",required=false) int pageSize){
        return service.queryNotUsingResource(orderId,nodeId,currentPage,pageSize);
    }
    
    /**
     * 
     * checkNodeResource: 节点时间区间修改检查
     * @author lijie
     * @param nodeId 节点ID
     * @param startDate 生效时间
     * @param endDate 失效时间
     * @return
     */
    @RequestMapping(value="/checkNodeResource", method = RequestMethod.GET)
    public Result checkNodeResource(@RequestParam(value="nodeId",required=true) String nodeId, @RequestParam(value="startDate",required=true) String startDate,
                                    @RequestParam(value="endDate",required=true) String endDate){
        return service.checkNodeResource(nodeId, startDate, endDate);
    }
    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/addOtherNode", method = RequestMethod.GET)
    public Result addOtherNode(@RequestParam(value="orderId",required=true) String orderId){
        return service.addOtherNode(orderId);
    }
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/store", method = RequestMethod.GET)
    public Result store(@RequestParam(value="orderId",required=true) String orderId){
        return service.store(orderId);
    }
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    @RequestMapping(value="/revoked", method = RequestMethod.GET)
    public Result revoked(@RequestParam(value="orderId",required=true) String orderId){
        return service.revoked(orderId);
    }
}

