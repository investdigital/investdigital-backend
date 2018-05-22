package info.investdigital.service;

import info.investdigital.AssetsAPI.huobi.entity.balance.Account;
import info.investdigital.AssetsAPI.huobi.entity.balance.AccountData;
import info.investdigital.AssetsAPI.huobi.entity.depositwithdraw.DepositWithdraw;
import info.investdigital.AssetsAPI.huobi.entity.depositwithdraw.DepositWithdrawData;
import info.investdigital.AssetsAPI.huobi.entity.margin.LoanOrders;
import info.investdigital.AssetsAPI.huobi.entity.margin.Margin;
import info.investdigital.AssetsAPI.huobi.entity.match.Match;
import info.investdigital.AssetsAPI.huobi.entity.match.MatchData;
import info.investdigital.AssetsAPI.huobi.entity.order.OrderData;
import info.investdigital.AssetsAPI.huobi.entity.order.Orders;
import info.investdigital.AssetsAPI.huobi.entity.price.Balance;
import info.investdigital.AssetsAPI.huobi.entity.price.Kind;
import info.investdigital.AssetsAPI.huobi.entity.support.CoinPrice;
import info.investdigital.AssetsAPI.huobi.exception.HuoBiApiException;
import info.investdigital.AssetsAPI.huobi.util.CryptoUtils;
import info.investdigital.AssetsAPI.huobi.util.HttpUtilManager;
import info.investdigital.AssetsAPI.huobi.util.ParamUtils;
import info.investdigital.common.JsonUtil;
import info.investdigital.common.ResourceParam;
import info.investdigital.dao.asset.HuobiCurrencysDao;
import info.investdigital.dao.asset.HuobiSymbolsDao;
import info.investdigital.entity.asset.HuobiCurrency;
import info.investdigital.entity.asset.HuobiSymbol;
import info.investdigital.entity.asset.Symbols;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static info.investdigital.AssetsAPI.huobi.common.HuoBiConst.*;

/**
 * @author luoxuri
 * @create 2018-03-15 14:12
 **/
