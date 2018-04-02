package com.cjsz.tech.cms.provider;

import com.github.pagehelper.StringUtil;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 查询资讯列表
 * Created by Administrator on 2016/11/22 0022.
 */
public class ArticleProvider {

    public String getArticleList(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT a.*,c.article_cat_name,c.article_cat_path from  article a " +
                "LEFT JOIN article_cat c ON a.org_id=c.org_id and a.article_cat_id=c.article_cat_id " +
                "LEFT JOIN article_cat_org_rel r ON a.org_id=r.org_id " +
                "WHERE a.article_cat_id in(SELECT article_cat_id FROM article_cat_org_rel " +
                "WHERE org_id= "+org_id+" and is_delete=2) and c.enabled=1 and a.is_delete = 2 " );
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and a.article_title like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.article_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(") x1 group by article_id,org_id,user_id,article_title,cover_url,cover_url_small,article_remark,order_weight,create_time,update_time order by order_weight desc");
        return sqlBuffer.toString();
    }

    public String sitePageQuery(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT a.*,u.user_name from article a " +
                " left join article_cat ac on ac.article_cat_id = a.article_cat_id " +
                " left join article_cat_org_rel r on r.article_cat_id = ac.article_cat_id " +
                " left join sys_user u on u.user_id = a.user_id " +
                " where a.is_delete = 2 and a.article_status = 1 and (a.org_id = 1 or a.org_id = " + org_id + ") and r.org_id = " + org_id );
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and ac.article_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(" and ac.enabled = 1 and ac.is_delete = 2 and r.is_show = 1 and r.is_delete = 2");
        return sqlBuffer.toString();
    }

}
