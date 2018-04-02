package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.MailTemplateBean;
import com.cjsz.tech.system.domain.MailTemplate;
import com.cjsz.tech.system.provider.MailTemplateProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/1/24.
 */
public interface MailTemplateMapper extends BaseMapper<MailTemplate> {

    @SelectProvider(type = MailTemplateProvider.class, method = "pageQuery")
    List<MailTemplateBean> pageQuery(@Param("bean") MailTemplateBean bean);

    @Select("select * from mail_template where template_code = #{0} limit 1")
    MailTemplate findByCode(Integer code);
}
