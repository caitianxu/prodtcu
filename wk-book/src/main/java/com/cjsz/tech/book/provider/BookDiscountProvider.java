package com.cjsz.tech.book.provider;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class BookDiscountProvider {

    public String getRepoList(Map<String,Object> param){
        String searchText = (String)param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select bd.*,bp.book_name,bp.book_cover,bp.price ,bp.book_author from book_discount bd left join book_repo bp on" +
                         " bd.book_id = bp.book_id where channel_type = 3 ");
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and bp.book_name like '%"+searchText+"%' or " +
                             " bp.book_author like '%"+searchText+"%' ");
        }
        sqlBuffer.append(" order by bd.order_weight desc ");
        return sqlBuffer.toString();
    }

    public String getCJZWWList(Map<String,Object> param){
        String searchText = (String)param.get("searchText");
        Integer channel_type = (Integer)param.get("channel_type");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select bd.*,bp.book_name,bp.book_cover,bp.book_author from book_discount bd left join cjzww_books bp on" +
                " bd.book_id = bp.book_id where channel_type = " + channel_type );
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and bp.book_name like '%"+searchText+"%' or " +
                    " bp.book_author like '%"+searchText+"%' ");
        }
        sqlBuffer.append(" order by bd.order_weight desc ");
        return sqlBuffer.toString();
    }
}
