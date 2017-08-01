package com.huntkey.rx.sceo.monitor.provider.controller.client;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.common.service.common.model.to.SimpleSmsTO;
import org.springframework.stereotype.Component;

/**
 * Created by zhaomj on 2017/5/2.
 */
@Component
public class SmsClientFallback implements SmsClient {
    @Override
    public Result sendSms(SimpleSmsTO to) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("sms client fallback");
        return result;
    }
}
