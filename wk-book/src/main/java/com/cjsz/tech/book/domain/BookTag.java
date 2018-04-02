package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 图书标签
 * Created by Administrator on 2016/12/19 0019.
 */
@Entity
@Table(name = "book_tag")
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tag_id;        //标签ID

    private String tag_name;    //标签名称

    private String tag_code;   //层级code

    private Long tag_pid;       //父id

    private String tag_path;    //标签path

    private Long order_weight;  //排序

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //创建时间

    private Integer is_delete;  //是否删除

    @Transient
    private Long book_count;    //标签下图书数量

    @Transient
    private List<BookTag> children;

    public String getTag_code() {
        return tag_code;
    }

    public void setTag_code(String tag_code) {
        this.tag_code = tag_code;
    }

    public List<BookTag> getChildren() {
        return children;
    }

    public void setChildren(List<BookTag> children) {
        this.children = children;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getTag_pid() {
        return tag_pid;
    }

    public void setTag_pid(Long tag_pid) {
        this.tag_pid = tag_pid;
    }

    public String getTag_path() {
        return tag_path;
    }

    public void setTag_path(String tag_path) {
        this.tag_path = tag_path;
    }

    public Long getTag_id() {
        return tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Long getBook_count() {
        return book_count;
    }

    public void setBook_count(Long book_count) {
        this.book_count = book_count;
    }
}
