package info.investdigital.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ccl
 * @time 2017-12-05 10:08
 * @name RegexUtils
 * @desc:
 */
public class RegexUtils {
    //国内电话
    public static final String REGEX_PHONE="^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$";
    public static final String REGEX_MOBILEPHONE="^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0-3,5-9]))\\d{8}$";
    public static final String REGEX_EMAIL="^[0-9a-z_]+@(([0-9a-z]+)[.]){1,2}[a-z]{2,3}$";//[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\.){1,3}[a-zA-z\-]{1,}
    public static final String REGEX_ZH="^[\\u0391-\\uFFE5]+$";
    public static final String REGEX_URL="^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$ ";
    public static final String REGEX_NAME = "^[a-zA-Z][a-zA-Z_0-9]*$";
    public static final String REGEX_NAME_LEN32 = "^[a-zA-Z][a-zA-Z_0-9]{4,31}$";
    public static final String REGEX_BTC_ADDRESS = "^[a-zA-Z0-9]${25,33}";

    public static boolean match(String str ,String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    private RegexUtils(){}
}
