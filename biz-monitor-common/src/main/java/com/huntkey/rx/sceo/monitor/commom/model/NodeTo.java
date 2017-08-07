/**
 * Project Name:biz-monitor-common
 * File Name:NodeTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月5日下午5:21:10
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:NodeTo 节点对象
 * Date:     2017年8月5日 下午5:21:10
 * @author   lijie
 * @version  
 * @see 	 
 */
public class NodeTo implements Serializable{
    
    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String id;
    
    /**
     * 关联临时单id
     */
    private String pid;
    
    /**
     * 节点编号
     */
    private String mtor_007;
    
    /**
     * 节点名称
     */
    private String mtor_008;
    
    /**
     * 节点定义
     */
    private String mtor_009;
    
    /**
     * 主管人
     */
    private String mtor_010;
    
    /**
     * 协管人
     */
    private String mtor_011;
    
    /**
     * 生效时间
     */
    private String mtor_012;
    
    /**
     * 失效时间
     */
    private String mtor_013;
    
    /**
     * 上级对象
     */
    private String mtor_014;
    
    /**
     * 下级对象
     */
    private String mtor_015;
    
    /**
     * 左邻对象
     */
    private String mtor_016;
    
    /**
     * 右邻对象
     */
    private String mtor_017;
    
    /**
     * 层级编码
     */
    private String mtor_019;
    
    /**
     * 节点所在层级
     */
    private String mtor_020;
    
    /**
     * 关联资源对象集合
     */
    private List<ResourceTo> mtor_021;
    
    /**
     * 监管更新标记
     */
    private String mtor_023;
    
    /**
     * 直属关联对象条件(方法)
     */
    private String mtor_024;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMtor_007() {
        return mtor_007;
    }

    public void setMtor_007(String mtor_007) {
        this.mtor_007 = mtor_007;
    }

    public String getMtor_008() {
        return mtor_008;
    }

    public void setMtor_008(String mtor_008) {
        this.mtor_008 = mtor_008;
    }

    public String getMtor_009() {
        return mtor_009;
    }

    public void setMtor_009(String mtor_009) {
        this.mtor_009 = mtor_009;
    }

    public String getMtor_010() {
        return mtor_010;
    }

    public void setMtor_010(String mtor_010) {
        this.mtor_010 = mtor_010;
    }

    public String getMtor_011() {
        return mtor_011;
    }

    public void setMtor_011(String mtor_011) {
        this.mtor_011 = mtor_011;
    }

    public String getMtor_012() {
        return mtor_012;
    }

    public void setMtor_012(String mtor_012) {
        this.mtor_012 = mtor_012;
    }

    public String getMtor_013() {
        return mtor_013;
    }

    public void setMtor_013(String mtor_013) {
        this.mtor_013 = mtor_013;
    }

    public String getMtor_014() {
        return mtor_014;
    }

    public void setMtor_014(String mtor_014) {
        this.mtor_014 = mtor_014;
    }

    public String getMtor_015() {
        return mtor_015;
    }

    public void setMtor_015(String mtor_015) {
        this.mtor_015 = mtor_015;
    }

    public String getMtor_016() {
        return mtor_016;
    }

    public void setMtor_016(String mtor_016) {
        this.mtor_016 = mtor_016;
    }

    public String getMtor_017() {
        return mtor_017;
    }

    public void setMtor_017(String mtor_017) {
        this.mtor_017 = mtor_017;
    }

    public String getMtor_019() {
        return mtor_019;
    }

    public void setMtor_019(String mtor_019) {
        this.mtor_019 = mtor_019;
    }

    public String getMtor_020() {
        return mtor_020;
    }

    public void setMtor_020(String mtor_020) {
        this.mtor_020 = mtor_020;
    }

    public List<ResourceTo> getMtor_021() {
        return mtor_021;
    }

    public void setMtor_021(List<ResourceTo> mtor_021) {
        this.mtor_021 = mtor_021;
    }

    public String getMtor_023() {
        return mtor_023;
    }

    public void setMtor_023(String mtor_023) {
        this.mtor_023 = mtor_023;
    }

    public String getMtor_024() {
        return mtor_024;
    }

    public void setMtor_024(String mtor_024) {
        this.mtor_024 = mtor_024;
    }
    
}

