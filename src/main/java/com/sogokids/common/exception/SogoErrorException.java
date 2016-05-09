package com.sogokids.common.exception;

public class SogoErrorException extends SogoException {
    public SogoErrorException(String msg) {
        super(msg);
    }

    public SogoErrorException(String msg, Throwable t) {
        super(msg, t);
    }
}
