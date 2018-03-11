package info.investdigital.rest;

import info.investdigital.common.RestResp;
import info.investdigital.service.UploadService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ccl
 * @time 2018-02-06 13:36
 * @name UploadController
 * @desc:
 */
@RestController
@RequestMapping(value = "/upload")
public class UploadController {
    @Resource
    private UploadService uploadService;

    @RequestMapping(value = "/image/{userId}")
    public RestResp uploadImage(@PathVariable Long userId, HttpServletRequest request){
        return uploadService.uploadImage(request,userId);
    }
}
