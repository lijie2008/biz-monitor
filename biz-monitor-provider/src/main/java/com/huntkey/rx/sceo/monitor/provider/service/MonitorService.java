package com.huntkey.rx.sceo.monitor.provider.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
public interface MonitorService {
	//监管树临时单预览查询
	JSONArray tempTree(String tempId,int hasResource,String validDate);
	//监管树临时单预览是否包含资源
	Result containResource(String[] nodes);
	//节点详情查询
	JSONObject nodeDetail(String nodeId);
	//节点关联资源查询
	Result nodeResource(String nodeId);
	//节点详情保存
	Result saveNodeDetail(NodeTo nodeDetail);
	//删除节点资源
	Result deleteNodeResource(String nodeId,String resourceId);
	//变更公式
	Result changeFormula(String nodeId,String formularId);
	//新增资源
	Result addResource(String nodeId,String[] resourceIds);
	Result saveTemp(String datas);
	//新增节点
	String addNode(String nodeId,String nodeType);
	//删除节点
	Result deleteNode(String nodeId);
	//移动节点
	Result moveNode(String datas);
}
