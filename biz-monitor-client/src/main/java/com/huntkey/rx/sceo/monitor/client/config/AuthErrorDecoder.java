package com.huntkey.rx.sceo.monitor.client.config;


import com.huntkey.rx.sceo.monitor.client.exception.AuthFeignException;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Created by zhaomj on 2017/4/25.
 */
public class AuthErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        return new AuthFeignException(response);
    }
}
