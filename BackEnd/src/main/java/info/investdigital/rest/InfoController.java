package info.investdigital.rest;
import info.investdigital.common.RestResp;
import info.investdigital.service.SystemInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ccl
 * @time 2018-03-07 10:17
 * @name InfoController
 * @desc:
 */
@RestController
public class InfoController {

    @Resource
    private SystemInfoService systemInfoService;

    @GetMapping(value = "/")
    public RestResp info(){
        return systemInfoService.info();
    }

    @GetMapping(value = "/infos")
    public RestResp sysInfo(){
        return systemInfoService.getSystemInfo();
    }
}
