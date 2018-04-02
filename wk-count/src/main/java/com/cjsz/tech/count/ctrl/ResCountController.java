package com.cjsz.tech.count.ctrl;

import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.cms.service.ArticleService;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.journal.service.JournalService;
import com.cjsz.tech.journal.service.NewspaperService;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.meb.service.MemberService;
import com.cjsz.tech.media.service.AudioService;
import com.cjsz.tech.media.service.VideoService;
import com.cjsz.tech.member.service.UnifyMemberService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源统计
 * Created by DaiXiaoFeng on 2017/5/11.
 */
@Controller
public class ResCountController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private BookOrgRelService bookOrgRelService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private NewspaperService newsPaperService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private AudioService audioService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UnifyMemberService unifyMemberService;

    @Autowired
    private PeriodicalRepoService periodicalRepoService;



    @RequestMapping(value = "/admin/resCount/getCountData", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getCountData(HttpServletRequest request, @RequestBody Map<String,Object> params){
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            Long org_id = null;
            if(sysUser.getOrg_id() == 1){
                org_id = params.get("org_id") != null ? Long.valueOf(params.get("org_id").toString()):sysUser.getOrg_id();
            }else{
                org_id = sysUser.getOrg_id();
            }
            Map<String, Object> map = new HashMap<String, Object>();
            Organization organization = organizationService.selectById(org_id);
            map.put("org_name", organization.getOrg_name());
            //机构会员数量，未删除
            Integer member_count = unifyMemberService.getCountByOrgId(org_id);
            map.put("member_count", member_count);
            //终端数量
            Integer device_count = deviceService.getCountByOrgId(org_id);
            map.put("device_count", device_count);
            //图书数量去重复
            Integer book_count = bookOrgRelService.getCountByOrgId(org_id);
            map.put("book_count", book_count);
            //资讯数量，未删除，分类已启用
            Integer article_count = articleService.getCountByOrgId(org_id);
            map.put("article_count",article_count);
            //报纸数量,未删除
            Integer newspaper_count = newsPaperService.getCountByOrgId(org_id);
            map.put("newspaper_count",newspaper_count);
            //杂志数量
            Integer periodical_count = periodicalRepoService.getCountByOrgId(org_id);
            map.put("periodical_count",periodical_count);
            //视频数量
            Integer video_count = videoService.getCountByOrgId(org_id);
            map.put("video_count",video_count);
            //音频数量
            Integer audio_count = audioService.getCountByOrgId(org_id);
            map.put("audio_count",audio_count);

            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(map);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
