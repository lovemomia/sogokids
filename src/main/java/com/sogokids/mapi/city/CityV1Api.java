package com.sogokids.mapi.city;

import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.poi.City;
import com.sogokids.service.poi.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/city")
public class CityV1Api extends AbstractApi {
    @Autowired private PoiService poiService;

    @RequestMapping(method = RequestMethod.GET)
    public List<City> listAll() {
        return poiService.listAllCities();
    }
}

