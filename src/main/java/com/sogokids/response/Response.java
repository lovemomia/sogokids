package com.sogokids.response;

import java.util.Date;

public class Response {
    private static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int FAILED = 1;

        public static final int TOKEN_EXPIRED = 100001;

        public static final int BAD_REQUEST = 400;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    private static final String SUCCESS_MSG = "success";
    private static final String FAILED_MSG = "failed";

    public static final Response SUCCESS = new Response(SUCCESS_MSG);
    public static final Response FAILED = new Response(ErrorCode.FAILED, FAILED_MSG);

    public static final Response TOKEN_EXPIRED = new Response(ErrorCode.TOKEN_EXPIRED, "用户token过期，需要重新登录");

    public static final Response BAD_REQUEST = new Response(ErrorCode.BAD_REQUEST, "参数不正确");
    public static final Response FORBIDDEN = new Response(ErrorCode.FORBIDDEN, "禁止访问");
    public static final Response NOT_FOUND = new Response(ErrorCode.NOT_FOUND, "页面不存在");
    public static final Response METHOD_NOT_ALLOWED = new Response(ErrorCode.METHOD_NOT_ALLOWED, "无效的请求方法");
    public static final Response INTERNAL_SERVER_ERROR = new Response(ErrorCode.INTERNAL_SERVER_ERROR, "服务器内部错误");

    public static Response SUCCESS(Object data) {
        return new Response(data);
    }

    public static Response FAILED(String errmsg) {
        return new Response(ErrorCode.FAILED, errmsg);
    }

    private int errno = ErrorCode.FAILED;
    private String errmsg;
    private Object data;
    private long time = new Date().getTime();

    private Response() {}

    private Response(Object data) {
        this(ErrorCode.SUCCESS, SUCCESS_MSG);
        this.data = data;
    }

    private Response(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
