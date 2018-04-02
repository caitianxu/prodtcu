package com.cjsz.tech.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by LuoLi on 2017/4/1 0001.
 */
@Service
public class CmsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    //活动、资讯
    public List<Map<String, Object>> getCmsListByOrgIdAndCount(Long org_id, Integer count){
        String cms_sql = "select activity_id id,null cat_id,activity_title title,cover_url,cover_url_small,SUBSTR(create_time FROM 6 FOR 6)create_time,order_weight,\"activity\" type from activity where activity_status = 1 UNION " +
                "select article_id id,article_cat_id cat_id,article_title title,cover_url,cover_url_small,SUBSTR(create_time FROM 6 FOR 6)create_time,order_weight,\"article\" type from article " +
                "where article_cat_id in(select article_cat_id from article_cat_org_rel where org_id = " + org_id + " and is_delete = 2 and is_show = 1) and article_status = 1 " +
        "order by order_weight desc limit " + count;
        List<Map<String, Object>> cmsList = jdbcTemplate.queryForList(cms_sql);
        return cmsList;
    }

    public List<Map<String, Object>> getArtListByOrgIdAndCount(Long org_id, Integer count){
        String cms_sql = "select article_id id,article_type,article_content,article_cat_id cat_id,article_title title,cover_url,cover_url_small,SUBSTR(create_time FROM 6 FOR 6)create_time,order_weight,\"article\" type from article " +
                "where article_cat_id in(select article_cat_id from article_cat_org_rel where org_id = " + org_id + " and is_delete = 2 and is_show = 1) and article_status = 1 " +
                "order by order_weight desc limit " + count;
        List<Map<String, Object>> cmsList = jdbcTemplate.queryForList(cms_sql);
        return cmsList;
    }

}
