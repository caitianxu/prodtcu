package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.service.BookOrgRelService;
import com.cjsz.tech.count.domain.SearchCount;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.WebConfig;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/22 0022.
 */
@Controller
public class BookSearchController {

    @Autowired
    private SearchCountService searchCountService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private BookOrgRelService bookOrgRelService;
    @Autowired
    private ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/bookSearch")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            String key = request.getParameter("key");
            String tag_name = request.getParameter("tag_name");
            String order_type = request.getParameter("order_type");
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            if (StringUtils.isEmpty(key)) {
                key = "";
            } else {
                //添加搜索记录
                SearchCount searchCount = searchCountService.selectByNameAndOrgId(key, Long.valueOf(org_id));
                if (searchCount == null) {
                    //搜索前未搜索过本次搜索内容
                    Organization org = organizationService.selectById(Long.valueOf(org_id));
                    searchCount = new SearchCount();
                    searchCount.setOrg_id(org.getOrg_id());
                    searchCount.setOrg_name(org.getOrg_name());
                    searchCount.setName(key);
                    searchCount.setSearch_count(1L);
                    searchCount.setStatus(1);
                    searchCountService.saveSearchCount(searchCount);
                } else {
                    //搜索前已搜索过本次搜索内容
                    Long count = searchCount.getSearch_count() + 1L;
                    searchCount.setSearch_count(count);
                    searchCountService.updateSearchCount(searchCount);
                }
            }
            request.setAttribute("key", key);
            if (StringUtils.isEmpty(tag_name)) {
                tag_name = "";
            }
            request.setAttribute("tag_name", tag_name);


            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));

            FindBookBean bean = new FindBookBean();
            bean.setPageNum(1);
            bean.setPageSize(35);
            bean.setSearchText(key);
            bean.setTag_name(tag_name);
            bean.setOrg_id(Long.valueOf(org_id));
            if (StringUtils.isNotEmpty(order_type)){
                bean.setOrder_type(order_type);
            }
            Object bookList = bookOrgRelService.sitePageQuery(sort, bean);
            mv.addObject("bookList", bookList);

            Map<String,Object> map = (Map)request.getAttribute("org_map");
            Long pro_org_extend_id =Long.parseLong(map.get("pro_org_extend_id").toString());
            List<WebConfig> webConfigList= proOrgExtendService.getList(pro_org_extend_id);
            mv.addObject("webConfigList",webConfigList);
            mv.setViewName(tempName + "/searchBooks");
        }
        else{
            mv.setViewName("web/404");
        }
        return mv;
    }

}
