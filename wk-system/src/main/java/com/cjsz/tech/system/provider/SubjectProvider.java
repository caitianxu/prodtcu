package com.cjsz.tech.system.provider;

import com.cjsz.tech.system.beans.SubjectOrgListBean;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public class SubjectProvider {

    public String getList(Map<String,Object>param){
        String searchText = (String)param.get("searchText");
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select s.*,r.is_show,r.order_weight from subject_org_rel r left join subject_manage s " +
                         " on s.subject_id = r.subject_id where s.is_delete = 2 and r.is_delete =2 ");
        if (org_id == 1){
            sqlBuffer.append(" and r.org_id = " +org_id);
        }else{
            sqlBuffer.append(" and s.enabled = 1 and  r.org_id = " + org_id );
        }
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and s.subject_name like '%"+searchText+"%' ");
        }
        sqlBuffer.append(" order by r.order_weight desc");
        return sqlBuffer.toString();
    }

    public String orgList(Map<String, Object> param) {
        SubjectOrgListBean bean = (SubjectOrgListBean)param.get("bean");
        String searchText = bean.getSearchText();
        Long project_id = bean.getProject_id();
        Long subject_id = bean.getSubject_id();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select sor.subject_id,sp.project_id,sp.project_code,sp.project_name,so.org_id,so.org_name,\n" +
                "                soe.extend_code org_code,soe.short_name,sor.update_time,sor.is_delete from sys_org_extend soe \n" +
                "                left join (select * from subject_org_rel where subject_id = "+subject_id+ "  ) sor on soe.org_id = sor.org_id \n" +
                "                left join sys_organization so on soe.org_id = so.org_id \n" +
                "                left join sys_project sp on soe.project_code = sp.project_code \n" +
                "                where soe.is_delete = 2 and so.is_delete = 2 and so.enabled = 1 ");
        if(project_id != null){
            sqlBuffer.append( " and sp.project_id = " + project_id);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            sqlBuffer.append(" and so.org_name like '%" + searchText + "%'");
        }
        return sqlBuffer.toString();
    }
}
