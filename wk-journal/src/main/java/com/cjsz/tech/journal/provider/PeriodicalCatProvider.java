package com.cjsz.tech.journal.provider;

import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.FindNewCatOrgBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/5.
 */
public class PeriodicalCatProvider {

    public String getCatsByOrgId(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select r.rel_id, r.org_id, r.periodical_cat_id , " +
                "r.order_weight, r.is_show, " +
                "r.create_time, r.update_time, r.is_delete, c.periodical_cat_pid , " +
                "c.periodical_cat_name , c.periodical_cat_path , " +
                "c.enabled, c.remark, c.org_count  " +
                "from periodical_cat_org_rel r " +
                "left join periodical_cat c on r.periodical_cat_id = c.periodical_cat_id " +
                "where c.is_delete = 2 and r.is_delete = 2 ");
        if (org_id == 1) {
            sqlBuffer.append(" and r.org_id = " + org_id);
        } else {
            sqlBuffer.append(" and c.enabled = 1 and r.org_id = " + org_id);
        }
        sqlBuffer.append(" order by length(c.periodical_cat_path)-length(replace(c.periodical_cat_path,'|','')) asc, r.order_weight desc");
        return sqlBuffer.toString();
    }

    public String getOrgQuery(Map<String, Object> param) {
        FindNewCatOrgBean bean = (FindNewCatOrgBean) param.get("bean");
        String searchText = bean.getSearchText();
        Long project_id = bean.getProject_id();
        Long cat_id = bean.getPeriodical_cat_id();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select jcor.periodical_cat_id ,sp.project_id,sp.project_code, " +
                "sp.project_name,so.org_id,so.org_name, " +
                "soe.extend_code org_code,soe.short_name, jcor.create_time, jcor.is_delete " +
                "from sys_org_extend soe " +
                "left join (select * from periodical_cat_org_rel " +
                "where periodical_cat_id = " + cat_id + ") jcor on soe.org_id = jcor.org_id " +
                "left join sys_organization so on soe.org_id = so.org_id " +
                "left join sys_project sp on soe.project_code = sp.project_code " +
                "where soe.is_delete = 2 and so.is_delete = 2 and so.enabled = 1 ");
        if (project_id != null) {
            sqlBuffer.append(" and sp.project_id = " + project_id);
        }
        if (StringUtils.isNotEmpty(searchText)) {
            sqlBuffer.append(" and so.org_name like '%" + searchText + "%'");
        }
        return sqlBuffer.toString();
    }



    public String getCatTree(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select r.rel_id, r.org_id, r.periodical_cat_id , " +
                "r.order_weight, r.is_show, " +
                "r.create_time, r.update_time, r.is_delete, c.periodical_cat_pid , " +
                "c.periodical_cat_name , c.periodical_cat_path , " +
                "c.enabled, c.remark, c.org_count  " +
                "from periodical_cat_org_rel r " +
                "left join periodical_cat c on r.periodical_cat_id = c.periodical_cat_id " +
                "where c.is_delete = 2 and r.is_delete = 2 and r.is_show =1 and c.enabled = 1 and r.org_id = " + org_id);

        return sqlBuffer.toString();
    }

}
