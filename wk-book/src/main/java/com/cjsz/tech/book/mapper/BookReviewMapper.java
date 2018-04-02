package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 图书评论
 * Created by Administrator on 2017/3/16 0016.
 */
public interface BookReviewMapper extends BaseMapper<BookReview> {

    @Select("select * from book_review where review_id = #{0} and is_delete = 2")
    BookReview selectById(Long review_id);

    @Select("select br.*,m.nick_name,m.icon,b.book_name from book_review br left join tb_member m " +
            "on br.member_id = m.member_id left join book_repo b on b.book_id = br.book_id where review_id = #{0} and is_delete = 2 limit 1")
    BookReviewBean selectReviewById(Long review_id);


    @Select("select r.*,b.book_cover_small,m.nick_name,m.is_sys_icon,m.grade_id,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join member m on m.member_id = r.member_id " +
            "where r.review_id = #{0}")
    BookReview getInfoById(Long review_id);

    @Update("update book_review set review_nums = review_nums + 1 where review_id = #{0}")
    void addReviewNums(Long review_id);

    @Select("select r.*,b.book_cover_small,m.nick_name,m.is_sys_icon,m.grade_id,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join member m on m.member_id = r.member_id " +
            "where r.is_delete != 1 and r.org_id = #{0} and r.book_id = #{1} and (r.pid is null or r.pid = 0) order by r.pid asc, r.create_time asc")
    List<BookReview> selectFirstByOrgIdAndBookId(Long org_id, Long book_id);


    @Select("select r.*,b.book_cover_small,m.nick_name,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join tb_member m on m.id = r.member_id " +
            "where r.is_delete != 1 and r.org_id = #{0} and r.book_id = #{1} and (r.pid is null or r.pid = 0) order by r.pid asc, r.create_time asc")
    List<BookReview> selectLevel1ByOrgIdAndBookId(Long org_id, Long book_id);

    @Select("select r.*,b.book_cover_small,m.nick_name,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join tb_member m on m.id = r.member_id " +
            "where r.is_delete != 1 and r.org_id = #{0} and r.book_id = #{1} and (r.pid is not null or r.pid = 0) ")
    List<BookReview> selectSubLevelByOrgIdAndBookId(Long org_id, Long book_id);


    @Select("select r.*,b.book_cover_small,m.nick_name,m.is_sys_icon,m.grade_id,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join member m on m.member_id = r.member_id " +
            "where r.is_delete != 1 and r.org_id = #{0} and r.book_id = #{1} and (r.pid is not null or r.pid = 0) ")
    List<BookReview> selectChildByOrgIdAndBookId(Long org_id, Long book_id);

    @Select("select r.*,b.book_cover_small,b.book_name,m.nick_name,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join tb_member m on m.member_id = r.member_id " +
            "where r.is_delete != 1 and r.member_id = #{0} ")
    List<BookReview> selectFirstByMemberId(Long member_id);

    @Select("select r.*,b.book_cover_small,b.book_name,m.nick_name,m.icon from book_review r " +
            "left join book_repo b on r.book_id = b.book_id " +
            "left join tb_member m on m.member_id = r.member_id " +
            "where r.is_delete != 1 and r.pid in(${pids})")
    List<BookReview> selectByPid(@Param("pids") String pids);

    @Update("update book_review set is_delete = 1 where review_id= #{ids} ")
    void deleteReview(@Param("ids") Long ids);

    @Update("update book_review set is_delete = 1 where pid in(${ids})")
    void deleteReviews(@Param("ids") String ids);

    @Select("select * from book_review where member_id != #{member_id} and review_id = #{review_id}")
    BookReview selectNotMyReviewsById(@Param("member_id") Long member_id, @Param("review_id") Long review_id);

    @Select("select * from book_review where member_id != ${member_id} and review_id in(${review_id})")
    List<BookReview> selectNotMyReviewsByIds(@Param("member_id") Long member_id, @Param("review_ids") String review_ids);

    @Select("select count(0) from book_review where member_id = #{0}")
    Integer getReviewCountByMemberId(Long member_id);

    @Select("select review_id from book_review where pid = #{review_id}")
    List<Integer> selectByIds(@Param("review_id") Long review_id);


    @Select("select * from book_review where is_delete=2 and  full_path  like concat(#{0},'%') order by pid")
    List<BookReview> findReviewsByFullPath(String fullPath);

    @Select("select br.*,m.nick_name,m.icon,b.book_name from book_review br left join tb_member m " +
            "on(br.member_id=m.member_id) left join book_repo b on b.book_id = br.book_id " +
            "where br.is_delete=2 and  br.pid=#{0} group by review_id order by br.create_time desc")
    List<BookReviewBean> getReviewsByPid(Long reviewId);

    @Select("select br.*,b.book_name,b.book_cover_small from book_review br left join book_repo b on br.book_id = b.book_id " +
            "            where br.member_id = #{0}  and br.is_delete =2 and br.book_type =2 " +
            "union " +
            "select br.*,b.book_name,b.book_cover as book_cover_small from book_review br left join cjzww_books b on br.book_id = b.book_id " +
            "            where br.member_id = #{0}  and br.is_delete =2 and br.book_type =1 ")
    List<BookReviewBean> getMemberReview(Long member_id);

    @Select("select br.*,b.book_name,b.book_cover_small from book_review br left join book_repo b on br.book_id = b.book_id " +
            "            where br.member_id = #{0}  and br.is_delete =2 and br.book_type =2 ")
    List<BookReviewBean> getMemberReviewv2(Long member_id);

    @Select("select br.* ,m.icon,m.nick_name from book_review br left join tb_member m on m.member_id = br.member_id " +
            " where br.org_id = #{1} and br.book_id = #{0} and br.is_delete = 2 and (br.pid is null or br.pid = 0) " +
            " GROUP BY review_id order by br.create_time desc limit 3")
    List<BookReviewBean> getBookReviewList(Long book_id, Long org_id);

    @Select("select br.* ,m.icon,m.nick_name from book_review br left join tb_member m on m.member_id = br.member_id " +
            " where br.org_id = #{0} and br.book_id = #{1} and br.is_delete = 2 and (br.pid is null or br.pid = 0) GROUP BY review_id ")
    List<BookReviewBean> getReviewsList(Long org_id, Long book_id);

    @Select("select * from book_review where review_id = #{0}")
    BookReview selectChildById(Long review_id);

    @Update("update book_review set praise_count = #{0} where review_id = #{1}")
    void updatePraiseCount(Integer praise_count, Long review_id);

    @Select("select br.* ,m.icon,m.nick_name,p.praise_id from book_review br left join tb_member m on m.member_id = br.member_id " +
            " left join (select * from book_review_praise where is_delete = 2 and member_id = #{3}) p on br.review_id = p.review_id where br.org_id = #{0} and br.book_id = #{1} " +
            "  and br.book_type = #{2} and br.is_delete = 2 and (br.pid is null or br.pid = 0) GROUP BY review_id")
    List<BookReviewBean> getReviewList(Long org_id, Long book_id,Integer book_type,Long member_id);

    @Select("select count(review_id) from book_review where member_id = #{member_id} and review_id in (${review_ids}) and is_delete =2")
    Integer selectCountMyReviewsById(@Param("member_id") Long member_id, @Param("review_ids") String review_ids);

    @Update("update book_review set is_delete =1 where review_id in (${review_ids})")
    void deleteByReviews(@Param("review_ids") String review_ids);
}
