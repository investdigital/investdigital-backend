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
@Table(name = "comments")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 32)
    private String appKey;
    private Long itemId;
    private Long userId;
    private Date createTime;
    @Column(length = 255)
    private String contents;

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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    //@Transient
    @Column(length = 32)
    private String username;

    //@Transient
    @Column(length = 32)
    private String avatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private Integer disapproval;

    private Integer approval;

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
