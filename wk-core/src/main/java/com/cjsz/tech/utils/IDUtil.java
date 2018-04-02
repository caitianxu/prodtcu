package com.cjsz.tech.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 产生唯一ID
 * Created by shiaihua on 16/11/30.
 */
public class IDUtil {

    private static SimpleDateFormat F = new SimpleDateFormat("yyMMddhhmmss");
    private static Random random = new Random();

    /**
     * 生成唯一编码
     * @return
     */
    public static String createId() {
        Date date = new Date();
        String dateSub = F.format(date);
        int r = random.nextInt(10000);
        r = r+r ^3;
        String randomSub = String.format("%03d", r);
        int rlen = randomSub.length();
        return dateSub+randomSub.substring(rlen-3, rlen);
    }
}
