package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.MailSettingBean;
import com.cjsz.tech.system.domain.MailSetting;
import com.cjsz.tech.system.mapper.MailSettingMapper;
import com.cjsz.tech.system.service.MailSettingService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/24.
 */
@Service
public class MailSettingServiceImpl implements MailSettingService {

    @Autowired
    private MailSettingMapper mailSettingMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并将列表对象转换为dto对象传输到前台
        List<MailSetting> result = mailSettingMapper.pageQuery(bean);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "邮件设置")
    public MailSetting save(MailSetting mailSetting) {
        mailSettingMapper.insert(mailSetting);
        return mailSettingMapper.selectByPrimaryKey(mailSetting);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "邮件设置")
    public MailSetting update(MailSetting mailSetting) {
        mailSettingMapper.updateByPrimaryKey(mailSetting);
        return mailSettingMapper.selectByPrimaryKey(mailSetting.getMail_setting_id());
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "邮件设置")
    public void deleteByIdStr(String idStr) {
        if (StringUtil.isNotEmpty(idStr)) {
            mailSettingMapper.deleteByIdStr(idStr);
        }
    }

	@Override
	public MailSetting getMail() {
		return mailSettingMapper.getMail();
	}

	@Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "邮件设置")
	public void updateTimes(MailSetting setting) {
		setting.setUsed_times(setting.getUsed_times() + 1);
		//可用次数 > 0 
		if(setting.getAvailable_times() > 0){
			Integer times = setting.getAvailable_times() - 1;
			setting.setAvailable_times(times);
		}
		setting.setUpdate_time(new Date());
		mailSettingMapper.updateByPrimaryKey(setting);
	}

	@Override
	public MailSetting selectById(Long id) {
		return mailSettingMapper.selectByPrimaryKey(id);
	}

	@Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "邮件设置")
	public void restSendTimes() {
		mailSettingMapper.restSendTimes();
	}
	
}
