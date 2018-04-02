package com.cjsz.tech.journal.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public class JournalProvider {

    public String getJournalList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT a.*,c.journal_cat_name,c.journal_cat_path from  journal a " +
                "LEFT JOIN journal_cat c ON a.org_id=c.org_id and a.journal_cat_id=c.journal_cat_id " +
                "LEFT JOIN journal_cat_org_rel r ON a.org_id=r.org_id " +
                "WHERE a.journal_cat_id in(SELECT journal_cat_id FROM journal_cat_org_rel " +
                "WHERE org_id= "+org_id+" and is_delete=2 ) and c.enabled=1  " );
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and a.journal_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.journal_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(") x1 group by journal_id,org_id,user_id,journal_name,journal_cover,journal_cover_small,order_weight,create_time,update_time order by order_weight desc");
        return sqlBuffer.toString();
    }


    public String sitePageQuery(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT j.*,u.user_name from journal j " +
                " left join journal_cat jc on jc.journal_cat_id = j.journal_cat_id " +
                " left join sys_user u on u.user_id = j.user_id " +
                " where j.journal_status = 1 and j.org_id = " + org_id);
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and jc.journal_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        return sqlBuffer.toString();
    }

    public String getOffLineNumList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select  * from journal a left join journal_cat c on a.org_id=c.org_id " +
                "WHERE a.journal_cat_id in (select journal_cat_id from journal_cat_org_rel WHERE org_id="+org_id+" " +
                "and is_delete = 2) and c.enabled = 1 and c.is_delete =2");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and a.journal_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.journal_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(" order by order_weight desc");
        sqlBuffer.append("");
        return sqlBuffer.toString();
    }
}
