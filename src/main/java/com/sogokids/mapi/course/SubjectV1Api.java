package com.sogokids.mapi.course;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.common.exception.SogoLoginException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.course.Course;
import com.sogokids.service.course.CourseService;
import com.sogokids.service.course.Subject;
import com.sogokids.service.course.SubjectOrder;
import com.sogokids.service.course.SubjectService;
import com.sogokids.service.payment.OrderService;
import com.sogokids.service.payment.Price;
import com.sogokids.service.payment.PriceService;
import com.sogokids.service.payment.Type;
import com.sogokids.service.user.User;
import com.sogokids.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/subject")
public class SubjectV1Api extends AbstractApi {
    @Autowired private SubjectService subjectService;
    @Autowired private CourseService courseService;
    @Autowired private PriceService priceService;
    @Autowired private OrderService orderService;
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

    @RequestMapping(value = "/placeorder", method = RequestMethod.POST)
    public SubjectOrder placeOrder(@RequestParam String utoken, @RequestParam String order) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(order)) throw new SogoErrorException("订单信息不能为空");

        JSONObject orderJson = JSON.parseObject(order);
        JSONObject contactJson = orderJson.getJSONObject("contact");
        if (contactJson == null ||
                StringUtils.isBlank(contactJson.getString("name")) ||
                StringUtils.isBlank(contactJson.getString("mobile"))) throw new SogoErrorException("联系人信息缺失");

        List<Price> prices = new ArrayList<Price>();
        JSONArray pricesJson = orderJson.getJSONArray("prices");
        if (pricesJson == null || pricesJson.isEmpty()) throw new SogoErrorException("订单数据为空");
        for (int i = 0; i < pricesJson.size(); i++) {
            JSONObject priceJson = pricesJson.getJSONObject(i);
            Price price = JSON.toJavaObject(priceJson, Price.class);
            if (price.getCount() > 0) prices.add(price);
        }
        if (prices.isEmpty()) throw new SogoErrorException("订单数据为空");

        User user = userService.getByToken(utoken);

        long orderId = subjectService.placeOrder(user.getId(), Type.SUBJECT_PACKAGE, contactJson.getString("name"), contactJson.getString("mobile"), prices);
        if (orderId <= 0) throw new SogoErrorException("下单失败");

        return subjectService.getSubjectOrder(orderId);
    }
}
