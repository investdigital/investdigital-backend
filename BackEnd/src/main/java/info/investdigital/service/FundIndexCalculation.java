package info.investdigital.service;

import info.investdigital.common.DateUtil;
import info.investdigital.dao.DigitalCurrency.EthUsdMinRepo;
import info.investdigital.dao.FundStatisticalRepo;
import info.investdigital.entity.DigitalCurrency.EthUsdMin;
import info.investdigital.entity.FundPerformanceAnalysis;
import info.investdigital.entity.FundStatistical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huohuo
 * Created in 17:18  2018/3/9.
 */
@Service
public class FundIndexCalculation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private FundStatisticalRepo fundStatisticalRepo;
    @Resource
    private EthUsdMinRepo ethUsdMinRepo;
    //计算期间收益
    public Double duringEarningCalculation(List<FundStatistical> fundStatisticalList){
        Double v = null;
        try {
            v = 0d;
            if(fundStatisticalList != null && fundStatisticalList.size()>0){
                Double netAssetValue = fundStatisticalList.get(0).getNetAssetValue();
                Double netAssetValue1 = fundStatisticalList.get(fundStatisticalList.size() - 1).getNetAssetValue();
                v = (netAssetValue1 - netAssetValue) / netAssetValue1;
            }
        } catch (Exception e) {
            logger.error("Calculation duringEarning error:{}",e.getMessage(),e);
            return v;
        }
        return v;
    }
    //计算年化收益
    public Double annualEarningCalculation(Double duringEarning,Long startTime,Long endTime){
        Double annualEarning = null;
        try {
            startTime = startTime.toString().length()==13?startTime:startTime*1000;
            endTime = endTime.toString().length()==13?endTime:endTime*1000;
            Long dayNum = ( endTime - startTime ) / (1000 * 60 * 60 * 24);
            annualEarning = duringEarning / ( dayNum / 365 );
        } catch (Exception e) {
            logger.error("Calculation annualEarning error:{}",e.getMessage(),e);
            return annualEarning;
        }
        return annualEarning;

    }
    //计算年化超额收益
    public Double annualExcessEarningCalculation(Double annualEarning,Long startTime,Long endTime){
        double v1 = 0;
        try {
            startTime = startTime.toString().length()==13?startTime/1000:startTime;
            endTime = endTime.toString().length()==13?endTime/1000:endTime;
            List<EthUsdMin> ethUsdMinList = ethUsdMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqual(startTime, endTime);
            if(ethUsdMinList.size()>0){
                Double start = ethUsdMinList.get(0).getClose();
                Double close = ethUsdMinList.get(ethUsdMinList.size()-1).getClose();
                double v = (close - start) / start;
                Long dayNum = ( endTime - startTime ) / (60 * 60 * 24);
                Double ethAnnualEarning = v / ( dayNum / 365 );
                v1 = annualEarning - ethAnnualEarning;
            }
            return v1;
        } catch (Exception e) {
            logger.error("Calculation annualExcessEarning error:{}",e.getMessage(),e);
            return v1;
        }

    }
    //计算12个月滚动收益均值 最低值  最大值
    public void monthRollingEarningAvgCalculation(List<FundStatistical> fundStatisticalList,FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            Long startTime = fundStatisticalList.get(0).getTime();
            Long endTime  = fundStatisticalList.get(fundStatisticalList.size()-1).getTime();
            startTime = startTime.toString().length()==13?startTime:startTime*1000;
            endTime = endTime.toString().length()==13?endTime:endTime*1000;
            List<FundStatistical> timeList = new ArrayList<>();
            List<Double> earningList = new ArrayList<>();
            fundStatisticalList.stream().forEach(fundStatistical -> {
                if(timeList.size() == 0){
                    timeList.add(fundStatistical);
                }
                if(!DateUtil.longToString(fundStatistical.getTime(),"MM").equals(DateUtil.longToString(timeList.get(timeList.size()-1).getTime(),"MM"))){
                    timeList.add(fundStatistical);
                }
            });

            for (int i = 0;i<timeList.size();i++){
                if(i+1 != timeList.size()){
                    if(i+11 >timeList.size()){
                        Double v = ( timeList.get(i+11).getNetAssetValue() - timeList.get(i).getNetAssetValue() ) / timeList.get(i).getNetAssetValue();
                        earningList.add(v);
                    }
                    else{
                        Double v = ( timeList.get(timeList.size()-1).getNetAssetValue() - timeList.get(i).getNetAssetValue() ) / timeList.get(i).getNetAssetValue();
                        earningList.add(v);
                    }
                }
            }
            Double max = Collections.max(earningList);
            Double min = Collections.min(earningList);
            Double sum = 0d;
            for (Double v: earningList) {
                sum += v;
            }
            Double avg = sum/earningList.size();
            fundPerformanceAnalysis.setMonthRollingEarningAvg(avg);
            fundPerformanceAnalysis.setMonthRollingEarningMax(max);
            fundPerformanceAnalysis.setMonthRollingEarningLow(min);
        } catch (Exception e) {
            logger.error("Calculation monthRollingEarning error:{}",e.getMessage(),e);
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(Math.pow(4,2));
    }
    //计算波动性
    public Double volatilityCalculation(List<FundStatistical> fundStatisticalList){
        Double volatility = 0d;
        try {
            List<Double> doubleList = new ArrayList<>();
            int index = 0;
            for (int i = 0;i<fundStatisticalList.size();i++){
                if(i == 0){
                    doubleList.add(fundStatisticalList.get(i).getThisDayEarning());
                }else{
                    index += 1440;
                    if(fundStatisticalList.size()>index){
                        doubleList.add(fundStatisticalList.get(index).getThisDayEarning());
                    }
                }
            }
            Double num = 0d;
            for (Double v : doubleList) {
                num += v;
            }
            Double avg = num/doubleList.size();
            Double snum = 0d;
            for (Double v : doubleList) {
                snum += Math.pow((v - avg),2);
            }
            Double sdt = Math.sqrt(snum/doubleList.size());

            volatility = sdt * Math.sqrt(365);
        } catch (Exception e) {
            logger.error("Calculation volatility error:{}",e.getMessage(),e);
            return volatility;
        }
        return volatility;
    }
    //计算最大回撤 和最大时间段
    public void maxRetracementCalculation(List<FundStatistical> fundStatisticalList,FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            List<FundStatistical> doubleList = new ArrayList<>();
            int index = 0;
            for (int i = 0;i<fundStatisticalList.size();i++){
                if(i == 0){
                    doubleList.add(fundStatisticalList.get(i));
                }else{
                    index += 1440;
                    if(fundStatisticalList.size()>index){
                        doubleList.add(fundStatisticalList.get(index));
                    }
                }
            }
            Double retracement = 0d;
            FundStatistical startFundStatistical = null;
            FundStatistical endFundStatistical = null;
            for (int i = 0;i<doubleList.size();i++){
                int indexs = 1;
                while (i + indexs < doubleList.size()){
                    FundStatistical fundStatisticali = doubleList.get(i);
                    FundStatistical fundStatisticalj = doubleList.get(i + index);
                    double v = (fundStatisticali.getNetAssetValue() - fundStatisticalj.getNetAssetValue()) / fundStatisticali.getNetAssetValue();
                    if(v > retracement){
                        retracement = v;
                        startFundStatistical = fundStatisticali;
                        endFundStatistical = fundStatisticalj;
                    }
                }
            }
            fundPerformanceAnalysis.setMaxRetracement(retracement);
            fundPerformanceAnalysis.setMaxRetracementTime(this.getTime(startFundStatistical.getTime())+"-"+this.getTime(endFundStatistical.getTime()));
        } catch (Exception e) {
            logger.error("Calculation maxRetracement error:{}",e.getMessage(),e);
        }

    }
    private String getTime(Long timeStmap){
        String s = null;
        if(timeStmap != null){
            timeStmap = timeStmap.toString().length() == 13?timeStmap:timeStmap*1000;
            s = DateUtil.longToString(timeStmap, "yyyy-MM-dd");
        }
        return s;
    }
    //计算sharpe
    public Double sharpeCalculation(FundPerformanceAnalysis fundPerformanceAnalysis){
        Double v = null;
        try {
            Double annualEarning = fundPerformanceAnalysis.getAnnualEarning();
            Double rf = 0.0175;
            Double volatility = fundPerformanceAnalysis.getVolatility();
            v = ( annualEarning - rf ) / volatility;
        } catch (Exception e) {
            logger.error("Calculation sharpe error:{}",e.getMessage(),e);
            return v;
        }
        return v;

    }
    //计算索提诺比率
    public Double sortinoCalculation(List<FundStatistical> fundStatisticalList,FundPerformanceAnalysis fundPerformanceAnalysis){
        Double sortino = 0d;
        try {
            List<Double> doubleList = new ArrayList<>();
            int index = 0;
            for (int i = 0;i<fundStatisticalList.size();i++){
                if(i == 0){
                    doubleList.add(fundStatisticalList.get(i).getThisDayEarning());
                }else{
                    index += 1440;
                    if(fundStatisticalList.size()>index){
                        doubleList.add(fundStatisticalList.get(index).getThisDayEarning());
                    }
                }
            }
            Double num = 0d;
            for (Double v : doubleList) {
                num += v;
            }
            Double avg = num/doubleList.size();

            List<Double> doubleList1 = doubleList.stream().filter(aDouble -> aDouble < avg).collect(Collectors.toList());
            Double snum = 0d;
            for (Double v : doubleList1) {
                snum += Math.pow((v - avg),2);
            }
            Double sdt = snum/doubleList1.size();
            Double volatility = sdt * Math.sqrt(365);
            Double annualEarning = fundPerformanceAnalysis.getAnnualEarning();
            Double rf = 0.0175;

            sortino = ( annualEarning - rf ) / volatility;
        } catch (Exception e) {
            logger.error("Calculation volatility error:{}",e.getMessage(),e);
            return sortino;
        }
        return sortino;
    }

    //计算贝塔系数 Beta=Cov（基金收益，业绩基准收益）/基金收益标准差
    //Cov(X，Y)=E(XY)-E(X)E(Y)。
    public Double betaCalculation(List<FundStatistical> fundStatisticalList){
        Double vv = 0d;
        try {
            if(fundStatisticalList != null && fundStatisticalList.size()>0){
                Long startTime = fundStatisticalList.get(0).getTime();
                Long endTime = fundStatisticalList.get(fundStatisticalList.size()-1).getTime();

                startTime = startTime.toString().length() == 13? startTime/1000:startTime;
                endTime = endTime.toString().length() == 13 ? endTime/1000:endTime;
                List<EthUsdMin> ethUsdMinList = ethUsdMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqual(startTime, endTime);
               //基金收益率
                List<Double> fundEaring = new ArrayList<>();
                //业绩基准增长率
                List<Double>  benchmarkEaring = new ArrayList<>();

                for (int i =1; i<fundStatisticalList.size();i++){
                    double v = (fundStatisticalList.get(i).getNetAssetValue() - fundStatisticalList.get(i - 1).getNetAssetValue()) / fundStatisticalList.get(i - 1).getNetAssetValue();
                    double v1 = (ethUsdMinList.get(i).getClose() - ethUsdMinList.get(i - 1).getClose()) / ethUsdMinList.get(i - 1).getClose();
                    fundEaring.add(v);
                    benchmarkEaring.add(v1);
                }
                Double avg1 = 0d;
                Double num1=0d;
                Double avg2 = 0d;
                Double num2=0d;
                for(Double d:fundEaring){
                    num1 += d;
                }
                for(Double d:benchmarkEaring){
                    num2 += d;
                }
                //基金收益平均值
                avg1 = num1 / fundEaring.size();//E(X)
                //业绩基准平均值
                avg2 = num2 / benchmarkEaring.size();//E(Y)

                Double avg3 = 0d;
                Double num3 = 0d;

                for(int i = 0 ; i <fundEaring.size();i++){
                    num3 +=  fundEaring.get(i) * benchmarkEaring.get(i) ;
                }
                avg3 = num3 / fundEaring.size(); //E(XY)

                double v1 = avg3 - avg1 * avg2; //协方差

                List<Double> doubleList = new ArrayList<>();
                int index = 0;
                for (int i = 0;i<fundStatisticalList.size();i++){
                    if(i == 0){
                        doubleList.add(fundStatisticalList.get(i).getThisDayEarning());
                    }else{
                        index += 1440;
                        if(fundStatisticalList.size()>index){
                            doubleList.add(fundStatisticalList.get(index).getThisDayEarning());
                        }
                    }
                }
                Double num = 0d;
                for (Double v : doubleList) {
                    num += v;
                }
                Double avg = num/doubleList.size();
                Double snum = 0d;
                for (Double v : doubleList) {
                    snum += Math.pow((v - avg),2);
                }
                Double sdt = snum/doubleList.size();
                vv = v1 / sdt; //贝塔系数
            }
            return vv;
        } catch (Exception e) {
            logger.error("Calculation bate error:{}",e.getMessage(),e);
            return vv;
        }
    }
    //计算相关系数
    public Double biggestFactorCalculation(List<FundStatistical> fundStatisticalList){
        Double vv = 0d;
        try {
            if(fundStatisticalList != null && fundStatisticalList.size()>0){
                // 基金净值和业绩基准的的 协方差
                Long startTime = fundStatisticalList.get(0).getTime();
                Long endTime = fundStatisticalList.get(fundStatisticalList.size()-1).getTime();

                startTime = startTime.toString().length() == 13? startTime/1000:startTime;
                endTime = endTime.toString().length() == 13 ? endTime/1000:endTime;
                List<EthUsdMin> ethUsdMinList = ethUsdMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqual(startTime, endTime);
                List<Double> fundEaring = new ArrayList<>();
                List<Double>  benchmarkEaring = new ArrayList<>();

                for (int i =1; i<fundStatisticalList.size();i++){
                    double v = (fundStatisticalList.get(i).getNetAssetValue() - fundStatisticalList.get(i - 1).getNetAssetValue()) / fundStatisticalList.get(i - 1).getNetAssetValue();
                    double v1 = (ethUsdMinList.get(i).getClose() - ethUsdMinList.get(i - 1).getClose()) / ethUsdMinList.get(i - 1).getClose();
                    fundEaring.add(v);
                    benchmarkEaring.add(v1);
                }
                Double avg1 = 0d;
                Double num1=0d;
                Double avg2 = 0d;
                Double num2=0d;
                for(Double d:fundEaring){
                    num1 += d;
                }
                for(Double d:benchmarkEaring){
                    num2 += d;
                }
                avg1 = num1 / fundEaring.size();
                avg2 = num2 / benchmarkEaring.size();
                Double avg3 = 0d;
                Double num3 = 0d;

                for(int i = 0 ; i <fundEaring.size();i++){
                    num3 +=  fundEaring.get(i) * benchmarkEaring.get(i) ;
                }
                avg3 = num3 / fundEaring.size(); //E(XY)

                double v1 = avg3 - avg1 * avg2; //协方差 //基金收益和业绩基准的协方差

                //基金收益方差
                List<Double> doubleList = new ArrayList<>();
                Double snum = 0d;
                for (Double v : doubleList) {
                    snum += Math.pow((v - avg1),2);
                }
                Double varX = snum/doubleList.size();//基金收益方差

                //计算业绩基准方差
                List<Double> doubleList1 = new ArrayList<>();
                Double num4 = 0d;
                for (Double dou: benchmarkEaring) {
                    num4 += Math.pow((dou - avg2),2);
                }
                double varY = num4 / benchmarkEaring.size();//基准收益方差

                 vv = v1 / Math.sqrt(varX * varY);
            }
            return vv;
        } catch (Exception e) {
            logger.error("Calculation biggestFactor error:{}",e.getMessage(),e);
            return vv;
        }
    }

}
