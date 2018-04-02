package com.cjsz.tech.system.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.PageConditionBean;

import java.util.Date;

/**
 * Created by Li Yi on 2017/1/24.
 */
public class MailTemplateBean extends PageConditionBean {

    private Long mail_template_id;//邮件模板ID

    private Integer template_code;//编号

    private String mail_template_name;//模板名称

    private String content;//内容

    private Long user_id;//修改人ID

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//修改时间

    private String user_name;//修改人名称

    public Long getMail_template_id() {
        return mail_template_id;
    }

    public void setMail_template_id(Long mail_template_id) {
        this.mail_template_id = mail_template_id;
    }

    public Integer getTemplate_code() {
        return template_code;
    }

    public void setTemplate_code(Integer template_code) {
        this.template_code = template_code;
    }

    public String getMail_template_name() {
        return mail_template_name;
    }

    public void setMail_template_name(String mail_template_name) {
        this.mail_template_name = mail_template_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
