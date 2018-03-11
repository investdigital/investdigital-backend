package info.investdigital.entity.DigitalCurrency;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author: huohuo
 * Created in 23:15  2018/3/9.
 */
@Data
@Entity
@Table(name = "ethusdt_min")
public class EthUsdMin {
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
