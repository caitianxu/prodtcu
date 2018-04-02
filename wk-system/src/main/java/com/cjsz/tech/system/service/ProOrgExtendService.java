package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.domain.ProOrgExtend;
import com.cjsz.tech.system.domain.WebConfig;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 项目设置
 * Created by LuoLi on 2017/3/23 0023.
 */
public interface ProOrgExtendService {

    /**
     * 项目的机构————分页
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, PageConditionBean bean);

    /**
     * 项目的机构————新增
     * @param proOrgExtend
     */
    public void saveProOrg(ProOrgExtend proOrgExtend);

    /**
     * 项目的机构————修改
     * @param proOrgExtend
     */
    public void updateProOrg(ProOrgExtend proOrgExtend);

    /**
     * 项目的机构————删除
     * @param ids
     */
    public void delProOrgs(String ids);

    /**
     * 项目的机构————Id查询
     * @param pro_org_extend_id
     * @return
     */
    public ProOrgExtend selectById(Long pro_org_extend_id);

    /**
     * 项目机构
     * @param project_code
     * @return
     */
    public List<OrganizationBean> getProOrgList(String project_code);

    /**
     * 域名查询项目机构
     * @param server_name
     * @return
     */
    public ProOrgExtend selectByServerName(String server_name);

    //保存网站导航配置信息
    void saveWebConf(List<WebConfig> webConfigList);

    //修改网站导航配置信息
    void updateConf(List<WebConfig> webConfigList,Long pro_org_extend_id);

    //获取该网站配置的导航菜单
    List<WebConfig> getList(Long pro_org_extend_id);

    ProOrgExtend selectByOrgId(Long org_id);

    //机构修改所属项目同时修改网站配置项目/修改机构名称
    void updateProjectByOrgId(String project_code, String project_name,Long org_id,String org_name);

    //修改项目名称
    void updateProName(String project_code, String project_name);

    //项目网站启用、停用
    void updateEnabled(Long pro_org_extend_id, Integer enabled);

    String getTemple(String server_name, String org_id);
}
