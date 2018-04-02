package com.cjsz.tech.journal.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Author:Jason
 * Date:2016/12/6
 */
public class NewspaperProvider {

    public String pageQuery(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT a.*,c.newspaper_cat_name,c.newspaper_cat_path from  newspaper a " +
                "LEFT JOIN newspaper_cat c ON a.org_id=c.org_id and a.newspaper_cat_id=c.newspaper_cat_id " +
                "LEFT JOIN newspaper_cat_org_rel r ON a.org_id=r.org_id " +
                "WHERE a.is_delete = 2 and a.newspaper_cat_id in(SELECT newspaper_cat_id FROM newspaper_cat_org_rel " +
                "WHERE org_id= "+org_id+" and is_delete=2  )  " );
        if (org_id != 1){
            sqlBuffer.append(" and c.enabled=1");
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and a.paper_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.newspaper_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(") x1 group by newspaper_id,org_id,user_id,paper_name,paper_url,paper_img_small,paper_img,order_weight,create_time,update_time order by order_weight desc");
        return sqlBuffer.toString();

    }

    public String pageSiteQuery(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT v.*,u.user_name from newspaper v " +
                " left join newspaper_cat c on c.newspaper_cat_id = v.newspaper_cat_id " +
                " left join newspaper_cat_org_rel r on r.newspaper_cat_id = c.newspaper_cat_id " +
                " left join sys_user u on u.user_id = v.user_id " +
                " where v.is_delete = 2 and (v.org_id = 1 or v.org_id = " + org_id + ") and r.org_id = " + org_id );
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.newspaper_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(" and c.enabled = 1 and c.is_delete = 2 and r.is_show = 1 and r.is_delete = 2");
        sqlBuffer.append(" order by v.order_weight desc ");
        return sqlBuffer.toString();
    }
}
