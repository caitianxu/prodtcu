package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.service.BookIndexService;
import com.cjsz.tech.meb.service.MemberReadIndexService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private MemberReadIndexService memberReadIndexService;

    @Autowired
    private BookIndexService bookIndexService;

    @Autowired
    private ProOrgExtendService proOrgExtendService;

    @Autowired
    private AdvCatService advCatService;
    @Autowired
    private AdvService advService;

    @RequestMapping("/web/order")
    public ModelAndView search(HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            mv.setViewName(tempName + "/order");
//            Map<String,Object> map = (Map)request.getAttribute("org_map");
//            Long pro_org_extend_id =Long.parseLong(map.get("pro_org_extend_id").toString());
//            List<WebConfig> webConfigList= proOrgExtendService.getList(pro_org_extend_id);
//            mv.addObject("webConfigList",webConfigList);
            //广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList",advModuleList);
        }
        else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    /**
     * 排行个人
     * @return
     */
    @RequestMapping(value = "/site/userOrder/getMemberData", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getMemberData(HttpServletRequest request, PageConditionBean bean){
        try{
            String type = request.getParameter("type");//week,month,year
            Sort sort = new Sort(Sort.Direction.DESC, "read_index");
            Long org_id = Long.parseLong(request.getParameter("org_id"));
            Object obj = memberReadIndexService.sitePageQuery(sort, bean, type,org_id);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch(Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 图书排行
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/site/bookOrder/getBookData", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getBookData(HttpServletRequest request, PageConditionBean bean){
        try{
            String type = request.getParameter("type");//week,month,year
            Sort sort = new Sort(Sort.Direction.DESC, "unite_index");
            Long org_id = Long.parseLong(request.getParameter("org_id"));
            Object obj = bookIndexService.sitePageQuery(sort, bean, type,org_id);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        }catch(Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
