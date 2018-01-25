package com.oxchains.comments.rest;

import com.oxchains.comments.common.RestResp;
import com.oxchains.comments.entity.Comments;
import com.oxchains.comments.entity.CommentsFavor;
import com.oxchains.comments.entity.CommentsReply;
import com.oxchains.comments.service.CommentsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ccl
 * @time 2018-01-22 13:32
 * @name CommentsController
 * @desc:
 */
@RestController
public class CommentsController {

    @Resource
    private CommentsService commentsService;

    @GetMapping(value = "/list")
    public RestResp getComments(String appKey, Long itemId, Integer pageSize, Integer pageNo){
        return commentsService.getComments(appKey,itemId,pageSize,pageNo);
    }

    @GetMapping(value = "/list/{userId}")
    public RestResp getComments(String appKey, Long itemId, @PathVariable Long userId){
        return commentsService.getComments(appKey,itemId,userId);
    }

    @PostMapping(value = "/add")
    public RestResp addComments(Comments comments){
        return commentsService.addComments(comments);
    }

    @DeleteMapping(value = "/delete/{commentsId}")
    public RestResp deleteComments(@PathVariable Long commentsId){
        return commentsService.deleteComments(commentsId);
    }

    @PostMapping(value = "/reply")
    public RestResp addCommentsReply(CommentsReply reply){
        return commentsService.addCommentReplay(reply);
    }

    @PostMapping(value = "/favor")
    public RestResp addCommentsFavor(CommentsFavor favor){
        return commentsService.addCommentFavor(favor);
    }
}
