package info.investdigital.service;

import com.oxchains.basicService.files.tfsService.TFSConsumer;
import info.investdigital.common.*;
import info.investdigital.dao.EncryptedStrDao;
import info.investdigital.dao.SubscribeInfoRepo;
import info.investdigital.dao.TransferInfoRepo;
import info.investdigital.dao.UserRepo;
import info.investdigital.dao.fund.*;
import info.investdigital.entity.*;
import info.investdigital.entity.fund.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ccl
 * @time 2017-12-13 14:44
 * @name FundService
 * @desc:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FundService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private FundReturnRepo fundReturnRepo;
    @Resource
    private FundOfTagRepo fundOfTagRepo;
    @Resource
    private FundTagRepo fundTagRepo;
    @Resource
    private UserRepo userRepo;
    @Resource
    private FundDetailDao fundDetailDao;
    @Resource
    private SubscribeInfoRepo subscribeInfoRepo;
    @Resource
    private TransferInfoRepo transferInfoRepo;
    @Resource
    private FundStatisticalRepo fundStatisticalRepo;
    @Resource
    private FundIndexCalculation fundIndexCalculation;
    @Resource
    private FundPerformanceAnalysisRepo fundPerformanceAnalysisRepo;
    @Resource
    private DigitalService digitalService;
    @Resource
    private HuoBiApiService huoBiApiService;
    @Resource
    private ResourceParam resourceParam;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MyMessageSource myMessageSource;
    @Resource
    private FundEncryptionService fundEncryptionService;
    @Resource
    private EncryptedStrDao encryptedStrDao;
    @Resource
    private FundRankMoveDao fundRankMoveDao;
    private Map<String,Integer> rankMap = new HashMap<>(256);
    public RestResp listFund(Integer sort, Integer pageSize, Integer pageNum) {
        try {
            Sort orders = null;
            Pageable pager = null;
            List<FundDetailVo> voList = new ArrayList<>();
            List<Long> fundCodeList = this.fetchNoParticipatingFund();
            //根据总收益排序
            if(sort == 1){
                orders = new Sort(Sort.Direction.DESC,"totalReturn");
                pager = new PageRequest(pageNum - 1, pageSize,orders);
                Page<FundReturn> page = fundReturnRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}),pager);
                List<FundReturn> fundReturnList = page.getContent();
                if(fundReturnList != null){
                    fundReturnList.forEach(fundReturn -> {
                        FundDetailVo fundDetailVo = this.fetchFundDetailVo(fundReturn.getFundCode());
                        voList.add(fundDetailVo);
                    });
                }
                return RestRespPage.success(voList,page.getTotalElements());
            }
            //根据最大增长排序
            if(sort == 2){
                orders = new Sort(Sort.Direction.DESC,"maxGrowth");
            }
            //根据最大回撤排序
            if(sort == 3){
                orders = new Sort(Sort.Direction.ASC,"maxRetracement");
            }
            //根据最大shape排序
            if(sort == 4){
                orders = new Sort(Sort.Direction.DESC,"sharpe");
            }
            pager = new PageRequest(pageNum - 1, pageSize,orders);
            Page<FundPerformanceAnalysis> all = fundPerformanceAnalysisRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}),pager);
            List<FundPerformanceAnalysis> fundPerformanceAnalysisList = all.getContent();
            if(fundPerformanceAnalysisList != null){
                fundPerformanceAnalysisList.forEach(fundPerformanceAnalysis -> {
                    FundDetailVo fundDetailVo = this.fetchFundDetailVo(fundPerformanceAnalysis.getFundCode());
                    voList.add(fundDetailVo);
                });
            }
            return RestRespPage.success(voList, all.getTotalElements());
        } catch (Exception e) {
            log.error("findAllFund error ;{}", e.getMessage(), e);
            return RestResp.fail();
        }
    }
    private FundPerformanceAnalysis fetchEthIndex(Long fundCode,Long  startTime,Long endTime){
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String s = hashOperations.get(resourceParam.getEthIndexHashKey(), fundCode.toString());
            if(s == null){
                FundPerformanceAnalysis ethFundPerformanceAnalysis = fundIndexCalculation.getEthFundPerformanceAnalysis(null,startTime, endTime);
                hashOperations.put(resourceParam.getEthIndexHashKey(),fundCode.toString(), JsonUtil.toJson(ethFundPerformanceAnalysis));
                return ethFundPerformanceAnalysis;
            }
            return JsonUtil.jsonToEntity(s,FundPerformanceAnalysis.class);
        } catch (Exception e) {
            logger.error("fetchEthIndex error :{}",e.getMessage(),e);
            return null;
        }
    }

    private FundPerformanceAnalysis fetchFundIndex(Long fundCode){
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String s = hashOperations.get(resourceParam.getFundIndexHashKey(), fundCode.toString());
            if(s == null){
                FundPerformanceAnalysis ethFundPerformanceAnalysis = fundPerformanceAnalysisRepo.findByFundCode(fundCode);
                hashOperations.put(resourceParam.getFundIndexHashKey(),fundCode.toString(), JsonUtil.toJson(ethFundPerformanceAnalysis));
                return ethFundPerformanceAnalysis;
            }
            return JsonUtil.jsonToEntity(s,FundPerformanceAnalysis.class);
        } catch (Exception e) {
            logger.error("fetchEthIndex error :{}",e.getMessage(),e);
            return null;
        }
    }

    //获取  所有基金 明星基金 的 funddetalVo
    private FundDetailVo fetchFundDetailVo(Long fundCode){

        FundDetailVo vo = null;
        try {
            HashOperations<String, String, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
            String s = stringObjectObjectHashOperations.get(resourceParam.getFundDetalVoHashKey(), fundCode.toString());
            if(s == null){
                vo = new FundDetailVo();
                FundDetail fundDetail = this.fetchFundDetail(fundCode);
                vo.setFundDetail(fundDetail);
                vo.setUser(userRepo.findOne(fundDetail.getUserId()));
                vo.buildFundReturn(this.fetchFundReturn(fundDetail.getFundCode()),fundDetail.getFinalCapAmount());
                FundPerformanceAnalysis fundPerformanceAnalysis = this.fetchFundIndex(fundDetail.getFundCode());
                vo.setStartTimeStr(DateUtil.longToString(fundPerformanceAnalysis.getStartTime(),"yyyy-MM-dd"));
                vo.setFundIndex(fundPerformanceAnalysis);
                stringObjectObjectHashOperations.put(resourceParam.getFundDetalVoHashKey(),fundDetail.getFundCode().toString(), JsonUtil.toJson(vo));
                return vo;
            }
            vo = JsonUtil.jsonToEntity(s, FundDetailVo.class);
            return vo;
        } catch (Exception e) {
            logger.error("fetchFundDetailVo error:{}",e.getMessage(),e);
            return vo;
        }
    };

    //给所有的 交易附上涨跌幅度
    private void setPriceChangeRatio(List<FundStatistical> fundStatisticalList){
        if(null != fundStatisticalList && fundStatisticalList.size()>1){
            FundStatistical fundStatistical = fundStatisticalList.get(0);
            fundStatistical.setPriceChangeRatio(0d);
            fundStatisticalList.stream().forEach(fundStatistical2 -> {
                if(fundStatistical2.getPriceChangeRatio() == null){
                    double v = fundStatistical.getNetAssetValue() == 0 ? 0 : (fundStatistical2.getNetAssetValue() - fundStatistical.getNetAssetValue()) / fundStatistical.getNetAssetValue();
                    fundStatistical2.setPriceChangeRatio(v);
                }
            });
        }
    }
    public RestResp listStarFund(){
        try {
            List<FundDetail> content = this.fetchStartFund();
            List<FundDetailVo> voList = new ArrayList<>();
            content.stream().forEach(fundDetail -> {
                FundDetailVo fundDetailVo = this.fetchFundDetailVo(fundDetail.getFundCode());
                voList.add(fundDetailVo);
            });
            return RestRespPage.success(voList);
        } catch (Exception e) {
            log.error("findAllFund error ;{}",e.getMessage(),e);
            return RestResp.fail();
        }
    }

    public List<FundDetail> fetchStartFund(){
        List<FundDetail> content = new ArrayList<>();
        try {
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            String s = opsForValue.get(resourceParam.getStarFundKey());
            if(null == s){
                Sort orders = new Sort(Sort.Direction.DESC,"totalReturn");
                Pageable pager = new PageRequest(0, 3,orders);
                List<FundDetail> fundDetailList = fundDetailDao.findByApplyForStatusAndFundActivityId(ParamType.FundApplyForStatus.RUNNING.getStatus(), ParamType.FundActivity.NOT_ATTEND.getStatus());
                List<Long> collect = fundDetailList.stream().map(FundDetail::getFundCode).collect(Collectors.toList());
                Page<FundReturn> page = fundReturnRepo.findAllByFundCodeIn(collect.toArray(new Long[]{}),pager);
                List<FundReturn> fundReturnList = page.getContent();
                fundReturnList.forEach(fundReturn -> {
                    content.add(fundDetailDao.findByFundCode(fundReturn.getFundCode()));
                });
                if(content != null && content.size()>0){
                    opsForValue.set(resourceParam.getStarFundKey(), JsonUtil.toJson(content),1800,TimeUnit.SECONDS);
                }
                return content;
            }
            return JsonUtil.jsonToList(s,FundDetail.class);
        } catch (Exception e) {
            logger.error("fetchStartFund error :{}",e.getMessage(),e);
            return content;
        }
    }

    private Echart getEchart(List<FundStatistical> fundStatisticalList, List<Double> ethUsdtDayList){
        Echart echart = null;
        try {
            echart = new Echart();
            List<String> xAxis = new ArrayList<>();
            List<Map<String,Object>> yAxis = new ArrayList<>();
            Map<String,Object> fundMap = new HashMap<>();
            Map<String,Object> benchmarkMap = new HashMap<>();
            List<String> fund = new ArrayList<>();
            List<String> benchmark = new ArrayList<>();

            for (int i = 0; i<fundStatisticalList.size()-1;i++){
                Long time = fundStatisticalList.get(i).getTime();
                xAxis.add(time != null ? DateUtil.longToString(time,"yyyy-MM-dd HH:mm:ss") : null);
                fund.add(NumberFormatUtil.KeepTwoDecimalPoints(fundStatisticalList.get(i).getPriceChangeRatio()*100).toString());
                if(null != ethUsdtDayList){
                    if(ethUsdtDayList.size()>i){
                        benchmark.add(NumberFormatUtil.KeepTwoDecimalPoints(ethUsdtDayList.get(i)*100).toString());
                    }else{
                        benchmark.add(NumberFormatUtil.KeepTwoDecimalPoints(ethUsdtDayList.get(ethUsdtDayList.size()-1)*100).toString());
                    }
                }
            }
            Long fundCode = fundStatisticalList.get(0).getFundCode();
            FundDetail byFundCode = fundDetailDao.findByFundCode(fundCode);
            fundMap.put("name",byFundCode.getFundCodeName());
            fundMap.put("data",fund);
            benchmarkMap.put("name",resourceParam.getBenchmarkName());
            benchmarkMap.put("data",benchmark);
            yAxis.add(fundMap);
            yAxis.add(benchmarkMap);
            echart.setXAxis(xAxis);
            echart.setYAxis(yAxis);
        } catch (Exception e) {
            log.error("get echart error :{}",e.getMessage(),e);
        }
        return echart;
    }
    private Echart getEchart(List<FundStatistical> fundStatisticalList){
        Echart echart = null;
        try {
            echart = new Echart();
            List<String> xAxis = new ArrayList<>();
            List<Map<String,Object>> yAxis = new ArrayList<>();
            Map<String,Object> fundMap = new HashMap<>();
            Map<String,Object> benchmarkMap = new HashMap<>();
            List<String> fund = new ArrayList<>();

            for (int i = 0; i<fundStatisticalList.size()-1;i++){
                Long time = fundStatisticalList.get(i).getTime();
                xAxis.add(time != null ? DateUtil.longToString(time,"yyyy-MM-dd HH:mm:ss") : null);
                fund.add(NumberFormatUtil.KeepTwoDecimalPoints(fundStatisticalList.get(i).getPriceChangeRatio()*100).toString());
            }
            Long fundCode = fundStatisticalList.get(0).getFundCode();
            FundDetail byFundCode = fundDetailDao.findByFundCode(fundCode);
            fundMap.put("name",byFundCode.getFundCodeName());
            fundMap.put("data",fund);
            yAxis.add(fundMap);
            yAxis.add(benchmarkMap);
            echart.setXAxis(xAxis);
            echart.setYAxis(yAxis);
        } catch (Exception e) {
            log.error("get echart error :{}",e.getMessage(),e);
        }
        return echart;
    }

    //获取基金详情
    public RestResp getFundInfos(Long fundCode) {
        FundDetailVo fundDetailVo = null;
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String s = hashOperations.get(resourceParam.getFundInfoVoHashKey(), fundCode.toString());
            if(s == null){
                fundDetailVo = new FundDetailVo();
                FundDetail fundDetail = this.fetchFundDetail(fundCode);
                if (null == fundDetail) {
                    return null;
                }
                fundDetailVo.setFundDetail(fundDetail);
                fundDetailVo.setUser(this.fetchUser(fundDetail.getUserId()));
                fundDetailVo.setTags(this.getTagsByFundId(fundDetail.getFundCode()));
                fundDetailVo.buildFundReturn(this.fetchFundReturn(fundCode),fundDetail.getFinalCapAmount());
                //基金指数
                FundPerformanceAnalysis fundIndex = this.fetchFundIndex(fundCode);
                fundDetailVo.setFundIndex(fundIndex);
                //业绩基准指数
                FundPerformanceAnalysis benchIndex = this.fetchEthIndex(fundCode, fundIndex.getStartTime(), fundIndex.getEndTime());
                fundDetailVo.setBenchmarkIndex(benchIndex);
                hashOperations.put(resourceParam.getFundInfoVoHashKey(),fundCode.toString(), JsonUtil.toJson(fundDetailVo));
                return RestResp.success(fundDetailVo);
            }
            fundDetailVo = JsonUtil.jsonToEntity(s,FundDetailVo.class);
            return RestResp.success(fundDetailVo);
        } catch (Exception e) {
            logger.error("getFundInfos error :{}",e.getMessage(),e);
            return RestResp.fail();
        }
    }
    //获取基金详情
    public RestResp getFundInfosTrading(Long fundCode) {
        FundDetailTradingVo fundDetailVo = null;
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String s = hashOperations.get(resourceParam.getFundInfoVoHashKey(), fundCode.toString());
            if(s == null){
                fundDetailVo = new FundDetailTradingVo();
                FundDetail fundDetail = this.fetchFundDetail(fundCode);
                if (null == fundDetail) {
                    return null;
                }
                fundDetailVo.setFundDetail(fundDetail);
                fundDetailVo.buildFundReturn(this.fetchFundReturn(fundCode),fundDetail.getFinalCapAmount());
                //基金指数
                FundPerformanceAnalysis fundIndex = this.fetchFundIndex(fundCode);
                fundDetailVo.setFundIndex(fundIndex);
                hashOperations.put(resourceParam.getFundInfoVoHashKey(),fundCode.toString(), JsonUtil.toJson(fundDetailVo));
            }
            else{
                fundDetailVo = JsonUtil.jsonToEntity(s,FundDetailTradingVo.class);
            }
            if(fundDetailVo != null){
                Integer rank = this.getRankByFundCode(fundDetailVo.getFundCode());
                if(rank != null){
                    fundDetailVo.setRank(rank);
                    Integer pre = this.getRankMoveByFundCode(fundCode,rank);
                    fundDetailVo.setRankMove(pre);
                }
            }
            return RestResp.success(fundDetailVo);
        } catch (Exception e) {
            log.error("get fund info error :{}",e.getMessage(),e);
            return RestResp.fail();
        }
    }
    private Integer getRankByFundCode(Long fundCode){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getRankHashKey(),fundCode.toString());
            if(s != null){
                return Integer.parseInt(s);
            }
            return null;
        } catch (Exception e) {
            logger.error("getRankByFundCode error:{}  {}",e.getMessage(),fundCode,e);
            return null;
        }
    }
    private Integer getRankMoveByFundCode(Long fundCode,Integer rank){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(1));
            if(s != null){
                Map<String, Integer> stringIntegerMap = this.conversMap(s);
                Integer integer = stringIntegerMap.get(fundCode.toString());
                return integer - rank;
            }
            return 0;
        } catch (Exception e) {
            logger.error("getRankMovePreByFundCode error:{}  {}",e.getMessage(),fundCode,e);
            return 0;
        }
    }
    //基金详情的 缓存信息
    private FundDetailVo fetchFundDetailVoInfoTrading(Long fundCode){
        FundDetailVo fundDetailVo = null;
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String s = hashOperations.get(resourceParam.getFundInfoVoHashKey(), fundCode.toString());
            if(s == null){
                fundDetailVo = new FundDetailVo();
                FundDetail fundDetail = this.fetchFundDetail(fundCode);
                if (null == fundDetail) {
                    return null;
                }
                fundDetailVo.setTradingFundDetail(fundDetail);
                fundDetailVo.buildFundReturn(this.fetchFundReturn(fundCode),fundDetail.getFinalCapAmount());
                //基金指数
                FundPerformanceAnalysis fundIndex = this.fetchFundIndex(fundCode);
                fundDetailVo.setFundIndex(fundIndex);
                hashOperations.put(resourceParam.getFundInfoVoHashKey(),fundCode.toString(), JsonUtil.toJson(fundDetailVo));
                return fundDetailVo;
            }
            fundDetailVo = JsonUtil.jsonToEntity(s,FundDetailVo.class);
            return fundDetailVo;
        } catch (Exception e) {
            logger.error("fetchFundDetailVoInfo error :{}",e.getMessage(),e);
            return fundDetailVo;
        }
    }
    public List<String> getTagsByFundId(Long fundId) {
        List<FundOfTag> fundOfTags = fundOfTagRepo.findByFundId(fundId);
        List<String> tags = new ArrayList<>();
        if (null != fundOfTags && fundOfTags.size() > 0) {
            Iterable<FundTag> list = fundTagRepo.findAll();
            for (FundOfTag fundOfTag : fundOfTags) {
                for (FundTag fundTag : list) {
                    if (fundOfTag.getTagId().equals(fundTag.getId())) {
                        tags.add(fundTag.getTagName());
                    }
                }
            }
        }
        return tags;
    }
    @Resource
    private TFSConsumer tfsConsumer;
    public void image(MultipartFile multipartFile){
        String s = tfsConsumer.saveTfsFile(multipartFile, 1L);
        System.out.println(s);
    }
    public RestResp tradingCcontest(Integer sort){
        Sort orders = null;
        try {
            List<FundDetailTradingVo> voList = new ArrayList<>();
            List<Long> fundCodeList = this.fetchParticipatingFund();
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            Map<String, String> fundDetailTradingMap = opsForHash.entries(resourceParam.getFundDetalVoHashKey());
            //根据总收益排序
            if(sort == ParamType.FundSortRule.TOTAL_RETURN.getStatus()){
                orders = new Sort(Sort.Direction.DESC, ParamType.FundSortRule.TOTAL_RETURN.getDesc());
                List<FundReturn> fundReturnList = fundReturnRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}), orders);
                if(fundReturnList != null){
                    fundReturnList.forEach(fundReturn -> {
                        FundDetailTradingVo fundDetailTradingVo = null;
                        String fundDetailVoStr = fundDetailTradingMap.get(fundReturn.getFundCode().toString());
                        if(StringUtils.isNotBlank(fundDetailVoStr)){
                            fundDetailTradingVo = JsonUtil.jsonToEntity(fundDetailVoStr, FundDetailTradingVo.class);
                        }
                        else{
                            fundDetailTradingVo = new FundDetailTradingVo();
                            FundDetail fundDetail = this.fetchFundDetail(fundReturn.getFundCode());
                            fundDetailTradingVo.setFundDetail(fundDetail);
                            fundDetailTradingVo.buildFundReturn(this.fetchFundReturn(fundDetail.getFundCode()),fundDetail.getFinalCapAmount());
                            fundDetailTradingVo.setFundIndex(this.fetchFundPerformanceAnalysis(fundDetail.getFundCode()));
                            opsForHash.put(resourceParam.getFundDetalVoHashKey(),fundDetail.getFundCode().toString(), JsonUtil.toJson(fundDetailTradingVo));
                        }
                        voList.add(fundDetailTradingVo);
                    });
                }
                this.setRank(voList,sort);
                return RestResp.success(voList);
            }
            //根据最大增长排序
            if(sort == ParamType.FundSortRule.MAX_GROWTH.getStatus()){
                orders = new Sort(Sort.Direction.DESC, ParamType.FundSortRule.MAX_GROWTH.getDesc());
            }
            //根据最大回撤排序
            if(sort == ParamType.FundSortRule.MAX_RETRACEMENT.getStatus()){
                orders = new Sort(Sort.Direction.ASC, ParamType.FundSortRule.MAX_RETRACEMENT.getDesc());
            }
            //根据最大shape排序
            if(sort == ParamType.FundSortRule.SHARPE.getStatus()){
                orders = new Sort(Sort.Direction.DESC, ParamType.FundSortRule.SHARPE.getDesc());
            }
            List<FundPerformanceAnalysis> fundPerformanceAnalysisList = fundPerformanceAnalysisRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}),orders);
            if(fundPerformanceAnalysisList != null){
                fundPerformanceAnalysisList.forEach(fundPerformanceAnalysis -> {
                    FundDetailTradingVo fundDetailTradingVo = null;
                    String fundDetailVoStr = fundDetailTradingMap.get(fundPerformanceAnalysis.getFundCode().toString());
                    if(StringUtils.isNotBlank(fundDetailVoStr)){
                        fundDetailTradingVo = JsonUtil.jsonToEntity(fundDetailVoStr, FundDetailTradingVo.class);
                    }
                    else{
                        fundDetailTradingVo = new FundDetailTradingVo();
                        FundDetail fundDetail = this.fetchFundDetail(fundPerformanceAnalysis.getFundCode());
                        fundDetailTradingVo.setFundDetail(fundDetail);
                        fundDetailTradingVo.buildFundReturn(this.fetchFundReturn(fundDetail.getFundCode()),fundDetail.getFinalCapAmount());
                        fundDetailTradingVo.setFundIndex(this.fetchFundPerformanceAnalysis(fundDetail.getFundCode()));
                        opsForHash.put(resourceParam.getFundDetalVoHashKey(),fundDetail.getFundCode().toString(), JsonUtil.toJson(fundDetailTradingVo));
                    }
                    voList.add(fundDetailTradingVo);
                });
            }
            this.setRank(voList,sort);
            return RestResp.success(voList);
        } catch (Exception e) {
            logger.error("Get the transaction contest data exception :",e.getMessage(),e);
            return RestResp.fail("Get the transaction contest data exception");
        }
    }
    private Map<String,Integer> getRankMove(Integer sort){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort));
            if(s == null){
                FundRankMove fundRankMove = fundRankMoveDao.findOne(1L);
                if(sort == ParamType.FundSortRule.TOTAL_RETURN.getStatus()){
                    String totalReturnRank = fundRankMove.getTotalReturnRank();
                    if(totalReturnRank == null){
                        List<Long> fundCodeList = this.fetchParticipatingFund();
                        //total
                        Sort orders = new Sort(Sort.Direction.DESC, ParamType.FundSortRule.getDesc(sort));
                        List<FundReturn> fundReturns = fundReturnRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}), orders);
                        if(fundReturns != null && fundReturns.size() >0 ){
                            Map totalReturnMap = new ConcurrentHashMap<String,Integer>();
                            for(int i = 0;i<fundReturns.size();i++){
                                totalReturnMap.put(fundReturns.get(i).getFundCode().toString(),i+1);
                            }
                            totalReturnRank = JsonUtil.toJson(totalReturnMap);
                            fundRankMove.setTotalReturnRank(totalReturnRank);
                            fundRankMoveDao.save(fundRankMove);
                        }
                    }
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),totalReturnRank);
                    return this.conversMap(totalReturnRank);
                }
                if(sort == ParamType.FundSortRule.MAX_GROWTH.getStatus()){
                    String maxGrowthRank = fundRankMove.getMaxGrowthRank();
                    if(maxGrowthRank == null){
                        List<Long> fundCodeList = this.fetchParticipatingFund();
                        Sort orders = new Sort(Sort.Direction.DESC, ParamType.FundSortRule.getDesc(sort));
                        List<FundPerformanceAnalysis> fundPerformanceAnalysisList = fundPerformanceAnalysisRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}), orders);
                        if(fundPerformanceAnalysisList != null && fundPerformanceAnalysisList.size() > 0){
                            Map maxGrowthMap = new ConcurrentHashMap<String,Integer>();
                            for(int i = 0;i<fundPerformanceAnalysisList.size();i++){
                                maxGrowthMap.put(fundPerformanceAnalysisList.get(i).getFundCode().toString(),i+1);
                            }
                            maxGrowthRank = JsonUtil.toJson(maxGrowthMap);
                            fundRankMove.setMaxGrowthRank(maxGrowthRank);
                            fundRankMoveDao.save(fundRankMove);
                        }
                    }
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),maxGrowthRank);
                    return this.conversMap(maxGrowthRank);
                }
                if(sort == ParamType.FundSortRule.MAX_RETRACEMENT.getStatus()){
                    String maxRetracementRank = fundRankMove.getMaxRetracementRank();
                    if(maxRetracementRank == null){
                        List<Long> fundCodeList = this.fetchParticipatingFund();
                        Sort orders = new Sort(Sort.Direction.ASC, ParamType.FundSortRule.getDesc(sort));
                        List<FundPerformanceAnalysis> fundPerformanceAnalysisList = fundPerformanceAnalysisRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}), orders);
                        if(fundPerformanceAnalysisList != null && fundPerformanceAnalysisList.size() > 0){
                            Map maxRetracementMap = new ConcurrentHashMap<String,Integer>();
                            for(int i = 0;i<fundPerformanceAnalysisList.size();i++){
                                maxRetracementMap.put(fundPerformanceAnalysisList.get(i).getFundCode().toString(),i+1);
                            }
                            maxRetracementRank = JsonUtil.toJson(maxRetracementMap);
                            fundRankMove.setMaxRetracementRank(maxRetracementRank);
                            fundRankMoveDao.save(fundRankMove);
                        }
                    }
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),maxRetracementRank);
                    return this.conversMap(maxRetracementRank);
                }
                if(sort == ParamType.FundSortRule.SHARPE.getStatus()){
                    String sharpeRank = fundRankMove.getSharpeRank();
                    if(sharpeRank == null){
                        List<Long> fundCodeList = this.fetchParticipatingFund();
                        Sort orders = new Sort(Sort.Direction.DESC, ParamType.FundSortRule.getDesc(sort));
                        List<FundPerformanceAnalysis> fundPerformanceAnalysisList = fundPerformanceAnalysisRepo.findAllByFundCodeIn(fundCodeList.toArray(new Long[]{}), orders);
                        if(fundPerformanceAnalysisList != null && fundPerformanceAnalysisList.size() > 0){
                            Map sharpeRankMap = new ConcurrentHashMap<String,Integer>();
                            for(int i = 0;i<fundPerformanceAnalysisList.size();i++){
                                sharpeRankMap.put(fundPerformanceAnalysisList.get(i).getFundCode().toString(),i+1);
                            }
                            sharpeRank = JsonUtil.toJson(sharpeRankMap);
                            fundRankMove.setMaxRetracementRank(sharpeRank);
                            fundRankMoveDao.save(fundRankMove);
                        }
                    }
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),sharpeRank);
                    return this.conversMap(sharpeRank);
                }
            }
            return this.conversMap(s);
        } catch (Exception e) {
            logger.error("getRankMove error:{}",e.getMessage(),e);
            return null;
        }
    }
    private Map<String,Integer> conversMap(String s){
        if(s != null){
            Map<String,Integer> map = new ConcurrentHashMap<>();
            Map<String,Double> concurrentHashMap = JsonUtil.jsonToEntity(s, ConcurrentHashMap.class);
            for (Map.Entry<String, Double> entry : concurrentHashMap.entrySet()) {
                map.put(entry.getKey(),entry.getValue().intValue());
            }
            return map;
        }
        return null;
    }
    private void setRank(List<FundDetailTradingVo> detailVos,Integer sort){
        Map<String, Integer> rankMove = this.getRankMove(sort);
        for (int i = 0;i<detailVos.size();i++){
            FundDetailTradingVo fundDetailVo = detailVos.get(i);
            fundDetailVo.setRank(i +1);
            if(sort == ParamType.FundSortRule.TOTAL_RETURN.getStatus()){
                rankMap.put(fundDetailVo.getFundCode().toString(),fundDetailVo.getRank());
            }
            Integer integer = rankMove.get(fundDetailVo.getFundCode().toString());
            if(integer!=null){
                Integer runkMove = integer - fundDetailVo.getRank();
                fundDetailVo.setRankMove(runkMove);
            }
            else{
                FundRankMove fundRankMove = fundRankMoveDao.findOne(1L);
                HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
                if(sort == ParamType.FundSortRule.TOTAL_RETURN.getStatus()){
                    String totalReturnRank = fundRankMove.getTotalReturnRank();
                    Map<String, Integer> stringIntegerMap = this.conversMap(totalReturnRank);
                    stringIntegerMap.put(fundDetailVo.getFundCode().toString(), fundDetailVo.getRank());
                    String s = JsonUtil.toJson(stringIntegerMap);
                    fundRankMove.setTotalReturnRank(s);
                    fundRankMoveDao.save(fundRankMove);
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),s);
                }
                if(sort ==  ParamType.FundSortRule.MAX_GROWTH.getStatus()){
                    String maxGrowthRank = fundRankMove.getMaxGrowthRank();
                    Map<String, Integer> stringIntegerMap = this.conversMap(maxGrowthRank);
                    stringIntegerMap.put(fundDetailVo.getFundCode().toString(), fundDetailVo.getRank());
                    String s = JsonUtil.toJson(stringIntegerMap);
                    fundRankMove.setMaxGrowthRank(s);
                    fundRankMoveDao.save(fundRankMove);
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),s);
                }
                if(sort ==  ParamType.FundSortRule.MAX_RETRACEMENT.getStatus()){
                    String maxRetracementRank = fundRankMove.getMaxRetracementRank();
                    Map<String, Integer> stringIntegerMap = this.conversMap(maxRetracementRank);
                    stringIntegerMap.put(fundDetailVo.getFundCode().toString(), fundDetailVo.getRank());
                    String s = JsonUtil.toJson(stringIntegerMap);
                    fundRankMove.setMaxRetracementRank(s);
                    fundRankMoveDao.save(fundRankMove);
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),s);
                }
                if(sort ==  ParamType.FundSortRule.SHARPE.getStatus()){
                    String sharpeRank = fundRankMove.getSharpeRank();
                    Map<String, Integer> stringIntegerMap = this.conversMap(sharpeRank);
                    stringIntegerMap.put(fundDetailVo.getFundCode().toString(), fundDetailVo.getRank());
                    String s = JsonUtil.toJson(stringIntegerMap);
                    fundRankMove.setSharpeRank(s);
                    fundRankMoveDao.save(fundRankMove);
                    opsForHash.put(resourceParam.getRankMoveHashKey(), ParamType.FundSortRule.getDesc(sort),s);
                }
                fundDetailVo.setRankMove(0);
            }
        }
    }
    public RestResp getParticipatingFundEchart(Long fundCode){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getFundEaringYieldHashKey(), fundCode.toString());
            if(s == null){
                List<FundStatistical> fundStatisticalList = fundStatisticalRepo.findAllByFundCodeOrderByTimeAsc(fundCode);
                if(fundStatisticalList != null && fundStatisticalList.size() > 2){
                    //给所有的 交易附上涨跌幅度
                    this.setPriceChangeRatio(fundStatisticalList);
                    Echart echart = this.getEchart(fundStatisticalList);
                    if(echart != null){
                        opsForHash.put(resourceParam.getFundEaringYieldHashKey(),fundCode.toString(), JsonUtil.toJson(echart));
                        return RestResp.success(echart);
                    }
                }
                return RestResp.success();
            }
            return RestResp.success(JsonUtil.jsonToEntity(s,Echart.class));
        } catch (Exception e) {
            logger.error("get Fund Echart error:{} fundcode:{}",e.getMessage(),fundCode,e);
            return RestResp.fail();
        }
    }
    //获取交易大赛的参赛基金
    public List<Long> fetchParticipatingFund()throws Exception{
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        String s = opsForValue.get(resourceParam.getParticipatingFund());
        if(!StringUtils.isNotBlank(s)){
            List<FundDetail> fundDetailList = fundDetailDao.findByApplyForStatusAndFundActivityId(ParamType.FundApplyForStatus.RUNNING.getStatus(), ParamType.FundActivity.TRADET_COMPETITION.getStatus());
            if(fundDetailList != null && fundDetailList.size()>0){
                List<Long> collect = fundDetailList.stream().map(FundDetail::getFundCode).collect(Collectors.toList());
                opsForValue.set(resourceParam.getParticipatingFund(), JsonUtil.toJson(collect));
                stringRedisTemplate.expire(resourceParam.getParticipatingFund(),3, TimeUnit.DAYS);
                return collect;
            }
            return null;
        }
        return JsonUtil.jsonToList(s,Long.class);
    }
    //获取交易大赛的参赛基金
    private List<Long> fetchNoParticipatingFund()throws Exception{
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        String s = opsForValue.get(resourceParam.getNoParticipatingFund());
        if(!StringUtils.isNotBlank(s)){
            List<FundDetail> fundDetailList = fundDetailDao.findByApplyForStatusAndFundActivityId(ParamType.FundApplyForStatus.RUNNING.getStatus(), ParamType.FundActivity.NOT_ATTEND.getStatus());
            if(fundDetailList != null && fundDetailList.size()>0){
                List<Long> collect = fundDetailList.stream().map(FundDetail::getFundCode).collect(Collectors.toList());
                opsForValue.set(resourceParam.getNoParticipatingFund(), JsonUtil.toJson(collect));
                stringRedisTemplate.expire(resourceParam.getNoParticipatingFund(),3, TimeUnit.DAYS);
                return collect;
            }
            return null;
        }
        return JsonUtil.jsonToList(s,Long.class);
    }
    public RestResp getFundEchart(Long fundCode){
        try {
            FundDetail fundDetail = this.fetchFundDetail(fundCode);
            if(null == fundDetail){
                return RestResp.fail("The fund does not exist");
            }
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getFundEaringYieldHashKey(), fundCode.toString());
            if(s == null){
                List<FundStatistical> fundStatisticalList = fundStatisticalRepo.findAllByFundCodeOrderByTimeAsc(fundCode);
                if(fundStatisticalList != null && fundStatisticalList.size() > 2){
                    Long starttime = fundStatisticalList.get(0).getTime();
                    Long endtime = fundStatisticalList.get(fundStatisticalList.size() - 1).getTime();
                    //给所有的 交易附上涨跌幅度
                    this.setPriceChangeRatio(fundStatisticalList);
                    //获取基准线
                    List<Double> benchmartEarning = digitalService.getBenchmartEarning(starttime, endtime);
                    Echart echart = this.getEchart(fundStatisticalList, benchmartEarning);
                    if(echart != null){
                        opsForHash.put(resourceParam.getFundEaringYieldHashKey(),fundCode.toString(), JsonUtil.toJson(echart));
                        return RestResp.success(echart);
                    }
                }
                return RestResp.success();
            }
            return RestResp.success(JsonUtil.jsonToEntity(s,Echart.class));
        } catch (Exception e) {
            logger.error("get Fund Echart error:{} fundcode:{}",e.getMessage(),fundCode,e);
            return RestResp.fail();
        }
    }
    //获取funddetail
    private FundDetail fetchFundDetail(Long fundCode){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getFundDetailHashkey(), fundCode.toString());
            if(!StringUtils.isNotBlank(s)){
                FundDetail fundDetail = fundDetailDao.findByFundCode(fundCode);
                if(fundDetail != null){
                    opsForHash.put(resourceParam.getFundDetailHashkey(),fundCode.toString(), JsonUtil.toJson(fundDetail));
                    stringRedisTemplate.expire(resourceParam.getFundDetailHashkey(),3,TimeUnit.DAYS);
                    return fundDetail;
                }
                return null;
            }
            return JsonUtil.jsonToEntity(s,FundDetail.class);
        } catch (Exception e) {
            logger.error("fetch FundDetail from redis error:{}  fundCode:{}",e.getMessage(),fundCode,e);
            return null;
        }
    }
    public FundPerformanceAnalysis fetchFundPerformanceAnalysis(Long fundCode){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getFundIndexHashKey(), fundCode.toString());
            if(s == null){
                FundPerformanceAnalysis fundPerformanceAnalysis = fundPerformanceAnalysisRepo.findByFundCode(fundCode);
                if(fundPerformanceAnalysis != null){
                    opsForHash.put(resourceParam.getFundIndexHashKey(),fundCode.toString(), JsonUtil.toJson(fundPerformanceAnalysis));
                    return fundPerformanceAnalysis;
                }
                return null;
            }
            return JsonUtil.jsonToEntity(s,FundPerformanceAnalysis.class);
        } catch (Exception e) {
            logger.error("fetch FundPerformanceAnalysis error:{} fundCode:{}",e.getMessage(),fundCode,e);
            return null;
        }
    }
    public User fetchUser(Long userId){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getUserHashKey(), userId.toString());
            if(s == null){
                User user = userRepo.findOne(userId);
                if(user != null){
                    opsForHash.put(resourceParam.getUserHashKey(),user.toString(), JsonUtil.toJson(user));
                    stringRedisTemplate.expire(resourceParam.getUserHashKey(),3,TimeUnit.DAYS);
                    return user;
                }
                return null;
            }
            return  JsonUtil.jsonToEntity(s,User.class);
        } catch (Exception e) {
            logger.error("fetch User from redis error :{} userId:{}",e.getMessage(),userId,e);
            return null;
        }
    }
    public FundReturn fetchFundReturn(Long fundCode){
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getFundReturnHashKey(), fundCode.toString());
            if(s == null){
                FundReturn fundReturn = fundReturnRepo.findByFundCode(fundCode);
                if(fundReturn != null){
                    opsForHash.put(resourceParam.getFundReturnHashKey(),fundCode.toString(), JsonUtil.toJson(fundReturn));
                    return fundReturn;
                }
                return null;
            }
            return JsonUtil.jsonToEntity(s,FundReturn.class);
        } catch (Exception e) {
            logger.error("fetch fundReturn error:{},fundCode:{}",e.getMessage(),fundCode,e);
            return null;
        }
    }
    //获取交易大赛 所有基金的 funddetail
    private FundDetailTradingVo fetchFundDetailVoNotContainsEchart(Long fundCode){
        FundDetailTradingVo vo = null;
        try {
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            String s = opsForHash.get(resourceParam.getFundDetalVoHashKey(), fundCode.toString());
            Map<String, String> entries = opsForHash.entries(resourceParam.getFundDetalVoHashKey());
            if(s == null){
                vo = new FundDetailTradingVo();
                FundDetail fundDetail = this.fetchFundDetail(fundCode);
                vo.setFundDetail(fundDetail);
                vo.buildFundReturn(this.fetchFundReturn(fundDetail.getFundCode()),fundDetail.getFinalCapAmount());
                vo.setFundIndex(this.fetchFundPerformanceAnalysis(fundDetail.getFundCode()));
                opsForHash.put(resourceParam.getFundDetalVoHashKey(),fundDetail.getFundCode().toString(), JsonUtil.toJson(vo));
                return vo;
            }
            return JsonUtil.jsonToEntity(s,FundDetailTradingVo.class);
        } catch (Exception e) {
            logger.error("fetchFundDetailVo error:{}",e.getMessage(),e);
            return vo;
        }
    };
}
