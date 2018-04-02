package com.cjsz.tech.meb.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.beans.MemberIdsBean;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 会员管理
 * Created by Administrator on 2016/10/25.
 */
@Controller
@RequestMapping("/admin/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    /**
     * 会员列表
     *
     * @return
     */
    @RequestMapping("/listAll")
    @ResponseBody
    public Object listAll(HttpServletRequest request, @RequestBody PageConditionBean condition) {
        SysUser sUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "update_time");
            PageList result = (PageList) memberService.pageQuery(sort, condition, sUser.getOrg_id());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 启用，停用
     *
     * @return
     */
    @RequestMapping("/updateStatus")
    @ResponseBody
    public Object saveUser(HttpServletRequest request, @RequestBody MemberIdsBean memberIds) {
        String ids = StringUtils.join(memberIds.getIds(), ",");
        SysUser sUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            memberService.updateMemberStatus(memberIds.getEnabled(), ids);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            jsonResult.setData(new ArrayList());
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
