package info.investdigital.entity;

import javax.persistence.*;

/**
 * @Author: Gaoyp
 * @Description:
 * @Date: Create in 上午10:49 2018/3/5
 * @Modified By:
 */
@Entity
@Table(name = "tbl_secret_user")
public class GoogleSecretUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false,name = "s_id")
    private Long sid;
    @Column(unique = true,nullable = false,name = "u_id")
    private Long uid;

    public GoogleSecretUser() {
    }

    public GoogleSecretUser(Long sid, Long uid) {
        this.sid = sid;
        this.uid = uid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
