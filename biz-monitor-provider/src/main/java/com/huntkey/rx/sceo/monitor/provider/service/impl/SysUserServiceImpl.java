package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.commons.utils.uuid.UuidCreater;
import com.huntkey.rx.sceo.monitor.commom.contants.MessageConstants;
import com.huntkey.rx.sceo.monitor.commom.contants.SecurityCenterConstants;
import com.huntkey.rx.sceo.monitor.commom.enums.SysUserSecurityType;
import com.huntkey.rx.sceo.monitor.commom.exception.BusinessValidateException;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyEmailTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyLoginNameTO;
import com.huntkey.rx.sceo.monitor.commom.model.ModifyMobileTO;
import com.huntkey.rx.sceo.monitor.commom.model.RegisterRequestTO;
import com.huntkey.rx.sceo.monitor.commom.model.ResetPasswordTO;
import com.huntkey.rx.sceo.monitor.commom.model.SysUser;
import com.huntkey.rx.sceo.monitor.commom.model.SysUserExample;
import com.huntkey.rx.sceo.monitor.commom.model.SysUserSecurity;
import com.huntkey.rx.sceo.monitor.commom.model.ValidationTO;
import com.huntkey.rx.sceo.monitor.commom.utils.BCryptUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.MaskUtil;
import com.huntkey.rx.sceo.monitor.provider.dao.SysUserMapper;
import com.huntkey.rx.sceo.monitor.provider.dao.SysUserSecurityMapper;
import com.huntkey.rx.sceo.monitor.provider.service.SysUserService;

//import javax.validation.Validator;

/**
 * Created by xuyf on 2017/4/27 0027.
 */
@Service
@Transactional(readOnly = true)
public class SysUserServiceImpl implements SysUserService {

    private static Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysUserSecurityMapper sysUserSecurityMapper;

    @Autowired
    MessageSource messageSource;

