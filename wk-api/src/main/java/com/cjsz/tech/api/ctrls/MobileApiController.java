package com.cjsz.tech.api.ctrls;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.beans.RecordTypeEnum;
import com.cjsz.tech.api.beans.ReviewBeans;
import com.cjsz.tech.api.service.CommonValidCodeService;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.BookInfo;
import com.cjsz.tech.book.beans.BookReviewBean;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.BookReview;
import com.cjsz.tech.book.service.*;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.member.beans.BalanceBean;
import com.cjsz.tech.member.beans.MemberInfo;
import com.cjsz.tech.member.beans.MemberInfoJSONBean;
import com.cjsz.tech.member.beans.UnifyMemeberConstants;
import com.cjsz.tech.member.domain.UnifyMember;
import com.cjsz.tech.member.enums.ClientType;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.utils.HttpClientUtil;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author:Jason
 * Date:2017/6/26
 */
@Controller
@RequestMapping("/v2/api/mobile")
public class MobileApiController {
    @Autowired
    private UnifyMemberService unifyMemberService;
    @Autowired
    private BookOrgRelService bookOrgRelService;
    @Autowired
    private BookIndexRecordService bookIndexRecordService;
    @Autowired
    private BookRepoService bookRepoService;
    @Autowired
    private BookReviewService bookReviewService;
    @Autowired
    private CommonValidCodeService commonValidCodeService;
    @Autowired
    private OrganizationService organizationService;


