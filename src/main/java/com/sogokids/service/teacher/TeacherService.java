package com.sogokids.service.teacher;

import com.sogokids.service.AbstractService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TeacherService extends AbstractService {
    public List<Teacher> queryByCourse(int courseId) {
        String sql = "SELECT TeacherId FROM SG_CourseSku WHERE CourseId=? AND Status=1";
        return list(queryIntList(sql, new Object[] { courseId }));
    }

    private List<Teacher> list(Collection<Integer> teacherIds) {
        if (teacherIds.isEmpty()) return new ArrayList<Teacher>();

        String sql = "SELECT Id, Avatar, Name, Education, Experience FROM SG_Teacher WHERE Id IN (%s) AND Status=1";
        return listByIds(sql, teacherIds, Integer.class, Teacher.class);
    }
}
