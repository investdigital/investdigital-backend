package com.oxchains.rmsuser.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ccl
 * @time 2018-01-26 10:10
 * @name KaptchaService
 * @desc:
 */
@Slf4j
@Service
public class KaptchaService {
    @Resource
    DefaultKaptcha defaultKaptcha;


    public String createText(){
        return defaultKaptcha.createText();
    }

    public BufferedImage createImage(String text){
        return defaultKaptcha.createImage(text);
    }

    public ByteArrayOutputStream createImageByteArrayOutputStream(BufferedImage bufferedImage, String type){
        if(null == bufferedImage){
            return null;
        }
        if(null == type || "".equals(type.trim())){
            type = "jpg";
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            return outputStream;
        }catch (IOException e){
            log.error("IO异常",e);
            return null;
        }
    }

    public byte[] createByteArray(ByteArrayOutputStream byteArrayOutputStream){
        if(null == byteArrayOutputStream){
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }
}
