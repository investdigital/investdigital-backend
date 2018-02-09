package com.oxchains.message.auth;

import com.alibaba.fastjson.JSON;
import com.oxchains.message.dao.TokenKeyDao;
import com.oxchains.message.dao.UserDao;
import com.oxchains.message.domain.TokenKey;
import com.oxchains.message.domain.User;
import com.oxchains.message.utils.ObjectByteUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;

/**
 * @author aiet
 */
@Service
public class JwtService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${jwt.key.store}")
    private String keystore;

    @Value("${jwt.key.pass}")
    private String keypass;

    @Value("${jwt.key.alias}")
    private String keyalias;

    @Value("${jwt.cert}")
    private String cert;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private final UserDao userDao;

    @Resource
    private TokenKeyDao tokenKeyDao;

    public JwtService(UserDao userDao) {
        this.userDao = userDao;
    }

    @PostConstruct
    private void init() throws Exception {
        char[] pass = keypass.toCharArray();
        KeyStore from = KeyStore.getInstance("JKS", "SUN");
        from.load(new ClassPathResource(keystore).getInputStream(), pass);
        privateKey = (ECPrivateKey) from.getKey(keyalias, pass);

        String prvKey = JSON.toJSONString(privateKey);
        LOG.info("私钥序JSON字符串: {}" , prvKey);

        CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
        X509Certificate x509Cert = (X509Certificate) certificatefactory.generateCertificate(new ClassPathResource(cert).getInputStream());
        publicKey = x509Cert.getPublicKey();

        String pubKey = JSON.toJSONString(publicKey);
        LOG.info("公钥序JSON字符串: {}" , pubKey);

        saveTokenKey(publicKey,privateKey);
    }

    public String generate(User user) {
        return new DefaultJwtBuilder().
                setId(UUID.randomUUID().toString()).
                setSubject(user.getId().toString()).
                setExpiration(Date.from(ZonedDateTime.now().plusWeeks(1).toInstant())).claim("id", user.getId()).claim("email", user.getEmail()).claim("monilephone",user.getMobilephone()).claim("loginname",user.getLoginname()).
                signWith(SignatureAlgorithm.ES256, privateKey).
                compact();
    }

    Optional<JwtAuthentication> parse(String token) {
        User user = null;
        try {
            Jws<Claims> jws = new DefaultJwtParser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token);
            Claims claims = jws.getBody();
            String subject=claims.getSubject();
            user = userDao.findByLoginname(subject);
            JwtAuthentication jwtAuthentication = new JwtAuthentication(user, token, claims);
            return Optional.of(jwtAuthentication);
        } catch (Exception e) {
            LOG.error("failed to parse jwt token {}: ", token, e);
        }
        return empty();
    }

    private void saveTokenKey(PublicKey pubKey,PrivateKey priKey){
        TokenKey tokenKey = tokenKeyDao.findOne(1L);
        if(null == tokenKey){
            tokenKey = new TokenKey();
            tokenKey.setCreateTime(new Date());
        }
        tokenKey.setUpdateTime(new Date());
        tokenKey.setPubKey(ObjectByteUtil.toByteArray(pubKey));
        tokenKey.setPriKey(ObjectByteUtil.toByteArray(priKey));

        tokenKeyDao.save(tokenKey);
    }
}
