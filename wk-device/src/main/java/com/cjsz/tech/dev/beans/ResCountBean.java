package com.cjsz.tech.dev.beans;

import java.io.Serializable;

/**
 * Created by shiaihua on 16/12/24.
 */
public class ResCountBean implements Serializable{

    private String org_name;
    
    private String resource_name;
    
    private Integer operation_type;
    
    private Long num;

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getResource_name() {
		return resource_name;
	}

	public void setResource_name(String resource_name) {
		this.resource_name = resource_name;
	}

	public Integer getOperation_type() {
		return operation_type;
	}

	public void setOperation_type(Integer operation_type) {
		this.operation_type = operation_type;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}
}
