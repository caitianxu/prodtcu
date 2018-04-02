package com.cjsz.tech.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Bruce on 2016/10/28.
 */
public class CookieUtil {

    private static final int expireTime = 24 * 60 * 60;

    /**
     * 添加Cookie
     * @param resp
     * @param name
     * @param value
     */
    public static void addCookie(HttpServletResponse resp, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        if(expireTime > 0) {
            cookie.setMaxAge(expireTime);
        }
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public static void removeCookie(HttpServletRequest req, HttpServletResponse resp, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
                    System.out.println("被删除的cookie名字为:"+cookie.getName());
                    resp.addCookie(cookie);
                }
            }
        }
    }
    public static String getValue(HttpServletRequest req, String name) {
        String value = null;
        Cookie[] cookies = req.getCookies();
        if(null != cookies) {
            for(Cookie c : cookies) {
                String key = c.getName();
                if(key.equals(name)) {
                    value = c.getValue();
                    break;
                }
            }
        }
        return value;
    }

    public static Cookie getCookie(HttpServletRequest req, String name) {
        Cookie cookie = null;
        Cookie[] cookies = req.getCookies();
        if(null != cookies) {
            for(Cookie c : cookies) {
                String key = c.getName();
                if(key.equals(name)) {
                  cookie = c;
                }
            }
        }
        return cookie;
    }
}
