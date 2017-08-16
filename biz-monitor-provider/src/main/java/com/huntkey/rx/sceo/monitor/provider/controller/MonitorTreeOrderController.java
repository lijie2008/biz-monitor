/**
 * Project Name:biz-monitor-provider
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月8日下午8:10:11
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.constant.PersistanceConstant;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;
import com.huntkey.rx.sceo.monitor.commom.model.TargetNodeTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.monitor.provider.service.RedisService;

/**
 * ClassName:MonitorTreeOrderController 临时单据类
 * Date:     2017年8月8日 下午8:10:11
 * @author   lijie
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/monitor")
public class MonitorTreeOrderController {
    
    @Autowired
    private MonitorTreeOrderService service;
    
    @Autowired
    private MonitorService mService;
    
    @Autowired
    private RedisService redisService;
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorTreeOrderController.class);
    
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
    public Result queryNotUsingResource(@RequestParam(required=true) String orderId, @RequestParam(required=true) String nodeId,
                                        @RequestParam(defaultValue = "1",required=false) int currentPage, @RequestParam(defaultValue="20",required=false) int pageSize){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);

        MonitorTreeOrderTo order = service.queryOrder(orderId);
        NodeTo node = service.queryNode(nodeId);
        if(JsonUtil.isEmpity(order) || JsonUtil.isEmpity(node))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"表单、节点" + ErrorMessage._60005.getMsg());
        
        String mtor003 = order.getMtor003();
        EdmClassTo edmClass = service.getEdmClass(mtor003, PersistanceConstant.EDMPCODE);
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        String resourceEdmName = edmClass.getEdmcNameEn();
        JSONArray resources = service.getAllResource(resourceEdmName);
        if(JsonUtil.isEmpity(resources))
            return result;
        
        List<ResourceTo> usedResources = service.queryTreeNodeUsingResource(orderId, node.getMtor011(), node.getMtor012(),null);
        
        List<Object> datas = null;
        if(JsonUtil.isEmpity(usedResources))
            datas = resources;
        else{
            Set<String> usedResourceIds = usedResources.parallelStream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
            datas = resources.parallelStream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(PersistanceConstant.ID)))
                    .collect(Collectors.toList());
        }
            
        int totalSize = datas == null ? 0 : datas.size();
        JSONObject obj = new JSONObject();
        obj.put("totalSize", totalSize);
        obj.put("data", totalSize == 0 ? null : datas.subList((currentPage-1)*pageSize < 0 ? 0 : 
            (currentPage-1)*pageSize > totalSize?totalSize:(currentPage-1)*pageSize, (currentPage*pageSize)>totalSize?totalSize:currentPage*pageSize));
        result.setData(obj);
        return result;
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
    public Result checkNodeResource(@RequestParam(required=true) String nodeId, @RequestParam(required=true) String startDate, @RequestParam(required=true) String endDate){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);

        NodeTo node = service.queryNode(nodeId);
        if(JsonUtil.isEmpity(node) || JsonUtil.isEmpity(node.getPid()))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"节点" + ErrorMessage._60005.getMsg());
    
        // 查询当前节点已经拥有的资源 - 集合1
       List<ResourceTo> nodeResources = service.queryResource(nodeId);
       if(JsonUtil.isEmpity(nodeResources))
           return result;
        
        // 查询已被其它节点使用的资源信息 - 集合2
        List<ResourceTo> usedResources = service.queryTreeNodeUsingResource(node.getPid(), startDate, endDate, nodeId);
        if(JsonUtil.isEmpity(usedResources))
            return result;
       Set<String> usedResourceIds = usedResources.stream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
        if(nodeResources.parallelStream().anyMatch(re -> usedResourceIds.contains(re.getMtor020())))
            ApplicationException.throwCodeMesg(ErrorMessage._60006.getCode(),ErrorMessage._60006.getMsg());
        return result;
    }
    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/addOtherNode", method = RequestMethod.GET)
    public Result addOtherNode(@RequestParam String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        if(JsonUtil.isEmpity(orderId))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(),ErrorMessage._60004.getMsg());
        
        MonitorTreeOrderTo order = service.queryOrder(orderId);
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),ErrorMessage._60005.getMsg());
        
        String mtor003 = order.getMtor003();
        EdmClassTo edmClass = service.getEdmClass(mtor003, PersistanceConstant.EDMPCODE);
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        String resourceEdmName = edmClass.getEdmcNameEn();
        JSONArray resources = service.getAllResource(resourceEdmName);
        if(JsonUtil.isEmpity(resources))
            return result;
        
        List<ResourceTo> usedResources = service.queryTreeNodeUsingResource(orderId, null, null,null);
        if(JsonUtil.isEmpity(usedResources))
            return result;
       Set<String> usedResourceIds = usedResources.stream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
       List<Object> datas = resources.parallelStream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(PersistanceConstant.ID)))
                .collect(Collectors.toList());
        if(datas == null || datas.size() == 0)
            return result;

        NodeTo rootNode = service.queryRootNode(orderId);
        if(JsonUtil.isEmpity(rootNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"根节点"+ErrorMessage._60005.getMsg());
        NodeTo lastRootChildNode = service.queryRootChildrenNode(orderId, rootNode.getId());
        String nodeId = null;
        if(JsonUtil.isEmpity(lastRootChildNode))
            nodeId = mService.addNode(rootNode.getId(),0);
        else
            nodeId = mService.addNode(lastRootChildNode.getId(),2);
        mService.addResource(nodeId, JsonUtil.getList(datas, NodeTo.class).parallelStream().map(NodeTo::getId).toArray(String[]::new));
        return result;
    }
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping(value="/store", method = RequestMethod.GET)
    public Result store(@RequestParam String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        // 取出整个临时单信息
        logger.info("查询表单开始。。。。。。。。。。。。。。。。。。。。。。。");
        MonitorTreeOrderTo order = service.queryOrder(orderId);
        logger.info("查询表单结束。。。。。。。。。。。。。。。。。。。。。。。");
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单" + ErrorMessage._60005.getMsg());
        
        // 根据EDM 取出对应的目标监管类
        logger.info("查询监管类开始。。。。。。。。。。。。。。。。。。。。。。。");
        String edmName = service.queryEdmClassName(order.getMtor003());
        if(JsonUtil.isEmpity(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        logger.info("查询监管类结束。。。。。。。。。。。。。。。。。。。。。。。");
        
        // 取出表单下所有节点信息
        logger.info("查询树节点详情开始。。。。。。。。。。。。。。。。。。。。。。。");
        List<NodeDetailTo> treeNodes = service.getAllNodesAndResource(orderId);
        if(JsonUtil.isEmpity(treeNodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"节点" + ErrorMessage._60005.getMsg());
        logger.info("查询节点详情结束。。。。。。。。。。。。。。。。。。。。。。。");
        
        logger.info("节点详情筛选开始。。。。。。。。。。。。。。。。。。。。。。。");
        List<NodeDetailTo> nodes = treeNodes.stream().filter(s->ChangeType.valueOf(s.getMtor021()) != ChangeType.INVALID).collect(Collectors.toList());
        logger.info("节点详情筛选结束。。。。。。。。。。。。。。。。。。。。。。。");
        
        ChangeType type = ChangeType.valueOf(order.getMtor002());
        NodeDetailTo rootNode = null;
        switch(type){
            case ADD:
                break;
            case UPDATE:
                // 更新根节点信息
                rootNode = updateTargetRootNode(nodes, order, edmName);
               break;
            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(),"变更标记" + ErrorMessage._60000.getMsg());
        }
        logger.info("新增节点开始。。。。。。。。。。。。。。。。。。。。。。。");
        addTargetNode(nodes,edmName,type,rootNode,orderId);
        logger.info("新增节点结束。。。。。。。。。。。。。。。。。。。。。。。");
//        service.deleteOrder(orderId);
        return result;
    }
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param orderId 临时单ID
     * @return
     */
    @RequestMapping(value="/revoked", method = RequestMethod.GET)
    public Result revoked(@RequestParam String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        if(JsonUtil.isEmpity(orderId))
            ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(),ErrorMessage._60004.getMsg());
        if(redisService.size(orderId) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60011.getCode(), ErrorMessage._60011.getMsg());
        if(redisService.size(orderId) == 1){
            result.setData(new RevokedTo(null, OperateType.INITIALIZE));
            return result;
        }
        
        RevokedTo re = (RevokedTo)redisService.lPop(orderId);
        switch(re.getType()){
            case NODE:
                createNewTree(re.getObj(),orderId);
                break;
            case DETAIL:
                // 只是更新节点信息 - 新增资源信息等
                NodeDetailTo to = JSON.parseObject(JSON.toJSONString(re), NodeDetailTo.class);
                service.updateNodeAndResource(PersistanceConstant.MTOR_MTOR005A,to);
                re.setObj(to.getId());
                break;
             default:
                 ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(), ErrorMessage._60000.getMsg());
        }
        result.setData(re);
        return result;
    }
    
    /**
     * 
     * createNewTree:(描述这个方法的作用)
     * @author lijie
     * @param data 所有节点 和 数据源数据
     * @param orderId 临时单号
     */
    @SuppressWarnings("unchecked")
    private void createNewTree(Object data,String orderId) {
        List<NodeDetailTo> nodes = (List<NodeDetailTo>) data;
        if(JsonUtil.isEmpity(nodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
//        List<String> resourceIds = new ArrayList<String>();
//        nodes.stream().forEach(s->{
//            if(JsonUtil.isEmpity(s.getMtor019()))
//            resourceIds.addAll(s.getMtor019().stream().map(ResourceTo::getId).collect(Collectors.toList()));
//        });
//        service.batchDeleteResource(PersistanceConstant.MTOR_MTOR019B, resourceIds);
        
        List<String> nodeIds = nodes.stream().map(NodeDetailTo::getId).collect(Collectors.toList());
        service.batchDeleteResource(PersistanceConstant.MTOR_MTOR005A, nodeIds);
        addTNode(nodes,orderId);
    }
    
    /**
     * 
     * addTNode: 新增临时单监管树信息
     * @author lijie
     * @param nodes 树的数据
     * @param orderId 临时单号
     */
    private void addTNode(List<NodeDetailTo> nodes, String orderId) {
        List<NodeDetailTo> nodes_c = new ArrayList<NodeDetailTo>();
        if(JsonUtil.isEmpity(nodes))
            return;
        nodes.parallelStream().forEach(s->{
            try {
                NodeDetailTo tt = s.clone();
                tt.setMtor013(orderId);
                tt.setMtor014(orderId);
                tt.setMtor015(orderId);
                tt.setMtor016(orderId);
                tt.setId(null);
                if(!JsonUtil.isEmpity(tt.getMtor019()))
                    tt.getMtor019().stream().forEach(t->{
                        t.setId(null);
                        t.setPid(null);
                    });
                nodes_c.add(tt);
            } catch (CloneNotSupportedException e) {
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(), ErrorMessage._60000.getMsg());
            }
         });
        service.batchAdd(PersistanceConstant.MTOR_MTOR005A, JSON.parseArray(JSON.toJSONString(nodes_c)));
        
        // 根据orderId查询出目标表的所有信息
        List<NodeDetailTo> allNodes = service.queryTargetNode(PersistanceConstant.MTOR_MTOR005A, "mtor013", orderId);
        if(JsonUtil.isEmpity(allNodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单数据节点" + ErrorMessage._60005.getMsg());
        allNodes.stream().forEach(s->{
            NodeDetailTo no = nodes.parallelStream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            s.setMtor013(getId(nodes,allNodes,no.getMtor013()));
            s.setMtor014(getId(nodes,allNodes,no.getMtor014()));
            s.setMtor015(getId(nodes,allNodes,no.getMtor015()));
            s.setMtor016(getId(nodes,allNodes,no.getMtor016()));
        });
        
        // 更新目标表数据
        JSONArray ar = new JSONArray();
        ar.addAll(allNodes);
        service.batchUpdate(PersistanceConstant.MTOR_MTOR005A, ar);
    }

    /**
     * 
     * addTargetNode: 新增目标表数据
     * @author lijie
     * @param nodes 所有节点信息 包括资源
     * @param edmName 目标类
     * @param type 类型
     * @param node 根节点信息
     * @param orderId 临时单信息
     */
    private void addTargetNode(List<NodeDetailTo> nodes, String edmName, ChangeType type,
                               NodeDetailTo node,String orderId) {
        // 新增节点信息
        List<TargetNodeTo> targetNodes = JSON.parseArray(JsonUtil.getJsonArrayString(nodes), TargetNodeTo.class);
        logger.info("节点详情筛选开始2。。。。。。。。。。。。。。。。。。。。。。。");
        if(!(targetNodes == null || targetNodes.isEmpty())){
            targetNodes.parallelStream().forEach(s->{
                s.setMoni006(orderId);
                s.setMoni007(orderId);
                s.setMoni008(orderId);
                s.setMoni009(orderId);
                s.setId(null);
                if(!JsonUtil.isEmpity(s.getMoni015()))
                    s.getMoni015().stream().forEach(t->{
                        t.setId(null);
                        t.setPid(null);
                    });
            });
            logger.info("节点详情筛选结束2。。。。。。。。。。。。。。。。。。。。。。。");
            service.batchAdd(edmName, JSON.parseArray(JSON.toJSONString(targetNodes)));
        }
        logger.info("节点新增结束。。。。。。。。。。。。。。。。。。。。。。。");
        if(type == ChangeType.UPDATE)
            nodes.add(node);
        
        // 根据orderId查询出目标表的所有信息
        logger.info("查询目标树节点开始。。。。。。。。。。。。。。。。。。。。。。。");
        List<NodeDetailTo> targetAllNode = service.queryTargetNode(edmName, "moni006", orderId);
        if(JsonUtil.isEmpity(targetAllNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"目标树节点" + ErrorMessage._60005.getMsg());
        logger.info("查询目标树节点结束。。。。。。。。。。。。。。。。。。。。。。。");
        targetAllNode.stream().forEach(s->{
            NodeDetailTo no = nodes.parallelStream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            s.setMtor013(getId(nodes,targetAllNode,no.getMtor013()));
            s.setMtor014(getId(nodes,targetAllNode,no.getMtor014()));
            s.setMtor015(getId(nodes,targetAllNode,no.getMtor015()));
            s.setMtor016(getId(nodes,targetAllNode,no.getMtor016()));
        });
        logger.info("修改目标树节点结束。。。。。。。。。。。。。。。。。。。。。。。");
        
        
        // 更新目标表数据
        JSONArray ar = new JSONArray();
        ar.addAll(JSON.parseArray(JsonUtil.getJsonArrayString(targetAllNode), TargetNodeTo.class));
        service.batchUpdate(edmName, ar);
        logger.info("更新目标树节点结束。。。。。。。。。。。。。。。。。。。。。。。");
    }

    /**
     * 
     * updateTargetRootNode:更新目标类根节点
     * @author lijie
     * @param order 临时单信息
     * @param edmName 目标类名
     * @param nodes
     * @return
     */
    private NodeDetailTo updateTargetRootNode(List<NodeDetailTo> nodes, MonitorTreeOrderTo order, String edmName) {
        String orderId = order.getId();
        String targetRootNodeId = order.getMtor004();
        if(StringUtil.isNullOrEmpty(targetRootNodeId))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"目标根节点" + ErrorMessage._60005.getMsg());
//        NodeDetailTo node = nodes.stream().filter(s -> JsonUtil.isEmpity(s.getMtor013())).findFirst().get();
        // TODO null
        NodeDetailTo node = nodes.stream().filter(s -> Constant.NULL.equals(s.getMtor013())).findFirst().get();
        TargetNodeTo targetNode = JsonUtil.getObject(JsonUtil.getJsonString(node), TargetNodeTo.class);
        // 结构最后做统一修改
        targetNode.setMoni006(orderId);
        targetNode.setMoni007(orderId);
        targetNode.setMoni008(orderId);
        targetNode.setMoni009(orderId);
        targetNode.setId(targetRootNodeId);
        // 资源
        if(!( targetNode.getMoni015() == null || targetNode.getMoni015().size() == 0 )){
            targetNode.getMoni015().stream().forEach(s -> {
                s.setId(null);
                s.setPid(targetRootNodeId);
            });
        }
       service.updateTargetNode(edmName, targetNode);
       
       // 更新其他节点信息(失效日期大于当天的，全部置为当天)
       JSONArray targetChildNodes = service.getTargetAllChildNode(edmName, targetRootNodeId, new Date(System.currentTimeMillis()).toString());
       Map<String, Object> map = new HashMap<String, Object>();
       map.put("moni005", new Date(System.currentTimeMillis()).toString());
       targetChildNodes = JsonUtil.addAttr(targetChildNodes, map);
       if(!JsonUtil.isEmpity(targetChildNodes))
           service.batchUpdate(edmName, targetChildNodes);
       // 去除根节点信息
       nodes.remove(node);
        return node;
    }

    /**
     * 
     * getId: 查找id信息
     * @author lijie
     * @param nodes 原临时单信息
     * @param targetAllNode 目标表信息
     * @param str 原对象id信息
     */
    private String getId(List<NodeDetailTo> nodes, List<NodeDetailTo> targetAllNode, String str) {
        if(JsonUtil.isEmpity(str) || !nodes.parallelStream().anyMatch(h->h.getId().equals(str)))
            return Constant.NULL;
        String code = nodes.parallelStream().filter(h->h.getId().equals(str)).findFirst().get().getMtor006();
        return targetAllNode.parallelStream().filter(q->q.getMtor006().equals(code)).findFirst().get().getId();
    }

}

