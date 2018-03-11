package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 16:59  2018/3/9.
 */
@Data
@Entity
@Table(name = "FundPerformanceAnalysis")
public class FundPerformanceAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigInteger fundCode;
    private Double duringEarning;//期间收益
    private Double annualEarning;//年化收益
    private Double annualExcessEarning;//年华超额收益
    private Double monthRollingEarningAvg;//12个月滚动收益均值
    private Double monthRollingEarningMax;//12个月滚动收益最大
    private Double monthRollingEarningLow;//12个月滚动收益最低
    private Double volatility;//波动性
    private Double maxRetracement;//最大回撤
    private String maxRetracementTime;//最大回撤时间段
    private Double sharpe; //Sharpe比例
    private Double sortino; //sortino比例
    private Double biggestFactor;//最大系数
    private Double beta;//贝塔系数

}
