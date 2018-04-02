package com.cjsz.tech.media.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/7.
 */
public class AudioProvider {

    public String pageQuery(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select DISTINCT a.*,c.audio_cat_name ,c.audio_cat_path from  audio a " +
                " LEFT JOIN audio_cat c ON a.org_id=c.org_id  and a.audio_cat_id=c.audio_cat_id" +
                " LEFT JOIN audio_cat_org_rel r ON a.org_id=r.org_id " +
                " WHERE a.is_delete = 2 and  a.audio_cat_id in(SELECT audio_cat_id FROM audio_cat_org_rel " +
                " WHERE org_id="+org_id+" and is_delete=2 ) and c.enabled=1 ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and a.audio_title like '%" + searchText + "%'";
            sqlBuffer.append(searchTextSql);
        }
        //catPath为null或者""时：查询全部
        if(StringUtil.isNotEmpty(catPath)) {
            String catSql = " and c.audio_cat_path like '" + catPath + "%'";
            sqlBuffer.append(catSql);
        }
        sqlBuffer.append(" ) x1 " +
                "  group by audio_id,org_id,user_id,audio_title,cover_url,cover_url_small,audio_url,audio_remark,order_weight,create_time,update_time");
        return sqlBuffer.toString();
    }
}
