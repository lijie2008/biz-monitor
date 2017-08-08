package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/***********************************************************************
 * @author chenxj												      
 * 																	  
 * @email: kaleson@163.com											  
 * 																	  
 * @date : 2017年7月28日 下午3:44:50											 
 *																	  															 
 **********************************************************************/
// 查询参数对象
public class SearchParam {
    //////////////////////////常量的定义//////////////////////////////////////////////
    public final static String COLUMNS = "columns";
    public final static String CONDITIONS ="conditions";
    public final static String PAGENATION ="pagenation";
    public final static String ORDERBY = "orderby";
    ////////////////////////////////////////////////////////////////////////
    //列名
    private List<String> columns  = null ;
    //条件数组
    private List<ConditionParam> conditions = null;
    //分页
    private PagenationParam pagenation = null;
    //排序
    private List<SortParam> orderby = null ;

//    public SearchParam(){
//        columns = new ArrayList<String>();
//        conditions = new ArrayList<ConditionParam>();
//        pagenation = new PagenationParam();
//        orderby = new ArrayList<SortParamer>();
//    }

    //初始化数据
    public void  SearchParam(JSONObject jsonObject){
        //需要查询的列
        if(jsonObject.containsKey(COLUMNS)){
            JSONArray _columns = jsonObject.getJSONArray(COLUMNS);
            this.columns = JSON.parseArray(JSONArray.toJSONString(_columns), String.class);
        }
        //查询条件
        if(jsonObject.containsKey(CONDITIONS)) {
            JSONArray _conditions = jsonObject.getJSONArray(CONDITIONS);
            this.conditions = JSON.parseArray(JSONArray.toJSONString(_conditions), ConditionParam.class);
        }
        //查询的分页属性
        if(jsonObject.containsKey(PAGENATION)){
            JSONObject _pagenation = jsonObject.getJSONObject(PAGENATION);
            this.pagenation = JSON.parseObject(_pagenation.toJSONString(),PagenationParam.class);
        }
        //排序 属性
        if(jsonObject.containsKey(ORDERBY)){
            JSONArray _orderby = jsonObject.getJSONArray(ORDERBY);
            this.orderby = JSON.parseArray(_orderby.toJSONString(),SortParam.class);
        }
        return ;
    }

    public void addConditions(ConditionParam conditionParam){
        conditions.add(conditionParam);
        return;
    }

    public void addSortOrder(SortParam sortParamer){
        orderby.add(sortParamer);
        return;
    }

    public PagenationParam getPagenation() {
        return pagenation;
    }

    public void setPagenation(PagenationParam pagenation) {
        this.pagenation = pagenation;
    }

    //获取对象类型
    public JSONArray getColumnsArray(){
        JSONArray jsonArray  = new JSONArray();
        for (String column: columns){
            jsonArray.add(column);
        }
        return jsonArray;
    }

    public JSONArray getConditionsArray(){
        JSONArray jsonArray = new JSONArray();
        for(ConditionParam conditionParam: conditions){
            String jsonString = JSON.toJSONString(conditionParam);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public JSONObject getPagenationJson(){
       String jsonString = JSON.toJSONString(pagenation);
       JSONObject jsonObject = JSON.parseObject(jsonString);
       return jsonObject;
    }

    public JSONArray getSortOrderArray(){
        JSONArray jsonArray = new JSONArray();
        for(ConditionParam conditionParam: conditions){
            String jsonString = JSON.toJSONString(conditionParam);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        return "${" +
                "'columns':'" + getColumnsArray().toJSONString() + '\''+
                ",'conditions':'" + getConditionsArray().toJSONString() + '\''+
                ",'pagenation':'" + getPagenationJson().toString() + '\''+
                ",'orderby':'" + getSortOrderArray().toJSONString() + '\''+
                '}';
    }

}
