package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookChapter;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBook;
import com.cjsz.tech.book.domain.UnBookErr;
import com.cjsz.tech.book.service.BookChapterService;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.book.service.UnBookErrService;
import com.cjsz.tech.book.service.UnBookService;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.epub.EpubAnalyze;
import com.cjsz.tech.utils.images.ImageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 图书解析
 * Created by henry on 2017/08/08 0022.
 */
@Controller
public class UnBookController {

    @Autowired
    private UserService userService;

    @Autowired
    private UnBookService unBookService;

    @Autowired
    private BookRepoService bookRepoService;

    @Autowired
    private UnBookErrService unBookErrService;

    @Autowired
    private BookChapterService bookChapterService;

    /**
     * 创建解析任务
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/unBook/save", method = {RequestMethod.POST})
    @ResponseBody
    public Object unBook_add(HttpServletRequest request, @RequestBody UnBook bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            bean.setUser_id(user.getUser_id());
            if (bean.getSid() == null) {
                unBookService.insertUnBook(bean);
            } else if (bean.getS_status() == null || bean.getS_status() == 0 || bean.getS_status() == 3) {
                unBookService.saveUnBook(bean);
            } else {
                return JsonResult.getException("该任务当前状态无法修改!");
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(bean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取列表
     *
     * @param request
     * @param ben
     * @return
     */
    @RequestMapping(value = "/admin/unBook/dataList", method = {RequestMethod.POST})
    @ResponseBody
    public Object unBook_data_list(HttpServletRequest request, @RequestBody PageConditionBean ben) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            if (ben.getPageNum() == null || ben.getPageSize() == null) {
                return JsonResult.getException("参数错误!");
            }
            Object obj = unBookService.getUnBookAll(ben, sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(obj);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 开始解析
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/unBook/unEpub", method = {RequestMethod.POST})
    @ResponseBody
    public Object unBook_data_start(HttpServletRequest request, @RequestBody UnBook bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");

            if (bean.getSid() == null) {
                return JsonResult.getError("参数异常!");
            }
            if (bean.getS_path() == null) {
                return JsonResult.getError("解析路径异常!");
            }
            if (bean.getS_status() == 1) {
                return JsonResult.getError("解析中!请勿重复操作");
            }
            if (runJob.containsKey(bean.getSid())) {
                return JsonResult.getError("解析中!请勿重复操作");
            }
            if (runJob.size() >= 1) {
                return JsonResult.getError("服务器线程已满， 请等待其他解析任务完成后在继续执行!");
            }
            //立即修改状态
            bean.setS_status(1);
            unBookService.saveUnBook(bean);
            //开启线程
            new Thread(new SplitFilesJob(bean)).start();

            JsonResult jsonResult = JsonResult.getSuccess("文件已开始解析!");
            jsonResult.setData(bean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    Map runJob = new HashMap<>();

    class SplitFilesJob implements Runnable {
        private UnBook unbook;
        private String log_msg = null;

        /**
         * 图书
         *
         * @param unbook
         */
        public SplitFilesJob(UnBook unbook) {
            this.unbook = unbook;
        }

        public void errEnd(String message) {
            System.out.println(message);
            unbook.setS_status(3);
            unBookService.saveUnBook(unbook);
            runJob.remove(unbook.getSid());
        }

        /**
         * 解压epub
         *
         * @return
         */
        public Boolean parseEpubBook(String bookPath, String rootPath, File epub, BookRepo book) {
            String file_name = epub.getName();
            String book_url = bookPath + "/" + file_name;
            boolean rt;
            try {
                //开始解压
                EpubAnalyze analyze = new EpubAnalyze();
                String flag = analyze.openEpubFile(rootPath, epub.getAbsolutePath());
                if (flag == null) {
                    List<EpubAnalyze.EpubCatalog> chapters = analyze.getCatalogList();
                    if (chapters == null || chapters.isEmpty()) {
                        return false;
                    } else {
                        String chapter_file = analyze.getChapterroot();
                        if (chapter_file != null) {
                            chapter_file = chapter_file + "/";
                        } else {
                            chapter_file = "";
                        }

                        if (book != null) {
                            //存在删除以前的章节信息
                            bookChapterService.deleteChapterByBookAndType(book.getBook_id(), ".epub");
                            //存储时就删除 目录 封面 等章节
                            this.outChapterForEpub(chapters, book.getBook_id(), 0L, "", "0|", chapter_file);
                            book.setBook_url(book_url);
                            book.setFile_name(file_name);
                            book.setParse_status(3);//1:未解析;2:解析中;3:已解析
                            book.setUpdate_time(new Date());
                            bookRepoService.updateBookRepo(book);
                        } else {
                            book = new BookRepo();
                            book.setBook_url(book_url);
                            book.setFile_name(file_name);
                            book.setParse_status(3);//1:未解析;2:解析中;3:已解析
                            book.setBook_status(2);//1:上架;2:下架
                            book.setCreate_time(new Date());
                            book.setUpdate_time(new Date());
                            bookRepoService.saveBookRepo(book);
                            book = unBookService.getBookByFileName(epub.getName());
                            if (book != null) {
                                this.outChapterForEpub(chapters, book.getBook_id(), 0L, "", "0|", chapter_file);
                            }
                        }
                    }
                } else {
                    System.out.println(file_name + "=================//==============" + flag);
                }
                log_msg = flag;
                rt = flag == null ? true : false;
            } catch (Exception e) {
                e.printStackTrace();
                rt = false;
            }
            return rt;
        }

        private Map<String, Object> copyCoverToCoverDir(BookRepo repo, String coverPath) {
            try {
                File srcFile = new File(coverPath);
                if (!srcFile.exists()) {
                    return null;
                }
                String cover_name = srcFile.getName();
                String type = StringUtils.substringAfterLast(cover_name, ".");
                String cover_path = "/" + srcFile.getPath().substring(srcFile.getPath().indexOf("szyun")).replaceAll("\\\\", "/");

                Map<String, Object> result = new HashMap<String, Object>();
                result.put("book_cover", cover_path);

                try {
                    ImageHelper.getTransferImageByScale(srcFile.getPath(), "small", 0.5);
                    result.put("book_cover_small", StringUtils.substringBeforeLast(cover_path, ".") + "_small." + type);
                } catch (Exception e) {
                    //TODO
                    //图片为打印格式
                    result.put("book_cover_small", cover_path);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void outChapterForEpub(List<EpubAnalyze.EpubCatalog> catlist, Long book_id, Long pid, String pname, String ppath, String purl) {
            for (int j = 0; j < catlist.size(); j++) {
                String title = catlist.get(j).title;
                if ("封面".equals(title) || "目录".equals(title) || "出版说明".equals(title)) {
                    continue;
                }
                BookChapter book_chapter = new BookChapter();
                book_chapter.setBook_id(book_id);
                book_chapter.setName(catlist.get(j).title);
                if ((purl + catlist.get(j).href).indexOf("/") != -1) {
                    book_chapter.setUrl(StringUtils.substringAfterLast(purl + catlist.get(j).href, "/"));
                    if (catlist.get(j).href.indexOf("../") != -1) {
                        String purl_1 = purl;
                        if (purl != null && purl != "") {
                            purl_1 = "";
                            String[] lr = purl.split("/");
                            for (int k = 0; k < lr.length - 1; k++) {
                                purl_1 = lr[k] + "/";
                            }
                        }
                        catlist.get(j).href = catlist.get(j).href.replace("../", "");
                        book_chapter.setPurl(StringUtils.substringBeforeLast(purl_1 + catlist.get(j).href, "/") + "/");
                    } else {
                        book_chapter.setPurl(StringUtils.substringBeforeLast(purl + catlist.get(j).href, "/") + "/");
                    }
                } else {
                    book_chapter.setUrl(catlist.get(j).href);
                    book_chapter.setPurl("");
                }
                book_chapter.setPname(pname);
                book_chapter.setFormat(".epub");
                book_chapter.setPid(pid);
                book_chapter.setPath(ppath);
                try {
                    book_chapter.setStart_page(Long.valueOf(catlist.get(j).order));
                } catch (Exception e) {
                    book_chapter.setStart_page(0L);
                }
                book_chapter.setCode(catlist.get(j).tag);
                Long chapterid = bookChapterService.saveChapter(book_chapter);

                String curpath = ppath + chapterid + "|";

                if (catlist.get(j).subitem.size() > 0) {
                    outChapterForEpub(catlist.get(j).subitem, book_id, chapterid, book_chapter.getName(), curpath, purl);
                }
            }
        }

        /**
         * 获取图书封面
         */
        public void getBookCover(BookRepo book, String rootPath, String epubName, File root) {
            String cover = book.getBook_cover();
            if (StringUtils.isEmpty(cover) ||(StringUtils.isNotEmpty(cover) && cover.indexOf("attachment")!=-1 )) {
                //验证是否有cover目录
                String coverPath = rootPath + "/cover";
                File coverFile = new File(coverPath);
                if (coverFile.exists()) {
                    String coverImg = coverPath + "/" + epubName.replace(".epub", ".jpg");
                    //验证是否存在封面
                    File imgFile = new File(coverImg);
                    if (imgFile.exists()) {
                        //验证是否已经存在 封面压缩图
                        String cover_img = imgFile.getPath().replace(root.getAbsolutePath(), "").replaceAll("\\\\", "/");
                        String cover_img_small = cover_img.replace(".jpg", "_small.jpg");
                        File small_img = new File(root.getAbsolutePath() + cover_img_small);
                        if (!small_img.exists()) {
                            ImageHelper.getTransferImageByScale(coverImg, "small", 0.5);
                        }
                        book.setBook_cover(cover_img);
                        book.setBook_cover_small(cover_img_small);
                        book.setUpdate_time(new Date());
                        System.out.println(cover_img);
                        System.out.println(cover_img_small);
                        //更新封面
                        bookRepoService.updateBookRepo(book);
                    }
                }
            }
        }


        @Override
        public void run() {
            if (runJob.containsKey(unbook.getSid())) {
                return;
            }
            runJob.put(unbook.getSid(), "run");
            System.out.println("开启线程：" + unbook.getSid());
            try {
                //根目录
                File rootFile = SpringContextUtil.getApplicationContext().getResource("").getFile();
                //解析目录
                String rootPath = rootFile.getAbsolutePath() + "/szyun/books/" + unbook.getS_path();
                System.out.println("开始解析目录：" + rootPath);
                File booksDir = new File(rootPath);
                //目录不存在
                if (!booksDir.exists()) {
                    errEnd("解析路径不存在!");
                    return;
                }
                File[] booksFile = booksDir.listFiles();
                if (booksFile == null || booksFile.length < 1) {
                    errEnd("这个目录是空的!");
                    return;
                }
                List<File> epubList = new ArrayList<>();
                for (File bookfile : booksFile) {
                    if (bookfile.isDirectory()) {
                        System.out.println("不是epub文件:" + bookfile.getName());
                    } else if (bookfile.getAbsolutePath().endsWith(".epub")) {
                        epubList.add(bookfile);
                    } else {
                        System.out.println("不是epub文件:" + bookfile.getName());
                    }
                }
                System.out.println("总文件数量:" + booksFile.length + "/epub文件数量:" + epubList.size());
                unbook.setS_num(epubList.size());
                unbook.setS_status(1);
                unBookService.saveUnBook(unbook);

                //解压目录
                String unBookName = rootFile.getAbsolutePath() + "/szyun/books/" + unbook.getS_path() + "/unfile";
                File unBooksDir = new File(unBookName);
                if (!unBooksDir.exists()) {
                    unBooksDir.mkdirs();
                }
                int success_num = 0;
                int error_unm = 0;
                int for_index = 1;
                //开始循环解压
                for (File epub : epubList) {
                    BookRepo book = unBookService.getBookByFileName(epub.getName());
                    log_msg = null;
                    if (book != null && book.getParse_status() == 3) {
                        //判断是否解析成功
                        System.out.println("=====================曾经解析成功！" + epub.getName());
                        success_num += 1;
                        //在更新封面一次
                        getBookCover(book, rootPath, epub.getName(), rootFile);
                    } else {
                        System.out.println("=====================进程(" + unbook.getS_title() + ")开始解析第" + for_index + "/" + epubList.size() + "本:" + epub.getName() + "=====================");
                        String bookPath = "/szyun/books/" + unbook.getS_path();
                        if (parseEpubBook(bookPath, unBooksDir.getAbsolutePath(), epub, book)) {
                            success_num += 1;
                            //成功后 更新一次封面
                            book = unBookService.getBookByFileName(epub.getName());
                            getBookCover(book, rootPath, epub.getName(), rootFile);
                        } else {
                            error_unm += 1;
                            if (log_msg == null){
                                log_msg = "解析异常";
                            }
                        }
                    }
                    //更新解析任务 图书记录
                    UnBookErr bLog = new UnBookErr();
                    if (book != null) {
                        bLog.setBook_id(book.getBook_id());
                    }
                    bLog.setBook_url(epub.getParent().replace(rootFile.getAbsolutePath(), "").replaceAll("\\\\", "/") + "/" + epub.getName());
                    bLog.setFile_name(epub.getName());
                    bLog.setMessage(log_msg);
                    bLog.setTask_id(unbook.getSid());
                    Long sts = log_msg == null ? 1L : 0L;
                    bLog.setTask_status(sts);
                    unBookErrService.insertUnBookErr(bLog);

                    //更新任务信息
                    for_index++;
                    unbook.setUn_success(success_num);
                    unbook.setUn_error(error_unm);
                    unBookService.saveUnBook(unbook);
                }
                unbook.setUn_success(success_num);
                unbook.setUn_error(error_unm);
                unbook.setS_status(2);
                unbook.setEnd_time(new Date());
                unBookService.saveUnBook(unbook);
                System.out.println("完成解析-结束线程：" + unbook.getSid());
                runJob.remove(unbook.getSid());
            } catch (Exception e) {
                e.printStackTrace();
                errEnd(e.getMessage());
                System.out.println("异常中断-结束线程：" + unbook.getSid());
                runJob.remove(unbook.getSid());
            }
        }
    }
}
