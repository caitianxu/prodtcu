package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface BookCatMapper extends BaseMapper<BookCat> {

    //获取机构图书分类
    @Select("select * from book_cat where org_id = #{org_id} " +
            "order by length(book_cat_path)-length(replace(book_cat_path,'|','')) asc, order_weight desc, create_time desc")
    List<BookCat> getOrgCats(@Param("org_id") Long org_id);

    //查找包的分类
    @Select("select * from book_cat where pkg_id = #{0} and org_id = #{1} and book_cat_id = #{2} ")
    BookCat selectPkgBookCat(Long pkg_id, Long org_id, Long cat_id);

    //查找机构的分类
    @Select("select * from book_cat where org_id = #{0} and book_cat_id = #{1} ")
    BookCat selectOrgBookCat(Long org_id, Long cat_id);

    //将层次路径中包含old_full_path的，更新为new_full_path
    @Update("update book_cat set book_cat_path = REPLACE(book_cat_path,#{0},#{1}) ,update_time = now() " +
            "where book_cat_path like concat(#{0},'%') and org_id = #{2} ")
    public void updateOrgFullPath(String old_full_path, String new_full_path, Long org_id);

    //查找机构的分类集合
    @Select("select book_cat_id from book_cat where org_id=#{0} " +
            "and book_cat_path like CONCAT((select book_cat_path from book_cat where org_id=#{0} and book_cat_id=#{1}), '%')")
    List<Long> getOrgAllCatTree(Long orgid, Long book_cat_id);

    //删除机构图书分类
    @Delete("delete from book_cat where org_id=#{orgid} and book_cat_id in(${catids})")
    public void deleteOrgBookCat(@Param("orgid") Long orgid, @Param("catids") String catids);

    //删除机构的图书分类表
    @Delete("delete from book_cat where org_id = #{0}")
    public void deleteByOrgId(Long org_id);

    @Select("select * from book_cat where org_id=#{0} and  update_time >#{1} limit #{2}, #{3} ")
    List<BookCat> getOffLineNumList(Long orgid, String oldtimestamp, Integer pageNum, Integer pageSize);

    @Select("select count(*) as num from book_cat where org_id=#{0} and  update_time >#{1}")
    Integer hasOffLine(Long orgid, String oldtimestamp);

    //获取当前分类本身和下级分类
    @Select("select * from book_cat where org_id = #{0} and book_cat_path like concat(#{1},'%')")
    List<BookCat> getOwnerCats(Long org_id, String book_cat_path);

    //获取机构第一层级图书分类
    @Select("select * from book_cat where org_id = #{0} and book_cat_pid = 0  order by order_weight desc ")
    List<BookCat> selectFirstCatsByOrgId(Long org_id);

    //获取机构第一层级有限图书分类
    @Select("select * from book_cat where org_id = #{0} and book_cat_pid = 0 order by order_weight desc limit #{1}")
    List<BookCat> selectFirstCatsByOrgIdAndCount(Long org_id, Integer count);

    @Select("select * from book_cat where org_id = #{0} and book_cat_pid = 0 order by order_weight desc limit #{1}")
    List<Map<String, Object>> selectFirstCatsByIdAndCount(Long org_id, Integer count);

    @Select("select * from book_cat where org_id = 189 and book_cat_pid = 0 order by order_weight desc ")
    List<BookCat> getCatList();

    @Select("select * from book_cat where org_id = 189 and book_cat_pid = #{0} order by order_weight desc " )
    List<BookCat> getListByPid(Long pid);

    @Select("select * from book_cat where org_id = #{0} order by length(book_cat_path)-length(replace(book_cat_path,'|','')) asc, order_weight desc, create_time desc")
    List<BookCat> getAllCats(Long org_id);

    @Select("select * from book_cat where book_cat_id = #{0} and org_id = #{1} limit 1")
    BookCat selectByCatId(Long book_cat_id,Long org_id);
}
