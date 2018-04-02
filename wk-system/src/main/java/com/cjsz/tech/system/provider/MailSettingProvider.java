package com.cjsz.tech.system.provider;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.MailSettingBean;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/24.
 */
public class MailSettingProvider {

    public String pageQuery(Map<String, Object> param){
        PageConditionBean bean = (PageConditionBean) param.get("bean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT * FROM mail_setting");
        return sqlBuffer.toString();
    }
}
