package com.cjsz.tech.member.ctrl;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.MultiFileForm;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.ExcelUtil;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Author:Jason
 * Date:2017/6/26
 */
@Controller
@RequestMapping("/admin/unifyMember")
public class UnifyMemberController {

    @Autowired
    private UserService userService;
    @Autowired
    private UnifyMemberService unifyMemberService;


    /**
     * 会员列表
     *
     * @return
     */
    @RequestMapping("/listAll")
    @ResponseBody
    public Object listAll(HttpServletRequest request, @RequestBody PageConditionBean bean) {
        SysUser sUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            PageList result = (PageList) unifyMemberService.pageQuery(sort, bean, sUser.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 批量注册账号
     * @param request
     * @param multiFileForm
     * @return
     */
    @RequestMapping(value = "/importMember", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Object importMember(HttpServletRequest request, @RequestBody MultiFileForm multiFileForm) {
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
                    message = unifyMemberService.importExcel(content);
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
}
