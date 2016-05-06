package com.sogokids.service.sms.impl;

import com.sogokids.config.Configuration;
import com.sogokids.service.sms.SmsSender;
import com.sogokids.util.MiscUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YiMeiSmsSender implements SmsSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(YiMeiSmsSender.class);

    @Override
    public boolean send(String mobile, String message) {
        try {
            LOGGER.info("trying to send message...");

            HttpPost httpPost = new HttpPost(Configuration.getString("Sms.Yimei.Service"));

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cdkey", Configuration.getString("Sms.Yimei.CDKey")));
            params.add(new BasicNameValuePair("password", Configuration.getString("Sms.Yimei.Password")));
            params.add(new BasicNameValuePair("phone", mobile));
            params.add(new BasicNameValuePair("message", "【松果亲子】" + message));
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOGGER.error("fail to send msg to user, {}/{}, http response code: {}", mobile, message, response.getStatusLine().getStatusCode());
                return false;
            }

            String responseEntity = EntityUtils.toString(response.getEntity());
            Map<String, String> responseXml = MiscUtil.xmlToMap(responseEntity);

            int error = Integer.valueOf(responseXml.get("error"));
            if (error == 0) return true;

            LOGGER.error("fail to send msg to user, {}/{}, error code is: {}", mobile, message, error);
        } catch (Exception e) {
            LOGGER.error("fail to send msg to user, {}/{}", mobile, message, e);
        }

        return false;
    }
}
