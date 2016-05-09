package com.sogokids.payment.factory;


import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.payment.CallbackParam;
import com.sogokids.payment.PayType;
import com.sogokids.payment.alipay.AlipayCallbackParam;
import com.sogokids.payment.weixin.WeixinpayCallbackParam;

import java.util.Map;

public class CallbackParamFactory {
    public static CallbackParam create(Map<String, String> params, int payType) {
        switch (payType) {
            case PayType.ALIPAY: return new AlipayCallbackParam(params);
            case PayType.WEIXIN: return new WeixinpayCallbackParam(params);
            default: throw new SogoErrorException("无效的支付类型: " + payType);
        }
    }
}
