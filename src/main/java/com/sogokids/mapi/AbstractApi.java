package com.sogokids.mapi;

import cn.momia.image.api.ImageFile;
import com.sogokids.web.ctrl.BaseController;
import com.sogokids.service.course.Course;
import com.sogokids.service.course.Subject;
import com.sogokids.service.course.UserBooked;
import com.sogokids.service.course.UserPackage;
import com.sogokids.service.teacher.Teacher;
import com.sogokids.service.user.User;
import com.sogokids.common.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

public class AbstractApi extends BaseController {
    protected static final String SUCCESS = "success";
    protected static final int PAGESIZE = 20;

    protected Subject postProcessSubject(Subject subject) {
        subject.setCover(ImageFile.largeUrl(subject.getCover()));
        return subject;
    }

    protected Course postProcessCourse(Course course) {
        course.setCover(ImageFile.largeUrl(course.getCover()));
        if (course.getImgs() != null) {
            List<String> imgs = new ArrayList<String>();
            for (String img : course.getImgs()) {
                imgs.add(ImageFile.largeUrl(img));
            }
            course.setImgs(imgs);
        }

        return course;
    }

    protected List<Course> postProcessCourses(List<Course> courses) {
        for (Course course : courses) {
            course.setCover(ImageFile.middleUrl(course.getCover()));
        }

        return courses;
    }

    protected List<Teacher> postProcessTeachers(List<Teacher> teachers) {
        for (Teacher teacher : teachers) {
            teacher.setAvatar(ImageFile.smallUrl(teacher.getAvatar()));
        }

        return teachers;
    }

    protected User postProcessUser(User user) {
        user.setAvatar(ImageFile.smallUrl(user.getAvatar()));
        user.setMobile(MiscUtil.encryptMobile(user.getMobile()));
        return user;
    }

    protected List<UserPackage> postProcessUserPackages(List<UserPackage> userPackages) {
        for (UserPackage userPackage : userPackages) {
            userPackage.setCover(ImageFile.middleUrl(userPackage.getCover()));
        }

        return userPackages;
    }

    protected List<UserBooked> postProcessUserBookeds(List<UserBooked> userBookeds) {
        for (UserBooked userBooked : userBookeds) {
            userBooked.setCover(ImageFile.middleUrl(userBooked.getCover()));
        }

        return userBookeds;
    }

    protected UserBooked postProcessUserBooked(UserBooked userBooked) {
        userBooked.setCover(ImageFile.largeUrl(userBooked.getCover()));
        return userBooked;
    }
}
