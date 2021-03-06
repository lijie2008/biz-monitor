package com.huntkey.rx.sceo.monitor.provider.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
public interface MonitorService {
    
    String addMonitorTree(AddMonitorTreeTo addMonitorTreeTo) throws Exception;

    String treeMaintaince(String classId,String rootId,String rootEdmcNameEn) throws Exception;
    
    JSONObject checkOrder(String classId, String rootId, int type) throws Exception;
    
    String editBefore(String key, boolean flag) throws Exception;
    
    List<NodeTo> tempTree(String key,String validDate, int type,boolean flag) throws Exception;
    
  	//节点详情查询
  	NodeTo nodeDetail(String key,String lvlCode);
  	//节点详情保存
  	String saveNodeDetail(NodeTo nodeDetail);
  	//删除节点资源
  	String deleteNodeResource(String key,String lvlCode,String resourceId);
  	//新增资源
  	String addResource(String key,String lvlCode,String resourceId,String resourceText);
  	//新增节点
  	String addNode(String key,String lvlCode,int type);
    //移动节点
  	String moveNode(String key,String moveLvlcode,String desLvlcode,int type);  	
  	//删除节点
  	String deleteNode(String key,String lvlCode,int type);
  	// 获取临时单中当前节点所有子节点
  	List<NodeTo> getChildNode(String key,String lvlCode);
  	
  	List<ResourceTo> formula(NodeTo node) throws Exception;
}
