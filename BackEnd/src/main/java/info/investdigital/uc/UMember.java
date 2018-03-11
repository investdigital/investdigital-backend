package info.investdigital.uc;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-12-12 17:06
 * @name User
 * @desc:
 */
public class UMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(length = 15)
    private String username;

    @Column(length = 32)
    private String password;

    @Column(length = 32)
    private String email;

    @Column(length = 30)
    private String myid;

    @Column(length = 16)
    private String myidkey;

    @Column(length = 15)
    private String regip;

    @Column(length = 10)
    private Integer regdate;

    @Column(length = 10)
    private Integer lastloginip;

    @Column(length = 10)
    private Integer lastlogintime;

    @Column(length = 6)
    private String salt;

    @Column(length = 8)
    private String secques;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getMyidkey() {
        return myidkey;
    }

    public void setMyidkey(String myidkey) {
        this.myidkey = myidkey;
    }

    public String getRegip() {
        return regip;
    }

    public void setRegip(String regip) {
        this.regip = regip;
    }

    public Integer getRegdate() {
        return regdate;
    }

    public void setRegdate(Integer regdate) {
        this.regdate = regdate;
    }

    public Integer getLastloginip() {
        return lastloginip;
    }

    public void setLastloginip(Integer lastloginip) {
        this.lastloginip = lastloginip;
    }

    public Integer getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(Integer lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSecques() {
        return secques;
    }

    public void setSecques(String secques) {
        this.secques = secques;
    }


    public UMember(){
        this.username = "";
        this.password = "";
        this.email = "";
        this.myid = "";
        this.myidkey = "";
        this.regip = "";
        this.regdate = 0;
        this.lastloginip = 0;
        this.lastlogintime = 0;
        this.secques = "";
    }
}
