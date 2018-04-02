package com.cjsz.tech.web.ctrl;


import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.SysFileService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.Guid;
import com.cjsz.tech.system.utils.UploadCallBack;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sah on 2016/1/8.
 * 上传
 */
@Controller
public class UploadController {

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private UserService userService;

    public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST, consumes = "multipart/form-data", produces = {"application/json", "application/xml"})
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile file, final HttpServletRequest request, HttpSession session) {
        String type = request.getParameter("type");
        if (StringUtils.isNotEmpty(type) && type.equals("admin")) {
            String token = request.getParameter("token");
            SysUser user = userService.findByToken(token);
            if (user == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
        }
        return processUpload(file, request, new UploadCallBack() {
            @Override
            public Object onSuccess(MultipartFile file, String fileName, String saveFile, String realPath) {
                List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("url", saveFile);
                String name = file.getOriginalFilename();
                map.put("name", name);
                items.add(map);
                JsonResult result = new JsonResult();
                result.setCode(0);
                result.setData(items);
                return result;
            }

            @Override
            public Object onFailure(Exception e) {
                JsonResult result = new JsonResult();
                result.setCode(1);
                return result;
            }

        });
    }

    /**
     * 处理文件上传
     *
     * @param file
     * @param request
     * @return 上传文件的路径
     * @throws IOException
     */
    public static final Object processUpload(MultipartFile file, HttpServletRequest request, UploadCallBack callBack) {
        //构建保存的目录
        ServletContext sc = request.getSession().getServletContext();
        String rootPath = request.getSession().getServletContext().getRealPath("/uploads");
        String dateVar = dateFormatThreadLocal.get().format(new Date());
        String pathDir = rootPath + "/" + dateVar;
        String webPath = "/uploads" + "/" + dateVar;
        //得到保存目录的真实路径
        //根据真实路径创建目录
        File saveFileDir = new File(pathDir);
        if (!saveFileDir.exists()) {
            saveFileDir.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        int inx = fileName.lastIndexOf(".");
        String newfileName = Guid.newId() + fileName.substring(inx);
        //拼成完整的文件保存路径加文件
        String localFilePath = pathDir + File.separator + newfileName;
        File localfile = new File(localFilePath);
        String uploadFilePath = webPath + "/" + newfileName;
        try {
            file.transferTo(localfile);
            return callBack.onSuccess(file, fileName, uploadFilePath, localFilePath);
        } catch (IOException e) {
            return callBack.onFailure(e);
        }
    }


    //图书上传专用
    @RequestMapping(value = "/file/upload/uploadBook", method = RequestMethod.POST, consumes = "multipart/form-data",
            produces = {"application/json", "application/xml"})
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile[] file, HttpServletRequest request) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            String rootPath = request.getSession().getServletContext().getRealPath("/attachment/indexattach");
            String dateVar = dateFormatThreadLocal.get().format(new Date());
            String pathDir = rootPath + "/" + dateVar;
            String webPath = "/attachment/indexattach";
            for (int i = 0; i < file.length; i++) {
                String bookfilename = file[i].getOriginalFilename();
                if (bookfilename.endsWith(".epub") || bookfilename.endsWith(".pdf")) {
                    String uploadFilePath = processUpload(file[i], rootPath, webPath, request);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("url", /*sc.getContextPath() +*/ uploadFilePath);
                    bookfilename = bookfilename.trim().replace("(", "-").replace(")", "");//文件名去空格、去括号改为“-”
                    map.put("name", bookfilename);//
                    items.add(map);
                }
            }
            JsonResult result = new JsonResult();
            if (items.size() > 0) {
                result.setCode(0);
                result.setData(items);
            } else {
                result.setCode(1);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            JsonResult result = new JsonResult();
            result.setCode(1);
            return result;
        }
    }

    /**
     * 处理文件上传
     *
     * @param file
     * @param request
     * @return 上传文件的路径
     * @throws IOException
     */
    public static final String processUpload(MultipartFile file, String rootPath, String webPath, HttpServletRequest request) throws IOException {
        /**根据真实路径创建目录**/
        File saveFileDir = new File(rootPath);
        if (!saveFileDir.exists()) {
            saveFileDir.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        /**拼成完整的文件保存路径加文件**/
        String localFilePath = rootPath + File.separator + fileName;
        File localfile = new File(localFilePath);
        file.transferTo(localfile);
        return webPath + "/" + fileName;
    }


    /**
     * pdf上传
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "/file/upload/uploadPdf", method = RequestMethod.POST, consumes = "multipart/form-data",
            produces = {"application/json", "application/xml"})
    @ResponseBody
    public Object uploadPdf(@RequestParam("file") MultipartFile[] file, HttpServletRequest request) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            String rootPath = request.getSession().getServletContext().getRealPath("/periodical/");
            String dateVar = dateFormatThreadLocal.get().format(new Date());
            String webPath = "/periodical/" + dateVar;
            String pathDir = rootPath + dateVar;
            for (int i = 0; i < file.length; i++) {
                if (!file[i].getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                    continue;
                }
                String uploadFilePath = processUploadPdf(file[i], pathDir, webPath, request);
                Map<String, Object> map = new HashMap<>();
                map.put("url", uploadFilePath);
                String pdffilename = file[i].getOriginalFilename();
                map.put("name", pdffilename);
                items.add(map);
            }
            JsonResult result = new JsonResult();
            if (items.size() > 0) {
                result.setCode(0);
                result.setData(items);
            } else {
                result.setCode(1);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            JsonResult result = new JsonResult();
            result.setCode(1);
            return result;
        }
    }

    /**
     * 处理pdf上传
     *
     * @param file
     * @param request
     * @return 上传文件的路径
     * @throws IOException
     */
    public static final String processUploadPdf(MultipartFile file, String rootPath, String webPath, HttpServletRequest request) throws IOException {
        /**根据真实路径创建目录**/
        File saveFileDir = new File(rootPath);
        if (!saveFileDir.exists()) {
            saveFileDir.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        int inx = fileName.lastIndexOf(".");
        String newfileName = Guid.newId() + fileName.substring(inx);
        /**拼成完整的文件保存路径加文件**/
        String localFilePath = rootPath + File.separator + newfileName;
        File localfile = new File(localFilePath);
        file.transferTo(localfile);
        return webPath + "/" + newfileName;
    }
}
