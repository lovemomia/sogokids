package com.sogokids.service.course;

import com.alibaba.fastjson.annotation.JSONField;
import com.sogokids.common.util.TimeUtil;

import java.util.Date;

public class UserBooked {
    public static final UserBooked NOT_EXIST_USER_BOOKED = new UserBooked();

    private long id;
    private long userId;
    private long childId;
    private long packageId;
    private int courseId;
    private int courseSkuId;

    private String cover;
    private String title;
    private Date startTime;
    private Date endTime;
    private String address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseSkuId() {
        return courseSkuId;
    }

    public void setCourseSkuId(int courseSkuId) {
        this.courseSkuId = courseSkuId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return TimeUtil.formatTimeRange(startTime, endTime);
    }

    public boolean exists() {
        return id > 0;
    }
}
