package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 图书标签关系
 * Created by Administrator on 2016/12/19 0019.
 */
@Entity
@Table(name = "book_tag_rel")
public class BookTagRel {

    private Long book_id;       //图书ID

    private Long tag_id;        //图书标签ID

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    @Transient
    private String tag_name;

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public BookTagRel(){}

    public BookTagRel(Long book_id, Long tag_id, Date create_time){
        this.book_id = book_id;
        this.tag_id = tag_id;
        this.create_time = create_time;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getTag_id() {
        return tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    @Override
    public boolean equals(Object object){
        if(this == object){
            return true;
        }
        if(object == null){
            return false;
        }
        if(getClass() != object.getClass()){
            return false;
        }
        BookTagRel bookTagRel = (BookTagRel) object;
        if(!book_id.equals(bookTagRel.getBook_id())){
            return false;
        }
        if(!tag_id.equals(bookTagRel.getTag_id())){
            return false;
        }
        return true;
    }
}
