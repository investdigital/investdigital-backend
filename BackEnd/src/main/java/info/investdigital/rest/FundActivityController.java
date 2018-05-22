package info.investdigital.rest;

import info.investdigital.common.ParamType;
import info.investdigital.common.RestResp;
import info.investdigital.service.FundService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: huohuo
 * Created in 17:54  2018/4/10.
 */
@RestController
@RequestMapping("/fund/activity")
public class FundActivityController {
    @Resource
    private FundService fundService;
    //获取交易大赛  基金数据
    @GetMapping(value = "/contest")
    public RestResp tradingCcontest(@RequestParam Integer sort){
        if(!check(sort)){
            return RestResp.fail("Parameters of illegal");
        }
        return fundService.tradingCcontest(sort);
    }
    private boolean check(Integer sort){
        sort = sort == null ? 1 : sort;
        if(ParamType.FundSortRule.contains(sort)){
            return true;
        }
        return false;
    }
    @GetMapping(value = "/{fundCode}")
    public RestResp fundInfo(@PathVariable Long fundCode) {
        return fundService.getFundInfosTrading(fundCode);
    }
    @GetMapping(value = "/earing/{fundCode}")
    public RestResp earing(@PathVariable Long fundCode){
       return fundService.getParticipatingFundEchart(fundCode);
    }
}