    public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    public static boolean clientTypeQY(String clientType) {
        if (ClientType.ORG_VERSION.code().equals(clientType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean clientTypeDZ(String clientType) {
        if (ClientType.PUBLIC_VERSION.code().equals(clientType)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 会员注册
     *
     * @param request
     * @param memberInfo
     * @return
     */
    @RequestMapping(value = "/registe", method = {RequestMethod.POST})
    @ResponseBody
    public Object registe(HttpServletRequest request, MemberInfo memberInfo) {
        if (StringUtils.isEmpty(memberInfo.getAccount())) {
            return JsonResult.getError("请输入账号");
        }
        if (StringUtils.isEmpty(memberInfo.getPwd())) {
            return JsonResult.getError("请输入密码");
        }
        Boolean flag = bookRepoService.containsEmoji(memberInfo.getAccount());
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }
        //判断app客户端类型
        if (StringUtils.isEmpty(memberInfo.getClinet_type())) {
            memberInfo.setClinet_type(ClientType.ORG_VERSION.code());
        }
        return unifyMemberService.registe(memberInfo);
    }

    /**
     * 网站注册
     * 只能手机号和邮箱注册
     *
     * @param request
     * @param memberInfo
     * @return
     */
    @RequestMapping(value = "/webRegister", method = {RequestMethod.POST})
    @ResponseBody
    public Object webRegister(HttpServletRequest request, MemberInfo memberInfo) {
        if (StringUtils.isEmpty(memberInfo.getAccount())) {
            return JsonResult.getError("请输入手机号");
        }
        Boolean flag = bookRepoService.containsEmoji(memberInfo.getAccount());
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }
        String dept_id = request.getParameter("dept_id");
        if (StringUtils.isNotEmpty(dept_id)){
            memberInfo.setDept_id(Long.parseLong(dept_id));
        }
        //判断app客户端类型
        if (StringUtils.isEmpty(memberInfo.getClinet_type())) {
            memberInfo.setClinet_type(ClientType.ORG_VERSION.code());
        }
        return unifyMemberService.webRegister(memberInfo);
    }


    /**
     * 会员登陆
     *
     * @param request
     * @param memberInfo
     * @return
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public Object login(HttpServletRequest request, MemberInfo memberInfo) {
        if (StringUtils.isEmpty(memberInfo.getAccount())) {
            return JsonResult.getError("请输入账号");
        }
        if (StringUtils.isEmpty(memberInfo.getPwd())) {
            return JsonResult.getError("请输入密码");
        }
        if (StringUtils.isEmpty(memberInfo.getToken_type())) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        Boolean flag = bookRepoService.containsEmoji(memberInfo.getAccount());
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }
        Boolean flags = bookRepoService.containsEmoji(memberInfo.getPwd());
        if (flags == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }

        String org_id = request.getParameter("org_id");
        if (StringUtils.isEmpty(org_id)) {
            String serverName = request.getServerName();
            int len = serverName.indexOf(".");
            if (len != -1) {
                serverName = serverName.substring(0, len);
                if ("cjszyun".equals(serverName) || "192".equals(serverName) || "localhost".equals(serverName)|| "www".equals(serverName)) {
                    serverName = "cjzww";
                }
            } else {
                serverName = "cjzww";
            }
            String sql = "select * from pro_org_extend where server_name ='" + serverName + "'";
            Map<String, Object> map = null;
            try {
                JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContextUtil.getBean("jdbcTemplate");
                map = jdbcTemplate.queryForMap(sql);
                org_id = map.get("org_id").toString();
            } catch (Exception e) {
                return JsonResult.getError("无机构信息");
            }
        }
        Long org_Id = Long.parseLong(org_id);
        //判断app客户端类型
        if (StringUtils.isEmpty(memberInfo.getClinet_type())) {
            memberInfo.setClinet_type(ClientType.ORG_VERSION.code());
        }
        JsonResult jsonResult = unifyMemberService.login(memberInfo, org_Id);
        //判断是否登录成功
        if (jsonResult.code == 0) {
            HttpSession session = request.getSession(true);
            session.setAttribute("member_info", jsonResult.data);
        }
        return jsonResult;
    }


    /**
     * 修改密码
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/changePwd", method = {RequestMethod.POST})
    @ResponseBody
    public Object chagePwd(HttpServletRequest request, @RequestParam String token, @RequestParam String token_type, @RequestParam String newPwd) {
        String client_type = request.getParameter("client_type");
        UnifyMember member = null;
        if (token != null) {
            member = unifyMemberService.findByToken(token, token_type);
        }
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        Boolean flag = bookRepoService.containsEmoji(newPwd);
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }
        //判断app客户端类型
        if (StringUtils.isEmpty(client_type)) {
            client_type = ClientType.ORG_VERSION.code();
        }
        String org_id = request.getParameter("org_id");
        if (StringUtils.isNotEmpty(org_id)){
            //查询机构密码是否锁定
            Boolean is_lock = organizationService.orgIsLock(Long.parseLong(org_id));
            if (is_lock){
                return JsonResult.getError("密码被锁定，无法修改，请联系管理员！");
            }
        }
        JsonResult jsonResult = (JsonResult) unifyMemberService.chagePwd(member.getToken(), newPwd, client_type);
        //判断是否登录成功
        if (jsonResult.code == 0) {
            HttpSession session = request.getSession(true);
            session.setAttribute("member_info", jsonResult.data);
        }
        return jsonResult;
    }


    /**
     * 忘记密码修改新密码
     *
     * @param request
     * @param account
     * @param newPwd
     * @return
     */
    @RequestMapping(value = "/forgetPwd", method = {RequestMethod.POST})
    @ResponseBody
    public Object forgetPwd(HttpServletRequest request, @RequestParam String account, @RequestParam String newPwd) {
        Boolean flag = bookRepoService.containsEmoji(newPwd);
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }
        String client_type = request.getParameter("client_type");
        //判断app客户端类型
        if (StringUtils.isEmpty(client_type)) {
            client_type = ClientType.ORG_VERSION.code();
        }
        String org_id = request.getParameter("org_id");
        if (StringUtils.isNotEmpty(org_id)){
            //查询机构密码是否锁定
            Boolean is_lock = organizationService.orgIsLock(Long.parseLong(org_id));
            if (is_lock){
                return JsonResult.getError("密码被锁定，无法修改，请联系管理员！");
            }
        }
        return unifyMemberService.forgetPwd(account, newPwd, client_type);
    }


    /**
     * 用户信息
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/loadUserInfo", method = {RequestMethod.POST})
    @ResponseBody
    public Object loadUserInfo(HttpServletRequest request, @RequestParam String token) {
        return unifyMemberService.loadUserInfo(token);
    }

    /**
     * 用户详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/userInfo", method = {RequestMethod.POST})
    @ResponseBody
    public Object userInfo(HttpServletRequest request) {
        String member_id = request.getParameter("member_id");
        return unifyMemberService.userInfo(Long.valueOf(member_id));
    }

    /**
     * 会员用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/memberInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object memberInfo(HttpServletRequest request) {
        try {
            String token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = unifyMemberService.findByToken(token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("token", token);
            Long member_balance;
            String result = null;
            try {
                result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.USER_ACCOUNT), params);
                if (StringUtils.isEmpty(result)) {
                    return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                }
                MemberInfoJSONBean resultInfo = JSONObject.parseObject(result, MemberInfoJSONBean.class);
                if (resultInfo.getCode() == 0) {
                    BalanceBean bean = resultInfo.getData();
                    member_balance = Long.parseLong(bean.getBalance());
                } else {
                    return JsonResult.getError(resultInfo.getMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }

            member.setBalance(member_balance);
            Integer sum = unifyMemberService.getSum(member.getMember_id());
            if (sum == null) {
                sum = 0;
            }
            member.setReview_num(sum);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(member);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 绑定手机
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/memberInfo/updatePhone", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updatePhone(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("token");
            String phone = request.getParameter("phone");
            String code = request.getParameter("code");
            String client_type = request.getParameter("client_type");
            //判断app客户端类型
            if (StringUtils.isEmpty(client_type)) {
                client_type = ClientType.ORG_VERSION.code();
            }
            if (member_token == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (StringUtils.isEmpty(phone)) {
                return JsonResult.getObjError("请输入手机号码！");
            }
            if (StringUtils.isEmpty(code)) {
                return JsonResult.getObjError("请输入手机验证码！");
            }
            String token_type = request.getParameter("token_type");
            // 检查是否注册
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            //判断验证码是否正确
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService("changePhone", code, phone);
            if (jsonCode.getCode() != 0) {
                return jsonCode;
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("token", member_token);
            params.put("mob", phone);
            String result = null;
            try {
                if (clientTypeQY(client_type)) {
                    //企业版
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.BIND_MOBI), params);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                } else if (clientTypeDZ(client_type)) {
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.BIND_MOBI), params);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                }
            } catch (Exception e) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            JSONObject resultInfo = JSONObject.parseObject(result);
            System.out.println(resultInfo.toJSONString());
            String resultCode = String.valueOf(resultInfo.get("code"));
            if ("0".equals(resultCode)) {
                unifyMemberService.bindPhone(member.getMember_id(), phone);
                UnifyMember unifyMember = unifyMemberService.findUserByToken(member_token);
                JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.CHANGE_OK);
                jsonResult.setData(unifyMember);
                //判断是否登录成功
                if (jsonResult.code == 0) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("member_info", jsonResult.data);
                }
                return jsonResult;
            } else {
                return JsonResult.getError((String) resultInfo.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 绑定邮箱
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/memberInfo/updateEmail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateEmail(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("token");
            String email = request.getParameter("email");
            String code = request.getParameter("code");
            String client_type = request.getParameter("client_type");
            //判断app客户端类型
            if (StringUtils.isEmpty(client_type)) {
                client_type = ClientType.ORG_VERSION.code();
            }

            if (member_token == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(email)) {
                return JsonResult.getObjError("请输入邮箱！");
            }
            if (StringUtil.isEmpty(code)) {
                return JsonResult.getObjError("请输入邮箱验证码！");
            }
            String token_type = request.getParameter("token_type");
            // 检查是否注册
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            //判断验证码是否正确
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService("changeEmail", code, email);
            if (jsonCode.getCode() != 0) {
                return jsonCode;
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("token", member_token);
            params.put("mail", email);
            String result = null;
            try {
                if (clientTypeQY(client_type)) {
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getInvokeApiUrl(UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.BIND_EMAIL), params);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                } else if (clientTypeDZ(client_type)) {
                    result = HttpClientUtil.httpPostRequest(UnifyMemeberConstants.getNewInvokeApiUrl(UnifyMemeberConstants.API_KEY_DZ, UnifyMemeberConstants.API_SECRET_DZ, UnifyMemeberConstants.Module.USER, UnifyMemeberConstants.Module.Action.BIND_EMAIL), params);
                    if (StringUtils.isEmpty(result)) {
                        return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
                    }
                }
            } catch (Exception e) {
                return JsonResult.getError(UnifyMemeberConstants.TipInfo.REQUEST_FAILURE);
            }
            JSONObject resultInfo = JSONObject.parseObject(result);
            System.out.println(resultInfo.toJSONString());
            String resultCode = String.valueOf(resultInfo.get("code"));
            if ("0".equals(resultCode)) {
                unifyMemberService.bindEmail(member.getMember_id(), email);
                UnifyMember unifyMember = unifyMemberService.findUserByToken(member_token);
                JsonResult jsonResult = JsonResult.getSuccess(UnifyMemeberConstants.TipInfo.CHANGE_OK);
                jsonResult.setData(unifyMember);
                //判断是否登录成功
                if (jsonResult.code == 0) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("member_info", jsonResult.data);
                }
                return jsonResult;
            } else {
                return JsonResult.getError((String) resultInfo.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 会员机构绑定
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/bindOrg", method = {RequestMethod.POST})
    @ResponseBody
    public Object bindOrg(HttpServletRequest request, @RequestParam String token, @RequestParam Long orgId, @RequestParam String token_type) {
        return unifyMemberService.bindOrg(token, orgId, token_type);
    }

    /**
     * 更改会员基本信息
     *
     * @param request
     * @param token
     * @param nickName
     * @param sex
     * @return
     */
    @RequestMapping(value = "/modifyUserInfo", method = {RequestMethod.POST})
    @ResponseBody
    public Object modifyUserInfo(HttpServletRequest request, @RequestParam String token, @RequestParam String token_type, String sign, String nickName, Integer sex, String client_type) {
        if (StringUtils.isNotEmpty(nickName)) {
            Boolean flag = bookRepoService.containsEmoji(nickName);
            if (flag == true) {
                return JsonResult.getOther("请勿输入非法字符");
            }
        }
        //判断app客户端类型
        if (StringUtils.isEmpty(client_type)) {
            client_type = ClientType.ORG_VERSION.code();
        }
        JsonResult jsonResult = (JsonResult) unifyMemberService.modifyUserInfo(token, nickName, sign, sex, token_type, client_type);
        //判断是否登录成功
        if (jsonResult.code == 0) {
            HttpSession session = request.getSession(true);
            session.setAttribute("member_info", jsonResult.data);
        }
        return jsonResult;
    }


    /**
     * 发送手机或者邮箱验证码 （通过账号区分是手机还是邮箱）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/validCode/sendValidCode", method = {RequestMethod.POST})
    @ResponseBody
    public Object sendVerificationCode(HttpServletRequest request) {
        try {
            String account = request.getParameter("account");
            String type = request.getParameter("type");
            return commonValidCodeService.sendVerificationCode(account, type);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 检验验证码（修改邮箱、手机时，原邮箱、手机验证）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/validCode/matchValidCode", method = {RequestMethod.POST})
    @ResponseBody
    public Object matchVerificationCode(HttpServletRequest request) {
        try {
            String type = request.getParameter("type");
            String account = request.getParameter("account");
            String code = request.getParameter("code");
            account = account.toLowerCase();
            JsonResult jsonCode = commonValidCodeService.matchVerificationCodeService(type, code, account);
            return jsonCode;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 移动端图书详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/book/getBookDetailInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookInfo(HttpServletRequest request) {
        try {
            String bookid = request.getParameter("book_id");
            Long member_id = 0L;
            Long org_id = 1L;
            String token = request.getParameter("token");
            String token_type = request.getParameter("token_type");
            if (token != null) {
                UnifyMember member = unifyMemberService.findByToken(token, token_type);
                if (member != null) {
                    member_id = member.getMember_id();
                    if (member.getOrg_id() != null && member.getOrg_id() != 0 && member.getOrg_id() != -1) {
                        org_id = member.getOrg_id();
                    } else {
                        return JsonResult.getObjError("还未绑定机构!");
                    }
                } else {
                    return JsonResult.getExpire(Constants.TOKEN_FAILED);
                }
            } else {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            //1.获取图书详情信息
            BookBean bookInfo = bookOrgRelService.findByOrgIdAndBookIdAndMemberId(org_id, Long.valueOf(bookid), member_id);
            if (bookInfo == null) {
                return JsonResult.getOther("你的机构未购买此书，无法阅读!");
            }
            //2.记录用户点击记录
            BookIndexRecord bookIndexRecord = new BookIndexRecord(org_id, Long.valueOf(bookid), 2, member_id, RecordTypeEnum.CLICK.code(), new Date(), token_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(bookInfo);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 相关推荐接口（通过同作者、同分类进行过滤）返回数量可设置，不设置返回6条
     *
     * @param request
     * @return
     */
    @RequestMapping("/book/listSuggest")
    @ResponseBody
    public Object listSuggest1(HttpServletRequest request, @RequestParam Long org_id, @RequestParam Long bookId, @RequestParam Long bookCatId, @RequestParam Integer num) {
        Integer limit = null != num ? num : 6;
        try {
            if (org_id == null) {
                return JsonResult.getObjError("还未绑定机构!");
            }
            List<BookInfo> datas = Lists.newArrayList();
            //1.获取同作者的的相关推荐
            List<BookInfo> sameAuthorBooks = bookOrgRelService.findSameAuthorBooks(bookId, org_id, limit);
            if (sameAuthorBooks != null && sameAuthorBooks.size() > 0) {
                limit -= sameAuthorBooks.size();
                datas.addAll(sameAuthorBooks);
            }
            List<Long> book_ids = new ArrayList<>();
            String bookIds = "";
            if (sameAuthorBooks.size() > 0) {
                for (BookInfo bookInfo : sameAuthorBooks) {
                    book_ids.add(bookInfo.getBook_id());
                }
                bookIds = StringUtils.join(book_ids, ",");
            }
            //2.获取同分类的相关推荐
            if (limit > 0) {
                List<BookInfo> sameCatalogBooks = bookOrgRelService.findSameCatalogBooks(bookId, org_id, bookCatId, limit, bookIds);//需要明确指定是那个分类，因为同一本图书可能在不同的分类下面
                if (sameCatalogBooks != null && sameCatalogBooks.size() > 0) {
                    datas.addAll(sameCatalogBooks);
                }
            }
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(datas);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 图书评论列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bookReview/list", method = {RequestMethod.POST})
    @ResponseBody
    public Object getBookReviewList(HttpServletRequest request, PageConditionBean bean) {
        String book_id = request.getParameter("book_id");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        } else {
            try {
                Long.parseLong(book_id);
            } catch (Exception e) {
                return JsonResult.getError("图书非法");
            }
        }
        BookRepo repoBook = bookRepoService.findByBookId(Long.parseLong(book_id));
        if (repoBook == null) {
            return JsonResult.getError(APIConstants.NO_BOOK);
        }
        String org_id = request.getParameter("org_id");
        Sort sort = new Sort(Sort.Direction.DESC, "create_time");
        Object obj = bookReviewService.getReviewsList(sort, bean, Long.valueOf(org_id), Long.valueOf(book_id));
        JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
        result.setData(obj);
        return result;

    }

    /**
     * 单条评论详情（包含评论的回复+本评论信息）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bookReview/getReview", method = {RequestMethod.POST})
    @ResponseBody
    public Object getReview(HttpServletRequest request, @RequestParam Long reviewId, PageConditionBean bean) {
        BookReviewBean bookReview = bookReviewService.selectReviewById(reviewId);
        Sort sort = new Sort(Sort.Direction.DESC, "create_time");
        PageList subBookReview = bookReviewService.getReviewsByPid(sort, bean, reviewId);
        ReviewBeans reviewBeans = new ReviewBeans();
        reviewBeans.setBookReview(bookReview);
        reviewBeans.setObject(subBookReview);
        JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
        result.setData(reviewBeans);
        return result;
    }


    /**
     * 发布、回复评论
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bookReview/addReview", method = {RequestMethod.POST})
    @ResponseBody
    public Object addReview(HttpServletRequest request) {
        String member_token = request.getParameter("member_token");
        String token_type = request.getParameter("token_type");
        if (StringUtil.isEmpty(member_token)) {
            return JsonResult.getExpire(Constants.TOKEN_FAILED);
        }
        UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
        if (member == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }

        String book_id = request.getParameter("book_id");
        if (StringUtil.isEmpty(book_id)) {
            return JsonResult.getError(APIConstants.MEMBER_SHELF_NOTHING_ERROR);
        }
        BookRepo book = bookRepoService.findByBookId(Long.parseLong(book_id));
        if (book == null) {
            return JsonResult.getError(APIConstants.NO_BOOK);
        }
        String review_content = request.getParameter("review_content");
        if (StringUtil.isEmpty(review_content)) {
            return JsonResult.getError(APIConstants.REVIEW_CONTENT_NOTHING_ERROR);
        }
        Boolean flag = bookRepoService.containsEmoji(review_content);
        if (flag == true) {
            return JsonResult.getOther("请勿输入非法字符");
        }

        String book_type = request.getParameter("book_type");
        if (StringUtils.isEmpty(book_type)) {
            book_type = "2";
        }
        Integer bk_type = Integer.parseInt(book_type);

        BookReview review = new BookReview();
        review.setPid(0L);
        review.setMember_id(member.getMember_id());
        review.setBook_id(Long.parseLong(book_id));
        review.setReview_content(review_content);
        review.setOrg_id(member.getOrg_id());
        review.setCreate_time(new Date());
        review.setReview_nums(0L);
        review.setNick_name(member.getNick_name());
        review.setBook_name(book.getBook_name());
        review.setBook_cover_small(book.getBook_cover_small());
        review.setBook_type(bk_type);

        String pid = request.getParameter("pid");
        if (StringUtil.isEmpty(pid)) {
            bookReviewService.saveBookReview(review, token_type);
        } else {
            //评论回复
            BookReviewBean bookReview = bookReviewService.selectReviewById(Long.parseLong(pid));
            if (bookReview == null) {
                return JsonResult.getError(APIConstants.NO_BOOK_REVIEW);
            }
            review.setPid(Long.parseLong(pid));
            review.setFull_path(bookReview.getFull_path());
            review.setBook_type(2);
            bookReviewService.saveBookReviewResponse(review, token_type);
        }
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.BOOK_REVIEW_ADD_SUCCESS);
        jsonResult.setData(review);
        return jsonResult;
    }


    /**
     * 删除评论（只能删除自己的，状态删除）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/bookReview/deleteReviews", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object deleteReviews(HttpServletRequest request) {
        try {
            String review_id = request.getParameter("review_id");
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            UnifyMember member = null;
            if (member_token != null) {
                member = unifyMemberService.findByToken(member_token, token_type);
            }
            if (member == null) {
                return JsonResult.getExpire(Constants.OVER_TIME);
            }
            if (review_id == null) {
                return JsonResult.getError(Constants.EXCEPTION);
            }
            BookReview bookReview = bookReviewService.selectNotMyReviewsByIds(member.getMember_id(), Long.parseLong(review_id));
            if (bookReview != null) {
                return JsonResult.getError("不能删除其他用户的评论！");
            }
            BookReview bookReview1 = bookReviewService.selectById(Long.parseLong(review_id));
            if (bookReview1 != null) {
                if (bookReview1.getPid() == 0) {
                    bookReviewService.deleteReview(Long.parseLong(review_id));
                } else {
                    bookReviewService.deleteReviewChild(Long.parseLong(review_id));
                }
            } else {
                return JsonResult.getError("评论不存在");
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 修改头像
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/memberInfo/updateIcon", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult updateIcon1(HttpServletRequest request) {
        try {
            String member_token = request.getParameter("member_token");
            String token_type = request.getParameter("token_type");
            String icon = request.getParameter("icon");
            if (StringUtil.isEmpty(member_token)) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (StringUtil.isEmpty(icon)) {
                return JsonResult.getObjError("请上传头像！");
            }
            // 检查是否注册
            UnifyMember member = unifyMemberService.findByToken(member_token, token_type);
            if (member == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            //修改头像
            unifyMemberService.updateIcon(member.getMember_id(), icon);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            UnifyMember unifyMember = unifyMemberService.findUserByToken(member_token);
            result.setData(unifyMember);

            //判断是否登录成功
            if (result.code == 0) {
                HttpSession session = request.getSession(true);
                session.setAttribute("member_info", result.data);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
