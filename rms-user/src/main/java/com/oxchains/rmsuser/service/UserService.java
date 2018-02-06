package com.oxchains.rmsuser.service;


import com.oxchains.rmsuser.auth.JwtService;
import com.oxchains.rmsuser.common.*;
import com.oxchains.rmsuser.dao.UserRepo;
import com.oxchains.rmsuser.dao.UserResourceRepo;
import com.oxchains.rmsuser.entity.User;
import com.oxchains.rmsuser.entity.UserResource;
import com.oxchains.rmsuser.entity.UserVO;
import com.oxchains.rmsuser.entity.VerifyCode;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author ccl
 * @time 2017-12-12 17:08
 * @name UserService
 * @desc:
 */

@Slf4j
@Service
public class UserService {

    //private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepo userRepo;

    @Resource
    JwtService jwtService;

    private String token;

    @Resource
    MailService mailService;

    @Resource
    private KaptchaService kaptchaService;

    @Resource
    private UserResourceRepo userResourceRepo;


    public RestResp addUser(UserVO userVO) {
        boolean mail = false;
        if(null == userVO){
            return RestResp.fail("请正确提交的注册信息");
        }
        if(null == userVO.getLoginname() || !RegexUtils.match(userVO.getLoginname(),RegexUtils.REGEX_NAME_LEN32)){
            return RestResp.fail("请正确填写登录名，只能包含字母、数字、下划线，且只能以字母开头");
        }
        if(null != userVO.getMobilephone()){
            if(!RegexUtils.match(userVO.getMobilephone(),RegexUtils.REGEX_MOBILEPHONE)){
                return RestResp.fail("请正确填写手机号");
            }
            // 手机验证码 验证
            String vcodeVal = getVcodeFromRedis(userVO.getMobilephone());
            if(null == vcodeVal || !vcodeVal.equals(userVO.getVcode())){
                return RestResp.fail("手机验证码不正确");
            }

        }
        if(null != userVO.getEmail()){
            if(!RegexUtils.match(userVO.getEmail(),RegexUtils.REGEX_EMAIL)){
                return RestResp.fail("请正确填写邮箱地址");
            }
            userVO.setEnabled(Status.EnableStatus.UNENABLED.getStatus());
            mail = true;
        }else {
            userVO.setEnabled(Status.EnableStatus.ENABLED.getStatus());
        }
        Optional<User> optional = getUser(userVO);
        if (optional.isPresent()) {
            User u = optional.get();
            if(null != userVO.getLoginname() && userVO.getLoginname().equals(u.getLoginname())){
                return RestResp.fail("用户名已经存在");
            }
            if(null != userVO.getMobilephone() && userVO.getMobilephone().equals(u.getMobilephone())){
                return RestResp.fail("该手机号已被注册");
            }
            if(null != userVO.getEmail() && userVO.getEmail().equals(u.getEmail())){
                return RestResp.fail("该邮箱已被注册");
            }
            return RestResp.fail("注册用户已经存在");
        }
        if(null==userVO.getPassword() || "".equals(userVO.getPassword().trim())){
            return RestResp.fail("请正确填写登录密码");
        }

        userVO.setPassword(EncryptUtils.encodeSHA256(userVO.getPassword()));
        if(null == userVO.getCreateTime()){
            userVO.setCreateTime(DateUtil.getPresentDate());
        }

        User user = userRepo.save(userVO.userVO2User());
        if (user == null) {
            return RestResp.fail("操作失败");
        }

        if(mail){
            String kaptcha = kaptchaService.createText();
            saveVcode(user.getEmail(),kaptcha);
            String url = "http://"+"----------"+"/islive?email="+user.getEmail();
            url = "http://192.168.1.111:8081/oxchains/user/vunlock?email="+user.getEmail()+"&vcode="+kaptcha;
            try {
                mailService.sendHtmlMail(user.getEmail(),"账号激活","请点击以下链接进行账号激活操作：\n" +
                        "<a href='"+url+"'>点击这里</a>");
                return RestResp.success("注册成功，验证信息已经发送到邮箱："+user.getEmail()+"中，请前往操作",null);
            }catch (Exception e){
                log.error("邮件发送异常",e);
                userRepo.delete(user.getId());
                return RestResp.fail("邮件发送失败,请重新操作");
            }
        }else {
            return RestResp.success("注册成功",null);
        }
    }

