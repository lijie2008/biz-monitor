package com.huntkey.rx.sceo.monitor.commom.model;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by xuyf on 2017/5/22 0022.
 */
public class ModifyEmailTO {

    @NotBlank(message = "用户ID不能为空")
    private String id;

    @NotBlank(message = "新邮箱地址不能为空")
    @Pattern(regexp = "^(\\w+\\.?)*\\w+@(?:\\w+\\.)\\w+$",message = "新邮箱地址格式校验失败")
    private String newEmail;

    @NotBlank(message = "国际地区不能为空")
    @Pattern(regexp = "^[A-Z]{2}$", message = "国籍代码格式校验失败")
    private String nationality = "CN";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
