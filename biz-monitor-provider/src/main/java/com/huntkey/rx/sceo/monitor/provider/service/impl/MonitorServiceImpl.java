package com.huntkey.rx.sceo.monitor.provider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;

import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.*;

import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.Condition;
import com.huntkey.rx.sceo.monitor.commom.model.InputArgument;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
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
	public JSONArray tempTree(String tempId, int hasResource, String validDate) {
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		//初始化查询参数器
		Condition condition=new Condition();
		//组装查询条件
		condition.addCondition(PID, EQUAL, tempId, true);
		condition.addCondition(MTOR012, GT, validDate, false);
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
	public Result containResource(String[] nodes) {
		// TODO Auto-generated method stub
		Condition condition=new Condition();
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		JSONArray resourceArray=new JSONArray();
		JSONObject resourceObj=new JSONObject();
		//mode018表和资源表关联查询
		for(String node:nodes){
			condition.addCondition(PID, EQUAL, node, true);
			resourceObj=DBUtils.getObjectResult(MTOR019,null,condition);
			if(resourceObj!=null){
				resourceArray.add(resourceObj);
			}
		}
		
		//循环查询资源表
		
		result.setData(resourceArray);
		return null;
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
		JSONObject nodeJson=queryNode(condition);
		return nodeJson;
	}
	/**
	 * 查询节点关联资源
	 * @param nodeId 节点ID
	 * @return
	 */
	@Override
	public Result nodeResource(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result saveNodeDetail(NodeTo nodeDetail) {
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		InputArgument inputArgument=new InputArgument();
		inputArgument.addData(JsonUtil.getJson(nodeDetail));
		inputArgument.setEdmName(MTOR005);
		if(StringUtil.isNullOrEmpty(nodeDetail.getId())){
			result=hbase.add(inputArgument.toString());
		}else{//修改
			result=hbase.update(inputArgument.toString());
		}
		return null;
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
	public String addNode(String nodeId,String nodeType) {
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
			if(StringUtil.isEqual("0", nodeType)){//创建子节点
				condition.addCondition(MTOR013, EQUAL, node.getString(ID), true);//当前节点的子节点
				condition.addCondition(MTOR016, EQUAL, "", false);//最右侧节点
				nodeRight=queryNode(condition);
				nodeDetail=setNodePosition(node.getString(ID), "", 
						nodeRight!=null?nodeRight.getString(ID):"", "",node.getString(PID),1);
				newNodeId=DBUtils.addOrUpdate(MTOR005, JsonUtil.getJson(nodeDetail));
				
				if(StringUtil.isNullOrEmpty(node.getString(MTOR014))) {
					//如果父节点以前没有子节点  变更父节点的子节点信息
					changeNodePosition(node.getString(ID), 2, newNodeId);
				}
				
			}else if(StringUtil.isEqual("1", nodeType)){//创建右节点
				condition.addCondition(ID, EQUAL, node.getString(MTOR016), true);//当前节点的右节点
				nodeRight=queryNode(condition);
				//1.创建新的右节点
				nodeDetail=setNodePosition(node.getString(MTOR013), "", 
						node.getString(ID), nodeRight!=null?nodeRight.getString(ID):"",
						node.getString(PID),1);
				newNodeId=DBUtils.addOrUpdate(MTOR005, JsonUtil.getJson(nodeDetail));
				//2.要变更当前节点的右节点信息
				changeNodePosition(node.getString(ID), 4, newNodeId);
				//3.变更之前右节点的左节点信息
				changeNodePosition(nodeRight.getString(ID), 3, newNodeId);
				
			}else{//创建左节点
				condition.addCondition(ID, EQUAL, node.getString(MTOR015), true);//当前节点的左节点
				nodeLeft=queryNode(condition);
				
				//1.创建新的左节点
				nodeDetail=setNodePosition(node.getString(MTOR013), "", 
						node.getString(ID), nodeLeft!=null?nodeLeft.getString(ID):""
						,node.getString(PID),1);
				newNodeId=DBUtils.addOrUpdate(MTOR005, JsonUtil.getJson(nodeDetail));
				//2.如果当前节点之前没有左节点 则变更父节点的子节点信息 
				if(nodeLeft==null){
					condition.addCondition(ID, EQUAL, node.getString(MTOR013), true);//当前节点的左节点
					nodeParent=queryNode(condition);
					changeNodePosition(nodeParent.getString(ID), 2, newNodeId);
				}
				//3.要变更当前节点的左节点信息
				changeNodePosition(node.getString(ID), 3, newNodeId);
				//4.变更之前左节点的右节点信息
				changeNodePosition(nodeLeft.getString(ID), 3, newNodeId);
			}
		}
		return newNodeId;
	}
	/**
	 * 删除节点
	 * @param nodeId 节点ID
	 * @return
	 */
	@Override
	public Result deleteNode(String nodeId) {
		// TODO Auto-generated method stub
		//查询出被删除节点信息
		Condition condition=new Condition();
		condition.addCondition(ID, EQUAL, nodeId, true);
		JSONObject delNode=queryNode(condition);
		if(delNode!=null){
			//1.递归查询删除的节点的子节点(包含子节点的子节点)
			JSONArray nodes=getChildNode(delNode.getString(ID));
			if(!JsonUtil.isNullOrEmpty(nodes)){
				nodes.add(delNode);
				//循环删除(In)
			}else{//没有子节点只删除当前一个节点
				
			}
			//2.删除的节点以及字节点
			
			//3.查询删除的节点的子节点
		}
		return null;
	}
	@Override
	public Result moveNode(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//递归查询子节点
	private JSONArray getChildNode(String nodeId){
		JSONArray allNodes=new JSONArray();
		Condition condition=new Condition();
		condition.addCondition(MTOR014, EQUAL, nodeId, true);
		JSONArray nodes=queryNodes(condition);
		while(!JsonUtil.isNullOrEmpty(nodes)){
			for(Object obj:nodes){
				JSONObject json=JsonUtil.getJson(obj);
				if(json!=null){
					//判断子节点的状态 不为新增则要提示 0表示失效 1表示新增  2表示编辑
					if(!StringUtil.isEqual(json.getString(MTOR021),ChangeType.ADD.toString())){
						ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), 
								ErrorMessage._60009.getMsg());
					}
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
	private JSONObject queryNode(Condition condition){
		JSONObject nodeJson=DBUtils.getObjectResult(MTOR005,null,condition);
		if(nodeJson==null){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		return null;
	}
	/***
	 * 查询节点集信息
	 * @param condition 查询条件
	 * @return
	 */
	private JSONArray queryNodes(Condition condition){
		JSONArray nodes=DBUtils.getArrayResult(MTOR005,null,condition);
		if(nodes==null){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		return null;
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
		NodeTo nodeDetail=new NodeTo();
		switch(changeTpye){
			case 1://改变父节点位置
				nodeDetail.setMtor013(positionNodeId);
				break;
			case 2://改变子节点位置
				nodeDetail.setMtor014(positionNodeId);
				break;
			case 3://改变左节点位置
				nodeDetail.setMtor015(positionNodeId);
				break;
			case 4://改变右节点位置
				nodeDetail.setMtor016(positionNodeId);
				break;	
		}
		nodeDetail.setId(nodeId);
		DBUtils.addOrUpdate(MTOR005, JsonUtil.getJson(nodeDetail));
	}

}
