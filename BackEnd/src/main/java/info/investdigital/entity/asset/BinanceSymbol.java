package info.investdigital.entity.asset;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2018-05-04 16:00
 **/
@Data
@Entity
@Table(name = "binance_symbols")
public class BinanceSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String symbol;
}
