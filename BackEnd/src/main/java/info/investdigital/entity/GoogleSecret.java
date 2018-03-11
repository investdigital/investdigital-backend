package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author: Gaoyp
 * @Description:
 * @Date: Create in 上午10:38 2018/3/5
 * @Modified By:
 */
@Entity
@Table(name = "tbl_secret")
public class GoogleSecret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //秘钥值
    private String secret;


    public GoogleSecret() {
    }

    public GoogleSecret(String secret) {
        this.secret = secret;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
