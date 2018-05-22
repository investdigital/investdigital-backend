package info.investdigital.service;

import com.alibaba.fastjson.JSONObject;
import info.investdigital.common.RestResp;
import info.investdigital.common.okhttp.BaseOkHttpClient;
import info.investdigital.common.okhttp.OkHttpClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ccl
 * @time 2018-04-10 9:51
 * @name SmsService
 * @desc:
 */
@Slf4j
@Service
public class SmsService {

    private final String STATUS_STR = "OK";

    private final String DMACLOUD_SINGLE_SMS = "https://sms.dmacloud.com/rest/v2/single_sms";
    private final String DMACLOUD_SINGLE_INDIVIDUATION_SMS = "https://sms.dmacloud.com/rest/v2/single_individuation_sms";
    private final String DMACLOUD_BATCH_SMS = "https://sms.dmacloud.com/rest/v2/batch_sms";
    private final String DMACLOUD_BATCH_INDIVIDUATION_SMS = "https://sms.dmacloud.com/rest/v2/batch_individuation_sms";


    @Value("${web.power.string}")
    private String authorization;

    public RestResp sendSingleIndividuationSms(){

        return RestResp.fail();
    }

    public boolean sendSingleSms(String mobile,String content,int campaignID,String realUrl){
        BaseOkHttpClient baseOkHttpClient = new BaseOkHttpClient();
        Map<String, String> header = new HashMap<>();
        header.put("Authorization",authorization);
        Map<String, Object> body = new HashMap<>();
        body.put("mobile",mobile);
        body.put("content",content);
        body.put("campaignID",campaignID);
        if(null != realUrl){
            body.put("realUrl","");
        }
        boolean flag = false;

        String result = OkHttpClientHelper.post(DMACLOUD_SINGLE_SMS,header,body);
        JSONObject  jsonObject = JSONObject.parseObject(result);
        String status = jsonObject.get("status").toString();
        if(STATUS_STR.equals(status)){
            flag = true;
        }
        /*IResponse response = baseOkHttpClient.post(DMACLOUD_SINGLE_SMS,header,body);
        log.info(response.getData());
        if(IResponse.STATE_OK == response.getCode()){
            String data = response.getData();
            SmsResponse smsResponse = (SmsResponse) JsonUtil.fromJson(data,SmsResponse.class);
            flag = true;
        }*/
        return flag;
    }
}
