package info.investdigital.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 17:51  2018/3/5.
 */
@Data
@Table(name = "fund_detail")
@Entity
public class FundDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private BigInteger fundCode;  // 基金编号
    private String fundName;  // 基金名称
    private String fundAdvertising;   //基金广告
    private Long startTime; //基金成立日期
    private Float purchaseAmount; //起购金额
    private String openday;   // 开放日
    private String custodyUser; // 托管人
    private String brokerCompany; //经纪服务商
    private String investAdviserCompany; //投顾公司
    private String investAdviserRegion; //投顾地区
    private String investAdviserFee; //投顾费用
    //chains need
    private Long startRaiseTime; //开始募集时间  时间戳到秒
    private Long endRaiseTime;//结束募集时间 时间戳到秒
    private Long lastingClosedPeriod;//基金的封闭期  时间一到就可以分红了
    private Long capSupply; //募集的上限
    private Long manageFeeRate;//基金发行者对盈利部分的抽成  以1除以当前数的结果作为比例 例如当前述如果是10  则比例就是1/10  0.1  业绩提成比率
    private Long finalCapAmount; //最终募集金额
    private Integer applyForStatus; //请求状态   1 请求中 2 请求通过  3 请求失败

    private String address; //基金地址
}
