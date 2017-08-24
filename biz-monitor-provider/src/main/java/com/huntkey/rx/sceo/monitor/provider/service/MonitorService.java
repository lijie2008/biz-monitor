package com.huntkey.rx.sceo.monitor.provider.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
public interface MonitorService {
	//监管树临时单预览查询
	JSONArray tempTree(String tempId,String validDate);
	//监管树临时单预览是否包含资源
	JSONArray resource(String[] nodes,String classId);
	//节点详情查询
	JSONObject nodeDetail(String nodeId);
	//节点关联资源查询
	JSONArray nodeResource(String nodeId,String classId);
	//节点详情保存
	String saveNodeDetail(NodeTo nodeDetail);
	//删除节点资源
	Result deleteNodeResource(String nodeId,String resourceId);
	//变更公式
	Result changeFormula(String nodeId,String formularId);
	//新增资源
	List<String> addResource(String nodeId,String[] resourceIds);
	Result saveTemp(String datas);
	//新增节点
	String addNode(String nodeId,int nodeType,String nodeName);
	//删除节点
	String deleteNode(String nodeId,int type);
	//移动节点
	String moveNode(String nodeId,String nodeParentId,String nodeLeftId,String nodeRightId);
	//新增监管树
	String addMonitorTree(AddMonitorTreeTo addMonitorTreeTo);
	//监管树维护
	String treeMaintaince(String classId,String rootId,String edmcNameEn);
}
