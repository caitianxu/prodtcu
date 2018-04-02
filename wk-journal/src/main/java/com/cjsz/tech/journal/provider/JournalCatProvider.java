package com.cjsz.tech.journal.provider;

import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/5.
 */
public class JournalCatProvider {

    public String getOrgQuery(Map<String, Object> param) {
        FindCatOrgBean bean = (FindCatOrgBean)param.get("bean");
        String searchText = bean.getSearchText();
        Long project_id = bean.getProject_id();
        Long cat_id = bean.getJournal_cat_id();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select jcor.journal_cat_id,sp.project_id,sp.project_code,sp.project_name,so.org_id,so.org_name," +
                "soe.extend_code org_code,soe.short_name,jcor.create_time,jcor.is_delete from sys_org_extend soe " +
                "left join (select * from journal_cat_org_rel where journal_cat_id = " + cat_id + ") jcor on soe.org_id = jcor.org_id " +
                "left join sys_organization so on soe.org_id = so.org_id " +
                "left join sys_project sp on soe.project_code = sp.project_code " +
                "where soe.is_delete = 2 and so.is_delete = 2 and so.enabled = 1 ");
        if(project_id != null){
            sqlBuffer.append( " and sp.project_id = " + project_id);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            sqlBuffer.append(" and so.org_name like '%" + searchText + "%'");
        }
        return sqlBuffer.toString();
    }

}
