package com.cjsz.tech.system.mapper;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.MailSettingBean;
import com.cjsz.tech.system.domain.MailSetting;
import com.cjsz.tech.system.provider.MailSettingProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2017/1/24.
 */
public interface MailSettingMapper extends BaseMapper<MailSetting> {

    @SelectProvider(type = MailSettingProvider.class, method = "pageQuery")
    List<MailSetting> pageQuery(@Param("bean") PageConditionBean bean);

    @Delete("DELETE FROM mail_setting WHERE mail_setting_id IN (${idStr})")
    void deleteByIdStr(@Param("idStr") String idStr);
    
    @Select("select * from mail_setting where status = 1 and available_times > 0 order by update_time asc limit 1")
    MailSetting getMail();

    @Update("update mail_setting set available_times = 600, used_times = 0, update_time = now() where used_times != 0")
	void restSendTimes();
}
