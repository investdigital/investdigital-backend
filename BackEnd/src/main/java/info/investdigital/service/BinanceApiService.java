package info.investdigital.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Deposit;
import com.binance.api.client.domain.account.DepositHistory;
import com.binance.api.client.domain.account.Withdraw;
import com.binance.api.client.domain.account.WithdrawHistory;
import info.investdigital.AssetsAPI.binance.BinanceApi;
import info.investdigital.AssetsAPI.binance.entity.DepositWithDraw;
import info.investdigital.AssetsAPI.binance.exception.BinanceException;
import info.investdigital.AssetsAPI.dto.BalanceDTO;
import info.investdigital.AssetsAPI.huobi.entity.price.Kind;
import info.investdigital.common.HttpUtils;
import info.investdigital.common.JsonUtil;
import info.investdigital.common.ResourceParam;
import info.investdigital.dao.asset.BinanceSymbolsDao;
import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static info.investdigital.AssetsAPI.binance.common.BinanceConst.ALL_CURRENCY;
import static info.investdigital.AssetsAPI.binance.common.BinanceConst.BA_MAP;

/**
 * @author luoxuri
 * @create 2018-03-29 17:58
 **/
@Service
public class BinanceApiService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ResourceParam resourceParam;
    @Resource
    private BinanceSymbolsDao binanceSymbolsDao;

    private final Logger LOG = LoggerFactory.getLogger(BinanceApiService.class);
    private final OkHttpClient client = new OkHttpClient();

    private OkHttpClient getClient() {
        return client;
    }

    /**
     * 获取资产总值和持仓信息
     *
     * @return dto
     */
    public BalanceDTO<Kind> getMyAssetNotEmptyBalance(String apiKey, String secretKey) {
        BalanceDTO<Kind> balanceDTO = new BalanceDTO<>();
        try {
            List<BinanceApi.Balance> balanceList = getNotEmptyAccount(apiKey, secretKey);
            List<Kind> kinds = this.dealWithBalance(balanceList);
            BigDecimal myUSDTTotal = getMyUSDTTotal(balanceList);
            BigDecimal usdt2ETHCount = getUSDT2ETHCount(myUSDTTotal);
            String[] doubles = new String[2];
            doubles[0] = myUSDTTotal.toPlainString();
            doubles[1] = usdt2ETHCount.toPlainString();
            balanceDTO.setTotalAsset(doubles);
            balanceDTO.setNotEmptyBalance(kinds);
            return balanceDTO;
        } catch (Exception e) {
            LOG.error("get my total asset and not empty account balance failed: {}", e.getMessage(), e);
        }
        return null;
    }

    private List<Kind> dealWithBalance(List<BinanceApi.Balance> balanceList) {
        List<Kind> tradeList = null;
        if (balanceList != null && balanceList.size() > 0) {
            tradeList = new ArrayList<>(balanceList.size());
            balanceList.stream().forEach(balance -> {
                Kind kind = new Kind();
                kind.setCurrency(balance.getAsset());
                kind.setType("trade");
                kind.setBalance(balance.getFree().add(balance.getLocked()).toPlainString());
            });
        }
        return tradeList;
    }

    /**
     * 获取所有违规的充提记录
     * 大赛期间有充提币记录的数据
     */
    public List<DepositWithDraw> getAllFoulDepositWithdraw(String apiKey, String secretKey, String startTime) throws Exception {
        Assert.assertNotNull("start time can be not null", startTime);
        if (startTime.length() != 13) {
            throw new Exception("start time is not a millisecond");
        }
        List<DepositWithDraw> foulList = new ArrayList<>();
        List<DepositWithDraw> allList = new ArrayList<>();
        Arrays.asList(ALL_CURRENCY).stream().forEach(c -> {
            List<Deposit> list1 = getMyDepositHistory(apiKey, secretKey, c);
            if (list1 != null) {
                list1.stream().forEach(l -> {
                    DepositWithDraw dw = initDeposit(l);
                    allList.add(dw);
                });
            }
            List<Withdraw> list2 = getMyWithDrawHistory(apiKey, secretKey, c);
            if (list2 != null) {
                list2.stream().forEach(l -> {
                    DepositWithDraw dw = initWithdraw(l);
                    allList.add(dw);
                });
            }
        });
        allList.stream().forEach(a -> {
            if (a != null) {
                if (a.getInsertTime() != null) {
                    if (Long.parseLong(startTime) <= Long.parseLong(a.getInsertTime())) {
                        foulList.add(a);
                    }
                }
                if (a.getApplyTime() != null) {
                    if (Long.parseLong(startTime) <= Long.parseLong(a.getApplyTime())) {
                        foulList.add(a);
                    }
                }
            }
        });
        return foulList;
    }

    private DepositWithDraw initWithdraw(Withdraw l) {
        DepositWithDraw dw = new DepositWithDraw();
        dw.setId(l.getId());
        dw.setAmount(l.getAmount());
        dw.setAddress(l.getAddress());
        dw.setAsset(l.getAsset());
        dw.setTxId(l.getTxId());
        dw.setApplyTime(l.getApplyTime());
        dw.setStatus(l.getStatus());
        return dw;
    }

    private DepositWithDraw initDeposit(Deposit l) {
        DepositWithDraw dw = new DepositWithDraw();
        dw.setInsertTime(l.getInsertTime());
        dw.setAmount(l.getAmount());
        dw.setAsset(l.getAsset());
        dw.setTxId(l.getTxId());
        dw.setStatus(l.getStatus());
        return dw;
    }

    /**
     * 获取提币记录
     * 如果获取成功，但是没有记录，返回空的list
     * 如果获取失败，返回null
     *
     * @param asset 例如：ETH
     * @return
     */
    public List<Withdraw> getMyWithDrawHistory(String apiKey, String secretKey, String asset) {
        try {
            BinanceApiRestClient client = BinanceApiClientFactory.newInstance(apiKey, secretKey).newRestClient();
            WithdrawHistory withdrawHistory = client.getWithdrawHistory(asset);
            if (withdrawHistory.isSuccess()) {
                return withdrawHistory.getWithdrawList();
            }
        } catch (Exception e) {
            LOG.error("get my withdraw history failed: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取充币记录
     * 如果获取成功但是没有充币记录，返回一个空的list
     * 如果获取失败，返回null
     *
     * @param asset 例如：ETH
     * @return
     */
    public List<Deposit> getMyDepositHistory(String apiKey, String secretKey, String asset) {
        try {
            BinanceApiRestClient client = BinanceApiClientFactory.newInstance(apiKey, secretKey).newRestClient();
            DepositHistory depositHistory = client.getDepositHistory(asset);
            if (depositHistory.isSuccess()) {
                return depositHistory.getDepositList();
            }
        } catch (Exception e) {
            LOG.error("get my deposit history failed: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取我的所有货币转换成ETH总量
     *
     * @param apiKey
     * @param secretKey
     * @return
     * @throws Exception
     */
    public String[] getMyTotal(String apiKey, String secretKey) {
        try {
            BigDecimal myUSDTTotal = getMyUSDTTotal(apiKey, secretKey);
            BigDecimal usdt2ETHCount = getUSDT2ETHCount(myUSDTTotal);
            String[] doubles = new String[2];
            doubles[0] = myUSDTTotal.toPlainString();
            doubles[1] = usdt2ETHCount.toPlainString();
            return doubles;
        } catch (Exception e) {
            LOG.error("get my total failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取我的所有货币转换成USDT总量
     *
     * @param apiKey
     * @param secretKey
     * @return
     * @throws Exception
     */
    public BigDecimal getMyUSDTTotal(String apiKey, String secretKey) throws Exception {
        BigDecimal[] total = {new BigDecimal(0)};
        List<BinanceApi.Balance> balanceList = getNotEmptyAccount(apiKey, secretKey);
        balanceList.stream().forEach(b -> {
            if ("USDT".equals(b.getAsset())) {
                total[0] = total[0].add(b.getFree().add(b.getLocked()));
            } else {
                try {
                    if (BA_MAP.get(b.getAsset() + "USDT") != null) {
                        BigDecimal usdtCount = getCoin2USDTCount(b.getAsset(), b.getFree().add(b.getLocked()));
                        total[0] = total[0].add(usdtCount);
                    } else if (BA_MAP.get(b.getAsset() + "ETH") != null) {
                        BigDecimal ethCount = getCoin2ETHCount(b.getAsset(), b.getFree().add(b.getLocked()));
                        BigDecimal usdtCount = getETH2USDTPrice(ethCount);
                        total[0] = total[0].add(usdtCount);
                    } else if (BA_MAP.get(b.getAsset() + "BTC") != null) {
                        BigDecimal btcCount = getCoin2BTCCount(b.getAsset(), b.getFree().add(b.getLocked()));
                        BigDecimal usdtCount = getBTC2USDTPrice(btcCount);
                        total[0] = total[0].add(usdtCount);
                    } else {
                        if (binanceSymbolsDao.findBySymbol(b.getAsset() + "USDT") != null) {
                            BA_MAP.put(b.getAsset() + "USDT", b.getAsset() + "USDT");
                            BigDecimal usdtCount = getCoin2USDTCount(b.getAsset(), b.getFree().add(b.getLocked()));
                            total[0] = total[0].add(usdtCount);
                        } else if (binanceSymbolsDao.findBySymbol(b.getAsset() + "ETH") != null) {
                            BA_MAP.put(b.getAsset() + "ETH", b.getAsset() + "ETH");
                            BigDecimal ethCount = getCoin2ETHCount(b.getAsset(), b.getFree().add(b.getLocked()));
                            BigDecimal usdtCount = getETH2USDTPrice(ethCount);
                            total[0] = total[0].add(usdtCount);
                        } else if (binanceSymbolsDao.findBySymbol(b.getAsset() + "BTC") != null) {
                            BA_MAP.put(b.getAsset() + "BTC", b.getAsset() + "BTC");
                            BigDecimal btcCount = getCoin2BTCCount(b.getAsset(), b.getFree().add(b.getLocked()));
                            BigDecimal usdtCount = getBTC2USDTPrice(btcCount);
                            total[0] = total[0].add(usdtCount);
                        } else {
                            // 不支持的交易对的货币，被视为无价值货币，默认是0
                            total[0] = total[0].add(new BigDecimal(0));
                        }
                    }
                } catch (Exception e) {
                    LOG.error("get my USDT total failed: {}", e.getMessage(), e);
                }
            }
        });
        return total[0];
    }

    public BigDecimal getMyUSDTTotal(List<BinanceApi.Balance> balanceList) throws Exception {
        BigDecimal[] total = {new BigDecimal(0)};
        balanceList.stream().forEach(b -> {
            if ("USDT".equals(b.getAsset())) {
                total[0] = total[0].add(b.getFree().add(b.getLocked()));
            } else {
                try {
                    if (BA_MAP.get(b.getAsset() + "USDT") != null) {
                        BigDecimal usdtCount = getCoin2USDTCount(b.getAsset(), b.getFree().add(b.getLocked()));
                        total[0] = total[0].add(usdtCount);
                    } else if (BA_MAP.get(b.getAsset() + "ETH") != null) {
                        BigDecimal ethCount = getCoin2ETHCount(b.getAsset(), b.getFree().add(b.getLocked()));
                        BigDecimal usdtCount = getETH2USDTPrice(ethCount);
                        total[0] = total[0].add(usdtCount);
                    } else if (BA_MAP.get(b.getAsset() + "BTC") != null) {
                        BigDecimal btcCount = getCoin2BTCCount(b.getAsset(), b.getFree().add(b.getLocked()));
                        BigDecimal usdtCount = getBTC2USDTPrice(btcCount);
                        total[0] = total[0].add(usdtCount);
                    } else {
                        if (binanceSymbolsDao.findBySymbol(b.getAsset() + "USDT") != null) {
                            BA_MAP.put(b.getAsset() + "USDT", b.getAsset() + "USDT");
                            BigDecimal usdtCount = getCoin2USDTCount(b.getAsset(), b.getFree().add(b.getLocked()));
                            total[0] = total[0].add(usdtCount);
                        } else if (binanceSymbolsDao.findBySymbol(b.getAsset() + "ETH") != null) {
                            BA_MAP.put(b.getAsset() + "ETH", b.getAsset() + "ETH");
                            BigDecimal ethCount = getCoin2ETHCount(b.getAsset(), b.getFree().add(b.getLocked()));
                            BigDecimal usdtCount = getETH2USDTPrice(ethCount);
                            total[0] = total[0].add(usdtCount);
                        } else if (binanceSymbolsDao.findBySymbol(b.getAsset() + "BTC") != null) {
                            BA_MAP.put(b.getAsset() + "BTC", b.getAsset() + "BTC");
                            BigDecimal btcCount = getCoin2BTCCount(b.getAsset(), b.getFree().add(b.getLocked()));
                            BigDecimal usdtCount = getBTC2USDTPrice(btcCount);
                            total[0] = total[0].add(usdtCount);
                        } else {
                            // 不支持的交易对的货币，被视为无价值货币，默认是0
                            total[0] = total[0].add(new BigDecimal(0));
                        }
                    }
                } catch (Exception e) {
                    LOG.error("get my USDT total failed: {}", e.getMessage(), e);
                }
            }
        });
        return total[0];
    }

    /**
     * 所有账户
     *
     * @param apiKey
     * @param secretKey
     * @return
     * @throws Exception
     */
    public List<BinanceApi.Balance> getAccount(String apiKey, String secretKey) throws Exception {
        BinanceApi binanceApi = new BinanceApi(apiKey, secretKey, getClient());
        return binanceApi.getAccount().getBalances();
    }

    /**
     * 账户中的非空账户
     *
     * @param apiKey
     * @param secretKey
     * @return
     * @throws Exception
     */
    public List<BinanceApi.Balance> getNotEmptyAccount(String apiKey, String secretKey) throws Exception {
        List<BinanceApi.Balance> list = new ArrayList<>();
        getAccount(apiKey, secretKey).stream().forEach(account -> {
            // free账户非0                                           lock账户非0
            if (account.getFree().compareTo(BigDecimal.ZERO) != 0 || account.getLocked().compareTo(BigDecimal.ZERO) != 0) {
                list.add(account);
            }
        });
        return list;
    }

    /**
     * 获取用户个人的所有交易列表
     *
     * @param apiKey     true
     * @param secretKey  true
     * @param symbol     true
     * @param limit      false
     * @param fromId     false
     * @param recvWindow false default 5000
     * @return 交易列表
     */
    public List<BinanceApi.MyTrades> getMyTrades(String apiKey, String secretKey, String symbol, Integer limit, Long fromId, Long recvWindow) {
        BinanceApi binanceApi = new BinanceApi(apiKey, secretKey, getClient());
        return binanceApi.getMyTrades(symbol, limit, fromId, recvWindow);
    }

    /**
     * 获取用户个人所有订单，正在交易的，取消的和完成的
     *
     * @param apiKey     true
     * @param secretKey  true
     * @param symbol     true
     * @param orderId    false
     * @param limit      false
     * @param recvWindow false default 5000
     * @return
     */
    public List<BinanceApi.Order> getAllOrder(String apiKey, String secretKey, String symbol, Long orderId, Integer limit, Long recvWindow) {
        BinanceApi binanceApi = new BinanceApi(apiKey, secretKey, getClient());
        return binanceApi.getAllOrder(symbol, orderId, limit, recvWindow);
    }

    /**
     * 获取当前货币对应流通货币USDT/ETH/BTC的价格
     *
     * @param coin
     * @param tradeCoin
     * @return
     * @throws Exception
     */
    private BigDecimal getCoin2TradeCoinPrice(String coin, String tradeCoin) throws Exception {
        BigDecimal price = getRedisHashValue(coin + tradeCoin);
        if (price != null) {
            return price;
        }
        throw new Exception("Invalid symbol:{" + coin + tradeCoin + "}");
    }

    /**
     * 获取BTC对应USDT的数量
     *
     * @param coinCount 货币数量
     * @return 转换成USDT的数量
     * @throws Exception
     */
    private BigDecimal getBTC2USDTPrice(BigDecimal coinCount) throws Exception {
        BigDecimal price = getRedisHashValue("BTCUSDT");
        if (price != null) {
            return coinCount.multiply(price);
        }
        throw new Exception("get BTC to USDT price failed");
    }

    /**
     * 获取USDT对应ETH的数量
     *
     * @param usdtCount USDT数量
     * @return 转换成ETH的数量
     * @throws Exception
     */
    private BigDecimal getUSDT2ETHCount(BigDecimal usdtCount) throws Exception {
        BigDecimal price = getRedisHashValue("ETHUSDT");
        if (price != null) {
            return usdtCount.divide(price, 18, BigDecimal.ROUND_CEILING);
        }
        throw new Exception("get ETH to USDT ticket failed");
    }

    /**
     * 获取ETH对应USDT的数量
     *
     * @param coinCount ETH数量
     * @return 转换成USDT数量
     * @throws Exception
     */
    private BigDecimal getETH2USDTPrice(BigDecimal coinCount) throws Exception {
        BigDecimal price = getRedisHashValue("ETHUSDT");
        if (price != null) {
            return coinCount.multiply(price);
        }
        throw new Exception("get ETH to USDT price failed");
    }

    /**
     * 获取当前货币对应ETH的数量
     *
     * @param coinName
     * @param coinCount
     * @return
     * @throws Exception
     */
    private BigDecimal getCoin2BTCCount(String coinName, BigDecimal coinCount) throws Exception {
        String symbol = coinName + "BTC";
        return getResponse(coinCount, symbol);
    }

    /**
     * 获取当前货币对应ETH的数量
     *
     * @param coinName
     * @param coinCount
     * @return
     * @throws Exception
     */
    private BigDecimal getCoin2ETHCount(String coinName, BigDecimal coinCount) throws Exception {
        String symbol = coinName + "ETH";
        return getResponse(coinCount, symbol);
    }

    /**
     * 获取当前货币对应USDT的数量
     *
     * @param coinName
     * @param coinCount
     * @return
     * @throws Exception
     */
    private BigDecimal getCoin2USDTCount(String coinName, BigDecimal coinCount) throws Exception {
        String symbol = coinName + "USDT";
        return getResponse(coinCount, symbol);
    }

    private BigDecimal getResponse(BigDecimal coinCount, String symbol) throws Exception {
        BigDecimal price = getRedisHashValue(symbol);
        if (price != null) {
            return coinCount.multiply(price);
        }
        return null;
    }

    public BigDecimal getRedisHashValue(String symbol) {
        try {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            String value = hashOperations.get(resourceParam.getBinanceMarketKey(), symbol);
            if (value == null) {
                BigDecimal price = getHttpPrice(symbol);
                if (price != null) {
                    hashOperations.put(resourceParam.getBinanceMarketKey(), symbol, price.toPlainString());
                    return price;
                }
                return null;
            }
            return new BigDecimal(value);
        } catch (Exception e) {
            LOG.error("query binance hash redis value failed: {}", e.getMessage(), e);
            return null;
        }
    }

    private BigDecimal getHttpPrice(String symbol) {
        try {
            String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;
            String json = HttpUtils.sendGet(url);
            BinanceApi.Ticker ticker = (BinanceApi.Ticker) JsonUtil.fromJson(json, BinanceApi.Ticker.class);
            if (ticker == null) {
                json = HttpUtils.sendGet(url);
                ticker = (BinanceApi.Ticker) JsonUtil.fromJson(json, BinanceApi.Ticker.class);
            }
            if (ticker != null) {
                if (ticker.getSymbol() != null) {
                    return ticker.getPrice();
                }
                // 请求错误处理
                Object object = JSON.parse(json);
                if (object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    if (jsonObject.getBigDecimal("code") != null) {
                        throw new BinanceException(jsonObject.getInteger("code"), jsonObject.getString("msg"));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("getHttpPrice failed: {} {}", symbol, e.getMessage(), e);
            return null;
        }
        return null;
    }

}
