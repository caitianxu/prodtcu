package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.conditions.PageAndSortCondition;
import com.cjsz.tech.system.domain.SysFile;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.SysFileService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.FileTypeEnum;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * Author:Jason
 * Date:2016/11/28
 */
@Controller
@RequestMapping("/admin/fileCenter")
public class SysFileController {
    private static final String PID_KEY = "pid";
    private static final String FOLDERNAME_KEY = "folderName";
    private static final String FILEID_KEY = "file_id";
    private static final String NEWFILENAME_KEY = "newFileName";
    private static final String DIR_ID_KEY = "file_id";


    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private UserService userService;

    /**
     * 新建文件夹
     *
     * @param request
     * @param params  {pid:父id  folderName:文件夹名称}
     * @return
     */
    @RequestMapping("/addFolder")
    @ResponseBody
    public Object addFolder(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));
        ;
        Long pid = (Long) params.get(PID_KEY);
        String folderName = (String) params.get(FOLDERNAME_KEY);
        if (pid == null) {
            pid = 0L;
        }

        SysFile sysFile = new SysFile();
        sysFile.setCreate_time(new Date());
        sysFile.setUpdate_time(new Date());
        sysFile.setFile_name(folderName);
        sysFile.setFile_type(FileTypeEnum.FOLDER.code());
        sysFile.setCreate_userid(sysUser.getUser_id());
        sysFile.setOrg_id(sysUser.getOrg_id());
        sysFile.setPid(pid);//放到指定父节点下,“0”为根节点
        try {
            sysFileService.addFile(sysFile);
            SysFile parent = sysFileService.findById(pid);
            if (parent == null) {
                sysFile.setFullPath("0|" + sysFile.getFile_id() + "|");
            } else {
                sysFile.setFullPath(parent.getFullPath() + sysFile.getFile_id() + "|");
            }
            sysFileService.update(sysFile);
            JsonResult result = JsonResult.getSuccess("文件夹创建成功!");
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getError("文件夹创建失败!");
        }
    }

    /**
     * 文件重命名
     *
     * @param request
     * @param params  {file_id:文件id  newFileName:新名称}
     * @return
     */
    @RequestMapping("/rename")
    @ResponseBody
    public Object rename(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long id = (Long) params.get(FILEID_KEY);
        String newFileName = (String) params.get(NEWFILENAME_KEY);
        try {
            sysFileService.renameFile(id, newFileName);
            JsonResult result = JsonResult.getSuccess("文件夹创建成功!");
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getError("文件夹创建失败!");
        }
    }

    /**
     * 删除指定id集合的文件
     *
     * @param request
     * @param params  {ids:["1","2","3"]}
     * @return
     */
    @RequestMapping("/del")
    @ResponseBody
    public Object del(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        String[] ids = (String[]) params.get("ids");
        String basePath = request.getSession().getServletContext().getRealPath("/");
        try {
            sysFileService.delFiles(ids, basePath);
            JsonResult result = JsonResult.getSuccess("删除成功!");
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            return JsonResult.getError("删除失败!");
        }
    }


    /**
     * 查询该机构下面的所有匹配的文件（排序 默认：降序）
     *
     * @param request
     * @param pageCondition {pageNum：1，pageSize：10，searchText："xxx",sort_field:"create_time",sort_order:"desc"}
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object listAll(HttpServletRequest request, @RequestBody PageAndSortCondition pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            PageList result = (PageList) sysFileService.pageQuery(pageCondition, sysUser.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param request
     * @param params  {file_id:文件id,pageNum:1,pageSize:10,sort_field:"create_time",sort_order:"desc"}
     * @return
     */
    @RequestMapping("/listdir")
    @ResponseBody
    public Object listdir(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long dirId = (Long) params.get(DIR_ID_KEY);
        if (dirId == null) {
            dirId = 0L;
        }
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        String sort_field = (String) params.get("sort_field");
        String sort_order = (String) params.get("sort_order");
        PageAndSortCondition condition = new PageAndSortCondition();
        condition.setPageNum(pageNum);
        condition.setPageSize(pageSize);
        condition.setSort_field(sort_field);
        condition.setSort_order(sort_order);
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            PageList result = (PageList) sysFileService.pageQueryOfDir(condition, dirId, sysUser.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
