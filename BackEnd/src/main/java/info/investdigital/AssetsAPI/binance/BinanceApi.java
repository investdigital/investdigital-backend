package info.investdigital.AssetsAPI.binance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.investdigital.AssetsAPI.binance.common.BinanceCryptUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

//import info.investdigital.service.BinanceApiService;

/**
 * @author Ox
 * @createTime x
 */
@Slf4j
public class BinanceApi {
    private static final String BINANCE_HOST = "https://www.binance.com";
    private static final String SERVER_TIME_URL = BINANCE_HOST + "/api/v1/time";
    private static final String LATEST_PRICE_URL = BINANCE_HOST + "/api/v1/ticker/allPrices";
    private static final String DEPTH_URL = BINANCE_HOST + "/api/v1/depth";
    private static final String ORDER_URL = BINANCE_HOST + "/api/v3/order";
    private static final String OPEN_ORDERS_URL = BINANCE_HOST + "/api/v3/openOrders";
    private static final String ACCOUNT_URL = BINANCE_HOST + "/api/v3/account";
    private static final String MYTRADE_URL = BINANCE_HOST + "/api/v3/myTrades";
    private static final String ALL_ORDER_URL = BINANCE_HOST + "/api/v3/allOrders";
    private static final List DEPTH_LEGAL_NUMBERS = Lists.newArrayList(50, 20, 100, 500, 5, 200, 10);

