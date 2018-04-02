package com.cjsz.tech.system.mapper;


import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.OrgExtend;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface OrgExtendMapper extends BaseMapper<OrgExtend> {

	@Select("select * from sys_org_extend where org_id = #{0} and is_delete = 2")
	OrgExtend getByOrgId(Long org_id);

	@Update("update sys_org_extend set is_delete = 1 where org_id = #{0}")
	void updateIsDelete(Long org_id);

	@Select("select * from sys_org_extend where short_name = #{0} and is_delete = 2")
	OrgExtend getByShortName(String short_name);

	@Select("select * from sys_org_extend where project_code = #{0}")
	List<OrgExtend> selectByProjectCode(String project_code);
}
