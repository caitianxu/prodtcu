package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by henry on 2017/8/8.
 */
@Entity
@Table(name = "un_periodical_err")
public class UnPeriodicalErr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long periodical_id;
    private String periodical_url;
    private String file_name;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeriodical_id() {
        return periodical_id;
    }

    public void setPeriodical_id(Long periodical_id) {
        this.periodical_id = periodical_id;
    }

    public String getPeriodical_url() {
        return periodical_url;
    }

    public void setPeriodical_url(String periodical_url) {
        this.periodical_url = periodical_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
