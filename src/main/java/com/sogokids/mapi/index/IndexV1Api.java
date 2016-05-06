package com.sogokids.mapi.index;

import com.sogokids.mapi.AbstractApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/index")
public class IndexV1Api extends AbstractApi {
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam(value = "city", required = false, defaultValue = "1") int cityId) {
        return SUCCESS;
    }
}