    private String apiKey;
    private String secretKey;
    private OkHttpClient client;
    public BinanceApi(String apiKey, String secretKey, OkHttpClient client) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.client = client;
    }

    public Long getServerTime() {
        String respContent = new BinanceRequest(SERVER_TIME_URL).get()
                .execute();
        return JSON.parseObject(respContent).getLong("serverTime");
    }

    public BigDecimal getLatestPrice(final String symbol) {
        Assert.assertNotNull("symbol can not be null", symbol);

        String respContent = new BinanceRequest(LATEST_PRICE_URL).get()
                .execute();
        List<Ticker> tickers = JSON.parseObject(respContent, new TypeReference<List<Ticker>>() {
        });
        Ticker ticker = Iterables.find(tickers, new Predicate<Ticker>() {
            @Override
            public boolean apply(Ticker input) {
                return input.getSymbol().equals(symbol);
            }
        }, null);
        if (ticker == null) {
            log.error("invalid symbol {}", symbol);
        }
        return ticker == null ? null : ticker.getPrice();
    }

    public OrderBook getDepth(String symbol) {
        return getDepth(symbol, 100);
    }


    public OrderBook getDepth(String symbol, Integer limit) {
        limit = limit == null ? 100 : limit;
        Assert.assertTrue("Illegal characters found in parameter 'limit'; legal range is '50, 20, 100, 500, 5, 200, 10'.", DEPTH_LEGAL_NUMBERS.contains(limit));

        String respContent = new BinanceRequest(DEPTH_URL).get()
                .addParam("symbol", symbol)
                .addParam("limit", limit.toString())
                .execute();
        return buildOrderBook(respContent);
    }

    public PlaceOrderResponse placeLimitOrder(PlaceOrderRequest placeOrderRequest) {
        Assert.assertNotNull("limit order price can not be null", placeOrderRequest.getPrice());

        placeOrderRequest.setOrderType(OrderType.LIMIT);
        return placeOrder(placeOrderRequest);
    }

    public PlaceOrderResponse placeMarketOrder(PlaceOrderRequest placeOrderRequest) {
        Assert.assertNull("market order do not need price", placeOrderRequest.getPrice());

        placeOrderRequest.setOrderType(OrderType.MARKET);
        return placeOrder(placeOrderRequest);
    }

    private PlaceOrderResponse placeOrder(PlaceOrderRequest placeOrderRequest) {
        Assert.assertNotNull("symbol can not be null", placeOrderRequest.getSymbol());
        Assert.assertNotNull("order side can not be null", placeOrderRequest.getOrderSide());
        Assert.assertNotNull("order type can not be null", placeOrderRequest.getOrderType());
        Assert.assertNotNull("order quantity can not be null", placeOrderRequest.getQuantity());

        BinanceRequest bRequest = new BinanceRequest(ORDER_URL).post()
                .addParam("symbol", placeOrderRequest.getSymbol())
                .addParam("side", placeOrderRequest.getOrderSide().name())
                .addParam("type", placeOrderRequest.getOrderType().name())
                .addParam("quantity", placeOrderRequest.getQuantity().toString());
        if (placeOrderRequest.getOrderType() == OrderType.LIMIT) {
            bRequest.addParam("price", placeOrderRequest.getPrice().toString());
            bRequest.addParam("timeInForce", placeOrderRequest.getOrderTimeInForce().name());
        }
        if (placeOrderRequest.getNewClientOrderId() != null) {
            bRequest.addParam("newClientOrderId", placeOrderRequest.getNewClientOrderId());
        }
        if (placeOrderRequest.getStopPrice() != null) {
            bRequest.addParam("stopPrice", placeOrderRequest.getStopPrice().toString());
        }
        if (placeOrderRequest.getIcebergQty() != null) {
            bRequest.addParam("icebergQty", placeOrderRequest.getIcebergQty().toString());
        }
        String respContent = bRequest.sign().execute();

        return JSON.parseObject(respContent, PlaceOrderResponse.class);
    }

    public Order getOrder(String symbol, Long orderId) {
        return getOrder(symbol, orderId, null, null);
    }

    public Order getOrder(String symbol, Long orderId, String origClientOrderId, Long recvWindow) {
        Assert.assertNotNull("symbol can not be null", symbol);

        BinanceRequest bRequest = new BinanceRequest(ORDER_URL).get()
                .addParam("symbol", symbol);
        if (orderId != null) {
            bRequest.addParam("orderId", orderId.toString());
        }
        if (origClientOrderId != null) {
            bRequest.addParam("origClientOrderId", origClientOrderId.toString());
        }
        if (recvWindow != null) {
            bRequest.addParam("recvWindow", recvWindow.toString());
        }
        String respContent = bRequest.sign().execute();
        return JSON.parseObject(respContent, Order.class);
    }

    public List<Order> openOrders(String symbol) {
        return openOrders(symbol, null);
    }

    public List<Order> openOrders(String symbol, Long recvWindow) {
        Assert.assertNotNull("symbol can not be null", symbol);
        BinanceRequest bRequest = new BinanceRequest(OPEN_ORDERS_URL)
                .addParam("symbol", symbol);
        if (recvWindow != null) {
            bRequest.addParam("recvWindow", recvWindow.toString());
        }
        String respContent = bRequest.sign().execute();
        return JSON.parseObject(respContent, new TypeReference<List<Order>>() {
        });
    }

    public CancelOrderResponse cancelOrder(String symbol, Long orderId) {
        return cancelOrder(symbol, orderId, null, null, 20000L);
    }

    public CancelOrderResponse cancelOrder(String symbol, Long orderId, String origClientOrderId, String newClientOrderId, Long recvWindow) {
        Assert.assertNotNull("symbol can not be null", symbol);
        Assert.assertTrue("orderId or origClientOrderId must be send", orderId != null || origClientOrderId != null);
        BinanceRequest bRequest = new BinanceRequest(ORDER_URL).delete()
                .addParam("symbol", symbol);
        if (orderId != null) {
            bRequest.addParam("orderId", orderId.toString());
        }
        if (origClientOrderId != null) {
            bRequest.addParam("origClientOrderId", origClientOrderId.toString());
        }
        if (newClientOrderId != null) {
            bRequest.addParam("origClientOrderId", newClientOrderId.toString());
        }
        if (recvWindow != null) {
            bRequest.addParam("recvWindow", recvWindow.toString());
        }

        String respContent = bRequest.sign().execute();
        return JSON.parseObject(respContent, CancelOrderResponse.class);
    }

    /**
     * 获取账户信息 recvWindow默认5000
     *
     * @return accountInfo
     */
    public AccountInfo getAccount() {
        return getAccount(null);
    }

    /**
     * 自定义调用间隔时间获取账户信息
     *
     * @param recvWindow 安全设置,调用时间和服务器时间间隔不能大于recvWindow
     * @return accountInfo
     */
    public AccountInfo getAccount(Long recvWindow) {
        BinanceRequest bRequest = new BinanceRequest(ACCOUNT_URL).get();
        if (recvWindow != null) {
            bRequest.addParam("recvWindow", recvWindow.toString());
        }
        String respConent = bRequest.sign().execute();
        return JSON.parseObject(respConent, AccountInfo.class);
    }

    /**
     * 获取用户个人的所有交易列表
     *
     * @param symbol
     * @return
     */
    public List<MyTrades> getMyTrades(String symbol, Integer limit, Long fromId, Long recvWindow) {
        BinanceRequest bRequest = new BinanceRequest(MYTRADE_URL).addParam("symbol", symbol).get();
        if (limit != null) {
            bRequest.addParam("limit", limit.toString());
        }
        if (fromId != null) {
            bRequest.addParam("fromId", fromId.toString());
        }
        if (fromId != null) {
            bRequest.addParam("recvWindow", recvWindow.toString());
        }
        String respContent = bRequest.sign().execute();
        return JSON.parseArray(respContent, MyTrades.class);
    }

    /**
     * 获取用户所有订单,正在交易的,取消的和完成的
     * @param symbol
     * @param orderId
     * @param limit
     * @param recvWindow
     * @return
     */
    public List<Order> getAllOrder(String symbol, Long orderId, Integer limit, Long recvWindow) {
        Assert.assertNotNull("symbol can not be null", symbol);

        BinanceRequest bRequest = new BinanceRequest(ALL_ORDER_URL).get().addParam("symbol", symbol);
        if (orderId != null) {
            bRequest.addParam("orderId", orderId.toString());
        }
        if (limit != null) {
            bRequest.addParam("limit", limit.toString());
        }
        if (recvWindow != null) {
            bRequest.addParam("recvWindow", recvWindow.toString());
        }
        String respContent = bRequest.sign().execute();
        return JSON.parseArray(respContent, Order.class);
    }

    private OrderBook buildOrderBook(String respContent) {
        JSONObject jsonObject = JSON.parseObject(respContent);
        JSONArray bidsArray = jsonObject.getJSONArray("bids");
        List<OrderBookItem> bids = new ArrayList<OrderBookItem>();
        for (int i = 0; i < bidsArray.size(); i++) {
            JSONArray bidArray = bidsArray.getJSONArray(i);
            OrderBookItem item = new OrderBookItem(new BigDecimal(bidArray.getString(0)), new BigDecimal(bidArray.getString(1)));
            bids.add(item);
        }
        JSONArray asksArray = jsonObject.getJSONArray("asks");
        List<OrderBookItem> asks = new ArrayList<OrderBookItem>();
        for (int i = 0; i < asksArray.size(); i++) {
            JSONArray askArray = asksArray.getJSONArray(i);
            OrderBookItem item = new OrderBookItem(new BigDecimal(askArray.getString(0)), new BigDecimal(askArray.getString(1)));
            asks.add(item);
        }
        OrderBook orderBook = new OrderBook();
        orderBook.setLastUpdateId(jsonObject.getLong("lastUpdateId"));
        orderBook.setBids(bids);
        orderBook.setAsks(asks);
        return orderBook;
    }

    private String okHttp(String url, Map<String, String> headers, String method, Map<String, String> params) {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (method.equals("POST")
                || method.equals("PUT")
                || method.equals("PATCH")
                || method.equals("PROPPATCH")
                || method.equals("REPORT")
                || method.equals("DELETE")) {

            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = formBodyBuilder.build();
            builder.addHeader("Content-type", "application/x-www-form-urlencoded");
            builder.method(method, requestBody);
        }

        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        try {
            Response response = client.newCall(builder.build()).execute();
            String respContent = response.body().string();
            if (!response.isSuccessful()) {
                log.error("http response code  {}, msg {}", response.code(), respContent);
            }
            return respContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert params map to string format like "key=value&key=value"
     *
     * @param params params
     * @return result
     */
    private String httpBuildQuery(Map<String, String> params) {
        String reString = "";
        if (params.isEmpty()) {
            return reString;
        }
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            reString += key + "=" + value + "&";
        }
        reString = reString.substring(0, reString.length() - 1);
        try {
            reString = java.net.URLEncoder.encode(reString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        reString = reString.replace("%3D", "=").replace("%26", "&");
        return reString;
    }

    /**
     * 检查返回结果,如果返回中有code、msg字段,异常
     *
     * @param respContent
     */
    private void checkResponse(String respContent) {
        Object object = JSON.parse(respContent);
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.getInteger("code") != null) {
                throw new BinanceException(jsonObject.getInteger("code"), jsonObject.getString("msg"));
            }
        }
    }

    /**
     * 自定义异常
     */
    @Data
    public static class BinanceException extends RuntimeException {
        private Integer code;
        private String msg;

        public BinanceException(Integer code, String msg) {
            super(String.format("errorCode %s errorMsg %s", code, msg));
            this.code = code;
            this.msg = msg;
        }
    }

    class BinanceRequest {
        private String url;
        private String method;
        private Boolean sign = false;
        private Map<String, String> headers = Maps.newHashMap();
        private Map<String, String> params = Maps.newHashMap();

        BinanceRequest(String url) {
            this.url = url;
            this.method = "GET";
        }

        BinanceRequest get() {
            this.method = "GET";
            return this;
        }

        BinanceRequest put() {
            this.method = "PUT";
            return this;
        }

        BinanceRequest delete() {
            this.method = "DELETE";
            return this;
        }

        BinanceRequest post() {
            this.method = "POST";
            return this;
        }

        BinanceRequest sign() {
            this.sign = true;
            headers.put("X-MBX-APIKEY", apiKey);
            return this;
        }

        BinanceRequest addParam(String key, String value) {
            params.put(key, value);
            return this;
        }

        BinanceRequest addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        String execute() {
            if (method.equals("GET")) {
                url += sign ? signAndGetHttpQueryStr(params) : "?" + httpBuildQuery(params);
            } else if (sign) {
                params = signAndGetHttpRequestBody(params);
            }
            String respContent = okHttp(url, headers, method, params);
            checkResponse(respContent);
            return respContent;
        }

        private String signAndGetHttpQueryStr(Map<String, String> params) {
            params.put("timestamp", System.currentTimeMillis() + "");
            String queryStr = httpBuildQuery(params);
            String signature = BinanceCryptUtil.hmacSHA256(queryStr, secretKey);
            return "?" + queryStr + "&signature=" + signature;
        }

        private Map<String, String> signAndGetHttpRequestBody(Map<String, String> params) {
            params.put("timestamp", System.currentTimeMillis() + "");
            String queryStr = httpBuildQuery(params);
            String signature = BinanceCryptUtil.hmacSHA512(queryStr, secretKey);
            params.put("signature", signature);
            return params;
        }


    }

    @Data
    public static class PlaceOrderRequest {
        private String symbol;
        private OrderSide orderSide;
        @Setter(AccessLevel.PRIVATE)
        private OrderType orderType;
        private TimeInForce orderTimeInForce = TimeInForce.GTC;
        private BigDecimal quantity;
        private BigDecimal price;
        private String newClientOrderId;
        private BigDecimal stopPrice;
        private BigDecimal icebergQty;
        private Long recvWindow;
    }

    @Data
    public static class Ticker {
        private String symbol;
        private BigDecimal price;
    }

    @Data
    public static class OrderBook {
        private Long lastUpdateId;
        private List<OrderBookItem> asks;
        private List<OrderBookItem> bids;
    }

    @Data
    @AllArgsConstructor
    public static class OrderBookItem {
        private BigDecimal price;
        private BigDecimal number;
    }


    @Data
    public static class PlaceOrderResponse {
        private String symbol;
        private Long orderId;
        private String clientOrderId;
        private Long transactTime;
    }

    @Data
    public static class Order {
        private String symbol;
        private Long orderId;
        private String clientOrderId;
        private BigDecimal price;
        private BigDecimal origQty;
        private BigDecimal executedQty;
        private OrderStatus status;
        private TimeInForce timeInForce;
        private OrderType type;
        private OrderSide side;
        private BigDecimal stopPrice;
        private BigDecimal icebergQty;
        private Date time;
    }

    @Data
    public static class CancelOrderResponse {
        private String symbol;
        private String origClientOrderId;
        private Long orderId;
        private String clientOrderId;
    }

    @Data
    public static class AccountInfo {
        private Long makerCommission;
        private Long takerCommission;
        private Long buyerCommission;
        private Long sellerCommission;
        private Boolean canTrade;
        private Boolean canWithdraw;
        private Boolean canDeposit;
        private List<Balance> balances;
    }

    @Data
    public static class Balance {
        private String asset;
        private BigDecimal free;
        private BigDecimal locked;
    }

    public static void main(String[] args) {
        String[] s = {"1" ,"2" ,"3" ,"4"};
        String[] strings = Arrays.copyOfRange(s, 0, 2);
        System.out.println(strings.length);

    }

    @Data
    public static class MyTrades {
        private Long id;
        private Long orderId;
        private BigDecimal price;
        private BigDecimal qty;
        private BigDecimal commission;
        private String commissionAsset;
        private Long time;
        private Boolean isBuyer;
        private Boolean isMaker;
        private Boolean isBestMatch;
    }

    public enum OrderSide {
        BUY, SELL
    }

    public enum OrderType {
        LIMIT, MARKET
    }

    public enum TimeInForce {
        GTC, IOC
    }

    public enum OrderStatus {
        NEW, PARTIALLY_FILLED, FILLED, CANCELED, PENDING_CANCEL, REJECTED, EXPIRED
    }
}


