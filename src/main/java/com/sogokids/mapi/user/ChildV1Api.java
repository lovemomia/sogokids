package com.sogokids.mapi.user;

import com.alibaba.fastjson.JSON;
import com.sogokids.exception.SogoErrorException;
import com.sogokids.exception.SogoLoginException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.user.Child;
import com.sogokids.service.user.ChildService;
import com.sogokids.service.user.User;
import com.sogokids.service.user.UserService;
import com.sogokids.util.CastUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/user/child")
public class ChildV1Api extends AbstractApi {
    @Autowired private ChildService childService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public User add(@RequestParam String utoken, @RequestParam(value = "children") String childrenJson) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(childrenJson)) throw new SogoErrorException("孩子信息不能为空");

        User user = userService.getByToken(utoken);
        List<Child> children = CastUtil.toList(JSON.parseArray(childrenJson), Child.class);
        for (Child child : children) {
            child.setUserId(user.getId());
            if (child.isInvalid()) throw new SogoErrorException("添加失败，无效的孩子信息");
        }

        for (Child child : children) {
            if (child.getId() <= 0) childService.add(child);
            else childService.update(child);
        }

        return postProcessUser(userService.getByToken(utoken));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public User update(@RequestParam String utoken, @RequestParam(value = "children") String childrenJson) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (StringUtils.isBlank(childrenJson)) throw new SogoErrorException("孩子信息不能为空");

        User user = userService.getByToken(utoken);
        Set<Long> childIds = new HashSet<Long>();
        for (Child child : user.getChildren()) {
            childIds.add(child.getId());
        }

        List<Child> children = CastUtil.toList(JSON.parseArray(childrenJson), Child.class);
        for (Child child : children) {
            if (!childIds.contains(child.getId()) || child.isInvalid()) throw new SogoErrorException("更新失败，无效的孩子信息");
        }

        for (Child child : children) {
            childService.update(child);
        }

        return postProcessUser(userService.getByToken(utoken));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public User delete(@RequestParam String utoken, @RequestParam(value = "cid") long childId) {
        if (StringUtils.isBlank(utoken)) throw new SogoLoginException();
        if (childId <= 0) throw new SogoErrorException("无效的孩子ID");

        User user = userService.getByToken(utoken);
        if (!childService.delete(user.getId(), childId)) throw new SogoErrorException("删除孩子信息失败");

        return postProcessUser(userService.getByToken(utoken));
    }
}
