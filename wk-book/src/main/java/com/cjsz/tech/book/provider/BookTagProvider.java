package com.cjsz.tech.book.provider;

import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class BookTagProvider {

    public String getBookTagList(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select bt.*,(select count(*) from book_tag_rel btr where bt.tag_id = btr.tag_id) book_count from book_tag bt where 1 = 1 ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and bt.tag_name like '%" + searchText + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        return sqlBuffer.toString();
    }

    public String getNoTagList(Map<String,Object> param){
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from book_repo where book_id not in(select book_id from book_tag_rel GROUP BY book_id) and parse_status = 3 ");
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and book_name like '%"+searchText+"%'");
        }
        return sqlBuffer.toString();
    }
}
