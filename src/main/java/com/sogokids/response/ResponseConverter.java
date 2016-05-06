package com.sogokids.response;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class ResponseConverter extends FastJsonHttpMessageConverter {
    public ResponseConverter() {
        super();
        this.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        this.setFeatures(SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (obj instanceof Response) super.writeInternal(obj, outputMessage);
        else super.writeInternal(Response.SUCCESS(obj), outputMessage);
    }
}
