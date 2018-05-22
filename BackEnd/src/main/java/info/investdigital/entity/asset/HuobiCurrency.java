package info.investdigital.entity.asset;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2018-05-07 15:38
 **/
@Data
@Entity
@Table(name = "huobi_currency")
public class HuobiCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String currency;

}