    public RestResp updateUser(User user) {
        User u = userRepo.findByLoginname(user.getLoginname());
        if(u==null){
            return RestResp.fail("提交信息有误");
        }
        u.setUsername(user.getUsername());
        user = userRepo.save(u);
        if (user == null) {
            return RestResp.fail("操作失败");
        }
        return RestResp.success("操作成功",null);
    }
    public RestResp updateUser(UserVO user, ParamType.UpdateUserInfoType uuit) {
        Object res = null;
        if(null == user){
            return RestResp.fail("参数不能为空");
        }
        if(user.getLoginname()==null){
            return RestResp.fail("用户名不能为空");
        }
        User u = userRepo.findByLoginname(user.getLoginname());
        if(null == u){
            return RestResp.fail("用户信息不正确");
        }
        switch (uuit){
            case INFO:
                boolean flag = false;
                if(null!=user.getDescription() && !"".equals(user.getDescription().trim())) {
                    u.setDescription(user.getDescription());
                    flag = true;
                }
                if(!flag){
                    return RestResp.fail("没有需要修改的信息");
                }
                break;
            case PWD:
                if(null==user.getPassword() || "".equals(user.getPassword().trim())){
                    return RestResp.fail("旧密码不能为空");
                }
                if(null==user.getNewPassword() || "".equals(user.getNewPassword().trim())){
                    return RestResp.fail("新密码不能为空");
                }
                if(EncryptUtils.encodeSHA256(user.getPassword()).equals(u.getPassword())){
                    u.setPassword(EncryptUtils.encodeSHA256(user.getNewPassword()));
                }else {
                    return RestResp.fail("输入的旧密码错误");
                }
                break;

            case EMAIL:
                if(null == user.getEmail() || "".equals(user.getEmail().trim()) || !RegexUtils.match(user.getEmail(),RegexUtils.REGEX_EMAIL)){
                    return RestResp.fail("请输入正确的邮箱地址");
                }
                u.setEmail(user.getEmail());
                break;
            case PHONE:
                if(null == user.getMobilephone() || "".equals(user.getMobilephone().trim()) || !RegexUtils.match(user.getMobilephone(),RegexUtils.REGEX_MOBILEPHONE)){
                    return RestResp.fail("请输入正确的手机号");
                }
                u.setMobilephone(user.getMobilephone());
                break;
                default:
                    break;
        }

        return save(u, res);
    }
    public RestResp avatar(User user){
        if(null == user){
            return RestResp.fail("参数不能为空");
        }
        if(user.getLoginname()==null){
            return RestResp.fail("用户名不能为空");
        }
        User u = userRepo.findByLoginname(user.getLoginname());

        return RestResp.fail("上传头像失败");
    }
    private RestResp save(User user,Object res){
        try {
            userRepo.save(user);
            return RestResp.success("操作成功",res);
        }catch (Exception e){
            log.error("保存用户信息异常", e);
            return RestResp.fail("操作失败");
        }
    }

