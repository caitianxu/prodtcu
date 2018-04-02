package com.cjsz.tech.system.domain;


import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 项目
 */
@Entity
@Table(name = "sys_project")
public class SysProject  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    project_id;     //项目ID
    private String  project_code;   //项目编号
    private String  project_name;   //项目名称
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date    create_time;    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date    update_time; //更新时间
    private String  remark;         //项目描述


    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
