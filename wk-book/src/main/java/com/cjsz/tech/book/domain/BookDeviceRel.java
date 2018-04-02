package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 图书设备关系
 * Created by Administrator on 2016/12/24 0024.
 */
@Entity
@Table(name = "book_device_rel")
public class BookDeviceRel {

    private Long device_id;     //设备ID

    private Long book_id;       //图书ID

    private Long book_cat_id;       //图书分类ID

    private Long org_id;        //机构ID

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    private Integer status;     //1:离线中;2:已离线;3:取消离线

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改时间

    public BookDeviceRel(){}

    public BookDeviceRel(Long device_id, Long book_id, Long book_cat_id, Long org_id, Date create_time, Integer status, Date update_time){
        this.device_id = device_id;
        this.book_id = book_id;
        this.book_cat_id = book_cat_id;
        this.org_id = org_id;
        this.create_time = create_time;
        this.status = status;
        this.update_time = update_time;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

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

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
