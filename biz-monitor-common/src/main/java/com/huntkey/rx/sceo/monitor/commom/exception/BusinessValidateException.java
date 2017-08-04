package com.huntkey.rx.sceo.monitor.commom.exception;

/**
 * Created by xuyf on 2017/5/24 0024.
 */
public class BusinessValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessValidateException(){
        super();
    }

    public BusinessValidateException(String msg) {
        super(msg);
    }
}
