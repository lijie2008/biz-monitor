package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.common.service.common.model.to.SimpleSmsTO;

/**
 * Created by zhaomj on 2017/4/28.
 */
@FeignClient(value = "commonService-provider", fallback = SmsClientFallback.class)
public interface SmsClient {
    @RequestMapping(value = "/sms", method = RequestMethod.POST)
    Result sendSms(@RequestBody SimpleSmsTO to);
}
