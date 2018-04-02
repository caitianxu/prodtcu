package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.beans.ShelfBook;
import com.cjsz.tech.api.service.BookShelfService;
import com.cjsz.tech.book.beans.BookBaseInfo;
import com.cjsz.tech.book.domain.BookChapter;
import com.cjsz.tech.book.domain.BookDeviceRel;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.service.BookChapterService;
import com.cjsz.tech.book.service.BookDeviceRelService;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.ProOrgExtend;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图书接口
 * Created by shiaihua on 16/12/29.
 */
@Controller
public class FrontBookController {

    @Autowired
    BookRepoService bookRepoService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    BookOrgRelService bookOrgRelService;

    @Autowired
    BookDeviceRelService bookDeviceRelService;

    @Autowired
    MemberService memberService;

    @Autowired
    BookShelfService bookShelfService;

    @Autowired
    BookChapterService bookChapterService;

    @Autowired
    MemberReadRecordService memberReadRecordService;

    @Autowired
    ProOrgExtendService proOrgExtendService;


    @RequestMapping(value = "/api/book/info", method = {RequestMethod.POST, RequestMethod.GET})
    public void bookinfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getParameter("token");
        String a = request.getParameter("a");
        String o = request.getParameter("o");
        System.out.println(o);
        try {
            Long org_id = Long.parseLong(o);
            ProOrgExtend proOrgExtend = proOrgExtendService.selectByOrgId(org_id);
            String path = request.getContextPath();
            String basePath = "";


            if (proOrgExtend == null || StringUtils.isEmpty(proOrgExtend.getServer_name())) {
                basePath = request.getScheme() + "://cjzww.cjszyun.cn";

            } else {
                basePath = request.getScheme() + "://" + proOrgExtend.getServer_name() + ".cjszyun.cn";
            }
            if (request.getServerPort() != 80) {
                basePath = basePath + ":" + request.getServerPort();
            }
            basePath += path;
            if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(a) && "add".equals(a)) {
                request.getRequestDispatcher("/api/book/info1").forward(request, response);
            } else {
                String b = request.getParameter("b");
                String url = basePath + "/mobile/bookInfo/" + b;
                response.sendRedirect(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
            response.sendRedirect("/mobile/err");
        }
    }

