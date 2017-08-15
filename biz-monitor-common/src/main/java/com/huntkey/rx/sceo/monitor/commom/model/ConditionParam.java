package com.huntkey.rx.sceo.monitor.commom.model;

/**
 * 条件对象
 * Created by kongquan on 2017/8/10.
 */
public class ConditionParam {

    public final static String ATTR = "attr";
    public final static String OPERATOR = "operator";
    public final static String VALUE = "value";

    private String attr;
    private String operator;
    private String value;

    public ConditionParam(String attr, String operator, String value){
    	this.attr = attr;
    	this.operator = operator;
    	this.value = value;
    }
    
    
    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "attr:'" + attr + '\'' +
                ", operator:'" + operator + '\'' +
                ", value:'" + value + '\'' +
                '}';
    }
}
