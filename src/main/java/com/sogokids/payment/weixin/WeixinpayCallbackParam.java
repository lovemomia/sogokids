package com.sogokids.payment.weixin;

import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.common.platform.Platform;
import com.sogokids.payment.CallbackParam;
import com.sogokids.payment.MapWrapper;
import com.sogokids.payment.PayType;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WeixinpayCallbackParam extends MapWrapper implements CallbackParam {
    public static class Field {
        public static final String TRADE_TYPE = "trade_type";
        public static final String RETURN_CODE = "return_code";
        public static final String SIGN = "sign"; //签名
        public static final String RESULT_CODE = "result_code"; //返回结果编码
        public static final String OPEN_ID = "openid"; //用户标识
        public static final String TOTAL_FEE = "total_fee"; //总金额
        public static final String TRANSACTION_ID = "transaction_id"; //微信支付订单号
        public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
        public static final String TIME_END = "time_end"; //支付完成时间
    }

    private static final String DATE_FORMAT_STR = "yyyyMMddHHmmss";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);
    private static final String SUCCESS = "SUCCESS";

    public WeixinpayCallbackParam(Map<String, String> params) {
        addAll(params);
    }

    @Override
    public boolean isPayedSuccessfully() {
        try {
            String return_code = get(Field.RETURN_CODE);
            String result_code = get(Field.RESULT_CODE);

            if (return_code == null || !return_code.equalsIgnoreCase(SUCCESS) ||
                    result_code == null || !result_code.equalsIgnoreCase(SUCCESS)) return false;

            return validateCallbackSign();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateCallbackSign() {
        String tradeType = get(Field.TRADE_TYPE);
        int platform;
        if ("APP".equalsIgnoreCase(tradeType)) platform = Platform.APP;
        else if ("JSAPI".equalsIgnoreCase(tradeType)) platform = Platform.WAP;
        else throw new SogoErrorException("invalid trade type: " + tradeType);

        return WeixinpayUtil.validateSign(getAll(), platform);
    }

    @Override
    public long getOrderId() {
        String outTradeNo = get(Field.OUT_TRADE_NO);
        return Long.valueOf(outTradeNo.substring(3, outTradeNo.length() - DATE_FORMAT_STR.length()));
    }

    @Override
    public int getPayType() {
        String tradeType = get(Field.TRADE_TYPE);
        if ("APP".equalsIgnoreCase(tradeType)) return PayType.WEIXIN_APP;
        else if ("JSAPI".equalsIgnoreCase(tradeType)) return PayType.WEIXIN_JSAPI;
        else return PayType.WEIXIN;
    }

    @Override
    public String getPayer() {
        return get(Field.OPEN_ID);
    }

    @Override
    public Date getFinishTime() {
        Date finishTime;
        try {
            finishTime = DATE_FORMATTER.parse(get(Field.TIME_END));
        } catch (ParseException e) {
            finishTime = new Date();
        }

        return finishTime;
    }

    @Override
    public String getTradeNo() {
        return get(Field.TRANSACTION_ID);
    }

    @Override
    public BigDecimal getTotalFee() {
        return new BigDecimal(get(Field.TOTAL_FEE)).divide(new BigDecimal(100));
    }
}
