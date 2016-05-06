package com.sogokids.mapi.course;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sogokids.exception.SogoErrorException;
import com.sogokids.exception.SogoLoginException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.course.Course;
import com.sogokids.service.course.CourseService;
import com.sogokids.service.course.Subject;
import com.sogokids.service.course.SubjectService;
import com.sogokids.service.payment.Price;
import com.sogokids.service.payment.PriceService;
import com.sogokids.service.user.User;
import com.sogokids.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/subject")
public class SubjectV1Api extends AbstractApi {
    @Autowired private SubjectService subjectService;
    @Autowired private CourseService courseService;
    @Autowired private PriceService priceService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public JSON get(@RequestParam(value = "id") int subjectId) {
        if (subjectId <= 0) throw new SogoErrorException("无效的课程体系ID");

        Subject subject = subjectService.get(subjectId);
        if (!subject.exists()) throw new SogoErrorException("课程体系不存在");
        subject = postProcessSubject(subject);

        List<Course> courses = courseService.queryBySubject(subjectId);
        for (Course course : courses) {
            postProcessCourse(course);
        }

        JSONObject result = new JSONObject();
        result.put("subject", subject);
        result.put("courses", courses);

        return result;
    }

    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public List<Course> listCourses(@RequestParam(value = "id") int subjectId) {
        if (subjectId <= 0) throw new SogoErrorException("无效的课程体系ID");
        return postProcessCourses(courseService.queryBySubject(subjectId));
    }

    @RequestMapping(value = "/placeorder", method = RequestMethod.GET)
    public JSON placeOrder(@RequestParam String utoken, @RequestParam(value = "id") int subjectId) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (subjectId <= 0) throw new SogoErrorException("无效的课程体系ID");

        User user = userService.getByToken(utoken);
        JSONObject contactJson = new JSONObject();
        contactJson.put("name", user.getNickName());
        contactJson.put("mobile", user.getMobile());

        List<Price> prices = priceService.queryBySubject(subjectId);

        JSONObject result = new JSONObject();
        result.put("contact", contactJson);
        result.put("prices", prices);

        return result;
    }
}
