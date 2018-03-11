package info.investdigital.service;

import info.investdigital.common.GoogleAuthenticator;

import info.investdigital.common.RegexUtils;
import info.investdigital.common.RestResp;
import info.investdigital.dao.GoogleSecretRepo;
import info.investdigital.dao.GoogleSecretUserRepo;

import info.investdigital.dao.UserRepo;
import info.investdigital.entity.GoogleSecret;
import info.investdigital.entity.GoogleSecretUser;

import info.investdigital.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @Author: Gaoyp
 * @Description:
 * @Date: Create in 下午12:52 2018/3/5
 * @Modified By:
 */
@Slf4j
@Service
@Transactional
public class GoogleSecretService {


    @Resource
    private GoogleSecretRepo googleSecretRepo;

    @Resource
    private GoogleSecretUserRepo googleSecretUserRepo;

    @Resource
    private UserRepo userRepo;

    /**
     * @Description: 获取秘钥
     * @Date: Create in 下午1:02 2018/3/5
     */

    public RestResp getSecretKey(Long id){
        try{
            User user = userRepo.findOne(id);
            if (user == null){
                return RestResp.fail("用户不存在");
            }
            GoogleSecretUser googleSecretUser = googleSecretUserRepo.findByUid(id);
            GoogleSecret googleSecret = null;
            if(null != googleSecretUser){
                googleSecret = googleSecretRepo.findOne(googleSecretUser.getSid());
                return RestResp.success("密钥已存在",googleSecret.getSecret());
            }

            String secretKey = GoogleAuthenticator.generateSecretKey();
            if (secretKey == null){
                return RestResp.fail("生成秘钥失败");
            }

            googleSecret = new GoogleSecret(secretKey);
            GoogleSecret secret = googleSecretRepo.save(googleSecret);
            if (secret == null){
                return RestResp.fail("秘钥保存失败");
            }

            googleSecretUser = new GoogleSecretUser(googleSecret.getId(),id);
            GoogleSecretUser secretUser = googleSecretUserRepo.save(googleSecretUser);
            if (secretUser == null){
                return RestResp.fail("秘钥用户关联保存失败");
            }

            return RestResp.success("生成秘钥成功",secretKey);

        }catch (Exception e){
            log.error("获取密钥失败",e);
            return RestResp.fail("获取密钥失败");
        }
    }


    /**
     * @Description: 验证输入验证码
     * @Date: Create in 下午1:02 2018/3/5
     */

    public RestResp authGoogleCode(String code,Long u_id){
        if(null == code || "".equals(code.trim())){
            return RestResp.fail("请输入验证码");
        }
        GoogleSecretUser googleSecretUser = googleSecretUserRepo.findByUid(u_id);
        GoogleSecret googleSecret = googleSecretRepo.findOne(googleSecretUser.getSid());
        String secret = googleSecret.getSecret();
        if (secret == null){
            return RestResp.fail("获取秘钥失败");
        }else {
            GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
            googleAuthenticator.setWindowSize(5);
            boolean b = googleAuthenticator.check_code(secret, Long.valueOf(code), System.currentTimeMillis());
            if (b){
                return RestResp.success("认证成功");
            }else {
                return RestResp.fail("认证码错误");
            }
        }
    }

    public RestResp authGoogleCode(String code,String email){
        if(!RegexUtils.match(email,RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("邮箱格式不正确");
        }
        User user = userRepo.findByEmail(email);
        if(null == user){
            return RestResp.fail("用户不存在");
        }
        return authGoogleCode(code,user.getId());
    }

    public RestResp bindGoogleAuth(String email){
        if(!RegexUtils.match(email,RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("邮箱格式不正确");
        }
        if(!RegexUtils.match(email,RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("邮箱格式不正确");
        }
        User user = userRepo.findByEmail(email);
        if(null == user){
            return RestResp.fail("用户不存在");
        }
        GoogleSecretUser googleSecretUser = googleSecretUserRepo.findByUid(user.getId());
        if(null == googleSecretUser){
            return RestResp.fail("未绑定");
        }
        return RestResp.success("已绑定",googleSecretUser);
    }

}
