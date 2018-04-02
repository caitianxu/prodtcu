package com.cjsz.tech.api.mapper;

import com.cjsz.tech.api.beans.ShelfBook;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by shiaihua on 16/12/29.
 */
public interface BookShelfMapper extends BaseMapper<ShelfBook> {

    @Select("select bs.*,br.book_publisher from book_shelf bs " +
            "left join book_repo br on bs.bk_id = br.book_id " +
            "where bs.user_id=#{0} and bs.is_delete = 2")
    public List<ShelfBook> findShelfBookList(Long user_id);

    @Select("select * from book_shelf where user_id=#{0} and bk_id=#{1}")
    public List<ShelfBook> findShelfBookByUser(Long user_id, Long bkid);


    @Select("select bs.*,br.book_publisher from book_shelf bs " +
            " left join book_repo br on bs.bk_id = br.book_id " +
            " where bs.user_id=#{0} and book_type =2 and bs.is_delete = 2 ORDER BY update_time desc ")
    public List<ShelfBook> findShelfBookList_v2(Long user_id);

    @Select("select * from book_shelf where user_id=#{0} and bk_id=#{1}")
    public List<ShelfBook> findShelfBookByUser_v2(Long user_id, Long bkid);

    @Select("select * from book_shelf where user_id = #{0} and bk_id = #{1} and book_type = #{2} limit 1")
    ShelfBook findShelfBookByUser_v3(Long user_id,Long bkid,Integer book_type);

    @Select("select bs.*,br.book_publisher from book_shelf bs " +
            " left join book_repo br on bs.bk_id = br.book_id " +
            " where bs.user_id=#{0}  and bs.is_delete = 2 ORDER BY update_time desc ")
    List<ShelfBook> findShelfBookList_v3(Long member_id);

    @Update("update book_shelf set is_delete =1 ,update_time = now() where shelf_id in (${shelf_ids})")
    void deleteShelfBooks_v3(@Param("shelf_ids") String shelf_ids);

    @Select("select * from book_shelf where user_id =#{0} and bk_id =#{1} and book_type =#{2} limit 1")
    ShelfBook findBookInShelf(Long member_id, Long book_id, Integer book_type);
}
