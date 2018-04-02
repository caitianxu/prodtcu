package com.cjsz.tech.meb.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.beans.MemberReadIndexBean;
import com.cjsz.tech.meb.domain.MemberReadIndex;
import com.cjsz.tech.meb.provider.MemberReadIndexProvider;
import com.cjsz.tech.system.beans.SearchBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/4/13 0013.
 */
public interface MemberReadIndexMapper extends BaseMapper<MemberReadIndex> {

    @Select("select member_id, sum(total_time) total_time,sum(total_chapter) total_chapter,(floor(sum(total_time)/60))*sum(total_chapter) read_index from member_read_record " +
            "where to_days(end_time) = (to_days(now()) - 1) group by member_id")
    List<Map<String, Object>> selectYesterdayData();

    @Select("select mr.member_id, sum(mr.total_time) total_time, sum(mr.total_chapter) total_chapter, sum(mr.read_index) read_index, " +
            "m.nick_name, m.icon from member_read_index mr " +
            "left join tb_member m on m.member_id = mr.member_id " +
            "where m.org_id = #{0} and mr.member_id > 0 and WEEK(mr.create_time)=WEEK(NOW()) and YEAR(mr.create_time)=YEAR(NOW()) " +
            "group by mr.member_id order by mr.read_index desc")
    List<MemberReadIndexBean> getListByWeek(Long org_id);

    @Select("select mr.member_id, sum(mr.total_time) total_time, sum(mr.total_chapter) total_chapter, sum(mr.read_index) read_index, " +
            "m.nick_name, m.icon from member_read_index mr " +
            "left join tb_member m on m.member_id = mr.member_id " +
            "where m.org_id = #{0} and MONTH(mr.create_time)=MONTH(NOW()) and YEAR(mr.create_time)=YEAR(NOW()) " +
            "group by mr.member_id order by mr.read_index desc")
    List<MemberReadIndexBean> getListByMonth(Long org_id);

    @Select("select mr.member_id, sum(mr.total_time) total_time, sum(mr.total_chapter) total_chapter, sum(mr.read_index) read_index, " +
            "m.nick_name, m.icon from member_read_index mr " +
            "left join tb_member m on m.member_id = mr.member_id " +
            "where m.org_id = #{0} and mr.member_id > 0 and YEAR(mr.create_time)=YEAR(NOW()) " +
            "group by mr.member_id order by mr.read_index desc")
    List<MemberReadIndexBean> getListByYear(Long org_id);


    @Select("select * from " +
                "(select member_id, total_time, read_index, @curRow := @curRow + 1 as read_order from ( " +
                    " select m.member_id, mr.total_time, mr.read_index from tb_member m " +
                        " left join member_read_index mr on m.member_id = mr.member_id " +
                        " group by m.member_id order by mr.read_index desc  " +
                    ")ddd " +
                "join (select @curRow := 0) r" +
            ")a where member_id = #{0}")
    Map<String, Object> getMemberOrder(Long member_id);

    @SelectProvider(type = MemberReadIndexProvider.class, method = "pageQuery")
    List<MemberReadIndex> pageQuery(@Param("searchBean") SearchBean searchBean);
}