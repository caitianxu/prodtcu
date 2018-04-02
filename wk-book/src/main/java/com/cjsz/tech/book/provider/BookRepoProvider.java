package com.cjsz.tech.book.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class BookRepoProvider {

    //图书仓库图书查询
    public String getBookRepoList(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        Integer book_status = (Integer) param.get("book_status");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.*,group_concat(bt.tag_name) as tag_name from book_repo br left join book_tag_rel btr on btr.book_id = br.book_id " +
                         " left join book_tag bt on btr.tag_id = bt.tag_id where 1 = 1 ");
        if(book_status != null && book_status != -1){
            //1:上架;2:下架
            sqlBuffer.append(" and br.book_status = " + book_status);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or br.file_name like '%" + searchText + "%' " +
                    " or br.book_isbn like '%" + searchText +"%' " +
                    " or bt.tag_name like '%" + searchText +"%' ) ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" group by br.book_id ");
        return sqlBuffer.toString();
    }

    //图书移除标签的查询
    public String getInBookRepoList(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        Long tag_id = (Long) param.get("tag_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.*,GROUP_CONCAT(bt.tag_code) tag_names from book_repo br " +
                " left join book_tag_rel btr1 on btr1.book_id = br.book_id " +
                " left join book_tag bt on btr1.tag_id = bt.tag_id " +
                " where br.book_id in (select btr.book_id from book_tag_rel btr left join book_tag bbb on " +
                " btr.tag_id = bbb.tag_id where bbb.tag_path like '%" + tag_id + "%') ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or br.file_name like '%" + searchText + "%' " +
                    " or br.book_isbn like '%" + searchText +"%') ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY br.book_id");
        return sqlBuffer.toString();
    }

    //图书添加标签的查询(上架的图书)
    public String getNotInBookRepoList(Map<String, Object> param){
        String searchText = (String) param.get("searchText");
        Long tag_id = (Long) param.get("tag_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.*,GROUP_CONCAT(bt.tag_name) tag_names from book_repo br " +
                        " left join book_tag_rel btr1 on btr1.book_id = br.book_id " +
                        " left join book_tag bt on btr1.tag_id = bt.tag_id " +
                        " where br.book_id not in (select btr.book_id from book_tag_rel btr where btr.tag_id = " + tag_id + ") ");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or br.file_name like '%" + searchText + "%' " +
                    " or br.book_isbn like '%" + searchText +"%') ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY br.book_id");
        return sqlBuffer.toString();
    }
}