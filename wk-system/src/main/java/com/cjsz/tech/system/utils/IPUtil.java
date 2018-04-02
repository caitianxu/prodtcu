package com.cjsz.tech.system.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类
 * @author macsah
 *
 */
public class IPUtil {

	/**
	 * 获取用户IP
	 * @param request
	 * @return
	 */
	public static String getRemoteHost(HttpServletRequest request){
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getRemoteAddr();
	    }
	    return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}
	
	/**
	 * 服务器路径
	 * @param request
	 * @return
	 */
	public static String getAccessPath(HttpServletRequest request) {
//	String requestUrl = request.getScheme() //当前链接使用的协议
//		    +"://" + request.getServerName()//服务器地址 
//		    + ":" + request.getServerPort() //端口号 
//		    + request.getContextPath() //应用名称，如果应用名称为
//		    + request.getServletPath() //请求的相对url 
//		    + "?" + request.getQueryString(); //请求参数
		String requestUrl = request.getScheme() //当前链接使用的协议
			    +"://" + request.getServerName()//服务器地址 
			    + ":" + request.getServerPort() //端口号 
			    + request.getContextPath(); //应用名称，如果应用名称为
		return requestUrl;
	}
}
