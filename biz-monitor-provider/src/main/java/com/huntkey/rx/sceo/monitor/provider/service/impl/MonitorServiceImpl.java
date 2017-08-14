package com.huntkey.rx.sceo.monitor.provider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.*;
import java.util.HashMap;
import java.util.Map;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.Condition;
import com.huntkey.rx.sceo.monitor.commom.model.InputArgument;
import com.huntkey.rx.sceo.monitor.commom.model.JoinTO;
import com.huntkey.rx.sceo.monitor.commom.model.LoopTO;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.utils.DataUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.ToolUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.HbaseClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.utils.DBUtils;
@Service
public class MonitorServiceImpl implements MonitorService {
	@Autowired
	DBUtils DBUtils;
	@Autowired
	HbaseClient hbase;
	/***
	 * 查询监管树临时结构
	 * @param tempId 监管树临时单id
	 * @param hasResource 是否包含资源
	 * @param validDate 日期
	 * @return
	 */
	@Override
	public JSONArray tempTree(String tempId, String validDate) {
		// TODO Auto-generated method stub
		//初始化查询参数器
		Condition condition=new Condition();
		//组装查询条件
		condition.addCondition(PID, EQUAL, tempId, true);
		if(!StringUtil.isNullOrEmpty(validDate)){
			condition.addCondition(MTOR012, GT, ToolUtil.formatDateStr(validDate, YYYY_MM_DD), false);
		}
		//查询节点集合表
		JSONArray nodeArray=DBUtils.getArrayResult(MTOR005,null,condition);
		if(JsonUtil.isNullOrEmpty(nodeArray)){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		return nodeArray;
	}
	/**
	 * 监管树临时单预览 是否需要包含资源
	 * @param nodes
	 * @return
	 */
	@Override
	public JSONArray containResource(String[] nodes,String classId) {
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		
		//循环查询资源表
		JSONArray resourceArr=new JSONArray();
		JSONArray resourceArrNew=new JSONArray();
		for(String node:nodes){
			if(!StringUtil.isNullOrEmpty(node)){
				resourceArr=nodeResource(node, classId);
				JsonUtil.mergeJsonArray(resourceArr, resourceArrNew);
			}
		}
		
		return resourceArrNew;
	}
	/**
	 * 查询节点详情
	 * @param nodeId 节点ID
	 * @return
	 */
	@Override
	public JSONObject nodeDetail(String nodeId) {
		// TODO Auto-generated method stub
		//组装查询条件
		Condition condition=new Condition();
		condition.addCondition(ID, EQUAL, nodeId, true);
		//查询节点详情
		JSONObject nodeJson=queryNode(condition,null);
		if(nodeJson!=null && !nodeJson.isEmpty()){
			//查询员工表并且做左连
			JSONObject staffObj=null;
			condition.addCondition(ID, EQUAL, nodeJson.getString(MTOR009), true);//主管人
			staffObj=DBUtils.getObjectResult(STAFF, new String[]{STAF002}, condition);
			if(staffObj!=null){
				nodeJson.put("majorStaff", staffObj.getString(STAF002));
			}
			
			condition.addCondition(ID, EQUAL, nodeJson.getString(MTOR010), true);//协管人
			staffObj=DBUtils.getObjectResult(STAFF, new String[]{STAF002}, condition);
			if(staffObj!=null){
				nodeJson.put("assistStaff", staffObj.getString(STAF002));
			}
			
		}else{
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), 
					ErrorMessage._60003.getMsg());
		}
		return nodeJson;
	}
	/**
	 * 查询节点关联资源
	 * @param nodeId 节点ID
	 * @return
	 */
	@Override
	public JSONArray nodeResource(String nodeId,String classId) {
		// TODO Auto-generated method stub
		String resourceTab="",resourceClassId="";//资源表 资源类ID
		JSONArray resources=null;
		//根据类ID查询出资源表
		JSONObject resourceObj=DBUtils.getEdmcNameEn(classId, "moni012");
		if(resourceObj!=null && !resourceObj.isEmpty()){
			resourceTab=resourceObj.getString("edmcNameEn").toLowerCase();
			resourceClassId=resourceObj.getString(ID);
		}else{
			ApplicationException.throwCodeMesg(ErrorMessage._60012.getCode(), 
					ErrorMessage._60012.getMsg());
		}
		//根据节点ID查询出关联资源结果集
		Condition condition=new Condition();
		condition.addCondition(PID, EQUAL, nodeId, true);
		JSONArray resourceArr=DBUtils.getArrayResult(MTOR019, null, condition);
		if(!JsonUtil.isNullOrEmpty(resourceArr)){
			LoopTO loop=new LoopTO(resourceTab,ID,MTOR021,null,null);
			//循环查询资源表
			resources=DBUtils.loopQuery(loop, resourceArr);
			//数据集做交集
			JoinTO join=new JoinTO(MTOR021,ID,new String[]{"text"});
			resourceArr=DataUtil.mergeJsonArray(resourceArr, resources, join);
		}else{
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), 
					ErrorMessage._60003.getMsg());
		}
		return resourceArr;
	}
	@Override
	public String saveNodeDetail(NodeTo nodeDetail) {
		// TODO Auto-generated method stub
		String retStr="";
		InputArgument inputArgument=new InputArgument();
		inputArgument.addData(JsonUtil.getJson(nodeDetail));
		inputArgument.setEdmName(MTOR005);
		if(StringUtil.isNullOrEmpty(nodeDetail.getId())){
			retStr=DBUtils.add(MTOR005, JsonUtil.getJson(nodeDetail));
		}else{//修改
			retStr=DBUtils.update(MTOR005, JsonUtil.getJson(nodeDetail));
		}
		return retStr;
	}
	/**
	 * 删除节点资源
	 * @param nodeId 节点ID
	 * @param resourceId 临时单ID
	 * @return
	 */
	@Override
	public Result deleteNodeResource(String nodeId,String resourceId) {  
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		Condition condition=new Condition();
		condition.addCondition(PID, EQUAL, nodeId, true);
		condition.addCondition(MTOR020, EQUAL, resourceId, false);
		JSONObject retObj=DBUtils.getObjectResult(MTOR019, new String[]{"id"}, condition);
		if(retObj==null){
			return result;
		}else{
			InputArgument inputArgument=new InputArgument();
			inputArgument.addData(retObj);
			result=hbase.delete(inputArgument.toString());
		}
		return result;
	}
	@Override
	public Result changeFormula(String nodeId,String formularId) {
		// TODO Auto-generated method stub   
		
		return null;
	}
	/**
	 * 新增资源
	 * @param nodeId 节点ID
	 * @param resourceIds 资源id集合
	 * @return
	 */
	@Override
	public Result addResource(String nodeId,String[] resourceIds) {
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		InputArgument inputArgument=new InputArgument();
		JSONObject param=new JSONObject();
		for(String resourceId:resourceIds){
			if(!StringUtil.isNullOrEmpty(resourceId)){
				param.put(MTOR020, resourceId);
				inputArgument.addData(param);
			}
		}
		inputArgument.setEdmName(MTOR019);
		result=hbase.add(inputArgument.toString());
		return result;
	}
	@Override
	public Result saveTemp(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 新增节点
	 * @param nodeId 节点ID
	 * @param nodeType 创建节点的类型 0 创建子节点 1创建上节点 2创建下节点
	 * @return
	 */
	@Override
	public String addNode(String nodeId,int nodeType) {
		// TODO Auto-generated method stub
		//根据nodeId查询当前节点信息
		String newNodeId="";
		JSONObject node= nodeDetail(nodeId);
		NodeTo nodeDetail=null;
		JSONObject nodeRight=null;
		JSONObject nodeLeft=null;
		JSONObject nodeParent=null;  
		Condition condition=new Condition();
		if(node!=null){
			switch (nodeType){
				case 0://创建子节点
					condition.addCondition(MTOR013, EQUAL, node.getString(ID), true);//当前节点的子节点
					condition.addCondition(MTOR016, EQUAL, NULL, false);//最右侧节点
					nodeRight=DBUtils.getObjectResult(MTOR005,null,condition);
					nodeDetail=setNodePosition(node.getString(ID), NULL, 
							nodeRight!=null?nodeRight.getString(ID):NULL, NULL,node.getString(PID),1);
					newNodeId=DBUtils.add(MTOR005, JsonUtil.getJson(nodeDetail));
					
					if(StringUtil.isEqual(NULL, node.getString(MTOR014))) {
						//如果父节点以前没有子节点  变更父节点的子节点信息
						changeNodePosition(node.getString(ID), 2, newNodeId);
					}
				break;
				case 2://创建右节点
					condition.addCondition(ID, EQUAL, node.getString(MTOR016), true);//当前节点的右节点
					nodeRight=DBUtils.getObjectResult(MTOR005,null,condition);
					//1.创建新的右节点
					nodeDetail=setNodePosition(node.getString(MTOR013), NULL, 
							node.getString(ID), nodeRight!=null?nodeRight.getString(ID):NULL,
							node.getString(PID),1);
					newNodeId=DBUtils.add(MTOR005, JsonUtil.getJson(nodeDetail));
					//2.要变更当前节点的右节点信息
					changeNodePosition(node.getString(ID), 4, newNodeId);
					//3.变更之前右节点的左节点信息
					changeNodePosition(nodeRight.getString(ID), 3, newNodeId);
				break;
				case 1://创建左节点
					condition.addCondition(ID, EQUAL, node.getString(MTOR015), true);//当前节点的左节点
					nodeLeft=DBUtils.getObjectResult(MTOR005,null,condition);
					
					//1.创建新的左节点
					nodeDetail=setNodePosition(node.getString(MTOR013), NULL, 
							node.getString(ID), nodeLeft!=null?nodeLeft.getString(ID):NULL
							,node.getString(PID),1);
					newNodeId=DBUtils.add(MTOR005, JsonUtil.getJson(nodeDetail));
					//2.如果当前节点之前没有左节点 则变更父节点的子节点信息 
					if(nodeLeft==null){
						condition.addCondition(ID, EQUAL, node.getString(MTOR013), true);//当前节点的左节点
						nodeParent=queryNode(condition,null);
						changeNodePosition(nodeParent.getString(ID), 2, newNodeId);
					}
					//3.要变更当前节点的左节点信息
					changeNodePosition(node.getString(ID), 3, newNodeId);
					//4.变更之前左节点的右节点信息
					changeNodePosition(nodeLeft.getString(ID), 3, newNodeId);
				break;	
			}
		}
		return newNodeId;
	}
	/**
	 * 删除节点
	 * @param nodeId 节点ID
	 * @param type 0失效 1删除
	 * @return
	 */
	@Override
	public String deleteNode(String nodeId,int type) {
		// TODO Auto-generated method stub
		//查询出被删除节点信息
		Condition condition=new Condition();
		condition.addCondition(ID, EQUAL, nodeId, true);
		JSONObject delNode=queryNode(condition,null);
		JSONObject nodeParent=null;
		JSONObject nodeLeft=null;
		JSONObject nodeRight=null;
		if(delNode!=null){
			//1.递归查询删除的节点的子节点(包含子节点的子节点)
			JSONArray nodes=getChildNode(nodeId);
			JSONObject nodesClassify=classifyNodes(nodes);
			JSONArray addNodes=null;
			JSONArray updateNodes=null;
			if(nodesClassify!=null && nodesClassify.containsKey("addNodes")){//取出新增节点
				addNodes=JsonUtil.getJsonArrayByAttr(nodesClassify, "addNodes");
			}
			if(nodesClassify!=null && nodesClassify.containsKey("updateNodes")){//取出新增节点
				updateNodes=JsonUtil.getJsonArrayByAttr(nodesClassify, "updateNodes");
			}
			
			//新增节点做删除
			if(!JsonUtil.isNullOrEmpty(addNodes)){
				if(type==1){
					addNodes.add(delNode);
				}
				DBUtils.delete(MTOR005, addNodes);
			}else{//没有子节点只删除当前一个节点
				JSONObject json=new JSONObject();
				json.put(ID, delNode.getString(ID));
				DBUtils.delete(MTOR005, json);
			}
			
			//修改节点失效
			if(!JsonUtil.isNullOrEmpty(updateNodes)){
				if(type==0){
					addNodes.add(delNode);
				}
				Map<String, Object> map=new HashMap<String, Object>();
				map.put(MTOR021, ChangeType.INVALID.getValue());
				JsonUtil.addAttr(updateNodes, map);
				DBUtils.add(MTOR005, addNodes);
			}else{//没有子节点只删除当前一个节点
				JSONObject json=new JSONObject();
				json.put(ID, delNode.getString(ID));
				json.put(MTOR021, ChangeType.INVALID.getValue());
				DBUtils.delete(MTOR005, json);
			}
			
			//2.查询删除节点之前的父节点 左、右节点信息
			condition.addCondition(ID, EQUAL, delNode.getString(MTOR013),true);
			nodeParent=queryNode(condition, null);
			if(nodeParent==null){//如果父节点为空
				ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), 
						ErrorMessage._60009.getMsg());
			}
			condition.addCondition(ID, EQUAL, delNode.getString(MTOR015),true);
			nodeLeft=queryNode(condition, null);
			
			condition.addCondition(ID, EQUAL, delNode.getString(MTOR016),true);
			nodeRight=queryNode(condition, null);
			//3.变更各节点信息
			//a.如果删除的节点没有左右节点 
			if(nodeLeft==null && nodeRight==null){
				changeNodePosition(nodeParent.getString(ID), 2, "");//将父节点的子节点置空
			}
			//b.如果删除的节点没有左节点右有节点   
			else if(nodeLeft==null && nodeRight!=null){
				changeNodePosition(nodeParent.getString(ID), 2, nodeRight.getString(ID));//更改父节点的子节点为右节点
				changeNodePosition(nodeRight.getString(ID), 3, "");//右节点的左节点置空
			}
			//c.如果删除的节点有左节点没有右节点  
			else if(nodeLeft!=null && nodeRight==null){
				changeNodePosition(nodeLeft.getString(ID), 4, "");//将左节点的右节点置空
			}
			//d.如果存在左右节点
			else{
				changeNodePosition(nodeLeft.getString(ID), 4, nodeRight.getString(ID));//将左节点的右节点变更成右节点
				changeNodePosition(nodeRight.getString(ID), 3, nodeLeft.getString(ID));//将右节点的左节点变更成左节点
			}
			
		}
		return nodeId;
	}
	//将节点分类为修改的和新增的
	private JSONObject classifyNodes(JSONArray nodes) {
		// TODO Auto-generated method stub
		JSONObject allNodes=new JSONObject();
		JSONArray updateNodes=new JSONArray();
		JSONArray addNodes=new JSONArray();
		JSONObject node=new JSONObject();
		for(Object obj:nodes){
			node=JsonUtil.getJson(obj);
			if(node!=null){
				if(StringUtil.isEqual(node.getString(MTOR021), ChangeType.UPDATE.toString())){
					updateNodes.add(node);
				}else{
					addNodes.add(node);
				}
			}
		}
		if(!JsonUtil.isNullOrEmpty(updateNodes)){
			allNodes.put("updateNodes", updateNodes);
		}
		if(!JsonUtil.isNullOrEmpty(addNodes)){
			allNodes.put("addNodes", addNodes);
		}
		return allNodes;
	}
	@Override
	public String moveNode(String nodeId,String nodeParentId,String nodeLeftId,String nodeRightId) {
		// TODO Auto-generated method stub
		if(!StringUtil.isNullOrEmpty(nodeParentId)){//如果存在父节点
			changeNodePosition(nodeId, 1, nodeParentId);//变更移动节点的父节点
		}
		
		if(!StringUtil.isNullOrEmpty(nodeLeftId)){//变更左节点
			changeNodePosition(nodeId, 3, nodeLeftId);//变更移动节点的左节点
			changeNodePosition(nodeLeftId, 4, nodeId);//左节点的右节点变更
			
		}else{
			//1.如果左节点为空  则将移动节点做为父节点的子节点
			changeNodePosition(nodeParentId, 2, nodeId);
		}
		if(!StringUtil.isNullOrEmpty(nodeRightId)){//变更右节点
			changeNodePosition(nodeId, 4, nodeRightId);//变更移动节点的右节点
			changeNodePosition(nodeRightId, 3, nodeId);//变更右节点的左节点
		}
		
		return nodeId;
	}
	
	//递归查询子节点
	private JSONArray getChildNode(String nodeId){
		JSONArray allNodes=new JSONArray();
		Condition condition=new Condition();
		condition.addCondition(MTOR014, EQUAL, nodeId, true);
		condition.addCondition(MTOR021, LT, ChangeType.INVALID.toString(), false);
		JSONArray nodes=queryNodes(condition,new String[]{ID});
		while(!JsonUtil.isNullOrEmpty(nodes)){
			for(Object obj:nodes){
				JSONObject json=JsonUtil.getJson(obj);
				if(json!=null){
					allNodes.add(json);//添加本节点
					nodes=getChildNode(json.getString(ID));
					allNodes=JsonUtil.mergeJsonArray(allNodes,nodes);
				}
			}
		}
		return allNodes;
	}
	
	/***
	 * 查询节点信息
	 * @param condition 查询条件
	 * @return
	 */
	private JSONObject queryNode(Condition condition,String[] columns){
		JSONObject nodeJson=DBUtils.getObjectResult(MTOR005,columns,condition);
		if(nodeJson==null){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		return nodeJson;
	}
	/***
	 * 查询节点集信息
	 * @param condition 查询条件
	 * @return
	 */
	private JSONArray queryNodes(Condition condition,String[] columns){
		JSONArray nodes=DBUtils.getArrayResult(MTOR005,columns,condition);
		if(nodes==null){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		return nodes;
	}
	/**
	 * 新增节点设置节点的方位信息
	 * @param parentNode
	 * @param childNode
	 * @param leftNode
	 * @param rightNode
	 * @param treeId
	 * @return
	 */
	private NodeTo setNodePosition(String parentNode,String childNode,
			String leftNode,String rightNode,String treeId,int updateType){
		NodeTo node=new NodeTo();
		node.setMtor013(parentNode);
		node.setMtor014(childNode);
		node.setMtor015(leftNode);
		node.setMtor016(rightNode);
		node.setMtor021(updateType);
		node.setPid(treeId);
		return node;
	}
	
	
	/**
	 * 改变节点的位置
	 * @param nodeId 要改变节点的ID
	 * @param changeTpye 改变类型
	 * @param positionNodeId 给出位置节点ID
	 */
	private void changeNodePosition(String nodeId,int changeTpye,String positionNodeId){
		JSONObject json=new JSONObject();
		switch(changeTpye){
			case 1://改变父节点位置
				json.put(MTOR013,positionNodeId);
				break;
			case 2://改变子节点位置
				json.put(MTOR014,positionNodeId);
				break;
			case 3://改变左节点位置
				json.put(MTOR015,positionNodeId);
				break;
			case 4://改变右节点位置
				json.put(MTOR016,positionNodeId);
				break;	
		}
		json.put(ID,nodeId);
		DBUtils.update(MTOR005, json);
	}

}
