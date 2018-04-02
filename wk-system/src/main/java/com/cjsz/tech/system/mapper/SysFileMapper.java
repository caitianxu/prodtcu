package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.SysFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Author:Jason
 * Date:2016/11/28
 */
public interface SysFileMapper extends BaseMapper<SysFile> {
    @Select("select  * from sys_file sf where sf.file_id in(${ids}) ")
    List<SysFile> selectByIds(@Param("ids") String inStr);

    @Delete("delete from sys_file  where full_path like CONCAT('',#{0},'%')")
    void deleteByFullPath(String fullPath);

    @Select("select savePath from sys_file sf where sf.full_path like CONCAT('',#{0},'%') and sf.file_type!=-1")
    List<String> selectAllFileOfFolder(String fullPath);

    @Select("select  * from sys_file sf where sf.file_id =#{0} ")
    SysFile selectById(Long id);

    @Select("select * from sys_file sf where sf.org_id=#{0} and sf.file_name like CONCAT('%',#{1},'%')  ")
    List<SysFile> findLike(Long root_org_id, String searchText);

    @Select("select * from sys_file sf where sf.org_id=#{0} and sf.pid =#{1}")
    List<SysFile> findFilesOfDir(Long root_org_id, Long dirId);
}
