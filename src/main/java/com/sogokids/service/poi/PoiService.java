package com.sogokids.service.poi;

import com.sogokids.service.AbstractService;

import java.util.List;

public class PoiService extends AbstractService {
    public List<City> listAllCities() {
        String sql = "SELECT Id, Name FROM SG_City WHERE Status=1";
        return queryObjectList(sql, City.class);
    }
}
