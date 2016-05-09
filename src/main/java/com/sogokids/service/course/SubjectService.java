package com.sogokids.service.course;

import com.alibaba.fastjson.JSON;
import com.sogokids.service.AbstractService;
import com.sogokids.service.payment.Price;

import java.util.List;

public class SubjectService extends AbstractService {
    public Subject get(int subjectId) {
        String sql = "SELECT Id, CityId, Cover, Title, Intro, Rules FROM SG_Subject WHERE Id=? AND Status<>0";
        return queryObject(sql, new Object[] { subjectId }, Subject.class, Subject.NOT_EXIST_SUBJECT);
    }

    public long placeOrder(long userId, int type, String name, String mobile, List<Price> prices) {
        return 0;
    }

    public SubjectOrder getSubjectOrder(long orderId) {
        return null;
    }
}
