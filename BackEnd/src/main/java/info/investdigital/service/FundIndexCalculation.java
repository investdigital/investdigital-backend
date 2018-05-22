package info.investdigital.service;

import info.investdigital.common.DateUtil;
import info.investdigital.common.JsonUtil;
import info.investdigital.common.ResourceParam;
import info.investdigital.entity.DigitalCurrency.BenchmarkO;
import info.investdigital.entity.DigitalCurrency.IndustryBenchmark;
import info.investdigital.entity.fund.FundPerformanceAnalysis;
import info.investdigital.entity.fund.FundStatistical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huohuo
 * Created in 17:18  2018/3/9.
 */
@Service
public class FundIndexCalculation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static Double RF =  0.0175;
    @Resource
    private DigitalService digitalService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourceParam resourceParam;
    @Resource
    private FundService fundService;
   //根据时间获取动态的eth指数
    public FundPerformanceAnalysis getEthFundPerformanceAnalysis(IndustryBenchmark industryBenchmark,Long startTime,Long endTime){
        FundPerformanceAnalysis fundPerformanceAnalysis = new FundPerformanceAnalysis();
        try {
            if(null == industryBenchmark){
                industryBenchmark = digitalService.getIndustryBenchmarkMin(startTime, endTime);
            }
            List<Double> ethHour = this.getEthHourNetAssetValue(industryBenchmark.getBenchmarkOS());
            fundPerformanceAnalysis.setStartTime(startTime);
            fundPerformanceAnalysis.setEndTime(endTime);
            //期间收益
            fundPerformanceAnalysis.setDuringEarning(industryBenchmark.getDuringEarning());
            //年化收益
            fundPerformanceAnalysis.setAnnualEarning(industryBenchmark.getAnnualEarning());
            //计算年化超额收益
            fundPerformanceAnalysis.setAnnualExcessEarning(0d);
            //12个月滚动收益均值 最低值  最大值 +
            this.monthRollingEarningAvgCalculationEth(industryBenchmark.getBenchmarkOS(),fundPerformanceAnalysis);
            //计算波动性
            Double volatility = this.volatilityCalculation(ethHour);
            fundPerformanceAnalysis.setVolatility(volatility);
            //最大回撤 和最大回撤时间段
            this.maxRetracementCalculationEth(industryBenchmark.getBenchmarkOS(),fundPerformanceAnalysis);
            //最大增长率 和最大增长的时间段
            this.maxGrowthCalculationEth(industryBenchmark.getBenchmarkOS(),fundPerformanceAnalysis);
            //计算夏普比率
            Double sharpe = this.sharpeCalculation(fundPerformanceAnalysis);
            fundPerformanceAnalysis.setSharpe(sharpe);
            //计算索诺提比率
            Double sortino = this.sortinoCalculation(ethHour, fundPerformanceAnalysis);
            fundPerformanceAnalysis.setSortino(sortino);
            //beta系数
            fundPerformanceAnalysis.setBeta(1d);
            //相关系数
            fundPerformanceAnalysis.setBiggestFactor(1d);
        }
        catch (Exception e) {
            logger.error("get eth fundPerformanceAnalysis faild:{}",e.getMessage(),e);
            return fundPerformanceAnalysis;
        }
        return fundPerformanceAnalysis;
    }
    public void putEthIndexToRedis(IndustryBenchmark industryBenchmark, Long fundCode,Long starttime,Long endTime){
        try {
            HashOperations<String, String, String> stringHashOperations = stringRedisTemplate.opsForHash();
            FundPerformanceAnalysis ethFundPerformanceAnalysis = this.getEthFundPerformanceAnalysis(industryBenchmark, starttime, endTime);
            stringHashOperations.put(resourceParam.getEthIndexHashKey(),fundCode.toString(), JsonUtil.toJson(ethFundPerformanceAnalysis));
        } catch (Exception e) {
            logger.error("putEthIndexToRedis error :{}",e.getMessage(),e);
        }
    }
    //计算基金指数 根据基金的数据
    public FundPerformanceAnalysis getFundPerformanceAnalysis(List<FundStatistical> fundStatisticalList,FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            Long startTime = DateUtil.getHighTime(fundStatisticalList.get(0).getTime());
            Long endTime = DateUtil.getHighTime(fundStatisticalList.get(fundStatisticalList.size() - 1).getTime());
            //获取基金计算时间段的  eth基准 数据
            IndustryBenchmark industryBenchmark = digitalService.getIndustryBenchmarkMin(startTime, endTime);
            fundPerformanceAnalysis.setStartTime(startTime);
            fundPerformanceAnalysis.setEndTime(endTime);
            //获取基金小时的收益率
            List<Double> fundDayNetAssetValue = this.getFundHourEaring(fundStatisticalList);
            //获取所有的净值
            List<Double> collect = fundStatisticalList.stream().map(FundStatistical::getNetAssetValue).collect(Collectors.toList());
            //期间收益 +
            Double duringEarning = this.duringEarningCalculation(collect);
            fundPerformanceAnalysis.setDuringEarning(duringEarning);
            //年化收益 +
            Double annualEarning = this.annualEarningCalculation(duringEarning,startTime , endTime);
            fundPerformanceAnalysis.setAnnualEarning(annualEarning);
            //年化超额收益 +
            Double annualExcess = this.annualExcessEarningCalculation(annualEarning,industryBenchmark.getAnnualEarning());
            fundPerformanceAnalysis.setAnnualExcessEarning(annualExcess);

            //12个月滚动收益均值 最低值  最大值 +
            this.monthRollingEarningAvgCalculation(fundStatisticalList,fundPerformanceAnalysis);
            //波动性 +
            Double volatility = this.volatilityCalculation(fundDayNetAssetValue);
            fundPerformanceAnalysis.setVolatility(volatility);
            //最大回撤 和最大时间段 +
            this.maxRetracementCalculation(fundStatisticalList,fundPerformanceAnalysis);
            //最大增长率 和最大增长的时间段
            this.maxGrowthCalculation(fundStatisticalList,fundPerformanceAnalysis);
            //sharpe +
            Double sharpe = this.sharpeCalculation(fundPerformanceAnalysis);
            fundPerformanceAnalysis.setSharpe(sharpe);
            ////计算索提诺比率
            Double sortino = this.sortinoCalculation(fundDayNetAssetValue, fundPerformanceAnalysis);
            fundPerformanceAnalysis.setSortino(sortino);
            //计算贝塔系数 和相关系数
            if(industryBenchmark.getBenchmarkEarning() != null){
                List<Double> ethHour = this.getEthHourNetAssetValue(industryBenchmark.getBenchmarkOS());
                if(fundDayNetAssetValue.size() >2){
                    Double beta = this.betaCalculation(fundDayNetAssetValue,ethHour);
                    fundPerformanceAnalysis.setBeta(beta);
                    Double biggestFactor = this.biggestFactorCalculation(fundDayNetAssetValue,ethHour);
                    fundPerformanceAnalysis.setBiggestFactor(biggestFactor);
                }
            }
            //将此基金对应的 基准 指数 存入redis
            this.putEthIndexToRedis(industryBenchmark,fundPerformanceAnalysis.getFundCode(),startTime,endTime);
            return fundPerformanceAnalysis;
        } catch (Exception e) {
            logger.error("getFundPerformanceAnalysis error :{}",e.getMessage(),e);
            return fundPerformanceAnalysis;
        }
    }

    //计算期间收益
    public Double duringEarningCalculation(List<Double> doubleList){
        Double v = 0d;
        try {
            if(doubleList != null && doubleList.size()>0){
                double netAssetValue = doubleList.get(0);
                double netAssetValue1 = doubleList.get(doubleList.size() - 1);
                v = netAssetValue != 0 ? (netAssetValue1 - netAssetValue) / netAssetValue : 0d;
            }
        } catch (Exception e) {
            logger.error("Calculation duringEarning error:{}",e.getMessage(),e);
            return v;
        }
        return v;
    }
    //计算年化收益
    public Double annualEarningCalculation(Double duringEarning,Long startTime,Long endTime){
        Double annualEarning = 0d;
        try {
            startTime = DateUtil.getLowTime(startTime);
            endTime = DateUtil.getLowTime(endTime);
            double dayNum = ( endTime - startTime ) / 86400d;
            double yearNum = dayNum / 365;
            annualEarning = yearNum == 0 ? 0d:duringEarning / yearNum;;
        } catch (Exception e) {
            logger.error("Calculation annualEarning error:{}",e.getMessage(),e);
            return annualEarning;
        }
        return annualEarning > -1 ? annualEarning : -1;
    }
    //计算年化超额收益
    public Double annualExcessEarningCalculation(Double annualEarning,Double industryBenchmarkAnnualEarning){
        double v1 = 0;
        try {
            v1 = industryBenchmarkAnnualEarning != null ? annualEarning - industryBenchmarkAnnualEarning : 0d;
            return v1 > -1 ? v1 : -1;
        } catch (Exception e) {
            logger.error("Calculation annualExcessEarning error:{}",e.getMessage(),e);
            return v1 > -1 ? v1 : -1;
        }
    }
    //计算12个月滚动收益均值 最低值  最大值
    public void monthRollingEarningAvgCalculation(List<FundStatistical> fundStatisticalList,FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            //每个月的净值
            List<Double> fundMonthNetAssetValue = this.getFundMonthNetAssetValue(fundStatisticalList);
            if(fundMonthNetAssetValue  == null && fundMonthNetAssetValue.size()>2){
                //每个月的收益率
                List<Double> earningList = this.getEarningYield(fundMonthNetAssetValue);
                //每个月的滚动收益计算
                List<Double> rollingEarningList = new ArrayList<>();
                for(int i = 0 ; i< earningList.size();i++ ){
                    Double avg = 0d;
                    if( i+11 < earningList.size()){
                        Double num = 0d;
                        for(int j = 0;j<=11;j++){
                            num += earningList.get(i+j);
                        }
                        avg = num / 12;
                    }
                    else{
                        Double num = 0d;
                        for(int j = 0;j + i < earningList.size();j++){
                            num += earningList.get(i+j);
                        }
                        avg = num / earningList.size() - i;
                    }
                    rollingEarningList.add(avg);
                }
                Double max = Collections.max(earningList);
                Double min = Collections.min(earningList);
                Double average = earningList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                fundPerformanceAnalysis.setMonthRollingEarningAvg(average > -1 ? average : -1);
                fundPerformanceAnalysis.setMonthRollingEarningMax(max > -1 ? max : -1);
                fundPerformanceAnalysis.setMonthRollingEarningLow(min > -1 ? min : -1);
            }
            else {
                fundPerformanceAnalysis.setMonthRollingEarningAvg(0d);
                fundPerformanceAnalysis.setMonthRollingEarningMax(0d);
                fundPerformanceAnalysis.setMonthRollingEarningLow(0d);
            }
        } catch (Exception e) {
            logger.error("Calculation monthRollingEarning error:{}",e.getMessage(),e);
        }
    }
    //计算12个月滚动收益均值 最低值  最大值
    public void monthRollingEarningAvgCalculationEth(List<BenchmarkO> ethUsdMinList, FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            //每个月的净值
            List<Double> ethMonthNetAssetValue = this.getETHMonthNetAssetValue(ethUsdMinList);
            //每个月的收益率
            List<Double> earningList = this.getEarningYield(ethMonthNetAssetValue);
            //每个月的滚动收益计算
            List<Double> rollingEarningList = new ArrayList<>();
            for(int i = 0 ; i< earningList.size();i++ ){
                Double avg = 0d;
                if( i+11 < earningList.size()){
                    Double num = 0d;
                    for(int j = 0;j<=11;j++){
                        num += earningList.get(i+j);
                    }
                    avg = num / 12;
                }
                else{
                    Double num = 0d;
                    for(int j = 0;j + i < earningList.size();j++){
                        num += earningList.get(i+j);
                    }
                    avg = num / earningList.size() - i;
                }
                rollingEarningList.add(avg);
            }

            Double max = 0d;
            Double min = 0d;
            Double average = 0d;
            if(null != earningList && earningList.size()>0){
                max = Collections.max(earningList);
                min = Collections.min(earningList);
                average = earningList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            }
            fundPerformanceAnalysis.setMonthRollingEarningAvg(average > -1 ? average : -1);
            fundPerformanceAnalysis.setMonthRollingEarningMax(max > -1 ? max : -1);
            fundPerformanceAnalysis.setMonthRollingEarningLow(min > -1 ? min : -1);

        } catch (Exception e) {
            logger.error("Calculation monthRollingEarningAvgCalculationEth error:{}",e.getMessage(),e);
        }
    }
    //计算波动性
    public Double volatilityCalculation(List<Double> doubleList){
        Double volatility = 0d;
        try {
            Double sdt = this.sdt(doubleList);
            volatility = sdt * Math.sqrt(24*365);
        } catch (Exception e) {
            logger.error("Calculation volatility error:{}",e.getMessage(),e);
            return volatility;
        }
        return volatility;
    }
    //计算波动性
    public Double volatilityDayCalculation(List<Double> doubleList){
        Double volatility = 0d;
        try {
            Double sdt = this.sdt(doubleList);
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
            Double retracement = -1000d;
            FundStatistical startFundStatistical = null;
            FundStatistical endFundStatistical = null;
            for (int i = 0;i<fundStatisticalList.size();i++){
                int indexs = 1;
                while (i + indexs < fundStatisticalList.size()){
                    FundStatistical fundStatisticali = fundStatisticalList.get(i);
                    FundStatistical fundStatisticalj = fundStatisticalList.get(i + indexs);
                    double netAssetValuei = fundStatisticali.getNetAssetValue();
                    double netAssetValuej = fundStatisticalj.getNetAssetValue();
                        double v =  netAssetValuei != 0 ? (netAssetValuei - netAssetValuej) / netAssetValuei : 0d;
                        if(v > retracement){
                            retracement = v;
                            startFundStatistical = fundStatisticali;
                            endFundStatistical = fundStatisticalj;
                        }
                    indexs++;
                }
            }
            fundPerformanceAnalysis.setMaxRetracement(retracement);
            fundPerformanceAnalysis.setMaxRetracementTime(this.getTime(startFundStatistical.getTime())+" -- "+this.getTime(endFundStatistical.getTime()));
        } catch (Exception e) {
            logger.error("Calculation maxRetracement error:{}",e.getMessage(),e);
        }
    }
    //计算最大增长 和最大时间段
    public void maxGrowthCalculation(List<FundStatistical> fundStatisticalList,FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            Double retracement = -1000d;
            FundStatistical startFundStatistical = null;
            FundStatistical endFundStatistical = null;
            for (int i = 0;i<fundStatisticalList.size();i++){
                int indexs = 1;
                FundStatistical fundStatisticali = fundStatisticalList.get(i);
                double netAssetValuei = fundStatisticali.getNetAssetValue();
                FundStatistical fundStatisticalj = null;
                double netAssetValuej = 0d;
                while (i + indexs < fundStatisticalList.size()){
                    fundStatisticalj = fundStatisticalList.get(i + indexs);
                    netAssetValuej = fundStatisticalj.getNetAssetValue();
                    double v =  netAssetValuei != 0 ? (netAssetValuej - netAssetValuei) / netAssetValuei : 0d;
                    if(v > retracement){
                        retracement = v;
                        startFundStatistical = fundStatisticali;
                        endFundStatistical = fundStatisticalj;
                    }
                    indexs++;
                }
            }
            fundPerformanceAnalysis.setMaxGrowth(retracement);
            fundPerformanceAnalysis.setMaxGrowthTime(this.getTime(startFundStatistical.getTime())+" -- "+this.getTime(endFundStatistical.getTime()));
        } catch (Exception e) {
            logger.error("Calculation maxRetracement error:{}",e.getMessage(),e);
        }
    }
    //计算 基准 最大回撤 和最大时间段
    public void maxRetracementCalculationEth(List<BenchmarkO> ethUsdtDays, FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            Double retracement = -1000d;
            BenchmarkO startFundStatistical = null;
            BenchmarkO endFundStatistical = null;

            for (int i = 0;i<ethUsdtDays.size();i++){
                int indexs = 1;
                while (i + indexs < ethUsdtDays.size()){
                    BenchmarkO fundStatisticali = ethUsdtDays.get(i);
                    BenchmarkO fundStatisticalj = ethUsdtDays.get(i + indexs);
                    double netAssetValuei = fundStatisticali.getClose();
                    double netAssetValuej = fundStatisticalj.getClose();
                    double v =  netAssetValuei != 0 ? (netAssetValuei - netAssetValuej) / netAssetValuei : 0d;
                        if(v > retracement){
                            retracement = v;
                            startFundStatistical = fundStatisticali;
                            endFundStatistical = fundStatisticalj;
                        }
                    indexs++;
                }
            }
            fundPerformanceAnalysis.setMaxRetracement(retracement);
            fundPerformanceAnalysis.setMaxRetracementTime(this.getTime(startFundStatistical.getId())+" -- "+this.getTime(endFundStatistical.getId()));
        } catch (Exception e) {
            logger.error("Calculation maxRetracement error:{}",e.getMessage(),e);
        }
    }
    //计算 基准 最大增长 和最大时间段
    public void maxGrowthCalculationEth(List<BenchmarkO> ethUsdtDays,FundPerformanceAnalysis fundPerformanceAnalysis){
        try {
            if(ethUsdtDays == null){
                fundPerformanceAnalysis.setMaxGrowth(0D);
                fundPerformanceAnalysis.setMaxGrowthTime(this.getTime(System.currentTimeMillis())+" -- "+this.getTime(System.currentTimeMillis()));
                return;
            }
            Double retracement = -1000d;
            BenchmarkO startFundStatistical = null;
            BenchmarkO endFundStatistical = null;
            for (int i = 0;i<ethUsdtDays.size();i++){
                BenchmarkO fundStatisticali = ethUsdtDays.get(i);
                double netAssetValuei = fundStatisticali.getClose();
                BenchmarkO fundStatisticalj = null;
                double netAssetValuej = 0d;
                int indexs = 1;
                while (i + indexs < ethUsdtDays.size()){
                    fundStatisticalj = ethUsdtDays.get(i + indexs);
                    netAssetValuej = fundStatisticalj.getClose();
                    double v =  netAssetValuei != 0 ? (netAssetValuej - netAssetValuei) / netAssetValuei : 0d;
                    if(v > retracement){
                        retracement = v;
                        startFundStatistical = fundStatisticali;
                        endFundStatistical = fundStatisticalj;
                    }
                    indexs++;
                }
            }
            fundPerformanceAnalysis.setMaxGrowth(retracement);
            fundPerformanceAnalysis.setMaxGrowthTime(this.getTime(startFundStatistical.getId())+" -- "+this.getTime(endFundStatistical.getId()));
        } catch (Exception e) {
            logger.error("Calculation maxRetracement error:{}",e.getMessage(),e);
        }
    }

    //根据时间戳获取相应的时间yyyy-MM-dd
    private String getTime(Long timeStmap){
        String s = null;
        if(timeStmap != null){
            timeStmap = DateUtil.getHighTime(timeStmap);
            s = DateUtil.longToString(timeStmap, "yyyy-MM-dd");
        }
        return s;
    }
    //计算sharpe
    public Double sharpeCalculation(FundPerformanceAnalysis fundPerformanceAnalysis){
        Double v = 0d;
        try {
            Double annualEarning = fundPerformanceAnalysis.getAnnualEarning();
            Double volatility = fundPerformanceAnalysis.getVolatility();
            v = volatility != 0 ? ( annualEarning - RF ) / volatility : 0d;
        } catch (Exception e) {
            logger.error("Calculation sharpe error:{}",e.getMessage(),e);
            return v;
        }
        return v;

    }
    //计算索提诺比率
    public Double sortinoCalculation(List<Double> doubleList,FundPerformanceAnalysis fundPerformanceAnalysis){
        Double sortino = 0d;
        try {
            //收益率
            Double avg = doubleList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            List<Double> doubleList2 = doubleList.stream().filter(aDouble -> aDouble < avg).collect(Collectors.toList());
            Double sdt = this.sdt(doubleList2);
            Double volatility = sdt * Math.sqrt(24*365);
            Double annualEarning = fundPerformanceAnalysis.getAnnualEarning();
            sortino = volatility != 0 ? ( annualEarning - RF ) / volatility : 0d;
        } catch (Exception e) {
            logger.error("Calculation volatility error:{}",e.getMessage(),e);
            return sortino;
        }
        return sortino;
    }
    //计算贝塔系数 Beta=Cov（基金收益，业绩基准收益）/基金收益标准差
    //Cov(X，Y)=E(XY)-E(X)E(Y)。
    public Double betaCalculation(List<Double> fundStatisticalList, List<Double> benchmarkEaring){
        Double vv = 0d;
        try {
            if(fundStatisticalList == null || fundStatisticalList.size()==0){
                return vv;
            }
            while(fundStatisticalList.size() > benchmarkEaring.size()){
                benchmarkEaring.add(benchmarkEaring.get(benchmarkEaring.size()-1));
            }
            //基准收益率
            List<Double> earningYield = this.getEarningYield(benchmarkEaring);
            //基金收益率
            List<Double> fundEaring = this.getEarningYield(fundStatisticalList);
            double cov = this.getCov(fundEaring, earningYield);
            double sdt = this.sdt(fundEaring);
            vv = sdt != 0 ? cov / sdt : 0d; //贝塔系数
            return vv;
        } catch (Exception e) {
            logger.error("Calculation bate error:{}",e.getMessage(),e);
            return vv;
        }
    }
    //计算相关系数
    public Double biggestFactorCalculation(List<Double> fundStatisticalList,List<Double> netWorth){
        Double vv = 0d;
        try {
            if(fundStatisticalList == null || fundStatisticalList.size()==0){
                return vv;
             }
            while(fundStatisticalList.size() > netWorth.size()){
                netWorth.add(netWorth.get(netWorth.size()-1));
            }
            Double cov = this.getCov(fundStatisticalList, netWorth);
            //基金收益方差
            double varFund = this.var(fundStatisticalList);
            double varEth = this.var(netWorth);
            vv = Math.sqrt(varFund * varEth) == 0 ? 0 : cov / Math.sqrt(varFund * varEth);
            return vv;
        } catch (Exception e) {
            logger.error("Calculation biggestFactor error:{}",e.getMessage(),e);
            return vv;
        }
    }
    //计算协方差
    private Double getCov(List<Double> aList,List<Double> bList)throws Exception{
        double cov = 0d;
        try {
            Double aAvg = aList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            Double bAvg = bList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            Double num = 0d;
            for(int i = 0 ; i <aList.size();i++){
                num +=  aList.get(i) * bList.get(i) ;
            }
            Double avg3 = aList.size() != 0 ? num / aList.size():0d;
            cov = avg3 - aAvg * bAvg; //协方差
            return cov;
        } catch (Exception e) {
            logger.error("getCov error :{}",e.getMessage(),e);
            return cov;
        }
    };
    //计算收益率
    private List<Double> getEarningYield(List<Double> doubleList) throws Exception{
        List<Double> earningList = new ArrayList<>(doubleList.size()-1);
        for (int i = 1; i< doubleList.size(); i++){
            double a = doubleList.get(i);
            double b = doubleList.get(i - 1);
            double yield = b != 0 ? (a-b)/b : 0d;
            earningList.add(yield);
        }
        return earningList;
    }
    //根据基金的数据获取月数据的净值
    private List<Double> getFundMonthNetAssetValue(List<FundStatistical> fundStatisticalList)throws Exception{
        List<FundStatistical> timeList =  new ArrayList<>();
        List<Double> netAssetValueList =  null;
        try {
            fundStatisticalList.stream().forEach(fundStatistical -> {
                if(timeList.size() == 0){
                    timeList.add(fundStatistical);
                }
                else{
                    if(!DateUtil.longToString(fundStatistical.getTime(),"MM").equals(DateUtil.longToString(timeList.get(timeList.size()-1).getTime(),"MM"))){
                        timeList.add(fundStatistical);
                    }
                }
            });
            timeList.add(fundStatisticalList.get(fundStatisticalList.size()-1));
            netAssetValueList = timeList.stream().map(FundStatistical::getNetAssetValue).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("getFundMonthNetAssetValue error:{}",e.getMessage(),e);
            return netAssetValueList;
        }
        return netAssetValueList;
    }
    //根据基金的数据获取小时数据的收益
    private List<Double> getFundHourEaring(List<FundStatistical> fundStatisticalList)throws Exception{
        FundStatistical fundStatistical = fundStatisticalList.get(0);
        Double begin = fundStatistical.getNetAssetValue();
        if(begin == 0){
            return null;
        }
        List<Double> netAssetValueList =  new ArrayList<>();
        try {
            for(int i =0;i<fundStatisticalList.size();i+=12){
                Double netAssetValue = fundStatisticalList.get(i).getNetAssetValue()/begin;
                netAssetValueList.add(netAssetValue);
            }
        } catch (Exception e) {
            logger.error("getFundMonthNetAssetValue error:{}",e.getMessage(),e);
            return netAssetValueList;
        }
        return netAssetValueList;
    }
    //根据基金的数据获取小时数据的收益
    private List<Double> getEaring(List<Double> doubleList)throws Exception{
        Double begin = doubleList.get(0);
        if(begin == 0){
            return null;
        }
        List<Double> netAssetValueList =  new ArrayList<>();
        try {
            for(int i =0;i<doubleList.size();i+=12){
                Double netAssetValue = doubleList.get(i)/begin;
                netAssetValueList.add(netAssetValue);
            }
        } catch (Exception e) {
            logger.error("getFundMonthNetAssetValue error:{}",e.getMessage(),e);
            return netAssetValueList;
        }
        return netAssetValueList;
    }
    //根据的数据获取小时数据的净值
    private List<Double> getEthHourNetAssetValue(List<BenchmarkO> benchmarkOS)throws Exception{
        Double close = benchmarkOS.get(0).getClose();
        if(close == 0){
            return null;
        }
        List<Double> netAssetValueList = new ArrayList<>();
        try {
            for(int i =0;i<benchmarkOS.size();i+=12){
                Double netAssetValue = benchmarkOS.get(i).getClose()/close;
                netAssetValueList.add(netAssetValue);
            }
            return netAssetValueList;
        } catch (Exception e) {
            logger.error("getFundMonthNetAssetValue error:{}",e.getMessage(),e);
            return netAssetValueList;
        }
    }
    //根据基金的数据获取年数据
    public List<FundStatistical> getFundYear(List<FundStatistical> fundStatisticalList){
        List<FundStatistical> timeList =  new ArrayList<>();
        try {
            fundStatisticalList.stream().forEach(fundStatistical -> {
                if(timeList.size() == 0){
                    timeList.add(fundStatistical);
                }
                else{
                    if(!DateUtil.longToString(fundStatistical.getTime(),"yyyy").equals(DateUtil.longToString(timeList.get(timeList.size()-1).getTime(),"yyyy"))){
                        timeList.add(fundStatistical);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("getFundMonthNetAssetValue error:{}",e.getMessage(),e);
            return timeList;
        }
        return timeList;
    }
    //根据eth的分钟数据获取月数据
    private List<Double> getETHMonthNetAssetValue(List<BenchmarkO> ethUsdMinList){
        List<BenchmarkO> timeList =  new ArrayList<>();
        List<Double> netAssetValueList =  null;
        try {
            ethUsdMinList.stream().forEach(ethUsdMin -> {
                if(timeList.size() == 0){
                    timeList.add(ethUsdMin);
                }
                else{
                    if(!DateUtil.longToString(ethUsdMin.getId(),"MM").equals(DateUtil.longToString(timeList.get(timeList.size()-1).getId(),"MM"))){
                        timeList.add(ethUsdMin);
                    }
                }
            });
            netAssetValueList = timeList.stream().map(BenchmarkO::getClose).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("getFundMonthNetAssetValue error:{}",e.getMessage(),e);
            return netAssetValueList;
        }
        return netAssetValueList;
    }
    //计算标准差
    private Double sdt(List<Double> doubleList)throws Exception{
        if(null != doubleList && doubleList.size()>0){
            Double avg = doubleList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            if(avg != 0){
                Double snum = 0d;
                for (Double v : doubleList) {
                    snum += Math.pow((v - avg),2);
                }
                Double sdt = doubleList.size() != 0 ? Math.sqrt(snum/doubleList.size()) : 0d;
                return sdt;
            }
        }
        return 0d;
    }
    //计算方差
    public Double var(List<Double> doubleList)throws Exception{
        if(doubleList != null && doubleList.size()>0){
            Double avg = doubleList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            Double snum = 0d;
            for (Double v : doubleList) {
                snum += Math.pow((v - avg),2);
            }
            Double sdt = doubleList.size() != 0 ? snum/doubleList.size() : 0d;
            return sdt;
        }
        return 0d;
    }

}
