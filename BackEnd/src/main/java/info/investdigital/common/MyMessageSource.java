package info.investdigital.common;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @Author: Gaoyp
 * @Description: 优化原生的MessageSource
 * @Date: Create in 下午3:44 2018/3/22
 * @Modified By:
 */
@Component
public class MyMessageSource {

    @Resource
    private MessageSource messageSource;
    /**
     *
     * @param code 需要国际化的字符串
     * @return 国际化后对应语言的字符串
     */
    public String getMessage(String code){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code,null,locale);
    }

}
