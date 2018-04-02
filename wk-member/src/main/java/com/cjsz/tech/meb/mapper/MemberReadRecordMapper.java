package com.cjsz.tech.meb.mapper;

import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.beans.ReadingRecord;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.meb.provider.MemberReadRecordProvider;
import com.cjsz.tech.system.beans.SearchBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/13 0013.
 */
public interface MemberReadRecordMapper extends BaseMapper<MemberReadRecord> {

    @Select("select * from member_read_record where member_id = #{0} and book_id = #{1} and device_type_code = #{2} and to_days(end_time) = to_days(now()) " +
            "order by end_time desc limit 1 ")
    MemberReadRecord selectCurDayNearestRecord(Long member_id, Long book_id, String device_type_code);

    @Select("select sum(total_time) from member_read_record where member_id = #{0} and to_days(end_time) = to_days(now()) ")
    Integer getTodayReadTime(Long member_id);

    @Select("select count(*) from ( " +
                "select book_id from member_read_record where member_id = #{0} group by book_id " +
            ") a ")
    Integer getReadBookCount(Long member_id);

//    @SelectProvider(type = MemberReadRecordProvider.class, method = "pageQuery")
//    List<MemberReadRecord> pageQuery(@Param("searchBean") SearchBean searchBean);

    @Select("select * from member_read_record where member_id = #{0} and book_id = #{1} and to_days(end_time) = to_days(now()) " +
            "order by end_time desc limit 1")
    MemberReadRecord selectCurDayNearestRecord_v2(Long member_id, Long book_id);

    @SelectProvider(type = MemberReadRecordProvider.class, method = "getList")
    List<MemberReadRecord> getList(@Param("bean") SearchBean bean, @Param("org_id") Long org_id);

    @SelectProvider(type = MemberReadRecordProvider.class, method = "clickList")
    List<BookIndex>  clickList(@Param("bean") SearchBean bean, @Param("org_id") Long org_id);

    @Update("update member_read_record set is_delete = 1 WHERE member_id=#{0}")
    void deleteReadRecord_v2(Long member_id);

    @Select("select * from( select r.book_name,r.book_cover as book_cover_small,r.last_chapter_id as chapter_id,r.book_type,r.end_time,r.book_id,r.last_chapter_id,r.chapter_name as name," +
            " r.record_id ,r.plan as schedule from member_read_record r where member_id = #{0} and r.is_delete = 2  order by end_time desc )a  group by a.book_id order by end_time desc ")
    List<ReadingRecord> getMemberReadList(Long member_id);

    @Select("select * from( select r.book_name,r.book_cover as book_cover_small,r.last_chapter_id as chapter_id,r.book_type,r.end_time,r.book_id,r.last_chapter_id,r.chapter_name as name," +
            " r.record_id ,r.plan as schedule from member_read_record r where member_id = #{0} and r.is_delete = 2 and r.book_type =2 order by end_time desc )a  group by a.book_id order by end_time desc ")
    List<ReadingRecord> getMemberReadListV2(Long member_id);
}
