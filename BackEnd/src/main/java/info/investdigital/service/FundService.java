package info.investdigital.service;

import com.alibaba.fastjson.JSONObject;
import info.investdigital.common.*;
import info.investdigital.dao.*;
import info.investdigital.dao.DigitalCurrency.EthUsdtDayRepo;
import info.investdigital.entity.*;
import info.investdigital.entity.DigitalCurrency.EthUsdtDay;
import info.investdigital.entity.FundDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
/**
 * @author ccl
 * @time 2017-12-13 14:44
 * @name FundService
 * @desc:
 */
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FundService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private FundRepo fundRepo;
    @Resource
    private FundReturnRepo fundReturnRepo;
    @Resource
    private FundReturnDetailRepo fundReturnDetailRepo;
    @Resource
    private FundOfTagRepo fundOfTagRepo;
    @Resource
    private FundTagRepo fundTagRepo;
    @Resource
    private UserRepo userRepo;
    @Resource
    private FundInfoRepo fundInfoRepo;
    @Resource
    private FundCommentRepo fundCommentRepo;
    @Resource
    private FundDetailDao fundDetailDao;
    @Resource
    private SubscribeInfoRepo subscribeInfoRepo;

    @Resource
    private Web3j web3j;
    @Resource
    private Web3Service web3Service;

    @Resource
    private TransferInfoRepo transferInfoRepo;
    @Resource
    private EthUsdtDayRepo ethUsdtDayRepo;
    @Resource
    private FundStatisticalRepo fundStatisticalRepo;

    //Issue fund
    public RestResp issuefund(FundDetail fundDetail) {
        if (null != fundDetail) {
            try {
                fundDetail.setFundCode(this.getFundCode());
                fundDetail.setApplyForStatus(ParamType.FundApplyForStatus.APPLYFORING.getStatus());
                fundDetail.setStartTime(System.currentTimeMillis());
                FundDetail save = fundDetailDao.save(fundDetail);
                FundReturn fundReturn = new FundReturn();
                fundReturn.setFundCode(save.getFundCode());
                fundReturn.setFundId(save.getId());
                fundReturn.setNetAssetValue(0f);
                fundReturn.setNetValue(0f);
                fundReturn.setTotalReturn(0f);
                fundReturn.setPriceChangeRatio(0f);
                FundReturn save1 = fundReturnRepo.save(fundReturn);
                return RestResp.success("The application for the issuance fund is successful.", save);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return RestResp.fail("The application for the issuance fund is failure.");
            }
        }
        return RestResp.fail("The application for the issuance fund is successful.");
    }
    public BigInteger getFundCode()throws Exception{
        try {
            Random random = new Random();
            while (true){
                String fundCode = "";
                for (int i = 0;i<= 7;i++){
                    fundCode += random.nextInt(9)+"";
                }
                FundDetail byFundCode = fundDetailDao.findByFundCode(new BigInteger(fundCode));
                if(byFundCode == null){
                    return new BigInteger(fundCode);
                }
            }
        } catch (Exception e) {
            log.error("get fundCode error :{}",e.getMessage(),e);
            throw  e;
        }
    }
    //审核基金
    public RestResp reviewFund(BigInteger fundCode, Integer status){
        try {
            FundDetail fundDetail = fundDetailDao.findByFundCode(fundCode);
            if(fundDetail == null){
                return RestResp.fail("The fund does not exist.");
            }
            fundDetail.setApplyForStatus(status);
            FundDetail save = fundDetailDao.save(fundDetail);
            if(status == ParamType.FundApplyForStatus.ALLOW.getStatus()){
                boolean b = this.issueFundToChain(fundDetail);
                if(!b){
                    throw new Exception("review fund fail");
                }
            }
            return RestResp.success();
        } catch (Exception e) {
            logger.error("review fund fail:{}",e.getMessage(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RestResp.fail("review fund fail");
        }
    }
    private boolean issueFundToChain(FundDetail fundDetail){
        try {
                //在链上发布基金
                Function function = new Function(
                        "ReleaseFund",
                        Arrays.<Type>asList(new Uint256(fundDetail.getFundCode()),
                                new Uint256(fundDetail.getStartRaiseTime()),
                                new Uint256(fundDetail.getEndRaiseTime()),
                                new Uint256(fundDetail.getLastingClosedPeriod()),
                                new Uint256(fundDetail.getCapSupply()),
                                new Uint256(fundDetail.getManageFeeRate())),
                        Collections.<TypeReference<?>>emptyList());
                String data = FunctionEncoder.encode(function);
                String from = userRepo.findOne(fundDetail.getUserId()).getFirstAddress();
                EthTransactionEntity ethTransactionEntity = new EthTransactionEntity(web3Service.getNonce(from),from,ParamType.ContractAddress.FUND_MANAGE_CONTRACT.getAddress(),"0x00",data);
                String s = JsonUtil.toJson(ethTransactionEntity);
                String txStr = this.getTxStr(s);
                if(txStr != null){
                    EthSendTransaction send = web3j.ethSendRawTransaction(txStr).send();
                    if(send.hasError() || send.getTransactionHash() == null){
                        return false;
                    }
                    return true;
                }
                return false;
        } catch (Exception e) {
            logger.error("issue Fund To Chain error fundCode:{},Cause By:{}",fundDetail.getFundCode(),e.getMessage(),e);
            return false;
        }

    }

    //get fund address by fundCode
    private String getFundAddress(BigInteger fundCode) throws IOException {
        try {
        FundDetail byFundCode = fundDetailDao.findByFundCode(fundCode);
        if(byFundCode != null){
            String address = byFundCode.getAddress();
            if(address == null){
                Function function = new Function("getFundAddr",
                        Arrays.<Type>asList(new Uint256(fundCode)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
                String data = FunctionEncoder.encode(function);
                Transaction ethCallTransaction = Transaction.createEthCallTransaction(null, ParamType.ContractAddress.FUND_MANAGE_CONTRACT.getAddress(), data);
                EthCall send = web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).send();
                address = send.getValue();
                byFundCode.setAddress(address);
                fundDetailDao.save(byFundCode);
            }
            return address;
        }
        throw new NullPointerException();
        } catch (Exception e) {
            logger.error("get Fund Address faild fundCode:{} Cause By:{}",fundCode,e.getMessage(),e);
            throw e;
        }
    }

    private String getTxStr(String rawStr){
        String txStr = null;
        try {
            Map map = new HashMap<String,String>();
            map.put("rawTx",rawStr);
            String str = HttpClientUtils.doPost(ParamZero.signServerUrl, map);
            JSONObject o  = (JSONObject) JSONObject.parse(str);
            txStr = (String) o.get("txStr");
        } catch (Exception e) {
            logger.error("get txStr faild:{}",e.getMessage(),e);
        }
        return txStr;
    }
    //subscription fundp
    public  RestResp subFund(SubscribeInfo subscribeInfo){
        try {
            FundDetail fundDetail = fundDetailDao.findByFundCode(subscribeInfo.getFundCode());
            if(fundDetail != null){
                User user = userRepo.findOne(fundDetail.getUserId());
                String firstAddress = user.getFirstAddress();
                String nonce = web3Service.getNonce(firstAddress);
                String fundAddress = this.getFundAddress(subscribeInfo.getFundCode());
                EthTransactionEntity ethTransactionEntity = new EthTransactionEntity(nonce,firstAddress,fundAddress,web3Service.getAmount(subscribeInfo.getAmount()).toString());
                String txStr = this.getTxStr(JsonUtil.toJson(ethTransactionEntity));
                boolean b = web3Service.sendRawTransaction(txStr);
                if(b){
                    subscribeInfo.setTime(System.currentTimeMillis());
                    SubscribeInfo save = subscribeInfoRepo.save(subscribeInfo);
                    return RestResp.success();
                }
                return RestResp.fail("subscrip fund fail");
            }
            return RestResp.fail("this fund is not fund");
        } catch (Exception e) {
            logger.error("subscrip fund fail:{}",e.getMessage(),e);
            return RestResp.fail("subscrip fund fail"+e.getMessage());
        }
    }
    //转让份额
    public RestResp transferFundShare(TransferInfo transferInfo){
        try {
            String fromAddress = transferInfo.getFromAddress();
            Function functions = new Function(
                    "transfer",
                    Arrays.<Type>asList(new Uint256(transferInfo.getFundCode()),
                            new Address(transferInfo.getToAddress()),
                            new Uint256(web3Service.getAmount(transferInfo.getAmount()))),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(functions);
            EthTransactionEntity ethTransactionEntity = new EthTransactionEntity(web3Service.getNonce(fromAddress),fromAddress,ParamType.ContractAddress.FUND_SHARE_CONTRACT.getAddress(),"0x00",data);
            String txStr = web3Service.getTxStr(JsonUtil.toJson(ethTransactionEntity));
            boolean b = web3Service.sendRawTransaction(txStr);
            if(b){
                transferInfo.setTime(System.currentTimeMillis());
                transferInfoRepo.save(transferInfo);
                return RestResp.success();
            }
            return RestResp.fail();
        } catch (Exception e) {
            log.error("transfer fund share faild ");
            return RestResp.fail();
        }

    }
    public RestResp findAllFund(Integer pageSize,Integer pageNum){
        try {
            Pageable pager = new PageRequest((pageNum-1)*pageSize, pageSize);
            Page<FundDetail> page = fundDetailDao.findAll(pager);
            List<FundDetail> content = page.getContent();
            List<FundDetailVo> voList = new ArrayList<>();
            //each set fund datil info
            content.stream().forEach(fundDetail->{
                FundDetailVo vo = new FundDetailVo();
                //funddetail
                vo.setFundDetail(fundDetail);
                //username
                vo.setIssueUserName(userRepo.findOne(fundDetail.getId()).getUsername());
                //fundreturn
                FundReturn fundReturn = fundReturnRepo.findByFundId(fundDetail.getId());
                FundReturn fundReturn1 = new FundReturn();
                //总收益
                fundReturn1.setTotalReturn(fundReturn.getTotalReturn()*100);
                //涨跌幅
                float v1 = (fundReturn.getCurrentQuantity() - fundDetail.getFinalCapAmount()) / fundDetail.getFinalCapAmount() * 100;
                fundReturn1.setPriceChangeRatio(v1);
                fundReturn1.setNetAssetValue(PriceListening.eth);
                fundReturn1.setNetValue(PriceListening.eth);
                fundReturn1.setFundId(fundReturn.getFundId());
                fundReturn1.setFundCode(fundReturn.getFundCode());
                fundReturn1.setCurrentQuantity(fundReturn.getCurrentQuantity());
                vo.setFundReturn(fundReturn1);
                //tags
                vo.setTags(this.getTagsByFundId(fundDetail.getId()));

                List<FundStatistical> fundStatisticalList = fundStatisticalRepo.findAllByFundCodeOrderByTimeAsc(fundDetail.getFundCode());

                if(fundStatisticalList.size() == 0){
                    vo.setEchart(null);

                }
                else{
                    //获取  开始和最后时间
                    FundStatistical fundStatistical = fundStatisticalList.get(0);
                    Long starttime = fundStatistical.getTime();
                    fundStatistical.setPriceChangeRatio(0d);
                    FundStatistical fundStatistical1 = fundStatisticalList.get(fundStatisticalList.size() - 1);
                    Long endtime = fundStatistical1.getTime();
                    //给所有的 交易附上涨跌幅度
                    fundStatisticalList.stream().forEach(fundStatistical2 -> {
                        if(fundStatistical2.getPriceChangeRatio() == null){
                            double v = (fundStatistical2.getNetAssetValue() - fundStatistical.getNetAssetValue()) / fundStatistical.getNetAssetValue();
                            fundStatistical2.setPriceChangeRatio(v);
                        }
                    });
                    //获取基准线
                    List<EthUsdtDay> ethUsdtDayList = ethUsdtDayRepo.findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(starttime, endtime);
                    EthUsdtDay eth = ethUsdtDayList.get(0);
                    eth.setPriceChangeRatio(0d);
                    ethUsdtDayList.stream().forEach(ethUsdtDay -> {
                        if(ethUsdtDay.getPriceChangeRatio() == null){
                            double v = (ethUsdtDay.getClose() - eth.getClose()) / eth.getClose();
                            ethUsdtDay.setPriceChangeRatio(v);
                        }
                    });
                    vo.setEchart(this.getEchart(fundStatisticalList,ethUsdtDayList));
                }
                voList.add(vo);
            });
            return RestRespPage.success(voList,page.getTotalElements());
        } catch (Exception e) {
            log.error("findAllFund error ;{}",e.getMessage(),e);
            return RestResp.fail();
        }
    }
    public RestResp findStarFund(){
        try {
            Pageable pager = new PageRequest(0, 3);
            Page<FundDetail> page = fundDetailDao.findAll(pager);
            List<FundDetail> content = page.getContent();
            List<FundDetailVo> voList = new ArrayList<>();
            //each set fund datil info
            content.stream().forEach(fundDetail->{
                FundDetailVo vo = new FundDetailVo();
                //funddetail
                vo.setFundDetail(fundDetail);
                //username
                vo.setIssueUserName(userRepo.findOne(fundDetail.getId()).getUsername());
                //fundreturn
                FundReturn fundReturn = fundReturnRepo.findByFundId(fundDetail.getId());
                FundReturn fundReturn1 = new FundReturn();
                //总收益
                fundReturn1.setTotalReturn(fundReturn.getTotalReturn()*100);
                //涨跌幅
                float v1 = (fundReturn.getCurrentQuantity() - fundDetail.getFinalCapAmount()) / fundDetail.getFinalCapAmount() * 100;
                fundReturn1.setPriceChangeRatio(v1);
                fundReturn1.setNetAssetValue(PriceListening.eth);
                fundReturn1.setNetValue(PriceListening.eth);
                fundReturn1.setFundId(fundReturn.getFundId());
                fundReturn1.setFundCode(fundReturn.getFundCode());
                fundReturn1.setCurrentQuantity(fundReturn.getCurrentQuantity());
                vo.setFundReturn(fundReturn1);
                //tags
                vo.setTags(this.getTagsByFundId(fundDetail.getId()));

                List<FundStatistical> fundStatisticalList = fundStatisticalRepo.findAllByFundCodeOrderByTimeAsc(fundDetail.getFundCode());

                if(fundStatisticalList.size() == 0){
                    vo.setEchart(null);

                }
                else{
                    //获取  开始和最后时间
                    FundStatistical fundStatistical = fundStatisticalList.get(0);
                    Long starttime = fundStatistical.getTime();
                    fundStatistical.setPriceChangeRatio(0d);
                    FundStatistical fundStatistical1 = fundStatisticalList.get(fundStatisticalList.size() - 1);
                    Long endtime = fundStatistical1.getTime();
                    //给所有的 交易附上涨跌幅度
                    fundStatisticalList.stream().forEach(fundStatistical2 -> {
                        if(fundStatistical2.getPriceChangeRatio() == null){
                            double v = (fundStatistical2.getNetAssetValue() - fundStatistical.getNetAssetValue()) / fundStatistical.getNetAssetValue();
                            fundStatistical2.setPriceChangeRatio(v);
                        }
                    });
                    //获取基准线
                    List<EthUsdtDay> ethUsdtDayList = ethUsdtDayRepo.findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(starttime, endtime);
                    EthUsdtDay eth = ethUsdtDayList.get(0);
                    eth.setPriceChangeRatio(0d);
                    ethUsdtDayList.stream().forEach(ethUsdtDay -> {
                        if(ethUsdtDay.getPriceChangeRatio() == null){
                            double v = (ethUsdtDay.getClose() - eth.getClose()) / eth.getClose();
                            ethUsdtDay.setPriceChangeRatio(v);
                        }
                    });
                    vo.setEchart(this.getEchart(fundStatisticalList,ethUsdtDayList));
                }
                voList.add(vo);
            });
            return RestRespPage.success(voList,page.getTotalElements());
        } catch (Exception e) {
            log.error("findAllFund error ;{}",e.getMessage(),e);
            return RestResp.fail();
        }
    }
    private Echart getEchart(List<FundStatistical> fundStatisticalList,List<EthUsdtDay> ethUsdtDayList){
        Echart echart = null;
        try {
            echart = new Echart();
            List<String> xAxis = new ArrayList<>();
            List<Map<String,Object>> yAxis = new ArrayList<>();
            Map<String,Object> fundMap = new HashMap<>();
            Map<String,Object> benchmarkMap = new HashMap<>();
            List<Double> fund = new ArrayList<>();
            List<Double> benchmark = new ArrayList<>();
            for (int i = 0; i<fundStatisticalList.size()-1;i++){
                Long time = fundStatisticalList.get(i).getTime()*1000;
                xAxis.add(DateUtil.longToString(time,"yyyy-MM-dd"));
                fund.add(fundStatisticalList.get(i).getPriceChangeRatio());
                benchmark.add(ethUsdtDayList.get(i).getPriceChangeRatio());
            }
            fundMap.put("name",fundStatisticalList.get(0).getFundCode());
            fundMap.put("data",fund);
            benchmarkMap.put("name","ETH");
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
    public RestResp getFundInfos(BigInteger fundCode) {
        FundDetailVo fundDetailVo = null;
        try {
            fundDetailVo = new FundDetailVo();
            FundDetail fundDetail = fundDetailDao.findByFundCode(fundCode);

            if (null == fundDetail) {
                return RestResp.fail("无法查询基金详细信息");
            }
            fundDetailVo.setFundDetail(fundDetail);
            User user = userRepo.findOne(fundDetail.getUserId());
            if (null != user) {
                fundDetailVo.setIssueUserName(user.getLoginname());
            }
            FundReturn fundReturn = fundReturnRepo.findByFundId(fundDetail.getId());
            FundReturn fundReturn1 = new FundReturn();
            fundReturn1.setTotalReturn(fundReturn.getTotalReturn()*100);
            float v1 = (fundReturn.getCurrentQuantity() - fundDetail.getFinalCapAmount()) / fundDetail.getFinalCapAmount() * 100;
            fundReturn1.setPriceChangeRatio(v1);
            fundReturn1.setNetAssetValue(PriceListening.eth);
            fundReturn1.setNetValue(PriceListening.eth);
            fundReturn1.setFundId(fundReturn.getFundId());
            fundReturn1.setFundCode(fundReturn.getFundCode());
            fundReturn1.setCurrentQuantity(fundReturn.getCurrentQuantity());
            fundDetailVo.setFundReturn(fundReturn1);
            //tags
            List<String> tags = getTagsByFundId(fundDetail.getId());
            fundDetailVo.setTags(tags);
            List<FundStatistical> fundStatisticalList = fundStatisticalRepo.findAllByFundCodeOrderByTimeAsc(fundDetail.getFundCode());

            if(fundStatisticalList.size() == 0){
                fundDetailVo.setEchart(null);
            }
            else {
                //获取  开始和最后时间
                FundStatistical fundStatistical = fundStatisticalList.get(0);
                Long starttime = fundStatistical.getTime();
                fundStatistical.setPriceChangeRatio(0d);
                FundStatistical fundStatistical1 = fundStatisticalList.get(fundStatisticalList.size() - 1);
                Long endtime = fundStatistical1.getTime();
                //给所有的 交易附上涨跌幅度
                fundStatisticalList.stream().forEach(fundStatistical2 -> {
                    if (fundStatistical2.getPriceChangeRatio() == null) {
                        double v = (fundStatistical2.getNetAssetValue() - fundStatistical.getNetAssetValue()) / fundStatistical.getNetAssetValue();
                        fundStatistical2.setPriceChangeRatio(v);
                    }
                });
                //获取基准线
                List<EthUsdtDay> ethUsdtDayList = ethUsdtDayRepo.findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(starttime, endtime);
                EthUsdtDay eth = ethUsdtDayList.get(0);
                eth.setPriceChangeRatio(0d);
                ethUsdtDayList.stream().forEach(ethUsdtDay -> {
                    if (ethUsdtDay.getPriceChangeRatio() == null) {
                        double v = (ethUsdtDay.getClose() - eth.getClose()) / eth.getClose();
                        ethUsdtDay.setPriceChangeRatio(v);
                    }
                });
                fundDetailVo.setEchart(this.getEchart(fundStatisticalList,ethUsdtDayList));
                this.getFundReturn(fundDetailVo,fundStatisticalList);
            }
        } catch (Exception e) {
            log.error("get fund info error :{}",e.getMessage(),e);
            return RestResp.fail();
        }
        return RestResp.success("成功", fundDetailVo);
    }


    public void getFundReturn(FundDetailVo  fundDetailVo,List<FundStatistical> fundStatisticalList){
        Integer integer = fundStatisticalRepo.countByFundCode(fundDetailVo.getFundCode());
        Float monthChange = 0f;
        Float month3Change = 0f;
        Float yearChange = 0f;
        Float thisYearChange = 0f;
        if(integer <=30 && integer>0){
            monthChange = fundDetailVo.getFundReturn().getTotalReturn();
            month3Change = monthChange;
            yearChange = monthChange;
            thisYearChange = monthChange;
        }
        if (integer <= 90 && integer>30 ){
            double v = (fundStatisticalList.get(30).getNetAssetValue() - fundDetailVo.getFinalCapAmount()) / fundDetailVo.getFinalCapAmount();
            monthChange = Float.valueOf(String.valueOf(v));
            month3Change = fundDetailVo.getFundReturn().getTotalReturn();
            yearChange = month3Change;
            thisYearChange = month3Change;
        }
        if (integer <= 365 && integer>90 ){
            double v = (fundStatisticalList.get(30).getNetAssetValue() - fundDetailVo.getFinalCapAmount()) / fundDetailVo.getFinalCapAmount();
            monthChange = Float.valueOf(String.valueOf(v));
            double month3 = (fundStatisticalList.get(90).getNetAssetValue() - fundDetailVo.getFinalCapAmount()) / fundDetailVo.getFinalCapAmount();
            month3Change =  Float.valueOf(String.valueOf(month3));
            yearChange = month3Change;
            thisYearChange = month3Change;
        }
        fundDetailVo.getFundReturn().setMonthChange(monthChange*100);
        fundDetailVo.getFundReturn().setMonth3Change(month3Change*100);
        fundDetailVo.getFundReturn().setYearChange(yearChange*100);
        fundDetailVo.getFundReturn().setThisYearChange(thisYearChange*100);
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


    public RestResp fundComment(Long fundId,Integer pageSize,Integer pageNum) {
        pageNum = pageNum == null?1:pageNum;
        pageSize = pageSize == null ?10 :pageSize;
        Pageable pager = new PageRequest((pageNum-1)*pageSize, pageSize);
        Page<FundComment> page = fundCommentRepo.findByFundId(fundId,pager);
        List<FundComment> comments = page.getContent();//fundCommentRepo.findByFundId(fundId);
        if (comments != null && comments.size() > 0) {
            Iterable<User> users = userRepo.findAll();
            for (int i = 0; i < comments.size(); i++) {
                for (User user : users) {
                    if (comments.get(i).getUserId().equals(user.getId())) {
                        comments.get(i).setUsername(user.getLoginname());
                        break;
                    }
                }
            }
            return RestRespPage.success(comments,page.getTotalElements());
        }
        return RestResp.fail("无评论数据", null);
    }

    private Echart dataToEchart(List<FundReturnDetail> list){
        Echart echart = null;
        if(null!=list && list.size()>0){
            echart = new Echart();
            List<String> xAxis = new ArrayList<>();
            List<Map<String,Object>> yAxis = new ArrayList<>();
            Map<String,Object> mapFund = new HashMap<>();
            Map<String,Object> mapCsi300 = new HashMap<>();
            //Map<String,Object> mapShIndex = new HashMap<>();

            List<Float> fund = new ArrayList<>();
            List<Float> csi300 = new ArrayList<>();
            //List<Float> shindex = new ArrayList<>();
            for(FundReturnDetail detail:list){
                xAxis.add(detail.getDateStr());

                fund.add(detail.getFundReturn());
                csi300.add(detail.getCsi300());
                //shindex.add(detail.getShcompositeIndex());

            }

            mapFund.put("name",list.get(0).getFundCode());
            mapFund.put("data",fund);
            yAxis.add(mapFund);

            mapCsi300.put("name","沪深300");
            mapCsi300.put("data",csi300);
            yAxis.add(mapCsi300);
            echart.setXAxis(xAxis);
            echart.setYAxis(yAxis);
        }
        return echart;
    }


    public RestResp addComment(FundComment comment){
        if(null == comment.getUserId() || null == comment.getFundId() || null == comment.getComments() || "".equals(comment.getComments().trim())){
            return RestResp.fail("未填写评论");
        }
        try{
            comment.setDate(DateUtil.getPresentDate());
            comment = fundCommentRepo.save(comment);
            return RestResp.success("添加评论成功",comment);
        }catch (Exception e){
            log.error("添加评论失败",e);
            return RestResp.fail("添加评论失败");
        }
    }
    @Resource
    private TFSConsumer tfsConsumer;

    public RestResp images(FundCommentVO vo){
        if(null == vo){
            return RestResp.fail("参数不能为空");
        }
        if(vo.getUserId()==null){
            return RestResp.fail("用户名不能为空");
        }
        FundComment ct = null;
        if(vo.getId() != null){
            ct = fundCommentRepo.findOne(vo.getId());
        }else {
            vo.setDate(DateUtil.getPresentDate());
            ct = fundCommentRepo.save(vo.vo2FundComment());
        }

        MultipartFile file = vo.getFile();
        if(null != file) {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = tfsConsumer.saveTfsFile(file, ct.getId());
            if (null == newFileName) {
                return RestResp.fail("图片上传失败");
            }
            String images = ct.getImages();
            if(null==images || "".equals(images.trim())){
                ct.setImages(newFileName);
            }else if(images.endsWith(",")){
                ct.setImages(ct.getImages()+images);
            }else {
                ct.setImages(ct.getImages()+","+images);
            }
            ct = fundCommentRepo.save(ct);
            return RestResp.success("头像图片成功",new FundCommentVO(ct));
        }
        return RestResp.fail("上传图片失败");
    }

    public RestResp deleteImage(FundCommentVO vo){
        if(null == vo || null == vo.getId()){
            return RestResp.fail("评论信息错误");
        }
        String deImage = vo.getImages();
        if(null == deImage || "".equals(deImage.trim())){
            return RestResp.fail("请选择需要删除的图片");
        }
        FundComment comment = fundCommentRepo.findOne(vo.getId());
        String images = comment.getImages();
        if(null == null || "".equals(images.trim()) || !images.contains(deImage.trim())){
            return RestResp.fail("图片不存在");
        }
        if(images.contains(deImage+",")){
            comment.setImages(images.replace(deImage+",",""));
        }else {
            comment.setImages(images.replace(deImage,""));
        }
        comment = fundCommentRepo.save(comment);
        return RestResp.success("删除图片成功",comment);
    }

}
