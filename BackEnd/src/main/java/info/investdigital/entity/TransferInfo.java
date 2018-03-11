package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 12:27  2018/3/8.
 */
@Data
@Entity
@Table(name = "transfer_info")
public class TransferInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long time;
    private Long transferUserId;
    private String fromAddress;
    private String toAddress;
    private Double amount;
    private BigInteger fundCode;


}
