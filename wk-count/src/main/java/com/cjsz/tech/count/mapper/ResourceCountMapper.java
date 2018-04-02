package com.cjsz.tech.count.mapper;

import java.util.Date;
import java.util.List;

import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.count.domain.ResourceCount;
import com.cjsz.tech.dev.beans.BookCountBean;
import com.cjsz.tech.dev.beans.MemberCountBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.beans.ResCountBean;
import com.cjsz.tech.count.provider.ResourceCountProvider;
import org.apache.ibatis.annotations.Update;

/**
 * Created by Li Yi on 2016/12/20.
 */
public interface ResourceCountMapper extends BaseMapper<ResourceCount>{

	@Select("select res_count_id from resource_count order by res_count_id desc limit 1")
	Long getMaxId();
	
	@Select("SELECT o.org_name, b.book_name as resource_name, r.operation_type, count(r.res_count_id) as num "
			+ " FROM resource_count r "
			+ " LEFT JOIN sys_organization o on r.org_id = o.org_id "
			+ " LEFT JOIN book_repo b on b.book_id = r.resource_id "
			+ " where r.resource_type = 1 and o.is_delete = 2 and o.enabled = 1"
			+ " GROUP BY r.org_id, r.resource_id, r.operation_type")
	List<ResCountBean> getBookCount();
	
	@Select("SELECT o.org_name, a.article_title as resource_name, count(r.res_count_id) as num "
			+ " FROM resource_count r "
			+ " LEFT JOIN sys_organization o on r.org_id = o.org_id "
			+ " LEFT JOIN article a on a.article_id = r.resource_id "
			+ " where r.resource_type = 2 and o.is_delete = 2 and o.enabled = 1 "
			+ " GROUP BY r.org_id, r.resource_id")
	List<ResCountBean> getNewsCount();
	
	@SelectProvider(type = ResourceCountProvider.class, method = "getCount")
	List<ResCountBean> getOrgCount(@Param("year") String year, @Param("resource_type") Integer resource_type);
	
	@Select("SELECT concat(o.org_name,'-' ,d.device_code) as org_name, count(r.res_count_id) as num "
			+ " FROM resource_count r "
			+ " LEFT JOIN sys_organization o on r.org_id = o.org_id "
			+ " LEFT JOIN device d on r.device_id = d.device_id "
			+ " where r.create_time BETWEEN CONCAT(#{0},'-1-1 00:00:00') AND CONCAT(#{0},'-12-31 23:59:59')"
			+ " and o.is_delete = 2 and o.enabled = 1"
			+ " GROUP BY r.device_id order by r.device_id limit 10")
	List<ResCountBean> getDeviceCount(String year);

	@SelectProvider(type = ResourceCountProvider.class, method = "getArticleCount")
	Integer getArticleCount(@Param("org_id") Long org_id);

	@SelectProvider(type = ResourceCountProvider.class, method = "getNewspaperCount")
	Integer getNewspaperCount(@Param("org_id") Long org_id);

	@SelectProvider(type = ResourceCountProvider.class, method = "getJournalCount")
	Integer getJournalCount(@Param("org_id") Long org_id);

	@SelectProvider(type = ResourceCountProvider.class, method = "getVideoCount")
	Integer getVideoCount(@Param("org_id") Long org_id);

	@SelectProvider(type = ResourceCountProvider.class, method = "getAudioCount")
	Integer getAudioCount(@Param("org_id") Long org_id);
	
	@SelectProvider(type = ResourceCountProvider.class, method = "getIndexMemberCount")
	Integer getIndexMemberCount(@Param("org_id") Long org_id);
	
	@SelectProvider(type = ResourceCountProvider.class, method = "getIndexDeviceCount")
	Integer getIndexDeviceCount(@Param("org_id") Long org_id);
	
	@SelectProvider(type = ResourceCountProvider.class, method = "getIndexBookCount")
	Integer getIndexBookCount(@Param("org_id") Long org_id);
	
	@Select("SELECT concat(o.org_name,'-' ,d.device_code) as org_name, count(r.res_count_id) as num "
			+ " FROM resource_count r "
			+ " LEFT JOIN sys_organization o on r.org_id = o.org_id "
			+ " LEFT JOIN device d on r.device_id = d.device_id "
			+ " where r.create_time BETWEEN #{0} AND #{1}"
			+ " and o.is_delete = 2 and o.enabled = 1"
			+ " GROUP BY r.device_id order by r.device_id limit 10")
	List<ResCountBean> getDeviceDateCount(String startDate, String endDate);

	@Select("SELECT DISTINCT * from( " +
			"select m.nick_name,mi.member_id,mi.read_index as count from member_read_index mi " +
			"left join tb_member m on m.member_id=mi.member_id  order by mi.read_index desc " +
			")aaa limit 10 ")
	List<MemberCountBean> getMemberIndexCount();

	@Select("SELECT DISTINCT * from( " +
			"select m.nick_name,mi.member_id,mi.read_index as count from member_read_index mi " +
			"left join tb_member m on m.member_id=mi.member_id where m.org_id = #{0} order by mi.read_index desc " +
			")aaa limit 10")
	List<MemberCountBean> getMemberIndexCountByOrgId(Long org_id);

	@Select("select b.book_name,bi.book_id,max(bi.unite_index) as count from book_index bi left join book_repo b on b.book_id=bi.book_id " +
			"group by bi.book_id order by count desc limit 30  ")
	List<BookCountBean> getBookIndexCount();

	@Select("select b.book_name,bi.book_id,max(bi.unite_index) as count from book_index bi left join book_repo b on b.book_id=bi.book_id " +
			"where bi.org_id = #{0} group by bi.book_id order by count desc limit 30  ")
	List<BookCountBean> getBookIndexCountByOrgId(Long org_id);

}


