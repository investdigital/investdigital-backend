package info.investdigital.rest;
import info.investdigital.common.RestResp;
import info.investdigital.common.GoogleAuthenticator;
import info.investdigital.common.QrcodeUtils;
import info.investdigital.service.GoogleSecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

/**
 * @Author: Gaoyp
 * @Description:
 * @Date: Create in 下午12:55 2018/3/5
 * @Modified By:
 */

@RestController
public class GoogleSecretController {


    @Autowired
    private GoogleSecretService googleSecretService;

    /**
     * @Description: 生成绑定验证器的二维码
     * @Date: Create in 下午12:59 2018/3/5
    */

    @RequestMapping("/imgqrcode")
    public void getQrcode(@RequestParam String secret,
                          @RequestParam String username,
                          HttpServletResponse response) throws Exception {

        byte[] qrcodeChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        try {
            String qrBarcode = GoogleAuthenticator.getQRBarcode(username, secret);
            QrcodeUtils.gen(qrBarcode,jpegOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        qrcodeChallengeAsJpeg = jpegOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(qrcodeChallengeAsJpeg);
        servletOutputStream.flush();
        servletOutputStream.close();
    }


    /**
     * @Description: 获取秘钥
     * @Date: Create in 下午1:03 2018/3/5
    */

    @RequestMapping("/getsecretkey/{id}")
    public RestResp getSecretKey(@PathVariable("id") Long id){
        return googleSecretService.getSecretKey(id);
    }

    /**
     * @Description: 验证输入验证码
     * @Date: Create in 下午1:03 2018/3/5
    */

    @PostMapping("/authgooglecode/{id}")
    public RestResp authGoogleCode(@RequestParam String code,
                                   @PathVariable("id") Long u_id){
        return googleSecretService.authGoogleCode(code,u_id);
    }

    @PostMapping("/authgooglecode}")
    public RestResp authGoogleCode(@RequestParam String email,@RequestParam String code){
        return googleSecretService.authGoogleCode(code,email);
    }

    @RequestMapping(value = "/google/bind")
    public RestResp bindGoogleAuth(String email){
        return googleSecretService.bindGoogleAuth(email);
    }

}
