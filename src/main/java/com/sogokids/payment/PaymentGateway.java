package com.sogokids.payment;

import com.google.common.base.Function;

public abstract class PaymentGateway {
    public abstract PrepayResult prepay(PrepayParam param);

    public CallbackResult callback(CallbackParam param, Function<CallbackParam, Boolean> callback) {
        return callback.apply(param) ? CallbackResult.SUCCESS : CallbackResult.FAILED;
    }
}