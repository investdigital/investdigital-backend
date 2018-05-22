package info.investdigital.entity.asset;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2018-05-04 11:14
 **/
@Data
@Entity
@Table(name = "huobi_symbols")
public class HuobiSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String basecurrency;    // 交易对前面的货币 ltcusdt 中的ltc
    private String quotecurrency;   // 交易对后面的货币
//    private String priceprecision;
//    private String amountprecision;
    private String symbolpartition; // 主币区、创新区

}
