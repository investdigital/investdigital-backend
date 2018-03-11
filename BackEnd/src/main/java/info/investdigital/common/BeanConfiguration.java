package info.investdigital.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @Author: huohuo
 * Created in 11:35  2018/2/8.
 */
@Component
public class BeanConfiguration {
    @Value("${eth.web3j.url}")
    private String web3jUrl;
    @Bean
    public Web3j getWeb3j(){
        Web3j web3j = Web3j.build(new HttpService(web3jUrl));
        return web3j;
    }
}
