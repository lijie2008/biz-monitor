package com.huntkey.rx.sceo.monitor.provider.service.impl;

import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.Condition;
import com.huntkey.rx.sceo.monitor.commom.model.NodeDetailSaveTO;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.utils.DBUtils;
@Service
public class MonitorServiceImpl implements MonitorService {
	@Autowired
	DBUtils DBUtils;
	/***
	 * 查询监管树临时结构
	 * @param tempId 监管树临时单id
	 * @param hasResource 是否包含资源
	 * @param validDate 日期
	 * @return
	 */
	@Override
	public Result tempTree(String tempId, int hasResource, String validDate) {
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		//初始化查询参数器
		Condition condition=new Condition();
		//组装查询条件
		condition.addCondition(PID, EQUAL, tempId, true);
		condition.addCondition(MODE010, GT, validDate, false);
		//查询节点集合表
		JSONArray nodeArray=DBUtils.getArrayResult(MODE003,null,condition);
		if(JsonUtil.isNullOrEmpty(nodeArray)){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		result.setData(nodeArray);
		return result;
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
			resourceObj=DBUtils.getObjectResult(MODE018,null,condition);
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
	public Result nodeDetail(String nodeId) {
		// TODO Auto-generated method stub
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		//组装查询条件
		Condition condition=new Condition();
		condition.addCondition(ID, EQUAL, nodeId, true);
		//查询节点详情
		JSONObject nodeJson=DBUtils.getObjectResult(MODE003,null,condition);
		if(nodeJson==null){
			ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
					ErrorMessage._60003.getMsg()); 
		}
		result.setData(nodeJson);
		return result;
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
	public Result saveNodeDetail(NodeDetailSaveTO datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result deleteNodeResource(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result changeFormula(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result addResource(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result saveTemp(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result addNode(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result deleteNode(String datas) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result moveNode(String datas) {
		// TODO Auto-generated method stub
		return null;
	}


}
