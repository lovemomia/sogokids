package com.sogokids.web.ctrl;

import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.common.exception.SogoException;
import com.sogokids.common.exception.SogoLoginException;
import com.sogokids.web.resp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        return params;
    }

    @ExceptionHandler
    public Response exception(Exception exception) throws Exception {
        if (exception instanceof SogoErrorException) {
            return Response.FAILED(exception.getMessage());
        } else if (exception instanceof SogoLoginException) {
            return Response.TOKEN_EXPIRED;
        } else {
            if (!(exception instanceof SogoException)) LOGGER.error("exception!!", exception);
            throw exception;
        }
    }
}
