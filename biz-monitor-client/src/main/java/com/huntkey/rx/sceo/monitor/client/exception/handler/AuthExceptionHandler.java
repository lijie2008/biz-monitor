package com.huntkey.rx.sceo.monitor.client.exception.handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.huntkey.rx.sceo.monitor.client.exception.AuthFeignException;
import com.huntkey.rx.sceo.monitor.commom.model.LoginResponseTO;


/**
 * Created by zhaomj on 2017/4/25.
 */
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler
    public LoginResponseTO authFialHandler(AuthFeignException e, HttpServletResponse response){
        response.setStatus(e.getResponse().status());
        return new LoginResponseTO();
    }
}
