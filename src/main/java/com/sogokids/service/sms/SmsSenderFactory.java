package com.sogokids.service.sms;

import com.sogokids.exception.SogoErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SmsSenderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderFactory.class);

    private Map<String, SmsSender> prototypes;

    public void setPrototypes(Map<String, SmsSender> prototypes) {
        this.prototypes = prototypes;
    }

    public SmsSender getSmsSender(String name) {
        if (prototypes.containsKey(name)) return prototypes.get(name);

        LOGGER.error("invalid sms sender: {}", name);
        throw new SogoErrorException("invalid sms sender: " + name);
    }
}
