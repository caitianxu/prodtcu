package com.cjsz.tech.book.ctrl;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.MultiFileForm;
import com.cjsz.tech.book.beans.BookExportNums;
import com.cjsz.tech.book.beans.BookReposBean;
import com.cjsz.tech.book.beans.FindBookRepoBean;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBookErr;
import com.cjsz.tech.book.service.BookRepoService;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.ExcelUtil;
import com.cjsz.tech.utils.ExportExcel;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图书仓库管理
 * Created by shiaihua on 16/12/17.
 */
@Controller
public class BookRepoController {

    @Autowired
    private BookRepoService bookRepoService;

    /**
     * 图书仓库图书查询
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/bookrepo/json/getList", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody FindBookRepoBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object obj = bookRepoService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getError(Constants.EXCEPTION);
        }
    }

    /**
     * 待补全信息各属性数量
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/bookrepo/json/exprotNums", method = RequestMethod.POST)
    @ResponseBody
    public Object exprotNums(HttpServletRequest request){
        try{
            BookExportNums nums = bookRepoService.getNums();
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(nums);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getError(Constants.EXCEPTION);
        }
    }

    /**
     * 图书移除标签的查询、图书添加标签的查询列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/bookrepo/json/getTagBookList", method = RequestMethod.POST)
    @ResponseBody
    public Object getTagBookList(HttpServletRequest request, @RequestBody FindBookRepoBean bean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object obj = null;
            if(bean.getBool()){
                //移除标签里的图书
                obj = bookRepoService.pageQueryIn(sort, bean);
            }else{
                //标签里添加图书
                obj = bookRepoService.pageQueryNotIn(sort, bean);
            }
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getError(Constants.EXCEPTION);
        }
    }

    /**
     * 修改图书仓库中图书基本信息
     * @param request
     * @param repo
     * @return
     */
    @RequestMapping(value = "/admin/bookrepo/json/updateBookRepo", method = RequestMethod.POST)
    @ResponseBody
    public Object updateBookRepo(HttpServletRequest request, @RequestBody BookRepo repo){
        try{
            //验证isbn编号重复————目前ISBN编号可以重复，文件名不能重复
            /*if(StringUtils.isNotEmpty(repo.getBook_isbn())){
                //通过isbn编号查询其他图书
                List<BookRepo> bookRepos = bookRepoService.selectOtherBooksByIsbn(repo.getBook_isbn(), repo.getBook_id());
                if(bookRepos.size()>0){
                    return JsonResult.getError("ISBN编号重复！");
                }
            }*/
            bookRepoService.updateBookRepo(repo);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(repo);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getError(Constants.EXCEPTION);
        }
    }

