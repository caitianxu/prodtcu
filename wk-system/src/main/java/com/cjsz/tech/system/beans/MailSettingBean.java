package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2017/1/24.
 */
public class MailSettingBean extends PageConditionBean {

    private Long[] ids;//ID数组

    private Long mail_setting_id;//邮件设置ID

    private String email;//测试接收邮件地址

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    public Long getMail_setting_id() {
        return mail_setting_id;
    }

    public void setMail_setting_id(Long mail_setting_id) {
        this.mail_setting_id = mail_setting_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
