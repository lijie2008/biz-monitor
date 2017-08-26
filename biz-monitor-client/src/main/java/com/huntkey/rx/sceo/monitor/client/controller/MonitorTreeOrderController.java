/**
 * Project Name:biz-monitor-client
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.client.controller
 * Date:2017年8月11日下午3:48:16
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.client.controller;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Validated
@RequestMapping("/v1/nodes")
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
    @GetMapping("/resource")
    public Result queryNotUsingResource(@RequestParam @NotBlank(message = "临时单ID不能为空") String orderId,
                                        @RequestParam @NotBlank(message = "节点ID不能为空") String nodeId, 
                                        @RequestParam(defaultValue = "1",required=false) int currentPage, 
                                        @RequestParam(defaultValue="20",required=false) int pageSize){
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
    @GetMapping(value="/checkDate")
    public Result checkNodeResource(@RequestParam @NotBlank(message="临时单ID不能为空") String nodeId,
                                    @RequestParam @NotBlank(message="生效日期不能为空") String startDate,
                                    @RequestParam @NotBlank(message="失效日期不能为空") String endDate){
        return service.checkNodeResource(nodeId, startDate, endDate);
    }
    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @GetMapping("/other")
    public Result addOtherNode(@RequestParam @NotBlank(message="临时单ID不能为空") String orderId){
        return service.addOtherNode(orderId);
    }
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @GetMapping(value="/{orderId}")
    public Result store(@PathVariable(value="orderId") @NotBlank(message="临时单ID不能为空") String orderId){
        return service.store(orderId);
    }
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    @GetMapping("/revoke/{orderId}")
    public Result revoked(@PathVariable(value="orderId") @NotBlank(message="临时单ID不能为空") String orderId){
        return service.revoked(orderId);
    }
    
    /**
     * 
     * checkAvailableResource:校验是否存在资源未分配
     * @author lijie
     * @param orderId 临时单id
     * @return
     */
    @GetMapping("/other/resource")
    public Result checkAvailableResource(@RequestParam @NotBlank(message = "临时单ID不能为空") String orderId){
        return service.checkAvailableResource(orderId);
    }
}

