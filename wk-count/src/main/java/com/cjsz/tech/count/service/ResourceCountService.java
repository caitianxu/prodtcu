package com.cjsz.tech.count.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.count.domain.ResourceCount;
import com.cjsz.tech.dev.beans.IndexCountBean;
import com.cjsz.tech.dev.beans.IndexCountNewBean;
import com.cjsz.tech.dev.beans.ResCountBean;

import java.util.List;

import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface ResourceCountService {
	
	//新增统计
	void saveCount(ResourceCount count);
	
	//分页查询
	Object pageQuery(Sort sort, PageConditionBean pageCondition);

	//获取新增数据的id
	Long getMaxId();
	
	//获取机构总访问量
	List<ResCountBean> getOrgCount(String year, Integer resource_type);
	
	//设备访问总量
	List<ResCountBean> getDeviceCount(String year);

	//资讯详情总量
	Integer getArticleCount(Long root_org_id);

	//报纸详情总量
	Integer getNewspaperCount(Long root_org_id);

	//杂志详情总量
	Integer getJournalCount(Long root_org_id);

	//视频详情总量
	Integer getVideoCount(Long root_org_id);

	//音频详情总量
	Integer getAudioCount(Long root_org_id);
	
	//会员总量
	Integer getMemberCount(Long root_org_id);
	
	//终端总量
	Integer getDevicesCount(Long root_org_id);
	
	//图书总量
	Integer getBooksCount(Long root_org_id);

	//首页统计
	IndexCountNewBean indexCount(Long root_org_id);
}
