package com.cjsz.tech.system.utils;

import com.cjsz.tech.system.beans.AppDevice;
import com.cjsz.tech.system.service.LogService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志记录工具类
 * @author macsah
 *
 */
public class AccessLogUtil {

	/**
	 * 访问记录日志
	 * @param request
	 * @param devobj
	 * @param biztype
	 * @param bizid
	 * @param act
	 */
	public static void record(HttpServletRequest request,LogService logService,AppDevice devobj,Integer biztype,Long bizid,Integer act) {
		String ip = IPUtil.getRemoteHost(request);
		//IP特殊处理--多个IP(例如:222.42.239.4,123.151.64.142)
		if(!StringUtils.isEmpty(ip)) {
			if(ip.contains(",")) {
				ip = ip.substring(ip.lastIndexOf(",")+1).trim();
			}
		}
		String url = request.getRequestURL().toString();
		logService.record(devobj.getOrg_id(), devobj.getId(), devobj.getUid(), biztype, bizid, act, url, ip);
	}
}
