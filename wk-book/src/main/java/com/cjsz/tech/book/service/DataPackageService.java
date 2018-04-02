package com.cjsz.tech.book.service;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.AllortedOrgBean;
import com.cjsz.tech.book.beans.CopyPkgBean;
import com.cjsz.tech.book.beans.DataPackagesBean;
import com.cjsz.tech.book.domain.DataPackage;
import com.cjsz.tech.system.domain.Organization;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface DataPackageService {

    /**
     * 数据包分页列表
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, PageConditionBean bean);

    /**
     * 新增数据包
     * @param dataPackage
     */
    public void savePkg(DataPackage dataPackage);

    /**
     * 修改数据包
     * @param dataPackage
     */
    public void updatePkg(DataPackage dataPackage);

    /**
     * 分配数据包
     * @param pkg_id
     * @param orgs
     */
    public void allotPkg(Long org_id, Long pkg_id, List<Organization> orgs);

    /**
     * 数据包分配给机构的机构列表
     * @param pkg_id
     * @return
     */
    public List<AllortedOrgBean> getAllortOrgs(Long pkg_id);

    /**
     * 删除数据包
     * @param bean
     */
    public void deletePkgs(DataPackagesBean bean);

    /**
     * 复制数据包
     * @param org_id
     * @param bean
     */
    public DataPackage copyPkg(Long user_id, Long org_id, CopyPkgBean bean);

    /**
     * 更新数据包图书数量(包括重复)
     * @param pkg_id
     * @param book_count
     */
    public void updatePkgBookCount(Long pkg_id, Long book_count);

    /**
     * 更新数据包图书数量(去重复)
     * @param pkg_id
     * @param book_real_count
     */
    public void updatePkgBookRealCount(Long pkg_id, Long book_real_count);

    /**
     * 通过pkg_id查询数据包
     * @param pkg_id
     * @return
     */
    public DataPackage selectById(Long pkg_id);
}
