package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.AppBooksBean;
import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.BookInfo;
import com.cjsz.tech.book.beans.PublicBookBean;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.provider.BookOrgRelProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface BookOrgRelMapper extends BaseMapper<BookOrgRel> {

    //删除机构的图书表
    @Delete("delete from book_org_rel where org_id = #{0}")
    public void deleteByOrgId(Long org_id);

    //分页查询机构的图书列表
    @SelectProvider(type = BookOrgRelProvider.class, method = "getOrgBookList")
    List<BookBean> getOrgBookList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText,
                                  @Param("is_hot") Integer is_hot, @Param("is_recommend") Integer is_recommend, @Param("enabled") Integer enabled);

    //分页查询机构的图书列表
    @SelectProvider(type = BookOrgRelProvider.class, method = "sitePageQuery")
    List<BookBean> sitePageQuery(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText, @Param("tag_name") String tag_name,@Param("order_type")String order_type);

    //分页查询机构的图书离线列表
    @SelectProvider(type = BookOrgRelProvider.class, method = "getOrgOffLineBookList")
    List<BookBean> getOrgOffLineBookList(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("status") Integer status,
                                         @Param("device_id") Long device_id, @Param("searchText") String searchText);

    //数据包图书、机构图书   排序置顶
    @Update("update book_org_rel set order_weight = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateOrderTop(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Long order_weight);

    //根据联合主键查询关系数据
    @Select("select * from book_org_rel where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3} ")
    public BookOrgRel selectBookOrgRelByUnionIds(Long book_id, Long org_id, Long book_cat_id, Long pkg_id);

    //机构图书 是否热门
    @Update("update book_org_rel set is_hot = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateRelHot(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Integer is_hot);

    //机构图书 是否推荐
    @Update("update book_org_rel set is_recommend = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateRelRecommend(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Integer is_recommend);

    //机构图书 是否离线
    @Update("update book_org_rel set offline_status = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateOffLine(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Integer offline_status);

    //查询机构图书详情
    @Select("select * from book_org_rel where org_id = #{orgid} and book_cat_id in(${catids})")
    public List<BookOrgRel> selectOrgRels(@Param("orgid") Long orgid, @Param("catids") String catids);

    //删除图书机构关系数据
    @Delete("delete from book_org_rel where org_id = #{orgid} and book_cat_id in(${catids})")
    public void deleteOrgBookOrgRel(@Param("orgid") Long orgid, @Param("catids") String catids);

    //查询机构图书id为book_id的图书集合（不同分类下的同一图书）
    @Select("select * from book_org_rel where org_id = #{0} and book_id = #{1} ")
    List<BookOrgRel> selectOrgBooksByBookId(Long orgid, Long book_id);

    //查询机构所有分类下的某一书
    @Select("select * from book_org_rel where org_id = #{0} and book_id = #{1}")
    List<BookOrgRel> selectOrgRelsByBookId(Long org_id, Long bkid);

    @Select("select DISTINCT * from (select br.book_id,br.book_cover,br.book_cover_small,br.book_name,br.book_author,br.book_publisher from book_org_rel bor " +
            "left join book_repo br on bor.book_id = br.book_id " +
            "where br.book_status = 1 and bor.org_id = #{0} and bor.enabled = 1 " +
            "order by bor.collect_count desc,bor.is_hot asc,bor.order_weight desc)aaa limit #{1}")
    public List<Map<String,Object>> selectHotListByOrgIdAndCount(Long org_id, Integer count);

    @Select("select br.book_id,bor.book_cat_id,br.book_cover,br.book_cover_small,br.book_remark,br.book_name,br.book_author,br.book_publisher from book_org_rel bor " +
            "left join book_repo br on bor.book_id = br.book_id " +
            "where br.book_status = 1 and bor.org_id = #{0} and bor.enabled = 1 and bor.book_cat_id = #{1} " +
            "order by bor.is_recommend asc,bor.order_weight desc limit #{2}")
    public List<Map<String,Object>> selectRecommendListByOrgIdAndCatIdAndCount(Long org_id, Long book_cat_id, Integer count);

    @Select("select br.book_id,bor.book_cat_id,br.book_cover,br.book_cover_small,br.book_remark,br.book_name,br.book_author,br.book_publisher from book_org_rel bor " +
            "left join book_repo br on bor.book_id = br.book_id left join book_cat bc on bor.book_cat_id = bc.book_cat_id " +
            "where br.book_status = 1 and bor.org_id = #{0} and bor.enabled = 1 and bc.book_cat_path like concat(#{1},'%') " +
            "group by br.book_id order by bor.is_recommend asc,bor.order_weight desc limit #{2}")
    public List<Map<String,Object>> selectRecommendListByOrgIdAndCatId(Long org_id, String book_cat_path, Integer count);

    @Select("select br.book_name, br.book_url,br.book_remark,br.publish_time, br.book_publisher, br.book_author, br.book_cover, br.book_cover_small, br.book_isbn,bs.shelf_id, bor.*," +
            "    GROUP_CONCAT(DISTINCT bt.tag_name) tag_names,GROUP_CONCAT(DISTINCT bc.book_cat_name) book_cat_names,bc.book_cat_id from book_org_rel bor" +
            "    left join book_repo br on br.book_id = bor.book_id" +
            "    left join book_tag_rel btr on btr.book_id = br.book_id" +
            "    left join book_tag bt on btr.tag_id = bt.tag_id" +
            "    left join book_cat bc on (bc.book_cat_id = bor.book_cat_id and bc.org_id = #{0})" +
            "    left join (select * from book_shelf where is_delete = 2 and user_id = #{2} ) bs on bs.bk_id = br.book_id" +
            "    where bor.book_id = #{1} and bor.org_id = #{0} GROUP BY bor.book_id")
    public BookBean findByOrgIdAndBookIdAndMemberId(Long org_id, Long bookId, Long member_id);

    //图书机构关系的统计数据更新
    @Update("update book_org_rel set click_count = click_count + #{2}, collect_count = collect_count + #{3}, share_count = share_count + #{4}, " +
            "review_count = review_count + #{5}, read_count = read_count + #{6}, order_weight = order_weight + #{7} " +
            "where org_id = #{0} and book_id = #{1}")
    public void updateBookCountInfo(Long org_id, Long book_id, Long click_count, Long collect_count, Long share_count, Long review_count, Long read_count, Long unite_index);

    //数据包图书全部基本信息
    @SelectProvider(type = BookOrgRelProvider.class, method = "getAllBooksBaseInfo")
    List<Map<String,Object>> getAllBooksBaseInfo(@Param("org_id") Long org_id, @Param("catPath") String catPath);

    //查询图书数量
    @Select("select count(*) from \n" +
            "(select r.book_id from book_org_rel r \n" +
            "left join book_repo b on r.book_id = b.book_id \n" +
            "where r.org_id = #{0} and b.book_status = 1 group by r.book_id) a")
    Integer getCountByOrgId(Long org_id);

    @Select("select count(*) from book_repo ")
    Integer getCount();


    @Select("select bor.book_id,bor.org_id,bor.book_cat_id,bor.order_weight,bor.is_hot,bor.is_recommend,br.book_name,br.book_author,br.book_cover,br.book_cover_small,br.book_isbn,br.book_url,br.book_publisher,br.book_remark,br.publish_time from book_org_rel bor ,book_repo br " +
            " where bor.book_id=br.book_id " +
            " and bor.org_id=#{1} " +
            " and br.book_status = 1  and bor.enabled = 1" +
            " and br.book_author = (select book_author from book_repo b where b.book_id=#{0}) and bor.book_id !=#{0}" +
            " group by br.book_id" +
            " order by bor.order_weight desc limit 0,#{2}")
    List<BookInfo> findSameAuthorBooks(Long bookId, Long org_id, Integer limit);

    @Select("select bor.book_id,bor.org_id,bor.book_cat_id,bor.order_weight,bor.is_hot,bor.is_recommend,br.book_name,br.book_author,br.book_cover,br.book_cover_small,br.book_isbn,br.book_url,br.book_publisher,br.book_remark,br.publish_time from book_org_rel bor ,book_repo br " +
            " where bor.book_id=br.book_id" +
            " and bor.org_id=#{org_id} and bor.book_id  not in(' ${bookIds} ')" +
            " and br.book_status = 1  and bor.enabled = 1" +
            " and bor.book_cat_id =#{bookCatId} and bor.book_id !=#{bookId}" +
            " group by br.book_id order by bor.order_weight desc limit 0,#{limit}")
    List<BookInfo> findSameCatalogBooks(@Param("bookId") Long bookId,@Param("org_id") Long org_id,
                                        @Param("bookCatId") Long bookCatId,@Param("limit") Integer limit,@Param("bookIds") String bookIds);

    @Select("select book_cat_id from book_org_rel where book_id =#{0} and org_id = #{1} limit 1")
    Long getCatId(Long book_id,Long org_id);

    @Select("select click_count from book_index where book_id = #{0} and org_id = #{1} order by create_time desc limit 1")
    Long getClickCount(Long book_id,Long org_id);

    @Select("select collect_count from book_index where book_id = #{0} and org_id = #{1} order by create_time desc limit 1")
    Long getCollectCount(Long book_id,Long org_id);

    @Select("select share_count from book_index where book_id = #{0} and org_id = #{1} order by create_time desc limit 1")
    Long getShareCount(Long book_id,Long org_id);

    @SelectProvider(type = BookOrgRelProvider.class,method = "getAppBooks")
    List<AppBooksBean> getAppBooks(@Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("type") String type, @Param("searchText") String searchText,@Param("order")String order);

    @Select("select br.book_name,br.book_id,br.book_cover,br.book_publisher,br.book_author,br.book_url,br.book_remark,br.price,br.book_cover_small,bs.discount_price,bs.end_time , 2 as book_type,bc.book_cat_name,bc.book_cat_id,bsf.shelf_id from book_org_rel bor left join book_repo br on br.book_id = bor.book_id " +
            " left join book_cat bc on (bc.book_cat_id = bor.book_cat_id and bc.org_id = 189) " +
            " left join (select * from book_shelf where is_delete = 2 and user_id = #{1})bsf on br.book_id = bsf.bk_id " +
            " left join (select * from book_discount where channel_type =3 and enabled = 1 and start_time <= NOW() and end_time >= NOW() )bs on bs.book_id = br.book_id " +
            " where bor.book_id = #{0} and bor.org_id = 189 GROUP BY bor.book_id limit 1")
    PublicBookBean findByMemberIdAndBookId(Long book_id, Long member_id);

    @Select("select br.*,bc.book_cat_name,bc.book_cat_id,2 as book_type from book_org_rel bor left join book_repo br on bor.book_id = br.book_id " +
            " left join book_index bi on bor.book_id = bi.book_id LEFT JOIN book_cat bc on bc.book_cat_id = bor.book_cat_id " +
            " where bc.org_id = 189 and bor.org_id=189 and bi.org_id = 189 GROUP BY bor.book_id order by bi.unite_index desc ")
    List<AppBooksBean> findRankList();

    @Select("select br.* from book_org_rel bor left join book_repo br on br.book_id = bor.book_id where bor.org_id = #{0} and br.book_id is not null order by bor.is_hot asc,bor.order_weight desc limit #{1}")
    List<BookBean> getHotList(Long org_id, Integer limit);

    @Select("select br.* from book_org_rel bor left join book_repo br on br.book_id = bor.book_id where bor.org_id = #{0} and br.book_id is not null order by bor.is_recommend asc,bor.order_weight desc limit #{1}")
    List<BookBean> getRecommendList(Long org_id, Integer limit);

    @Select("select br.book_id,bor.book_cat_id,br.book_cover,br.book_cover_small,br.book_remark,br.book_name,br.book_author,br.book_publisher from book_org_rel bor " +
            "left join book_repo br on bor.book_id = br.book_id where br.book_status = 1 and bor.org_id = #{0} and bor.enabled = 1 " +
            "group by br.book_id order by br.create_time desc limit #{1}")
    List<Map<String,Object>> selectNewCatBookList(Long org_id,Integer count);
}
