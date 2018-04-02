package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.OrgExtend;
import com.cjsz.tech.system.mapper.OrgExtendMapper;
import com.cjsz.tech.system.service.OrgExtendService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 机构扩展
 * Created by Administrator on 2016/10/25.
 */
@Service
public class OrgExtendServiceImpl implements OrgExtendService{
	
    @Autowired
    private OrgExtendMapper orgExtendMapper;

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "机构拓展")
	public void saveExtend(OrgExtend extend) {
		/*String extend_code = DateUtils.dateFormatThreadLocal.get().format(new Date());
		extend.setExtend_code(extend_code);
		extend.setIs_delete(Constants.IS_DELETE);*/
		orgExtendMapper.insert(extend);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "机构拓展")
	public void updateExtend(OrgExtend extend) {
		orgExtendMapper.updateByPrimaryKey(extend);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "机构拓展")
	public void deleteById(Long extend_id) {
		orgExtendMapper.deleteByPrimaryKey(extend_id);		
	}

	@Override
	public OrgExtend selectById(Long extend_id) {
		return orgExtendMapper.selectByPrimaryKey(extend_id);
	}

	@Override
	public OrgExtend getByOrgId(Long org_id){
		return orgExtendMapper.getByOrgId(org_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "机构拓展")
	public void updateIsDelete(Long org_id){
		orgExtendMapper.updateIsDelete(org_id);
	}

	@Override
	public OrgExtend getByShortName(String short_name) {
		return orgExtendMapper.getByShortName(short_name);
	}

	@Override
	public List<OrgExtend> selectByProjectCode(String project_code) {
		return orgExtendMapper.selectByProjectCode(project_code);
	}
}
