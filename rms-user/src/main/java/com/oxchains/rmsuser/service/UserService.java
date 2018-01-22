package com.oxchains.rmsuser.service;


import com.oxchains.rmsuser.auth.JwtService;
import com.oxchains.rmsuser.common.*;
import com.oxchains.rmsuser.dao.UserRepo;
import com.oxchains.rmsuser.entity.User;
import com.oxchains.rmsuser.entity.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

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
            String url = "http://"+"----------"+"/islive?email="+user.getEmail();
            try {
                mailService.sendHtmlMail(user.getEmail(),"账号激活","请点击以下链接进行账号激活操作：\n" +
                        "<a href='"+url+"'>点击这里</a>");
                return RestResp.success("注册成功，验证信息已经发送到邮箱："+user.getEmail()+"中，请前往操作",null);
            }catch (Exception e){
                log.error("邮件发送异常",e);
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

    public RestResp addBitcoinAddress(String loginname,String firstAddress){
        if(null == loginname || "".equals(loginname.trim())){
            return RestResp.fail("用户名不正确");
        }
        if(null == firstAddress || "".equals(firstAddress.trim()) || firstAddress.length()<26 || firstAddress.length()>34){
            return RestResp.fail("未正确填写收款地址,请重新填写");
        }
        firstAddress = firstAddress.trim();
        User user = userRepo.findByLoginname(loginname);
        if(null == user){
            return RestResp.fail("用户名不正确");
        }
//        if(firstAddress.equals(user.getFirstAddress())){
//            return RestResp.fail("您未修改地址");
//        }
//        user.setFirstAddress(firstAddress);
        userRepo.save(user);
        return RestResp.success("操作成功",firstAddress);

    }
}
