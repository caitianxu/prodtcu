package com.cjsz.tech.beans;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
public class PageConditionBean {

    private Integer pageNum;

    private Integer pageSize;

    private String searchText;

    public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
