/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderService.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.service
 * Date:2017年8月8日上午10:22:56
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service;

import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:MonitorTreeOrderService 临时单服务
 * Date:     2017年8月8日 上午10:22:56
 * @author   lijie
 * @version  
 * @see 	 
 */
public interface MonitorTreeOrderService {
    
    /**
     * 
     * queryNode: 查询节点详细信息 
     * @author lijie
     * @param nodeId 节点ID
     * @return
     */
    JSONObject queryNode(String nodeId);
    
    /**
     * 
     * queryNode: 查询指定节点下的资源集信息 
     * @author lijie
     * @param nodeId 节点ID
     * @return
     */
    JSONObject queryResource(String nodeId);
    
    /**
     * 
     * queryOrder: 查询监管树临时单信息
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    JSONObject queryOrder(String orderId);
    
    /**
     * 
     * queryTreeNode: 查询监管树下所有节点信息
     * @author lijie
     * @param orderId
     * @param startDate
     * @param endDate - 可为空 空代表最大的失效日期
     * @return
     */
    JSONObject queryTreeNode(String orderId, String startDate, String endDate);
    
}

