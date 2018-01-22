package com.oxchains.rmsuser.entity;

/**
 * @author ccl
 * @time 2018-01-09 13:29
 * @name UserVO
 * @desc:
 */
public class UserVO extends User {
    private String token;
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserVO() {}

    public UserVO(User user) {
        setId(user.getId());
        setUsername(user.getUsername());
        setLoginname(user.getLoginname());
        setMobilephone(user.getMobilephone());
        setEmail(user.getEmail());
        setCreateTime(user.getCreateTime());
        setImage(user.getImage());
        setEnabled(user.getEnabled());
        setDescription(user.getDescription());
    }

    public User userVO2User(UserVO vo){
        if(null != vo){
            User user = new User();
            user.setId(vo.getId());
            user.setUsername(vo.getUsername());
            user.setLoginname(vo.getLoginname());
            user.setMobilephone(vo.getMobilephone());
            user.setEmail(vo.getEmail());
            user.setPassword(vo.getPassword());
            user.setEnabled(vo.getEnabled());
            user.setImage(vo.getImage());
            user.setDescription(vo.getDescription());

            return user;
        }
        return null;
    }

    public User userVO2User(){
        if(null != this){
            User user = new User();
            user.setId(this.getId());
            user.setUsername(this.getUsername());
            user.setLoginname(this.getLoginname());
            user.setMobilephone(this.getMobilephone());
            user.setEmail(this.getEmail());
            user.setPassword(this.getPassword());
            user.setEnabled(this.getEnabled());
            user.setImage(this.getImage());
            user.setDescription(this.getDescription());

            return user;
        }
        return null;
    }
}
