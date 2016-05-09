package com.sogokids.mapi.course;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.course.Course;
import com.sogokids.service.course.CourseService;
import com.sogokids.service.course.CourseSku;
import com.sogokids.service.course.Subject;
import com.sogokids.service.course.SubjectService;
import com.sogokids.service.teacher.Teacher;
import com.sogokids.service.teacher.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/course")
public class CourseV1Api extends AbstractApi {
    @Autowired private SubjectService subjectService;
    @Autowired private CourseService courseService;
    @Autowired private TeacherService teacherService;

    @RequestMapping(method = RequestMethod.GET)
    public JSON get(@RequestParam(value = "id") int courseId) {
        if (courseId <= 0) throw new SogoErrorException("无效的课程ID");

        Course course = courseService.get(courseId);
        if (!course.exists()) throw new SogoErrorException("课程不存在");
        postProcessCourse(course);

        List<CourseSku> skus = courseService.querySkus(courseId);

        Subject subject = subjectService.get(course.getSubjectId());
        if (!subject.exists()) throw new SogoErrorException("无效的课程");

        List<Teacher> teachers = teacherService.queryByCourse(courseId);
        postProcessTeachers(teachers);

        JSONObject result = new JSONObject();
        result.put("course", course);
        if (!skus.isEmpty()) result.put("sku", skus.get(0));
        result.put("rules", subject.getRules());
        if (!teachers.isEmpty()) result.put("teachers", teachers);

        return result;
    }

    @RequestMapping(value = "/sku/bookable", method = RequestMethod.GET)
    public List<CourseSku> listBookableSkus(@RequestParam(value = "id") int courseId) {
        if (courseId <= 0) throw new SogoErrorException("无效的课程ID");

        List<CourseSku> skus = courseService.querySkus(courseId);
        List<CourseSku> bookableSkus = new ArrayList<CourseSku>();
        for (CourseSku sku : skus) {
            if (sku.getStock() > 0) bookableSkus.add(sku);
        }

        return bookableSkus;
    }
}
