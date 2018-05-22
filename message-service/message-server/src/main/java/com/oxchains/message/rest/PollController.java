package com.oxchains.message.rest;

import com.oxchains.message.common.RestResp;
import com.oxchains.message.domain.MessageText;
import com.oxchains.message.domain.PollData;
import com.oxchains.message.service.PollService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author luoxuri
 * @create 2018-02-07 14:17
 **/
@RestController
@RequestMapping(value = "/message")
public class PollController {

    @Resource
    private PollService pollService;

    @PostMapping(value = "/send/noticeMsg")
    public RestResp sendNoticeMsg(@RequestBody MessageText messageText) {
        if (messageText.getLogo() == null) {
            return RestResp.fail("请填写项目标识");
        }
        if (messageText.getMessage() == null) {
            return RestResp.fail("请填写内容");
        }
        if (messageText.getOrderId() == null) {
            messageText.setOrderId("");
        }
        if (messageText.getUserGroup() == null) {
            messageText.setUserGroup(4L);
        }
        return pollService.sendNoticeMsg(messageText);
    }

    @PostMapping(value = "/query/noticeMsg")
    public RestResp queryNoticeMsg(@RequestBody PollData pollData) {
        return pollService.queryNoticeMsg(pollData);
    }

    @PostMapping(value = "/query/globalMsg")
    public RestResp queryGlobalMsg(@RequestBody PollData pollData) {
        return pollService.queryGlobalMsg(pollData);
    }

    @PostMapping(value = "/query/privateMsg")
    public RestResp queryPrivateMsg(@RequestBody PollData pollData) {
        return pollService.queryPrivateMsg(pollData);
    }

    @PostMapping(value = "/query/unReadCount")
    public RestResp queryUnReadCount(@RequestBody PollData pollData){
        return pollService.queryUnReadCount(pollData);
    }

    @DeleteMapping(value = "delete/noticeMsg/{messageId}")
    public RestResp deleteNoticeMsg(@PathVariable Long messageId){
        return pollService.deleteNoticeMsg(messageId);
    }

}
