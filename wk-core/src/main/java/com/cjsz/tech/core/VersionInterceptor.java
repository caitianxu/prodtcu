package com.cjsz.tech.core;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yqx on 15/11/16.
 */
public class VersionInterceptor implements AsyncWebRequestInterceptor {

    private String profile;

    private String version;

    public VersionInterceptor(String profile) {
        this.profile = profile;
    }

    public VersionInterceptor(String profile, String appVersion) {
        this.profile = profile;
        this.version = appVersion;
    }

    @Override
    public void afterConcurrentHandlingStarted(WebRequest request) {

    }

    @Override
    public void preHandle(WebRequest request) throws IOException {
        HttpServletRequest myrequest = (HttpServletRequest) ((DispatcherServletWebRequest) request).getRequest();
        String ctxPath = "";
        if (request.getContextPath() != null) {
            ctxPath = request.getContextPath();
        }
        myrequest.setAttribute("ctxPath", ctxPath);
        String[] web_path = myrequest.getRequestURI().split("/");
        List<String> webPaths = Arrays.asList(web_path);
        //只允许前台页面进入
        boolean flag = webPaths.contains("static") || webPaths.contains("maxApp") ||
                myrequest.getRequestURI().indexOf("/web/404") != -1 || webPaths.contains("err") ||
                webPaths.contains("error");
        if (flag) {
            System.out.println("server:" + myrequest.getRequestURI());
            //静态文件 + 过滤页面
        } else if (myrequest.getRequestURI().indexOf("/admin") != -1) {
            //后台接口
        } else if (myrequest.getRequestURI().indexOf("/api") != -1 || myrequest.getRequestURI().indexOf("/site") != -1) {
            //移动api + 网站api
        } else {
            //网站 web site
            Object org_id = myrequest.getAttribute("org_id");
            JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContextUtil.getBean("jdbcTemplate");
            System.out.println("send_start:" + myrequest.getRequestURI());

            //没有机构通过二级域名重新解析机构
            if (org_id == null) {
                String serverName = myrequest.getServerName();
                System.out.println(serverName);
                if (serverName.indexOf(".") != -1) {
                    if (serverName.indexOf("www") != -1) {
                        serverName = serverName.split("\\.")[1];
                    } else {
                        serverName = serverName.split("\\.")[0];
                    }
                    if (serverName.equals("cjszyun") || serverName.equals("192") || serverName.equals("localhost") || serverName.equals("www")) {
                        serverName = "cjzww";
                    }
                } else {
                    serverName = "cjzww";
                }
                myrequest.setAttribute("serverName", serverName);
                System.out.println("serverName:" + serverName);

                String sql = "select * from pro_org_extend where server_name ='" + serverName + "' and enabled = 1";
                Map<String, Object> map = null;
                try {
                    map = jdbcTemplate.queryForMap(sql);
                    org_id = map.get("org_id");
                    myrequest.setAttribute("org_id", org_id);
                    myrequest.setAttribute("org_map", map);
                } catch (Exception e) {
                    myrequest.removeAttribute("org_id");
                    myrequest.removeAttribute("org_map");
                }
            }
            HttpSession session = myrequest.getSession(true);
            if (org_id == null) {
                myrequest.setAttribute("error", 404);
                session.removeAttribute("member_info");
                session.removeAttribute("member_token");
            } else {
                //用户信息验证
                Map<String, Object> memberInfo = JSONObject.parseObject(JSONObject.toJSONString(session.getAttribute("member_info")));
                Map<String, Object> newMap = null;
                Object member_token = null;
                if (memberInfo != null) {
                    member_token = memberInfo.get("token");
                    //判断当前token是否跟数据库相同
                    String sql = "SELECT * from tb_member where  token_type = '" + memberInfo.get("token_type") + "' and token = '" + member_token + "'";
                    try {
                        newMap = jdbcTemplate.queryForMap(sql);
                    } catch (Exception e) {
                        System.out.println("error:" + e.getMessage());
                    }
                }
                if (newMap != null) {
                    session.setAttribute("member_token", member_token);
                } else {
                    session.removeAttribute("member_info");
                    session.removeAttribute("member_token");
                }
            }
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        HttpServletRequest myrequest = (HttpServletRequest) ((DispatcherServletWebRequest) request).getRequest();
        if (model != null) {
            String[] web_path = myrequest.getRequestURI().split("/");
            List<String> webPaths = Arrays.asList(web_path);
            //只允许前台页面进入
            boolean flag = webPaths.contains("static") || webPaths.contains("maxApp") ||
                    myrequest.getRequestURI().indexOf("/web/404") != -1 || webPaths.contains("err") ||
                    webPaths.contains("error");
            if (flag) {
                //静态文件 + 过滤页面
            } else if (myrequest.getRequestURI().indexOf("/admin") != -1) {
                //后台接口
                version = String.valueOf(System.currentTimeMillis());
                model.put("v", version);
                String lang = (String) request.getAttribute("_lang", 1);
                if (StringUtils.isNotBlank(lang)) {
                    model.put("lang", lang);
                } else {
                    model.put("lang", "zh_cn");
                }
            } else if (myrequest.getRequestURI().indexOf("/api") != -1 || myrequest.getRequestURI().indexOf("/site") != -1) {
                //移动接口
            } else {
                System.out.println("send_end_model:" + myrequest.getRequestURI());
                //web site
                Map<String, Object> map = (Map<String, Object>) myrequest.getAttribute("org_map");
                if (null == map) {
                    myrequest.setAttribute("error", 404);
                } else {
                    model.put("pro_code", map.get("pro_code"));
                    model.put("org_id", map.get("org_id"));
                    model.put("org_logo", map.get("org_logo"));
                    model.put("org_weixin", map.get("org_weixin"));

                    version = String.valueOf(System.currentTimeMillis());
                    model.put("v", version);

                    String lang = (String) request.getAttribute("_lang", 1);
                    if (StringUtils.isNotBlank(lang)) {
                        model.put("lang", lang);
                    } else {
                        model.put("lang", "zh_cn");
                    }

                    HttpSession session = myrequest.getSession(true);
                    Map<String, Object> memberInfo = JSONObject.parseObject(JSONObject.toJSONString(session.getAttribute("member_info")));
                    Object member_token = session.getAttribute("member_token");

                    //判断是否有登录用户信息
                    if (memberInfo != null && member_token != null) {
                        model.put("memberInfo", JSONObject.toJSONString(memberInfo));
                        model.put("member_token", member_token);
                        model.put("member", memberInfo);
                    } else {
                        model.remove("memberInfo");
                        model.remove("member");
                        model.put("member_token", "");
                        Object server_name = myrequest.getAttribute("serverName");
                        if (server_name != null && map.get("is_registe").toString().equals("1")) {
                            if (myrequest.getRequestURI().indexOf("/login") == -1 && myrequest.getRequestURI().indexOf("/forget") == -1) {
                                if (myrequest.getRequestURI().indexOf("/mobile") != -1) {
                                    ((DispatcherServletWebRequest) request).getResponse().sendRedirect(myrequest.getAttribute("ctxPath") + "/mobile/login");
                                } else {
                                    ((DispatcherServletWebRequest) request).getResponse().sendRedirect(myrequest.getAttribute("ctxPath") + "/web/login");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
