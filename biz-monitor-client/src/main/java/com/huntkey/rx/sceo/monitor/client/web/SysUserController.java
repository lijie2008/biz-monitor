package com.huntkey.rx.sceo.monitor.client.web;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.feign.SysUserService;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyEmailTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyLoginNameTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyMobileTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyPasswordTO;
import com.huntkey.rx.sceo.monitor.commom.model.RegisterRequestTO;
import com.huntkey.rx.sceo.monitor.commom.model.ResetPasswordTO;
import com.huntkey.rx.sceo.monitor.commom.model.SysUserQuestionsTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by xuyf on 2017/5/5 0005.
 */
@RestController
@RequestMapping("/v1/sys_user")
@Api(value = "用户注册、认证相关接口", description = "用户注册，用户登录，认证，修改认证，修改密码等功能接口")
@Validated
public class SysUserController {

    @Autowired
    SysUserService sysUserService;

    /**
     * 用户注册
     * @param registerRequestTO
     * @return
     */
    @PostMapping
    @ApiOperation("用户注册")
    public Result register(@Validated @RequestBody RegisterRequestTO registerRequestTO){
        Result result = sysUserService.register(registerRequestTO);
        return result;
    }

    /**
     * 重置密码
     * @param resetPasswordTO
     * @return
     */
    @PutMapping("/password/reset")
    @ApiOperation("重置密码")
    public Result resetPassword(@Validated @RequestBody ResetPasswordTO resetPasswordTO){
        Result result = sysUserService.resetPassword(resetPasswordTO);
        return result;
    }

    /**
     * 修改用户手机号
     * @param modifyMobileTO
     * @return
     */
    @PutMapping("/mobile")
    @ApiOperation("修改用户手机号")
    public Result modifyMobile(@Validated @RequestBody ModifyMobileTO modifyMobileTO){
        Result result = sysUserService.modifyMobile(modifyMobileTO);
        return result;
    }

    /**
     * 修改用户邮箱
     * @param modifyEmailTO
     * @return
     */
    @PutMapping("/email")
    @ApiOperation("修改用户邮箱")
    public Result modifyEmail(@Validated @RequestBody ModifyEmailTO modifyEmailTO){
        Result result = sysUserService.modifyEmail(modifyEmailTO);
        return result;
    }

    /**
     * 修改用户密码
     * @param modifyPasswordTO
     * @return
     */
    @PutMapping("/password")
    @ApiOperation("修改用户密码")
    public Result modifyPassword(@Validated @RequestBody ModifyPasswordTO modifyPasswordTO){
        Result result = sysUserService.modifyPassword(modifyPasswordTO);
        return result;
    }

    /**
     * 修改锐信号
     * @param modifyLoginNameTO
     * @return
     */
    @PutMapping("/login_name")
    @ApiOperation("修改锐信号")
    public Result modifyLoginName(@Validated @RequestBody ModifyLoginNameTO modifyLoginNameTO){
        Result result = sysUserService.modifyLoginName(modifyLoginNameTO);
        return result;
    }

    /**
     * 获取认证方式
     * @param id
     * @return
     */
    @GetMapping("/{id}/validation")
    @ApiOperation("获取认证方式")
    public Result getValidationMethods(@PathVariable("id") @NotBlank(message = "用户ID不能为空") String id){
        Result result = sysUserService.getValidationMethods(id);
        return result;
    }


    /**
     * 添加用户密保问题
     * @param questionsTOList
     * @return
     */
    @PostMapping("/{id}/questions")
    @ApiOperation("添加用户密保问题")
    public Result addQuestions(@PathVariable("id") @NotBlank(message = "用户ID不能为空") String id, @RequestBody List<SysUserQuestionsTO> questionsTOList){
        Result result = sysUserService.addQuestions(id, questionsTOList);
        return result;
    }

    /**
     * 查询用户密保问题
     * @param id
     * @return
     */
    @GetMapping("/{id}/questions")
    @ApiOperation("查询用户密保问题")
    public Result queryQuestions(@PathVariable("id") @NotBlank(message = "用户ID不能为空") String id){
        Result result = sysUserService.queryQuestions(id);
        return result;
    }


    /**
     * 校验密保问题答案
     * @param questionsTOList
     * @return
     */
    @PostMapping("/{id}/questions/validity")
    @ApiOperation("校验密保问题答案")
    public Result checkQuestions(@PathVariable("id") @NotBlank(message = "用户ID不能为空") String id, @RequestBody List<SysUserQuestionsTO> questionsTOList){
        Result result = sysUserService.checkQuestions(id, questionsTOList);
        return result;
    }
}
