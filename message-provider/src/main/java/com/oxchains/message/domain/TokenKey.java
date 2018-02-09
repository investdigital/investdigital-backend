package com.oxchains.message.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author ccl
 * @time 2017-11-08 13:28
 * @name TokenKey
 * @desc:
 */

@Entity
@Table(name = "tbl_sys_tokenkey")
public class TokenKey implements Serializable{

    private static final long serialVersionUID = -1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createTime;

    private Date updateTime;

    @Lob
    private byte[] priKey;

    @Lob
    private byte[] pubKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public byte[] getPriKey() {
        return priKey;
    }

    public void setPriKey(byte[] priKey) {
        this.priKey = priKey;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }
}
