package info.investdigital.rest;

import info.investdigital.common.RestResp;
import info.investdigital.service.FundService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ccl
 * @time 2017-12-13 15:10
 * @name FundController
 * @desc:
 */
@RestController
@RequestMapping(value = "/fund")
public class FundController {
    @Resource
    private FundService fundService;

    @GetMapping(value = "/all")
    public RestResp findAllFund(@RequestParam Integer sort, @RequestParam Integer pageSize, @RequestParam Integer pageNum) {
        if(!check(sort,pageSize,pageNum)){
            return RestResp.fail("Parameters of illegal");
        }
        return fundService.listFund(sort,pageSize, pageNum);
    }
    @GetMapping(value = "/earing/{fundCode}")
    public RestResp earing(@PathVariable Long fundCode){
        return fundService.getFundEchart(fundCode);
    }

    @GetMapping(value = "/{fundCode}")
    public RestResp fundInfo(@PathVariable Long fundCode) {
        return fundService.getFundInfos(fundCode);
    }

    @GetMapping(value = "/star")
    public RestResp listStarFund() {
        return fundService.listStarFund();
    }

    private boolean check(Integer sort,Integer pageSize,Integer pageNum){
        sort = sort == null ? 1 : sort;
        pageSize = pageSize == null ? 1 : pageSize;
        pageNum = pageNum == null ? 8 : pageNum;
        if(sort > 4 || sort < 1){
            return false;
        }
        return true;
    }
}
