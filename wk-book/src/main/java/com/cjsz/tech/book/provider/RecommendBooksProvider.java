package com.cjsz.tech.book.provider;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/6 0006.
 */
public class RecommendBooksProvider {

    public String getBookList(Map<String, Object> map){
        Long recommend_cat_id = (Long)map.get("recommend_cat_id");
        String searchText = (String) map.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from recommend_books where recommend_cat_id = "+recommend_cat_id );
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and book_name like '%" + searchText + "%' or" +
                             "     book_author like '%" + searchText + "%' "  );
        }
        sqlBuffer.append(" order by order_weight desc ");
        return sqlBuffer.toString();
    }


    public String getAllBooks(Map<String,Object> param) {
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from cjzww_books  where 1=1");
        if (StringUtils.isNotEmpty(searchText)) {
            sqlBuffer.append(" and book_author like '%" + searchText + "%' or " +
                             "     book_name like '%" + searchText + "%' ");
        }
        sqlBuffer.append(" order by book_name asc ");
        return sqlBuffer.toString();
    }
}
