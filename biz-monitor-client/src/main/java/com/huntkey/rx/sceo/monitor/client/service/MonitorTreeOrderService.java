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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping(value="/nodes/resource")
    public Result queryNotUsingResource(@RequestParam(value="key") String key, 
                                        @RequestParam(value="lvlCode") String lvlCode, 
                                        @RequestParam(value="currentPage", defaultValue = "1",required=false) int currentPage,
                                        @RequestParam(value="pageSize", defaultValue="20",required=false) int pageSize);

    
    /**
     * 
     * checkNodeResource: 节点时间区间修改检查
     * @author lijie
     * @param key 临时单key
     * @param lvlCode 节点编号
     * @param startDate 生效时间
     * @param endDate 失效时间
     * @return
     */
    @RequestMapping(value="/nodes/checkDate")
    public Result checkNodeResource(@RequestParam(value="key") String key,
                                    @RequestParam(value="lvlCode") String lvlCode,
                                    @RequestParam(value="startDate") String startDate,
                                    @RequestParam(value="endDate") String endDate);

    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/nodes/other")
    public Result addOtherNode(@RequestParam(value="key") String key);
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param key 临时单key
     * @return
     */
    @RequestMapping(value="/nodes/{key}")
    public Result store(@PathVariable(value="key") String key);
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param key 临时单Key
     * @return
     */
    @RequestMapping(value="/nodes/revoke/{key}")
    public Result revoked(@PathVariable(value="key") String key);


    /**
     * 
     * checkAvailableResource:校验是否存在资源未分配
     * @author lijie
     * @param key 临时单Key
     * @return
     */
    @RequestMapping(value="/nodes/other/resource")
    public Result checkAvailableResource(@RequestParam(value="key") String key);
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    @RequestMapping(value="/nodes/save/{key}")
    public Result save(@PathVariable(value="key") String key);
    
}