@Service
public class HuoBiApiService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourceParam resourceParam;
    @Resource
    private HuobiSymbolsDao huobiSymbolsDao;
    @Resource
    private HuobiCurrencysDao huobiCurrencysDao;

    private static final Logger LOG = LoggerFactory.getLogger(HuoBiApiService.class);
    private HttpUtilManager httpClient = HttpUtilManager.getInstance();


    //将持仓信息的 空闲账户和 冻结账户整合
    private List<Kind> dealWithBalance(List<Kind> balanceList) {
        if (balanceList != null && balanceList.size() > 0) {
            List<Kind> tradeList = new ArrayList<>(balanceList.size());
            List<Kind> frozenList = new ArrayList<>(balanceList.size());
            balanceList.forEach(kind -> {
                if (kind.getType().equals("frozen")) {
                    frozenList.add(kind);
                }
                if (kind.getType().equals("trade")) {
                    tradeList.add(kind);
                }
            });
            frozenList.forEach(kind -> {
                Optional<Kind> first = tradeList.stream().filter(kind1 -> kind.getCurrency().equals(kind1.getCurrency())).findFirst();
                if (first.isPresent()) {
                    Kind kind1 = first.get();
                    Double sum = Double.valueOf(kind1.getBalance()) + Double.valueOf(kind.getBalance());
                    kind1.setBalance(sum.toString());
                } else {
                    tradeList.add(kind);
                }
            });
            return tradeList;
        }
        return null;
    }

    /**
     * 获取大于starTime的所有充提币记录
     *
     * @param startTime 大赛开始时间
     */
    public List<DepositWithdrawData> depositWithdraws(String accessKey, String secretKey, String startTime) throws Exception {
        Assert.assertNotNull("start time can not be null", startTime);
        if (startTime.length() != 13) {
            throw new Exception("start time is not a millisecond");
        }

        List<DepositWithdrawData> allList = new ArrayList<>();
        List<DepositWithdrawData> foulList = new ArrayList<>();

        List<HuobiCurrency> list = huobiCurrencysDao.findAll();
        list.stream().forEach(c -> {
            List<DepositWithdrawData> list1 = depositWithdraw(accessKey, secretKey, c.getCurrency(), "deposit", "0", "100");
            if (list1 != null && list1.size() != 0) {
                allList.addAll(list1);
            }
            List<DepositWithdrawData> list2 = depositWithdraw(accessKey, secretKey, c.getCurrency(), "withdraw", "0", "100");
            if (list2 != null && list2.size() != 0) {
                allList.addAll(list2);
            }
        });

        allList.stream().forEach(a -> {
            if (a != null) {
                // 判断时间
                if (Long.parseLong(startTime) <= a.getCreatedat().longValue()) {
                    foulList.add(a);
                }
            }
        });
        return foulList;
    }

    /**
     * 查询虚拟币充值记录
     * 如果没有currency的充提币记录，有时会返回一个null，有时会返回空list,获取方判断的时候直接以null，或者空的list来判断有没有此货币的记录
     *
     * @return
     */
    public List<DepositWithdrawData> depositWithdraw(String accessKey, String secretKey, String currency, String type, String from, String size) {
        String depositWithdrawJson = getDepositWithdraw(accessKey, secretKey, currency, type, from, size);
        depositWithdrawJson = depositWithdrawJson.replaceAll("-", "");
        DepositWithdraw depositWithdraw = JsonUtil.jsonToEntity(depositWithdrawJson, DepositWithdraw.class);
        if (OK.equals(depositWithdraw.getStatus())) {
            return depositWithdraw.getData();
        }
        // 如果没有currency的充提币记录，有时会返回一个null，有时会返回空list,获取方判断的时候直接以null，或者空的list来判断有没有此货币的记录
        return null;
    }

    /**
     * 申请提现虚拟币
     *
     * @return 提现ID
     */
    public String withdraw(String accessKey, String secretKey, String address, String amount, String currency, String fee, String addrTag) {
        try {
            Map<String, String> signMap = getCommonParam(accessKey);
            String sign = CryptoUtils.buildSign(METHOD_POST, SIGN_URL, WITHDRAW_URL, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);

            Map<String, String> paramMap = new HashMap<>();
            setParam(paramMap, SIGN_MAP_ADDRESS, address);
            setParam(paramMap, SIGN_MAP_AMOUNT, amount);
            setParam(paramMap, SIGN_MAP_CURRENCY, currency);
            setParam(paramMap, SIGN_MAP_FEE, fee);
            setParam(paramMap, SIGN_MAP_ADDR_TAG, addrTag);

            return httpClient.requestHttpPost(HUOBI_URL, WITHDRAW_URL, signMap, paramMap);
        } catch (Exception e) {
            LOG.error("withdraw virtual coin error: {}", e.getMessage(), e);
        }
        return null;
    }

    private String getDepositWithdraw(String accessKey, String secretKey, String currency, String type, String from, String size) {
        try {
            Map<String, String> signMap = getCommonParam(accessKey);
            setParam(signMap, SIGN_MAP_CURRENCY, currency);
            setParam(signMap, SIGN_MAP_TYPE, type);
            setParam(signMap, SIGN_MAP_FROM, from);
            setParam(signMap, SIGN_MAP_SIZE, size);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, DEPOSIT_WITHDRAW, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);

            return httpClient.requestHttpGet(HUOBI_URL, DEPOSIT_WITHDRAW, signMap);
        } catch (Exception e) {
            LOG.error("get deposit withdraw info error", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取我的账户所有货币对应的总ETH数量 和usdt数量
     *
     * @return ethCount
     */
    public BigDecimal totalAssets(String accessKey, String secretKey) {
        try {
            BigDecimal[] total = {new BigDecimal(0)};
            List<Kind> list = hasPosition(accessKey, secretKey);
            list.stream().forEach(a -> {
                if (!USDT.equals(a.getCurrency())) {
                    BigDecimal currentCoin2UsdtPrice = getCurrentCoin2UsdtPrice(a.getCurrency());
                    BigDecimal coinCount = new BigDecimal(a.getBalance());
                    BigDecimal usdt = coinCount.multiply(currentCoin2UsdtPrice);
                    total[0] = total[0].add(usdt);
                } else {
                    total[0] = total[0].add(new BigDecimal(a.getBalance()));
                }
            });
            return total[0];
        } catch (Exception e) {
            LOG.error("totalAssets error:{}", e.getMessage(), e);
        }
        return null;
    }

    // 当前货币兑换USDT的价格
    private BigDecimal getCurrentCoin2UsdtPrice(String coin) {
        try {
            // 获取各支持的交易对集合
            List<HuobiSymbol> list = getSymbol();

            // 返回coin对usdt价格
            List<String> usdtList = getSupportCoinList(list, USDT);
            if (usdtList != null) {
                if (usdtList.contains(coin)) {
                    return getRedisHashValue(coin + USDT);
                }
            }

            // 返回coin对eth价格
            List<String> ethList = getSupportCoinList(list, ETH);
            if (ethList != null) {
                if (ethList.contains(coin)) {
                    return getRedisHashValue(coin + ETH);
                }
            }

            // 返回coin对btc价格
            List<String> btcList = getSupportCoinList(list, BTC);
            if (btcList != null) {
                if (btcList.contains(coin)) {
                    return getRedisHashValue(coin + BTC);
                }
            }
        } catch (Exception e) {
            LOG.error("query current coin to usdt price failed: {}", e.getMessage(), e);
        }
        return null;
    }
    
    // 获取火币支持的所有交易对
    private List<HuobiSymbol> getSymbol() {
        try {
            List<HuobiSymbol> list = huobiSymbolsDao.findAll();
            if(list != null){
                return list;
            }
            String json = httpClient.requestHttpGet(HUOBI_URL, SUPPORT_SYMBOL_URL);
            if (json == null && json.isEmpty()) {
                json = httpClient.requestHttpGet(HUOBI_URL, SUPPORT_SYMBOL_URL);
            }
            json = json.replaceAll("-", "");
            Symbols symbols = JsonUtil.jsonToEntity(json, Symbols.class);
            if (OK.equals(symbols.getStatus())) {
                return symbols.getData();
            }
        } catch (Exception e) {
            LOG.error("getSymbol failed: {}", e.getMessage(), e);
        }
        return null;
    }

    public List<Kind> hasPosition(String accessKey, String secretKey) throws Exception {
        try {
            Balance spotBalance = null;
            Balance otcBalance = null;

            Map<String, String> map = getAllAccountId(accessKey, secretKey);

            String spotId = map.get(ACCOUNT_SPOT);
            if (spotId != null) {
                String spotJson = singlePosition(accessKey, secretKey, spotId);
                spotBalance = JsonUtil.jsonToEntity(spotJson, Balance.class);
            }

            String otcId = map.get(ACCOUNT_OTC);
            if (otcId != null) {
                String otcJson = singlePosition(accessKey, secretKey, otcId);
                otcBalance = JsonUtil.jsonToEntity(otcJson, Balance.class);
            }
            List<Kind> kindList = new ArrayList<>();
            if (spotBalance != null && OK.equals(spotBalance.getStatus())) {
                spotBalance.getData().getList().stream().forEach(o -> {
                    Kind kind = JsonUtil.objectToEntity(o, Kind.class);
                    BigDecimal bd = new BigDecimal(kind.getBalance());
                    if (bd.compareTo(ZERO) > 0) {
                        kindList.add(kind);
                    }
                });
            }

            if (otcBalance != null && OK.equals(otcBalance.getStatus())) {
                otcBalance.getData().getList().stream().forEach(o -> {
                    Kind kind = JsonUtil.objectToEntity(o, Kind.class);
                    BigDecimal bd = new BigDecimal(kind.getBalance());
                    if (bd.compareTo(ZERO) > 0) {
                        kindList.add(kind);
                    }
                });
            }
            return kindList.stream()
                    .filter(a -> new BigDecimal(a.getBalance()).compareTo(BigDecimal.ZERO) != 0)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("getNotEmptyAccountBalance failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String singlePosition(String accessKey, String secretKey, String accountId) throws Exception {
        try {
            String url = String.format(ACCOUNT_BALANCE, accountId);
            Map<String, String> signMap = getCommonParam(accessKey);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, url, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);
            return httpClient.requestHttpGet(HUOBI_URL, url, signMap);
        } catch (Exception e) {
            LOG.error("fetch account balance error:{}", e.getMessage(), e);
            throw e;
        }
    }

    // 获取所有的账户ID
    private Map<String, String> getAllAccountId(String accessKey, String secretKey) throws Exception {
        try {
            Map<String, String> map = new HashMap<>();
            Account account = getAccounts(accessKey, secretKey);
            List<AccountData> accountDataList = account.getData();
            Optional<AccountData> spot = accountDataList.stream().filter(a -> ACCOUNT_SPOT.equals(a.getType())).findFirst();
            if (spot.isPresent()) {
                map.put(ACCOUNT_SPOT, spot.get().getId().toString());
            }
            Optional<AccountData> otc = accountDataList.stream().filter(a -> ACCOUNT_OTC.equals(a.getType())).findFirst();
            if (otc.isPresent()) {
                map.put(ACCOUNT_OTC, otc.get().getId().toString());
            }
            return map;
        } catch (Exception e) {
            LOG.error("getAllAccountId failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Account getAccounts(String accessKey, String secretKey) throws Exception {
        try {
            Map<String, String> signMap = getCommonParam(accessKey);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, ACCOUNT_URL, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);
            String accountsJson = httpClient.requestHttpGet(HUOBI_URL, ACCOUNT_URL, signMap);
            Account account = JsonUtil.jsonToEntity(accountsJson, Account.class);
            if (OK.equals(account.getStatus())) {
                return account;
            }
            String str = accountsJson.replaceAll("-", "");
            Account errAccount = JsonUtil.jsonToEntity(str, Account.class);
            throw new HuoBiApiException(errAccount.getErrmsg());
        } catch (Exception e) {
            LOG.error("getAccounts error", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据订单ID获取详情
     *
     * @param accessKey key
     * @param secretKey key
     * @param orderId   订单ID
     * @return json
     */
    public String ordersById(String accessKey, String secretKey, String orderId) {
        try {
            String url = String.format(OEDERS_BY_ID, orderId);
            Map<String, String> signMap = getCommonParam(accessKey);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, url, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);

            return httpClient.requestHttpGet(HUOBI_URL, url, signMap);
        } catch (Exception e) {
            LOG.error("fetch orders error:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 查询当前委托、历史委托
     *
     * @param accessKey key
     * @param secretKey key
     * @param symbol    qtumusdt
     * @return list
     */
    public List<OrderData> ordersInfo(String accessKey, String secretKey, String symbol) {
        String ordersInfoJson = queryOrders(accessKey, secretKey, symbol, null, null, null, "filled", null, null, null);
        ordersInfoJson = ordersInfoJson.replaceAll("-", "");
        Orders orders = (Orders) JsonUtil.fromJson(ordersInfoJson, Orders.class);
        if (OK.equals(orders.getStatus())) {
            return orders.getData();
        }
        return null;
    }

    /**
     * 查询所有的借贷订单
     */
    public List<LoanOrders> loanOrders(String accessKey, String secretKey, String size, String startDate) {
        List<HuobiSymbol> list = huobiSymbolsDao.findAll();
        List<LoanOrders> resultList = new ArrayList<>();
        list.stream().forEach(l -> {
            String symbol = l.getBasecurrency() + l.getQuotecurrency();
            List<LoanOrders> list1 = getLoanOrders(accessKey, secretKey, symbol, size, startDate);
            if (list1 != null && list1.size() != 0) {
                resultList.addAll(list1);
            }
        });
        return resultList;
    }

    private List<LoanOrders> getLoanOrders(String accessKey, String secretKey, String symbol, String size, String startDate) {
        String json = queryLoanOrders(accessKey, secretKey, symbol, size, startDate);
        if (!json.isEmpty()) {
            Margin margin = (Margin) JsonUtil.fromJson(json, Margin.class);
            if (OK.equals(margin.getStatus())) {
                return margin.getData();
            }
        }
        return null;
    }

    /**
     * 查询当前成交、成交历史
     *
     * @param accessKey key
     * @param secretKey key
     * @param symbol    qtumusdt
     * @return list
     */
    public List<MatchData> matchInfo(String accessKey, String secretKey, String symbol, String startDate, String endDate, String size) throws Exception {
        try {
            if (startDate == null) {
                throw new Exception("start time can not be null");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = sdf.format(Long.parseLong(startDate));
            String matchResultJson;
            if (endDate != null) {
                String endTime = sdf.format(Long.parseLong(endDate));
                matchResultJson = queryMatchresults(accessKey, secretKey, symbol, null, startTime, endTime, null, null, size);
            } else {
                matchResultJson = queryMatchresults(accessKey, secretKey, symbol, null, startTime, null, null, null, size);
            }

            if (matchResultJson != null && !matchResultJson.isEmpty()) {
                matchResultJson = matchResultJson.replaceAll("-", "");
                Match match = (Match) JsonUtil.fromJson(matchResultJson, Match.class);
                if (OK.equals(match.getStatus())) {
                    return match.getData();
                }
            }
        } catch (Exception e) {
            LOG.error("getMatchResultInfo failed: {}", e.getMessage(), e);
        }
        return null;
    }

    private String queryLoanOrders(String accessKey, String secretKey, String symbol, String size, String startDate) {
        try {
            Map<String, String> signMap = getCommonParam(accessKey);
            setParam(signMap, SIGN_MAP_SYMBOL, symbol);
            setParam(signMap, SIGN_MAP_SIZE, size);
            setParam(signMap, "start-date", startDate);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, "/v1/margin/loan-orders", signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);
            return httpClient.requestHttpGet(HUOBI_URL, "/v1/margin/loan-orders", signMap);
        } catch (Exception e) {
            LOG.error("queryLoanOrders failed: {}", e.getMessage(), e);
        }
        return null;
    }

    private String queryOrders(String accessKey, String secretKey, String symbol, String types, String startDate, String endDate, String states, String from, String direct, String size) {
        try {
            Map<String, String> signMap = getCommonParam(accessKey);
            setParam(signMap, SIGN_MAP_SYMBOL, symbol);
            setParam(signMap, SIGN_MAP_TYPES, types);
            setParam(signMap, SIGN_MAP_START_DATE, startDate);
            setParam(signMap, SIGN_MAP_END_DATE, endDate);
            setParam(signMap, SIGN_MAP_STATES, states);
            setParam(signMap, SIGN_MAP_FROM, from);
            setParam(signMap, SIGN_MAP_DIRECT, direct);
            setParam(signMap, SIGN_MAP_SIZE, size);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, ORDERS_URL, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);

            return httpClient.requestHttpGet(HUOBI_URL, ORDERS_URL, signMap);
        } catch (Exception e) {
            LOG.error("fetch ordersInfo error: {}", e.getMessage(), e);
        }
        return null;
    }

    private String queryMatchresults(String accessKey, String secretKey, String symbol, String types, String startDate, String endDate, String from, String direct, String size) {
        try {
            Map<String, String> signMap = getCommonParam(accessKey);
            setParam(signMap, SIGN_MAP_SYMBOL, symbol);
            setParam(signMap, SIGN_MAP_TYPES, types);
            setParam(signMap, SIGN_MAP_START_DATE, startDate);
            setParam(signMap, SIGN_MAP_END_DATE, endDate);
            setParam(signMap, SIGN_MAP_FROM, from);
            setParam(signMap, SIGN_MAP_DIRECT, direct);
            setParam(signMap, SIGN_MAP_SIZE, size);
            String sign = CryptoUtils.buildSign(METHOD_GET, SIGN_URL, MATCH_RESULT_URL, signMap, secretKey);
            signMap.put(SIGN_MAP_SIGNATURE, sign);
            return httpClient.requestHttpGet(HUOBI_URL, MATCH_RESULT_URL, signMap);
        } catch (Exception e) {
            LOG.error("fetch match result error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取k线数据
     *
     * @param symbol 交易对
     * @param period 1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
     * @param size
     * @return
     */
    private String kline(String symbol, String period, String size) {
        try {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("symbol", symbol);
            paramMap.put("period", period);
            paramMap.put("size", size);
            return httpClient.requestHttpGet(HUOBI_URL, KLINE_URL, paramMap);
        } catch (Exception e) {
            LOG.error("get kline data failed: {}", e.getMessage(), e);
        }
        return null;
    }

    // 获取支持USDT/ETH/BTC的各个交易对的货币集合
    private List<String> getSupportCoinList(List<HuobiSymbol> list, String quoteCoin) {
        try {
            List<HuobiSymbol> hsList = list.stream().filter(s -> s.getQuotecurrency().equals(quoteCoin)).collect(Collectors.toList());
            List<String> coinList = hsList.stream().map(s -> s.getBasecurrency()).collect(Collectors.toList());
            return coinList;
        } catch (Exception e) {
            LOG.error("getSupportUsdtCoinList failed: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * ETH转换USDT价格
     *
     * @return price
     */
    private String getEthToUsdtPrice() {
        try {
            BigDecimal price = getRedisHashValue("ethusdt");
            if (price != null) {
                return price.toPlainString();
            }
        } catch (Exception e) {
            LOG.error("Get ETH to USDT price error:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * BTC转换USDT价格
     *
     * @return price
     */
    private String getBtcToUsdtPrice() {
        try {
            BigDecimal price = getRedisHashValue("btcusdt");
            if (price != null) {
                return price.toPlainString();
            }
        } catch (Exception e) {
            LOG.error("Get BTC to USDT price error:{}", e.getMessage(), e);
        }
        return null;
    }

    public BigDecimal getRedisHashValue(String symbol) {
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String value = hashOperations.get(resourceParam.getHuobiMarketKey(), symbol);
            if (value == null) {
                BigDecimal price = getHttpPrice(symbol);
                if (price != null) {
                    hashOperations.put(resourceParam.getHuobiMarketKey(), symbol, price.toPlainString());
                    return price;
                }
                return null;
            }
            return new BigDecimal(value);
        } catch (Exception e) {
            LOG.error("query huobi hash redis value failed: {}", e.getMessage(), e);
            return null;
        }
    }

    private BigDecimal getHttpPrice(String symbol) {
        try {
            String json = httpClient.requestHttpGet(HUOBI_URL, "/market/detail?symbol=" + symbol);
            CoinPrice cp = (CoinPrice) JsonUtil.fromJson(json, CoinPrice.class);
            if (cp == null) {
                json = httpClient.requestHttpGet(HUOBI_URL, "/market/detail?symbol=" + symbol);
                cp = (CoinPrice) JsonUtil.fromJson(json, CoinPrice.class);
            }
            if (cp != null) {
                if (OK.equals(cp.getStatus())) {
                    return new BigDecimal(cp.getTick().getClose());
                }
            }
        } catch (Exception e) {
            LOG.error("getHttpPrice failed: {} {}", symbol, e.getMessage(), e);
            return null;
        }
        return null;
    }

    private static Map<String, String> getCommonParam(String accessKey) {
        Map<String, String> signMap = new HashMap<String, String>();
        signMap.put(SIGN_MAP_ACCESS_KEY, accessKey);
        signMap.put(SIGN_MAP_SIGNATURE_METHOD, SIGNATURE_METHOD);
        signMap.put(SIGN_MAP_SIGNATURE_VERSION, SIGNATURE_VERSION);
        signMap.put(SIGN_MAP_TIMESTAMP, ParamUtils.getUTCDate());
        return signMap;
    }

    private static void setParam(Map paramMap, String key, String value) {
        if (value != null) {
            paramMap.put(key, value);
        }
    }


}
