package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 21:24  2018/3/8.
 */
@Data
@Entity
@Table(name = "fund_statistical")
public class FundStatistical {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long time;
    private BigInteger fundCode;
    private Double netAssetValue;//资产净值
    private Double thisDayEarning;
    private Double thisWeekEarning;
    private Double thisMongthEarning;
    @Transient
    private Double priceChangeRatio;


}
