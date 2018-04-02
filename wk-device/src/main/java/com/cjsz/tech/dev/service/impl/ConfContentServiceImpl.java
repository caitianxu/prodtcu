package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.FindConfContentBean;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.dev.domain.ConfContent;
import com.cjsz.tech.dev.mapper.ConfContentMapper;
import com.cjsz.tech.dev.service.ConfContentService;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class ConfContentServiceImpl implements ConfContentService{

    @Autowired
    private ConfContentMapper confContentMapper;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Object pageQuery(Sort sort, PageConditionBean pageCondition) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<ConfContent> result = confContentMapper.pageQuery(null, pageCondition.getSearchText());
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	public Object selectConfContents(Sort sort, FindConfContentBean bean) {
		//分页的另外一种用法,紧随其后的第一个查询将使用分页
		PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null){
			PageHelper.orderBy(order);
		}
		//组装分页列表对象,并将列表对象转换为dto对象传输到前台
		List<ConfContent> result = confContentMapper.pageQuery(bean.getConf_id(), bean.getSearchText());
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	public List<ConfContent> selectConfContents(Long conf_id, Long update_time) {
		return null;
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "配置内容")
	public ConfContent saveContent(ConfContent content) {
    	confContentMapper.insert(content);
		return content;
	}

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "配置内容")
	public ConfContent updateContent(ConfContent content) {
    	confContentMapper.updateByPrimaryKey(content);
		return content;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "配置内容")
	public void deleteByIds(String content_ids_str) {
		confContentMapper.deleteByContentIds(content_ids_str);
	}

	@Override
	public List<ConfContent> getConfContentsByConfid(Long confid) {
		return confContentMapper.getConfContentsByConfid(confid);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "配置内容")
	public void deleteConfContentsByConfid(Long conf_id) {
		String sql = "delete from conf_content where conf_id="+conf_id;
		jdbcTemplate.update(sql);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "配置内容")
	public void saveContent(List<ConfContent> confList) {
		confContentMapper.insertList(confList);
	}

	@Override
	public List getOffLineNumList(Long orgid, String oldtimestamp, Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
		return confContentMapper.getOffLineNumList(orgid,oldtimestamp,(Long)otherparam[0], num, size);
	}

	@Override
	public Integer hasOffLine(Long orgid, String oldtimestamp,Object...otherparam) {
		Integer checknum = confContentMapper.checkOffLineNum(orgid,oldtimestamp,(Long)otherparam[0]);
		if(checknum==null ) {
			checknum =0;
		}
		return checknum;
	}

	@Override
	public List<ConfContent> selectByConfId(Long conf_id){
		return confContentMapper.getList(conf_id);
	}

	@Override
	public void updateContentTime(Long conf_id){
		confContentMapper.updateContentTime(conf_id);
	}
}
