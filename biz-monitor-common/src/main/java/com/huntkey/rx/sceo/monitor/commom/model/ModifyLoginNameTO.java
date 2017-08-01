package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * Created by xuyf on 2017/5/22 0022.
 */
public class ModifyLoginNameTO {

    @NotBlank(message = "用户ID不能为空")
    private String id;

    @NotBlank(message = "锐信号不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{6,20}$", message = "锐信号格式校验失败")
    private String newLoginName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewLoginName() {
        return newLoginName;
    }

    public void setNewLoginName(String newLoginName) {
        this.newLoginName = newLoginName;
    }
}
