package info.investdigital.entity;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-12-12 17:06
 * @name User
 * @desc:
 */

@Entity
@Table(name = "tbl_sys_user")
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

    //@JsonIgnore
    @Column(length = 64)
    private String password;

    @Column(length = 42)
    private String firstAddress;

    private Integer loginStatus;

    private String createTime;

    @Column(length = 64)
    private String image;

    @Column(length = 256)
    private String description;

    @Column(length = 128)
    private String fpassword;

    @Column
    private Integer enabled;

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

    public String getFirstAddress() {
        return firstAddress;
    }

    public void setFirstAddress(String firstAddress) {
        this.firstAddress = firstAddress;
    }

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    @Transient
    private String token;

    @Transient
    private String newPassword;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getFpassword() {
        return fpassword;
    }

    public void setFpassword(String fpassword) {
        this.fpassword = fpassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public User(User user){
        setEmail(user.getEmail());
        setLoginname(user.getLoginname());
        setUsername(user.getUsername());
        setFirstAddress((user.getFirstAddress()==null||"".equals(user.getFirstAddress().trim()))?"未填写":user.getFirstAddress());
        setId(user.getId());
        setMobilephone(user.getMobilephone());
        setLoginStatus(user.getLoginStatus());
        setCreateTime(user.getCreateTime());

        setEnabled(user.getEnabled());

        setImage(user.getImage());
        setDescription(user.getDescription());
    }

    @Transient
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
