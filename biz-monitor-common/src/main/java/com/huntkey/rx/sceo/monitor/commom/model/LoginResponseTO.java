package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clarkzhao on 2017/3/28.
 */
public class LoginResponseTO {
    private String id;
    private final Map<String, Object> attributes;

    public LoginResponseTO() {
        attributes = new HashMap<String, Object>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
