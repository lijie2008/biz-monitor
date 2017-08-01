package com.huntkey.rx.sceo.monitor.provider.service;

import com.huntkey.rx.sceo.monitor.commom.model.ModifyEmailTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyLoginNameTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyMobileTO;
import com.huntkey.rx.sceo.monitor.commom.model.RegisterRequestTO;
import com.huntkey.rx.sceo.monitor.commom.model.ResetPasswordTO;
import com.huntkey.rx.sceo.monitor.commom.model.SysUser;
import com.huntkey.rx.sceo.monitor.commom.model.ValidationTO;

/**
 * Created by xuyf on 2017/4/27 0027.
 */
public interface SysUserService {

    /**
     * 根据ID返回系统用户信息
     * @param id
     * @return
     */
    SysUser getSysUserById(String id);

    /**
     *注册系统用户
     * @param registerRequestTO
     * @return
     */
    String registerSysUser(RegisterRequestTO registerRequestTO);

    /**
     * 重置系统用户密码
     * @param resetPasswordTO
     * @return
     */
    boolean resetSysUserPassword(ResetPasswordTO resetPasswordTO);

    /**
     * 校验系统用户原始密码
     * @param id
     * @param oldPassword
     * @return
     */
    boolean checkSysUserOldPassword(String id, String oldPassword);

    /**
     * 修改系统用户锐信号
     * @param modifyLoginNameTO
     * @return
     */
    boolean modifySysUserLoginName(ModifyLoginNameTO modifyLoginNameTO);

    /**
     * 修改系统用户手机号
     * @param modifyMobileTO
     * @return
     */
    boolean modifySysUserMobileNumber(ModifyMobileTO modifyMobileTO);

    /**
     * 修改系统用户邮箱
     * @param modifyEmailTO
     * @return
     */
    boolean modifySysUserEmail(ModifyEmailTO modifyEmailTO);

    /**
     * 获取用户认证方式
     * @param id
     * @return
     */
    ValidationTO getValidation(String id);

    /**
     * 根据ID删除用户
     * @param id
     * @return
     */
    boolean deleteSysUserById(String id);

    /**
     * 根据账号查询用户信息，匹配锐信号、手机、邮箱
     * @param account
     * @return
     */
    SysUser selectSysUserByAccount(String account);

    /**
     * 根据用户ID查询用户信息，匹配锐信号、手机、邮箱,不包含密码
     * @param id
     * @return
     */
    SysUser selectSysUserByIdWithOutPass(String id);

}