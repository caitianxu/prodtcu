package com.cjsz.tech.web.ctrl.mobile;

import com.cjsz.tech.api.beans.RecordTypeEnum;
import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.BookInfo;
import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class MobileBookInfoController {

    @Autowired
    private BookOrgRelService bookOrgRelService;
    @Autowired
    private BookIndexRecordService bookIndexRecordService;
    @Autowired
    private BookReviewService bookReviewService;

    /**
     * 图书详情
     *
     * @return
     */
    @RequestMapping("/mobile/bookInfo/{bookid}")
    public ModelAndView mobileBookInfo(HttpServletRequest request, @PathVariable String bookid) {
        ModelAndView mv = new ModelAndView();
        Long member_id = 0L;
        if (request.getAttribute("org_id") != null) {
            String org_Id = request.getAttribute("org_id").toString();
            Object member_token = request.getSession().getAttribute("member_token");
            UnifyMember member = null;
            if (member_token != null) {
                member = (UnifyMember) request.getSession().getAttribute("member_info");
            }
            Long org_id = Long.parseLong(org_Id);
            if (member != null) {
                member_id = member.getMember_id();
            }
            BookBean bookInfo = bookOrgRelService.findByOrgIdAndBookIdAndMemberId(org_id, Long.valueOf(bookid), member_id);
            if (member != null) {
                //2.记录用户点击记录
                BookIndexRecord bookIndexRecord = new BookIndexRecord(org_id, Long.valueOf(bookid),2, member_id, RecordTypeEnum.CLICK.code(), new Date(), "weixin");
                bookIndexRecordService.addRecord(bookIndexRecord);
            }
            //图书详情
            mv.addObject("bookInfo", bookInfo);
            List<BookReviewBean> bookReviewList = bookReviewService.getBookReviewList(Long.parseLong(bookid), org_id);
            mv.addObject("bookReviewList", bookReviewList);

            Integer limit = 6;
            List<BookInfo> datas = Lists.newArrayList();
            //1.获取同作者的的相关推荐
            List<BookInfo> sameAuthorBooks = bookOrgRelService.findSameAuthorBooks(Long.valueOf(bookid), org_id, limit);
            if (sameAuthorBooks != null && sameAuthorBooks.size() > 0) {
                limit -= sameAuthorBooks.size();
                datas.addAll(sameAuthorBooks);
            }
            List<Long> book_ids = new ArrayList<>();
            String bookIds = "";
            if (sameAuthorBooks.size() > 0) {
                for (BookInfo bookinfo : sameAuthorBooks) {
                    book_ids.add(bookinfo.getBook_id());
                }
                bookIds = StringUtils.join(book_ids, ",");
            }
            Long bookCatId = bookOrgRelService.getCatId(Long.valueOf(bookid), org_id);
            //2.获取同分类的相关推荐
            if (limit > 0) {
                List<BookInfo> sameCatalogBooks = bookOrgRelService.findSameCatalogBooks(Long.valueOf(bookid), org_id, bookCatId, limit, bookIds);//需要明确指定是那个分类，因为同一本图书可能在不同的分类下面
                if (sameCatalogBooks != null && sameCatalogBooks.size() > 0) {
                    datas.addAll(sameCatalogBooks);
                }
            }
            //相关推荐
            mv.addObject("recommendList", datas);
            mv.setViewName("mobile/bookInfo");
        } else {
            mv.setViewName("mobile/404");
        }
        return mv;
    }

    /**
     * 图书详情 - 错误
     *
     * @return
     */
    @RequestMapping("/mobile/bookInfo")
    public ModelAndView mobileBookInfoErr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/err");
        return mv;
    }

}
