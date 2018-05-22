package info.investdigital.service;

import info.investdigital.AssetsAPI.huobi.entity.depositwithdraw.DepositWithdrawData;
import info.investdigital.common.DateUtil;
import info.investdigital.common.JsonUtil;
import info.investdigital.common.ResourceParam;
import info.investdigital.dao.fund.FundAccountInfoRepo;
import info.investdigital.entity.fund.FundAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @Author: huohuo
 * Created in 18:40  2018/4/18.
 */
@Service
public class FundEncryptionService {
}
