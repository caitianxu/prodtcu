package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodical;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface UnPeriodicalMapper extends BaseMapper<UnPeriodical> {

    //查询所有
    @Select("SELECT * FROM un_periodical where s_title like CONCAT('%','${searchText}','%')")
    List<UnPeriodical> pageQuery(@Param("searchText") String searchText);
    //查询所有
    @Select("SELECT * FROM un_periodical")
    List<UnPeriodical> pageQuery1();
    //通过文件名查询
    @Select("SELECT * from periodical_repo where file_name = #{0} LIMIT 1")
    PeriodicalRepo getPeriodicalByFileName(String file_name);
    //获取同系列一本 有分类和简介的期刊
    @Select("SELECT * from periodical_repo where series_name = #{0} and periodical_cat_id != '' and periodical_remark != '' LIMIT 1")
    PeriodicalRepo getParentRepo(String series_name);
    //查询未解析的期刊
    @Select("SELECT * from un_periodical where s_status = 0 LIMIT 1")
    UnPeriodical getUnPeriodicalTop();
    //修改解析任务状态
    @Update("UPDATE un_periodical set s_status=0 where s_title = #{0} ")
    void updateUnStatus(String title);
}
