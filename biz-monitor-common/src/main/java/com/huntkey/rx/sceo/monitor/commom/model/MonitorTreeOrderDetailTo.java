/**
 * Project Name:biz-monitor-common
 * File Name:MonitorTreeOrderDetailTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月8日下午2:33:44
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.List;

/**
 * ClassName:MonitorTreeOrderDetailTo
 * Date:     2017年8月8日 下午2:33:44
 * @author   lijie
 * @version  
 * @see 	 
 */
public class MonitorTreeOrderDetailTo extends MonitorTreeOrderTo {
    
    /**
     * 关联的节点集合
     */
    private List<NodeDetailTo> mtor005;
    
    public List<NodeDetailTo> getMtor005() {
        return mtor005;
    }

    public void setMtor005(List<NodeDetailTo> mtor005) {
        this.mtor005 = mtor005;
    }
}

