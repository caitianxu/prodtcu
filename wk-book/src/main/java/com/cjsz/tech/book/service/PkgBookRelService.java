package com.cjsz.tech.book.service;


import com.cjsz.tech.book.beans.BookBean;
import com.cjsz.tech.book.beans.FindBookBean;
import com.cjsz.tech.book.beans.PkgBookBean;
import com.cjsz.tech.book.beans.SaveBookBean;
import com.cjsz.tech.book.domain.BookOrgRel;
import com.cjsz.tech.book.domain.PkgBookRel;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface PkgBookRelService {

    /**
     * 分页查询图书列表
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, FindBookBean bean);

    /**
     * 查询图书是否在数据包里
     * @param bean
     * @return
     */
    PkgBookRel selectById(Long org_id,SaveBookBean bean);

    /**
     * 数据包新增图书
     * @param org_id
     * @param bookBean
     */
    public void savePkgBookRel(Long org_id, SaveBookBean bookBean);

    /**
     * 数据包单本加书
     * @param org_id
     * @param bean
     */
    void pkgInsertBook(Long org_id,SaveBookBean bean);

    /**
     * 数据包移除 图书
     * @param bookBean
     */
    public void removePkgBookRel(BookBean bookBean);

    /**
     * 数据包批量移除 图书
     * @param pkgBookBean
     */
    public void removePkgBookRels(PkgBookBean pkgBookBean);

    /**
     * 根据联合主键查询关系数据
     * @return
     */
    public PkgBookRel selectPkgBookRelByUnionIds(Long book_id, Long org_id, Long book_cat_id, Long pkg_id);

    /**
     * 机构图书 是否热门
     * @param bookBean
     */
    public void updateRelHot(BookBean bookBean);

    /**
     * 机构图书 是否推荐
     * @param bookBean
     */
    public void updateRelRecommend(BookBean bookBean);

    /**
     * 机构图书 是否离线
     * @param bookBean
     */
    public void updateOffLine(BookBean bookBean);

    /**
     * 数据包图书、机构图书   排序置顶
     * @param bookBean
     */
    public void updateOrderTop(BookBean bookBean);

    /**
     * 查找数据包的包与图书关系
     * @param pkg_id
     * @return
     */
    public List<PkgBookRel> findByPkgId(Long pkg_id);

    /**
     * 数据包图书全部基本信息
     * @param pkg_id
     * @return
     */
    public List<Map<String,Object>> getAllBooksBaseInfo(Long pkg_id);
}
