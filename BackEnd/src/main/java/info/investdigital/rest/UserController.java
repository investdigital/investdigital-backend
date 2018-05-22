package info.investdigital.rest;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import info.investdigital.common.Const;
import info.investdigital.common.ParamType;
import info.investdigital.common.RestResp;
import info.investdigital.entity.User;
import info.investdigital.entity.UserVO;
import info.investdigital.entity.VerifyCode;
import info.investdigital.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * @author ccl
 * @time 2017-10-12 18:19
 * @name UserController
 * @desc:
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Resource
    UserService userService;

    @Resource
    private MyMessageSource myMessageSource;


    @Deprecated
    @PostMapping(value = "/register")
    public RestResp register(@RequestBody User user) throws Exception {
        return userService.addUser(user);
    }
    //邮箱注册
    @PostMapping(value = "/signup")
    public RestResp signup(@RequestBody UserVO user) throws Exception {
        return userService.signup(user);
    }

    @Deprecated
    @PostMapping(value = "/login")
    public RestResp login(@RequestBody User user) {
        return userService.login(user);
    }

    /**
     * 用户登录 邮箱登录和 手机登录
     * @param user
     * @return
     */
    @PostMapping(value = "/signin")
    public RestResp signin(@RequestBody UserVO user) {
        return userService.signin(user);
    }

    @PostMapping(value = "/logout")
    public RestResp logout(@RequestBody UserVO user) {
        return userService.logout(user);
    }

    @PostMapping(value = "/signout/{userId}")
    public RestResp signout(@PathVariable Long userId) {
        return userService.signout(userId);
    }

    /**
     * 上传头像
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/avatar")
    public RestResp vatar(@ModelAttribute UserVO user) throws Exception {
        return userService.avatar(user);
    }

    @PostMapping(value = "/info")
    public RestResp info(@RequestBody UserVO user) throws Exception {
        return userService.updateUser(user, ParamType.UpdateUserInfoType.INFO);
    }

    @PostMapping(value = "/nick")
    public RestResp nick(@RequestBody UserVO user) throws Exception {
        return userService.updateUser(user, ParamType.UpdateUserInfoType.NICK_NAME);
    }

    /**
     * 修改电子邮箱
     *
     * @param user
     * @return
     */
    @PostMapping(value = "/bind/email")
    public RestResp email(@RequestBody UserVO user) {
        return userService.updateUser(user, ParamType.UpdateUserInfoType.EMAIL);
    }

    /**
     * 修改手机号
     *
     * @param user
     * @return
     */
    @PostMapping(value = "/bind/mobilephone")
    public RestResp phone(@RequestBody UserVO user) {
        return userService.updateUser(user, ParamType.UpdateUserInfoType.PHONE);
    }

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    @PostMapping(value = "/password")
    public RestResp password(@RequestBody UserVO user) {
        return userService.updateUser(user, ParamType.UpdateUserInfoType.PWD);
    }

    @Resource
    DefaultKaptcha defaultKaptcha;


    @Deprecated
    @RequestMapping(value = "/imgVcode")
    public void defaultKaptcha(VerifyCode vcode, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (null == vcode || vcode.getKey() == null || "".equals(vcode.getKey().trim())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    /**
     *
     */
    @RequestMapping(value = "/phoneVcode/{mobilephone}")
    public RestResp phoneVcode(@PathVariable String mobilephone, HttpServletRequest request) throws Exception {
        return userService.sendPhoneCode(mobilephone);
    }

    @RequestMapping(value = "/mailVcode")
    public RestResp sendVmailCode(String email) {
        return userService.sendVmailCode(email);
    }

    /**
     * 验证验证码
     */
    @RequestMapping("/verifyICode")
    public RestResp verifytKaptchaCode(VerifyCode vcode, HttpServletRequest request, HttpServletResponse response) {
        if (null == vcode || null == vcode.getKey() || null == vcode.getVcode() || "".equals(vcode.getKey().trim()) || "".equals(vcode.getVcode().trim())) {
            return RestResp.fail(getMessage(I18NConst.PARAMETERS_NOT_EMPTY));
        }
        String vcodeVal = userService.getVcodeFromRedis(vcode.getKey());
        if (vcodeVal.equals(vcode.getVcode())) {
            return RestResp.success(getMessage(I18NConst.VERIFICATION_CODE_CORRECT));
        }
        return RestResp.fail(getMessage(I18NConst.VERIFICATION_CODE_ERROR));
    }

    /**
     * 发送邮件
     *
     * @return
     */
    @RequestMapping(value = "/sendVmail")
    public RestResp sendVerifyMail(VerifyCode vcode) {
        return userService.sendVmail(vcode);
    }

    /**
     * 重置密码
     */
    @PostMapping(value = "/resetpwd")
    public RestResp resetpwd(String resetkey, String password) {
        return userService.resetpwd(resetkey, password);
    }

    @GetMapping(value = "/vlist/{pageSize}/{pageNo}")
    public RestResp vlist(@PathVariable Integer pageSize, @PathVariable Integer pageNo) {
        return userService.vlist(pageSize, pageNo);
    }

    @PostMapping(value = "/applyV/{userId}")
    public RestResp applyV(@PathVariable Long userId) {
        return userService.applyV(userId);
    }

    @PostMapping(value = "/cancelV/{userId}")
    public RestResp cancelV(@PathVariable Long userId) {
        return userService.applyV(userId, Const.APPLYV.CANCELED.getStatus());
    }

    @PostMapping(value = "/approveV/{userId}")
    public RestResp approveV(@PathVariable Long userId) {
        return userService.applyV(userId, Const.APPLYV.APPROVED.getStatus());
    }

    @PostMapping(value = "/rejectV/{userId}")
    public RestResp rejectV(@PathVariable Long userId) {
        return userService.applyV(userId, Const.APPLYV.REJECTED.getStatus());
    }

    @GetMapping(value = "/list/in/{userIds}")
    public RestResp rejectV(@PathVariable String userIds) {
        return userService.getUsers(userIds);
    }


    @RequestMapping(value = "/check/mail")
    public RestResp checkMail(String email) {
        return userService.checkUserExist(email, Const.CFIELD.EMAIL.getFieldValue());
    }

    @RequestMapping(value = "/check/phone")
    public RestResp checkPhone(String mobilephone) {
        return userService.checkUserExist(mobilephone, Const.CFIELD.MOBILE_PHONE.getFieldValue());
    }

    @GetMapping(value = "/exist/loginname/{loginname}")
    public RestResp existName(@PathVariable String loginname) {
        return userService.checkLoginname(loginname);
    }

    @GetMapping(value = "/exist/email")
    public RestResp existEmail(String email) {
        return userService.checkEmail(email);
    }

    @GetMapping(value = "/exist/mobilephone/{mobilephone}")
    public RestResp existPhone(@PathVariable String mobilephone) {
        return userService.checkMobilephone(mobilephone);
    }

    @PostMapping(value = "/token")
    public RestResp token(@RequestBody UserVO vo){
        return userService.token(vo);
    }

    @PostMapping(value = "/activity")
    public RestResp activitySign(String email){
        return userService.activitySign(email);
    }

    private String getMessage(String code) {
        return myMessageSource.getMessage(code);
    }

}
