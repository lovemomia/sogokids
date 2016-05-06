package com.sogokids.exception;

public abstract class SogoException extends RuntimeException {
    public SogoException() {}

    public SogoException(String msg) {
        super(msg);
    }

    public SogoException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
