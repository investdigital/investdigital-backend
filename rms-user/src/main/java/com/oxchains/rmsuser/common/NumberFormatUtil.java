package com.oxchains.rmsuser.common;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ccl
 * @time 2017-12-14 10:54
 * @name NumberFormat
 * @desc:
 */
@Slf4j
public class NumberFormatUtil {
    public static Float stringToFloat(String str){
        if(null == str || "".equals(str.trim())){
            return null;
        }
        try{
            return Float.valueOf(str);
        }catch (Exception e){
            return null;
        }

    }
    public static List<Long> stringSplit2Long(String str, String regex){
        if(null == str || "".equals(str.trim()) ||
                null == regex || "".equals(regex.trim())){
            return null;
        }
        String[] ls = str.split(regex);
        try{
            if(null == ls){
                return null;
            }
            List<Long> ll = new ArrayList<>(ls.length);
            for(String s : ls){
                ll.add(Long.valueOf(s));
            }
            return ll;
        }catch (Exception e){
            log.error("数据类型转换异常",e);
            return null;
        }
    }
}
