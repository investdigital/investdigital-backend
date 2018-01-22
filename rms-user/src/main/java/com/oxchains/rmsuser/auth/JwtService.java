package com.oxchains.rmsuser.auth;

import com.alibaba.fastjson.JSON;
import com.oxchains.rmsuser.common.IndexUtils;
import com.oxchains.rmsuser.dao.*;
import com.oxchains.rmsuser.entity.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Resource private PermissionRepo permissionRepo;
    @Resource private UserRoleRepo userRoleRepo;
    @Resource private RolePermissionRepo rolePermissionRepo;
    @Resource private UserPermissionRepo userPermissionRepo;

    private final UserRepo userRepo;

    public JwtService(UserRepo userRepo) {
        this.userRepo = userRepo;
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

    }

    public String generate(User user) {
        return new DefaultJwtBuilder().
                setId(UUID.randomUUID().toString()).
                setSubject(user.getLoginname()).
                setExpiration(Date.from(ZonedDateTime.now().plusWeeks(1).toInstant())).claim("id", user.getId()).claim("email", user.getEmail()).claim("monilephone",user.getMobilephone()).claim("loginname",user.getLoginname()).
                signWith(SignatureAlgorithm.ES256, privateKey).
                compact();
    }

    Optional<JwtAuthentication> parse(String token, String uri) {
        User user = null;
        try {
            Jws<Claims> jws = new DefaultJwtParser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token);
            Claims claims = jws.getBody();
            String subject=claims.getSubject();
            user = userRepo.findByLoginname(subject);


            // 根据userId获取权限列表
            List<Permission> permissionList = permissionRepo.findByUserId(user.getId());
            Set<String> urlSet = new HashSet<>();
            permissionList.stream().forEach(p -> {
                if (p != null){
                    urlSet.add(p.getUrl());
                }
            });

            // 获取userPermission表权限
            List<UserPermission> userPermissionList = userPermissionRepo.findByUserId(user.getId());
            userPermissionList.stream().forEach(p -> {
                Long permissionId = p.getPermissionId();
                Permission permission = permissionRepo.findOne(permissionId);
                if (permission != null){
                    urlSet.add(permission.getUrl());
                }
            });
            user.setPermissionUriSet(urlSet);
            JwtAuthentication jwtAuthentication = new JwtAuthentication(user, token, claims);
            return Optional.of(jwtAuthentication);

            /*
            // 1.权限表没有这个url就放行
            int index = IndexUtils.getIndex(uri, "/");
            String subUri = uri.substring(0, index);
            Permission permission = permissionRepo.findByUrl(subUri);
            if (permission == null){
                return Optional.of(jwtAuthentication);
            }

            // 2.有url,判断user权限
            List<RolePermission> list = checkRolePermissions(user, permission);

            if (list.size() != 0){
                return Optional.of(jwtAuthentication);
            }
            return empty();*/
        } catch (Exception e) {
            LOG.error("failed to parse jwt token {}: ", token, e);
        }
        return empty();
    }

    /** 检查角色权限 */
    private List<RolePermission> checkRolePermissions(User user, Permission permission) {
        // 获取roleId
        Long userId = user.getId();
        List<UserRole> userRoleList = userRoleRepo.findByUserId(userId);
        Set<Long> roleSet = new HashSet<>();
        userRoleList.stream().forEach(ur -> {
            roleSet.add(ur.getRoleId());
        });

        Iterator<Long> it = roleSet.iterator();
        List<RolePermission> list = new ArrayList<>();
        while (it.hasNext()){
            Long roleId = it.next();
            RolePermission rolePermission = rolePermissionRepo.findByRoleIdAndPermissionId(roleId, permission.getId());
            if (rolePermission != null){
                list.add(rolePermission);
            }
        }
        return list;
    }

}
