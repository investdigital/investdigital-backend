package info.investdigital.entity;

import info.investdigital.common.DateUtil;
import info.investdigital.entity.*;
import info.investdigital.entity.DigitalCurrency.EthUsdtDay;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @Author: huohuo
 * Created in 18:20  2018/3/8.
 */
@Data
public class FundDetailVo{
    private FundReturn fundReturn;
    private List<String> tags;
    private Echart echart;
    private BigInteger fundCode;
    private String fundName;
    private Long issueUser;
    private Long startTime;
    private String startTimeStr;
    private String fee;
    private String issueUserName;
    private Long fundId;
    private String fundAdvertising;   //基金广告
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
    private String endRaiseTimeStr;//结束募集时间 时间戳到秒
    private Double lastingClosedPeriod;//基金的封闭期  时间一到就可以分红了
    private Long manageFeeRate;//基金发行者对盈利部分的抽成  以1除以当前数的结果作为比例 例如当前述如果是10  则比例就是1/10  0.1  业绩提成比率
    private Long finalCapAmount; //最终募集金额
    public void setFundDetail(FundDetail fundDetail){
        this.fundAdvertising = fundDetail.getFundAdvertising();
        this.purchaseAmount = fundDetail.getPurchaseAmount();
        this.openday = DateUtil.longToString(Long.valueOf(fundDetail.getOpenday()),"yyyy-MM-dd");
        this.custodyUser = fundDetail.getCustodyUser();
        this.brokerCompany = fundDetail.getBrokerCompany();
        this.investAdviserCompany = fundDetail.getInvestAdviserCompany();
        this.investAdviserRegion = fundDetail.getInvestAdviserRegion();
        this.investAdviserFee = fundDetail.getInvestAdviserFee();
        this.startRaiseTime = fundDetail.getStartRaiseTime();
        this.endRaiseTime = fundDetail.getEndRaiseTime().toString().length() == 13 ?fundDetail.getEndRaiseTime():fundDetail.getEndRaiseTime()*1000;
        double v = (fundDetail.getLastingClosedPeriod()) / 2592000d;
        this.lastingClosedPeriod = Double.valueOf(v);
        this.manageFeeRate = fundDetail.getManageFeeRate();
        this.fundId = fundDetail.getId();
        this.fundCode = fundDetail.getFundCode();
        this.fundName = fundDetail.getFundName();
        this.issueUser = fundDetail.getUserId();
        this.finalCapAmount = fundDetail.getFinalCapAmount();
        if(fundDetail.getStartTime().toString().length() == 13) {
            this.startTime = fundDetail.getStartTime();
        }
        else{
            this.startTime = fundDetail.getStartTime()*1000;
        }

        this.startTimeStr = DateUtil.longToString(startTime,"yyyy-MM-dd");
        this.endRaiseTimeStr = DateUtil.longToString(endRaiseTime,"yyyy-MM-dd");
        this.fee = "1/"+fundDetail.getManageFeeRate();
    }
}
