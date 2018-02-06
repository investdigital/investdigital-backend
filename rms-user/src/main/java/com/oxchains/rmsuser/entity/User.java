package com.oxchains.rmsuser.entity;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Set;

/**
 * @author ccl
 * @time 2017-12-12 17:06
 * @name User
 * @desc:
 */

@Entity
@Table(name = "sys_user")
public class User {

    public User(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32)
    private String username;

    @Column(length = 32,unique = true)
    private String loginname;

    @Column(length = 32,unique = true)
    private String email;

    @Column(length = 11,unique = true)
    private String mobilephone;

    @Column(length = 64)
    private String password;

    private String createTime;

    @Column(length = 64)
    private String image;

    @Column(length = 256)
    private String description;

    @Column
    private Integer enabled;

    @Transient
    private Set<String> permissionUriSet;

    public Set<String> getPermissionUriSet() {
        return permissionUriSet;
    }

    public void setPermissionUriSet(Set<String> permissionUriSet) {
        this.permissionUriSet = permissionUriSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }


    public User(User user){
        //setRoleId(user.getRoleId());
        setEmail(user.getEmail());
        setLoginname(user.getLoginname());
        setUsername(user.getUsername());
        //setFirstAddress((user.getFirstAddress()==null||"".equals(user.getFirstAddress().trim()))?"未填写":user.getFirstAddress());
        setId(user.getId());
        setMobilephone(user.getMobilephone());
        //setLoginStatus(user.getLoginStatus());
        setCreateTime(user.getCreateTime());

        setEnabled(user.getEnabled());

        setImage(user.getImage());
        setDescription(user.getDescription());
    }

    @Transient
    private Set<String> authorities;

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
