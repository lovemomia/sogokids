package com.sogokids.mapi.user;

import com.sogokids.exception.SogoErrorException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.sms.SmsService;
import com.sogokids.service.user.User;
import com.sogokids.service.user.UserService;
import com.sogokids.util.MiscUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthV1Api extends AbstractApi {
    @Autowired private SmsService smsService;
    @Autowired private UserService userService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String send(@RequestParam String mobile)  {
        if (MiscUtil.isInvalidMobile(mobile)) throw new SogoErrorException("无效的手机号码");
        if (!smsService.sendCode(mobile)) throw new SogoErrorException("发送短信验证码失败");
        return SUCCESS;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public User register(@RequestParam(value = "nickname") String nickName,
                         @RequestParam String mobile,
                         @RequestParam String password,
                         @RequestParam String code) {
        if (StringUtils.isBlank(nickName)) throw new SogoErrorException("昵称不能为空");
        if (nickName.contains("官方")) throw new SogoErrorException("用户昵称不能包含“官方”");
        if (MiscUtil.isInvalidMobile(mobile)) throw new SogoErrorException("无效的手机号码");
        if (StringUtils.isBlank(password)) throw new SogoErrorException("密码不能为空");
        if (StringUtils.isBlank(code)) throw new SogoErrorException("验证码不能为空");
        if (userService.exists("NickName", nickName)) throw new SogoErrorException("昵称已存在，不能使用");
        if (userService.exists("Mobile", mobile)) throw new SogoErrorException("该手机号已经注册过");
        if (!smsService.verifyCode(mobile, code)) throw new SogoErrorException("验证码不正确");

        long userId = userService.register(nickName, mobile, password);
        if (userId <= 0) throw new SogoErrorException("注册失败");

        return postProcessUser(userService.get(userId));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestParam String mobile, @RequestParam String password) {
        if (MiscUtil.isInvalidMobile(mobile)) throw new SogoErrorException("无效的手机号码");
        if (StringUtils.isBlank(password)) throw new SogoErrorException("密码不能为空");

        return postProcessUser(userService.login(mobile, password));
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public User updatePassword(@RequestParam String mobile, @RequestParam String password, @RequestParam String code) {
        if (MiscUtil.isInvalidMobile(mobile)) throw new SogoErrorException("无效的手机号码");
        if (StringUtils.isBlank(password)) throw new SogoErrorException("密码不能为空");
        if (StringUtils.isBlank(code)) throw new SogoErrorException("验证码不能为空");
        if (!smsService.verifyCode(mobile, code)) throw new SogoErrorException("验证码不正确");

        return postProcessUser(userService.updatePassword(mobile, password));
    }
}
