package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.book.domain.PkgBookCat;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface PkgBookCatMapper extends BaseMapper<PkgBookCat> {

    //获取数据包图书分类
    @Select("select * from pkg_book_cat where org_id = #{org_id} and pkg_id = #{pkg_id} " +
            "order by length(book_cat_path)-length(replace(book_cat_path,'|','')) asc, order_weight desc, create_time desc")
    List<PkgBookCat> getPkgCats(@Param("org_id") Long org_id, @Param("pkg_id") Long pkg_id);

    //查找包的分类
    @Select("select * from pkg_book_cat where pkg_id = #{0} and org_id = #{1} and book_cat_id = #{2}")
    PkgBookCat selectPkgBookCat(Long pkg_id, Long org_id, Long cat_id);

    //将层次路径中包含old_full_path的，更新为new_full_path
    @Update("update pkg_book_cat set book_cat_path = REPLACE(book_cat_path,#{0},#{1}) ,update_time = now() " +
            "where book_cat_path like concat(#{0},'%') and org_id = #{2} and pkg_id = #{3}")
    public void updatePkgFullPath(String old_full_path, String new_full_path, Long org_id, Long pkg_id);

    //查找包的分类集合
    @Select("select book_cat_id from pkg_book_cat where pkg_id = #{0} and org_id=#{1} " +
            "and book_cat_path like CONCAT((select book_cat_path from pkg_book_cat where pkg_id = #{0} and org_id=#{1} and book_cat_id=#{2}), '%')")
    List<Long> getPkgAllCatTree(Long pkg_id, Long orgid, Long book_cat_id);

    //删除数据包图书分类
    @Delete("delete from pkg_book_cat where pkg_id = #{pkg_id} and org_id=#{orgid} and book_cat_id in(${catids})")
    public void deletePkgBookCat(@Param("pkg_id") Long pkg_id, @Param("orgid") Long orgid, @Param("catids") String catids);

    //删除数据包的图书分类数据
    @Delete("delete from pkg_book_cat where pkg_id in(${pkgIds})")
    public void deleteByPkgIds(@Param("pkgIds") String pkgIds);

    //获取当前分类本身和下级分类
    @Select("select * from pkg_book_cat where org_id = #{0} and pkg_id = #{1} and book_cat_path like concat(#{2},'%')")
    List<BookCat> getOwnerCats(Long org_id, Long pkg_id, String book_cat_path);
}
