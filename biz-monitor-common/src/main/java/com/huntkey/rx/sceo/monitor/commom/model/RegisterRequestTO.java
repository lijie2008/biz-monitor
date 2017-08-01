package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by xuyf on 2017/5/4 0004.
 */
public class RegisterRequestTO {

    @NotBlank(message = "注册账号不能为空")
    private String account;

    @NotNull(message = "注册类型不能为空")
    @DecimalMin(value = "1", message = "注册类型错误，1为手机，2为邮箱")
    @DecimalMax(value = "2", message = "注册类型错误，1为手机，2为邮箱")
    private int registerType;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,20}$", message = "密码格式校验失败")
    private String password;

    @Pattern(regexp = "^[0]{2}[0-9]{1,4}$", message = "国际区号格式校验失败")
    private String worldPost;

    @Pattern(regexp = "^[A-Z]{2}$", message = "国籍代码格式校验失败")
    private String nationality;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getRegisterType() {
        return registerType;
    }

    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWorldPost() {
        return worldPost;
    }

    public void setWorldPost(String worldPost) {
        this.worldPost = worldPost;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

}
