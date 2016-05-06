package com.sogokids.service.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.sogokids.util.MiscUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class Child {
    private long id;
    private long userId;
    private String avatar;
    private String name;
    private String sex;
    private Date birthday;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @JSONField(format = "yyyy-MM-dd")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @JSONField(serialize = false)
    public boolean isInvalid() {
        return userId <= 0 || StringUtils.isBlank(name) || MiscUtil.isInvalidSex(sex) || birthday == null;
    }
}
