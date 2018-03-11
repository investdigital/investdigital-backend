package info.investdigital.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author ccl
 * @time 2017-12-13 10:25
 * @name FundIssuance
 * @desc:
 */
@Entity
@Table(name = "fund_issuance")
@Data
public class FundIssuance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull(message = "Please set your username.")
    @NotEmpty(message = "Please set your username.")
    private String username;
    @NotNull(message = "Please set your investDigitalNo.")
    @NotEmpty(message = "Please set your investDigitalNo.")
    private String investDigitalNo;
    @NotNull(message = "Please set your mobilephone.")
    @NotEmpty(message = "Please set your mobilephone.")
    private String mobilephone;

    @NotNull(message = "Please select your information..")
    @NotEmpty(message = "Please select your information..")
    private Integer assetManageScale;//资产管理规模
    @NotNull(message = "Please select your information..")
    @NotEmpty(message = "Please select your information..")
    private Integer privateIssuanceTime;//拟发行私募时间 1 立刻发行 2 三个月内3 3-6个月内 4 无法确定
    @NotNull(message = "Please select your information..")
    @NotEmpty(message = "Please select your information..")
    private Integer fundQualification;//是否具备基金从业资格  1 一人具备从业资格 2 二人及二人以上具备从业资格 3  不具备
    @NotNull(message = "Please select your information..")
    @NotEmpty(message = "Please select your information..")
    private Integer privateIssuanceStage;  //私募运行阶段    1尚未成立公司 2 初创型私募 3 成长性私募
    @NotNull(message = "Please select your information..")
    @NotEmpty(message = "Please select your information..")
    private Integer fundAssociationRecord;//  是否在基金业协会备案   1已备案 2 未备案
    @NotNull(message = "Please select your information..")
    @NotEmpty(message = "Please select your information..")
    private Integer productDistribution; // 发行产品情况 未曾发行产品  1 未发行 2已发行

    //chains need
    @NotNull(message = "Please set your start Raise Time.")
    @NotEmpty(message = "Please set your start Raise Time.")
    private Long startRaiseTime; //开始募集时间  时间戳到秒
    @NotNull(message = "Please set your end Raise Time.")
    @NotEmpty(message = "Please set your end Raise Time.")
    private Long endRaiseTime;//结束募集时间 时间戳到秒
    @NotNull(message = "Please set your lasting Closed Period.")
    @NotEmpty(message = "Please set your lasting Closed Period.")
    private Long lastingClosedPeriod;//基金的封闭期  时间一到就可以分红了
    @NotNull(message = "Please set your cap Supply.")
    @NotEmpty(message = "Please set your cap Supply.")
    private Long capSupply; //募集的上限
    @NotNull(message = "Please set your manage Fee Rate.")
    @NotEmpty(message = "Please set your manage Fee Rate.")
    private Long manageFeeRate;//基金发行者对盈利部分的抽成  以1除以当前数的结果作为比例 例如当前述如果是10  则比例就是1/10  0.1

    private Integer applyForStatus; //请求状态   1 请求中 2 请求通过  3 请求失败


}
