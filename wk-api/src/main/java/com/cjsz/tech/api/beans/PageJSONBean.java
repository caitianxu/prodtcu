package com.cjsz.tech.api.beans;

import com.cjsz.tech.book.beans.AppBooksBean;
import com.cjsz.tech.member.beans.UnifyMemeberConstants;

import java.util.List;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class PageJSONBean {
    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;
    private List<AppBooksBean> rows;
    private Integer size;
    private Integer total;

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

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<AppBooksBean> getRows() {
        return rows;
    }

    public void setRows(List<AppBooksBean> rows) {
        this.rows = rows;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
