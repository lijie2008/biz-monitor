package com.huntkey.rx.sceo.monitor.provider.service;

import com.huntkey.rx.commons.utils.rest.Result;
public interface MonitorService {
	//监管树临时单预览查询
	Result tempTree(String tempId,int hasResource,String validDate);
	//监管树临时单预览是否包含资源
	Result containResource(String[] nodes);
	//节点详情查询
	Result nodeDetail(String nodeId);
	//节点关联资源查询
	Result nodeResource(String nodeId);
	Result saveNodeDetail(String datas);
	Result deleteNodeResource(String datas);
	Result changeFormula(String datas);
	Result addResource(String datas);
	Result saveTemp(String datas);
	Result addNode(String datas);
	Result deleteNode(String datas);
	Result moveNode(String datas);
}
