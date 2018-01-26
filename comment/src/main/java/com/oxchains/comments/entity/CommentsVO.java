package com.oxchains.comments.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author ccl
 * @time 2018-01-22 13:44
 * @name Comments
 * @desc:
 */
public class CommentsVO extends Comments{
    private List<CommentsReplyVO> replies;

    public List<CommentsReplyVO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentsReplyVO> replies) {
        this.replies = replies;
    }

    public CommentsVO() {}
    public CommentsVO(Comments comments) {
        setId(comments.getId());
        setAppKey(comments.getAppKey());
        setItemId(comments.getItemId());
        setUserId(comments.getUserId());
        setUsername(comments.getUsername());
        setAvatar(comments.getAvatar());
        setContents(comments.getContents());
        setCreateTime(comments.getCreateTime());
        setApproval(comments.getApproval());
        setDisapproval(comments.getDisapproval());
    }

    private Integer approvaled;

    public Integer getApprovaled() {
        return approvaled;
    }

    public void setApprovaled(Integer approvaled) {
        this.approvaled = approvaled;
    }

    public Comments commentsVO2Comments(){
        if(null != this){
            Comments comments = new Comments();
            comments.setId(this.getId());
            comments.setAppKey(this.getAppKey());
            comments.setItemId(this.getItemId());
            comments.setUserId(this.getUserId());
            comments.setUsername(this.getUsername());
            comments.setAvatar(this.getAvatar());
            comments.setContents(this.getContents());
            comments.setCreateTime(this.getCreateTime());
            comments.setApproval(this.getApproval());
            comments.setDisapproval(this.getDisapproval());
            return comments;
        }
        return null;
    }

    @Override
    public String toString() {
        super.toString();
        return "CommentsVO{" +
                "replies=" + replies +
                ", approvaled=" + approvaled +
                '}';
    }
}
