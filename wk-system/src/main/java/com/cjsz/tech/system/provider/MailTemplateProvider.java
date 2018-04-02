package com.cjsz.tech.system.provider;

import com.cjsz.tech.system.beans.MailTemplateBean;

import java.util.Map;

/**
 * Created by Li Yi on 2017/1/24.
 */
public class MailTemplateProvider {

    public String pageQuery(Map<String, Object> param){
        MailTemplateBean bean = (MailTemplateBean) param.get("bean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT mt.*, su.user_name FROM mail_template mt LEFT JOIN sys_user su ON su.user_id = mt.user_id");
        return sqlBuffer.toString();
    }
}
