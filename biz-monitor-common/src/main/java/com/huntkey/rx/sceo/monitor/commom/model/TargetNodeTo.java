/**
 * Project Name:biz-monitor-common
 * File Name:TargetMonitorTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月9日下午5:44:02
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.List;

/**
 * TargetNodeTo 监管类  - 节点信息
 * Date:     2017年8月9日 下午5:44:02
 * @author   lijie
 * @version  
 * @see 	 
 */
public class TargetNodeTo {
    
    private String id;
    
    /**
     * 节点编号
     */
    private String moni001;
    
    /**
     * 节点名称
     */
    private String moni002;
    
    /**
     * 节点定义
     */
    private String moni003;
    
    /**
     * 生效时间
     */
    private String moni004;
    
    /**
     * 失效时间
     */
    private String moni005;
    
    /**
     * 上级对象
     */
    private String moni006;
    
    /**
     * 下级对象
     */
    private String moni007;
    /**
     * 左邻对象
     */
    private String moni008;
    
    /**
     * 右邻对象
     */
    private String moni009;
    
    /**
     * 末端对象
     */
    private String moni010;
    
    /**
     * 指标配置
     */
    private String moni011;
    
    /**
     * 层级编码
     */
    private String moni013;
    
    /**
     * 节点所在层级
     */
    private String moni014;
    
    /**
     * 目标资源集合
     */
    private List<TargetResourceTo> moni015;
    
    /**
     * 直属关联对象条件
     */
    private String moni017;
    
    /**
     * 是否枚举
     */
    private String moni018;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoni001() {
        return moni001;
    }

    public void setMoni001(String moni001) {
        this.moni001 = moni001;
    }

    public String getMoni002() {
        return moni002;
    }

    public void setMoni002(String moni002) {
        this.moni002 = moni002;
    }

    public String getMoni003() {
        return moni003;
    }

    public void setMoni003(String moni003) {
        this.moni003 = moni003;
    }

    public String getMoni004() {
        return moni004;
    }

    public void setMoni004(String moni004) {
        this.moni004 = moni004;
    }

    public String getMoni005() {
        return moni005;
    }

    public void setMoni005(String moni005) {
        this.moni005 = moni005;
    }

    public String getMoni006() {
        return moni006;
    }

    public void setMoni006(String moni006) {
        this.moni006 = moni006;
    }

    public String getMoni007() {
        return moni007;
    }

    public void setMoni007(String moni007) {
        this.moni007 = moni007;
    }

    public String getMoni008() {
        return moni008;
    }

    public void setMoni008(String moni008) {
        this.moni008 = moni008;
    }

    public String getMoni009() {
        return moni009;
    }

    public void setMoni009(String moni009) {
        this.moni009 = moni009;
    }

    public String getMoni010() {
        return moni010;
    }

    public void setMoni010(String moni010) {
        this.moni010 = moni010;
    }

    public String getMoni011() {
        return moni011;
    }

    public void setMoni011(String moni011) {
        this.moni011 = moni011;
    }


    public String getMoni013() {
        return moni013;
    }

    public void setMoni013(String moni013) {
        this.moni013 = moni013;
    }

    public String getMoni014() {
        return moni014;
    }

    public void setMoni014(String moni014) {
        this.moni014 = moni014;
    }

    public List<TargetResourceTo> getMoni015() {
        return moni015;
    }

    public void setMoni015(List<TargetResourceTo> moni015) {
        this.moni015 = moni015;
    }

    public String getMoni017() {
        return moni017;
    }

    public void setMoni017(String moni017) {
        this.moni017 = moni017;
    }

    public String getMoni018() {
        return moni018;
    }

    public void setMoni018(String moni018) {
        this.moni018 = moni018;
    }

}

