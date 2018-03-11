package info.investdigital.rest;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import info.investdigital.common.ParamType;
import info.investdigital.common.RegexUtils;
import info.investdigital.common.RestResp;
import info.investdigital.entity.User;
import info.investdigital.entity.VerifyCode;
import info.investdigital.service.UserService;
import info.investdigital.common.Const;
import info.investdigital.entity.UserVO;
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
    @PostMapping(value = "/register")
    public RestResp register(@RequestBody User user){
        return userService.addUser(user);
    }

    @PostMapping(value = "/signup")
    public RestResp signup(@RequestBody UserVO user){
        return userService.signup(user);
    }

    @PostMapping(value = "/login")
    public RestResp login(@RequestBody User user){
        return userService.login(user);
    }
    @PostMapping(value = "/signin")
    public RestResp signin(@RequestBody UserVO user){
        return userService.signin(user);
    }
    @PostMapping(value = "/logout")
    public RestResp logout(@RequestBody User user){
        return userService.logout(user);
    }

    @RequestMapping(value = "/avatar")
    public RestResp vatar(@ModelAttribute User user) throws Exception{
        return userService.avatar(user);
    }

    @PostMapping(value = "/info")
    public RestResp info(@RequestBody User user) throws Exception{
        return userService.updateUser(user,ParamType.UpdateUserInfoType.INFO);
    }

    /**
     * 修改电子邮箱
     * @param user
     * @return
     */
    @PostMapping(value = "/email")
    public RestResp email(@RequestBody User user){
        return userService.updateUser(user, ParamType.UpdateUserInfoType.EMAIL);
    }

    /**
     * 修改手机号
     * @param user
     * @return
     */
    @PostMapping(value = "/phone")
    public RestResp phone(@RequestBody User user){
        return userService.updateUser(user, ParamType.UpdateUserInfoType.PHONE);
    }

    /**
     * 修改密码
     * @param user
     * @return
     */
    @PostMapping(value = "/password")
    public RestResp password(@RequestBody User user){
        return userService.updateUser(user, ParamType.UpdateUserInfoType.PWD);
    }
    @Resource
    DefaultKaptcha defaultKaptcha;

    /**
     * 图片验证码
     */
    @RequestMapping(value = "/imgVcode")
    public void defaultKaptcha(VerifyCode vcode, HttpServletRequest request, HttpServletResponse response) throws Exception{
        if(null == vcode || vcode.getKey()==null || "".equals(vcode.getKey().trim())){
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
    @RequestMapping(value = "/phoneVcode")
    public RestResp phoneVcode(String loginname,String mobilephone,HttpServletRequest request) throws Exception{
        if(null == mobilephone || "".equals(mobilephone.trim())){
            return RestResp.fail("手机号不能为空");
        }
        if(!RegexUtils.match(mobilephone,RegexUtils.REGEX_MOBILEPHONE)){
            return RestResp.fail("请输入正确的手机号");
        }
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            if(!userService.saveVcode(mobilephone,createText)){
                request.getSession().setAttribute(mobilephone, createText);
            }
            //手机发送
            //TODO
            return RestResp.success(createText);
        } catch (IllegalArgumentException e) {
            return RestResp.fail("404");
        }
    }

    @RequestMapping(value = "/mailVcode")
    public RestResp sendVmailCode(String email){
        return userService.sendVmailCode(email);
    }

    /**
     * 验证验证码
     */
    @RequestMapping("/verifyICode")
    public RestResp verifytKaptchaCode(VerifyCode vcode, HttpServletRequest request, HttpServletResponse response){
        if(null == vcode || null == vcode.getKey() || null == vcode.getVcode() || "".equals(vcode.getKey().trim()) || "".equals(vcode.getVcode().trim())){
            return RestResp.fail("参数不能为空");
        }
        String vcodeVal = userService.getVcodeFromRedis(vcode.getKey());
        if (vcodeVal.equals(vcode.getVcode())) {
            return RestResp.success("验证码正确");
        }
        return RestResp.fail("验证码错误");
    }

    /**
     * 发送邮件
     * @return
     */
    @RequestMapping(value = "/sendVmail")
    public RestResp sendVerifyMail(VerifyCode vcode){
        return userService.sendVmail(vcode);
    }

    /**
     * 重置密码
     */
    @PostMapping(value = "/resetpwd")
    public RestResp resetpwd(String resetkey,String password){
        return userService.resetpwd(resetkey,password);
    }

    @PostMapping(value = "/applyV/{userId}")
    public RestResp applyV(@PathVariable Long userId){
        return userService.applyV(userId);
    }

    @PostMapping(value = "/cancelV/{userId}")
    public RestResp cancelV(@PathVariable Long userId){
        return userService.applyV(userId, Const.APPLYV.CANCELED.getStatus());
    }

    @PostMapping(value = "/approveV/{userId}")
    public RestResp approveV(@PathVariable Long userId){
        return userService.applyV(userId, Const.APPLYV.APPROVED.getStatus());
    }

    @PostMapping(value = "/rejectV/{userId}")
    public RestResp rejectV(@PathVariable Long userId){
        return userService.applyV(userId, Const.APPLYV.REJECTED.getStatus());
    }

}
