package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 11:08  2018/3/8.
 * desc Details of the subscription fund.
 */
@Data
@Entity
@Table(name = "subscribe_info")
public class SubscribeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;  //认购者用户id
    private BigInteger fundCode; //基金编号
    private Double amount; //认购数量
    private Long time;



    /*private String username; //用户名
    private String phone;// 联系电话
    private String email;//通讯邮箱
    private String idCardNum;//证件号码
    private String contactAddress;//通讯地址
    private Integer zipCode;  //邮政编码*/

}