    /**
     * 图书详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/book/info1", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object bookinfo1(HttpServletRequest request) {
        String device_type = request.getParameter("device_type");
        String bkidstr = request.getParameter("b");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            return JsonResult.getError("图书编码非法");
        }
        String orgidstr = request.getParameter("o");
        Long orgid = 0L;
        try {
            orgid = Long.parseLong(orgidstr);
        } catch (Exception e) {
            return JsonResult.getError("非法机构");
        }
        String devcode = request.getParameter("d");
        if (StringUtils.isEmpty(devcode)) {
            return JsonResult.getError("设备未注册");
        }
        Device deviceObj = deviceService.getDeviceByCode(devcode);
        if (deviceObj == null) {
            return JsonResult.getError(APIConstants.DEVICE_NOTREG);
        }
        if (deviceObj.getEnabled() != 1) {
            return JsonResult.getError(APIConstants.DEVICE_DISABLED);
        }
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

        BookRepo bookObj = bookRepoService.findByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        //TOKEN
        String token = request.getParameter("token");
        String a = request.getParameter("a");
        String message = "";
        if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(a) && "add".equals(a)) {
            Long user_id = 0L;
            Member member = memberService.findByToken(token, "2");

            if (member != null) {
                //如果会员没有绑定机构，则绑定org_id
                if (member.getOrg_id() == null || member.getOrg_id() == -1) {
                    member.setOrg_id(orgid);
                    memberService.updateMember(member);
                }
                user_id = member.getMember_id();

                ShelfBook shelfBook = bookShelfService.findShelfBookByUser(user_id, bkid);
                if (shelfBook == null) {
                    shelfBook = new ShelfBook();
                    shelfBook.setBk_id(bookObj.getBook_id());
                    shelfBook.setBk_name(bookObj.getBook_name());
                    shelfBook.setBk_cover(bookObj.getBook_cover());
                    shelfBook.setBk_cover_small(bookObj.getBook_cover_small());
                    shelfBook.setBk_url(bookObj.getBook_url());
                    shelfBook.setBk_author(bookObj.getBook_author());
                    shelfBook.setUser_id(user_id);
//                    shelfBook.setOrg_id(orgid);
                    shelfBook.setUser_name(member.getNick_name());
                    shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                    shelfBook.setCreate_time(new Date());
                    shelfBook.setUpdate_time(new Date());
                    shelfBook.setSchedule("");
                    bookShelfService.saveShelfBook(shelfBook, member.getOrg_id(), device_type);
                    message = "添加到书架成功";
                } else {
                    if (shelfBook.getIs_delete() == 1) {
                        shelfBook.setIs_delete(2);//是否删除（1：是  2：否）
                        shelfBook.setUpdate_time(new Date());
                        bookShelfService.updateBookShelf(shelfBook);
                    } else {
                        message = "该书已经在您的书架中";
                    }
                }
            }
        }

        bookObj.setBook_cover(basePath + bookObj.getBook_cover());
        bookObj.setBook_url(basePath + bookObj.getBook_url());
        bookObj.setBook_cover_small(basePath + bookObj.getBook_cover_small());

        Hashtable result = new Hashtable();
        result.put("info", bookObj);
        result.put("token", token);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_INFO_SUCCESS);
        if (StringUtils.isNotEmpty(message)) {
            jsonResult.setMessage(message);
        }
        jsonResult.setData(result);
        return jsonResult;
    }


    /**
     * 大屏--获取图书目录
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/bigscreen/book/chapterlist", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object bigscreen_chapterlist(HttpServletRequest request) {

        String bkidstr = request.getParameter("bkid");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            return JsonResult.getError("图书编码非法");
        }

        String devcode = request.getParameter("devcode");
        if (StringUtils.isEmpty(devcode)) {
            return JsonResult.getError("设备未注册");
        }
        Device deviceObj = deviceService.getDeviceByCode(devcode);
        if (deviceObj == null) {
            return JsonResult.getError(APIConstants.DEVICE_NOTREG);
        }
        if (deviceObj.getEnabled() != 1) {
            return JsonResult.getError(APIConstants.DEVICE_DISABLED);
        }
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

//        BookRepo bookObj = bookRepoService.findByBookId(bkid);
        BookBaseInfo bookObj = bookRepoService.findBaseInfoByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }


        bookObj.setBook_cover(basePath + bookObj.getBook_cover());
//        bookObj.setBook_url(basePath + bookObj.getBook_url());
        bookObj.setBook_cover_small(basePath + bookObj.getBook_cover_small());

//        List<BookChapter> chapterList = bookChapterService.findEpubChapters(bkidstr);
        List<BookChapter> chapterList = bookChapterService.getTreeEpubChapters(bkidstr);


        Hashtable result = new Hashtable();
        result.put("info", bookObj);
        result.put("chapters", chapterList);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }

    /**
     * 移动端--获取图书目录
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/book/chapterlist", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object api_chapterlist(HttpServletRequest request) {

        String bkidstr = request.getParameter("bkid");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            return JsonResult.getError("图书编码非法");
        }

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

        BookBaseInfo bookObj = bookRepoService.findBaseInfoByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }

        bookObj.setBook_cover(basePath + bookObj.getBook_cover());
        bookObj.setBook_cover_small(basePath + bookObj.getBook_cover_small());

        List<BookChapter> chapterList = bookChapterService.getTreeEpubChapters(bkidstr);

        Hashtable result = new Hashtable();
        result.put("info", bookObj);
        result.put("chapters", chapterList);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }


    /**
     * 大屏--获取图书章节内容
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/bigscreen/book/chaptercontent", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object bigscreen_chaptercontent(HttpServletRequest request) {

        String bkidstr = request.getParameter("bkid");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            return JsonResult.getError("图书编码非法");
        }
        String chapteridstr = request.getParameter("chapterid");
        if (StringUtils.isEmpty(chapteridstr)) {
            return JsonResult.getError("请提供章节信息");
        }
        Long chapterid = 0L;
        try {
            chapterid = Long.parseLong(chapteridstr);
        } catch (Exception e) {
            return JsonResult.getError("图书章节信息非法");
        }

        String devcode = request.getParameter("devcode");
        if (StringUtils.isEmpty(devcode)) {
            return JsonResult.getError("设备未注册");
        }
        Device deviceObj = deviceService.getDeviceByCode(devcode);
        if (deviceObj == null) {
            return JsonResult.getError(APIConstants.DEVICE_NOTREG);
        }
        if (deviceObj.getEnabled() != 1) {
            return JsonResult.getError(APIConstants.DEVICE_DISABLED);
        }
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

        BookRepo bookObj = bookRepoService.findByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        BookChapter chapter = bookChapterService.findMyChapterById(bkid, chapterid);
        if (chapter == null) {
            return JsonResult.getError("章节信息不存在");
        }


        String content = loadEpubChapter(bookObj, chapter, basePath);

        Hashtable result = new Hashtable();
        result.put("content", content);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }

    /**
     * 移动端--获取图书章节内容
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/book/chaptercontent", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object api_chaptercontent(HttpServletRequest request) {

        String bkidstr = request.getParameter("bkid");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            return JsonResult.getError("图书编码非法");
        }
        String chapteridstr = request.getParameter("chapterid");
        if (StringUtils.isEmpty(chapteridstr)) {
            return JsonResult.getError("请提供章节信息");
        }
        Long chapterid = 0L;
        try {
            chapterid = Long.parseLong(chapteridstr);
        } catch (Exception e) {
            return JsonResult.getError("图书章节信息非法");
        }

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

        BookRepo bookObj = bookRepoService.findByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        BookChapter chapter = bookChapterService.findMyChapterById(bkid, chapterid);
        if (chapter == null) {
            return JsonResult.getError("章节信息不存在");
        }

        Long member_id = 0L;
        Long org_id = 1L;
        String token = request.getParameter("token");
        if (token != null) {
            Member member = memberService.findByToken(token, "2");
            if (member != null) {
                member_id = member.getMember_id();
                if (member.getOrg_id() != null && member.getOrg_id() != 0 && member.getOrg_id() != -1) {
                    org_id = member.getOrg_id();
                }
            }
        }
        String device_type = request.getParameter("device_type");
        //进入阅读器，更换章节，用户阅读记录，图书指数记录（阅读）;web:000004
        MemberReadRecord memberReadRecord = new MemberReadRecord(member_id, bkid, 2, chapterid);
        memberReadRecord.setBook_name(bookObj.getBook_name());
        memberReadRecord.setBook_cover(bookObj.getBook_cover_small());
        memberReadRecord.setChapter_name(chapter.getName());
        memberReadRecordService.updateRecord(org_id, memberReadRecord, device_type);
        String content = loadEpubChapter(bookObj, chapter, basePath);

        Hashtable result = new Hashtable();
        result.put("content", content);

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }


    /**
     * 根据EPUB书籍编码，章节编码，获取章节内容
     *
     * @param book
     * @param chapter
     * @return
     */
    public String loadEpubChapter(BookRepo book, BookChapter chapter, String webpath) {
        try {
            File file = SpringContextUtil.getApplicationContext().getResource("").getFile();
            //图书解压路径
            String un_book_path = book.getBook_url().replace(book.getFile_name(), "") + "unfile/" + book.getFile_name().replace(".epub", "");
            String chapter_path = file.getPath() + un_book_path + "/" + chapter.getPurl() + chapter.getUrl();
            File un_book_file = new File(chapter_path);
            if (!un_book_file.exists()) {
                return "无内容";
            }
            String content = FileUtils.readFileToString(un_book_file);
            String cssPath = webpath + un_book_path + "/" + chapter.getPurl() + chapter.getUrl().replace(un_book_file.getName(), "");
            if (cssPath.endsWith("/")) {
                cssPath = cssPath.substring(0, cssPath.length() - 1);
            }
//            content = content.replaceAll("\\.\\.", "");
            content = content.replace("src=\"", "src=\"" + cssPath + "/");
//            content = content.replace("href=\"", "href=\"" + cssPath + "/");
            content = content.replace("href=\"", "");


            String regxp = "<\\s*html\\s+([^>]*)\\s*>";
            Pattern pattern = Pattern.compile(regxp);
            Matcher matcher = pattern.matcher(content);
            StringBuffer sb = new StringBuffer();
            boolean result = matcher.find();
            matcher.appendReplacement(sb, "<html>");
            matcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无内容";
    }

    /**
     * 大屏图书离线状态反馈
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/bigscreen/book/offlineStatus", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object bigscreen_offlineStatus(HttpServletRequest request) {
        String device_code = request.getParameter("device_code");
        String book_id = request.getParameter("book_id");
        String offline_status = request.getParameter("offline_status");
        if (StringUtils.isEmpty(device_code)) {
            return JsonResult.getError("请提供设备编码");
        }
        if (StringUtils.isEmpty(book_id)) {
            return JsonResult.getError("请提供图书编号");
        }
        if (StringUtils.isEmpty(offline_status)) {
            return JsonResult.getError("请提供离线状态");
        }
        Integer status = 0;
        try {
            status = Integer.valueOf(offline_status);
        } catch (Exception e) {
            return JsonResult.getError("非法离线状态");
        }
        Device device = deviceService.getDeviceByCode(device_code);
        if (device == null) {
            return JsonResult.getError("该设备不存在");
        }
        try {
            List<String> list = Arrays.asList(book_id.split(","));
            for (String bkid : list) {
                //查询关系是否存在：存在则修改，不存在新增
                List<BookDeviceRel> bookDeviceRels = bookDeviceRelService.selectBookDeviceRel(device.getDevice_id(), Long.parseLong(bkid), device.getOrg_id());
                if (bookDeviceRels.size() == 0) {
                    List<BookOrgRel> bookOrgRels = bookOrgRelService.selectOrgRelsByBookId(device.getOrg_id(), Long.parseLong(bkid));
                    for (BookOrgRel bookOrgRel : bookOrgRels) {
                        BookDeviceRel bookDeviceRel = new BookDeviceRel();
                        bookDeviceRel.setOrg_id(device.getOrg_id());
                        bookDeviceRel.setBook_id(Long.parseLong(bkid));
                        bookDeviceRel.setBook_cat_id(bookOrgRel.getBook_cat_id());
                        bookDeviceRel.setDevice_id(device.getDevice_id());
                        bookDeviceRel.setStatus(status);
                        bookDeviceRel.setCreate_time(new Date());
                        bookDeviceRel.setUpdate_time(new Date());
                        bookDeviceRels.add(bookDeviceRel);
                    }
                    bookDeviceRelService.saveBookDeviceRels(bookDeviceRels);
                } else {
                    bookDeviceRelService.updateBookDeviceRelStatus(device.getOrg_id(), device.getDevice_id(), book_id, status);
                }
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("数据异常");
        }
    }

    /**
     * 获取图书目录层次
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/v2/api/book/chapterTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getChapterTree(HttpServletRequest request) {
        String bkidstr = request.getParameter("bkid");
        String member_id = request.getParameter("member_id");
        String token_type = request.getParameter("token_type");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("图书编码非法");
        }
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;
        String schedule = null;
        BookBaseInfo bookObj = bookRepoService.findBaseInfoByBookId(bkid);
        if ("max".equals(token_type)) {
            schedule = "0|0";
            bookObj.setSchedule(schedule);
        } else {
            if (StringUtils.isEmpty(member_id)) {
                return JsonResult.getError("请提供会员编号");
            }
            schedule = bookRepoService.getSchedule(bkid, Long.parseLong(member_id));
            if (schedule == null) {
                schedule = "0|0";
                bookObj.setSchedule(schedule);
            } else {
                bookObj.setSchedule(schedule);
            }
        }
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        bookObj.setBook_cover(basePath + bookObj.getBook_cover());
        bookObj.setBook_cover_small(basePath + bookObj.getBook_cover_small());
        List<BookChapter> chapterList = bookChapterService.getTreeEpubChapters(bkidstr);
        Hashtable result = new Hashtable();
        result.put("info", bookObj);
        result.put("chapters", chapterList);
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }


    /**
     * 获取图书章节内容
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/v2/api/book/getChapterContent", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getChapterContent(HttpServletRequest request) {
        String bkidstr = request.getParameter("bkid");
        if (StringUtils.isEmpty(bkidstr)) {
            return JsonResult.getError("请提供书的编码");
        }
        Long bkid = 0L;
        try {
            bkid = Long.parseLong(bkidstr);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("图书编码非法");
        }
        String chapteridstr = request.getParameter("chapterid");
        if (StringUtils.isEmpty(chapteridstr)) {
            return JsonResult.getError("请提供章节信息");
        }
        Long chapterid = 0L;
        try {
            chapterid = Long.parseLong(chapteridstr);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("图书章节信息非法");
        }

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;
        BookRepo bookObj = bookRepoService.findByBookId(bkid);
        if (bookObj == null) {
            return JsonResult.getError("该图书不存在");
        }
        BookChapter chapter = bookChapterService.findMyChapterById(bkid, chapterid);
        if (chapter == null) {
            return JsonResult.getError("章节信息不存在");
        }
        String content = loadEpubChapter(bookObj, chapter, basePath);
        Hashtable result = new Hashtable();
        result.put("content", content);
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_CHAPTER_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }

}
