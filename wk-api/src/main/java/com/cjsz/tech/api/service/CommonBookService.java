package com.cjsz.tech.api.service;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberGradePointService;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 图书服务
 * Created by caitianxu on 2017/04/18.
 */
@Service
public class CommonBookService {

    @Autowired
    private BookReviewService bookReviewService;
    @Autowired
    private MemberService memberService;
    @Autowired
    MemberGradePointService memberGradePointService;

    /**
     * 获取用户所有评论
     * @param member_token
     * @param bean
     * @return
     */
    public JsonResult getOwnReviewQuery(String member_token, PageConditionBean bean, String token_type){
        try {
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            }
            // 检查是否注册
            Member member = memberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            }
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object obj = bookReviewService.getOwnReviewQuery(sort, bean, member.getMember_id());
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @Transactional
    //添加图书评论
    public void saveReview(BookReview review, String device_type){
        //添加评论，图书指数记录
        bookReviewService.saveBookReview(review, device_type);
        //评论积分记录，会员积分变更
        memberGradePointService.saveMemberGradePoint(review.getMember_id(), review.getBook_id(), "000002");
    }

}