    @Override
    public SysUser getSysUserById(String id) {
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(id);
        if (StringUtil.isNullOrEmpty(sysUser)) {
            throw new BusinessValidateException(messageSource.getMessage(MessageConstants.ERR_VALID_ACCOUNT,
                    null, LocaleContextHolder.getLocale()));
        }
        return sysUser;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public String registerSysUser(RegisterRequestTO registerRequestTO) {
        String result = null;

        //生成用户ID
        String sysUserId = UuidCreater.uuid();
        //密码加密
        String hashPwd = BCryptUtil.encode(registerRequestTO.getPassword());
        Date now = new Date();

        SysUser sysUser = new SysUser();
        sysUser.setId(sysUserId);
        sysUser.setPassword(hashPwd);
        sysUser.setState("0");
        sysUser.setNameRealFlag(false);
        sysUser.setDeleteFlag(false);
        sysUser.setQuestionFlag(false);
        sysUser.setRegisterTime(now);

        SysUserSecurity sysUserSecurity = new SysUserSecurity();
        sysUserSecurity.setId(UuidCreater.uuid());
        sysUserSecurity.setUserId(sysUserId);
        sysUserSecurity.setResult(true);
        sysUserSecurity.setNewValue(registerRequestTO.getAccount());
        sysUserSecurity.setDeleteFlag(false);
        sysUserSecurity.setCreateTime(now);

        Integer securityType = null;
        //判断用户注册类型，并记录账户安全类型及认证通过的值
        if (registerRequestTO.getRegisterType() == SecurityCenterConstants.COMMON_TYPE_MOBILE) {
            sysUser.setMobileNumber(registerRequestTO.getAccount());
            sysUser.setWorldPost(registerRequestTO.getWorldPost());
            securityType = Integer.parseInt(SysUserSecurityType.MODIFY_MOBILE_NUMBER.toString());
        } else if (registerRequestTO.getRegisterType() == SecurityCenterConstants.COMMON_TYPE_EMAIL) {
            sysUser.setEmail(registerRequestTO.getAccount());
            sysUser.setNationality(registerRequestTO.getNationality());
            securityType = Integer.parseInt(SysUserSecurityType.MODIFY_EMAIL.toString());
        } else {
            return result;
        }
        sysUserSecurity.setType(securityType);

        int securityRowNum = sysUserSecurityMapper.insert(sysUserSecurity);
        if (securityRowNum == 1) {
            int userRowNum = sysUserMapper.insert(sysUser);
            if (userRowNum == 1) {
                log.info("register sys_user success, user_id:" + sysUserId);
                result = sysUserId;
            }
        }
        if (result == null) {
            log.error("register sys_user fail");
            throw new RuntimeException("register sys_user fail");
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public boolean resetSysUserPassword(ResetPasswordTO resetPasswordTO) {
        SysUser sysUser = getSysUserById(resetPasswordTO.getId());
        boolean result = false;

        String hashPwd = BCryptUtil.encode(resetPasswordTO.getNewPassword());
        Integer securityType = Integer.parseInt(SysUserSecurityType.MODIFY_PASSWORD.toString());
        SysUserSecurity sysUserSecurity = new SysUserSecurity();
        sysUserSecurity.setId(UuidCreater.uuid());
        sysUserSecurity.setUserId(resetPasswordTO.getId());
        sysUserSecurity.setType(securityType);
        sysUserSecurity.setResult(true);
        sysUserSecurity.setOldValue(sysUser.getPassword());
        sysUserSecurity.setNewValue(hashPwd);
        sysUserSecurity.setDeleteFlag(false);
        sysUserSecurity.setCreateTime(new Date());

        sysUserSecurityMapper.updateDeleteFlagByUserIdAndType(true, sysUser.getId(), securityType);
        int securityRowNum = sysUserSecurityMapper.insert(sysUserSecurity);
        if (securityRowNum == 1) {
            SysUser querySysUser = new SysUser();
            querySysUser.setId(resetPasswordTO.getId());
            querySysUser.setPassword(hashPwd);
            int userRowNum = sysUserMapper.updateByPrimaryKeySelective(querySysUser);
            if (userRowNum == 1) {
                log.info("modify sys_user password success, user_id:" + resetPasswordTO.getId());
                result = true;
            }
        }
        if (!result) {
            log.error("modify sys_user password fail, user_id:" + resetPasswordTO.getId());
            throw new RuntimeException("modify sys_user password fail, user_id:" + resetPasswordTO.getId());
        }
        return result;
    }

    @Override
    public boolean checkSysUserOldPassword(String id, String oldPassword) {
        SysUser sysUser = getSysUserById(id);
        boolean result = false;
        //判断原密码是否正确
        if (BCryptUtil.matches(oldPassword, sysUser.getPassword())) {
            result = true;
        }

        return result;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public boolean modifySysUserLoginName(ModifyLoginNameTO modifyLoginNameTO) {
        SysUser sysUser = getSysUserById(modifyLoginNameTO.getId());
        if (!StringUtil.isNullOrEmpty(sysUser.getLoginName())) {
            throw new BusinessValidateException("锐信号只能设置一次");
        }
        boolean result = false;

        Integer securityType = Integer.parseInt(SysUserSecurityType.MODIFY_LOGIN_NAME.toString());
        SysUserSecurity sysUserSecurity = new SysUserSecurity();
        sysUserSecurity.setId(UuidCreater.uuid());
        sysUserSecurity.setUserId(modifyLoginNameTO.getId());
        sysUserSecurity.setType(securityType);
        sysUserSecurity.setResult(true);
        sysUserSecurity.setOldValue(sysUser.getLoginName());
        sysUserSecurity.setNewValue(modifyLoginNameTO.getNewLoginName());
        sysUserSecurity.setDeleteFlag(false);
        sysUserSecurity.setCreateTime(new Date());

        sysUserSecurityMapper.updateDeleteFlagByUserIdAndType(true, sysUser.getId(), securityType);
        int securityRowNum = sysUserSecurityMapper.insert(sysUserSecurity);
        if (securityRowNum == 1) {
            SysUser querySysUser = new SysUser();
            querySysUser.setId(modifyLoginNameTO.getId());
            querySysUser.setLoginName(modifyLoginNameTO.getNewLoginName());
            int userRowNum = sysUserMapper.updateByPrimaryKeySelective(querySysUser);
            if (userRowNum == 1) {
                log.info("modify sys_user login_name success, user_id:" + modifyLoginNameTO.getId());
                result = true;
            }
        }
        if (!result) {
            log.error("modify sys_user login_name fail, user_id:" + modifyLoginNameTO.getId());
            throw new RuntimeException("modify sys_user login_name fail, user_id:" + modifyLoginNameTO.getId());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public boolean modifySysUserMobileNumber(ModifyMobileTO modifyMobileTO) {
        SysUser sysUser = getSysUserById(modifyMobileTO.getId());
        boolean result = false;

        Integer securityType = Integer.parseInt(SysUserSecurityType.MODIFY_MOBILE_NUMBER.toString());
        SysUserSecurity sysUserSecurity = new SysUserSecurity();
        sysUserSecurity.setId(UuidCreater.uuid());
        sysUserSecurity.setUserId(modifyMobileTO.getId());
        sysUserSecurity.setType(securityType);
        sysUserSecurity.setResult(true);
        sysUserSecurity.setOldValue(sysUser.getMobileNumber());
        sysUserSecurity.setNewValue(modifyMobileTO.getNewMobileNumber());
        sysUserSecurity.setDeleteFlag(false);
        sysUserSecurity.setCreateTime(new Date());

        sysUserSecurityMapper.updateDeleteFlagByUserIdAndType(true, sysUser.getId(), securityType);
        int securityRowNum = sysUserSecurityMapper.insert(sysUserSecurity);
        if (securityRowNum == 1) {
            SysUser querySysUser = new SysUser();
            querySysUser.setId(modifyMobileTO.getId());
            querySysUser.setMobileNumber(modifyMobileTO.getNewMobileNumber());
            querySysUser.setWorldPost(modifyMobileTO.getWorldPost());
            int userRowNum = sysUserMapper.updateByPrimaryKeySelective(querySysUser);
            if (userRowNum == 1) {
                log.info("modify sys_user mobile_number success, user_id:" + modifyMobileTO.getId());
                result = true;
            }
        }
        if (!result) {
            log.error("modify sys_user mobile_number fail, user_id:" + modifyMobileTO.getId());
            throw new RuntimeException("modify sys_user mobile_number fail, user_id:" + modifyMobileTO.getId());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public boolean modifySysUserEmail(ModifyEmailTO modifyEmailTO) {
        SysUser sysUser = getSysUserById(modifyEmailTO.getId());
        boolean result = false;

        Integer securityType = Integer.parseInt(SysUserSecurityType.MODIFY_EMAIL.toString());
        SysUserSecurity sysUserSecurity = new SysUserSecurity();
        sysUserSecurity.setId(UuidCreater.uuid());
        sysUserSecurity.setUserId(modifyEmailTO.getId());
        sysUserSecurity.setType(securityType);
        sysUserSecurity.setResult(true);
        sysUserSecurity.setOldValue(sysUser.getEmail());
        sysUserSecurity.setNewValue(modifyEmailTO.getNewEmail());
        sysUserSecurity.setDeleteFlag(false);
        sysUserSecurity.setCreateTime(new Date());

        sysUserSecurityMapper.updateDeleteFlagByUserIdAndType(true, sysUser.getId(), securityType);
        int securityRowNum = sysUserSecurityMapper.insert(sysUserSecurity);
        if (securityRowNum == 1) {
            SysUser querySysUser = new SysUser();
            querySysUser.setId(modifyEmailTO.getId());
            querySysUser.setEmail(modifyEmailTO.getNewEmail());
            querySysUser.setNationality(modifyEmailTO.getNationality());
            int userRowNum = sysUserMapper.updateByPrimaryKeySelective(querySysUser);
            if (userRowNum == 1) {
                log.info("modify sys_user email success, user_id:" + modifyEmailTO.getId());
                result = true;
            }
        }
        if (!result) {
            log.error("modify sys_user email fail, user_id:" + modifyEmailTO.getId());
            throw new RuntimeException("modify sys_user email fail, user_id:" + modifyEmailTO.getId());
        }
        return result;
    }

    @Override
    public ValidationTO getValidation(String id) {
        SysUser sysUser = getSysUserById(id);
        ValidationTO validationTO = new ValidationTO();
        validationTO.setMobileNumber(MaskUtil.addMaskForMobileNumber(sysUser.getMobileNumber()));
        validationTO.setEmail(MaskUtil.addMaskForEmail(sysUser.getEmail()));
        validationTO.setWorldPost(sysUser.getWorldPost());
        validationTO.setNationality(sysUser.getNationality());
        validationTO.setQuestionFlag(sysUser.getQuestionFlag());
        return validationTO;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deleteSysUserById(String id) {
        getSysUserById(id);
        SysUser querySysUser = new SysUser();
        querySysUser.setId(id);
        querySysUser.setDeleteFlag(true);
        int userRowNum = sysUserMapper.updateByPrimaryKeySelective(querySysUser);
        return userRowNum > 0;
    }

    @Override
    @Transactional(readOnly = false)
    public SysUser selectSysUserByAccount(String account) {
        List<SysUser> list = sysUserMapper.selectSysUserByAccount(account);
        SysUser user = null;
        if (list.size() == 1) {
            user = list.get(0);
        }
        return user;
    }

    @Override
    public SysUser selectSysUserByIdWithOutPass(String id) {
        SysUserExample example = new SysUserExample();
        example.createCriteria().andDeleteFlagEqualTo(false).andStateEqualTo("0").andIdEqualTo(id);
        List<SysUser> users = sysUserMapper.selectByExample(example);
        if (!users.isEmpty() && users.size() > 0) {
            SysUser user = users.get(0);
            user.setMobileNumber(MaskUtil.addMaskForMobileNumber(user.getMobileNumber()));
            user.setEmail(MaskUtil.addMaskForEmail(user.getEmail()));
            user.setPassword("");
            return user;
        } else {
            throw new BusinessValidateException(messageSource.getMessage(MessageConstants.ERR_VALID_ACCOUNT,
                    null, LocaleContextHolder.getLocale()));
        }
    }
}
