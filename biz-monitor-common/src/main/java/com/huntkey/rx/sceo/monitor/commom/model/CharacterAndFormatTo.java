/**
 * Project Name:biz-monitor-common
 * File Name:CharacterAndFormatTo.java
 * Package Name:com.huntkey.rx.sceo.monitor.commom.model
 * Date:2017年8月17日下午2:00:55
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.commom.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:CharacterAndFormatTo
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年8月17日 下午2:00:55
 * @author   lijie
 * @version  
 * @see 	 
 */
public class CharacterAndFormatTo {
    
    private List<String> character;
    
    private String format;

    public List<String> getCharacter() {
        return character;
    }

    public void setCharacter(List<String> character) {
        this.character = character;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    public String format(JSONObject obj){
        if(this.character == null || this.character.isEmpty() || this.format == null)
            return null;
        String str = this.format;
        for(String s : this.character){
            str = str.replace(s, obj.getString(s) == null ? "": obj.getString(s));
        }
        return str;
    }
    
}

