package com.xyh.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * @author xyh
 * @date 2021/11/1 15:40
 */
@Component
public class MD5Util {
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }
    public static final String salt="1a2b3c4d";

    public static String inputPassToFromPass(String inputPass){
        String str=""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String formPass,String salt){
        String str=""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    public static String inputPassToDBPass(String inputPas,String salt){
        String formPass = inputPassToFromPass(inputPas);
        String dbPass = fromPassToDBPass(formPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        //ce21b747de5af71ab5c2e20ff0a60eea
        //数据库 b7797cce01b4b131b433b6acf4add449
        System.out.println(inputPassToFromPass("123456"));
        System.out.println(fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
