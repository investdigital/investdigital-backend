package info.investdigital.service;

import com.oxchains.basicService.files.tfsService.TFSConsumer;
import info.investdigital.common.RestResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author ccl
 * @time 2018-02-06 13:39
 * @name UploadService
 * @desc:
 */
@Slf4j
@Service
public class UploadService {
    @Resource
    TFSConsumer tfsConsumer;

    public RestResp uploadImage(HttpServletRequest request, Long userId){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)){
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Set<String> newNames = new HashSet<>();
            for(Iterator<String> iterator = multiRequest.getFileNames();iterator.hasNext();){
                String key = iterator.next();
                MultipartFile multipartFile = multiRequest.getFile(key);
                if(null != multipartFile){
                    String fileName = multipartFile.getOriginalFilename();
                    String newFileName = tfsConsumer.saveTfsFile(multipartFile,userId);
                    if (null == newFileName) {
                        //return RestResp.fail("图片上传失败");
                    }else {
                        newNames.add(newFileName);
                    }
                }
            }
            return RestResp.success("图片上传成功",newNames);
        }
        return RestResp.fail();
    }
}
