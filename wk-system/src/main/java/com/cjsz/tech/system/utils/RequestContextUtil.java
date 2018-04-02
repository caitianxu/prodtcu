package com.cjsz.tech.system.utils;



import com.cjsz.tech.system.domain.SysUser;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yunke on 16/2/29.
 */
@Configuration
public class RequestContextUtil {

    /**
     * 获取用户
     * @param request
     * @return
     */
    public static SysUser getUser(HttpServletRequest request) {
            return (SysUser)request.getSession().getAttribute("user");
    }

    /**
     * 获取用户
     * @param request
     * @return
     */
    public static void removeUser(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
    }

    public static void setUser(HttpServletRequest request,SysUser user) {
        request.getSession().setAttribute("user",user);
    }




    /**
     * 获取前台用户
     * @param request
     * @return
     */
//    public static EduUser getForeUser(HttpServletRequest request) {
//        return (EduUser)request.getSession().getAttribute("fuser");
//    }

    /**
     * 获取前台用户
     * @param request
     * @return
     */
//    public static void removeForeUser(HttpServletRequest request) {
//        request.getSession().removeAttribute("fuser");
//    }

    /**
     * 设置前台用户
     * @param request
     * @param user
     */
//    public static void setForeUser(HttpServletRequest request,EduUser user) {
//        request.getSession().setAttribute("fuser",user);
//    }

}

