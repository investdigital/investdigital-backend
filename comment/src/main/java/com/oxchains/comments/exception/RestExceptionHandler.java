package com.oxchains.comments.exception;

import com.oxchains.comments.common.RestResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author ccl
 * @time 2018-01-23 10:29
 * @name RestExceptionHandler
 * @desc: 异常统一处理
 */

@ControllerAdvice(annotations = RestController.class)
@ResponseBody
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler
    @ResponseStatus
    public RestResp runtimeExceptionHandler(Exception e){
        log.error("统一异常处理: ", e.getMessage(), e);
        return RestResp.fail("服务器繁忙,请稍后再试!",null);
    }
}
