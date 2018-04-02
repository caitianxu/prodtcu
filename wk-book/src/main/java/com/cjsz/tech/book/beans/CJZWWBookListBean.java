package com.cjsz.tech.book.beans;

import com.cjsz.tech.book.domain.CJZWWBooks;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7 0007.
 */
public class CJZWWBookListBean {

    private List<CJZWWBooks> list;

    private Integer code;

    private Integer total;

    private Integer page;

    public List<CJZWWBooks> getList() {
        return list;
    }

    public void setList(List<CJZWWBooks> list) {
        this.list = list;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
