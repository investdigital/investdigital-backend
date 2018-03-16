package com.oxchains.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author luoxuri
 * @create 2018-02-01 14:05
 **/
@EnableTransactionManagement
@EnableAutoConfiguration
@SpringBootApplication
public class MessageServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageServerApplication.class, args);
    }
}
