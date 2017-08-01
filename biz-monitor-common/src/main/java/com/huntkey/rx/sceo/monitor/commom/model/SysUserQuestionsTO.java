package com.huntkey.rx.sceo.monitor.commom.model;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by xuyf on 2017/5/4 0004.
 */
public class SysUserQuestionsTO {

    private String id;

    @NotBlank(message = "密保问题不能为空")
    private String question;

    @NotBlank(message = "密保答案不能为空")
    private String answer;

    public SysUserQuestionsTO() {
    }

    public SysUserQuestionsTO(String id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
