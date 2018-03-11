package info.investdigital.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: huohuo
 * Created in 13:57  2018/2/1.
 */
@Component
public class PriceListening {
    public static Float eth = 0f;
    private String eth_url = "https://api.coinmarketcap.com/v1/ticker/ethereum/";
    @Scheduled(cron = "*/5 * * * * ?")
    public void listening(){
        String ethStr = HttpUtils.sendGet(eth_url);
        List<JSONObject> eths = JsonUtil.jsonToList(ethStr, JSONObject.class);
        if(eths != null && eths.size()>=1){
            String s = eths.get(0).get("price_usd").toString();
            eth = Float.valueOf(s);
        }
    }
}
