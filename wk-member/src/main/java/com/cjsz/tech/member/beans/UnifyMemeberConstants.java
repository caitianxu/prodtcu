package com.cjsz.tech.member.beans;

import com.cjsz.tech.utils.security.Md5Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Author:Jason
 * Date:2017/6/26
 */
public class UnifyMemeberConstants {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final String API_KEY = "10000001";
    public static final String API_SECRET = "W57HBHGSD2j6HuQ8";
    public static final String API_BASE_URL = "http://www.cjzww.com/interface/openapi/";
    public static final String CJZWW_ORG_CODE = "CJZWW";

    public static final String API_KEY_DZ = "10000003";
    public static final String API_SECRET_DZ = "VaoUJqCDx9kuPboUpUIJ";

    public static final class Module {
        public static final String USER = "User";
        public static final String PAY = "PayApi";
        public static final String BOOK = "Book";

        public static final class Action{
            public static final String REG_USER = "regUser";
            public static final String USER_LOGIN = "login";
            public static final String CHANGE_PWD = "changePassword";
            public static final String LOAD_USER_INFO = "userInfo";
            public static final String UPDATE_USER_INFO = "updateUserInfo";
            public static final String BIND_MOBI = "bindMobi";
            public static final String BIND_EMAIL = "bindEmail";
            public static final String GORGET_PWD = "resetPassword";
            public static final String ALIPAY = "aliPay";
            public static final String WEIXIN_PAY = "weixinPay";
            public static final String USER_ACCOUNT = "userAccount";

            public static final String INFO = "info";
            public static final String LISTS = "lists";
            public static final String CHAPTERLIST = "chapterList";
            public static final String CHAPTER = "chapter";
            public static final String GETCHAPTERSBUYINFO = "getChaptersBuyInfo";
            public static final String BUYCHAPTERS = "buyChapters";
            public static final String GETCATS = "getCats";
            public static final String RECOMMEND = "recommend";
            public static final String CHANGEACCOUNT = "changeAccount";
            public static final String HANDRANK = "handRank";
            public static final String AUTORANK = "autoRank";
            public static final String CHAPTERCOUNT = "chapterCount";
            public static final String DOLAREE = "doLaress";

            public static final String OTHER_LOGIN = "auth";
        }
    }

    /**
     * 获取API签名
     *
     * @param con       控制器
     * @param act       动作
     * @param timestamp 时间戳，秒级
     * @return
     */
    public static final String getSign(String con, String act, String timestamp) {
        return Md5Utils.hash(API_KEY + API_SECRET + con + act + timestamp);
    }

    /**
     * 获取新API签名
     *
     * @param con       控制器
     * @param act       动作
     * @param timestamp 时间戳，秒级
     * @return
     */
    public static final String getNewSign(String key,String secret,String con, String act, String timestamp) {
        return Md5Utils.hash(key + secret + con + act + timestamp);
    }



    /**
     * 获取相关接口地址
     *
     * @param con
     * @param act
     * @return
     */
    public static final String getInvokeApiUrl(String con, String act) {
        String timestamp = sdf.format(Calendar.getInstance().getTime());
        String sign = getSign(con, act, timestamp);
        return API_BASE_URL + "?con=" + con + "&act=" + act + "&client=" + API_KEY + "&sign=" + sign + "&timestamp=" + timestamp;
    }

    /**
     * 获取新相关接口地址
     *
     * @param con
     * @param act
     * @return
     */
    public static final String getNewInvokeApiUrl(String key,String secret, String con, String act) {
        String timestamp = sdf.format(Calendar.getInstance().getTime());
        String sign = getNewSign(key ,secret, con, act, timestamp);
        return API_BASE_URL + "?con=" + con + "&act=" + act + "&client=" + key + "&sign=" + sign + "&timestamp=" + timestamp;
    }


    public static final class TipInfo {
        public static final String REQUEST_FAILURE ="请求失败!";
        public static final String REGISTE_OK ="注册成功!";
        public static final String REGISTE_FAILURE ="注册失败!";
        public static final String LOGIN_OK ="登陆成功!";
        public static final String LOAD_OK ="加载成功!";
        public static final String CHANGE_PWD_OK ="密码修改成功!";
        public static final String CHANGE_OK ="修改成功!";
        public static final String CHANGE_FAILURE="修改失败!";

    }

}
