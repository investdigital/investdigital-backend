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
@Table(name = "comments_favor")
public class CommentsFavor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 32)
    private String appKey;
    private Long itemId;
    private Long commentsId;
    private Long commentsReplyId;
    private Long userId;
    private Date createTime;
    private Integer favor;
    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(Long commentsId) {
        this.commentsId = commentsId;
    }

    public Long getCommentsReplyId() {
        return commentsReplyId;
    }

    public void setCommentsReplyId(Long commentsReplyId) {
        this.commentsReplyId = commentsReplyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getFavor() {
        return favor;
    }

    public void setFavor(Integer favor) {
        this.favor = favor;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
