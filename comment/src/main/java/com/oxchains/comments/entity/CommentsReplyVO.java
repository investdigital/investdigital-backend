package com.oxchains.comments.entity;

/**
 * @author ccl
 * @time 2018-01-25 11:41
 * @name CommentsReplyVO
 * @desc:
 */
public class CommentsReplyVO extends CommentsReply {
    public CommentsReplyVO() {
    }

    public CommentsReplyVO(CommentsReply reply) {
        setId(reply.getId());
        setCommentsId(reply.getCommentsId());
        setUserId(reply.getUserId());
        setUsername(reply.getUsername());
        setAvatar(reply.getAvatar());

        setReplyUserId(reply.getReplyUserId());
        setReplyUsername(reply.getReplyUsername());

        setCreateTime(reply.getCreateTime());
        setContents(reply.getContents());
        setApproval(reply.getApproval());
        setDisapproval(reply.getDisapproval());
    }

    public CommentsReply commentsReplyVO2CommentsReply(){
        if(null != this){
            CommentsReply reply = new CommentsReply();
            reply.setId(this.getId());
            reply.setCommentsId(this.getCommentsId());
            reply.setUserId(this.getUserId());
            reply.setUsername(this.getUsername());
            reply.setAvatar(this.getAvatar());

            reply.setReplyUserId(this.getReplyUserId());
            reply.setReplyUsername(this.getReplyUsername());

            reply.setCreateTime(this.getCreateTime());
            reply.setContents(this.getContents());
            reply.setApproval(this.getApproval());
            reply.setDisapproval(this.getDisapproval());
        }
        return null;
    }

    @Override
    public String toString() {
        super.toString();
        return "CommentsReplyVO{}";
    }
}
