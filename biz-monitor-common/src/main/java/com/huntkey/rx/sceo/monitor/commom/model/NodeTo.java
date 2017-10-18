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
import java.util.List;

/**
 * ClassName:NodeDetail
 * Date:     2017年10月17日 上午10:45:31
 * @author   lijie
 * @version  
 * @see 	 
 */
public class NodeTo implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private String nodeNo;
    
    private String nodeName;
    
    private String nodeDef;
    
    private String major;
    
    private String assit;
    
    private String begin;
    
    private String end;
    
    private String indexConf;
    
    private double seq;
    
    private String lvlCode;
    
    private int lvl;
    
    private int mtorEnum;
    
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

    public int getMtorEnum() {
        return mtorEnum;
    }

    public void setMtorEnum(int mtorEnum) {
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
    
}

