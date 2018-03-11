package info.investdigital.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ccl
 * @time 2018-03-07 11:44
 * @name ApplyV
 * @desc:
 */
@Entity
@Table(name = "applyv")
public class ApplyV {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;

    @Transient
    private String username;

    private Date applyTime;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ApplyV() {
    }


    public ApplyV(Long userId, Date applyTime, Integer status) {
        this.userId = userId;
        this.applyTime = applyTime;
        this.status = status;
    }
}
