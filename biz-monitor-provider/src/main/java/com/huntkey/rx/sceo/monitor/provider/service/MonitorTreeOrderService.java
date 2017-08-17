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

import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.huntkey.rx.sceo.monitor.commom.model.CharacterAndFormatTo;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.TargetNodeTo;

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
    List<ResourceTo> queryTreeNodeUsingResource(String orderId, String startDate, String endDate,String excNodeId);
    
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
    
    /**
     * 
     * updateTargetNode:(描述这个方法的作用)
     * @author lijie
     * @param edmName 目标表的edmName
     * @param node 当前节点信息
     */
    void updateTargetNode(String edmName , TargetNodeTo node);
    
    /**
     * 
     * getTargetAllChildNode: 获取目标表当前节点下的所有子节点信息
     * @author lijie
     * @param edmName 目标类
     * @param nodeId 上级节点id
     * @param endDate 失效时间
     * @return
     */
    JSONArray getTargetAllChildNode(String edmName, String nodeId,String endDate);
    
    /**
     * 
     * batchUpdate: 批量更新目标表数据
     * @author lijie
     * @param edmName 目标类
     * @param nodes 目标表节点集合
     * @return
     */
    void batchUpdate(String edmName, JSONArray nodes);
    
    /**
     * 
     * batchAdd: 批量新增数据
     * @author lijie
     * @param edmName 目标类
     * @param nodes 目标表节点集合
     * @return
     */
    void batchAdd(String edmName, JSONArray nodes);

    /**
     * 
     * updateNodeAndResource: 更新临时单节点和资源信息
     * @author lijie
     * @param edmName edm类名
     * @param to
     */
    void updateNodeAndResource(String edmName, NodeDetailTo to);
    
    /**
     * 
     * updateNode: 更新临时单节点信息
     * @author lijie
     * @param edmName edm类名
     * @param to
     */
    void updateNode(String edmName, NodeDetailTo to);
    
    /**
     * 
     * batchDeleteResource: 批量删除临时单资源信息
     * @author lijie
     * @param edmName edm
     * @param ids 资源id信息
     */
    void batchDeleteResource(String edmName, List<String> ids);
    
    /**
     * 
     * queryTargetNode: 根据上级节点查询所有的节点信息
     * @author lijie
     * @param edmName edm
     * @param fieldName 字段名称
     * @param orderId 临时单id
     */
    List<NodeDetailTo> queryTargetNode(String edmName, String fieldName, String orderId);
    
    /**
     * 
     * getAllNodesAndResource: 查询临时单下所有的节点和资源信息
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    List<NodeDetailTo> getAllNodesAndResource(String orderId);
    
    /**
     * 
     * deleteOrder: 删除临时单信息
     * @author lijie
     * @param orderId 临时单
     */
    void deleteOrder(String orderId);
    
    /**
     * 根据类id 查询特征值字段集合和格式化样式
     * @param classId
     * @return
     */
    CharacterAndFormatTo getCharacterAndFormat(@RequestParam(value = "classId") String classId);
}

