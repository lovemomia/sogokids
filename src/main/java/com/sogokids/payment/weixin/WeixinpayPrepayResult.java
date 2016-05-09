package com.sogokids.payment.weixin;

import com.sogokids.common.platform.Platform;
import com.sogokids.payment.PrepayResult;

public class WeixinpayPrepayResult extends PrepayResult {
    public static WeixinpayPrepayResult create(int platform) {
        return Platform.isApp(platform) ? new WeixinpayPrepayResult.App() : new WeixinpayPrepayResult.JsApi();
    }

    public static class App extends WeixinpayPrepayResult {
        public static class Field {
            public static final String APPID = "appid";
            public static final String PARTNERID = "partnerid";
            public static final String PREPAYID = "prepayid";
            public static final String PACKAGE = "package";
            public static final String NONCE_STR = "noncestr";
            public static final String TIMESTAMP = "timestamp";
            public static final String SIGN = "sign";
        }

        public String getAppid() {
            return get(Field.APPID);
        }

        public String getPartnerid() {
            return get(Field.PARTNERID);
        }

        public String getPrepayid() {
            return get(Field.PREPAYID);
        }

        public String getPackage_app() {
            return get(Field.PACKAGE);
        }

        public String getNoncestr() {
            return get(Field.NONCE_STR);
        }

        public String getTimestamp() {
            return get(Field.TIMESTAMP);
        }

        public String getSign() {
            return get(Field.SIGN);
        }
    }

    public static class JsApi extends WeixinpayPrepayResult {
        public static class Field {
            public static final String APPID = "appId";
            public static final String PACKAGE = "package";
            public static final String NONCE_STR = "nonceStr";
            public static final String TIMESTAMP = "timeStamp";
            public static final String SIGN_TYPE = "signType";
            public static final String PAY_SIGN = "paySign";
        }

        public String getAppId() {
            return get(Field.APPID);
        }

        public String getPrepayId() {
            return get(Field.PACKAGE);
        }

        public String getNonceStr() {
            return get(Field.NONCE_STR);
        }

        public String getTimeStamp() {
            return get(Field.TIMESTAMP);
        }

        public String getSignType() {
            return get(Field.SIGN_TYPE);
        }

        public String getPaySign() {
            return get(Field.PAY_SIGN);
        }
    }
}
