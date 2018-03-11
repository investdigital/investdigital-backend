package info.investdigital.service;
import info.investdigital.auth.JwtService;
import info.investdigital.dao.PrepareAddressRepo;
import info.investdigital.dao.UserRepo;
import info.investdigital.entity.*;
import info.investdigital.common.*;
import info.investdigital.uc.UCHelper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.oxchains.basicService.files.tfsService.TFSConsumer;
import info.investdigital.dao.ApplyvRepo;
import info.investdigital.dao.RoleRepo;
import info.investdigital.dao.UserRoleRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author ccl
 * @time 2017-12-12 17:08
 * @name UserService
 * @desc:
 */

//@Transactional
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserService{

    //private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepo userRepo;

    @Resource
    JwtService jwtService;

    private String token;

    @Resource
    MailService mailService;

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
    private Web3Service web3Service;

    public String getRandomAddress(){
        Pageable pageable = new PageRequest(1,1,null);
        List<PrepareAddress> content = prepareAddressRepo.findAll(pageable).getContent();
        if(content.size()>0){
            PrepareAddress prepareAddress = content.get(0);
            String address =  prepareAddress.getAddress();
            prepareAddressRepo.delete(prepareAddress.getId());
            return address;
        }
        return null;
    }
    public RestResp addUser(User user) {
        boolean mail = false;
        if(null == user){
            return RestResp.fail("请正确提交的注册信息");
        }
        if(null == user.getLoginname() || !RegexUtils.match(user.getLoginname(),RegexUtils.REGEX_NAME_LEN32)){
            return RestResp.fail("请正确填写登录名，只能包含字母、数字、下划线，且只能以字母开头");
        }
        if(null != user.getMobilephone()){
            if(!RegexUtils.match(user.getMobilephone(),RegexUtils.REGEX_MOBILEPHONE)){
                return RestResp.fail("请正确填写手机号");
            }
        }
        if(null != user.getEmail()){
            if(!RegexUtils.match(user.getEmail(),RegexUtils.REGEX_EMAIL)){
                return RestResp.fail("请正确填写邮箱地址");
            }
            user.setEnabled(Status.EnableStatus.UNENABLED.getStatus());
            mail = true;
        }else {
            user.setEnabled(Status.EnableStatus.ENABLED.getStatus());
        }
        Optional<User> optional = getUser(user);
        if (optional.isPresent()) {
            User u = optional.get();
            if(null != user.getLoginname() && user.getLoginname().equals(u.getLoginname())){
                return RestResp.fail("用户名已经存在");
            }
            if(null != user.getMobilephone() && user.getMobilephone().equals(u.getMobilephone())){
                return RestResp.fail("该手机号已被注册");
            }
            if(null != user.getEmail() && user.getEmail().equals(u.getEmail())){
                return RestResp.fail("该邮箱已被注册");
            }
            return RestResp.fail("注册用户已经存在");
        }
        if(null==user.getPassword() || "".equals(user.getPassword().trim())){
            return RestResp.fail("请正确填写登录密码");
        }
        user.setPassword(EncryptUtils.encodeSHA256(user.getPassword()));
        if(null == user.getCreateTime()){
            user.setCreateTime(DateUtil.getPresentDate());
        }

        if (null == user.getLoginStatus()){
            user.setLoginStatus(0);
        }

        user = userRepo.save(user);
        //certificationUser tochains
        this.certificationUser(user.getId());
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

    public boolean deleteUserForChain(Long userId){
        try {
            String from  = ParamZero.IDOperationUserAddress;
            String firstAddress = userRepo.findOne(userId).getFirstAddress();
            Function function = new Function(
                    "deleteUser",
                    Arrays.<Type>asList(new Address(firstAddress)),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(function);
            EthTransactionEntity ethTransactionEntity = new EthTransactionEntity(web3Service.getNonce(from),from,ParamType.ContractAddress.USER_CONTRACT.getAddress(),"0x00",data);
            String txStr = web3Service.getTxStr(JsonUtil.toJson(ethTransactionEntity));
            boolean b = web3Service.sendRawTransaction(txStr);
            return b;
        } catch (Exception e) {
            log.error("delete user for chain error userID:{} Csusy By:{}",userId,e.getMessage(),e);
            return false;
        }
    }

    public void certificationUser(Long userId){
        try {
            User user = userRepo.findOne(userId);
            //给用户分配一个全新的地址
            String randomAddress = this.getRandomAddress();
            user.setFirstAddress(randomAddress);
            userRepo.save(user);
            boolean b = this.certificationUserAddress(randomAddress);
            if(b){
                log.info("certification user address error userId:{}",userId);
            }
        } catch (Exception e) {
           log.error("certification user address error:{}",e.getMessage(),e);
        }
    }
    //将用户地址认证到合约地址
    public boolean certificationUserAddress(String address) throws Exception {
        try {
            String to = ParamType.ContractAddress.USER_CONTRACT.getAddress();
            String from = ParamZero.IDOperationUserAddress;
            Function function = new Function(
                    "addUser",
                    Arrays.<Type>asList(new Address(address)),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(function);
            EthTransactionEntity ethTransactionEntity = new EthTransactionEntity(web3Service.getNonce(from),from,to,"0x00",data);
            String s = JsonUtil.toJson(ethTransactionEntity);
            String txStr = web3Service.getTxStr(s);
            boolean b = web3Service.sendRawTransaction(txStr);
            return b;
        } catch (Exception e) {
            log.error("certification User Address faild:{}",e.getMessage(),e);
            throw e;
        }

    }
    public RestResp signup(UserVO user) {
        if(null == user){
            return RestResp.fail("请正确提交的注册信息");
        }

        if(null != user.getEmail()){
            if(!RegexUtils.match(user.getEmail(),RegexUtils.REGEX_EMAIL)){
                return RestResp.fail("请正确填写邮箱地址");
            }
            user.setEnabled(Status.EnableStatus.ENABLED.getStatus());
        }else {
            return RestResp.fail("请正确填写邮箱地址");
        }
        Optional<User> optional = getUser(user);
        if (optional.isPresent()) {
            User u = optional.get();
            if(null != u.getEmail() && u.getEmail().equals(user.getEmail())){
                return RestResp.fail("该邮箱已被注册");
            }
            return RestResp.fail("注册用户已经存在");
        }
        if(null==user.getPassword() || "".equals(user.getPassword().trim())){
            return RestResp.fail("请正确填写登录密码");
        }
        String password = user.getPassword();
        user.setPassword(EncryptUtils.encodeSHA256(password));
        if(null == user.getCreateTime()){
            user.setCreateTime(DateUtil.getPresentDate());
        }
        if (null == user.getLoginStatus()){
            user.setLoginStatus(0);
        }
        String vcode = getVcodeFromRedis(user.getEmail());
        if(null == vcode || "".equals(vcode.trim()) || !vcode.equals(user.getVcode().trim())){
            return RestResp.fail("请正确填写验证码");
        }
        user.setLoginname(user.getEmail());
       User u = userRepo.save(user.userVO2User());
        if (u == null) {
            return RestResp.fail("操作失败");
        }

        addUserRole(u.getId(),Const.ROLE.USER.getId());
        UserVO userInfo = new UserVO(u);
        userInfo.setRoles(getRoles(u.getId()));
        UCHelper.reg(userInfo.getLoginname(),password,userInfo.getEmail());
        return RestResp.success("注册成功",userInfo);

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
    public RestResp updateUser(User user, ParamType.UpdateUserInfoType uuit) {
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
            case FPWD:
                u.setFpassword(EncryptUtils.encodeSHA256(user.getFpassword()));
                break;
            case EMAIL:
                if(null == user.getEmail() || "".equals(user.getEmail().trim()) || !RegexUtils.match(user.getEmail(),RegexUtils.REGEX_EMAIL)){
                    return RestResp.fail("请输入正确的邮箱地址");
                }
                if(u.getEmail().equals(user.getEmail())){
                    return RestResp.fail("邮箱正在使用");
                }
                User eu = userRepo.findByEmail(user.getEmail());
                if(null != eu){
                    return RestResp.fail("邮箱已被使用");
                }
                //TODO 发送邮件

                u.setEmail(user.getEmail());
                break;
            case PHONE:
                if(null == user.getMobilephone() || "".equals(user.getMobilephone().trim()) || !RegexUtils.match(user.getMobilephone(),RegexUtils.REGEX_MOBILEPHONE)){
                    return RestResp.fail("请输入正确的手机号");
                }
                if(u.getMobilephone().equals(user.getMobilephone())){
                    return RestResp.fail("手机号正在使用");
                }
                User mu = userRepo.findByMobilephone(user.getMobilephone());
                if(null != mu){
                    return RestResp.fail("手机号已被使用");
                }
                //TODO 发送验证码
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
        MultipartFile file = user.getFile();
        if(null != file) {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = tfsConsumer.saveTfsFile(file, u.getId());
            if (null == newFileName) {
                return RestResp.fail("头像上传失败");
            }
            u.setImage(newFileName);
            userRepo.save(u);
            return RestResp.success("头像上传成功",newFileName);
        }
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

    public RestResp login(User user) {
        user.setPassword(EncryptUtils.encodeSHA256(user.getPassword()));
        try{
            Optional<User> optional = findUser(user);
            return optional.map(u -> {
                if(u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())){
                    return RestResp.fail("账号未激活");
                }
                if(u.getLoginStatus().equals(Status.LoginStatus.LOGIN.getStatus())){
                    return RestResp.fail("用户已经登录");
                }
                String originToken = jwtService.generate(u);
                token = "Bearer " + originToken;


                log.info("token = " + token);
                UserVO userInfo = new UserVO(u);

                userInfo.setPassword(null);
                userInfo.setToken(token);


                u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
                User save = userRepo.save(u);

                //new UserToken(u.getUsername(),token)
                return RestResp.success("登录成功", userInfo);
            }).orElse(RestResp.fail("登录账号或密码错误"));
        }catch (Exception e){
            log.error("用户信息异常",e);
            return RestResp.fail("用户信息异常");
        }
    }

    public RestResp signin(UserVO user) {
        if(!RegexUtils.match(user.getEmail(),RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("账号有误");
        }
        user.setPassword(EncryptUtils.encodeSHA256(user.getPassword()));
        try{
            Optional<User> optional = findUser(user);
            return optional.map(u -> {
                if(u.getEnabled().equals(Status.EnableStatus.UNENABLED.getStatus())){
                    return RestResp.fail("账号未激活");
                }
                if(u.getLoginStatus().equals(Status.LoginStatus.LOGIN.getStatus())){
                    return RestResp.fail("用户已经登录");
                }
                String originToken = jwtService.generate(u);
                token = "Bearer " + originToken;
                log.info("token = " + token);

//                User userInfo = new User(u);
                UserVO userInfo = new UserVO(u);
                userInfo.setToken(token);
                u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
                User save = userRepo.save(u);

                //new UserToken(u.getUsername(),token)
                return RestResp.success("登录成功", userInfo);
            }).orElse(RestResp.fail("登录账号或密码错误"));
        }catch (Exception e){
            log.error("用户信息异常",e);
            return RestResp.fail("用户信息异常");
        }
    }

    public RestResp logout(User user){
        User u = userRepo.findByLoginname(user.getLoginname());
        if(null != u && u.getLoginStatus().equals(Status.LoginStatus.LOGIN.getStatus())){
            u.setLoginStatus(Status.LoginStatus.LOGOUT.getStatus());
            userRepo.save(u);
            return RestResp.success("退出成功",null);
        }else {
            return RestResp.fail("退出失败");
        }
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

    @Value("${ID.frontend.url}")
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

    private final String signupStr = "您好，</br></br>感谢您注册InvestDigital。</br></br>您的验证码是：%s</br></br>出于安全原因，该验证码将于30分钟后失效。请勿将该验证码透露给他人。</br></br></br>Sincerely</br>ID团队</br><a href='https://investdigital.info/'>https://investdigital.info/</a>";
    @Resource
    DefaultKaptcha defaultKaptcha;
    public RestResp sendVmailCode(String email){

        if(null==email||"".equals(email.trim()) || !RegexUtils.match(email,RegexUtils.REGEX_EMAIL)){
            return RestResp.fail("输入的邮箱格式不正确");
        }
        String code = defaultKaptcha.createText();
        saveVcode(email,code);

        String content =String.format(signupStr,code);
        try{
            mailService.sendHtmlMail(email,"注册",content);
            return RestResp.success("邮件已发送到："+email+"，请尽快登录获取",null);
        }catch (Exception e){
            log.error("邮件发送异常",e);
            return RestResp.fail("邮件发送失败,请重新操作");
        }

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
        if(firstAddress.equals(user.getFirstAddress())){
            return RestResp.fail("您未修改地址");
        }
        user.setFirstAddress(firstAddress);
        userRepo.save(user);
        return RestResp.success("操作成功",firstAddress);

    }

    public RestResp applyV(Long userId){
        try{
            ApplyV applyV = applyvRepo.findByUserId(userId);
            if(null != applyV){
                return RestResp.fail("您已经提交过申请,请勿重复提交");
            }

            applyV = new ApplyV(userId,new Date(),Const.APPLYV.APPLIED.getStatus());
            applyV = applyvRepo.save(applyV);
            return RestResp.success("申请已经提交,等待审核中");
        }catch (Exception e){
            log.error("申请异常",e);
            return RestResp.fail("申请异常");
        }
    }

    public RestResp applyV(Long userId,Integer status){
        String msg = null;
        try{
            ApplyV applyV = applyvRepo.findByUserId(userId);
            if(null == applyV){
                return RestResp.fail("您还提交过申请");
            }
            List<UserRole> userRoles = userRoleRepo.findByUserId(userId);
            if(status.equals(Const.APPLYV.CANCELED.getStatus())){
                msg = "取消申请成功";
            }else if(status.equals(Const.APPLYV.APPROVED.getStatus())){
                boolean isv = false;
                for(UserRole userRole: userRoles){
                    if(userRole.getRoleId().equals(Const.ROLE.V.getId())){
                        msg = "您已是大V用户";
                        isv = true;
                        break;
                    }
                }
                if(!isv){
                    UserRole userRole = new UserRole(userId,Const.ROLE.V.getId());
                    userRole = userRoleRepo.save(userRole);
                    msg = "批准申请";
                }
            }else if(status.equals(Const.APPLYV.REJECTED.getStatus())){
                msg = "拒绝申请";
            }else {

            }
            applyV.setStatus(status);
            applyV = applyvRepo.save(applyV);
            return RestResp.success(msg);
        }catch (Exception e){
            log.error("申请异常",e);
            return RestResp.fail("申请异常");
        }
    }

    private void addUserRole(Long userId,Long roleId){
        if(null == userId || null == roleId){
            return;
        }
        UserRole userRole = userRoleRepo.findByUserIdAndRoleId(userId,roleId);
        if(null == userRole){
            userRole = new UserRole(userId,roleId);
            userRoleRepo.save(userRole);
        }
    }

    private Set<String> getRoles(Long userId){
        if (null == userId){
            return null;
        }
        Set<String> set = new HashSet<>();
        List<UserRole> list = userRoleRepo.findByUserId(userId);
        if(null == list || list.size()<1){
            set.add("user");
            return set;
        }
        List<Long> ll = new ArrayList<>();
        list.stream().forEach(userRole -> {
            ll.add(userRole.getRoleId());
        });
        List<Role> roles = roleRepo.findByIdIn(ll);
        for(Role role: roles){
            set.add(role.getRoleName());
        }
        return set;
    }

}
