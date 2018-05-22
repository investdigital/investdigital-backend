package info.investdigital.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.oxchains.basicService.files.tfsService.TFSConsumer;
import info.investdigital.auth.JwtService;
import info.investdigital.common.*;
import info.investdigital.dao.*;
import info.investdigital.entity.*;
import info.investdigital.uc.UCHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
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
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Resource
    private UserRepo userRepo;

    @Resource
    JwtService jwtService;

    private String token;

    @Resource
    MailService mailService;

    @Resource
    SendGridService sendGridService;

    @Resource
    PrepareAddressRepo prepareAddressRepo;

    @Resource
    TFSConsumer tfsConsumer;

    @Resource
    private UserRoleRepo userRoleRepo;

    @Resource
    private ApplyvRepo applyvRepo;

    @Resource
    private RoleRepo roleRepo;

    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    private SmsService smsService;

    @Resource
    private UserActivityRepo userActivityRepo;

    @Resource
    private MyMessageSource myMessageSource;

    @Resource
    private RegInvitationRepo regInvitationRepo;

    @Resource
    private UResParam uresParam;

    public String getRandomAddress() {
        Pageable pageable = new PageRequest(1, 1, null);
        List<PrepareAddress> content = prepareAddressRepo.findAll(pageable).getContent();
        if (content.size() > 0) {
            PrepareAddress prepareAddress = content.get(0);
            String address = prepareAddress.getAddress();
            prepareAddressRepo.delete(prepareAddress.getId());
            return address;
        }
        return null;
    }

    public RestResp addUser(User user) throws Exception {
        boolean mail = false;
        if (null == user) {
            return RestResp.fail(getMessage(I18NConst.SUBMIT_REGISTRATION_INFORMATION_CORRECTLY));
        }
        if (null == user.getLoginname() || !RegexUtils.match(user.getLoginname(), RegexUtils.REGEX_NAME_LEN15)) {
            return RestResp.fail(getMessage(I18NConst.LOGIN_NAME_FORMAT));
        }
        if (null != user.getMobilephone()) {
            if (!RegexUtils.match(user.getMobilephone(), RegexUtils.REGEX_MOBILEPHONE)) {
                return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
            }
        }
        if (null != user.getEmail()) {
            if (!RegexUtils.match(user.getEmail(), RegexUtils.REGEX_EMAIL)) {
                return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
            }
            user.setEnabled(Status.EnableStatus.UNENABLED.getStatus());
            mail = true;
        } else {
            user.setEnabled(Status.EnableStatus.ENABLED.getStatus());
        }
        Optional<User> optional = getUser(user);
        if (optional.isPresent()) {
            User u = optional.get();
            if (null != user.getLoginname() && user.getLoginname().equals(u.getLoginname())) {
                return RestResp.fail(getMessage(I18NConst.USER_ALREADY_EXIST));
            }
            if (null != user.getMobilephone() && user.getMobilephone().equals(u.getMobilephone())) {
                return RestResp.fail(getMessage(I18NConst.PHONE_NUMBER_REGISTERED));
            }
            if (null != user.getEmail() && user.getEmail().equals(u.getEmail())) {
                return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_REGISTERED));
            }
            return RestResp.fail(getMessage(I18NConst.USER_ALREADY_EXIST));
        }
        if (null == user.getPassword() || "".equals(user.getPassword().trim())) {
            return RestResp.fail(getMessage(I18NConst.FILL_LOGIN_PASSWORD_CORRECTLY));
        }
        user.setPassword(EncryptUtils.encodeSHA256(user.getPassword()));
        if (null == user.getCreateTime()) {
            user.setCreateTime(DateUtil.getPresentDate());
        }

        if (null == user.getLoginStatus()) {
            user.setLoginStatus(0);
        }

        user = userRepo.save(user);
        if (user == null) {
            return RestResp.fail(getMessage(I18NConst.ACTION_FAILURE));
        }

        if (mail) {
            String url = "http://" + "----------" + "/islive?email=" + user.getEmail();
            try {
                mailService.sendHtmlMail(user.getEmail(), "账号激活", "请点击以下链接进行账号激活操作：\n" +
                        "<a href='" + url + "'>点击这里</a>");
                return RestResp.success(getMessage(I18NConst.REGISTER_SUCCESS_EMAIL) + user.getEmail() + getMessage(I18NConst.PLEASE_OPERATION), null);
            } catch (Exception e) {
                log.error("邮件发送异常", e);
                return RestResp.fail(getMessage(I18NConst.SEND_MAIL_FAILED));
            }
        } else {
            return RestResp.success(getMessage(I18NConst.REGISTER_SUCCESS), null);
        }
    }

    public RestResp signup(UserVO vo) throws Exception {
        if (null == vo) {
            return RestResp.fail(getMessage(I18NConst.SUBMIT_REGISTRATION_INFORMATION_CORRECTLY));
        }
        User user = null;
        String vcode = null;
        boolean mail = false;
        if (Const.CFIELD.EMAIL.getFieldName().equals(vo.getVfield())) {
            mail = true;
            if (null == vo.getEmail() || !RegexUtils.match(vo.getEmail(), RegexUtils.REGEX_EMAIL)) {
                return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
            } else {
                vo.setEnabled(Status.EnableStatus.ENABLED.getStatus());
            }
            user = userRepo.findByEmail(vo.getEmail());
            if (null != user) {
                return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_REGISTERED));
            }
            vo.setLoginname(vo.getEmail());
            vcode = getVcodeFromRedis(vo.getEmail());
        } else if (Const.CFIELD.MOBILE_PHONE.getFieldName().equals(vo.getVfield())) {
            if (null == vo.getMobilephone() || !RegexUtils.match(vo.getMobilephone(), RegexUtils.REGEX_MOBILEPHONE)) {
                return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
            } else {
                vo.setEnabled(Status.EnableStatus.ENABLED.getStatus());
            }
            user = userRepo.findByMobilephone(vo.getMobilephone());
            if (null != user) {
                return RestResp.fail(getMessage(I18NConst.PHONE_NUMBER_BEEN_USED));
            }
            vcode = getVcodeFromRedis(vo.getMobilephone());
            vo.setLoginname(vo.getMobilephone());
        } else {
            return RestResp.fail(getMessage(I18NConst.REGISTER_FAILURE));
        }

        if (null == vo.getPassword() || "".equals(vo.getPassword().trim())) {
            return RestResp.fail(getMessage(I18NConst.FILL_LOGIN_PASSWORD_CORRECTLY));
        }
        vo.setCreateTime(DateUtil.getPresentDate());
        vo.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
        String password = vo.getPassword();
        vo.setPassword(EncryptUtils.encodeSHA256(password));

        if (null == vcode || "".equals(vcode.trim()) || !vcode.equals(vo.getVcode().trim())) {
            return RestResp.fail(getMessage(I18NConst.FILL_VERIFYING_CODE_CORRECTLY));
        }
        vo.setImage(/*defaultUserAvatar*/uresParam.getDefaultUserAvatar());
        /*UserVO userInfo = null;
        try{
            User u = userRepo.save(vo.userVO2User());
            if (u == null) {
                return RestResp.fail(getMessage(I18NConst.ACTION_FAILURE));
            }

            addUserRole(u.getId(), Const.ROLE.USER.getId());
            userInfo = new UserVO(u);
            userInfo.setRoles(getRoles(u.getId()));
            if (null != discuzSyncSignup && discuzSyncSignup.intValue() == Const.ENABLE.ENABLEED.getEnable() && mail) {
                UCHelper.reg(userInfo.getLoginname(), password, userInfo.getEmail());
            }
            if(null != invitationCode && !"".equals(invitationCode.trim())){
                RegInvitation regInvitation = new RegInvitation(vo.getLoginname(),invitationCode);
                regInvitationRepo.save(regInvitation);
            }
            return RestResp.success(getMessage(I18NConst.REGISTER_SUCCESS), userInfo);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RestResp.fail(getMessage(I18NConst.REGISTER_FAILURE));
        }*/

        User u = userRepo.save(vo.userVO2User());
        if (u == null) {
            return RestResp.fail(getMessage(I18NConst.ACTION_FAILURE));
        }
        addUserRole(u.getId(), Const.ROLE.USER.getId());
        UserVO userInfo = new UserVO(u);
        userInfo.setRoles(getRoles(u.getId()));
        if(uresParam.isAutomaticSign()){
            String originToken = jwtService.generate(userInfo);
            token = "Bearer " + originToken;
            log.info("automaitc login token = " + token);
            userInfo.setToken(token);
        }
        if (uresParam.isDiscuzSyncSignup() && mail) {
            try {
                UCHelper.reg(userInfo.getLoginname(), password, userInfo.getEmail());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return RestResp.success(getMessage(I18NConst.FORUM_SYNCHRONIZED_REGISTRATION_FAILURE), userInfo);
            }
        }
        if(uresParam.isAutomaticSign() && uresParam.isDiscuzSyncSignin()){
            try{
                String str = UCHelper.login(userInfo.getLoginname(),password);
                userInfo.setDiscuzSyncScript(str);
                log.info("automatic login ---discuz---: ",str);
            }catch (Exception e){
                log.info("automatic login ---discuz---: failure ");
            }
        }
       return RestResp.success(getMessage(I18NConst.REGISTER_SUCCESS), userInfo);
    }

    public RestResp updateUser(UserVO vo) {
        User u = userRepo.findByLoginname(vo.getLoginname());
        if (u == null) {
            return RestResp.fail(getMessage(I18NConst.INFORMATION_INCORRECT));
        }
        u.setUsername(vo.getUsername());
        User user = userRepo.save(u);
        if (user == null) {
            return RestResp.fail(getMessage(I18NConst.ACTION_FAILURE));
        }
        return RestResp.success(getMessage(I18NConst.ACTION_SUCCESS), null);
    }

    public RestResp updateUser(UserVO user, ParamType.UpdateUserInfoType uuit) {
        Object res = null;
        if (null == user) {
            return RestResp.fail(getMessage(I18NConst.PARAMETERS_NOT_EMPTY));
        }
        if (user.getLoginname() == null) {
            return RestResp.fail(getMessage(I18NConst.USERNAME_NOT_EMPTY));
        }
        User u = userRepo.findByLoginname(user.getLoginname());
        if (null == u) {
            return RestResp.fail(getMessage(I18NConst.USER_INFORMATION_INCORRECT));
        }
        String vcode = null;
        switch (uuit) {
            case PWD:
                if (null == user.getPassword() || "".equals(user.getPassword().trim())) {
                    return RestResp.fail(getMessage(I18NConst.OLD_PASSWORD_NOT_EMPTY));
                }
                String newPassword = user.getNewPassword();
                if (null == newPassword || "".equals(newPassword.trim())) {
                    return RestResp.fail(getMessage(I18NConst.NEW_PASSWORD_NOT_EMPTY));
                }
                if (EncryptUtils.encodeSHA256(user.getPassword()).equals(u.getPassword())) {
                    if (uresParam.isDiscuzSyncResetpwd() && null != u.getEmail()) {
                        boolean flag1 = UCHelper.resetpwd(u.getLoginname(), u.getEmail(), newPassword);
                        if (!flag1) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return RestResp.fail(getMessage(I18NConst.RESET_PASSWORD_FAILURE), null);
                        }
                    }
                    u.setPassword(EncryptUtils.encodeSHA256(newPassword));
                } else {
                    return RestResp.fail(getMessage(I18NConst.OLD_PASSWORD_ERROR));
                }
                break;
            case EMAIL:
                if (null == user.getEmail() || "".equals(user.getEmail().trim()) || !RegexUtils.match(user.getEmail(), RegexUtils.REGEX_EMAIL)) {
                    return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
                }
                if (user.getEmail().equals(u.getEmail())) {
                    return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_BEING_USED));
                }
                vcode = getVcodeFromRedis(user.getEmail());
                if (null == user.getVcode() || "".equals(user.getVcode().trim()) || null == vcode || !vcode.equals(user.getVcode())) {
                    return RestResp.fail(getMessage(I18NConst.FILL_VERIFYING_CODE_CORRECTLY));
                }
                String password = user.getPassword();
                String encrypt = EncryptUtils.encodeSHA256(password);
                if(null == password || !u.getPassword().equals(EncryptUtils.encodeSHA256(password))){
                    return RestResp.fail(getMessage(I18NConst.FILL_LOGIN_PASSWORD_CORRECTLY));
                }

                User eu = userRepo.findByEmail(user.getEmail());
                if (null != eu) {
                    return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_BEEN_USED));
                }
                if (/*discuzSyncSignup*/uresParam.isDiscuzSyncSignup()) {
                    try{
                        UCHelper.reg(u.getLoginname(),password,user.getEmail());
                    }catch (Exception e){
                        return RestResp.fail("绑定邮箱失败");
                    }
                }
                u.setEmail(user.getEmail());
                break;
            case PHONE:
                if (null == user.getMobilephone() || "".equals(user.getMobilephone().trim()) || !RegexUtils.match(user.getMobilephone(), RegexUtils.REGEX_MOBILEPHONE)) {
                    return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
                }
                if (user.getMobilephone().equals(u.getMobilephone())) {
                    return RestResp.fail(getMessage(I18NConst.PHONE_NUMBER_BEING_USED));
                }
                vcode = getVcodeFromRedis(user.getMobilephone());
                if (null == user.getVcode() || "".equals(user.getVcode().trim()) || null == vcode || !vcode.equals(user.getVcode())) {
                    return RestResp.fail(I18NConst.FILL_VERIFYING_CODE_CORRECTLY);
                }
                User mu = userRepo.findByMobilephone(user.getMobilephone());
                if (null != mu) {
                    return RestResp.fail(getMessage(I18NConst.PHONE_NUMBER_BEEN_USED));
                }
                u.setMobilephone(user.getMobilephone());
                break;
            case NICK_NAME:

                if (null == user.getUsername() || "".equals(user.getUsername().trim())) {
                    return RestResp.fail(getMessage(I18NConst.MODIFY_NICKNAME_NOT_EMPTY));

                }
                if (user.getUsername().equals(u.getUsername())) {
                    return RestResp.fail(getMessage(I18NConst.NOT_MADE_ANY_CHANGES));
                }
                u.setUsername(user.getUsername());
                break;
            default:
                break;
        }

        return save(u, res);
    }

    public RestResp avatar(UserVO vo) {
        if (null == vo) {
            return RestResp.fail(getMessage(I18NConst.PARAMETERS_NOT_EMPTY));
        }
        /*if (vo.getLoginname() == null) {
            return RestResp.fail(getMessage(I18NConst.USERNAME_NOT_EMPTY));
        }
        User u = userRepo.findByLoginname(vo.getLoginname());*/

        Optional<User> optional = getUser(vo);
        if (!optional.isPresent()) {
            return RestResp.fail(getMessage(I18NConst.USER_INFORMATION_EXECPTION));
        }
        User u = optional.get();
        MultipartFile file = vo.getFile();
        if (null != file) {
            String newFileName = tfsConsumer.saveTfsFile(file, u.getId());
            if (null == newFileName) {
                return RestResp.fail(getMessage(I18NConst.IMAGE_UPLOAD_FAILED));
            }
            u.setImage(newFileName);
            userRepo.save(u);
            return RestResp.success(getMessage(I18NConst.IMAGE_UPLOAD_SUCCESS), newFileName);
        }
        return RestResp.fail(getMessage(I18NConst.IMAGE_UPLOAD_FAILED));
    }

    private RestResp save(User user, Object res) {
        try {
            userRepo.save(user);
            return RestResp.success(getMessage(I18NConst.ACTION_SUCCESS), res);
        } catch (Exception e) {
            log.error("保存用户信息异常", e);
            return RestResp.fail(getMessage(I18NConst.ACTION_FAILURE));
        }
    }

    @Deprecated
    public RestResp login(User user) {
        user.setPassword(EncryptUtils.encodeSHA256(user.getPassword()));
        try {
            Optional<User> optional = findUser(user);
            return optional.map(u -> {
                if (u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())) {
                    return RestResp.fail(getMessage(I18NConst.ACCOUNT_NOT_ACTICATED));
                }
                if (u.getLoginStatus().equals(Status.LoginStatus.LOGIN.getStatus())) {
                    return RestResp.fail(getMessage(I18NConst.USER_BEEN_LOGIN));
                }

                UserVO userInfo = new UserVO(u);
                String originToken = jwtService.generate(userInfo);
                token = "Bearer " + originToken;
                log.info("token = " + token);
                userInfo.setPassword(null);
                userInfo.setToken(token);


                u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
                User save = userRepo.save(u);

                //new UserToken(u.getUsername(),token)
                return RestResp.success(getMessage(I18NConst.LOGIN_SUCCESS), userInfo);
            }).orElse(RestResp.fail(getMessage(I18NConst.ACCOUNT_OR_PASSWORD_ERROR)));
        } catch (Exception e) {
            log.error("用户信息异常", e);
            return RestResp.fail(getMessage(I18NConst.USER_INFORMATION_EXECPTION));
        }
    }

    public RestResp signin(UserVO vo) {
        if (Const.CFIELD.EMAIL.getFieldName().equals(vo.getVfield())) {
            if (null == vo.getEmail() || !RegexUtils.match(vo.getEmail(), RegexUtils.REGEX_EMAIL)) {
                return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
            }
        } else if (Const.CFIELD.MOBILE_PHONE.getFieldName().equals(vo.getVfield())) {
            if (null == vo.getMobilephone() || !RegexUtils.match(vo.getMobilephone(), RegexUtils.REGEX_MOBILEPHONE)) {
                return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
            }
        } else {
            return RestResp.fail(getMessage(I18NConst.ACCOUNT_ERROR));
        }
//        if (!RegexUtils.match(vo.getEmail(), RegexUtils.REGEX_EMAIL)) {
//            return RestResp.fail(getMessage(I18NConst.ACCOUNT_ERROR));
//        }
        String password = vo.getPassword();
        vo.setPassword(EncryptUtils.encodeSHA256(password));
        try {
            Optional<User> optional = findUser(vo);
            return optional.map(u -> {
                if (u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())) {
                    return RestResp.fail(getMessage(I18NConst.ACCOUNT_NOT_ACTICATED));
                }
                if (u.getLoginStatus().equals(Status.LoginStatus.LOGIN.getStatus())) {
                    return RestResp.fail(getMessage(I18NConst.USER_BEEN_LOGIN));
                }

                UserVO userInfo = new UserVO(u);
                userInfo.setRoles(getRoles(userInfo.getId()));
                String originToken = jwtService.generate(userInfo);
                token = "Bearer " + originToken;
                log.info("token = " + token);
                userInfo.setToken(token);
//                u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
//                User save = userRepo.save(u);
                String discuzSigninScript = null;
                if ( /*discuzSyncSignin*/uresParam.isDiscuzSyncSignin()  && null != u.getEmail()) {
                    try {
                        discuzSigninScript = UCHelper.login(userInfo.getLoginname(), password);
                        log.info("Discuz Sync Signin Script: {}", discuzSigninScript);
                    } catch (Exception e) {
                        return RestResp.success(getMessage(I18NConst.FORUM_SYNCHRONIZED_LOGIN_FAILURE), userInfo);
                    }
                }
                userInfo.setDiscuzSyncScript(discuzSigninScript);
                return RestResp.success(getMessage(I18NConst.LOGIN_SUCCESS), userInfo);
            }).orElse(RestResp.fail(getMessage(I18NConst.ACCOUNT_OR_PASSWORD_ERROR)));
        } catch (Exception e) {
            log.error("用户信息异常", e);
            return RestResp.fail(getMessage(I18NConst.USER_INFORMATION_EXECPTION));
        }
    }

    public RestResp logout(UserVO user) {
        Optional<User> optional = getUser(user);
        return optional.map(u -> {
            u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
            userRepo.save(u);
            String message = null;
            if (/*discuzSyncSignin*/uresParam.isDiscuzSyncSignin()) {
                try {
                    message = UCHelper.logout();
                    return RestResp.success(getMessage(I18NConst.LOGOUT_SUCCESS), message);
                } catch (Exception e) {
                    return RestResp.success(getMessage(I18NConst.FORUM_LOGOUT_FAILURE), e);
                }
            }
            return RestResp.success(getMessage(I18NConst.LOGOUT_SUCCESS), message);
        }).orElse(RestResp.fail(getMessage(I18NConst.ACCOUNT_ERROR)));
    }

    public RestResp signout(Long userId) {
        UserVO user = new UserVO();
        user.setId(userId);
        return logout(user);
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

    public Optional<User> getUser(User user) {
        User u = null;
        if (null != user.getId()) {
            u = userRepo.findOne(user.getId());
            if (null != u) {
                return Optional.of(u);
            }
        }
        if (null != user.getLoginname()) {
            u = userRepo.findByLoginname(user.getLoginname());
            if (null != u) {
                return Optional.of(u);
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

    public RestResp getUser(Long id) {
        if (null == id) {
            return RestResp.fail(getMessage(I18NConst.USER_ID_NOT_EMPTY));
        }
        User user = userRepo.findOne(id);
        if (user != null) {
            user.setPassword(null);
        }
        return RestResp.success(user);
    }

    public boolean saveVcode(String key, String vcode) {
        try {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(key, vcode, 5L, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            log.error("Redis 操作异常:", e);
            return false;
        }
    }

    public String getVcodeFromRedis(String key) {
        String val = null;
        try {
            boolean flag = redisTemplate.hasKey(key);
            if (!flag) {
                return val;
            }
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            val = ops.get(key);
//            redisTemplate.delete(key);
            return val;
        } catch (Exception e) {
            log.error("Redis 操作异常", e);
            return null;
        }
    }

    public RestResp sendVmail(VerifyCode vcode) {
        if (null == vcode) {
            return RestResp.fail(getMessage(I18NConst.PARAMETERS_NOT_EMPTY));
        }
        if (null == vcode.getKey() || "".equals(vcode.getKey().trim()) || !vcode.getKey().contains("@")) {
            return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_FORMAT_INCORRECT));
        }
        if (null == vcode.getVcode() || "".equals(vcode.getKey().trim())) {
            return RestResp.fail(getMessage(I18NConst.VERIFYING_CODE_NOT_EMPTY));
        }

        String vcodeVal = getVcodeFromRedis(vcode.getKey());
        if (vcodeVal.equals(vcode.getVcode())) {
            String[] to = {vcode.getKey()};
            String url = "http://" + uresParam.getFrontEndUrl() + "/resetpsw?email=" + vcode.getKey() + "&vcode=" + vcode.getVcode();
            try {
                //mailService.send(new Email(to,"修改密码","请点击以下链接进行密码修改操作：\n" +  url));
                mailService.sendHtmlMail(vcode.getKey(), "修改密码", "请点击以下链接进行密码修改操作：\n" +
                        "<a href='" + url + "'>点击这里</a>");
                return RestResp.success(getMessage(I18NConst.MAIL_BEEN_SEND) + vcode.getKey() + getMessage(I18NConst.MODIFY_YOUR_PASSWORD), null);
            } catch (Exception e) {
                log.error("邮件发送异常", e);
                return RestResp.fail(getMessage(I18NConst.SEND_MAIL_FAILED));
            }
        }
        return RestResp.fail(getMessage(I18NConst.VERIFICATION_CODE_ERROR));
    }

    @Resource
    DefaultKaptcha defaultKaptcha;

    public RestResp sendVmailCode(String email) {

        if (null == email || "".equals(email.trim()) || !RegexUtils.match(email, RegexUtils.REGEX_EMAIL)) {
            return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_FORMAT_INCORRECT));

        }
        String code = defaultKaptcha.createText();
        saveVcode(email, code);

        String content = String.format(/*signupStr*/uresParam.getSignupStr(), code);
        try {
            //mailService.sendHtmlMail(email, "验证码", content);
            //sendGridService.sendMail(email, "验证码", content);
            sendGridService.sendHtmlMail(email,"验证码",content);
            return RestResp.success(getMessage(I18NConst.MAIL_BEEN_SEND) + email + getMessage(I18NConst.PLEAST_LOG_IN), null);
        } catch (Exception e) {
            log.error("邮件发送异常", e);
            return RestResp.fail(getMessage(I18NConst.SEND_MAIL_FAILED));
        }

    }

    public RestResp sendPhoneCode(String mobilephone) {
        if (null == mobilephone || "".equals(mobilephone.trim()) || !RegexUtils.match(mobilephone, RegexUtils.REGEX_MOBILEPHONE)) {
            return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
        }
        String code = defaultKaptcha.createText();
        saveVcode(mobilephone, code);
        String content = String.format(/*signupSmsStr*/uresParam.getSignupSmsStr(), code);
        log.info(content);
        try {
            boolean flag = smsService.sendSingleSms(mobilephone, content, /*smsCampaignId*/uresParam.getSmsCampaignId(), null);
            if (flag) {
                return RestResp.success(getMessage(I18NConst.SMS_BEEN_SEND) + mobilephone + getMessage(I18NConst.PLEASE_CHECK), null);
            }
            return RestResp.fail(getMessage(I18NConst.SMS_SENDING_FAILURE));
        } catch (Exception e) {
            log.error("短信发送异常", e);
            return RestResp.fail(getMessage(I18NConst.SMS_SENDING_FAILURE));
        }
    }

    public RestResp resetpwd(String resetkey, String password) {
        User u = null;
        if (resetkey == null || "".equals(resetkey.trim())) {
            return RestResp.fail(getMessage(I18NConst.ACCOUNT_ILLAGAL));
        }
        if (null == password || "".equals(password.trim())) {
            return RestResp.fail(getMessage(I18NConst.PASSWORD_NOT_EMPTY));
        }

        if (resetkey.contains("@")) {
            u = userRepo.findByEmail(resetkey);
        } else {
            u = userRepo.findByMobilephone(resetkey);
        }
        if (null == u) {
            return RestResp.fail(getMessage(I18NConst.RESET_PASSWORD_FAILURE));
        }
        if (null != password) {
            u.setPassword(EncryptUtils.encodeSHA256(password));
            userRepo.save(u);
            if (/*discuzSyncResetpwd*/uresParam.isDiscuzSyncResetpwd() && null != u.getEmail()) {
                boolean flag = UCHelper.resetpwd(u.getLoginname(), u.getEmail(), password);
                if (!flag) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return RestResp.fail(getMessage(I18NConst.RESET_PASSWORD_FAILURE), null);
                }
            }
            return RestResp.success(getMessage(I18NConst.RESET_PASSWORD_SUCCESS), null);
        }
        return RestResp.fail(getMessage(I18NConst.RESET_PASSWORD_FAILURE));
    }

    public RestResp active(String email) {
        if (email == null || "".equals(email) || !RegexUtils.match(email, RegexUtils.REGEX_EMAIL)) {
            return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_ACTIVATE_FAILURE));
        }
        User user = userRepo.findByEmail(email);
        if (null == user) {
            return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_NOT_REGISTERED));
        }
        if (user.getEnabled().equals(Status.EnableStatus.ENABLED.getStatus())) {
            return RestResp.fail(getMessage(I18NConst.ACCOUNT_BEEN_ACTIVATED));
        } else {
            user.setEnabled(Status.EnableStatus.ENABLED.getStatus());
            userRepo.save(user);
            return RestResp.success(getMessage(I18NConst.ACCOUNT_ACTIVATION_SUCCESS), null);
        }
    }

    public RestResp sendMail(String email, String subject, String content) {
        if (email == null || "".equals(email) || !RegexUtils.match(email, RegexUtils.REGEX_EMAIL)) {
            return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
        }
        if (content == null || "".equals(content.trim())) {
            return RestResp.fail(getMessage(I18NConst.SEND_CONTENT_NOT_EMPTY));
        }
        try {
            mailService.sendHtmlMail(email, subject, content);
            return RestResp.success(getMessage(I18NConst.MAIL_BEEN_SEND) + email + getMessage(I18NConst.PLEASE_CHECK), null);
        } catch (Exception e) {
            log.error("邮件发送异常", e);
            return RestResp.fail(getMessage(I18NConst.SEND_MAIL_FAILED));
        }
    }

