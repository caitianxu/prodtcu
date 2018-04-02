package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by LuoLi on 2017/4/28 0028.
 */
public class SearchBean extends PageConditionBean {

    private Long id;

    private String type;

    private String code;

    private String create_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
