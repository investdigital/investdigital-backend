package com.oxchains.rmsuser.rest;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.oxchains.rmsuser.common.RestResp;
import com.oxchains.rmsuser.entity.User;
import com.oxchains.rmsuser.entity.UserVO;
import com.oxchains.rmsuser.entity.VerifyCode;
import com.oxchains.rmsuser.service.UserService;
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

    @GetMapping(value = "/list")
    public RestResp list(){
        return userService.findUsers();
    }

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



}
