package com.sogokids.payment.alipay;

import com.sogokids.common.config.Configuration;
import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.common.platform.Platform;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlipayUtil {
    public static boolean verifyResponse(String notifyId) throws IOException {
        String partner = Configuration.getString("Payment.Ali.Partner");
        String verifyUrl = Configuration.getString("Payment.Ali.VerifyUrl") + "partner=" + partner + "&notify_id=" + notifyId;

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(verifyUrl);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new SogoErrorException("fail to execute request: " + request);

        String entity = EntityUtils.toString(response.getEntity());

        return Boolean.valueOf(entity);
    }

    public static String sign(Map<String, String> params, int platform) {
        List<String> kvs = new ArrayList<String>();
        String quote = Platform.isApp(platform) ? "\"" : "";
        kvs.add(AlipayPrepayResult.Field.PARTNER + "=" + quote + params.get(AlipayPrepayResult.Field.PARTNER) + quote);
        kvs.add(AlipayPrepayResult.Field.SELLER_ID + "=" + quote + params.get(AlipayPrepayResult.Field.SELLER_ID) + quote);
        kvs.add(AlipayPrepayResult.Field.OUT_TRADE_NO + "=" + quote + params.get(AlipayPrepayResult.Field.OUT_TRADE_NO) + quote);
        kvs.add(AlipayPrepayResult.Field.SUBJECT + "=" + quote + params.get(AlipayPrepayResult.Field.SUBJECT) + quote);
        kvs.add(AlipayPrepayResult.Field.BODY + "=" + quote + params.get(AlipayPrepayResult.Field.BODY) + quote);
        kvs.add(AlipayPrepayResult.Field.TOTAL_FEE + "=" + quote + params.get(AlipayPrepayResult.Field.TOTAL_FEE) + quote);
        kvs.add(AlipayPrepayResult.Field.NOTIFY_URL + "=" + quote + params.get(AlipayPrepayResult.Field.NOTIFY_URL) + quote);
        kvs.add(AlipayPrepayResult.Field.SERVICE + "=" + quote + params.get(AlipayPrepayResult.Field.SERVICE) + quote);
        kvs.add(AlipayPrepayResult.Field.PAYMENT_TYPE + "=" + quote + params.get(AlipayPrepayResult.Field.PAYMENT_TYPE) + quote);
        kvs.add(AlipayPrepayResult.Field.INPUT_CHARSET + "=" + quote + params.get(AlipayPrepayResult.Field.INPUT_CHARSET) + quote);
        kvs.add(AlipayPrepayResult.Field.IT_B_PAY + "=" + quote + params.get(AlipayPrepayResult.Field.IT_B_PAY) + quote);
        kvs.add(AlipayPrepayResult.Field.SHOW_URL + "=" + quote + params.get(AlipayPrepayResult.Field.SHOW_URL) + quote);
        if (Platform.isWap(platform)) {
            kvs.add(AlipayPrepayResult.Field.RETURN_URL + "=" + quote + params.get(AlipayPrepayResult.Field.RETURN_URL) + quote);
            Collections.sort(kvs);
        }

        try {
            return URLEncoder.encode(RSA.sign(StringUtils.join(kvs, "&"), Configuration.getString("Payment.Ali.PrivateKey"), "utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new SogoErrorException("签名失败");
        }
    }

    public static boolean validateSign(Map<String, String> params, String sign) {
        List<String> kvs = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(AlipayPrepayResult.Field.SIGN_TYPE) || key.equalsIgnoreCase(AlipayPrepayResult.Field.SIGN) || StringUtils.isBlank(value)) continue;
            kvs.add(key + "=" + value);
        }
        Collections.sort(kvs);

        return RSA.verify(StringUtils.join(kvs, "&"), sign, Configuration.getString("Payment.Ali.PublicKey"), "utf-8");
    }
}
