package com.cjsz.tech.system.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class DelUserBean implements Serializable {
	
	private String[]  ids;
	private String  token;
	
	public String[] getIds() {
		return ids;
	}
	public void setIds(String[] ids) {
		this.ids = ids;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
