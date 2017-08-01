package com.huntkey.rx.sceo.monitor.commom.model;

/**
 * Created by xuyf on 2017/5/5 0005.
 */
public class ValidationTO {

    private String mobileNumber;

    private String worldPost;

    private String email;

    private String nationality;

    private Boolean questionFlag;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWorldPost() {
        return worldPost;
    }

    public void setWorldPost(String worldPost) {
        this.worldPost = worldPost;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Boolean getQuestionFlag() {
        return questionFlag;
    }

    public void setQuestionFlag(Boolean questionFlag) {
        this.questionFlag = questionFlag;
    }
}
