package com.sogokids.payment;

import java.math.BigDecimal;
import java.util.Date;

public interface CallbackParam {
    boolean isPayedSuccessfully();
    long getOrderId();
    int getPayType();
    String getPayer();
    Date getFinishTime();
    String getTradeNo();
    BigDecimal getTotalFee();
}
