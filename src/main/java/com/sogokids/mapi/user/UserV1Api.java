package com.sogokids.mapi.user;

import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.common.exception.SogoLoginException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.mapi.PagedList;
import com.sogokids.service.course.Course;
import com.sogokids.service.course.CourseService;
import com.sogokids.service.course.CourseSku;
import com.sogokids.service.course.UserBooked;
import com.sogokids.service.course.UserPackage;
import com.sogokids.service.user.Child;
import com.sogokids.service.user.User;
import com.sogokids.service.user.UserService;
import com.sogokids.common.util.MiscUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/user")
public class UserV1Api extends AbstractApi {
    @Autowired private CourseService courseService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public User getByToken(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        return postProcessUser(userService.getByToken(utoken));
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.POST)
    public User updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(nickName)) throw new SogoErrorException("用户昵称不能为空");
        if (nickName.contains("官方")) throw new SogoErrorException("用户昵称不能包含“官方”");
        if (userService.exists("NickName", nickName)) throw new SogoErrorException("昵称已存在，不能使用");

        User user = userService.getByToken(utoken);
        if (!userService.updateNickName(user.getId(), nickName)) throw new SogoErrorException("更新用户昵称失败");

        user.setNickName(nickName);
        return postProcessUser(user);
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public User updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(avatar)) throw new SogoErrorException("用户头像不能为空");

        User user = userService.getByToken(utoken);
        if (!userService.updateAvatar(user.getId(), avatar)) throw new SogoErrorException("更新用户头像失败");

        user.setAvatar(avatar);
        return postProcessUser(user);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.POST)
    public User updateSex(@RequestParam String utoken, @RequestParam String sex) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(sex) || MiscUtil.isInvalidSex(sex)) throw new SogoErrorException("无效的用户性别");

        User user = userService.getByToken(utoken);
        if (!userService.updateSex(user.getId(), sex)) throw new SogoErrorException("更新用户性别失败");

        user.setSex(sex);
        return postProcessUser(user);
    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public User updateAddress(@RequestParam String utoken, @RequestParam String address) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(address)) throw new SogoErrorException("地址不能为空");

        User user = userService.getByToken(utoken);
        if (!userService.updateAddress(user.getId(), address)) throw new SogoErrorException("更新用户地址失败");

        user.setAddress(address);
        return postProcessUser(user);
    }

    @RequestMapping(value = "/bookable", method = RequestMethod.GET)
    public PagedList<UserPackage> bookable(@RequestParam String utoken, @RequestParam int start) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (start < 0) throw new SogoErrorException("无效的分页参数");

        User user = userService.getByToken(utoken);
        long totalCount = courseService.queryBookableCount(user.getId());
        List<UserPackage> userPackages = courseService.queryBookable(user.getId(), start, PAGESIZE);

        PagedList<UserPackage> pagedUserPackages = new PagedList<UserPackage>(totalCount, start, PAGESIZE);
        pagedUserPackages.setList(postProcessUserPackages(userPackages));

        return pagedUserPackages;
    }

    @RequestMapping(value = "/booking", method = RequestMethod.POST)
    public String booking(@RequestParam String utoken,
                          @RequestParam(value = "cid") long childId,
                          @RequestParam(value = "pid") long packageId,
                          @RequestParam(value = "coid") int courseId,
                          @RequestParam(value = "sid") int courseSkuId) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (childId <= 0) throw new SogoErrorException("无效的孩子ID");
        if (packageId <= 0) throw new SogoErrorException("无效的包ID");
        if (courseId <= 0) throw new SogoErrorException("无效的课程ID");
        if (courseSkuId <= 0) throw new SogoErrorException("无效的场次ID");

        User user = userService.getByToken(utoken);
        Set<Long> childIds = new HashSet<Long>();
        for (Child child : user.getChildren()) {
            childIds.add(child.getId());
        }
        if (!childIds.contains(childId)) throw new SogoErrorException("无效的孩子ID");

        UserPackage userPackage = courseService.getUserPackage(packageId);
        Course course = courseService.get(courseId);
        CourseSku courseSku = courseService.getSku(courseSkuId);
        if (!userPackage.exists() ||
                !course.exists() ||
                !courseSku.exists() ||
                courseSku.getCourseId() != courseId) throw new SogoErrorException("选课失败，无效的选课参数");

        if (userPackage.getBookableCount() <= 0) throw new SogoErrorException("选课失败，该课程包的选课次数已经用完");
        if (courseSku.getStock() <= 0 || courseSku.getDeadline().before(new Date())) throw new SogoErrorException("选课失败，该场次已经报满或选课已截止");

        if (!courseService.booking(user.getId(), childId, packageId, courseId, courseSkuId)) throw new SogoErrorException("预约失败");

        return SUCCESS;
    }

    @RequestMapping(value = "/booked/notfinished", method = RequestMethod.GET)
    public PagedList<UserBooked> notfinished(@RequestParam String utoken, @RequestParam int start) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (start < 0) throw new SogoErrorException("无效的分页参数");

        User user = userService.getByToken(utoken);
        long totalCount = courseService.queryNotFinishedCount(user.getId());
        List<UserBooked> userBookeds = courseService.queryNotFinished(user.getId(), start, PAGESIZE);

        PagedList<UserBooked> pagedUserBookeds = new PagedList<UserBooked>(totalCount, start, PAGESIZE);
        pagedUserBookeds.setList(postProcessUserBookeds(userBookeds));

        return pagedUserBookeds;
    }

    @RequestMapping(value = "/booked/finished", method = RequestMethod.GET)
    public PagedList<UserBooked> finished(@RequestParam String utoken, @RequestParam int start) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (start < 0) throw new SogoErrorException("无效的分页参数");

        User user = userService.getByToken(utoken);
        long totalCount = courseService.queryFinishedCount(user.getId());
        List<UserBooked> userBookeds = courseService.queryFinished(user.getId(), start, PAGESIZE);

        PagedList<UserBooked> pagedUserBookeds = new PagedList<UserBooked>(totalCount, start, PAGESIZE);
        pagedUserBookeds.setList(postProcessUserBookeds(userBookeds));

        return pagedUserBookeds;
    }

    @RequestMapping(value = "/booked", method = RequestMethod.GET)
    public UserBooked get(@RequestParam String utoken, @RequestParam(value = "bid") long bookedId) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (bookedId < 0) throw new SogoErrorException("无效的预约ID");

        User user = userService.getByToken(utoken);
        UserBooked userBooked = courseService.getUserBooked(bookedId);
        if (!userBooked.exists() || userBooked.getUserId() != user.getId()) throw new SogoErrorException("无效的预约ID");

        return postProcessUserBooked(userBooked);
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancel(@RequestParam String utoken, @RequestParam(value = "bid") long bookedId) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (bookedId < 0) throw new SogoErrorException("无效的预约ID");

        User user = userService.getByToken(utoken);
        UserBooked userBooked = courseService.getUserBooked(bookedId);
        if (!userBooked.exists() || userBooked.getUserId() != user.getId()) throw new SogoErrorException("无效的预约ID");

        if (!courseService.cancel(user.getId(), bookedId, userBooked.getPackageId(), userBooked.getCourseId(), userBooked.getCourseSkuId())) throw new SogoErrorException("取消预约失败");

        return SUCCESS;
    }
}
