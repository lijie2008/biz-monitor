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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    
       List<ResourceTo> nodeResources = service.queryResource(nodeId);
       if(JsonUtil.isEmpity(nodeResources))
           return result;
        
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
        
        MonitorTreeOrderTo order = service.queryOrder(orderId);
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单" + ErrorMessage._60005.getMsg());
        
        String edmName = service.queryEdmClassName(order.getMtor003());
        if(JsonUtil.isEmpity(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        List<NodeDetailTo> treeNodes = service.getAllNodesAndResource(orderId);
        if(JsonUtil.isEmpity(treeNodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"节点" + ErrorMessage._60005.getMsg());
        List<NodeDetailTo> nodes = treeNodes.stream().filter(s->ChangeType.valueOf(s.getMtor021()) != ChangeType.INVALID).collect(Collectors.toList());

        ChangeType type = ChangeType.valueOf(order.getMtor002());
        NodeDetailTo rootNode = null;
        
        if(type == ChangeType.UPDATE)
            rootNode = updateTargetRootNode(nodes, order, edmName);
        
        addTargetNode(nodes,edmName,type,rootNode,orderId);
        service.deleteOrder(orderId);
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
        
        if(redisService.size(orderId) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60011.getCode(), ErrorMessage._60011.getMsg());
        
        if(redisService.size(orderId) == 1){
            result.setData(new RevokedTo(null, OperateType.INITIALIZE));
            return result;
        }
        
        RevokedTo re = (RevokedTo)redisService.lPop(orderId);
        
        switch(re.getType()){
            
            case NODE:
                
                List<NodeDetailTo> allNodes = createNewTree(re.getObj(),orderId);
                updateRedis(orderId,allNodes);
                
                break;
                
            case DETAIL:
                
                NodeDetailTo to = JSON.parseObject(JSON.toJSONString(re.getObj()), NodeDetailTo.class);
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
     * updateRedis: 修改redis中节点操作类型的数据
     * @author lijie
     * @param orderId 临时单号
     * @param allNodes 所有的节点
     */
    private void updateRedis(String orderId, List<NodeDetailTo> allNodes) {
        if(JsonUtil.isEmpity(allNodes))
            return;
        Long size = redisService.size(orderId);
        for(int i = 1 ; i < size; i++){
            RevokedTo to = (RevokedTo)redisService.index(orderId, i);
            if(to.getType() != OperateType.DETAIL)
                continue;
            NodeDetailTo node = (NodeDetailTo)to.getObj();
            node.setId(allNodes.parallelStream().filter(s->s.getMtor006().equals(node.getMtor006())).findFirst().get().getId());
            to.setObj(node);
            redisService.set(orderId, i, to);
        }
    }

    /**
     * 
     * createNewTree:(描述这个方法的作用)
     * @author lijie
     * @param data 所有节点 和 数据源数据
     * @param orderId 临时单号
     */
    @SuppressWarnings("unchecked")
    private List<NodeDetailTo> createNewTree(Object data,String orderId) {
        // 清除原数据
        List<NodeTo> n_nodes = service.queryTreeNode(orderId);
        if(JsonUtil.isEmpity(n_nodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
        List<String> nodeIds = n_nodes.stream().map(NodeTo::getId).collect(Collectors.toList());
        service.batchDeleteResource(PersistanceConstant.MTOR_MTOR005A, nodeIds);
        
        // 新增redis中保存的数据
        List<NodeDetailTo> nodes = (List<NodeDetailTo>) data;
        List<NodeDetailTo> nodes_c = new ArrayList<NodeDetailTo>();
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
        
        List<NodeDetailTo> allNodes = service.queryTargetNode(PersistanceConstant.MTOR_MTOR005A, "mtor013", orderId);
        if(JsonUtil.isEmpity(allNodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单数据节点" + ErrorMessage._60005.getMsg());
        
        JSONArray ar = new JSONArray();
        allNodes.stream().forEach(s->{
            NodeDetailTo no = nodes.parallelStream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            JSONObject obj = new JSONObject();
            obj.put(PersistanceConstant.ID, s.getId());
            obj.put("mtor013", getId(nodes,allNodes,no.getMtor013()));
            obj.put("mtor014", getId(nodes,allNodes,no.getMtor014()));
            obj.put("mtor015", getId(nodes,allNodes,no.getMtor015()));
            obj.put("mtor016", getId(nodes,allNodes,no.getMtor016()));
            ar.add(obj);
        });
        service.batchUpdate(PersistanceConstant.MTOR_MTOR005A, ar);
        return allNodes;
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
        // 新增子节点信息
        List<TargetNodeTo> targetNodes = JSON.parseArray(JsonUtil.getJsonArrayString(nodes), TargetNodeTo.class);
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
            service.batchAdd(edmName, JSON.parseArray(JSON.toJSONString(targetNodes)));
        }
        
        if(type == ChangeType.UPDATE && !JsonUtil.isEmpity(node))
            nodes.add(node);
        
        List<NodeDetailTo> targetAllNode = service.queryTargetNode(edmName, "moni006", orderId);
        if(JsonUtil.isEmpity(targetAllNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"目标树节点" + ErrorMessage._60005.getMsg());
        
        JSONArray ar = new JSONArray();
        targetAllNode.stream().forEach(s->{
            NodeDetailTo no = nodes.parallelStream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            JSONObject obj = new JSONObject();
            obj.put(PersistanceConstant.ID, s.getId());
            obj.put("moni006", getId(nodes,targetAllNode,no.getMtor013()));
            obj.put("moni007", getId(nodes,targetAllNode,no.getMtor014()));
            obj.put("moni008", getId(nodes,targetAllNode,no.getMtor015()));
            obj.put("moni009", getId(nodes,targetAllNode,no.getMtor016()));
            ar.add(obj);
        });
        service.batchUpdate(edmName, ar);
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
        // TODO null
        NodeDetailTo node = nodes.stream().filter(s -> Constant.NULL.equals(s.getMtor013())).findFirst().get();
        TargetNodeTo targetNode = JsonUtil.getObject(JsonUtil.getJsonString(node), TargetNodeTo.class);
        targetNode.setMoni006(orderId);
        targetNode.setMoni007(orderId);
        targetNode.setMoni008(orderId);
        targetNode.setMoni009(orderId);
        targetNode.setId(targetRootNodeId);
        if(!( targetNode.getMoni015() == null || targetNode.getMoni015().size() == 0 )){
            targetNode.getMoni015().stream().forEach(s -> {
                s.setId(null);
                s.setPid(targetRootNodeId);
            });
        }
       service.updateTargetNode(edmName, targetNode);
       
       nodes.remove(node);
       
       // 更新其他子节点信息(失效日期大于当天的，全部置为当天)
       JSONArray targetChildNodes = service.getTargetAllChildNode(edmName, targetRootNodeId, new Date(System.currentTimeMillis()).toString());
       if(JsonUtil.isEmpity(targetChildNodes))
           return node;
       JSONArray ar = new JSONArray();
       targetChildNodes.parallelStream().forEach(s->{
           JSONObject obj = new JSONObject();
           obj.put(PersistanceConstant.ID, ((JSONObject)s).get(PersistanceConstant.ID));
           obj.put("moni005", new Date(System.currentTimeMillis()).toString());
           ar.add(obj);
       });
       service.batchUpdate(edmName, ar);
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

