package com.oxchains.rmsuser.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luoxuri
 * @create 2018-01-10 17:04
 **/
public class IndexUtils {

    /**
     * 方法不能作为公共工具类，为特定方法写的工具类,是指定位置截取的
     */
    public static int getIndex(String str, String reg){
        int c = 0, index = -1;
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        while (m.find()){
            c++;
            index = m.start();
            if (c == 4){
                return index;
            }
        }
        return 0;
    }
}
