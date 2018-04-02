package com.cjsz.tech.book.provider;

import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class PkgBookRelProvider {

    //查询数据包的图书列表
    public String getPkgBookList(Map<String, Object> param) {
        Long pkg_id = (Long) param.get("pkg_id");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        String searchText = (String) param.get("searchText");
        Integer is_hot = (Integer) param.get("is_hot");
        Integer is_recommend = (Integer) param.get("is_recommend");
        Integer offline_status = (Integer) param.get("offline_status");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_name, br.book_author, br.book_cover, br.book_cover_small, br.book_isbn, pbr.*, bc.book_cat_name, " +
                " GROUP_CONCAT(bt.tag_name) tag_names from book_repo br " +
                " left join book_tag_rel btr on btr.book_id = br.book_id " +
                " left join book_tag bt on btr.tag_id = bt.tag_id " +
                " left join pkg_book_rel pbr on br.book_id = pbr.book_id " +
                " left join pkg_book_cat bc on bc.book_cat_id = pbr.book_cat_id " +
                " where pbr.org_id = " + org_id + " and bc.org_id = " + org_id + " and pbr.pkg_id = " + pkg_id + " and bc.pkg_id = " + pkg_id );
        if(is_hot != null && is_hot != -1){
            sqlBuffer.append(" and pbr.is_hot = " + is_hot);//是否热门(1:是;2:否)
        }
        if(is_recommend != null && is_recommend != -1){
            sqlBuffer.append(" and pbr.is_recommend = " + is_recommend);//是否推荐(1:是;2:否)
        }
        if(offline_status != null && offline_status != -1){
            sqlBuffer.append(" and pbr.offline_status = " + offline_status);//0:不离线;1:发送离线
        }
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and bc.book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or br.book_isbn like '%" + searchText +"%' " +
                    " or bt.tag_name like '%" + searchText +"%' ) ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY br.book_id,bc.book_cat_id");
        return sqlBuffer.toString();
    }


    //数据包图书全部基本信息
    public String getAllBooksBaseInfo(Map<String, Object> param) {
        Long pkg_id = (Long) param.get("pkg_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_id, br.book_name, br.book_author, br.book_publisher,pbc.book_cat_name from pkg_book_rel pbr " +
                " left join pkg_book_cat pbc on pbc.book_cat_id = pbr.book_cat_id " +
                " left join book_repo br on br.book_id = pbr.book_id " +
                " where pbr.pkg_id = " + pkg_id + " and pbc.pkg_id = " + pkg_id );
        sqlBuffer.append(" and br.book_id is not null GROUP BY br.book_id");
        return sqlBuffer.toString();
    }
}
