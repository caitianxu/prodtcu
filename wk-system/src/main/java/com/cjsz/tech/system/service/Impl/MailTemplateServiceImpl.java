package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.MailTemplateBean;
import com.cjsz.tech.system.domain.MailTemplate;
import com.cjsz.tech.system.mapper.MailTemplateMapper;
import com.cjsz.tech.system.service.MailTemplateService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Administrator on 2017/1/24.
 */
@Service
public class MailTemplateServiceImpl implements MailTemplateService {

    @Autowired
    private MailTemplateMapper mailTemplateMapper;

	@Override
	public Object pageQuery(Sort sort, MailTemplateBean bean) {
		//分页的另外一种用法,紧随其后的第一个查询将使用分页
		PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		//组装分页列表对象,并将列表对象转换为dto对象传输到前台
		List<MailTemplateBean> result = mailTemplateMapper.pageQuery(bean);
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "邮件模板")
	public MailTemplate save(MailTemplate mailTemplate) {
		mailTemplateMapper.insert(mailTemplate);
		return mailTemplateMapper.selectByPrimaryKey(mailTemplate);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "邮件模板")
	public MailTemplate update(MailTemplate mailTemplate) {
		mailTemplateMapper.updateByPrimaryKey(mailTemplate);
		return mailTemplateMapper.selectByPrimaryKey(mailTemplate);
	}

	@Override
	public MailTemplate findByCode(Integer code) {
		return mailTemplateMapper.findByCode(code);
	}
}
