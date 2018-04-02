package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.FindConfContentBean;
import com.cjsz.tech.dev.domain.ConfContent;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface ConfContentService  extends IOfflineService {

	/**
	 * 获取所有导航的分页列表
	 *
	 * @param sort
	 * @param pageCondition
	 * @return
	 */
	Object pageQuery(Sort sort, PageConditionBean pageCondition);

	/**
	 * 通过配置ID分页查询该配置的导航
	 *
	 * @param sort
	 * @param bean
	 * @return
	 */
	Object selectConfContents(Sort sort, FindConfContentBean bean);

	/**
	 * 通过配置ID查询该配置的导航
	 *
	 * @param conf_id
	 * @param update_time
	 * @return
	 */
	List<ConfContent> selectConfContents(Long conf_id, Long update_time);

	//新增配置内容关系
	ConfContent saveContent(ConfContent content);
	
	//更新
	ConfContent updateContent(ConfContent content);

	void deleteByIds(String content_ids_str);

	List<ConfContent>  getConfContentsByConfid(Long confid);

	void deleteConfContentsByConfid(Long conf_id);


	/**
	 * 保存配置菜单内容
	 * @param confList
	 */
	void saveContent(List<ConfContent> confList);

	/**
	 * 根据配置ID查询配置菜单
	 * @param conf_id
	 * @return
	 */
	List<ConfContent> selectByConfId(Long conf_id);

	/**
	 * 更新配置菜单时间以推送菜单内容
	 * @param conf_id
	 */
	void updateContentTime(Long conf_id);
}
