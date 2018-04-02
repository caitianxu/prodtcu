package com.cjsz.tech.system.beans;

import java.util.List;

/**
 * 树形结构类
 * Created by LuoLi on 2017/4/28 0028.
 */
public class TreeBean {

    private Long id;

    private Long pid;

    private String code;

    private String text;

    private String type;

    private List<TreeBean> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TreeBean> getChildren() {
        return children;
    }

    public void setChildren(List<TreeBean> children) {
        this.children = children;
    }
}
