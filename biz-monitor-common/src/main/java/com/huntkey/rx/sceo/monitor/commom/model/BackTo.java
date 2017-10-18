/**
 * Project Name:biz-monitor-common
 * File Name:BackTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年10月17日上午10:54:23
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;

/**
 * ClassName:BackTo 备用字段
 * Date:     2017年10月17日 上午10:54:23
 * @author   lijie
 * @version  
 * @see 	 
 */
public class BackTo implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String bk1;
    
    private String bk2;
    
    private int bk3;

    public String getBk1() {
        return bk1;
    }

    public void setBk1(String bk1) {
        this.bk1 = bk1;
    }

    public String getBk2() {
        return bk2;
    }

    public void setBk2(String bk2) {
        this.bk2 = bk2;
    }

    public int getBk3() {
        return bk3;
    }

    public void setBk3(int bk3) {
        this.bk3 = bk3;
    }
    
}

