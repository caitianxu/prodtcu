package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.MailSettingBean;
import com.cjsz.tech.system.domain.MailSetting;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2017/1/24.
 */
public interface MailSettingService {

    Object pageQuery(Sort sort, PageConditionBean bean);

    MailSetting save(MailSetting mailSetting);

    MailSetting update(MailSetting mailSetting);

    void deleteByIdStr(String idStr);
    
    MailSetting getMail();
    
    void updateTimes(MailSetting mailSetting);
    
    MailSetting selectById(Long id);

	void restSendTimes();
}
