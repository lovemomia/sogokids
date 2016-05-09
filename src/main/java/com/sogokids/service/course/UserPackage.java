package com.sogokids.service.course;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class UserPackage {
    public static final UserPackage NOT_EXIST_USER_PACKAGE = new UserPackage();

    private long id;
    private long userId;
    private long orderId;
    private long priceId;
    private long subjectId;
    private long subjectSkuId;
    private int courseCount;
    private int bookableCount;
    private Date addTime;

    private String cover;
    private String title;

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

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getPriceId() {
        return priceId;
    }

    public void setPriceId(long priceId) {
        this.priceId = priceId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public long getSubjectSkuId() {
        return subjectSkuId;
    }

    public void setSubjectSkuId(long subjectSkuId) {
        this.subjectSkuId = subjectSkuId;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    public int getBookableCount() {
        return bookableCount;
    }

    public void setBookableCount(int bookableCount) {
        this.bookableCount = bookableCount;
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
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

    public boolean exists() {
        return id > 0;
    }
}
