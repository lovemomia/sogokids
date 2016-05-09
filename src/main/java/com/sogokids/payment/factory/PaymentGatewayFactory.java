package com.sogokids.payment.factory;

import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.payment.PaymentGateway;

import java.util.Map;

public class PaymentGatewayFactory {
    private static Map<Integer, PaymentGateway> prototypes;

    public void setPrototypes(Map<Integer, PaymentGateway> prototypes) {
        PaymentGatewayFactory.prototypes = prototypes;
    }

    public static PaymentGateway create(int payType) {
        PaymentGateway paymentGateway = prototypes.get(payType);
        if (paymentGateway == null) throw new SogoErrorException("无效的支付类型: " + payType);

        return paymentGateway;
    }
}
