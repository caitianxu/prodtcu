package com.cjsz.tech.journal.provider;



import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by LuoLi on 2017/6/2 0002.
 */
public class NewspaperCat2Provider {

    public String getOrgQuery(Map<String, Object> param) {
        FindCatOrgBean bean = (FindCatOrgBean)param.get("bean");
        String searchText = bean.getSearchText();
        Long project_id = bean.getProject_id();
        Long cat_id = bean.getNewspaper_cat_id();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select ncor.newspaper_cat_id,sp.project_id,sp.project_code,sp.project_name,so.org_id,so.org_name," +
                "soe.extend_code org_code,soe.short_name,ncor.create_time,ncor.is_delete from sys_org_extend soe " +
                "left join (select * from newspaper_cat_org_rel where newspaper_cat_id = " + cat_id + ") ncor on soe.org_id = ncor.org_id " +
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
