package info.investdigital.entity.asset;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2018-05-06 16:19
 **/
@Data
@Entity
@Table(name = "huobi_no_symbol_coin")
public class HuobiNoSymbolCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String coin;
    private String count;
}
