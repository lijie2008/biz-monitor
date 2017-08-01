package com.huntkey.rx.sceo.monitor.client.exception;

import feign.Response;

/**
 * Created by zhaomj on 2017/4/25.
 */
@SuppressWarnings("serial")
public class AuthFeignException extends RuntimeException {

    private Response response;

    public AuthFeignException(Response response){
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
