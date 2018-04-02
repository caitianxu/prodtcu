package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据包
 * Created by Administrator on 2016/12/19 0019.
 */
@Entity
@Table(name = "pkg")
public class DataPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkg_id;        //数据包ID

    private String pkg_name;    //数据包名

    private Long org_count;     //使用机构数

    private Long book_count;     //图书数量(包括重复)

    private Long book_real_count;     //图书数量(去重复)

    private Long create_user_id;     //创建用户Id

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改时间

    @Transient
    private String create_user_name;//创建用户名称

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public String getPkg_name() {
        return pkg_name;
    }

    public void setPkg_name(String pkg_name) {
        this.pkg_name = pkg_name;
    }

    public Long getOrg_count() {
        return org_count;
    }

    public void setOrg_count(Long org_count) {
        this.org_count = org_count;
    }

    public Long getBook_count() {
        return book_count;
    }

    public void setBook_count(Long book_count) {
        this.book_count = book_count;
    }

    public Long getBook_real_count() {
        return book_real_count;
    }

    public void setBook_real_count(Long book_real_count) {
        this.book_real_count = book_real_count;
    }

    public Long getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(Long create_user_id) {
        this.create_user_id = create_user_id;
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

    public String getCreate_user_name() {
        return create_user_name;
    }

    public void setCreate_user_name(String create_user_name) {
        this.create_user_name = create_user_name;
    }
}
