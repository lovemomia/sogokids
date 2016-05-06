package com.sogokids.service.course;

import com.alibaba.fastjson.annotation.JSONField;
import com.sogokids.util.TimeUtil;

import java.util.Date;

public class CourseSku {
    public static final CourseSku NOT_EXIST_COURSE_SKU = new CourseSku();

    private int id;
    private int courseId;
    private String desc;
    private int cityId;
    private int regionId;
    private String address;
    private Date startTime;
    private Date endTime;
    private Date deadline;
    private int stock;

    private String region;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean exists() {
        return id > 0;
    }

    public String getTime() {
        return TimeUtil.formatTimeRange(startTime, endTime);
    }
}
