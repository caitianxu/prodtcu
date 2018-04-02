package com.cjsz.tech.dev.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shiaihua on 16/12/24.
 */
public class OrgCountBean implements Serializable{

    private String year;
    
    private List<ResCountBean> orgCount;
    
    private List<ResCountBean> bookCount;
    
    private List<ResCountBean> newsCount;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public List<ResCountBean> getOrgCount() {
		return orgCount;
	}

	public void setOrgCount(List<ResCountBean> orgCount) {
		this.orgCount = orgCount;
	}

	public List<ResCountBean> getBookCount() {
		return bookCount;
	}

	public void setBookCount(List<ResCountBean> bookCount) {
		this.bookCount = bookCount;
	}

	public List<ResCountBean> getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(List<ResCountBean> newsCount) {
		this.newsCount = newsCount;
	}
}
