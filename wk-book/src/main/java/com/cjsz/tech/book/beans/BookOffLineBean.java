package com.cjsz.tech.book.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 图书离线实体类
 * Created by Administrator on 2016/12/26 0026.
 */
public class BookOffLineBean implements Serializable {

    private Long book_id;           //图书ID

    private String book_name;       //书名

    private String book_author;     //作者

    private String book_cover;      //图书封面

    private String book_isbn;       //ISBN号码

    private String book_url;        //图书路径

    private String book_publisher;  //出版社

    private String book_remark;     //图书简介

    @JSONField(format = "yyyy-MM-dd")
    private Date publish_time;    //出版时间

    private Integer book_status;    //1:上架;2:下架

    @JSONField(format = "yyyy-MM-dd")
    private Date end_time;          //版权截止日期

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;       //创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;       //更新时间

    private Integer parse_status;   //解析状态（1:未解析;2:解析中;3:已解析）

    private String tag_names;        //图书标签名称（"name1,name2,name3"）

    @JSONField(format = "yyyy-MM-dd")
    private Date book_create_time;  //图书机构关系创建时间

    private Long order_weight;      //机构图书排序

    private Integer is_hot;         //机构图书是否热门(1:是;2:否)

    private Integer is_recommend;   //机构图书是否推荐(1:是;2:否)

    private Long book_cat_id;       //机构图书分类Id

    private Integer enabled;        //机构图书是否启用(0:未启用;1:启用;2:停用)

    private Long device_id;         //设备Id

    private Integer offline_status; //设备图书离线状态（1:离线中;2:已离线;3:取消离线）

    private Long org_id;            //机构Id

    private Long pkg_id;            //数据包Id

    @JSONField(format = "yyyy-MM-dd")
    private Date book_update_time;  //机构图书更新时间

    @JSONField(format = "yyyy-MM-dd")
    private Date repo_update_time;  //图书仓库更新时间

    @JSONField(format = "yyyy-MM-dd")
    private Date sendoff_update_time;   //图书离线更新时间

    @Transient
    private String scanurl = null;//图书扫码地址



    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_isbn() {
        return book_isbn;
    }

    public void setBook_isbn(String book_isbn) {
        this.book_isbn = book_isbn;
    }

    public String getBook_url() {
        return book_url;
    }

    public void setBook_url(String book_url) {
        this.book_url = book_url;
    }

    public String getBook_publisher() {
        return book_publisher;
    }

    public void setBook_publisher(String book_publisher) {
        this.book_publisher = book_publisher;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public Integer getBook_status() {
        return book_status;
    }

    public void setBook_status(Integer book_status) {
        this.book_status = book_status;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
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

    public Integer getParse_status() {
        return parse_status;
    }

    public void setParse_status(Integer parse_status) {
        this.parse_status = parse_status;
    }

    public String getTag_names() {
        return tag_names;
    }

    public void setTag_names(String tag_names) {
        this.tag_names = tag_names;
    }

    public Date getBook_create_time() {
        return book_create_time;
    }

    public void setBook_create_time(Date book_create_time) {
        this.book_create_time = book_create_time;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(Integer is_hot) {
        this.is_hot = is_hot;
    }

    public Integer getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(Integer is_recommend) {
        this.is_recommend = is_recommend;
    }

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public Integer getOffline_status() {
        return offline_status;
    }

    public void setOffline_status(Integer offline_status) {
        this.offline_status = offline_status;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public Date getBook_update_time() {
        return book_update_time;
    }

    public void setBook_update_time(Date book_update_time) {
        this.book_update_time = book_update_time;
    }

    public Date getRepo_update_time() {
        return repo_update_time;
    }

    public void setRepo_update_time(Date repo_update_time) {
        this.repo_update_time = repo_update_time;
    }

    public Date getSendoff_update_time() {
        return sendoff_update_time;
    }

    public void setSendoff_update_time(Date sendoff_update_time) {
        this.sendoff_update_time = sendoff_update_time;
    }

    public String getScanurl() {
        return scanurl;
    }

    public void setScanurl(String scanurl) {
        this.scanurl = scanurl;
    }
}
