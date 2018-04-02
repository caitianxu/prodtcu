package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.beans.ShelfBook;
import com.cjsz.tech.api.service.BookShelfService;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.OrgExtend;
import com.cjsz.tech.system.service.OrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * 书架
 * Created by shiaihua on 16/12/21.
 */
@Controller
public class FrontShelfBookController {

    @Autowired
    BookShelfService bookShelfService;

    @Autowired
    BookRepoService bookRepoService;

    @Autowired
    MemberService memberService;

    @Autowired
    OrgExtendService orgExtendService;

    /**
     * 个人书架-书列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/book/shelf/list", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfList(HttpServletRequest request) {
        //TOKEN
        String token = request.getParameter("token");
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        //根据TOKEN找会员
        Long user_id = 0L;
        Member member = memberService.findByToken(token, "2");
        if (member == null) {
            return JsonResult.getError("无此用户");
        }
        user_id = member.getMember_id();

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

        //返回会员的图书列表
        List<ShelfBook> bookList = bookShelfService.findShelfBookList(user_id);
        if (bookList != null && bookList.size() > 0) {
            for (ShelfBook book : bookList) {
                book.setBk_url(basePath + book.getBk_url());
                book.setBk_cover(basePath + book.getBk_cover());
                book.setBk_cover_small(basePath + book.getBk_cover_small());
            }
        }

        Hashtable result = new Hashtable();
        result.put("list", bookList);
        result.put("token", token);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.MEMBER_SHELF_LOAD_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }


    /**
     * 个人书架-删除书
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/book/shelf/del", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfDelete(HttpServletRequest request) {
        //TOKEN
        String token = request.getParameter("token");
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        String bk_id = request.getParameter("bkid");
        if (StringUtil.isEmpty(bk_id)) {
            return JsonResult.getError("没有该书");
        }
        String[] bkidarray = bk_id.split(";");
        if (bkidarray.length == 0) {
            return JsonResult.getError("没有该书");
        }
        String bkids = "";
        for (int bi = 0; bi < bkidarray.length; bi++) {
            bkids += bkidarray[bi] + ",";
        }
        if (bkids.endsWith(",")) {
            bkids = bkids.substring(0, bkids.length() - 1);
        }

        Long member_id = 0L;
        Member member = memberService.findByToken(token, "2");
        if (member == null) {
            return JsonResult.getError(Constants.LOGIN_BIND_NOUSER);
        }
        member_id = member.getMember_id();
        bookShelfService.deleteShelfBooks(member_id, bkids);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.MEMBER_SHELF_DEL_SUCCESS);
        return jsonResult;
    }


    /**
     * 个人书架-增加书
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/book/shelf/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfadd(HttpServletRequest request) {
        String device_type = request.getParameter("device_type");
        //TOKEN
        String token = request.getParameter("token");
        System.out.println("************************" + token);
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        //增加书架
        String bk_id = request.getParameter("bkid");
        System.out.println("************************" + bk_id);
        if (StringUtil.isEmpty(bk_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        } else {
            try {
                Long.parseLong(bk_id);
            } catch (Exception e) {
                return JsonResult.getError("图书非法");
            }
        }
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(bk_id));
        if (repoBook == null) {
            return JsonResult.getError("图书不存在");
        }
        String org_id = request.getParameter("org_id");
        if (StringUtil.isEmpty(org_id)) {
            return JsonResult.getError(APIConstants.ORG_NOTHING_ERROR);
        } else {
            try {
                Long.parseLong(org_id);
            } catch (Exception e) {
                return JsonResult.getError("机构非法");
            }
        }
        OrgExtend orgExtend = orgExtendService.selectById(Long.parseLong(org_id));
        if (orgExtend == null) {
            return JsonResult.getError(APIConstants.NO_ORG);
        }

        Long member_id = 0L;
        String username = "";
        Member member = memberService.findByToken(token, "2");
        if (member == null) {
            return JsonResult.getError(Constants.LOGIN_BIND_NOUSER);
        } else {
            //如果会员没有绑定机构，则绑定org_id
            if (member.getOrg_id() == null || member.getOrg_id() == -1) {
                member.setOrg_id(Long.parseLong(org_id));
                memberService.updateMember(member);
            }
        }
        member_id = member.getMember_id();
        username = member.getNick_name();
        ShelfBook book = bookShelfService.findShelfBookByUser(member_id, Long.parseLong(bk_id));
        if (book != null) {
            if (book.getIs_delete() == 1) {
                book.setIs_delete(2);//是否删除（1：是  2：否）
                book.setUpdate_time(new Date());
                bookShelfService.updateBookShelf(book);
            } else {
                return JsonResult.getError("该图书已在书架中");
            }
        } else {
            book = new ShelfBook();
            book.setBk_id(Long.parseLong(bk_id));
            book.setBk_name(repoBook.getBook_name());
            book.setBk_cover(repoBook.getBook_cover());
            book.setBk_cover_small(repoBook.getBook_cover_small());
            book.setBk_url(repoBook.getBook_url());
            book.setBk_author(repoBook.getBook_author());
            book.setUser_id(member_id);
//            book.setOrg_id(member.getOrg_id());
            book.setUser_name(username);
            book.setIs_delete(2);//是否删除（1：是  2：否）
            book.setCreate_time(new Date());
            book.setUpdate_time(new Date());
            book.setSchedule("");

            bookShelfService.saveShelfBook(book, member.getOrg_id(), device_type);
        }

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.MEMBER_SHELF_ADD_SUCCESS);
        return jsonResult;
    }

    /**
     * 个人书架-读书进度
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/book/shelf/schedule", method = {RequestMethod.POST})
    @ResponseBody
    public Object book_schedule(HttpServletRequest request) {
        //TOKEN
        String token = request.getParameter("token");
        System.out.println("************************" + token);
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        //增加书架
        String bk_id = request.getParameter("bkid");
        System.out.println("************************" + bk_id);
        if (StringUtil.isEmpty(bk_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        } else {
            try {
                Long.parseLong(bk_id);
            } catch (Exception e) {
                return JsonResult.getError("图书非法");
            }
        }
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(bk_id));
        if (repoBook == null) {
            return JsonResult.getError("图书不存在");
        }
        //schedule
        String schedule = request.getParameter("schedule");
        System.out.println("************************" + schedule);
        if (StringUtil.isEmpty(schedule)) {
            return JsonResult.getError("进度为空");
        }
        Long member_id = 0L;
        Member member = memberService.findByToken(token, "2");
        if (member == null) {
            return JsonResult.getError(Constants.LOGIN_BIND_NOUSER);
        }
        member_id = member.getMember_id();
        ShelfBook book = bookShelfService.findShelfBookByUser(member_id, Long.parseLong(bk_id));
        if (book != null) {
            book.setSchedule(schedule);
            book.setUpdate_time(new Date());
            bookShelfService.updateBookShelf(book);
            return JsonResult.getError("进度修改成功");
        } else {
            return JsonResult.getError("找不到图书");
        }
    }

}
