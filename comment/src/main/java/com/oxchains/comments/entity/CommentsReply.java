package com.oxchains.comments.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ccl
 * @time 2018-01-22 13:44
 * @name Comments
 * @desc:
 */
@Entity
@Table(name = "comments_reply")
public class CommentsReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     private Long commentsId;
    private Long userId;
    private Long replyUserId;
    private Date createTime;
    @Column(length = 255)
    private String contents;

    //@Transient
    @Column(length = 32)
    private String username;
    @Transient
    @Column(length = 32)
    private String replyUsername;
    //@Transient
    @Column(length = 32)
    private String avatar;
    @Transient
    @Column(length = 32)
    private String ravatar;
    @Transient
    private Integer disapproval;
    @Transient
    private Integer approval;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(Long commentsId) {
        this.commentsId = commentsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReplyUsername() {
        return replyUsername;
    }

    public void setReplyUsername(String replyUsername) {
        this.replyUsername = replyUsername;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRavatar() {
        return ravatar;
    }

    public void setRavatar(String ravatar) {
        this.ravatar = ravatar;
    }

    public Integer getDisapproval() {
        return disapproval;
    }

    public void setDisapproval(Integer disapproval) {
        this.disapproval = disapproval;
    }

    public Integer getApproval() {
        return approval;
    }

    public void setApproval(Integer approval) {
        this.approval = approval;
    }
}