    /**
     * 图书仓库图书上架/下架
     * @param request
     * @param bean
     */
    @RequestMapping(value = "/admin/bookrepo/json/updateBookStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateBookStatus(HttpServletRequest request, @RequestBody BookReposBean bean){
        try{
            Integer status = 2; //下架
            if(bean.getBool()){
                status = 1;     //上架
            }
            //图书仓库图书上架
            bookRepoService.updateBookStatus(bean, status);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 图书上传解析
     *
     * @return
     */
    @RequestMapping(value = "/admin/bookrepo/json/uploadBookRepo", method = {RequestMethod.POST})
    @ResponseBody
    public Object uploadBookRepo(HttpServletRequest request, @RequestBody MultiFileForm multiFileForm) {
        try {
            List<FileForm> fileForms = multiFileForm.getData();
            if (null != fileForms && fileForms.size() > 0) {
                bookRepoService.uploadBooks(fileForms);
                return JsonResult.getSuccess(Constants.ACTION_UPDATE);
            } else {
                return JsonResult.getSuccess(Constants.ACTION_ERROR);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return JsonResult.getError("更新出错!");
        }
    }

    /**
     * 图书仓库导入图书Excel，修改图书信息
     * @param request
     * @param multiFileForm
     * @return
     */
    @RequestMapping(value = "/admin/bookrepo/json/importBook", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Object importBook(HttpServletRequest request, @RequestBody MultiFileForm multiFileForm) {
        try {
            String message = "";
            List<FileForm> files = multiFileForm.getData();
            if (null != files && files.size() > 0) {
                for (FileForm file : files) {
                    String srcFileName = (String) file.getName();
                    String suffix = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
                    String targetFileName = (String) file.getUrl();
                    String rootPath = SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
                    targetFileName = rootPath + targetFileName;
                    InputStream is = new FileInputStream(targetFileName);
                    Map<Integer, List<String>> content = ExcelUtil.readExcelContent(is,suffix);
                    //保存修改图书仓库图书信息
                    message = bookRepoService.batchImportExcel(content);
                }
            }
            JsonResult result = JsonResult.getSuccess("");
            if(StringUtils.isEmpty(message)){
                result.setMessage("导入成功");
            }else{
                result.setMessage(message);
            }
            result.setData(message);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getError("导入失败!");
        }
    }

    /**
     * 图书仓库图书导出Excel
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/admin/bookrepo/json/exportBook", method = { RequestMethod.GET, RequestMethod.POST })
    public void exportBook(HttpServletRequest request, HttpServletResponse response,@RequestParam("ids") Integer[] ids) {
        try {
            List<BookRepo> bookRepos = bookRepoService.exportBook(ids);
            String title = "图书仓库";
            String[] rowName = {"序号","文件名","书名","作者","ISBN","出版社","出版日期","标签","版权日期","简介","图书id","封面","价格"};
            List<Object[]>  dataList = new ArrayList<Object[]>();
            for(int i=0;i<bookRepos.size();i++){
                BookRepo bookRepo = bookRepos.get(i);
                String tag_names = bookRepo.getTag_names();
                if(StringUtils.isNotEmpty(tag_names)){
                    tag_names = tag_names.replace(",","/");
                }

                String end_time = "";
                if(bookRepo.getEnd_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    end_time = sdf.format(bookRepo.getEnd_time());
                    end_time = end_time.replace("-", "/");
                }
                String publish_time = "";
                if(bookRepo.getPublish_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    publish_time = sdf.format(bookRepo.getPublish_time());
                    publish_time = publish_time.replace("-", "/");
                }
                String book_cover = "";
                if (StringUtils.isNotEmpty(bookRepo.getBook_cover())){
                    book_cover="http://cjszyun.cn"+bookRepo.getBook_cover();
                }
                Object[] obj = {" ", bookRepo.getFile_name(), bookRepo.getBook_name(), bookRepo.getBook_author(), bookRepo.getBook_isbn(),
                        bookRepo.getBook_publisher(), publish_time, tag_names, end_time, bookRepo.getBook_remark(),bookRepo.getBook_id(),book_cover,bookRepo.getPrice()};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName,dataList,response);
            excel.export();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出标签的图书Excel数据
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/admin/bookrepo/json/exportTagBook", method = { RequestMethod.GET, RequestMethod.POST })
    public void exportTagBook(HttpServletRequest request, HttpServletResponse response,@RequestParam("id") Long id) {
        try {
            List<BookRepo> bookRepos = bookRepoService.selectBookByTagId(id);
            String title = "图书";
            String[] rowName = {"序号","文件名","书名","作者","ISBN","出版社","出版日期","标签","版权日期","简介","图书id","封面","价格"};
            List<Object[]>  dataList = new ArrayList<Object[]>();
            for(int i=0;i<bookRepos.size();i++){
                BookRepo bookRepo = bookRepos.get(i);
                String tag_names = bookRepo.getTag_names();
                if(StringUtils.isNotEmpty(tag_names)){
                    tag_names = tag_names.replace(",","/");
                }
                String end_time = "";
                if(bookRepo.getEnd_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    end_time = sdf.format(bookRepo.getEnd_time());
                    end_time = end_time.replace("-", "/");
                }
                String publish_time = "";
                if(bookRepo.getPublish_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    publish_time = sdf.format(bookRepo.getPublish_time());
                    publish_time = publish_time.replace("-", "/");
                }
                String book_cover = "";
                if (StringUtils.isNotEmpty(bookRepo.getBook_cover())){
                    book_cover="http://cjszyun.cn"+bookRepo.getBook_cover();
                }
                Object[] obj = {" ", bookRepo.getFile_name(), bookRepo.getBook_name(), bookRepo.getBook_author(), bookRepo.getBook_isbn(),
                        bookRepo.getBook_publisher(), publish_time, tag_names, end_time, bookRepo.getBook_remark(),bookRepo.getBook_id(),book_cover,bookRepo.getPrice()};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName,dataList,response);
            excel.export();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出选中的图书Excel数据
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/admin/bookrepo/json/exportBooks", method = { RequestMethod.GET, RequestMethod.POST })
    public void exportBooks(HttpServletRequest request, HttpServletResponse response,@RequestParam("ids") Integer[] ids) {
        try {
            String ids_str = StringUtils.join(ids, ",");
            List<BookRepo> bookRepos = bookRepoService.findByBookIds(ids_str);
            String title = "图书";
            String[] rowName = {"序号","文件名","书名","作者","ISBN","出版社","出版日期","标签","版权日期","简介","图书id","封面","价格"};
            List<Object[]>  dataList = new ArrayList<Object[]>();
            for(int i=0;i<bookRepos.size();i++){
                BookRepo bookRepo = bookRepos.get(i);
                String tag_names = bookRepo.getTag_names();
                if(StringUtils.isNotEmpty(tag_names)){
                    tag_names = tag_names.replace(",","/");
                }
                String end_time = "";
                if(bookRepo.getEnd_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    end_time = sdf.format(bookRepo.getEnd_time());
                    end_time = end_time.replace("-", "/");
                }
                String publish_time = "";
                if(bookRepo.getPublish_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    publish_time = sdf.format(bookRepo.getPublish_time());
                    publish_time = publish_time.replace("-", "/");
                }
                String book_cover = "";
                if (StringUtils.isNotEmpty(bookRepo.getBook_cover())){
                    book_cover="http://cjszyun.cn"+bookRepo.getBook_cover();
                }
                Object[] obj = {" ", bookRepo.getFile_name(), bookRepo.getBook_name(), bookRepo.getBook_author(), bookRepo.getBook_isbn(),
                        bookRepo.getBook_publisher(), publish_time, tag_names, end_time, bookRepo.getBook_remark(),bookRepo.getBook_id(),book_cover,bookRepo.getPrice()};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName,dataList,response);
            excel.export();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图书仓库全部导出
     * @param request
     * @param response
     */
    @RequestMapping(value = "/admin/bookrepo/json/allExport", method = {RequestMethod.GET, RequestMethod.POST})
    public void allExport(HttpServletRequest request, HttpServletResponse response){
        try{
            List<Map<String, Object>> books = bookRepoService.getAllBooksBaseInfo();
            String title = "图书";
            String[] rowName = {"序号","图书id", "书名", "作者", "出版社"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(Map<String, Object> book : books){
                Object[] obj = {" ",book.get("book_id"), book.get("book_name"), book.get("book_author"), book.get("book_publisher")};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName, dataList, response);
            excel.export();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 图书解析成功图书信息导出excel
     * @param request
     * @param response
     * @param task_id
     * @throws IOException
     */
    @RequestMapping(value = "/admin/unBook/exportTaskBook", method = { RequestMethod.GET, RequestMethod.POST })
    public void exportTaskBook(HttpServletRequest request, HttpServletResponse response,@RequestParam("task_id") Long task_id) {
        try {
            List<BookRepo> bookRepos = bookRepoService.exportTaskBook(task_id);
            String title = "图书解析数据";
            String[] rowName = {"序号","文件名","书名","作者","ISBN","出版社","出版日期","标签","版权日期","简介","图书id","封面","价格"};
            List<Object[]>  dataList = new ArrayList<Object[]>();
            for(int i=0;i<bookRepos.size();i++){
                BookRepo bookRepo = bookRepos.get(i);
                String tag_names = bookRepo.getTag_names();
                if(StringUtils.isNotEmpty(tag_names)){
                    tag_names = tag_names.replace(",","/");
                }
                String end_time = "";
                if(bookRepo.getEnd_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    end_time = sdf.format(bookRepo.getEnd_time());
                    end_time = end_time.replace("-", "/");
                }
                String publish_time = "";
                if(bookRepo.getPublish_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    publish_time = sdf.format(bookRepo.getPublish_time());
                    publish_time = publish_time.replace("-", "/");
                }
                String book_cover = "";
                if (StringUtils.isNotEmpty(bookRepo.getBook_cover())){
                    book_cover="http://cjszyun.cn"+bookRepo.getBook_cover();
                }
                Object[] obj = {" ", bookRepo.getFile_name(), bookRepo.getBook_name(), bookRepo.getBook_author(), bookRepo.getBook_isbn(),
                        bookRepo.getBook_publisher(), publish_time, tag_names, end_time, bookRepo.getBook_remark(),bookRepo.getBook_id(),book_cover,bookRepo.getPrice()};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName,dataList,response);
            excel.export();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出excel解析失败图书信息
     * @param request
     * @param response
     * @param task_id
     * @throws IOException
     */
    @RequestMapping(value = "/admin/unBook/exportTaskErrBook", method = { RequestMethod.GET, RequestMethod.POST })
    public void exportTaskErrBook(HttpServletRequest request, HttpServletResponse response,@RequestParam("task_id") Long task_id) {
        try {
            List<UnBookErr> unBookErrs = bookRepoService.exportTaskErrBook(task_id);
            String title = "图书解析失败数据";
            String[] rowName = {"序号","图书存放路径","文件名","创建时间","错误描述"};
            List<Object[]>  dataList = new ArrayList<Object[]>();
            for(int i=0;i<unBookErrs.size();i++){
                UnBookErr unBookErr = unBookErrs.get(i);


                String create_time = "";
                if(unBookErr.getCreate_time() != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    create_time = sdf.format(unBookErr.getCreate_time());
                    create_time = create_time.replace("-", "/");
                }

                Object[] obj = {" ",  unBookErr.getBook_url(), unBookErr.getFile_name(),
                        create_time,unBookErr.getMessage()};
                dataList.add(obj);
            }
            ExportExcel excel = new ExportExcel(title,rowName,dataList,response);
            excel.export();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
