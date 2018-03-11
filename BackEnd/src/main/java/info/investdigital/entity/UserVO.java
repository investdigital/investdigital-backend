package info.investdigital.entity;

import java.util.Set;

/**
 * @author ccl
 * @time 2018-01-09 13:29
 * @name UserVO
 * @desc:
 */
public class UserVO extends User {
    private String token;
    private String newPassword;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    private Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
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
            user.setLoginStatus(this.getLoginStatus());
            user.setDescription(this.getDescription());

            return user;
        }
        return null;
    }

    private String vcode;

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }
}
