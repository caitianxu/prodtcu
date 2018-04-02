package com.cjsz.tech.system.filter;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.Impl.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebFilter(filterName = "adminCacheFilter", urlPatterns = "/admin/*", initParams = {@WebInitParam(name = "exeludes", value = "/admin,/admin/login")})
public class AdminCacheFilter implements Filter {

    private String exeludes;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException {

        String ctxPath = req.getServletContext().getContextPath();
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();
        System.out.println("init param a=" + exeludes);
        System.out.println(request.getMethod());
        System.out.println(request.getRequestURI().replace(ctxPath, ""));
        if (exeludes.contains(request.getRequestURI().replace(ctxPath,"")) || request.getMethod().equalsIgnoreCase("get")) {
            try {
                chain.doFilter(req, res);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String token = request.getHeader("token");
            String session_token = (String)(session.getAttribute("token"));
            if (!checkToken(token, session_token, request)) {
                Writer writer = null;
                try {
                    response.setContentType("application/json;charset=utf-8");
                    writer = response.getWriter();

                    Map<String,Object> data = new HashMap<String,Object>();
                    data.put("code",600);
                    data.put("data",new ArrayList<>());
                    data.put("message", "token失效!");
                    JSONObject result = new JSONObject(data);

                    writer.append(result.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    chain.doFilter(req, res);
                    return;
                } catch (Exception e) {
                     e.printStackTrace();
                }
            }
        }

    }

    private boolean checkToken(String token, String session_token, HttpServletRequest request) {
        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(token) || !token.equals(session_token)){
            return false;
        }
        UserServiceImpl userService = (UserServiceImpl) SpringContextUtil.getBean("userServiceImpl");

        SysUser user = userService.findByToken(token);
        if(user != null){
            return true;
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        exeludes = filterConfig.getInitParameter("exeludes");
    }

    @Override
    public void destroy() {
    }

}