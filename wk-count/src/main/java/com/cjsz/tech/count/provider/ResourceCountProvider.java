package com.cjsz.tech.count.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/22.
 */
public class ResourceCountProvider {

    public String getCount(Map<String, Object> param) {
    	String year = (String) param.get("year");
    	Integer type = (Integer) param.get("resource_type");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT o.org_name, count(r.res_count_id) as num"
    				+ " FROM resource_count r "
    				+ " LEFT JOIN sys_organization o on r.org_id = o.org_id "
    				+ " where 1=1");
        if(StringUtil.isNotEmpty(year)){
        	String yearSql = " and r.create_time BETWEEN CONCAT(" + year + " ,'-1-1 00:00:00') AND CONCAT(" 
        			+ year + ",'-12-31 23:59:59')";
        	sqlBuffer.append(yearSql);
        }
        if(type != null){
        	String typeSql = " and r.resource_type = " + type;
        	sqlBuffer.append(typeSql);
        }
        sqlBuffer.append(" and o.is_delete = 2 and o.enabled = 1 GROUP BY r.org_id ORDER BY r.org_id limit 10");
        return sqlBuffer.toString();
    }

    public String getArticleCount(Map<String, Object> param){
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("select count(*) from article where  is_delete = 2 and article_status = 1 " +
                "and article_cat_id in (select DISTINCT article_cat_id from article_cat where enabled = 1)");
        return sqlBuffer.toString();
    }

    public String getNewspaperCount(Map<String, Object> param){
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from newspaper where 1=1 ");
        /*if(org_id != null){
            if(org_id != 1){
                String orgSql = " and org_id = " + org_id;
                sqlBuffer.append(orgSql);
            }
        }*/
        return sqlBuffer.toString();
    }

    public String getJournalCount(Map<String, Object> param){
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from journal where 1=1 and journal_status = 1");
        /*if(org_id != null){
            if(org_id != 1){
                String orgSql = " and org_id = " + org_id;
                sqlBuffer.append(orgSql);
            }
        }*/
        return sqlBuffer.toString();
    }

    public String getVideoCount(Map<String, Object> param){
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from video where 1=1");
        /*if(org_id != null){
            if(org_id != 1){
                String orgSql = " and org_id = " + org_id;
                sqlBuffer.append(orgSql);
            }
        }*/
        return sqlBuffer.toString();
    }

    public String getAudioCount(Map<String, Object> param){
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from audio where 1=1");
        /*if(org_id != null){
            if(org_id != 1){
                String orgSql = " and org_id = " + org_id;
                sqlBuffer.append(orgSql);
            }
        }*/
        return sqlBuffer.toString();
    }
    
    public String getIndexMemberCount(Map<String, Object> param){
    	Long org_id = (Long)param.get("org_id");
    	StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from member where 1=1 and enabled = 1 and is_delete = 2");
        /*if(org_id != null){
        	if(org_id != 1){
        		String orgSql = " and org_id = " + org_id;
        		sqlBuffer.append(orgSql);
        	}
        }*/
        return sqlBuffer.toString();
    }
    
    public String getIndexDeviceCount(Map<String, Object> param){
    	Long org_id = (Long)param.get("org_id");
    	StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from device where 1=1 and enabled = 1");
        /*if(org_id != null){
        	if(org_id != 1){
        		String orgSql = " and org_id = " + org_id;
        		sqlBuffer.append(orgSql);
        	}
        }*/
        return sqlBuffer.toString();
    }
    
    public String getIndexBookCount(Map<String, Object> param){
    	Long org_id = (Long)param.get("org_id");
    	StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select count(*) from book_repo where 1=1 and book_status = 1");
        /*if(org_id != null){
        	if(org_id != 1){
        		String orgSql = " and org_id = " + org_id;
        		sqlBuffer.append(orgSql);
        	}
        }*/
        return sqlBuffer.toString();
    }
}
