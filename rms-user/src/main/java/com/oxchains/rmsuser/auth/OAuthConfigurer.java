package com.oxchains.rmsuser.auth;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;

/**
 * @author ccl
 * @time 2018-01-09 16:49
 * @name OAuthConfigurer
 * @desc: OAuth认证配置类
 */
@Slf4j
@EnableAuthorizationServer
@Configuration
public class OAuthConfigurer extends AuthorizationServerConfigurerAdapter {
    @Value("${oxks.key.store}")
    private String keystore;

    @Value("${oxks.key.pass}")
    private String keypass;

    @Value("${oxks.key.alias}")
    private String keyalias;

    @Value("${oxks.cert}")
    private String cert;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        char[] pass = keypass.toCharArray();
        ClassPathResource resource = new ClassPathResource(keystore);
        KeyPair keyPair = new KeyStoreKeyFactory(resource,pass).getKeyPair(keyalias);

        converter.setKeyPair(keyPair);
        return converter;
    }

 /*   @Resource
    private TokenStore tokenStore;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }*/

    //这个bean是必不可少的，不然的话就会初始化报错的。
/*    @Bean
    public ApprovalStore approvalStore() throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }*/

/*    @Resource
    private AuthenticationManager authenticationManager;*/

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("oxchains").secret("oxchains").scopes("read","write")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .autoApprove(true);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(jwtAccessTokenConverter());
    }
}