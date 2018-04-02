package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.beans.WebOrgExtendBean;
import com.cjsz.tech.system.domain.ProOrgExtend;
import com.cjsz.tech.system.domain.WebConfig;
import com.cjsz.tech.system.mapper.ProOrgExtendMapper;
import com.cjsz.tech.system.mapper.WebConfigMapper;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.system.utils.CacheUtil;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目设置
 * Created by LuoLi on 2017/3/23 0023.
 */
@Service
public class ProOrgExtendServiceImpl implements ProOrgExtendService {

    @Autowired
    private ProOrgExtendMapper proOrgExtendMapper;

    @Autowired
    private WebConfigMapper webConfigMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<WebOrgExtendBean> orgExtendList = new ArrayList<WebOrgExtendBean>();
        if(StringUtils.isEmpty(bean.getSearchText())){
            orgExtendList = proOrgExtendMapper.getList();
//            for (WebOrgExtendBean webOrgExtendBean:orgExtendList){
//                List<WebConfig> webConfigList = new ArrayList<WebConfig>();
//                webConfigList = webConfigMapper.selectById(webOrgExtendBean.getPro_org_extend_id());
//                webOrgExtendBean.setWebConfigList(webConfigList);
//            }
        }else{
            orgExtendList = proOrgExtendMapper.getListBySearchText(bean.getSearchText());
//            for (WebOrgExtendBean webOrgExtendBean:orgExtendList){
//                List<WebConfig> webConfigList = new ArrayList<WebConfig>();
//                webConfigList = webConfigMapper.selectById(webOrgExtendBean.getPro_org_extend_id());
//                webOrgExtendBean.setWebConfigList(webConfigList);
//            }
        }
        PageList result = new PageList(orgExtendList, null);
        return result;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "项目机构拓展")
    public void saveProOrg(ProOrgExtend proOrgExtend) {
        proOrgExtend.setCreate_time(new Date());
        proOrgExtend.setUpdate_time(new Date());
        proOrgExtend.setEnabled(Constants.DISABLE);
        proOrgExtendMapper.insert(proOrgExtend);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "项目机构拓展")
    public void updateProOrg(ProOrgExtend proOrgExtend) {
        proOrgExtend.setUpdate_time(new Date());
        proOrgExtendMapper.updateByPrimaryKey(proOrgExtend);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "项目机构拓展")
    public void delProOrgs(String ids) {
        proOrgExtendMapper.deleteByIds(ids);
        webConfigMapper.deleteByIds(ids);
    }

    @Override
    public ProOrgExtend selectById(Long pro_org_extend_id) {
        return proOrgExtendMapper.selectById(pro_org_extend_id);
    }

    @Override
    public List<OrganizationBean> getProOrgList(String project_code) {
        return proOrgExtendMapper.getProOrgList(project_code);
    }

    @Override
    public ProOrgExtend selectByServerName(String server_name) {
        return proOrgExtendMapper.selectByServerName(server_name);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "网站导航配置")
    public void saveWebConf(List<WebConfig> webConfigList){
        webConfigMapper.insertList(webConfigList);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "网站导航配置")
    public void updateConf(List<WebConfig> webConfigList,Long pro_org_extend_id){
        //先删除原有的再新增
        webConfigMapper.deleteById(pro_org_extend_id);
        webConfigMapper.insertList(webConfigList);
    }

    @Override
    public List<WebConfig> getList(Long pro_org_extend_id){
        return webConfigMapper.selectById(pro_org_extend_id);
    }

    @Override
    public ProOrgExtend selectByOrgId(Long org_id){
        return proOrgExtendMapper.selectByOrgId(org_id);
    }

    @Override
    public void updateProjectByOrgId(String project_code, String project_name,Long org_id,String org_name) {
        proOrgExtendMapper.updateProjectByOrgId(project_code,project_name,org_id,org_name);
    }

    @Override
    public void updateProName(String project_code, String project_name) {
        proOrgExtendMapper.updateProName(project_code,project_name);
    }

    @Override
    public void updateEnabled(Long pro_org_extend_id, Integer enabled) {
        proOrgExtendMapper.updateEnabeld(pro_org_extend_id,enabled);
    }

    @Override
    public String getTemple(String server_name, String org_id) {
        Object obj = CacheUtil.get("templeName_" + server_name + "_" + org_id);
        String tempName;
        if(obj == null){
            tempName = proOrgExtendMapper.getTemple(server_name, org_id);
            CacheUtil.set("templeName_" + server_name + "_" + org_id, tempName);
        }else{
            tempName = obj.toString();
        }
        return tempName;
    }
}
