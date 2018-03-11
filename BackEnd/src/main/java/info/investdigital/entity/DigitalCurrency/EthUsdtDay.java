package info.investdigital.entity.DigitalCurrency;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author: huohuo
 * Created in 20:33  2018/3/8.
 */
@Data
@Entity
@Table(name = "ethusdt_day")
public class EthUsdtDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double open;
    private Double close;
    private Double low;
    private Double high;
    @Transient
    private Double priceChangeRatio;
}
