package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * @author oxchains
 * @time 2017-12-13 17:06
 * @name Fund
 * @desc:
 */
@Data
@Entity
@Table(name = "fund_return")
public class FundReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long fundId;//基金id

    private BigInteger fundCode; //基金编号

    private Float totalReturn; //总收益率

    private Float netAssetValue;//基金单位净值

    private Float netValue; //累计净值

    private Float priceChangeRatio;//涨跌幅

    private Float currentQuantity; //当前 基金数量

    private Float todayChange;

    private Float weekChange;

    private Float monthChange;

    @Column(name = "month3_change")
    private Float month3Change;
    @Column(name = "month6_change")
    private Float month6Change;
    private Float yearChange;
    @Column(name = "year2_change")
    private Float year2Change;
    @Column(name = "year3_change")
    private Float year3Change;
    private Float thisYearChange;
    private Float untilNowChange;

}
