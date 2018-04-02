package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 数据包图书分类
 * Created by Administrator on 2016/12/21 0021.
 */
@Entity
@Table(name = "pkg_book_cat")
public class PkgBookCat {

    private Long book_cat_id;       //图书分类ID

    private Long org_id;            //机构ID

    private Long book_cat_pid;      //分类上级ID

    private String book_cat_path;   //层次路径

    private Long pkg_id;            //数据包ID

    private String book_cat_name;   //分类名称

    private Long order_weight;      //排序

    private String book_cat_remark; //分类简介

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    private Integer source_type;    //是否自建(1:自建;2:分配)

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;     //最后修改时间

    @Transient
    private List<PkgBookCat> children;

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getBook_cat_pid() {
        return book_cat_pid;
    }

    public void setBook_cat_pid(Long book_cat_pid) {
        this.book_cat_pid = book_cat_pid;
    }

    public String getBook_cat_path() {
        return book_cat_path;
    }

    public void setBook_cat_path(String book_cat_path) {
        this.book_cat_path = book_cat_path;
    }

    public String getBook_cat_name() {
        return book_cat_name;
    }

    public void setBook_cat_name(String book_cat_name) {
        this.book_cat_name = book_cat_name;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public String getBook_cat_remark() {
        return book_cat_remark;
    }

    public void setBook_cat_remark(String book_cat_remark) {
        this.book_cat_remark = book_cat_remark;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getSource_type() {
        return source_type;
    }

    public void setSource_type(Integer source_type) {
        this.source_type = source_type;
    }

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }


    public List<PkgBookCat> getChildren() {
        return children;
    }

    public void setChildren(List<PkgBookCat> children) {
        this.children = children;
    }
}
