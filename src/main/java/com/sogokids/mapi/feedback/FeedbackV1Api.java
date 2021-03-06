package com.sogokids.mapi.feedback;

import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.mapi.AbstractApi;
import com.sogokids.service.feedback.FeedbackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/feedback")
public class FeedbackV1Api extends AbstractApi {
    @Autowired private FeedbackService feedbackService;

    @RequestMapping(method = RequestMethod.POST)
    public String addFeedback(@RequestParam String content, @RequestParam String contact) {
        if (StringUtils.isBlank(content)) throw new SogoErrorException("反馈内容不能为空");
        if (StringUtils.isBlank(contact)) throw new SogoErrorException("联系方式不能为空");
        if (content.length() > 500) throw new SogoErrorException("反馈内容字数超出限制");
        if (!feedbackService.add(content, contact)) throw new SogoErrorException("提交反馈意见失败");

        return SUCCESS;
    }
}
