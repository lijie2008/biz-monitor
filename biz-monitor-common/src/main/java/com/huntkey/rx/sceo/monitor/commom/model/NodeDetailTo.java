/**
 * Project Name:biz-monitor-common
 * File Name:NodeTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月8日下午2:27:40
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:NodeDetailTo
 * Date:     2017年8月8日 下午2:27:40
 * @author   lijie
 * @version  
 * @see 	 
 */
public class NodeDetailTo extends NodeTo implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 关联资源对象集合
     */
    private List<ResourceTo> mtor019;
    
    public List<ResourceTo> getMtor019() {
        return mtor019;
    }

    public void setMtor019(List<ResourceTo> mtor019) {
        this.mtor019 = mtor019;
    }
}

