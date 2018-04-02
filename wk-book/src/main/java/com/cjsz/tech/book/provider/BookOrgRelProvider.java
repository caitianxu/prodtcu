package com.cjsz.tech.book.provider;

import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class BookOrgRelProvider {

    //查询数据包的图书列表
    public String getPkgBookList(Map<String, Object> param) {
        Long pkg_id = (Long) param.get("pkg_id");
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_name, br.book_author, br.book_cover, br.book_cover_small, br.book_isbn, bor.*, " +
                " GROUP_CONCAT(bt.tag_name) tag_names from book_repo br " +
                " left join book_tag_rel btr on btr.book_id = br.book_id " +
                " left join book_tag bt on btr.tag_id = bt.tag_id " +
                " left join book_org_rel bor on br.book_id = bor.book_id " +
                " left join book_cat bc on bc.book_cat_id = bor.book_cat_id " +
                " where bor.org_id = " + org_id + " and bc.org_id = " + org_id + " and bor.pkg_id = " + pkg_id + " and bc.pkg_id = " + pkg_id );
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and bc.book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or br.book_isbn like '%" + searchText +"%') ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY br.book_id,bc.book_cat_id");
        return sqlBuffer.toString();
    }

    //查询机构的图书列表
    public String getOrgBookList(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        String searchText = (String) param.get("searchText");
        Integer is_hot = (Integer) param.get("is_hot");
        Integer is_recommend = (Integer) param.get("is_recommend");
        Integer enabled = (Integer) param.get("enabled");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_name, br.book_author, br.book_cover,br.book_remark, br.book_cover_small, br.book_isbn, bor.*, " +
                " GROUP_CONCAT(bt.tag_name) tag_names from book_org_rel bor " +
                " left join book_repo br on br.book_id = bor.book_id " +
                " left join book_tag_rel btr on btr.book_id = br.book_id " +
                " left join book_tag bt on btr.tag_id = bt.tag_id " +
                " left join book_cat bc on bc.book_cat_id = bor.book_cat_id " +
                " where br.book_status = 1 and bor.org_id = " + org_id + " and bc.org_id = " + org_id );
        if(is_hot != null && is_hot != -1){
            sqlBuffer.append(" and bor.is_hot = " + is_hot);//是否热门(1:是;2:否)
        }
        if(is_recommend != null && is_recommend != -1){
            sqlBuffer.append(" and bor.is_recommend = " + is_recommend);//是否推荐(1:是;2:否)
        }
        if(enabled != null && enabled != -1){
            sqlBuffer.append(" and bor.enabled = " + enabled);//是否启用(1:是;2:否)
        }
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and bc.book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or bt.tag_name like '%" + searchText + "%') ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY bor.book_id");
        return sqlBuffer.toString();
    }


    public String getOrgOffLineBookList(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        Long device_id = (Long) param.get("device_id");
        String searchText = (String) param.get("searchText");
        Integer status = (Integer) param.get("status");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select DISTINCT * from (select br.book_name, br.book_author,group_concat(tag_name) as tag_name, br.book_cover, br.book_cover_small, br.book_isbn, bor.*, " +
                " bc.book_cat_path," + device_id + " as device_id, bdr.status " +
                " from book_org_rel bor " +
                " left join book_repo br  on br.book_id = bor.book_id " +
                " left join book_tag_rel btr on br.book_id = btr.book_id " +
                " left join book_tag bt on bt.tag_id = btr.tag_id " +
                " left join ( select * from  book_device_rel where org_id= " + org_id + " and device_id= " + device_id + " ) bdr on bor.book_id = bdr.book_id " +
                " left join (select * from book_cat where org_id= " + org_id );
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(") as bc on bc.book_cat_id = bor.book_cat_id  where br.book_status = 1 and bor.org_id = " + org_id );
        if(status != null && status != -1){
            if(status == 0){
                sqlBuffer.append(" and (bdr.status = 0 or bdr.status = 3) ");
            }else{
                sqlBuffer.append(" and bdr.status = " + status);
            }
        }
        sqlBuffer.append(" group by br.book_id ) aaa where book_cat_path is not null");
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (aaa.book_name like '%" + searchText + "%' " +
                    " or aaa.book_author like '%" + searchText + "%' " +
                    " or aaa.book_isbn like '%" + searchText +"%' " +
                    " or aaa.tag_name like '%" + searchText +"%' ) ";
            sqlBuffer.append(searchTextSql);
        }
        return sqlBuffer.toString();
    }


    public String getOrgOffLineBooks(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        Long device_id = (Long) param.get("device_id");
        String searchText = (String) param.get("searchText");
        Integer status = (Integer) param.get("status");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" select bdr.* ,b.book_name,b.book_author,b.book_cover,b.book_cover_small,b.book_isbn,bc.book_cat_path " +
                "from (select * from book_device_rel where org_id = "+org_id+"  and device_id = "+device_id+") bdr " +
                " left join book_repo b on bdr.book_id = b.book_id " +
                "left join (select * from book_cat where org_id = "+org_id+" ");
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" ) bc on bdr.book_cat_id = bc.book_cat_id  where 1=1");
        if(status != null && status != -1){
            if(status == 0){
                sqlBuffer.append(" and (bdr.status = 0 or bdr.status = 3) ");
            }else{
                sqlBuffer.append(" and bdr.status = " + status);
            }
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (b.book_name like '%" + searchText + "%' " +
                    " or b.book_author like '%" + searchText + "%' " +
                    " or b.book_isbn like '%" + searchText +"%') ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" group by bdr.book_id ");
        return sqlBuffer.toString();
    }




    //查询机构的图书列表
    public String sitePageQuery(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        String searchText = (String) param.get("searchText");
        String tag_name = (String) param.get("tag_name");
        String order_type = (String)param.get("order_type");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_name, br.book_author, br.book_cover,br.book_cover_small, br.book_isbn,br.book_remark, bor.*, " +
                " GROUP_CONCAT(bt.tag_name) tag_names from book_org_rel bor " +
                " left join book_repo br on br.book_id = bor.book_id " +
                " left join book_tag_rel btr on btr.book_id = br.book_id " +
                " left join book_tag bt on btr.tag_id = bt.tag_id " +
                " left join book_cat bc on bc.book_cat_id = bor.book_cat_id " +
                " left join (select * from book_index where org_id = "+org_id+" and book_type = 2 ) bi on bi.book_id = bor.book_id" +
                " where br.book_status = 1 and bor.enabled = 1 and bor.org_id = " + org_id + " and bc.org_id = " + org_id );
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and bc.book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        if(StringUtil.isNotEmpty(tag_name)){
            sqlBuffer.append("and bt.tag_name = '" + tag_name + "' ");
        }
        if (StringUtil.isNotEmpty(searchText)) {
            String searchTextSql = " and (br.book_name like '%" + searchText + "%' " +
                    " or br.book_author like '%" + searchText + "%' " +
                    " or br.book_isbn like '%" + searchText +"%') ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY bor.book_id");
        return sqlBuffer.toString();
    }

    //数据包图书全部基本信息
    public String getAllBooksBaseInfo(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_id, br.book_name, br.book_author, br.book_publisher from book_org_rel bor " +
                " left join book_repo br on br.book_id = bor.book_id " +
                " left join book_cat bc on bc.book_cat_id = bor.book_cat_id " +
                " where br.book_status = 1 and bor.org_id = " + org_id + " and bc.org_id = " + org_id );
        if (StringUtil.isNotEmpty(catPath)) {
            String searchTextSql = " and bc.book_cat_path like '" + catPath + "%' ";
            sqlBuffer.append(searchTextSql);
        }
        sqlBuffer.append(" GROUP BY bor.book_id");
        return sqlBuffer.toString();
    }



    public String getAppBooks(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String catPath = (String) param.get("catPath");
        String order = (String)param.get("order");
        String type = (String)param.get("type");
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select br.book_name,br.price, br.book_author,bc.book_cat_name, br.book_cover,br.book_cover_small, br.book_isbn,br.book_remark, bor.* ," +
                " 2 as book_type, bd.discount_price,bd.end_time from book_org_rel bor  " +
                " left join book_repo br on br.book_id = bor.book_id " +
                " left join book_cat bc on bc.book_cat_id = bor.book_cat_id " +
                " left join (select * from book_discount where  now() <= end_time and now() >= start_time) bd on bd.book_id = br.book_id where br.book_status = 1 and" +
                " bor.enabled = 1 and bor.org_id = " + org_id + " and bc.org_id = " + org_id);
        if (StringUtils.isNotEmpty(type)){
            if("free".equals(type)){
                sqlBuffer.append(" and bd.enabled = 1 and bd.channel_type =3 and bd.discount_price  = 0  ");
            }else if("discount".equals(type)){
                sqlBuffer.append(" and bd.enabled = 1 and bd.channel_type =3 and bd.discount_price != 0  ");
            }
        }
        if (StringUtil.isNotEmpty(catPath)) {
                String searchTextSql = " and bc.book_cat_path like '" + catPath + "%' ";
                sqlBuffer.append(searchTextSql);
        }
        if (StringUtil.isNotEmpty(searchText) && searchText != "") {
            String searchTextSql = " and br.book_name like '%" + searchText + "%' ";
            sqlBuffer.append(searchTextSql);
        }

        sqlBuffer.append(" GROUP BY bor.book_id  ");
        if ("new".equals(order)){
            sqlBuffer.append(" order by br.create_time desc ");
        }else if ("recommend".equals(order)){
            sqlBuffer.append(" order by bor.is_recommend asc,bor.order_weight desc ");
        }else if("hot".equals(order)){
            sqlBuffer.append("order by bor.is_hot asc , bor.order_weight desc ");
        }else{
            sqlBuffer.append(" order by bor.order_weight desc ");
        }

        return sqlBuffer.toString();
    }
}
