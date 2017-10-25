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
    
    List<NodeTo> tempTree(String key,String validDate, int type,boolean flag);
}
