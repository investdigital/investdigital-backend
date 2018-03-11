package info.investdigital.rest;
import info.investdigital.common.RestResp;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ccl
 * @time 2018-03-07 10:17
 * @name InfoController
 * @desc:
 */
@RestController
public class InfoController {

    @RequestMapping(value = "/")
    public RestResp info(){
        return RestResp.success("Welcome to visit InvestDigital!",null);
    }
}
