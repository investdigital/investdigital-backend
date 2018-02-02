package com.oxchains.rmsuser.rest;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.oxchains.rmsuser.common.RegexUtils;
import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.entity.User;
import com.oxchains.rmsuser.entity.UserVO;
import com.oxchains.rmsuser.entity.VerifyCode;
import com.oxchains.rmsuser.service.KaptchaService;
import com.oxchains.rmsuser.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

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
    KaptchaService kaptchaService;

/*    @GetMapping(value = "/list")
    public RestResp list(){
        return userService.findUsers();
    }*/

    @PostMapping(value = "/signup")
    public RestResp register(@RequestBody UserVO user){
        return userService.addUser(user);
    }

    @PostMapping(value = "/signin")
    public RestResp login(@RequestBody UserVO user){
        return userService.login(user);
    }

    @PostMapping(value = "/logout")
    public RestResp logout(@RequestBody User user){
        return userService.logout(user);
    }

    @RequestMapping(value = "/avatar")
    public RestResp vatar(@ModelAttribute User user) throws Exception{
        return userService.avatar(user);
    }

    /**
     * 图片验证码
     */
    @RequestMapping(value = "/imgVcode")
    public void defaultKaptcha(VerifyCode vcode,HttpServletRequest request, HttpServletResponse response) throws Exception{
        if(null == vcode || vcode.getKey()==null || "".equals(vcode.getKey().trim())){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = kaptchaService.createText();
            if(!userService.saveVcode(vcode.getKey(),createText)){
                request.getSession().setAttribute(vcode.getKey(), createText);
            }
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = kaptchaService.createImage(createText);
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
    @PostMapping(value = "/phoneVcode")
    public RestResp phoneVcode(UserVO vo/*String loginname, String mobilephone*/,HttpServletRequest request) throws Exception{
        String mobilephone = vo.getMobilephone();
        if(null == mobilephone || "".equals(mobilephone.trim())){
            return RestResp.fail("手机号不能为空");
        }
        if(!RegexUtils.match(mobilephone,RegexUtils.REGEX_MOBILEPHONE)){
            return RestResp.fail("请输入正确的手机号");
        }
        try {
            //生产验证码字符串并保存到session中
            String createText = kaptchaService.createText();
            if(!userService.saveVcode(mobilephone,createText)){
                request.getSession().setAttribute(mobilephone, createText);
            }
            //手机发送
            //TODO
            System.out.println("********手机验证码********:"+createText);
            return RestResp.success(createText);
        } catch (IllegalArgumentException e) {
            return RestResp.fail("404");
        }
    }

    /**
     * 验证验证码
     */
    @RequestMapping("/verifyCode")
    public RestResp verifytKaptchaCode(VerifyCode vcode, HttpServletRequest request, HttpServletResponse response){
        if(null == vcode || null == vcode.getKey() || null == vcode.getVcode() || "".equals(vcode.getKey().trim()) || "".equals(vcode.getVcode().trim())){
            return RestResp.fail("参数不能为空");
        }
        String vcodeVal = userService.getVcodeFromRedis(vcode.getKey());
//        String captchaId = (String) request.getSession().getAttribute("vcode");
//        String parameter = request.getParameter("vcode");

        if (vcodeVal.equals(vcode.getVcode())) {
            return RestResp.success("验证成功");
        }
        return RestResp.fail("验证失败");
    }

    /**
     * 发送邮件
     * @return
     */
    @RequestMapping(value = "/sendVmail")
    public RestResp sendVerifyMail(VerifyCode vcode){
        return userService.sendVmail(vcode);
    }

    @RequestMapping(value = "/verifyEmail")
    public RestResp verifyEmainl(VerifyCode vcode){
        return userService.verifyEmail(vcode);
    }
    /**
     * 重置密码
     */
    @PostMapping(value = "/resetpwd")
    public RestResp resetpwd(String resetkey,String password){
        return userService.resetpwd(resetkey,password);
    }

    @GetMapping(value = "/active")
    public RestResp activeUser(String email){
        return userService.active(email);
    }

    @PostMapping(value = "/mail")
    public RestResp sendMail(String email, String subject,String content){
        return userService.sendMail(email,subject,content);
    }


    /**
     * 邮箱/手机获取激活验证码
     */
    @PostMapping(value = "/captcha")
    public RestResp getCaptcha(@RequestBody UserVO user){
        return userService.getCaptcha(user);
    }

    /**
     *  邮箱/手机激活账号
     */
    @RequestMapping(value = "/vunlock")
    public RestResp vunlockUser(UserVO user){
        return userService.vunlockUser(user);
    }

    @GetMapping(value = "/list")
    public RestResp list(String loginname,Integer pageNo, Integer pageSize){
        return userService.list(loginname,pageNo,pageSize);
    }

    @PostMapping(value = "/add")
    public RestResp addUser(@RequestBody UserVO user){
            return userService.backAddUser(user);
    }

    @PostMapping(value = "/delete")
    public RestResp deleteUser(@RequestBody UserVO user){
        return userService.deleteUser(user);
    }

    @PostMapping(value = "/lock")
    public RestResp lockUser(@RequestBody UserVO user){
        return userService.lockUser(user);
    }

    @PostMapping(value = "/unlock")
    public RestResp unlockUser(@RequestBody UserVO user){
        return userService.unlockUser(user);
    }

}
