package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.AllortedOrgBean;
import com.cjsz.tech.book.domain.PkgOrgRel;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface PkgOrgRelMapper extends BaseMapper<PkgOrgRel> {

    //删除数据包机构关系
    @Delete("delete from pkg_org_rel where org_id = #{0}")
    void deleteByOrgId(Long org_id);

    //数据包分配给机构的机构列表
    @Select("select soe.extend_code org_code,soe.short_name,so.org_name,por.create_time from pkg_org_rel por " +
            " left join sys_organization so on(por.org_id = so.org_id) " +
            " left join sys_org_extend soe on(soe.org_id = so.org_id) " +
            " where por.pkg_id = #{0}")
    List<AllortedOrgBean> getAllortOrgs(Long pkg_id);

    //删除数据包机构关系
    @Delete("delete from pkg_org_rel where pkg_id in(${pkgIds})")
    void deleteByPkgIds(@Param("pkgIds") String pkgIds);

    //通过机构查询机构数据包关系
    @Select("select * from pkg_org_rel where org_id = #{0}")
    public PkgOrgRel selectByOrgId(Long org_id);

}
