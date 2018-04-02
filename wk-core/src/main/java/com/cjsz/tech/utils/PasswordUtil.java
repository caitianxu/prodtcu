package com.cjsz.tech.utils;

import java.io.IOException;
import java.util.Random;

/**
 * Created by yunke on 16/3/3.
 */
public class PasswordUtil {

    public static final String SLAT = "cjclound";

    /**
     * 加密
     *
     * @param orignPwd
     */
    public static String entryptPassword(String orignPwd) {
        byte[] hashPassword;
        String pwd = "";
        try {
            hashPassword = Digests.md5(orignPwd.getBytes(), SLAT.getBytes(), 1024);
            pwd = Encodes.encodeHex(hashPassword);
        } catch (IOException e) {
           throw new RuntimeException(e.getMessage());
        }
        return pwd;
    }

    /**
     * 图书id加密
     *
     * @param orignPwd
     */
    public static String bookMD5Key(String orignPwd) {
        byte[] hashPassword;
        String pwd = "";
        try {
            hashPassword = Digests.md5(orignPwd.getBytes());
            pwd = Encodes.encodeHex(hashPassword);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return pwd;
    }
    
    /**
     * 生成随机密码
     *
     * @return
     */
    public static String getRandomNum(){
        //35是因为数组是从0开始的，26个字母+10个数字
        final int  maxNum = 36;
        int i;  //生成的随机数
        int count = 0; //生成的密码的长度
        int pwd_len = 6;//设置的密码的长度
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < pwd_len){
            //生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(maxNum));  //生成的数最大为36-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }
}
