package com.cjsz.tech.media.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/5.
 */
public class VideoProvider {

    public String pageQuery(Map<String, Object> param) {
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT a.*,c.video_cat_name,c.video_cat_path from  video a " +
                " LEFT JOIN video_cat c ON a.org_id=c.org_id  and a.video_cat_id=c.video_cat_id " +
                " LEFT JOIN video_cat_org_rel r ON a.org_id=r.org_id " +
                " WHERE a.is_delete = 2 and a.video_cat_id in(SELECT video_cat_id FROM video_cat_org_rel " +
                " WHERE org_id="+org_id+" and is_delete=2 ) and c.enabled=1  ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and a.video_title like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.video_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(" ) x1 " +
                "   group by video_id,org_id,user_id,video_title,cover_url,cover_url_small,video_url,video_remark,order_weight,create_time,update_time");
        return sqlBuffer.toString();
    }

    public String pageSiteQuery(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT v.*,u.user_name from video v " +
                " left join video_cat c on c.video_cat_id = v.video_cat_id " +
                " left join video_cat_org_rel r on r.video_cat_id = c.video_cat_id " +
                " left join sys_user u on u.user_id = v.user_id " +
                " where v.is_delete =2 and  (v.org_id = 1 or v.org_id = " + org_id + ") and r.org_id = " + org_id );
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.video_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(" and c.enabled = 1 and c.is_delete = 2 and r.is_show = 1 and r.is_delete = 2");
        return sqlBuffer.toString();
    }
}
