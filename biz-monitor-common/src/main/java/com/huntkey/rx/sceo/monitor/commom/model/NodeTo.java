/**
 * Project Name:biz-monitor-common
 * File Name:NodeDetail.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年10月17日上午10:45:31
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;

/**
 * ClassName:NodeDetail
 * Date:     2017年10月17日 上午10:45:31
 * @author   lijie
 * @version  
 * @see 	 
 */
public class NodeTo implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String key;
    
    private String nodeNo;
    
    private String nodeName;
    
    private String nodeDef;
    
    private String major;
    
    private String majorText;
    
	private String assit;
    
    private String assitText;
    
    private String begin;
    
    private String end;
    
    private String indexConf;
    
    private double seq;
    
    private String lvlCode;
    
    private int lvl;
    
    private String mtorEnum;
    
    private List<ResourceTo> resources;
    
    private String relateCnd;
    
    private int type;
    
    private String relateId;
    
    private List<BackTo> backSet;

    public String getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(String nodeNo) {
        this.nodeNo = nodeNo;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeDef() {
        return nodeDef;
    }

    public void setNodeDef(String nodeDef) {
        this.nodeDef = nodeDef;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getAssit() {
        return assit;
    }

    public void setAssit(String assit) {
        this.assit = assit;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getIndexConf() {
        return indexConf;
    }

    public void setIndexConf(String indexConf) {
        this.indexConf = indexConf;
    }

    public String getLvlCode() {
        return lvlCode;
    }

    public void setLvlCode(String lvlCode) {
        this.lvlCode = lvlCode;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public String getMtorEnum() {
        return mtorEnum;
    }

    public void setMtorEnum(String mtorEnum) {
        this.mtorEnum = mtorEnum;
    }

    public List<ResourceTo> getResources() {
        return resources;
    }

    public void setResources(List<ResourceTo> resources) {
        this.resources = resources;
    }

    public String getRelateCnd() {
        return relateCnd;
    }

    public void setRelateCnd(String relateCnd) {
        this.relateCnd = relateCnd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRelateId() {
        return relateId;
    }

    public void setRelateId(String relateId) {
        this.relateId = relateId;
    }

    public List<BackTo> getBackSet() {
        return backSet;
    }

    public void setBackSet(List<BackTo> backSet) {
        this.backSet = backSet;
    }

    public double getSeq() {
        return seq;
    }

    public void setSeq(double seq) {
        this.seq = seq;
    }
    
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMajorText() {
        return majorText;
    }

    public void setMajorText(String majorText) {
        this.majorText = majorText;
    }

    public String getAssitText() {
        return assitText;
    }

    public void setAssitText(String assitText) {
        this.assitText = assitText;
    }

    public static List<NodeTo> setValue(JSONArray orderNodes){
        
        List<NodeTo> nodes = new ArrayList<NodeTo>();
        
        for(int i = 0; i < orderNodes.size(); i++){
            
            JSONObject to = orderNodes.getJSONObject(i);
            NodeTo node = new NodeTo();
            
            node.setNodeNo(to.getString("mtor_node_no"));
            node.setNodeName(to.getString("mtor_node_name"));
            node.setNodeDef(to.getString("mtor_node_def"));
            node.setMajor(to.getString("mtor_major"));
            node.setAssit(to.getString("mtor_assit"));
            node.setIndexConf(to.getString("mtor_index_conf"));
            node.setSeq(Double.valueOf(to.getString("mtor_seq")));
            node.setMtorEnum(to.getString("mtor_enum"));
            node.setLvl(Integer.valueOf(to.getString("mtor_lvl")));
            node.setLvlCode(to.getString("mtor_lvl_code"));
            node.setRelateCnd(to.getString("mtor_relate_cnd"));
            node.setType(Integer.valueOf(to.getString("mtor_type")));
            node.setRelateId(to.getString("mtor_relate_id"));
            
            node.setBegin(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(to.getLong("mtor_beg"))));
            node.setEnd(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(to.getLong("mtor_end"))));
            
            //TODO 主管人 协管人赋值
            node.setAssitText(null);
            node.setMajorText(null);
            // TODO 主管人 协管人赋值
            
            // 资源集 
            JSONArray res = to.getJSONArray("mtor_res_set");
            
            if(res != null && !res.isEmpty()){
                List<ResourceTo> resources = new ArrayList<ResourceTo>();
                for(int k = 0; k < res.size(); k++){
                    JSONObject rr = res.getJSONObject(k);
                    ResourceTo resourceTo = new ResourceTo();
                    resourceTo.setResId(rr.getString("mtor_res_id"));
                    resourceTo.setText(rr.getString("text"));
                    
                    resources.add(resourceTo);
                    
                }
                node.setResources(resources);
            }
            
            JSONArray bks = to.getJSONArray("mtor_bk_set");
            
            if(bks != null && !bks.isEmpty()){
                List<BackTo> bkList = new ArrayList<BackTo>();
                for(int k = 0; k < bks.size(); k++){
                    JSONObject bk = bks.getJSONObject(k);
                    BackTo bkTo = new BackTo();
                    bkTo.setBk1(bk.getString("mtor_bk1"));
                    bkTo.setBk2(bk.getString("mtor_bk2"));
                    bkTo.setBk3(Integer.valueOf(bk.getString("mtor_bk3")));
                    bkList.add(bkTo);
                }
                node.setBackSet(bkList);
            }
            nodes.add(node);
        }
        return nodes;
    }
}

