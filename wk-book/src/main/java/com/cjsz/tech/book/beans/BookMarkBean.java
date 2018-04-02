package com.cjsz.tech.book.beans;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 书签
 * Created by LuoLi on 2017/4/10.
 */

public class BookMarkBean {


    private Long mark_id;//书签Id

    private Long org_id;//机构Id

    private Long book_id;//图书Id

    private Integer book_type;//图书类型

    private Long member_id;//用户Id

    private Long chapter_id;//章节Id

    private String content;//书签内容

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//创建时间

    private String chapter_name;

    private String name;    //章节名称

    public String getName() {
        if (StringUtils.isEmpty(name)){
            return chapter_name;
        }else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMark_id() {
        return mark_id;
    }

    public void setMark_id(Long mark_id) {
        this.mark_id = mark_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Long getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(Long chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getBook_type() {
        return book_type;
    }

    public void setBook_type(Integer book_type) {
        this.book_type = book_type;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }
}