    public RestResp login(UserVO user) {
        user.setPassword(EncryptUtils.encodeSHA256(user.getPassword()));
        try{
            Optional<User> optional = findUser(user);
            return optional.map(u -> {
                if(u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())){
                    return RestResp.fail("账号未激活");
                }

                String originToken = jwtService.generate(u);
                token = "Bearer " + originToken;

                log.info("token = " + token);
                UserVO userInfo = new UserVO(u);

                userInfo.setPassword(null);
                userInfo.setToken(token);

                return RestResp.success("登录成功", userInfo);
            }).orElse(RestResp.fail("登录账号或密码错误"));
        }catch (Exception e){
            log.error("用户信息异常",e);
            return RestResp.fail("用户信息异常");
        }
    }

    public RestResp logout(User user){
        /*User u = userRepo.findByLoginname(user.getLoginname());
        if(null != u && u.getLoginStatus().equals(Status.LoginStatus.LOGIN.getStatus())){
            u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
            userRepo.save(u);
            return RestResp.success("退出成功",null);
        }else {
            return RestResp.fail("退出失败");
        }*/
        return null;
    }

    public Optional<User> findUser(User user) {
        Optional<User> optional = null;
        if (null != user.getLoginname()) {
            optional = userRepo.findByLoginnameAndPassword(user.getLoginname(), user.getPassword());
            if (optional.isPresent()) {
                return optional;
            }
        }
        if (null != user.getEmail()) {
            optional = userRepo.findByEmailAndPassword(user.getEmail(), user.getPassword());
            if (optional.isPresent()) {
                return optional;
            }
        }
        if (null != user.getMobilephone()) {
            optional = userRepo.findByMobilephoneAndPassword(user.getMobilephone(), user.getPassword());
            if (optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    public Optional<User> getUser(User user){
        User u = null;
        if(null != user.getId()){
            u = userRepo.findOne(user.getId());
            if (null != u) {
                return  Optional.of(u);
            }
        }
        if (null != user.getLoginname()) {
            u = userRepo.findByLoginname(user.getLoginname());
            if (null != u) {
                return  Optional.of(u);
            }
        }
        if (null != user.getEmail()) {
            u = userRepo.findByEmail(user.getEmail());
            if (null != u) {
                return Optional.of(u);
            }
        }
        if (null != user.getMobilephone()) {
            u = userRepo.findByMobilephone(user.getMobilephone());
            if (null != u) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public RestResp findUsers() {
        return RestResp.success(newArrayList(userRepo.findAll()));
    }

    public RestResp getUser(Long id){
        if(null == id){
            return RestResp.fail("用户id不能为空");
        }
        User user = userRepo.findOne(id);
        if(user != null){
            user.setPassword(null);
        }
        return RestResp.success(user);
    }




    public RestResp resetpwd(String resetkey,String password){
        User u = null;
        if(resetkey == null || "".equals(resetkey.trim())){
            return RestResp.fail("账号非法");
        }
        if(null == password || "".equals(password.trim())){
            return RestResp.fail("密码不能为空");
        }

        if(resetkey.contains("@")){
            u = userRepo.findByEmail(resetkey);
        }else {
            u = userRepo.findByMobilephone(resetkey);
        }
        if(null == u){
            return RestResp.fail("重置密码失败");
        }
        if(null !=password){
            u.setPassword(EncryptUtils.encodeSHA256(password));
            userRepo.save(u);
            return RestResp.success("重置密码成功!",null);
        }
        return RestResp.fail("重置密码失败");
    }

    public RestResp active(String email){
        if(email==null || "".equals(email) || !RegexUtils.match(email,RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("邮箱格式不正确，激活失败");
        }
        User user = userRepo.findByEmail(email);
        if(null == user){
            return RestResp.fail("该邮箱未注册，无法激活");
        }
        if(user.getEnabled().equals(Status.EnableStatus.ENABLED.getStatus())){
            return RestResp.fail("账号已经激活，请勿重复操作");
        }else {
            user.setEnabled(Status.EnableStatus.ENABLED.getStatus());
            userRepo.save(user);
            return RestResp.success("账号激活成功",null);
        }
    }
    public RestResp sendMail(String email ,String subject,String content){
        if(email==null || "".equals(email) || !RegexUtils.match(email,RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("请正确填写邮箱");
        }
        if(content==null || "".equals(content.trim()) ){
            return RestResp.fail("发送内容不能为空");
        }
        try {
            mailService.sendHtmlMail(email,subject,content);
            return RestResp.success("邮件已发送到："+email+"，请前往查收",null);
        }catch (Exception e){
            log.error("邮件发送异常",e);
            return RestResp.fail("邮件发送失败,请重新操作");
        }
    }

    @Resource
    private RedisTemplate redisTemplate;

    public boolean saveVcode(String key, String vcode){
        try {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(key, vcode, 5L, TimeUnit.MINUTES);
            return true;
        }catch (Exception e){
            log.error("Redis 操作异常:" ,e);
            return false;
        }
    }
    public String getVcodeFromRedis(String key){
        String val = null;
        try{
            boolean flag = redisTemplate.hasKey(key);
            if(!flag){
                return val;
            }
            ValueOperations<String,String> ops = redisTemplate.opsForValue();
            val = ops.get(key);
            redisTemplate.delete(key);
            return val;
        }catch (Exception e){
            log.error("Redis 操作异常", e);
            return null;
        }
    }

    @Value("${rms.frontend.url}")
    private String frontEndUrl;
    public RestResp sendVmail(VerifyCode vcode){
        if(null == vcode){
            return RestResp.fail("参数不能为空");
        }
        if(null==vcode.getKey()||"".equals(vcode.getKey().trim()) || !vcode.getKey().contains("@")){
            return RestResp.fail("输入的邮箱格式不正确");
        }
        if(null == vcode.getVcode() || "".equals(vcode.getKey().trim())){
            return RestResp.fail("验证码不能为空");
        }

        String vcodeVal = getVcodeFromRedis(vcode.getKey());
        if (vcodeVal.equals(vcode.getVcode())) {
            String[] to = {vcode.getKey()};
            String url = "http://"+frontEndUrl+"/resetpsw?email="+vcode.getKey()+"&vcode="+vcode.getVcode();
            try {
                //mailService.send(new Email(to,"修改密码","请点击以下链接进行密码修改操作：\n" +  url));
                mailService.sendHtmlMail(vcode.getKey(),"修改密码","请点击以下链接进行密码修改操作：\n" +
                        "<a href='"+url+"'>点击这里</a>");
                return RestResp.success("邮件已发送到："+vcode.getKey()+"，请尽快修改您的密码",null);
            }catch (Exception e){
                log.error("邮件发送异常",e);
                return RestResp.fail("邮件发送失败,请重新操作");
            }
        }
        return RestResp.fail("验证码错误");
    }

    public RestResp verifyEmail(VerifyCode verifyCode){
        String key = verifyCode.getKey();
        String code = verifyCode.getVcode();

        String kaptcha = getVcodeFromRedis(key);
        if(kaptcha.equals(key)){
            User user = userRepo.findByEmail(key);
            user.setEnabled(Status.EnableStatus.ENABLED.getStatus());
            userRepo.save(user);
            return RestResp.success("验证成功");
        }else {
            return RestResp.fail("验证失败,请重新获取验证码");
        }
    }

    public RestResp list(String loginname,Integer pageNo,Integer pageSize){
        pageNo = pageNo == null?1:pageNo;
        pageSize = pageSize == null ?10 :pageSize;
        Pageable pager = new PageRequest((pageNo-1)*pageSize, pageSize);
        try{
            Page<User> page = null;
            if(null != loginname && !"".equals(loginname.trim())){
                page = userRepo.findByLoginname(loginname,pager);
            }else {
                page = userRepo.findAll(pager);
            }
            List<UserVO> list = new ArrayList<>(page.getContent().size());
            for(User user:page.getContent()){
                list.add(new UserVO(user));
            }
            return RestRespPage.success(list,page.getTotalElements());
        }catch (Exception e){
            log.error("",e);
            return RestResp.fail();
        }
    }

    /**
     * 后台添加用户
     */
    public RestResp backAddUser(UserVO userVO) {
        if(null == userVO){
            return RestResp.fail("请正确提交的注册信息");
        }
        if(null == userVO.getLoginname() || !RegexUtils.match(userVO.getLoginname(),RegexUtils.REGEX_NAME_LEN32)){
            return RestResp.fail("请正确填写登录名，只能包含字母、数字、下划线，且只能以字母开头");
        }
        if(null != userVO.getMobilephone()){
            if(!RegexUtils.match(userVO.getMobilephone(),RegexUtils.REGEX_MOBILEPHONE)){
                return RestResp.fail("请正确填写手机号");
            }
        }
        if(null != userVO.getEmail()){
            if(!RegexUtils.match(userVO.getEmail(),RegexUtils.REGEX_EMAIL)){
                return RestResp.fail("请正确填写邮箱地址");
            }
            userVO.setEnabled(Status.EnableStatus.UNENABLED.getStatus());
        }else {
            userVO.setEnabled(Status.EnableStatus.ENABLED.getStatus());
        }
        Optional<User> optional = getUser(userVO);
        if (optional.isPresent()) {
            User u = optional.get();
            if(null != userVO.getLoginname() && userVO.getLoginname().equals(u.getLoginname())){
                return RestResp.fail("用户名已经存在");
            }
            if(null != userVO.getMobilephone() && userVO.getMobilephone().equals(u.getMobilephone())){
                return RestResp.fail("该手机号已被注册");
            }
            if(null != userVO.getEmail() && userVO.getEmail().equals(u.getEmail())){
                return RestResp.fail("该邮箱已被注册");
            }
            return RestResp.fail("注册用户已经存在");
        }
        if(null==userVO.getPassword() || "".equals(userVO.getPassword().trim())){
            return RestResp.fail("请正确填写登录密码");
        }

        userVO.setPassword(EncryptUtils.encodeSHA256(userVO.getPassword()));
        if(null == userVO.getCreateTime()){
            userVO.setCreateTime(DateUtil.getPresentDate());
        }

        User user = userRepo.save(userVO.userVO2User());
        if (user == null) {
            return RestResp.fail("操作失败");
        }
        return RestResp.success("添加成功",user);
    }


    public RestResp deleteUser(UserVO userVO){
        Optional<User> optional = getUser(userVO);
        return optional.map(u -> {
            userRepo.delete(u);
            return RestResp.success("删除账号成功");
        }).orElse(RestResp.fail("账号不存在"));
    }

    public RestResp lockUser(UserVO vo){
        Optional<User> optional = getUser(vo);
        return optional.map(u -> {
            if(u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())){
                return RestResp.success("账号已被锁定");
            }
            u.setEnabled(Status.EnableStatus.UNENABLED.getStatus());
            u = userRepo.save(u);
            UserVO userInfo = new UserVO(u);
            userInfo.setPassword(null);

            return RestResp.success("账号已经锁定", userInfo);
        }).orElse(RestResp.fail("账号不存在"));
    }

    public RestResp unlockUser(UserVO vo){
        Optional<User> optional = getUser(vo);
        return optional.map(u -> {
            if(u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())){
                u.setEnabled(Status.EnableStatus.ENABLED.getStatus());
                u = userRepo.save(u);
                UserVO userInfo = new UserVO(u);
                userInfo.setPassword(null);
                return RestResp.success("账号已经解锁", userInfo);
            }
            return RestResp.success("账号已经解锁");

        }).orElse(RestResp.fail("账号不存在"));
    }

    public RestResp vunlockUser(UserVO vo){
        String vcode = null;
        if(null != vo.getMobilephone()){
            vcode = getVcodeFromRedis(vo.getMobilephone());
        }
        if(null != vo.getEmail()){
            vcode = getVcodeFromRedis(vo.getEmail());
        }
        if(vcode==null || !vcode.equals(vo.getVcode())){
            return RestResp.fail("解锁失败");
        }
        return unlockUser(vo);
    }


    public RestResp getCaptcha(UserVO vo){
      String captcha = kaptchaService.createText();
      if(null != vo.getMobilephone()){
          // TODO
          saveVcode(vo.getMobilephone(), captcha);
          return RestResp.success("验证码已经发送到手机："+vo.getMobilephone()+"，请尽快激活账号",null);
      }
      if(null != vo.getEmail()){
          mailService.sendHtmlMail(vo.getEmail(),"验证码","账号激活验证码：" + captcha);
          saveVcode(vo.getEmail(), captcha);
          return RestResp.success("验证码已经发送到邮箱："+vo.getEmail()+"，请尽快激活账号",null);
      }
      return RestResp.fail();
    }

    @Transactional
    public RestResp auth(Long roleId, String resourceIds){
        if(null == roleId){
            return RestResp.fail("分配权限失败");
        }
        try{
            if(null == resourceIds || "".equals(resourceIds.trim())){
                userResourceRepo.deleteByUserId(roleId);
                return RestResp.success("取消成功");
            }

            List<Long> ids = NumberFormatUtil.stringSplit2Long(resourceIds,",");
            List<UserResource> roleResources = userResourceRepo.findByUserId(roleId);

            List<UserResource> auths = new ArrayList<>(ids.size());
            for(Long id : ids){
                for(UserResource roleResource : roleResources){
                    if(id.equals(roleResource.getId())){
                        auths.add(roleResource);

                        ids.remove(id);
                        roleResources.remove(roleResource);
                        break;
                    }
                }
            }

            if(roleResources.size() > 0){
                userResourceRepo.delete(roleResources);
            }

            if(ids.size() > 0){
                for(Long id : ids){
                    auths.add(new UserResource(roleId,id));
                }
            }

            Iterable<UserResource> it = userResourceRepo.save(auths);

            return RestResp.success("分配权限成功");
        }catch (Exception e){
            return RestResp.fail("分配权限失败");
        }
    }

    @Transactional
    public RestResp auth2(Long userId, String resourceIds){
        if(null == userId){
            return RestResp.fail("分配权限失败");
        }
        try{
            if(null == resourceIds || "".equals(resourceIds.trim())){
                userResourceRepo.deleteByUserId(userId);
                return RestResp.success("取消成功");
            }

            List<Long> ids = NumberFormatUtil.stringSplit2Long(resourceIds,",");
            List<UserResource> roleResources = userResourceRepo.findByUserId(userId);

            List<UserResource> auths = new ArrayList<>(ids.size());
            for(Long id : ids){
                for(UserResource roleResource : roleResources){
                    if(id.equals(roleResource.getId())){
                        ids.remove(id);
                        break;
                    }
                }
            }


            if(ids.size() > 0){
                for(Long id : ids){
                    auths.add(new UserResource(userId,id));
                }
            }else {
                return RestResp.fail("用户已有该权限");
            }

            Iterable<UserResource> it = userResourceRepo.save(auths);

            return RestResp.success("分配权限成功");
        }catch (Exception e){
            return RestResp.fail("分配权限失败");
        }
    }


    @Transactional
    public RestResp unauth(Long roleId, String resourceIds){
        if(null == roleId || null == resourceIds || "".equals(resourceIds.trim())){
            return RestResp.fail("取消权限失败");
        }
        try{
            List<Long> ids = NumberFormatUtil.stringSplit2Long(resourceIds,",");
            userResourceRepo.deleteByUserIdAndResourceIdIn(roleId,ids);

            return RestResp.success("取消权限成功");
        }catch (Exception e){
            return RestResp.fail("取消权限失败");
        }
    }

}
