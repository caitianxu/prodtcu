package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.SubjectBookBean;
import com.cjsz.tech.system.domain.SubjectBooksRel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public interface SubjectBooksRelMapper extends BaseMapper<SubjectBooksRel>{

    //获取专题图书列表
    @Select("select b.book_name,b.book_author,b.book_remark, b.book_publisher,b.book_cover_small,r.* from subject_books_rel r" +
            " left join book_repo b on b.book_id = r.book_id where r.subject_id = #{0} order by r.order_weight desc")
    List<SubjectBookBean> getBookList(Long subject_id);

    //专题移除图书
    @Delete("delete from subject_books_rel where subject_id = #{0} and book_id = #{1}")
    void removeBook(Long subject_id, Long book_id);

    @Select("select * from subject_books_rel where book_id = #{0} and subject_id = #{1}")
    SubjectBooksRel findBook(Long book_id, Long subject_id);

    @Update("update subject_books_rel set order_weight = #{1} where rel_id = #{0}")
    void updateBookOrder(Long rel_id,Long order_weight);
}
