/**
 * Project Name:biz-monitor-provider
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月8日下午8:10:11
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.constant.ValidBean;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.CharacterAndFormatTo;
import com.huntkey.rx.sceo.monitor.commom.model.EdmClassTo;
import com.huntkey.rx.sceo.monitor.commom.model.MonitorTreeOrderTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.commom.model.RevokedTo;
import com.huntkey.rx.sceo.monitor.commom.model.TargetNodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.TargetResourceTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.ToolUtil;
import com.huntkey.rx.sceo.monitor.provider.config.Revoked;
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
@RequestMapping("/nodes")
@Validated
public class MonitorTreeOrderController {
    
    private static final String MODUSER = "admin";
    
    private static final String ADDUSER = "admin";
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorTreeOrderController.class);
    
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
    @GetMapping("/resource")
    public Result queryNotUsingResource(@RequestParam @NotBlank(message = "临时单ID不能为空") String orderId,
                                        @RequestParam @NotBlank(message = "节点ID不能为空") String nodeId,
                                        @RequestParam(defaultValue = "1",required=false) int currentPage, 
                                        @RequestParam(defaultValue="20",required=false) int pageSize){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);

        MonitorTreeOrderTo order = service.queryOrder(orderId);
        
        NodeTo node = service.queryNode(nodeId);
        
        if(JsonUtil.isEmpity(order) || JsonUtil.isEmpity(node))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"表单、节点" + ErrorMessage._60005.getMsg());
        
        String mtor003 = order.getMtor003();
        EdmClassTo edmClass = service.getEdmClass(mtor003, Constant.EDMPCODE);
        
        if(JsonUtil.isEmpity(edmClass) || JsonUtil.isEmpity(edmClass.getEdmcNameEn()))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        String resourceEdmName = edmClass.getEdmcNameEn();
        JSONArray resources = service.getAllResource(resourceEdmName);
        
        if(JsonUtil.isEmpity(resources))
            return result;
        
        List<ResourceTo> usedResources = service.queryTreeNodeUsingResource(orderId, node.getMtor011(), node.getMtor012(),null,true);
        
        List<Object> datas = null;
        if(JsonUtil.isEmpity(usedResources))
            datas = resources;
        else{
            Set<String> usedResourceIds = usedResources.stream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
            datas = resources.stream().filter(re -> !usedResourceIds.contains(((JSONObject)re).getString(Constant.ID)))
                    .collect(Collectors.toList());
        }
        
        int totalSize = datas == null ? 0 : datas.size();
        
        List<Object> data = totalSize == 0 ? null : datas.subList((currentPage-1)*pageSize < 0 ? 0 : 
                    (currentPage-1)*pageSize > totalSize?totalSize:(currentPage-1)*pageSize, 
                    (currentPage*pageSize)>totalSize?totalSize:currentPage*pageSize);
        
        JSONArray re = new JSONArray();
        
        if(!JsonUtil.isEmpity(data)){
            
            CharacterAndFormatTo format = service.getCharacterAndFormat(edmClass.getId());
            
            if(format == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"特征值" + ErrorMessage._60005.getMsg());
            
            data.stream().forEach(s->{
                JSONObject obj = new JSONObject();
                obj.put(Constant.ID, ((JSONObject)s).get(Constant.ID));
                obj.put("text", format.format((JSONObject)s));
                re.add(obj);
            });   
            
        }
        
        // 返回的data
        JSONObject obj = new JSONObject();
        obj.put("totalSize", totalSize);
        obj.put("currentPage", currentPage);
        obj.put("data", re);
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
    @GetMapping("/checkDate")
    public Result checkNodeResource(@RequestParam @NotBlank(message = "节点ID不能为空") String nodeId,
                                    @RequestParam @NotBlank(message = "生效日期不能为空") @Pattern(regexp=ValidBean.DATE_REGX,message="生效日期格式不正确") String startDate, 
                                    @RequestParam @NotBlank(message = "失效日期不能为空") @Pattern(regexp=ValidBean.DATE_REGX,message="失效日期格式不正确") String endDate){
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        startDate = startDate + " 00:00:00";
        endDate = endDate + " 23:59:59";
        Date nowStartDate = ToolUtil.getDate(startDate);
        Date nowEndDate = ToolUtil.getDate(endDate);
        
        if(nowStartDate.after(nowEndDate))
            ApplicationException.throwCodeMesg(ErrorMessage._60015.getCode(),ErrorMessage._60015.getMsg());
        
        NodeTo node = service.queryNode(nodeId);
        
        if(JsonUtil.isEmpity(node) || JsonUtil.isEmpity(node.getPid()))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"节点" + ErrorMessage._60005.getMsg());
       
       // 校验当前节点时间区间不能超过上级节点时间区间
       if(!JsonUtil.isEmpity(node.getMtor013())){
           
           NodeTo upNode = service.queryNode(node.getMtor013());
           
           if(JsonUtil.isEmpity(upNode))
               ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"上级节点" + ErrorMessage._60005.getMsg());
           
           Date upStartDate = ToolUtil.getDate(upNode.getMtor011());
           Date upEndDate = ToolUtil.getDate(upNode.getMtor012());
           
           
           if(upStartDate.after(nowStartDate) ||  nowEndDate.after(upEndDate))
               ApplicationException.throwCodeMesg(ErrorMessage._60016.getCode(),ErrorMessage._60016.getMsg());
           
       }
           
       List<ResourceTo> nodeResources = service.queryResource(nodeId);
       
       if(!JsonUtil.isEmpity(nodeResources)){
           
           List<ResourceTo> usedResources = service.queryTreeNodeUsingResource(node.getPid(), startDate, endDate, nodeId,true);
           
           if(!JsonUtil.isEmpity(usedResources)){
               Set<String> usedResourceIds = usedResources.stream().map(ResourceTo::getMtor020).collect(Collectors.toSet());
               if(nodeResources.stream().anyMatch(re -> usedResourceIds.contains(re.getMtor020())))
                   ApplicationException.throwCodeMesg(ErrorMessage._60017.getCode(),ErrorMessage._60017.getMsg());
           }
       }
       
       // 修改的父节点是否会导致 子节点失效
       List<NodeTo> child_nodes = JsonUtil.getList(mService.getChildNode(nodeId), NodeTo.class);
       
       result.setData(true);
       
       if(!JsonUtil.isEmpity(child_nodes)){
           
           child_nodes.stream().anyMatch(s->{
               
               Date cStartDate = ToolUtil.getDate(s.getMtor011());
               
               Date cEndDate = ToolUtil.getDate(s.getMtor012());
               
               if(!nowStartDate.before(cEndDate) || !nowEndDate.after(cStartDate)){
                   result.setData(false);
                   return true;
               }
               return false;
           });
       }
        return result;
    }
    
    /**
     * 
     * checkAvailableResource:校验是否存在资源未分配
     * @author lijie
     * @param orderId 临时单id
     * @return
     */
    @GetMapping("/other/resource")
    public Result checkAvailableResource(@RequestParam @NotBlank(message="临时单ID不能为空") String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        List<Object> datas = service.queryAvailableResource(orderId);
       
       if(JsonUtil.isEmpity(datas))
            result.setData(false);
        else
            result.setData(true);
       return result;
    }
    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @Revoked(type=OperateType.NODE,character="orderId")
    @GetMapping("/other")
    public Result addOtherNode(@RequestParam @NotBlank(message="临时单ID不能为空") @Revoked String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
       List<Object> datas = service.queryAvailableResource(orderId);
       
        if(JsonUtil.isEmpity(datas))
            return result;

        NodeTo rootNode = service.queryRootNode(orderId);
        
        if(JsonUtil.isEmpity(rootNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"根节点"+ErrorMessage._60005.getMsg());
        
        NodeTo lastRootChildNode = service.queryRootChildrenNode(orderId, rootNode.getId());
        
        String nodeId = null;
        
        if(JsonUtil.isEmpity(lastRootChildNode))
            nodeId = mService.addNode(rootNode.getId(),0,"其他节点");
        else
            nodeId = mService.addNode(lastRootChildNode.getId(),2,"其他节点");
        
        mService.addResource(nodeId, JsonUtil.getList(datas, NodeTo.class).stream().map(NodeTo::getId).toArray(String[]::new));
        
        return result;
    }
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单Id
     * @return
     */
    @RequestMapping("/{orderId}")
    public Result store(@PathVariable(value="orderId") @NotBlank(message="临时单ID不能为空") String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        MonitorTreeOrderTo order = service.queryOrder(orderId);
        
        if(JsonUtil.isEmpity(order))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单" + ErrorMessage._60005.getMsg());
        
        String edmName = service.queryEdmClassName(order.getMtor003());
        
        if(JsonUtil.isEmpity(edmName))
            ApplicationException.throwCodeMesg(ErrorMessage._60008.getCode(),ErrorMessage._60008.getMsg());
        
        logger.debug("查询临时单所有节点 和 对应资源信息 开始" + new Timestamp(System.currentTimeMillis()));
        
        List<NodeDetailTo> nodes = service.getAllNodesAndResource(orderId);
        
        logger.debug("查询临时单所有节点 和 对应资源信息 结束" + new Timestamp(System.currentTimeMillis()));
        
        if(JsonUtil.isEmpity(nodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"节点" + ErrorMessage._60005.getMsg());
        
        ChangeType type = ChangeType.valueOf(order.getMtor002());
        
        switch(type){
            
            case ADD:
                
                addTargetNode(nodes,edmName,order);
                break;
                
            case UPDATE:
                
                updateTargetNode(nodes,edmName,order);
                break;
                
            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(),"临时单变更类型" + ErrorMessage._60000.getMsg());
        }
        
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
    @GetMapping("/revoke/{orderId}")
    public Result revoked(@PathVariable(value="orderId") @NotBlank(message="临时单ID不能为空") String orderId){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        if(redisService.size(orderId) == 0)
            ApplicationException.throwCodeMesg(ErrorMessage._60011.getCode(), ErrorMessage._60011.getMsg());
        
        if(redisService.size(orderId) == 1){
            result.setData(new RevokedTo(orderId, OperateType.INITIALIZE));
            return result;
        }
        
        RevokedTo re = (RevokedTo)redisService.lPop(orderId);
        
        switch(re.getType()){
            
            case NODE:
                
                List<NodeDetailTo> allNodes = createNewTree(re.getObj(),orderId);
                
                updateRedis(orderId,allNodes);
                
                re.setObj(orderId);
                break;
                
            case DETAIL:
                
                NodeDetailTo to = JSON.parseObject(JSON.toJSONString(re.getObj()), NodeDetailTo.class);
                to.setModuser(MODUSER);
                
                service.updateNodeAndResource(Constant.MTOR005,to);
                
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
        
        for(int i = 0 ; i < size; i++){
            
            RevokedTo to = (RevokedTo)redisService.index(orderId, i);
            
            if(to.getType() != OperateType.DETAIL)
                break;
            
            NodeDetailTo node = (NodeDetailTo)to.getObj();
            
            NodeDetailTo target = allNodes.stream().filter(s->s.getMtor006().equals(node.getMtor006())).findFirst().get();
            
            node.setId(target.getId());
            node.setMtor013(target.getMtor013());
            node.setMtor014(target.getMtor014());
            node.setMtor015(target.getMtor015());
            node.setMtor016(target.getMtor016());
            
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
        
        final AtomicBoolean flag = new AtomicBoolean(true);
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                // 清除原数据
                List<NodeTo> n_nodes = service.queryTreeNode(orderId);
                if(!JsonUtil.isEmpity(n_nodes)){
                    List<String> nodeIds = n_nodes.stream().map(NodeTo::getId).collect(Collectors.toList());
                    service.batchDeleteResource(Constant.MTOR005, nodeIds);
                }
                synchronized(flag){
                    flag.set(false);
                    flag.notifyAll();
                }
            }
        }).start();
        
        // 新增redis中保存的数据
        List<NodeDetailTo> nodes = (List<NodeDetailTo>) data;
        
        List<NodeDetailTo> nodes_c = new ArrayList<NodeDetailTo>();
        
        nodes.stream().forEach(s->{
            try {
                NodeDetailTo tt = s.clone();
                tt.setMtor013(orderId);
                tt.setMtor014(orderId);
                tt.setMtor015(orderId);
                tt.setMtor016(orderId);
                tt.setId(null);
                tt.setAdduser(ADDUSER);
                if(!JsonUtil.isEmpity(tt.getMtor019()))
                    tt.getMtor019().stream().forEach(t->{
                        t.setId(null);
                        t.setPid(null);
                        t.setAdduser(ADDUSER);
                    });
                nodes_c.add(tt);
            } catch (CloneNotSupportedException e) {
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(), ErrorMessage._60000.getMsg());
            }
         });
        
        List<String> ids = service.batchAdd(Constant.MTOR005, JSON.parseArray(JSON.toJSONString(nodes_c)));
        
        if(ids.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),"新增节点信息失败" + ErrorMessage._60003.getMsg());
        
        List<NodeDetailTo> allNodes = service.load(Constant.MTOR005, ids);
        
        if(JsonUtil.isEmpity(allNodes))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"临时单数据节点" + ErrorMessage._60005.getMsg());
        
        JSONArray ar = new JSONArray();
        allNodes.stream().forEach(s->{
            NodeDetailTo no = nodes.stream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            JSONObject obj = new JSONObject();
            obj.put(Constant.ID, s.getId());
            obj.put("mtor013", getId(nodes,allNodes,no.getMtor013()));
            obj.put("mtor014", getId(nodes,allNodes,no.getMtor014()));
            obj.put("mtor015", getId(nodes,allNodes,no.getMtor015()));
            obj.put("mtor016", getId(nodes,allNodes,no.getMtor016()));
            obj.put("moduser", MODUSER);
            ar.add(obj);
            s.setMtor013(obj.getString("mtor013"));
            s.setMtor014(obj.getString("mtor014"));
            s.setMtor015(obj.getString("mtor015"));
            s.setMtor016(obj.getString("mtor016"));
            s.setModuser(MODUSER);
        });
        
        service.batchUpdate(Constant.MTOR005, ar);
        
        synchronized(flag){
            if(flag.get())
                try {
                    flag.wait(10000);
                } catch (InterruptedException e) {
                    ApplicationException.throwCause(e);
                }
        }
        return allNodes;
    }
    
    /**
     * 
     * addTargetNode: 新增目标表数据
     * @author lijie
     * @param nodes 所有节点信息 包括资源
     * @param edmName 目标类
     * @param order 临时单信息
     */
    private void addTargetNode(List<NodeDetailTo> nodes, String edmName, MonitorTreeOrderTo order) {
        String orderId = order.getId();
        List<TargetNodeTo> targetNodes = JSON.parseArray(JsonUtil.getJsonArrayString(nodes), TargetNodeTo.class);
        if(targetNodes == null || targetNodes.isEmpty())
            return;
        targetNodes.stream().forEach(s->{
            s.setMoni006(orderId);
            s.setMoni007(orderId);
            s.setMoni008(orderId);
            s.setMoni009(orderId);
            s.setId(null);
            s.setAdduser(ADDUSER);
            if(!JsonUtil.isEmpity(s.getMoni015()))
                s.getMoni015().stream().forEach(t->{
                    t.setId(null);
                    t.setPid(null);
                    t.setAdduser(ADDUSER);
                });
        });
        List<String> ids = service.batchAdd(edmName, JSON.parseArray(JSONArray.toJSONString(targetNodes)));
        logger.debug("更新 所有目标 节点结束" + new Timestamp(System.currentTimeMillis()) + "数据：" + JsonUtil.listToJsonArray(ids));
        if(JsonUtil.isEmpity(ids))
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),"新增节点信息失败" + ErrorMessage._60003.getMsg());
        List<NodeDetailTo> targetAllNode = service.load(edmName, ids);
        if(JsonUtil.isEmpity(targetAllNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"目标树节点" + ErrorMessage._60005.getMsg());
        logger.debug("查询目标节点数据 " + new Timestamp(System.currentTimeMillis()) +"大小： " + targetAllNode.size() +", 数据： "+JsonUtil.listToJsonArray(targetAllNode).toJSONString());
        JSONArray ar = new JSONArray();
        targetAllNode.stream().forEach(s->{
            NodeDetailTo no = nodes.stream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            JSONObject obj = new JSONObject();
            obj.put(Constant.ID, s.getId());
            obj.put("moni006", getId(nodes,targetAllNode,no.getMtor013()));
            obj.put("moni007", getId(nodes,targetAllNode,no.getMtor014()));
            obj.put("moni008", getId(nodes,targetAllNode,no.getMtor015()));
            obj.put("moni009", getId(nodes,targetAllNode,no.getMtor016()));
            obj.put("moduser", MODUSER);
            ar.add(obj);
        });
        service.batchUpdate(edmName, ar);
    }
    
    /**
     * 
     * updateTargetNode:入库 - 更新临时单节点信息
     * @author lijie
     * @param nodes 所有节点信息
     * @param edmName 目标类
     * @param order 临时单ID 
     */
    private void updateTargetNode(List<NodeDetailTo> nodes, String edmName,
                                  MonitorTreeOrderTo order) {
        
        List<String> ids = new ArrayList<String>();
        List<TargetNodeTo> addNodes = new ArrayList<TargetNodeTo>();
        List<TargetNodeTo> updateNodes = new ArrayList<TargetNodeTo>();
        List<TargetNodeTo> invalidNodes = new ArrayList<TargetNodeTo>();
            
        nodes.stream().forEach(s->{
            ChangeType type = ChangeType.valueOf(s.getMtor021());
            TargetNodeTo node = JsonUtil.getObject(JsonUtil.getJsonString(s), TargetNodeTo.class);
            node.setMoni006(order.getId());
            node.setMoni007(order.getId());
            node.setMoni008(order.getId());
            node.setMoni009(order.getId());
            if(!JsonUtil.isEmpity(node.getMoni015()))
                node.getMoni015().stream().forEach(t->{
                t.setId(null);
                if(JsonUtil.isEmpity(node.getId()))
                    t.setPid(null);
                else
                    t.setPid(node.getId());
                t.setAdduser(ADDUSER);
            });
            switch(type){
                case ADD:
                    node.setAdduser(ADDUSER);
                    addNodes.add(node);
                    break;
                case UPDATE:
                    node.setModuser(MODUSER);
                    updateNodes.add(node);
                    break;
                case INVALID:
                    node.setModuser(MODUSER);
                    invalidNodes.add(node);
                    break;
                default:
                    ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(),"临时单中节点属性集变更类型" + ErrorMessage._60000.getMsg());
            }
        });
        
        // 将三种情况的节点集合入库
        if(!JsonUtil.isEmpity(addNodes))
            ids.addAll(service.batchAdd(edmName, JSON.parseArray(JSONArray.toJSONString(addNodes))));
        
        if(!JsonUtil.isEmpity(updateNodes)){
            List<TargetResourceTo> resources = new ArrayList<TargetResourceTo>();
            List<String> uIds = new ArrayList<String>();
            updateNodes.stream().forEach(s->{
                if(!JsonUtil.isEmpity(s.getMoni015()))
                    resources.addAll(s.getMoni015());
                uIds.add(s.getId());
            });
            JSONArray uNodes = JsonUtil.removeAttr(JSON.parseArray(JSONArray.toJSONString(updateNodes)), "moni015");
            service.batchUpdate(edmName, uNodes);
            List<String> targetResourceIds = service.queryTargetResource(edmName+".moni015",uIds);
            if(!JsonUtil.isEmpity(targetResourceIds))
                service.batchDeleteResource(edmName+".moni015", targetResourceIds);
            if(!JsonUtil.isEmpity(resources))
                service.batchAdd(edmName+".moni015", JSON.parseArray(JSONArray.toJSONString(resources)));
            ids.addAll(uIds);
        }
        
        if(!JsonUtil.isEmpity(invalidNodes)){
            List<NodeDetailTo> inNodes = service.load(edmName, invalidNodes.stream().map(TargetNodeTo::getId).collect(Collectors.toList()));
            if(!JsonUtil.isEmpity(inNodes)){
                JSONArray arry = new JSONArray();
                List<String> arry2 = new ArrayList<String>();
                Date currentDate = ToolUtil.getDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date())+ " 23:59:59");
                inNodes.stream().forEach(s->{
                    Date sStartDate = ToolUtil.getDate(s.getMtor011());
                    Date sEndDate = ToolUtil.getDate(s.getMtor012());
                    if(sStartDate.after(currentDate)){
                        arry2.add(s.getId());
                    }else if(!(sStartDate.after(currentDate) || sEndDate.before(currentDate))){
                        JSONObject obj = new JSONObject();
                        obj.put(Constant.ID,s.getId());
                        obj.put("moni005",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(currentDate));
                        obj.put("moduser", MODUSER);
                        arry.add(obj);
                    }
                    
                });
                if(!JsonUtil.isEmpity(arry2))
                    service.batchDeleteResource(edmName, arry2);
                if(!JsonUtil.isEmpity(arry))
                    service.batchUpdate(edmName, arry);
            }
        }
        
        // 更新节点的上下左右关系
        List<NodeDetailTo> targetAllNode = service.load(edmName, ids);
        if(JsonUtil.isEmpity(targetAllNode))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"目标树节点" + ErrorMessage._60005.getMsg());
        JSONArray ar = new JSONArray();
        targetAllNode.stream().forEach(s->{
            NodeDetailTo no = nodes.stream().filter(n->s.getMtor006().equals(n.getMtor006())).findFirst().get();
            JSONObject obj = new JSONObject();
            obj.put(Constant.ID, s.getId());
            obj.put("moni006", getId(nodes,targetAllNode,no.getMtor013()));
            obj.put("moni007", getId(nodes,targetAllNode,no.getMtor014()));
            obj.put("moni008", getId(nodes,targetAllNode,no.getMtor015()));
            obj.put("moni009", getId(nodes,targetAllNode,no.getMtor016()));
            obj.put("moduser", MODUSER);
            ar.add(obj);
        });
        service.batchUpdate(edmName, ar);
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
        
        if(JsonUtil.isEmpity(str) || !nodes.stream().anyMatch(h->h.getId().equals(str)))
            return Constant.NULL;
        String code = nodes.stream().filter(h->h.getId().equals(str)).findFirst().get().getMtor006();
        if(JsonUtil.isEmpity(code))
            return Constant.NULL;
        if(targetAllNode.stream().anyMatch(q->q.getMtor006().equals(code)))
            return targetAllNode.stream().filter(q->q.getMtor006().equals(code)).findFirst().get().getId();
        return Constant.NULL;
    }
    
}

