package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.beans.WebOrgExtendBean;
import com.cjsz.tech.system.domain.ProOrgExtend;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 项目设置
 * Created by LuoLi on 2017/3/23 0023.
 */
public interface ProOrgExtendMapper extends BaseMapper<ProOrgExtend> {

    @Select("select * from pro_org_extend")
    public List<WebOrgExtendBean> getList();

    @Select("select * from pro_org_extend where org_name like concat('%',#{0},'%')")
    public List<WebOrgExtendBean> getListBySearchText(String searchText);

    @Delete("delete from pro_org_extend where pro_org_extend_id in(${ids})")
    public void deleteByIds(@Param("ids") String ids);

    @Select("select * from pro_org_extend where pro_org_extend_id = #{0}")
    public ProOrgExtend selectById(Long pro_org_extend_id);

    @Select("select distinct so.org_id,so.org_name from sys_org_extend soe left join sys_organization so on so.org_id = soe.org_id where so.is_delete = 2 and soe.project_code = #{0} ")
    public List<OrganizationBean> getProOrgList(String project_code);

    @Select("select * from pro_org_extend where server_name = #{0} limit 1")
    public ProOrgExtend selectByServerName(String server_name);

    @Select("select * from pro_org_extend where org_id = #{0}  limit 1")
    ProOrgExtend selectByOrgId(Long org_id);

    @Update("update pro_org_extend set pro_code = #{0} , pro_name = #{1},org_name = #{3} where org_id = #{2}")
    void updateProjectByOrgId(String project_code, String project_name,Long org_id,String org_name);

    @Update("update pro_org_extend set pro_name = #{1} where pro_code = #{0}")
    void updateProName(String project_code, String project_name);

    @Update("update pro_org_extend set enabled = #{1},update_time = now() where pro_org_extend_id = #{0}")
    void updateEnabeld(Long pro_org_extend_id, Integer enabled);

    @Select("select t.temple_name from sys_temple t left join pro_org_extend pr on pr.temple_id = t.temple_id" +
            " where pr.server_name = #{0} and pr.org_id = #{1}")
    String getTemple(String server_name, String org_id);
}
