package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.service.CommonBookService;
import com.cjsz.tech.api.service.CommonMemberService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.service.BookReviewService;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.service.MemberReadIndexService;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.Guid;
import com.cjsz.tech.system.utils.UploadCallBack;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.StringUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LuoLi on 2017/4/19 0019.
 */
@Controller
public class ApiCenterController {

    @Autowired
    private CommonMemberService commonMemberService;
    @Autowired
    private CommonBookService commonBookService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BookReviewService bookReviewService;
    @Autowired
    private MemberReadIndexService memberReadIndexService;
    @Autowired
    private MemberReadRecordService memberReadRecordService;
    @Autowired
    private DeviceFeedbackService deviceFeedbackService;

    public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    /**
     * 移动端注册
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/register", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object webReg(HttpServletRequest request) {
        String reg_name = request.getParameter("reg_name");
        String member_pwd = request.getParameter("password");
        String nick_name = request.getParameter("nick_name");
        String code = request.getParameter("code");
        Object result = commonMemberService.memberReg(reg_name, member_pwd, nick_name, null, code, "2");
        return result;
    }

    /**
     * 移动端账号密码登录
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/login", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object webLogin(HttpServletRequest request) {
        String login_name = request.getParameter("login_name");
        String login_pwd = request.getParameter("login_pwd");
        Object result = commonMemberService.memberLogin(login_name, login_pwd, "2");
        JsonResult jsonResult = (JsonResult) result;
        //判断是否登录成功
        if (jsonResult.code == 0) {
            HttpSession session = request.getSession(true);
            session.setAttribute("member_info", jsonResult.data);
        }
        return result;
    }

    /**
     * 移动端账号找回密码
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/retrievePwd", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object retrievePwd(HttpServletRequest request) {
        String login_name = request.getParameter("login_name");
        if (StringUtils.isEmpty(login_name)) {
            return JsonResult.getError("请输入账号！");
        }
        String new_password = request.getParameter("new_password");
        if (StringUtils.isEmpty(new_password)) {
            return JsonResult.getError("请输入新密码！");
        }
        // 检查是否注册
        login_name = login_name.toLowerCase();
        Member member = memberService.findByLoginName(login_name, "2");
        if (member == null) {
            return JsonResult.getError("用户名未注册！");
        }
        //保存
        String md5Pwd = PasswordUtil.entryptPassword(new_password);
        member.setMember_pwd(md5Pwd);
        memberService.updateMember(member);
        JsonResult result = JsonResult.getSuccess("密码修改成功！");
        result.setData(new ArrayList());
        return result;
    }

    /**
     * 个人中心————用户评论
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/api/bookReview/getCenterList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getReviewList(HttpServletRequest request, PageConditionBean bean) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                return commonBookService.getOwnReviewQuery(member_token, bean, "2");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改昵称
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/updateNickName", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateNickName(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                String nick_name = request.getParameter("nick_name");
                JsonResult result = commonMemberService.updateNickName(member_token, nick_name, "2");
                if (result.getCode() == 0) {
                    Member member_info = memberService.getMemberInfoByToken(member_token, "2");
                    member_info.setMember_pwd("");
                    request.getSession().setAttribute("member_info", member_info);
                    result.setData(member_info);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 上传头像
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/updateIcon", method = {RequestMethod.GET, RequestMethod.POST},
            consumes = "multipart/form-data", produces = {"application/json", "application/xml"})
    @ResponseBody
    public Object updateIcon(@RequestParam("file") MultipartFile file, final HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                Object icon = processUpload(file, request, new UploadCallBack() {
                    @Override
                    public Object onSuccess(MultipartFile file, String fileName, String saveFile, String realPath) {
                        String icon = saveFile;
                        if (StringUtil.isNotEmpty(icon)) {
                            String path = request.getContextPath();
                            String basePath = request.getScheme() + "://" + request.getServerName();
                            if (request.getServerPort() != 80) {
                                basePath = basePath + ":" + request.getServerPort();
                            }
                            basePath += path;
                            icon = basePath + icon;
                        }
                        return icon;
                    }

                    @Override
                    public Object onFailure(Exception e) {
                        return null;
                    }
                });
                if (icon != null) {
                    JsonResult result = commonMemberService.updateIcon(member_token, icon.toString(), "2");
                    if (result.getCode() == 0) {
                        Member member_info = memberService.getMemberInfoByToken(member_token, "2");
                        member_info.setMember_pwd("");
                        request.getSession().setAttribute("member_info", member_info);
                        result.setData(member_info);
                    }
                    return result;
                } else {
                    return JsonResult.getError("上传失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
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
    public static final Object processUpload(MultipartFile file, HttpServletRequest request, UploadCallBack callBack) {
        /**构建保存的目录**/
        ServletContext sc = request.getSession().getServletContext();
        String rootPath = request.getSession().getServletContext().getRealPath("/uploads");
        String dateVar = dateFormatThreadLocal.get().format(new Date());
        String pathDir = rootPath + "/" + dateVar;
        String webPath = "/uploads" + "/" + dateVar;
        /**得到保存目录的真实路径**/
        /**根据真实路径创建目录**/
        File saveFileDir = new File(pathDir);
        if (!saveFileDir.exists()) {
            saveFileDir.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        int inx = fileName.lastIndexOf(".");
        String newfileName = Guid.newId() + fileName.substring(inx);
        /**拼成完整的文件保存路径加文件**/
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

    /**
     * 是否使用系统头像
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/updateIsSysIcon", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateIsSysIcon(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                String is_sys_icon = request.getParameter("is_sys_icon");
                JsonResult result = commonMemberService.updateIsSysIcon(member_token, is_sys_icon, "2");
                if (result.getCode() == 0) {
                    Member member_info = memberService.getMemberInfoByToken(member_token, "2");
                    member_info.setMember_pwd("");
                    request.getSession().setAttribute("member_info", member_info);
                    result.setData(member_info);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改密码
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/updatePwd", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updatePwd(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                String old_pwd = request.getParameter("old_pwd");
                String new_pwd = request.getParameter("new_pwd");
                Object result = commonMemberService.updatePwd(member_token, old_pwd, new_pwd, "2");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 手机更换
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/updatePhone", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updatePhone(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                String phone = request.getParameter("phone");
                String code = request.getParameter("code");
                JsonResult result = commonMemberService.updatePhone(member_token, phone, code, "2");
                if (result.getCode() == 0) {
                    Member member_info = memberService.getMemberInfoByToken(member_token, "2");
                    member_info.setMember_pwd("");
                    request.getSession().setAttribute("member_info", member_info);
                    result.setData(member_info);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 邮箱更换
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/updateEmail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateEmail(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            } else {
                String email = request.getParameter("email");
                String code = request.getParameter("code");
                JsonResult result = commonMemberService.bindEmail(member_token, email, code, "2");
                if (result.getCode() == 0) {
                    Member member_info = memberService.getMemberInfoByToken(member_token, "2");
                    member_info.setMember_pwd("");
                    request.getSession().setAttribute("member_info", member_info);
                    result.setData(member_info);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/memberInfo/getMemberInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getMemberInfo(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            }
            Member member = memberService.findByToken(member_token, "2");
            if (member == null) {
                return JsonResult.getError("用户超时");
            } else {
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName();
                if (request.getServerPort() != 80) {
                    basePath = basePath + ":" + request.getServerPort();
                }
                basePath += path;
                JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
                Member member_info = memberService.getMemberInfoByToken(member_token, "2");
                member_info.setMember_pwd("");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("nick_name", member_info.getNick_name());
                map.put("icon", member_info.getIcon());
                map.put("is_sys_icon", member_info.getIs_sys_icon());
                map.put("sys_icon", basePath + "/static/web/img/lv/r9s1g" + member_info.getGrade_id() + ".gif");
                map.put("grade_id", member_info.getGrade_id());
                map.put("grade_title", member_info.getGrade_title());

                //今日已读、累计时长、已读本数、阅读排名
                //今日已读
                Integer today_read_time = memberReadRecordService.getTodayReadTime(member_info.getMember_id());
                today_read_time = today_read_time != null ? today_read_time : 0;
                if (today_read_time != null) {
                    map.put("today_read_time", today_read_time);
                } else {
                    map.put("today_read_time", 0);
                }
                //累计时长、阅读排名
                Map<String, Object> memberOrder = memberReadIndexService.getMemberOrder(member_info.getMember_id());
                if (memberOrder != null) {
                    Integer total_time = memberOrder.get("total_time") != null ? Integer.valueOf(memberOrder.get("total_time").toString()) : 0;
                    Double read_order = memberOrder.get("read_order") != null ? ((Double) memberOrder.get("read_order")).intValue() : 0.0;
                    map.put("total_time", total_time + today_read_time);//累计时长
                    map.put("read_order", read_order);//阅读排名
                } else {
                    map.put("total_time", 0);
                    map.put("read_order", 0);
                }
                //已读本数
                Integer read_count = memberReadRecordService.getReadBookCount(member_info.getMember_id());
                if (read_count != null) {
                    map.put("read_count", read_count);
                } else {
                    map.put("read_count", 0);
                }
                Integer review_count = bookReviewService.getReviewCountByMemberId(member_info.getMember_id());
                if (review_count != null) {
                    map.put("review_count", review_count);
                } else {
                    map.put("review_count", 0);
                }
                map.put("phone", member_info.getPhone());
                map.put("email", member_info.getEmail());
                result.setData(map);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 提交意见反馈
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/suggest/commitSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object commitSuggest(HttpServletRequest request) {
        try {
            String device_type = request.getParameter("device_type");
            String content = request.getParameter("content");
            if (StringUtils.isEmpty(content)) {
                return JsonResult.getError("没有建议内容");
            }
            String member_token = request.getParameter("member_token");
            if (member_token == null) {
                return JsonResult.getError(Constants.TOKEN_FAILED);
            }
            Member member = memberService.findByToken(member_token, "2");
            if (member == null) {
                return JsonResult.getError("用户超时");
            } else {
                String device_type_code = "000004";
                if (StringUtils.isNotEmpty(device_type)) {
                    if ("android".equals(device_type)) {
                        device_type_code = "000003";
                    } else if ("ios".equals(device_type)) {
                        device_type_code = "000002";
                    } else if ("screen".equals(device_type)) {
                        device_type_code = "000001";
                    }
                }
                DeviceFeedback feedback = new DeviceFeedback();
                feedback.setOrg_id(member.getOrg_id());
                feedback.setUser_id(member.getMember_id());
                feedback.setUser_name(member.getNick_name());
                feedback.setUser_type(2);//1:管理员用户;2:会员用户
                feedback.setOpinion(content);
                feedback.setDevice_type_code(device_type_code);
                deviceFeedbackService.saveDeviceFeedback(feedback);
                JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
                result.setData(feedback);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
