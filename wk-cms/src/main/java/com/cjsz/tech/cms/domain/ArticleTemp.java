package com.cjsz.tech.cms.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 资讯模板
 * Created by Administrator on 2016/12/2 0002.
 */
@Entity
@Table(name = "article_temp")
public class ArticleTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer article_temp_id;    //资讯模板ID

    private String temp_name;   //模板名称

    private Long user_id;   //上传者ID

    private Long org_id;    //机构ID

    private Object temp_remark; //模板路径

    private String temp_url;    //备注

    @JSONField(format = "yyyy-MM-dd")
    private Date upload_time;   //上传时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;   //最后修改日期

    private Integer temp_status;    //1:解析;2:未解析;3:解析中

    @Transient
    private String user_name;   //上传用户名

    public Integer getArticle_temp_id() {
        return article_temp_id;
    }

    public void setArticle_temp_id(Integer article_temp_id) {
        this.article_temp_id = article_temp_id;
    }

    public String getTemp_name() {
        return temp_name;
    }

    public void setTemp_name(String temp_name) {
        this.temp_name = temp_name;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Object getTemp_remark() {
        return temp_remark;
    }

    public void setTemp_remark(Object temp_remark) {
        this.temp_remark = temp_remark;
    }

    public String getTemp_url() {
        return temp_url;
    }

    public void setTemp_url(String temp_url) {
        this.temp_url = temp_url;
    }

    public Date getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Date upload_time) {
        this.upload_time = upload_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getTemp_status() {
        return temp_status;
    }

    public void setTemp_status(Integer temp_status) {
        this.temp_status = temp_status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
