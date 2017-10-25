package com.huntkey.rx.sceo.monitor.provider.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
public interface MonitorService {
    
    String addMonitorTree(AddMonitorTreeTo addMonitorTreeTo);

    String treeMaintaince(String classId,String rootId,String rootEdmcNameEn);
    
    JSONObject checkOrder(String classId, String rootId, int type);
    
    String editBefore(String key, boolean flag);
    
    List<NodeTo> tempTree(String tempId,String validDate, int type,boolean flag);
  	//节点详情查询
  	NodeTo nodeDetail(String tempId,String levelCode);
  	//节点详情保存
  	String saveNodeDetail(NodeTo nodeDetail);
  	//删除节点资源
  	String deleteNodeResource(String tempId,String levelCode,String resourceId);
  	//新增资源
  	String addResource(String tempId,String levelCode,String resourceId,String resourceText);
  	//新增节点
  	String addNode(String tempId,String levelCode,int type);
    //移动节点
  	String moveNode(String tempId,String moveLvlcode,String desLvlcode,int type);  	
  	//删除节点
  	String deleteNode(String key,String levelCode,int type);
  	// 获取临时单中当前节点所有子节点
  	List<NodeTo> getChildNode(String key,String levelCode);
}
