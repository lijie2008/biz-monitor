package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * Created by xuyf on 2017/5/22 0022.
 */
public class ResetPasswordTO {

    public ResetPasswordTO(){}

    public ResetPasswordTO(String id, String newPassword) {
        this.id = id;
        this.newPassword = newPassword;
    }

    @NotBlank(message = "用户ID不能为空")
    private String id;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,20}$", message = "新密码格式校验失败")
    private String newPassword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
