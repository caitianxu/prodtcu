package com.cjsz.tech.book.beans;


import java.util.Date;

/**
 * 图书详情
 * Created by Administrator on 2016/10/25.
 */
public class BookBean {

    private Long book_id;       //图书ID

    private Long org_id;       //机构ID

    private Long book_cat_id;       //图书分类ID

    private String book_cat_name;       //图书分类名称

    private Long pkg_id;       //数据包ID

    private String book_name;       //书名

    private String book_author;     //作者

    private String book_cover;     //图书封面

    private String book_cover_small;     //图书封面压缩图

    private String book_isbn;     //ISBN号码

    private Integer enabled;     //是否启用(1:是;2:否)

    private Long order_weight;     //排序

    private Integer is_hot;     //是否热门(1:是;2:否)

    private Integer is_recommend;     //是否推荐(1:是;2:否)

    private Integer offline_status;     //0:不离线;1:发送离线

    private Long click_count;     //点击量

    private Long collect_count;     //收藏数

    private Long share_count;     //分享数

    private String tag_names;       //标签名称

    private Long device_id;         //设备ID

    private Integer status;         //1:离线中;2:已离线

    private Long shelf_id;          //书架Id

    private String book_url;        //图书url

    private String book_remark;     //图书描述

    private String book_publisher;  //出版社

    private String book_cat_names;//分类

    private Date publish_time;  //出版日期

    public String getBook_cat_names() {
        return book_cat_names;
    }

    public void setBook_cat_names(String book_cat_names) {
        this.book_cat_names = book_cat_names;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getBook_cat_id() {
        return book_cat_id;
    }

    public void setBook_cat_id(Long book_cat_id) {
        this.book_cat_id = book_cat_id;
    }

    public String getBook_cat_name() {
        return book_cat_name;
    }

    public void setBook_cat_name(String book_cat_name) {
        this.book_cat_name = book_cat_name;
    }

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
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

    public String getBook_cover_small() {
        return book_cover_small;
    }

    public void setBook_cover_small(String book_cover_small) {
        this.book_cover_small = book_cover_small;
    }

    public String getBook_isbn() {
        return book_isbn;
    }

    public void setBook_isbn(String book_isbn) {
        this.book_isbn = book_isbn;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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

    public Integer getOffline_status() {
        return offline_status;
    }

    public void setOffline_status(Integer offline_status) {
        this.offline_status = offline_status;
    }

    public Long getClick_count() {
        return click_count;
    }

    public void setClick_count(Long click_count) {
        this.click_count = click_count;
    }

    public Long getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(Long collect_count) {
        this.collect_count = collect_count;
    }

    public Long getShare_count() {
        return share_count;
    }

    public void setShare_count(Long share_count) {
        this.share_count = share_count;
    }

    public String getTag_names() {
        return tag_names;
    }

    public void setTag_names(String tag_names) {
        this.tag_names = tag_names;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getShelf_id() {
        return shelf_id;
    }

    public void setShelf_id(Long shelf_id) {
        this.shelf_id = shelf_id;
    }

    public String getBook_url() {
        return book_url;
    }

    public void setBook_url(String book_url) {
        this.book_url = book_url;
    }

    public String getBook_remark() {
        return book_remark;
    }

    public void setBook_remark(String book_remark) {
        this.book_remark = book_remark;
    }

    public String getBook_publisher() {
        return book_publisher;
    }

    public void setBook_publisher(String book_publisher) {
        this.book_publisher = book_publisher;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }
}
