package com.cjsz.tech.dev.mapper;

import java.util.List;

import com.cjsz.tech.dev.beans.DeviceFeedbackBean;
import com.cjsz.tech.dev.beans.MessageBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.provider.DeviceFeedbProvider;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceFeedbackMapper extends BaseMapper<DeviceFeedback> {

	@SelectProvider(type = DeviceFeedbProvider.class, method = "selectAll")
	List<DeviceFeedback> getList(@Param("keyword") String keyword, @Param("org_id") Long org_id);

	@Select("select * from device_feedback where user_id = #{0}")
	List<DeviceFeedback> getMemberList(Long member_id);

	@Select("select df.*,m.icon,m.nick_name from device_feedback df left join (select * from tb_member group by member_id) m" +
			" on df.user_id = m.member_id where df.user_id = #{0}")
	List<DeviceFeedbackBean> getMemberMessageList(Long member_id);

	@SelectProvider(type = DeviceFeedbProvider.class,method = "getListByOrgAndDept")
    List<DeviceFeedback> getListByOrgAndDept(@Param("searchText") String searchText,@Param("org_id") Long org_id, @Param("dept_id") Long dept_id);

    @SelectProvider(type = DeviceFeedbProvider.class,method = "getListByOrg")
    List<DeviceFeedback> getListByOrg(@Param("bean") MessageBean bean, @Param("org_id") Long org_id);
}
