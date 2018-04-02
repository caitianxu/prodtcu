package com.cjsz.tech.book.beans;

import java.util.List;

/**
 * 图书列表多选--机构的图书
 * 图书列表应为分配到机构的图书关系
 * Created by Administrator on 2016/12/23 0023.
 */
public class SelectBookBean {

    private List<BookBean> beanList;

    public List<BookBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<BookBean> beanList) {
        this.beanList = beanList;
    }
}
