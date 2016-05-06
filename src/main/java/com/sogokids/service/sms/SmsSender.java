package com.sogokids.service.sms;

public interface SmsSender {
    boolean send(String mobile, String message);
}
