package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.SysResource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
public interface SysResourceMapper extends BaseMapper<SysResource> {

    @Select("select * from sys_resource where res_id = #{0}")
    public SysResource findById(Long res_id);

    @Select("select * from sys_resource where res_id in(${res_ids})" )
    public List<SysResource> selectByResIds(@Param("res_ids") String res_ids);

    @Delete("delete from sys_resource where res_id = #{0}")
    public void deleteById(Long res_id);

    @Select("select * from sys_resource where org_id = 1")
    public List<SysResource> getAllRes();

    @Select("select * from sys_resource where enabled=1 and org_id = 1 order by pid asc")
    public List<SysResource> findAll();

    @Select("select * from sys_resource where enabled=1 and org_id = #{0} order by length(full_path)-length(replace(full_path,'|','')) asc, order_weight desc")
    public List<SysResource> selectAllSysMenuByOrgId(Long org_id);

    @Select("select * from sys_resource where enabled=1 and res_id in (select distinct res_id from sys_role_res_rel where role_id in (${roleid})) order by length(full_path)-length(replace(full_path,'|','')) asc, order_weight desc")
    List<SysResource> findAllByRoleId(@Param("roleid") String roleid);

    @Select("select * from sys_resource where pid=0 and enabled=1 and role_res_rel_id in (select distinct res_id from sys_role_res_rel where role_id in (${roleid}))  order by order_weight desc")
    public List<SysResource> selectTopMenuList(@Param("roleid") String roleid);

    @Select("select * from sys_resource where res_id<>#{pid} and enabled=1 and res_type = 1 and  full_path like CONCAT('',#{full_path},'%') and res_id in (select distinct res_id from sys_role_res_rel where role_id in (${roleid})) order by order_weight desc,pid asc")
    public List<SysResource> selectLeftMenuList(@Param("pid") Long pid, @Param("full_path") String full_path, @Param("roleid") String roleid);

    @Select("select * from sys_resource where enabled=1 and full_path like CONCAT('',#{0},'%') ")
    public List<SysResource> findByFullPath(String full_path);

    @Delete("delete from sys_resource where full_path like CONCAT('',#{0},'%')")
    public void deleteByFullPath(String full_path);

    @Update("update sys_resource sr set sr.full_path = REPLACE(sr.full_path,#{0},#{1}) , sr.update_time = now() " +
            "where sr.full_path like concat(#{0},'%') and sr.full_path != #{0}")
    public void updateSysMenuByFullPath(String old_full_path, String new_full_path);

    @Select("select * from sys_resource where org_id = #{0} order by length(full_path)-length(replace(full_path,'|','')) asc, order_weight desc")
    public List<SysResource> selectByOrg_id(Long org_id);

    @Select("select * from sys_resource where res_key = #{0} and org_id = #{1}")
    public SysResource selectByResKey(String res_key, Long org_id);

    @Delete("delete from sys_resource where org_id = #{0}")
    public void deleteByOrg_id(Long org_id);

    @Delete("delete from sys_resource where org_id !=1")
    public void deleteAllByOrg_id();

    @Select("select * from sys_resource where source_id = #{0} and org_id = #{1}")
    public SysResource selectBySourceIdAndOrgId(Long source_id, Long org_id);

    @Update("update sys_resource set enabled = #{0} , update_time = now() where full_path like concat(#{1},'%')")
    public void updateEnabledByFullPath(Integer enabled, String full_path);

    @Update("update sys_resource set enabled = #{enabled} , update_time = now() where res_id in(${res_ids})")
    public void updateEnabledByResId(@Param("enabled") Integer enabled, @Param("res_ids") String res_ids);

    @Update("update sys_resource set is_forbid = #{0} , update_time = now() where full_path like concat(#{1},'%')")
    public void updateForbidByFullPath(Integer is_forbid, String full_path);

    @Update("update sys_resource set is_forbid = #{is_forbid} , update_time = now() where res_id in(${res_ids})")
    public void updateForbidByResId(@Param("is_forbid") Integer is_forbid, @Param("res_ids") String res_ids);

    @Select("select * from sys_resource where org_id = #{0} and (is_forbid = #{1} or is_forbid is null) order by length(full_path)-length(replace(full_path,'|','')) asc, order_weight desc")
    public List<SysResource> selectByOrgIdAndForbid(long org_id, Integer is_forbid);

    //获取当前资源本身和下级资源
    @Select("select * from sys_resource where org_id = #{0} and full_path like concat(#{1},'%')")
    public List<SysResource> getOwnerResources(Long org_id, String full_path);

    @Select("select org_id from sys_organization where org_id != 1")
    List<Long> getOrgIds();
}

