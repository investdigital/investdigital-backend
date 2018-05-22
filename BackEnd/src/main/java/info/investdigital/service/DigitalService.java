package info.investdigital.service;

import info.investdigital.common.DateUtil;
import info.investdigital.common.ResourceParam;
import info.investdigital.dao.DigitalCurrency.EthUsdtDayRepo;
import info.investdigital.dao.fund.FundStatisticalRepo;
import info.investdigital.entity.DigitalCurrency.EthUsdtDay;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: huohuo
 * Created in 10:29  2018/3/23.
 */
@Service
public class DigitalService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private EthUsdtMinRepo ethUsdtMinRepo;
    @Resource
    private EthUsdtDayRepo ethUsdtDayRepo;
    @Resource
    private BtcUsdtMinRepo btcUsdtMinRepo;
    @Resource
    private BtcUsdtDayRepo btcUsdtDayRepo;
    @Resource
    private XrpUsdtDayRepo xrpUsdtDayRepo;
    @Resource
    private XrpUsdtMinRepo xrpUsdtMinRepo;
    @Resource
    private BchUsdtDayRepo bchUsdtDayRepo;
    @Resource
    private BchUsdtMinRepo bchUsdtMinRepo;
    @Resource
    private LtcUsdtDayRepo ltcUsdtDayRepo;
    @Resource
    private LtcUsdtMinRepo ltcUsdtMinRepo;
    @Resource
    private ResourceParam resourceParam;
    @Resource
    private FundIndexCalculation fundIndexCalculation;
    @Resource
    private FundStatisticalRepo fundStatisticalRepo;
    /*eth*/
    public List<EthUsdtDay> getEthUsdtDay(Long startTime, Long endTime){
        List<EthUsdtDay> list = ethUsdtDayRepo.findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
        return list;
    };
    public List<EthUsdtMin> getEthUsdtMin(Long startTime, Long endTime){
        return ethUsdtMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    /*btc*/
    public List<BtcUsdtDay> getBtcUsdtDay(Long startTime, Long endTime){
        return btcUsdtDayRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    public List<BtcUsdtMin> getBtcUsdtMin(Long startTime, Long endTime){
        return btcUsdtMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    /*ltc*/
    public List<LtcUsdtDay> getLtcUsdtDay(Long startTime, Long endTime){
        return ltcUsdtDayRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    public List<LtcUsdtMin> getLtcUsdtMin(Long startTime, Long endTime){
        return ltcUsdtMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    /*bch*/
    public List<BchUsdtDay> getBchUsdtDay(Long startTime, Long endTime){
        return bchUsdtDayRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    public List<BchUsdtMin> getBchUsdtMin(Long startTime, Long endTime){
        return bchUsdtMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    /*xrp*/
    public List<XrpUsdtDay> getXrpUsdtDay(Long startTime, Long endTime){
        return xrpUsdtDayRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    public List<XrpUsdtMin> getXrpUsdtMin(Long startTime, Long endTime){
        return xrpUsdtMinRepo.findAllByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(DateUtil.getLowTime(startTime), DateUtil.getLowTime(endTime));
    };
    public IndustryBenchmark getIndustryBenchmarkMin(Long startTime, Long endTime){
        IndustryBenchmark industryBenchmark = null;
        try {
            //计算 业内基准收益
            industryBenchmark = new IndustryBenchmark();
            //五种币的净值数据
            List<BtcUsdtMin> btcUsdtMin = this.getBtcUsdtMin(startTime, endTime);
            List<BchUsdtMin> bchUsdtMin = this.getBchUsdtMin(startTime, endTime);
            List<EthUsdtMin> ethUsdtMin = this.getEthUsdtMin(startTime, endTime);
            List<XrpUsdtMin> xrpUsdtMin = this.getXrpUsdtMin(startTime, endTime);
            List<LtcUsdtMin> ltcUsdtMin = this.getLtcUsdtMin(startTime, endTime);
            //判断数据是否为空
            boolean b = this.checkListIsNull(btcUsdtMin, bchUsdtMin, ethUsdtMin, ltcUsdtMin, xrpUsdtMin);
            if(b){
                //换算数据的时间间隔
                btcUsdtMin = this.checkListToFundUnit(btcUsdtMin);
                bchUsdtMin = this.checkListToFundUnit(bchUsdtMin);
                ethUsdtMin = this.checkListToFundUnit(ethUsdtMin);
                xrpUsdtMin = this.checkListToFundUnit(xrpUsdtMin);
                ltcUsdtMin = this.checkListToFundUnit(ltcUsdtMin);
                //填充数据
                this.fill(btcUsdtMin, bchUsdtMin, ethUsdtMin, ltcUsdtMin, xrpUsdtMin);
                //得到数据的净值
                List<Double> btcCollect = btcUsdtMin.stream().map(BtcUsdtMin::getClose).collect(Collectors.toList());
                List<Double> bchCollect = bchUsdtMin.stream().map(BchUsdtMin::getClose).collect(Collectors.toList());
                List<Double> ethCollect = ethUsdtMin.stream().map(EthUsdtMin::getClose).collect(Collectors.toList());
                List<Double> xrpCollect = xrpUsdtMin.stream().map(XrpUsdtMin::getClose).collect(Collectors.toList());
                List<Double> ltcCollect = ltcUsdtMin.stream().map(LtcUsdtMin::getClose).collect(Collectors.toList());
                //计算收益
                List<Double> btcCollectEaring  = this.getEarrning(btcCollect);
                List<Double> bchCollectEaring  = this.getEarrning(bchCollect);
                List<Double> ethCollectEaring  = this.getEarrning(ethCollect);
                List<Double> xrpCollectEaring  = this.getEarrning(xrpCollect);
                List<Double> ltcCollectEaring  = this.getEarrning(ltcCollect);
                //计算收益的加权 得到基准收益
                List<Double> bench = new ArrayList<>();
                for(int i = 0; i < btcCollectEaring.size();i++){
                    double v = btcCollectEaring.get(i) * resourceParam.getBTC_RATIO()
                            + ethCollectEaring.get(i) * resourceParam.getETH_RATIO()
                            + ltcCollectEaring.get(i) * resourceParam.getLTC_RATIO()
                            + bchCollectEaring.get(i) * resourceParam.getBCH_RATIO()
                            + xrpCollectEaring.get(i) * resourceParam.getXRP_RATIO();
                    bench.add(v);
                }
                //基准的 每个时刻 的收益
                industryBenchmark.setBenchmarkEarning(bench);
                //计算业内基准的 年化收益 每个币种的年化收益的加权
                double industryBenchmarkAnnualEarning = this.getIndustryBenchmarkAnnualEarningMin(btcCollect, bchCollect, ethCollect, xrpCollect, ltcCollect,startTime,endTime);
                industryBenchmark.setAnnualEarning(industryBenchmarkAnnualEarning);
                //期间收益
                double duringEarning = this.duringEarningCalculation(btcCollect, bchCollect, ethCollect, xrpCollect, ltcCollect);
                industryBenchmark.setDuringEarning(duringEarning);
                //获取综合净值 加时间
                List<BenchmarkO> benchmarkO = this.getBenchmarkOMin(btcUsdtMin, bchUsdtMin, ethUsdtMin, ltcUsdtMin, xrpUsdtMin);
                industryBenchmark.setBenchmarkOS(benchmarkO);
                //获取综合净值 多种币的净值的加权
                industryBenchmark.setNetWorth(benchmarkO.stream().map(BenchmarkO::getClose).collect(Collectors.toList()));
            }
            return industryBenchmark;
        } catch (Exception e) {
            logger.error("getIndustryBenchmark error:{}",e.getMessage(),e);
            return industryBenchmark;
        }
    };

    //获取业绩基准增长率 分钟数据
    public List<Double> getBenchmartEarning(Long startTime,Long endTime) throws Exception {
        List<Double> earningList = new ArrayList<>();
        try {
            List<BtcUsdtMin> btcUsdtMin = this.getBtcUsdtMin(startTime, endTime);
            List<BchUsdtMin> bchUsdtMin = this.getBchUsdtMin(startTime, endTime);
            List<EthUsdtMin> ethUsdtMin = this.getEthUsdtMin(startTime, endTime);
            List<XrpUsdtMin> xrpUsdtMin = this.getXrpUsdtMin(startTime, endTime);
            List<LtcUsdtMin> ltcUsdtMin = this.getLtcUsdtMin(startTime, endTime);
            boolean b = this.checkListIsNull(btcUsdtMin, bchUsdtMin, ethUsdtMin, ltcUsdtMin, xrpUsdtMin);
            if(b){
                //改变数据的时间间隔
                btcUsdtMin = this.checkListToFundUnit(btcUsdtMin);
                bchUsdtMin = this.checkListToFundUnit(bchUsdtMin);
                ethUsdtMin = this.checkListToFundUnit(ethUsdtMin);
                xrpUsdtMin = this.checkListToFundUnit(xrpUsdtMin);
                ltcUsdtMin = this.checkListToFundUnit(ltcUsdtMin);
                //填充数据
                this.fill(btcUsdtMin, bchUsdtMin, ethUsdtMin, ltcUsdtMin, xrpUsdtMin);
                //获取净值
                List<Double> btcCollect = btcUsdtMin.stream().map(BtcUsdtMin::getClose).collect(Collectors.toList());
                List<Double> bchCollect = bchUsdtMin.stream().map(BchUsdtMin::getClose).collect(Collectors.toList());
                List<Double> ethCollect = ethUsdtMin.stream().map(EthUsdtMin::getClose).collect(Collectors.toList());
                List<Double> xrpCollect = xrpUsdtMin.stream().map(XrpUsdtMin::getClose).collect(Collectors.toList());
                List<Double> ltcCollect = ltcUsdtMin.stream().map(LtcUsdtMin::getClose).collect(Collectors.toList());
                //计算收益
                List<Double> btcCollectEaring  = this.getEarrningEchar(btcCollect);
                List<Double> bchCollectEaring  = this.getEarrningEchar(bchCollect);
                List<Double> ethCollectEaring  = this.getEarrningEchar(ethCollect);
                List<Double> xrpCollectEaring  = this.getEarrningEchar(xrpCollect);
                List<Double> ltcCollectEaring  = this.getEarrningEchar(ltcCollect);
                for(int i = 0; i < btcCollectEaring.size();i++){
                    double v = btcCollectEaring.get(i) * resourceParam.getBTC_RATIO()
                            + ethCollectEaring.get(i) * resourceParam.getETH_RATIO()
                            + ltcCollectEaring.get(i) * resourceParam.getLTC_RATIO()
                            + bchCollectEaring.get(i) * resourceParam.getBCH_RATIO()
                            + xrpCollectEaring.get(i) * resourceParam.getXRP_RATIO();
                    earningList.add(v);
                }
            }
            return  earningList;
        } catch (Exception e) {
            logger.error("getBenchmartEarning error :{}",e.getMessage(),e);
            return earningList;
        }
    }

    private boolean checkListIsNull(List<BtcUsdtMin> d1, List<BchUsdtMin> d2, List<EthUsdtMin> d3, List<LtcUsdtMin> d4, List<XrpUsdtMin> d5){
        if(d1 == null || d1.size() == 0
                || d2 == null || d2.size() == 0
                || d3 == null || d3.size() == 0
                || d4 == null || d4.size() == 0
                || d5 == null || d5.size() == 0){
            return false;
        }
        return true;
    }
    private boolean checkListIsNullDay(List<BtcUsdtDay> d1, List<BchUsdtDay> d2, List<EthUsdtDay> d3, List<LtcUsdtDay> d4, List<XrpUsdtDay> d5){
        if(d1 == null || d1.size() == 0
                || d2 == null || d2.size() == 0
                || d3 == null || d3.size() == 0
                || d4 == null || d4.size() == 0
                || d5 == null || d5.size() == 0){
            return false;
        }
        return true;
    }
    //填充数据
    private void fill(List<BtcUsdtMin> d1, List<BchUsdtMin> d2, List<EthUsdtMin> d3, List<LtcUsdtMin> d4, List<XrpUsdtMin> d5){
        while(d1.size() > d2.size()){
            d2.add(d2.get(d2.size()-1));
        }
        while(d1.size() > d3.size()){
            d3.add(d3.get(d3.size()-1));
        }
        while(d1.size() > d4.size()){
            d4.add(d4.get(d4.size()-1));
        }
        while(d1.size() > d5.size()){
            d5.add(d5.get(d5.size()-1));
        }
    };
    //填充数据
    private void fillDay(List<BtcUsdtDay> d1, List<BchUsdtDay> d2, List<EthUsdtDay> d3, List<LtcUsdtDay> d4, List<XrpUsdtDay> d5){
        while(d1.size() > d2.size()){
            d2.add(d2.get(d2.size()-1));
        }
        while(d1.size() > d3.size()){
            d3.add(d3.get(d3.size()-1));
        }
        while(d1.size() > d4.size()){
            d4.add(d4.get(d4.size()-1));
        }
        while(d1.size() > d5.size()){
            d5.add(d5.get(d5.size()-1));
        }
    };
    private List<Double> getEarrning(List<Double> doubleList)throws Exception{
        List<Double> earrning = new ArrayList<>();
        for(int i = 1; i < doubleList.size();i++){
            double suff = doubleList.get(i);
            double pre = doubleList.get(i - 1);
            double v = pre !=0 ? (suff - pre) / pre : 0d;
            earrning.add(v);
        }
        return earrning;
    }
    //获取基准的收益率曲线
    private List<Double> getEarrningEchar(List<Double> doubleList)throws Exception{
        List<Double> earrning = new ArrayList<>();
        earrning.add(0d);
        double pre = doubleList.get(0);
        if(pre != 0){
            for(int i = 1; i < doubleList.size();i++){
                double suff = doubleList.get(i);
                double v = (suff - pre) / pre;
                earrning.add(v);
            }
        }
        return earrning;
    }
    private List<BenchmarkO> getBenchmarkOMin(List<BtcUsdtMin> d1, List<BchUsdtMin> d2, List<EthUsdtMin> d3, List<LtcUsdtMin> d4, List<XrpUsdtMin> d5) throws Exception {
        List<BenchmarkO> benchmarkOList = new ArrayList<>();
        BenchmarkO benchmarkO = null;
        for(int i = 0; i<d1.size();i++){
            double v = d1.get(i).getClose() * resourceParam.getBTC_RATIO()
                    + d2.get(i).getClose() * resourceParam.getBCH_RATIO()
                    + d3.get(i).getClose() * resourceParam.getETH_RATIO()
                    + d4.get(i).getClose() * resourceParam.getLTC_RATIO()
                    + d5.get(i).getClose() * resourceParam.getXRP_RATIO();
            benchmarkO = new BenchmarkO(v,d1.get(i).getId());
            benchmarkOList.add(benchmarkO);
        }
        return benchmarkOList;
    }
    private List<BenchmarkO> getBenchmarkODay(List<BtcUsdtDay> d1, List<BchUsdtDay> d2, List<EthUsdtDay> d3, List<LtcUsdtDay> d4, List<XrpUsdtDay> d5) throws Exception {
        List<BenchmarkO> benchmarkOList = new ArrayList<>();
        BenchmarkO benchmarkO = null;
        for(int i = 0; i<d1.size();i++){
            double v = d1.get(i).getClose() * resourceParam.getBTC_RATIO()
                    + d2.get(i).getClose() * resourceParam.getBCH_RATIO()
                    + d3.get(i).getClose() * resourceParam.getETH_RATIO()
                    + d4.get(i).getClose() * resourceParam.getLTC_RATIO()
                    + d5.get(i).getClose() * resourceParam.getXRP_RATIO();
            benchmarkO = new BenchmarkO(v,d1.get(i).getId());
            benchmarkOList.add(benchmarkO);
        }
        return benchmarkOList;
    }
    private <T> List<T> checkListToFundUnit(List<T> d1)throws Exception{
        List<T> ts = new ArrayList<T>();
        for(int i = 0;i<d1.size();i++){
            if(i%5 == 0){
                ts.add(d1.get(i));
            }
        }
        return ts;
    }
    //年华收益率
    private double getIndustryBenchmarkAnnualEarningMin(List<Double> d1,List<Double> d2,List<Double> d3,List<Double> d4,List<Double> d5,Long startTime,Long endTime) throws Exception {
        double v1 = this.getAnnualEarning(d1.get(d1.size()-1),endTime,d1.get(0),startTime);
        double v2 = this.getAnnualEarning(d2.get(d2.size()-1),endTime,d2.get(0),startTime);
        double v3 = this.getAnnualEarning(d3.get(d3.size()-1),endTime,d3.get(0),startTime);
        double v4 = this.getAnnualEarning(d4.get(d4.size()-1),endTime,d4.get(0),startTime);
        double v5 = this.getAnnualEarning(d5.get(d5.size()-1),endTime,d5.get(0),startTime);
        Double ann = v1 * resourceParam.getBTC_RATIO()
                + v2 * resourceParam.getBCH_RATIO()
                + v3 * resourceParam.getETH_RATIO()
                + v4 * resourceParam.getXRP_RATIO()
                + v5 * resourceParam.getLTC_RATIO();
        return ann > -1 ? ann:-1 ;
    }
    //期间收益
    public Double duringEarningCalculation(List<Double> btcUsdtDays,List<Double> bchUsdtDays,List<Double> ethUsdtDay,List<Double> xrpUsdtDay,List<Double> ltcUsdtDay){
        try {
            double v1 = fundIndexCalculation.duringEarningCalculation(btcUsdtDays);
            double v2 = fundIndexCalculation.duringEarningCalculation(bchUsdtDays);
            double v3 = fundIndexCalculation.duringEarningCalculation(ethUsdtDay);
            double v4 = fundIndexCalculation.duringEarningCalculation(xrpUsdtDay);
            double v5 = fundIndexCalculation.duringEarningCalculation(ltcUsdtDay);
            Double ann = v1 * resourceParam.getBTC_RATIO()
                    + v2 * resourceParam.getBCH_RATIO()
                    + v3 * resourceParam.getETH_RATIO()
                    + v4 * resourceParam.getXRP_RATIO()
                    + v5 * resourceParam.getLTC_RATIO();
            return ann > -1 ? ann:-1 ;
        } catch (Exception e) {
            logger.error("benchmark duringEarningCalculation error :{}",e.getMessage(),e);
            return 0d;
        }
    }
    //计算年化收益
    private double getAnnualEarning(double close1,Long time1,double close2 , Long time2)throws Exception{
        double v = close2 != 0 ? (close1 - close2) / close2:0d;
        double dayNum = ( DateUtil.getHighTime(time1) - DateUtil.getHighTime(time2) ) / DateUtil.getDayMS(1).doubleValue();
        double annualEarning = dayNum != 0 ? v / (dayNum / 365):0d;
        return annualEarning;
    }
}
