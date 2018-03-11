package info.investdigital.service;

import com.alibaba.fastjson.JSONObject;
import info.investdigital.common.HttpClientUtils;
import info.investdigital.common.ParamZero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import javax.annotation.Resource;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * @Author: huohuo
 * Created in 10:08  2018/3/8.
 */
@Service
public class Web3Service {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Resource
    private Web3j web3j;
    //get tx  nonce by address
    public String getNonce(String address)throws Exception{
        try {
            EthGetTransactionCount count = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
            BigInteger transactionCount = count.getTransactionCount();
            int i = transactionCount.intValue();
            String s = Integer.toHexString(i);
            if(s.length() % 2 == 0){
                s = "0x"+s;
            }
            else{
                s = "0x0"+s;
            }
            return s;
        } catch (Exception e) {
            log.error("get nonce fail  address : {}",address,e);
            throw e;
        }
    }
    public String getTxStr(String rawStr)throws Exception{
        String txStr = null;
        try {
            Map map = new HashMap<String,String>();
            map.put("rawTx",rawStr);
            String str = HttpClientUtils.doPost(ParamZero.signServerUrl, map);
            JSONObject o  = (JSONObject) JSONObject.parse(str);
            txStr = (String) o.get("txStr");
            if(txStr == null){
                throw new NullPointerException("txStr is null");
            }
        } catch (Exception e) {
            log.error("get txStr faild:{}",e.getMessage(),e);
            throw e;
        }
        return txStr;
    }
    public boolean sendRawTransaction(String tx) throws IOException {
        try {
            EthSendTransaction send = web3j.ethSendRawTransaction(tx).send();
            if(send.getTransactionHash() == null){
                if(send.hasError()){
                    String message = send.getError().getMessage();
                    if(message.startsWith("replacement")||message.startsWith("known")){
                        log.error("sendRawTransaction error Causy By :{}",message);
                    }
                    if(message.startsWith("insufficient funds for gas * price")){
                        log.error("sendRawTransaction error Causy By Lack of balance:{}",message);
                    }
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            log.error("sendRawTransaction error Causy By :{}",e.getMessage(),e);
            return false;
        }
    }

    public BigInteger getAmount(Double amount)throws Exception{
        try {
            if(amount != null){
                BigDecimal bigDecimal = new BigDecimal(amount);
                BigDecimal bigDecimal2 = new BigDecimal("1000000000000000000");
                BigInteger bigInteger = bigDecimal.multiply(bigDecimal2).toBigInteger();
                return bigInteger;
            }
            throw new NullPointerException("amount is null");
        } catch (Exception e) {
            log.error("get amount faild:{}",e.getMessage(),e);
            throw e;
        }
    }
}
