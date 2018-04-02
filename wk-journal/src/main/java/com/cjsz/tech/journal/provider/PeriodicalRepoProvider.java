package com.cjsz.tech.journal.provider;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/5.
 */
public class PeriodicalRepoProvider {

    public String getAllPeriodicalList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select pr.*, pc.periodical_cat_name, pc.periodical_cat_path " +
                "from  periodical_repo pr " +
                "LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
                " where 1=1 ");
        if (StringUtils.isNotEmpty(searchText)) {
            String searchTextSql = " and pr.periodical_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtils.isNotEmpty(catPath)) {
            if (catPath.equals("-1")){
                sqlBuffer.append("and pr.periodical_cat_id is null");
            }else {
                String catSql = " and pc.periodical_cat_path like '" + catPath + "%'";
                sqlBuffer.append(catSql);
            }
        }
        sqlBuffer.append(" order by pr.periodical_name desc");
        return sqlBuffer.toString();
    }

    public String getOrgPeriodicalList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select DISTINCT pr.*, pc.periodical_cat_name, pc.periodical_cat_path " +
                "from periodical_repo pr " +
                "LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
                "LEFT JOIN periodical_cat_org_rel cor ON  cor.periodical_cat_id = pc.periodical_cat_id " +
                "WHERE cor.is_delete=2 and pc.enabled=1 ");
        if(null != org_id){
            String orgSql = " and cor.org_id =" + org_id;
            sqlBuffer.append(orgSql);
        }
        if (StringUtils.isNotEmpty(searchText)) {
            String searchTextSql = " and pr.periodical_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtils.isNotEmpty(catPath)) {
            if (catPath.equals("-1")){
                sqlBuffer.append("and pr.periodical_cat_id is  null");
            }else {
                String catSql = " and pc.periodical_cat_path like '" + catPath + "%'";
                sqlBuffer.append(catSql);
            }
        }
        sqlBuffer.append(" order by pr.periodical_name desc");
        return sqlBuffer.toString();
    }

    public String getWebPeriodicalList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT pr.*, pc.periodical_cat_name, pc.periodical_cat_path " +
                "from periodical_repo pr " +
                "LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
                "LEFT JOIN periodical_cat_org_rel cor ON  cor.periodical_cat_id = pc.periodical_cat_id " +
                "WHERE cor.is_delete=2 and pc.enabled=1 and cor.is_show = 1 and pr.enabled = 1");
        if(null != org_id){
            String orgSql = " and cor.org_id =" + org_id;
            sqlBuffer.append(orgSql);
        }
        if (StringUtils.isNotEmpty(searchText)) {
            String searchTextSql = " and pr.series_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtils.isNotEmpty(catPath)) {
            if (catPath.equals("-1")){
                sqlBuffer.append("and pr.periodical_cat_id is null");
            }else {
                String catSql = " and pc.periodical_cat_path like '" + catPath + "%'";
                sqlBuffer.append(catSql);
            }
        }
        sqlBuffer.append(" order by pr.periodical_name desc )a group by series_name ");
        return sqlBuffer.toString();
    }

    public String getPeriodicalList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT pr.*, pc.periodical_cat_name, pc.periodical_cat_path " +
                "from periodical_repo pr " +
                "LEFT JOIN periodical_cat pc ON pr.periodical_cat_id = pc.periodical_cat_id " +
                "LEFT JOIN periodical_cat_org_rel cor ON  cor.periodical_cat_id = pc.periodical_cat_id " +
                "WHERE 1=1  ");
        if(null != org_id){
            if (org_id != 1) {
                String orgSql = " and cor.is_delete=2 and pc.enabled=1 and cor.is_show = 1 and cor.org_id =" + org_id;
                sqlBuffer.append(orgSql);
            }
        }
        if (StringUtils.isNotEmpty(searchText)) {
            String searchTextSql = " and pr.series_name like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtils.isNotEmpty(catPath)) {
            if (catPath.equals("-1")){
                sqlBuffer.append("and pr.periodical_cat_id is null");
            }else {
                String catSql = " and pc.periodical_cat_path like '" + catPath + "%'";
                sqlBuffer.append(catSql);
            }
        }
        sqlBuffer.append(" order by pr.periodical_name desc )a group by series_name ");
        return sqlBuffer.toString();
    }

}
