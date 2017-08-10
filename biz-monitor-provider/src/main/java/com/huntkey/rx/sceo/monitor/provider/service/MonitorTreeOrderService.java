/**
 * Project Name:service-center-provider
 * File Name:MonitorTreeOrderService.java
 * Package Name:com.huntkey.rx.sceo.serviceCenter.provider.service
 * Date:2017年8月8日上午10:22:56
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;

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
    NodeTo queryNode(String nodeId);
    
    /**
     * 
     * queryNode: 查询指定节点下的资源集信息 
     * @author lijie
     * @param nodeId 节点ID
     * @return
     */
    List<ResourceTo> queryResource(String nodeId);
    
    /**
     * 
     * queryOrder: 查询监管树临时单信息
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    MonitorTreeOrderTo queryOrder(String orderId);
    
    /**
     * 
     * queryTreeNodeUsingResource: 查询当前临时单里已使用的资源信息
     * @author lijie
     * @param orderId
     * @param startDate
     * @param endDate - 可为空 空代表最大的失效日期
     * @param excNodeId - 此id的资源不统计
     * @return
     */
    List<String> queryTreeNodeUsingResource(String orderId, String startDate, String endDate,String excNodeId);
    
    /**
     * 
     * getEdmClass: 获取Edm类信息
     * @author lijie
     * @param classId 监管树类ID
     * @param edmpCode 属性编码
     * @return
     */
    EdmClassTo getEdmClass(String classId, String edmpCode);
    
    /**
     * 
     * getAllResource: 取出资源类中所有的资源信息
     * @author lijie
     * @param edmName
     * @return
     */
    JSONArray getAllResource(String edmName);
    
    /**
     * 
     * queryRootNode:查询树根节点
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    NodeTo queryRootNode(String orderId);
    
    /**
     * 
     * queryRootChildrenNode: 查询根节点下的子节点的 最后一个子节点
     * @author lijie
     * @param orderId
     * @param rootNodeId
     * @return
     */
    NodeTo queryRootChildrenNode(String orderId,String rootNodeId);
    
    /**
     * 
     * queryTreeNodeResource: 查询当前临时单里已使用的资源信息
     * @author lijie
     * @param orderId
     * @param startDate
     * @param endDate - 可为空 空代表最大的失效日期
     * @param excNodeId - 此id的资源不统计
     * @return
     */
    List<ResourceTo> queryTreeNodeResource(String orderId, String startDate, String endDate,String excNodeId);
    
    /**
     * 
     * queryTreeNodeResource: 查询临时单下所有节点信息
     * @author lijie
     * @param orderId
     * @return
     */
    List<NodeTo> queryTreeNode(String orderId);
    
    /**
     * 
     * queryEdmClassName: 查询Edm类名称
     * @author lijie
     * @param id 类ID
     * @return
     */
    String queryEdmClassName(String id);
    
}

