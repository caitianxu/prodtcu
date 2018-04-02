package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/24.
 */
@Entity
@Table(name = "mail_setting")
public class MailSetting implements Serializable{

    @Id
    private Long mail_setting_id;//邮件设置ID

    private String smtp_url;//SMTP服务器地址

    private String account;//账号

    private String pwd;//密码

    private String account_name;//账号名称

    private Integer is_authenticate;//是否需要身份验证(0：否，1：是)

    private String port;//端口号

    private Integer is_ssl;//是否SSL加密(0：否，1：是)

    private Integer used_times;//已用次数

    private Integer available_times;//可用次数

    private Integer auto_zero;//是否自动清零(0：否，1：是)

    private Integer status;//状态

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//更新时间

    public Long getMail_setting_id() {
        return mail_setting_id;
    }

    public void setMail_setting_id(Long mail_setting_id) {
        this.mail_setting_id = mail_setting_id;
    }

    public String getSmtp_url() {
        return smtp_url;
    }

    public void setSmtp_url(String smtp_url) {
        this.smtp_url = smtp_url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public Integer getIs_authenticate() {
        return is_authenticate;
    }

    public void setIs_authenticate(Integer is_authenticate) {
        this.is_authenticate = is_authenticate;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Integer getIs_ssl() {
        return is_ssl;
    }

    public void setIs_ssl(Integer is_ssl) {
        this.is_ssl = is_ssl;
    }

    public Integer getUsed_times() {
        return used_times;
    }

    public void setUsed_times(Integer used_times) {
        this.used_times = used_times;
    }

    public Integer getAvailable_times() {
        return available_times;
    }

    public void setAvailable_times(Integer available_times) {
        this.available_times = available_times;
    }

    public Integer getAuto_zero() {
        return auto_zero;
    }

    public void setAuto_zero(Integer auto_zero) {
        this.auto_zero = auto_zero;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
