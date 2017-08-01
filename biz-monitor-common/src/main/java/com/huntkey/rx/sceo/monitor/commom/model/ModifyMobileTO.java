package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * Created by xuyf on 2017/5/22 0022.
 */
public class ModifyMobileTO {

    @NotBlank(message = "用户ID不能为空")
    private String id;

    @NotBlank(message = "新手机号不能为空")
    @Pattern(regexp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$", message = "新手机号格式校验失败")
    private String newMobileNumber;

    @NotBlank(message = "国际区号不能为空")
    @Pattern(regexp = "^[0]{2}[0-9]{1,4}$", message = "国际区号格式校验失败")
    private String worldPost = "0086";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewMobileNumber() {
        return newMobileNumber;
    }

    public void setNewMobileNumber(String newMobileNumber) {
        this.newMobileNumber = newMobileNumber;
    }

    public String getWorldPost() {
        return worldPost;
    }

    public void setWorldPost(String worldPost) {
        this.worldPost = worldPost;
    }
}
