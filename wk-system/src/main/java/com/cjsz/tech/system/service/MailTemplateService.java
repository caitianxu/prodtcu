package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.MailTemplateBean;
import com.cjsz.tech.system.domain.MailTemplate;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2017/1/24.
 */
public interface MailTemplateService {

	Object pageQuery(Sort sort, MailTemplateBean bean);

	MailTemplate save(MailTemplate mailTemplate);

	MailTemplate update(MailTemplate mailTemplate);

	MailTemplate findByCode(Integer code);
}
