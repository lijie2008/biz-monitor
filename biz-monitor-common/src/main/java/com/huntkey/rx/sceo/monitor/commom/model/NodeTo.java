/**
 * Project Name:biz-monitor-common
 * File Name:NodeTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月5日下午5:21:10
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;

/**
 * ClassName:NodeTo 节点对象
 * Date:     2017年8月5日 下午5:21:10
 * @author   lijie
 * @version  
 * @see 	 
 */
public class NodeTo {
    
    /**
     * 节点id
     */
    private String id;
    
    /**
     * 关联临时单id
     */
    @NotBlank(message="监管树临时单ID不能为空")
    private String pid;
    
    /**
     * 节点编号
     */
    @NotBlank(message="节点编号不能为空")
    private String mtor006;
    
    /**
     * 节点名称
     */
    @NotBlank(message="节点名称不能为空")
    private String mtor007;
    
    /**
     * 节点定义
     */
    private String mtor008;
    
    /**
     * 主管人
     */
    private String mtor009;
    
    /**
     * 协管人
     */
    private String mtor010;
    
    /**
     * 生效时间
     */
    private String mtor011;
    
    /**
     * 失效时间
     */
    private String mtor012;
    
    /**
     * 上级对象
     */
    private String mtor013;
    
    /**
     * 下级对象
     */
    private String mtor014;
    
    /**
     * 左邻对象
     */
    private String mtor015;
    
    /**
     * 右邻对象
     */
    private String mtor016;
    
    /**
     * 层级编码
     */
    private String mtor017;
    
    /**
     * 节点所在层级
     */
    private String mtor018;
    
    /**
     * 监管更新标记
     */
    private int mtor021;
    
    /**
     * 直属关联对象条件(方法)
     */
    private String mtor022;

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

    public String getMtor006() {
        return mtor006;
    }

    public void setMtor006(String mtor006) {
        this.mtor006 = mtor006;
    }

    public String getMtor007() {
        return mtor007;
    }

    public void setMtor007(String mtor007) {
        this.mtor007 = mtor007;
    }

    public String getMtor008() {
        return mtor008;
    }

    public void setMtor008(String mtor008) {
        this.mtor008 = mtor008;
    }

    public String getMtor009() {
        return mtor009;
    }

    public void setMtor009(String mtor009) {
        this.mtor009 = mtor009;
    }

    public String getMtor010() {
        return mtor010;
    }

    public void setMtor010(String mtor010) {
        this.mtor010 = mtor010;
    }

    public String getMtor011() {
        return mtor011;
    }

    public void setMtor011(String mtor011) {
        this.mtor011 = mtor011;
    }

    public String getMtor012() {
        return mtor012;
    }

    public void setMtor012(String mtor012) {
        this.mtor012 = mtor012;
    }

    public String getMtor013() {
        return mtor013;
    }

    public void setMtor013(String mtor013) {
        this.mtor013 = mtor013;
    }

    public String getMtor014() {
        return mtor014;
    }

    public void setMtor014(String mtor014) {
        this.mtor014 = mtor014;
    }

    public String getMtor015() {
        return mtor015;
    }

    public void setMtor015(String mtor015) {
        this.mtor015 = mtor015;
    }

    public String getMtor016() {
        return mtor016;
    }

    public void setMtor016(String mtor016) {
        this.mtor016 = mtor016;
    }

    public String getMtor017() {
        return mtor017;
    }

    public void setMtor017(String mtor017) {
        this.mtor017 = mtor017;
    }

    public String getMtor018() {
        return mtor018;
    }

    public void setMtor018(String mtor018) {
        this.mtor018 = mtor018;
    }

    public int getMtor021() {
        return mtor021;
    }

    public void setMtor021(int mtor021) {
        this.mtor021 = mtor021;
    }

    public String getMtor022() {
        return mtor022;
    }

    public void setMtor022(String mtor022) {
        this.mtor022 = mtor022;
    }

}

