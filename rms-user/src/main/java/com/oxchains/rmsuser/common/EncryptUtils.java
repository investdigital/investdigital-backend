package com.oxchains.rmsuser.common;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author ccl
 * @time 2017-10-12 15:49
 * @name EncryptUtil
 * @desc: 加密工具类
 */
public class EncryptUtils {
    public static String encodeMD5(String str){
        return encrypt(str,"MD5");
    }
    public static String encodeSHA1(String str){
        return encrypt(str,"SHA-1");
    }
    public static String encodeSHA256(String str){
        return encrypt(str,"SHA-256");
    }
    public static String encodeBase64(String str){
        BASE64Encoder encoder=new BASE64Encoder();
        return encoder.encode(str.getBytes());
    }
    public static String decodeBase64(String str){
        BASE64Decoder decoder=new BASE64Decoder();
        try {
            return new String(decoder.decodeBuffer(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encrypt(String src,String type){
        if(src==null){
            return null;
        }
        MessageDigest md=null;
        String result=null;
        byte[] b=src.getBytes();
        try {
            md=MessageDigest.getInstance(type);
            md.update(b);

            result=new BigInteger(1,md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
    private EncryptUtils(){}


}
