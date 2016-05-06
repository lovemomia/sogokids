package com.sogokids.service.course;

import com.sogokids.service.AbstractService;

public class SubjectService extends AbstractService {
    public Subject get(int subjectId) {
        String sql = "SELECT Id, CityId, Cover, Title, Intro, Rules FROM SG_Subject WHERE Id=? AND Status<>0";
        return queryObject(sql, new Object[] { subjectId }, Subject.class, Subject.NOT_EXIST_SUBJECT);
    }
}
