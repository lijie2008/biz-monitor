package com.huntkey.rx.sceo.monitor.client.feign.hystrix;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.feign.SysUserService;
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
@Component
public class SysUserServiceFallback implements SysUserService{

    @Override
    public Result register(RegisterRequestTO registerRequestTO) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result resetPassword(ResetPasswordTO resetPasswordTO) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result modifyMobile(ModifyMobileTO modifyMobileTO) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result modifyEmail(ModifyEmailTO modifyEmailTO) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result modifyPassword(ModifyPasswordTO modifyPasswordTO) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result modifyLoginName(ModifyLoginNameTO modifyLoginNameTO) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result getValidationMethods(String id) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result addQuestions(String id, List<SysUserQuestionsTO> questionsTOList) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result queryQuestions(String id) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }

    @Override
    public Result checkQuestions(String id, List<SysUserQuestionsTO> questionsTOList) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("fallback");
        return result;
    }
}
