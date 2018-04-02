package com.cjsz.tech.cms.provider;

import com.cjsz.tech.cms.beans.FindCatOrgBean;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/5.
 */
public class ArticleCatProvider {

    public String getOrgQuery(Map<String, Object> param) {
        FindCatOrgBean bean = (FindCatOrgBean)param.get("bean");
        String searchText = bean.getSearchText();
        Long project_id = bean.getProject_id();
        Long cat_id = bean.getArticle_cat_id();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select acor.article_cat_id,sp.project_id,sp.project_code,sp.project_name,so.org_id,so.org_name," +
                "soe.extend_code org_code,soe.short_name,acor.create_time,acor.is_delete from sys_org_extend soe " +
                "left join (select * from article_cat_org_rel where article_cat_id = " + cat_id + ") acor on soe.org_id = acor.org_id " +
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

    public String getOrgCat(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select r.rel_id,r.org_id,r.article_cat_id,r.order_weight,r.is_show,r.source_id,r.source_type, " +
                "r.create_time,r.update_time,r.is_delete,c.pid,c.article_cat_name,c.article_cat_path, " +
                "c.enabled,c.remark,c.cat_pic,c.org_count  from article_cat_org_rel r " +
                "left join article_cat c on r.article_cat_id = c.article_cat_id " );
        if(org_id == 1){
            sqlBuffer.append( " where c.is_delete = 2 and r.is_delete = 2 and r.org_id = 1 and c.org_id = 1");
        }
        else{
            sqlBuffer.append( " where c.is_delete = 2 and r.is_delete = 2 and ((r.org_id = #{org_id} and c.org_id != 1) or (r.org_id = #{org_id} and c.org_id = 1 and c.enabled = 1))");
        }
        sqlBuffer.append(" order by length(c.article_cat_path)-length(replace(c.article_cat_path,'|','')) asc, r.order_weight desc");
        return sqlBuffer.toString();
    }
}
