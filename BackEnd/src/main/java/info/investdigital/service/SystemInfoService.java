package info.investdigital.service;

import info.investdigital.common.I18NConst;
import info.investdigital.common.MyMessageSource;
import info.investdigital.common.RestResp;
import info.investdigital.dao.SystemInfoRepo;
import info.investdigital.entity.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ccl
 * @time 2018-03-29 15:46
 * @name SystemInfoService
 * @desc:
 */
@Slf4j
@Service
public class SystemInfoService {
    @Resource
    private SystemInfoRepo systemInfoRepo;
    
     @Resource
    private MyMessageSource myMessageSource;

    public RestResp info(){
        try{
            return RestResp.success(myMessageSource.getMessage(I18NConst.INFO_WELCOME_MESSAGE),null);
        }catch (Exception e){
            log.error("Exception", e);
            return RestResp.fail(myMessageSource.getMessage(I18NConst.FAILURE));
        }
    }

    public RestResp getSystemInfo(){
        try{
            SystemInfo systemInfo = systemInfoRepo.findOne(1L);
            return RestResp.success(myMessageSource.getMessage(I18NConst.SUCCESS),systemInfo);
        }catch (Exception e){
            log.error("Exception", e);
            return RestResp.fail(myMessageSource.getMessage(I18NConst.FAILURE));
        }
    }

    @Scheduled(cron = "0 0 */6 * * ?")
    public void updateInfo(){
        try{
            SystemInfo systemInfo = systemInfoRepo.findOne(1L);
            if(null != systemInfo){
                systemInfo.setVersion(systemInfo.getVersion() + 1);
                systemInfo.setUpdateTime(new Date());
            }else {
                systemInfo = createSystemInfo();
            }
            systemInfoRepo.save(systemInfo);
        }catch (Exception e){
            log.error("定时任务执行失败",e);
        }
    }

    private SystemInfo  createSystemInfo(){
        SystemInfo systemInfo = new SystemInfo();

        systemInfo.setId(1L);
        systemInfo.setOrganization("北京云湾科技有限公司");
        systemInfo.setTeam("InvestDigital Foundation");
        systemInfo.setCopyright("© ID");
        systemInfo.setVersion(1);
        systemInfo.setCreateTime(new Date());
        systemInfo.setUpdateTime(new Date());
        systemInfo.setWebsite("http://investdigital.info/");
        systemInfo.setAuthor("ccl");
        systemInfo.setDescription("InvestDigital is the management protocol and toolset for cryptocurrencies,and it is implemented on EOS block chain.Our vision is to build the completed ecosystem form content/tool production to investment strategy and then to financial products for cryptocurrency investment");

        return systemInfo;
    }
}