//    public RestResp addBitcoinAddress(String loginname, String firstAddress) {
//        if (null == loginname || "".equals(loginname.trim())) {
//            return RestResp.fail(getMessage(I18NConst.USERNAME_INCORRECT));
//        }
//        if (null == firstAddress || "".equals(firstAddress.trim()) || firstAddress.length() < 26 || firstAddress.length() > 34) {
//            return RestResp.fail(getMessage(I18NConst.FILL_RECEIVABLE_ADDRESS_CORRECTLY));
//        }
//        firstAddress = firstAddress.trim();
//        User user = userRepo.findByLoginname(loginname);
//        if (null == user) {
//            return RestResp.fail(getMessage(I18NConst.USERNAME_INCORRECT));
//        }
//        if (firstAddress.equals(user.getFirstAddress())) {
//            return RestResp.fail(getMessage(I18NConst.NOT_CHANGED_ADDRESS));
//        }
//        user.setFirstAddress(firstAddress);
//        userRepo.save(user);
//        return RestResp.success(getMessage(I18NConst.ACTION_SUCCESS), firstAddress);
//
//    }

    public RestResp vlist(Integer pageSize, Integer pageNo) {
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;
        Pageable pager = new PageRequest(pageNo - 1, pageSize);
        try {
            Page<ApplyV> page = applyvRepo.findByStatus(Const.APPLYV.APPLIED.getStatus(), pager);
            List<ApplyV> list = page.getContent();
            final List<Long> userIds = new ArrayList<>(list.size());
            list.stream().forEach(applyV -> {
                userIds.add(applyV.getUserId());
            });

            List<User> users = userRepo.findByIdIn(userIds);
            List<ApplyVVO> result = new ArrayList<>(list.size());
            for (ApplyV applyV : list) {
                ApplyVVO vvo = new ApplyVVO(applyV);
                for (User user : users) {
                    if (applyV.getUserId().equals(user.getId())) {
                        vvo.setUsername(user.getLoginname());
                    }
                }
                result.add(vvo);
            }
            return RestRespPage.success(result, page.getTotalElements());
        } catch (Exception e) {
            log.error("获取申请列表失败", e);
            return RestResp.fail(getMessage(I18NConst.OBTAIN_APPLICATION_LIST_FAILURE));
        }

    }

    public RestResp applyV(Long userId) {
        try {
            ApplyV applyV = applyvRepo.findByUserId(userId);
            if (null != applyV) {
                return RestResp.fail(getMessage(I18NConst.SUBMITTED_APPLICATION));
            }

            applyV = new ApplyV(userId, new Date(), Const.APPLYV.APPLIED.getStatus());
            applyV = applyvRepo.save(applyV);
            return RestResp.success(getMessage(I18NConst.APPLICATON_BEEN_SUBMITED));
        } catch (Exception e) {
            log.error("申请异常", e);
            return RestResp.fail(getMessage(I18NConst.APPLICATION_EXECPTION));
        }
    }

    public RestResp applyV(Long userId, Integer status) {
        String msg = null;
        try {
            ApplyV applyV = applyvRepo.findByUserId(userId);
            if (null == applyV) {
                return RestResp.fail(getMessage(I18NConst.HAVE_NOT_SUBMITED_APPLICATION));
            }
            List<UserRole> userRoles = userRoleRepo.findByUserId(userId);
            if (status.equals(Const.APPLYV.CANCELED.getStatus())) {
                msg = getMessage(I18NConst.CANEL_APPLICATION_SUCCESS);
            } else if (status.equals(Const.APPLYV.APPROVED.getStatus())) {
                boolean isv = false;
                for (UserRole userRole : userRoles) {
                    if (userRole.getRoleId().equals(Const.ROLE.V.getId())) {
                        msg = getMessage(I18NConst.ALREADY_BIG_V_USER);
                        isv = true;
                        break;
                    }
                }
                if (!isv) {
                    UserRole userRole = new UserRole(userId, Const.ROLE.V.getId());
                    userRole = userRoleRepo.save(userRole);
                    msg = getMessage(I18NConst.APPROVAL_APPLICATION);
                }
            } else if (status.equals(Const.APPLYV.REJECTED.getStatus())) {
                msg = getMessage(I18NConst.REFUSE_APPLICATION);
            } else {

            }
            applyV.setStatus(status);
            applyV = applyvRepo.save(applyV);
            return RestResp.success(msg);
        } catch (Exception e) {
            log.error("申请异常", e);
            return RestResp.fail(getMessage(I18NConst.APPLICATION_EXECPTION));
        }
    }

    public RestResp getUsers(String userIds) {
        if (null != userIds && !"".equals(userIds)) {
            String[] ids = userIds.split(Const.SEPARATOR_COMMA);
            List<Long> list = new ArrayList<>(ids.length);
            for (String id : ids) {
                list.add(Long.valueOf(id));
            }
            List<User> users = userRepo.findByIdIn(list);
            return RestResp.success(users);
        }
        return RestResp.success();
    }

    public RestResp activitySign(String email){
        if(null == email || "".equals(email) || !RegexUtils.match(email, RegexUtils.REGEX_EMAIL)){
            return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
        }
        try{
            UserActivity userActivity = userActivityRepo.findByActivityIdAndSignUser(/*activityId*/uresParam.getActivityId(),email);
            if(null != userActivity){
                return RestResp.fail(getMessage(I18NConst.SIGN_UP_MSG_ALREADY));
            }
            userActivity = new UserActivity();
            userActivity.setActivityId(/*activityId*/uresParam.getActivityId());
            userActivity.setSignTime(new Date());
            userActivity.setSignUser(email);
            String content = FileUtils.readFileToString(/*mailContentFile*/uresParam.getMailContentFile());
            log.info(content);
            MailVO vo = new MailVO(/*activityEmailUser*/uresParam.getActivityEmailUser(),/*activityEmailAddr*/uresParam.getActivityEmailAddr(),null,email,/*mailSubject*/uresParam.getMailSubject(),content, Const.MAIL_TYPE_HTML);
            //sendGridService.sendHtmlMail(activityEmailAddr,email,mailSubject,content);
            sendGridService.sendMail(vo);
            userActivity = userActivityRepo.save(userActivity);
            if(null != userActivity){
                return RestResp.success(getMessage(I18NConst.SIGN_UP_MSG_SUCCESS),userActivity);
            }
        }catch (Exception e) {
            log.error(getMessage(I18NConst.SIGN_UP_MSG_FAILURE),e);
        }
        return RestResp.fail(getMessage(I18NConst.SIGN_UP_MSG_FAILURE));
    }

    private void addUserRole(Long userId, Long roleId) {
        if (null == userId || null == roleId) {
            return;
        }
        UserRole userRole = userRoleRepo.findByUserIdAndRoleId(userId, roleId);
        if (null == userRole) {
            userRole = new UserRole(userId, roleId);
            userRoleRepo.save(userRole);
        }
    }

    public Set<String> getRoles(Long userId) {
        if (null == userId) {
            return null;
        }
        Set<String> set = new HashSet<>();
        List<UserRole> list = userRoleRepo.findByUserId(userId);
        if (null == list || list.size() < 1) {
            set.add("user");
            return set;
        }
        List<Long> ll = new ArrayList<>();
        list.stream().forEach(userRole -> {
            ll.add(userRole.getRoleId());
        });
        List<Role> roles = roleRepo.findByIdIn(ll);
        for (Role role : roles) {
            set.add(role.getRoleSign());
        }
        return set;
    }

    public RestResp checkUserExist(String name, int nameType) {
        User user = null;
        if (nameType == Const.CFIELD.LOGIN_NAME.getFieldValue()) {
            user = userRepo.findByLoginname(name);
        } else if (nameType == Const.CFIELD.EMAIL.getFieldValue()) {
            if (!RegexUtils.match(name, RegexUtils.REGEX_EMAIL)) {
                return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
            }
            user = userRepo.findByEmail(name);
        } else if (nameType == Const.CFIELD.MOBILE_PHONE.getFieldValue()) {
            if (!RegexUtils.match(name, RegexUtils.REGEX_MOBILEPHONE)) {
                return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
            }
            user = userRepo.findByMobilephone(name);
        } else {

        }
        if (null != user) {
            return RestResp.success(getMessage(I18NConst.VERIFICATION_SUCCESS));
        }
        return RestResp.fail(getMessage(I18NConst.VERIFICATION_FAILURE));
    }

    public RestResp checkLoginname(String loginname) {
        if (null == loginname || !RegexUtils.match(loginname, RegexUtils.REGEX_NAME_LEN15)) {
            return RestResp.fail(getMessage(I18NConst.LOGIN_NAME_FORMAT));
        }
        User user = userRepo.findByLoginname(loginname);
        if (null != user) {
            return RestResp.fail(getMessage(I18NConst.USER_ALREADY_EXIST));
        }
        return RestResp.success(getMessage(I18NConst.VERIFICATION_SUCCESS));
    }

    public RestResp checkEmail(String email) {
        if (null == email || !RegexUtils.match(email, RegexUtils.REGEX_EMAIL)) {
            return RestResp.fail(getMessage(I18NConst.FILL_MAIL_ADDRESS_CORRECTLY));
        }
        User user = userRepo.findByEmail(email);
        if (null != user) {
            return RestResp.fail(getMessage(I18NConst.MAIL_ADDRESS_BEEN_USED));
        }
        return RestResp.success(getMessage(I18NConst.VERIFICATION_SUCCESS));
    }

    public RestResp checkMobilephone(String mobilephone) {
        if (null == mobilephone || !RegexUtils.match(mobilephone, RegexUtils.REGEX_MOBILEPHONE)) {
            return RestResp.fail(getMessage(I18NConst.FILL_PHONE_NUMBER_CORRECTLY));
        }
        User user = userRepo.findByMobilephone(mobilephone);
        if (null != user) {
            return RestResp.fail(getMessage(I18NConst.PHONE_NUMBER_BEEN_USED));
        }
        return RestResp.success(getMessage(I18NConst.VERIFICATION_SUCCESS));
    }



    public RestResp token(UserVO vo){
        Optional<User> optional = findUser(vo);
        if(optional.isPresent()){
           return optional.map(u -> {
                UserVO userInfo = new UserVO(u);
                userInfo.setRoles(getRoles(u.getId()));
                String token = jwtService.generate(userInfo);
                return RestResp.success(token);
            }).orElse(RestResp.fail());
        }
        return RestResp.fail();
    }

    public UserVO getUserVO(Long id){
        User user = userRepo.findOne(id);
        if(null != user){
            UserVO userInfo = new UserVO(user);
            userInfo.setPassword(user.getPassword());
            userInfo.setRoles(getRoles(userInfo.getId()));
            return userInfo;
        }
        return null;
    }
    public UserVO getUserVO(String loginname){
        User user = userRepo.findByLoginname(loginname);
        if(null != user){
            UserVO userInfo = new UserVO(user);
            userInfo.setPassword(user.getPassword());
            userInfo.setRoles(getRoles(userInfo.getId()));
            return userInfo;
        }
        return null;
    }
    private String getMessage(String code) {
        return myMessageSource.getMessage(code);
    }
}
