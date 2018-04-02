package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookReviewPraise;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface BookReviewPraiseMapper extends BaseMapper<BookReviewPraise> {

    @Select("select * from book_review_praise where review_id = #{0} and member_id = #{1}")
    BookReviewPraise getPraiseById(Long review_id, Long member_id);

    @Update("update book_review_praise set is_delete = #{0} where praise_id = #{1}")
    void updatePraiseById(Integer is_delete, Long praise_id);
}
