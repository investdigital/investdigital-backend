package info.investdigital.rest;

import info.investdigital.common.RestResp;
import info.investdigital.entity.*;
import info.investdigital.service.FundService;
import org.springframework.validation.BindingResult;
import info.investdigital.entity.FundCommentVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigInteger;
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
    //issue fund
    @PostMapping(value = "/issueFund")
    public RestResp issueFund(@Valid @RequestBody FundDetail fundDetail, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return RestResp.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return fundService.issuefund(fundDetail);
    }
    //Review the fund
    @GetMapping(value = "/reviewFund/{fundCode}/{status}")
    public RestResp ReviewFund(@PathVariable BigInteger fundCode,@PathVariable Integer status) throws Exception {
        return fundService.reviewFund(fundCode,status);
    }
    //subscrip the fund
    @PostMapping(value = "/subscripFund")
    public RestResp subscripFund(@RequestBody SubscribeInfo subscribeInfo){
        return fundService.subFund(subscribeInfo);
    }
    @PostMapping(value = "/transferFundShare")
    public RestResp transferFundShare(@RequestBody TransferInfo transferInfo){
        return fundService.transferFundShare(transferInfo);
    }
    @GetMapping(value = "/findAllFund")
    public RestResp findAllFund(Integer pageSize, Integer pageNum){
        return fundService.findAllFund(pageSize,pageNum);
    }

    @GetMapping(value = "/fundInfo")
    public RestResp fundInfo(BigInteger fundCode){
        return fundService.getFundInfos(fundCode);
    }

    @GetMapping(value = "/addtest")
    public RestResp addtest(){
        fundService.addtest();
        return RestResp.success();
    }
    @GetMapping(value = "/findStarFund")
    public RestResp findStarFund(){
        return fundService.findStarFund();
    }

    @GetMapping(value = "/comment")
    public RestResp fundComment(Long fundId,Integer pageSize,Integer pageNum){
        return fundService.fundComment(fundId,pageSize,pageNum);
    }
    @PostMapping(value = "/comment")
    public RestResp fundComment(@RequestBody FundComment comment){
        return fundService.addComment(comment);
    }

    @RequestMapping(value = "/image")
    public RestResp images(@ModelAttribute FundCommentVO vo) throws Exception{
        return fundService.images(vo);
    }

}
