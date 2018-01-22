package com.oxchains.rmsuser.common;

/**
 * @author ccl
 * @time 2017-12-14 10:54
 * @name NumberFormat
 * @desc:
 */
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
}
