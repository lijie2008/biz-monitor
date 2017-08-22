package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
public class InputArgument {
	
	public void setAdduser(String adduser) {
		this.adduser = adduser;
	}

	public void setModuser(String moduser) {
		this.moduser = moduser;
	}


	// 1. 需要持久化的数据集合
    private JSONArray params;
    
    // 1. 新增还是修改
    private int addOrUpdate;

	// 2. 操作的edm类名称
    private String edmName;

    // 2. 操作的新增人
    private String adduser;
    
    // 2. 操作的修改人
    private String moduser;
    
    // 3. 查询的条件
    private JSONArray conditions;

    // 5. 需要返回的属性集合
    private Object[] columns;     

    // 6. 分页信息
    private JSONObject pagenation;

    // 9. 需要排序的字段
    private JSONArray orders;
    
    // 10. 类型
    private String type;
    
    // 10. id
    private List ids;

    public JSONArray getData() {
        return params;
    }
    public void setAddOrUpdate(int addOrUpdate) {
		this.addOrUpdate = addOrUpdate;
	}
    public void addData(Object data) {
    	if(data instanceof JSONObject){
	    	if(params==null){
	    		params=new JSONArray();
	    	}
	    	if(addOrUpdate==0){
	    		((JSONObject) data).put("adduser", adduser);
	    	}
	    	else{
	    		((JSONObject) data).put("moduser", moduser);
	    	}
	        this.params.add(data);
    	}else if(data instanceof JSONArray){
    		Map<String, Object> map=new HashMap<String, Object>();  
    		if(addOrUpdate==0){
    			map.put("adduser", adduser);
    		}
    		else{
    			map.put("moduser", moduser);
    		}
    		this.params=JsonUtil.addAttr((JSONArray) data, map);
    	}
    }

    public String getEdmName() {
        return edmName;
    }

    public void setEdmName(String edmName) {
        this.edmName = edmName;
    }

    public JSONArray getConditions() {
        return conditions;
    }

    public JSONArray getParams() {
		return params;
	}

	public void setParams(JSONArray params) {
		this.params = params;
	}

	public JSONObject getPagenation() {
		return pagenation;
	}

	public void setPagenation(JSONObject pagenation) {
		this.pagenation = pagenation;
	}

	public void setConditions(JSONArray conditions) {
		this.conditions = conditions;
	}

	public void setOrders(JSONArray orders) {
		this.orders = orders;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List getIds() {
		return ids;
	}

	public void setIds(List ids) {
		this.ids = ids;
	}

	public Object[] getColumns() {
        return columns;
    }

    public void setColumns(Object[] columns) {
        this.columns = columns;
    }

    public JSONArray getOrders() {
        return orders;
    }


    @Override
    public String toString(){
        JSONObject jsonObject = new JSONObject();
        JSONObject search = new JSONObject();
        if(!JsonUtil.isNullOrEmpty(params)){
        	jsonObject.put("params", params);
        }
        if(!StringUtil.isNullOrEmpty(edmName)){
        	jsonObject.put("edmName", edmName);
        }
        if(!JsonUtil.isNullOrEmpty(conditions)){
        	search.put("conditions", conditions);
        }
        if(columns!=null && columns.length>0){
        	search.put("columns", columns);
        }
        if(ids!=null && ids.size()>0){
        	search.put("ids", ids);
        }
        if(!StringUtil.isNullOrEmpty(type)){
        	search.put("type", type);
        }
        if(pagenation!=null){
        	search.put("pagenation", pagenation);
        }
        if(!StringUtil.isNullOrEmpty(orders)){
        	search.put("orders", orders);
        }
        if(search!=null && !search.isEmpty()){
        	jsonObject.put("search", search);
        }
        return jsonObject.toString();
    }
}
