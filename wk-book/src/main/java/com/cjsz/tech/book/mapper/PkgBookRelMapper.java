package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.domain.PkgBookRel;
import com.cjsz.tech.book.provider.PkgBookRelProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface PkgBookRelMapper extends BaseMapper<PkgBookRel> {

    //分页查询数据包的图书列表
    @SelectProvider(type = PkgBookRelProvider.class, method = "getPkgBookList")
    List<BookBean> getPkgBookList(@Param("pkg_id") Long pkg_id, @Param("org_id") Long org_id, @Param("catPath") String catPath, @Param("searchText") String searchText,
                                  @Param("is_hot") Integer is_hot, @Param("is_recommend") Integer is_recommend, @Param("offline_status") Integer offline_status);

    //查询未添加前数据包的该分类下的图书Id
    @Select("select book_id from pkg_book_rel where pkg_id = #{0} and org_id = #{1} and book_cat_id = #{2}")
    public List<Long> selectPkgRelsByCatId(Long pkg_id, Long org_id, Long book_cat_id);

    @Select("select * from pkg_book_rel where pkg_id = #{1} and org_id = #{0} and book_cat_id = #{2} and book_id = #{3} ")
    public PkgBookRel selectById(Long org_id,Long pkg_id, Long book_cat_id, Long book_id);


    //删除数据包的图书关系————通过图书Ids
    @Delete("delete from pkg_book_rel where pkg_id = #{pkg_id} and book_id in(${del_bookids})")
    void deletePkgBookByBookId(@Param("pkg_id") Long pkg_id, @Param("del_bookids") String del_bookids);

    //数据包移除图书
    @Delete("delete from pkg_book_rel where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void removePkgBookRel(Long book_id, Long org_id, Long book_cat_id, Long pkg_id);

    //数据包批量移除图书
    @Delete("delete from pkg_book_rel where pkg_id = #{pkg_id} and (book_cat_id,book_id) in(${ids})")
    void removePkgBookRels(@Param("pkg_id") Long pkg_id, @Param("ids") String ids);

    //根据联合主键查询关系数据
    @Select("select * from pkg_book_rel where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3} ")
    public PkgBookRel selectPkgBookRelByUnionIds(Long book_id, Long org_id, Long book_cat_id, Long pkg_id);

    //机构图书 是否热门
    @Update("update pkg_book_rel set is_hot = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateRelHot(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Integer is_hot);

    //机构图书 是否推荐
    @Update("update pkg_book_rel set is_recommend = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateRelRecommend(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Integer is_recommend);

    //机构图书 是否离线
    @Update("update pkg_book_rel set offline_status = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateOffLine(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Integer offline_status);

    //数据包图书、机构图书   排序置顶
    @Update("update pkg_book_rel set order_weight = #{4},update_time = now() where book_id = #{0} and org_id = #{1} and book_cat_id = #{2} and pkg_id = #{3}")
    void updateOrderTop(Long book_id, Long org_id, Long book_cat_id, Long pkg_id, Long order_weight);

    //查询数据包图书详情__所有
    @Select("select * from pkg_book_rel where pkg_id = #{pkg_id} and org_id = #{orgid}")
    public List<PkgBookRel> selectAllPkgRels(@Param("pkg_id") Long pkg_id, @Param("orgid") Long orgid);

    //删除图书数据包关系表数据
    @Delete("delete from pkg_book_rel where pkg_id in(${pkgIds})")
    public void deleteByPkgIds(@Param("pkgIds") String pkgIds);

    //删除数据包图书机构关系数据
    @Delete("delete from pkg_book_rel where pkg_id = #{pkg_id} and org_id = #{orgid} and book_cat_id in(${catids})")
    public void deletePkgBookOrgRel(@Param("pkg_id") Long pkg_id, @Param("orgid") Long orgid, @Param("catids") String catids);

    //查找数据包的包与图书关系
    @Select("select * from pkg_book_rel where pkg_id = #{0}")
    List<PkgBookRel> findByPkgId(Long pkg_id);

    //通过数据包和图书Id查找数据包的包与图书关系
    @Select("select * from pkg_book_rel where pkg_id = #{0} and book_id = #{1}")
    List<PkgBookRel> selectByPkgIdAndBookId(Long pkg_id, Long book_id);

    @Select("select count(*) from pkg_book_rel where pkg_id = #{0}")
    Long selectBookCountByPkgId(Long pkg_id);

    @Select("select count(*) from (select distinct book_id from pkg_book_rel where pkg_id = #{0}) a")
    Long selectBookRealCountByPkgId(Long pkg_id);

    //数据包图书全部基本信息
    @SelectProvider(type = PkgBookRelProvider.class, method = "getAllBooksBaseInfo")
    List<Map<String,Object>> getAllBooksBaseInfo(@Param("pkg_id") Long pkg_id);
}
