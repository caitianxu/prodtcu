package com.cjsz.tech.count.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.mapper.BookOrgRelMapper;
import com.cjsz.tech.cms.mapper.ArticleMapper;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.count.domain.ResourceCount;
import com.cjsz.tech.count.mapper.ResourceCountMapper;
import com.cjsz.tech.count.service.ResourceCountService;
import com.cjsz.tech.dev.beans.*;
import com.cjsz.tech.dev.mapper.DeviceMapper;
import com.cjsz.tech.journal.mapper.NewspaperMapper;
import com.cjsz.tech.journal.mapper.PeriodicalRepoMapper;
import com.cjsz.tech.media.mapper.AudioMapper;
import com.cjsz.tech.media.mapper.VideoMapper;
import com.cjsz.tech.member.mapper.UnifyMemberMapper;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.CacheUtil;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.DateUtils;
import com.github.pagehelper.PageHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class ResourceCountServiceImpl implements ResourceCountService {
	
    @Autowired
    private ResourceCountMapper resourceCountMapper;

	@Autowired
	private UnifyMemberMapper unifyMemberMapper;
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private BookOrgRelMapper bookOrgRelMapper;
	@Autowired
	private ArticleMapper articleMapper;
	@Autowired
	private NewspaperMapper newspaperMapper;
	@Autowired
	private PeriodicalRepoMapper periodicalRepoMapper;
	@Autowired
	private VideoMapper videoMapper;
	@Autowired
	private AudioMapper audioMapper;


	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源统计")
	public void saveCount(ResourceCount count) {
		count.setCreate_time(new Date());
		resourceCountMapper.insert(count);
	}

	@Override
	public Object pageQuery(Sort sort, PageConditionBean pageCondition) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<ResCountBean> result = null;
  		if("book".equals(pageCondition.getSearchText())){
  			result = resourceCountMapper.getBookCount();
  		}else if("news".equals(pageCondition.getSearchText())){
  			result = resourceCountMapper.getNewsCount();
  		}
  		PageList pageList = new PageList(result, null);
  		return pageList;
	}

	@Override
	public Long getMaxId() {
		return resourceCountMapper.getMaxId();
	}

	@Override
	public List<ResCountBean> getOrgCount(String year, Integer resource_type) {
		return resourceCountMapper.getOrgCount(year, resource_type);
	}

	@Override
	public List<ResCountBean> getDeviceCount(String year) {
		return resourceCountMapper.getDeviceCount(year);
	}

	@Override
	public Integer getArticleCount(Long org_id) {
		return resourceCountMapper.getArticleCount(org_id);
	}

	@Override
	public Integer getNewspaperCount(Long org_id) {
		return resourceCountMapper.getNewspaperCount(org_id);
	}

	@Override
	public Integer getJournalCount(Long org_id) {
		return resourceCountMapper.getJournalCount(org_id);
	}

	@Override
	public Integer getVideoCount(Long org_id) {
		return resourceCountMapper.getVideoCount(org_id);
	}

	@Override
	public Integer getAudioCount(Long org_id) {
		return resourceCountMapper.getAudioCount(org_id);
	}

	@Override
	public Integer getMemberCount(Long org_id) {
		return resourceCountMapper.getIndexMemberCount(org_id);
	}

	@Override
	public Integer getDevicesCount(Long org_id) {
		return resourceCountMapper.getIndexDeviceCount(org_id);
	}

	@Override
	public Integer getBooksCount(Long org_id) {
		return resourceCountMapper.getIndexBookCount(org_id);
	}



	//后台首页统计
	@Override
	public IndexCountNewBean indexCount(Long org_id) {
		IndexCountNewBean result = new IndexCountNewBean();
		Integer member_num,device_num,book_num,news_num,paper_num,periodical_num,video_num,audio_num;
		List<BookCountBean> books = new ArrayList<>();
		List<MemberCountBean> members= new ArrayList<>();
		if (org_id == 1) {
			member_num = unifyMemberMapper.getCount();
			device_num = deviceMapper.getCountByOrgId(org_id);
			book_num = bookOrgRelMapper.getCount();
			news_num = articleMapper.getCountOrg(org_id);
			paper_num = newspaperMapper.getCount(org_id);
			periodical_num = periodicalRepoMapper.getCount(org_id);
			video_num = videoMapper.getCount(org_id);
			audio_num = audioMapper.getCount(org_id);
			books=resourceCountMapper.getBookIndexCount();
			members = resourceCountMapper.getMemberIndexCount();
		}else{
			member_num = unifyMemberMapper.getCountByOrgId(org_id);
			device_num = deviceMapper.getCountByOrgId(org_id);
		 	book_num = bookOrgRelMapper.getCountByOrgId(org_id);
		 	news_num = articleMapper.getCountByOrgId(org_id);
		 	paper_num = newspaperMapper.getCountByOrgId(org_id);
			periodical_num = periodicalRepoMapper.getCountByOrgId(org_id);
		 	video_num = videoMapper.getCountByOrgId(org_id);
		 	audio_num = audioMapper.getCountByOrgId(org_id);
		 	books=resourceCountMapper.getBookIndexCountByOrgId(org_id);
		 	members=resourceCountMapper.getMemberIndexCountByOrgId(org_id);
		}

		result.setMember_num(member_num);
		result.setDevice_num(device_num);
		result.setBook_num(book_num);
		result.setNews_num(news_num);
		result.setPaper_num(paper_num);
		result.setPeriodical_num(periodical_num);
		result.setVideo_num(video_num);
		result.setAudio_num(audio_num);
		result.setBooks(books);
		result.setMembers(members);
		CacheUtil.set("index",result);
		return result;
	}

}
