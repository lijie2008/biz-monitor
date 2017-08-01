package com.huntkey.rx.sceo.monitor.client.feign;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.feign.hystrix.SysUserServiceFallback;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyEmailTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyLoginNameTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyMobileTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyPasswordTO;
import com.huntkey.rx.sceo.monitor.commom.model.RegisterRequestTO;
import com.huntkey.rx.sceo.monitor.commom.model.ResetPasswordTO;
import com.huntkey.rx.sceo.monitor.commom.model.SysUserQuestionsTO;

/**
 * Created by xuyf on 2017/5/5 0005.
 */
@FeignClient(value = "security-center-provider", fallback = SysUserServiceFallback.class)
public interface SysUserService {

    /**
     * 用户注册
     * @param registerRequestTO
     * @return
     */
    @RequestMapping(value = "/sys_user", method = RequestMethod.POST)
    Result register(@RequestBody RegisterRequestTO registerRequestTO);

    /**
     * 重置密码
     * @param resetPasswordTO
     * @return
     */
    @RequestMapping(value = "/sys_user/password/reset", method = RequestMethod.PUT)
    Result resetPassword(@RequestBody ResetPasswordTO resetPasswordTO);

    /**
     * 修改用户手机号
     * @param modifyMobileTO
     * @return
     */
    @RequestMapping(value = "/sys_user/mobile", method = RequestMethod.PUT)
    Result modifyMobile(@RequestBody ModifyMobileTO modifyMobileTO);

    /**
     * 修改用户邮箱
     * @param modifyEmailTO
     * @return
     */
    @RequestMapping(value = "/sys_user/email", method = RequestMethod.PUT)
    Result modifyEmail(@RequestBody ModifyEmailTO modifyEmailTO);

    /**
     * 修改用户密码
     * @param modifyPasswordTO
     * @return
     */
    @RequestMapping(value = "/sys_user/password", method = RequestMethod.PUT)
    Result modifyPassword(@RequestBody ModifyPasswordTO modifyPasswordTO);

    /**
     * 修改锐信号
     * @param modifyLoginNameTO
     * @return
     */
    @RequestMapping(value = "/sys_user/login_name", method = RequestMethod.PUT)
    Result modifyLoginName(@RequestBody ModifyLoginNameTO modifyLoginNameTO);

    /**
     * 获取认证方式
     * @param id
     * @return
     */
    @RequestMapping(value = "/sys_user/{id}/validation", method = RequestMethod.GET)
    Result getValidationMethods(@PathVariable("id") String id);


    /**
     * 添加用户密保问题
     * @param questionsTOList
     * @return
     */
    @RequestMapping(value = "/sys_user/{id}/questions", method = RequestMethod.POST)
    Result addQuestions(@PathVariable("id") String id, @RequestBody List<SysUserQuestionsTO> questionsTOList);

    /**
     * 查询用户密保问题
     * @param id
     * @return
     */
    @RequestMapping(value = "/sys_user/{id}/questions", method = RequestMethod.GET)
    Result queryQuestions(@PathVariable("id") String id);


    /**
     * 校验密保问题答案
     * @param questionsTOList
     * @return
     */
    @RequestMapping(value = "/sys_user/{id}/questions/validity", method = RequestMethod.POST)
    Result checkQuestions(@PathVariable("id") String id, @RequestBody List<SysUserQuestionsTO> questionsTOList);

}
