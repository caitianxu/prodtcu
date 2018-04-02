package com.cjsz.tech.book.service;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.*;
import com.cjsz.tech.book.domain.BookOrgRel;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookOrgRelService {

    /**
     * 分页查询图书列表
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, FindBookBean bean);

    /**
     * 前台分页查询图书列表
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object sitePageQuery(Sort sort, FindBookBean bean);

    /**
     * 数据包图书、机构图书   排序置顶
     * @param bookBean
     */
    public void updateOrderTop(BookBean bookBean);

    /**
     * 根据联合主键查询关系数据
     * @return
     */
    public BookOrgRel selectBookOrgRelByUnionIds(Long book_id, Long org_id, Long book_cat_id, Long pkg_id);

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
     * 修改图书 启用、停用
     * @param orgid
     * @param beanList
     */
    void updateBookRelStatus(Long orgid, List<BookBean> beanList);

    /**
     * 删除一批选中图书关系
     * @param orgid
     * @param beanList
     */
    void deleteBooksRel(Long orgid, List<BookBean> beanList);

    /**
     * 查询机构图书详情
     * @param orgid
     * @param catidList
     * @return
     */
    public List<BookOrgRel> selectOrgRels(Long orgid, List<Long> catidList);

    /**
     * 更新组织下某一设备的图书离线状态
     * @param orgid
     * @param device_id
     * @param bookIds
     * @param bool
     */
    public void updateBookOffLine(Long orgid, Long device_id, List<Long> bookIds, Boolean bool);

    /**
     * 查询机构所有分类下的 book_id = bkid 的书
     * @param org_id
     * @param bkid
     * @return
     */
    public List<BookOrgRel> selectOrgRelsByBookId(Long org_id, Long bkid);

    /**
     * 获取机构有限热榜图书
     * @param org_id
     * @param count
     * @return
     */
    public List<Map<String,Object>> selectHotListByOrgIdAndCount(Long org_id, Integer count);

    /**
     * 获取机构有限热榜图书
     * @param org_id
     * @param book_cat_id
     * @param count
     * @return
     */
    public List<Map<String,Object>> selectRecommendListByOrgIdAndCatIdAndCount(Long org_id, Long book_cat_id, Integer count);

    /**
     * 根据bookId,member_id获取图书、书架信息
     * @param org_id
     * @param bookId
     * @param member_id
     * @return
     */
    public BookBean findByOrgIdAndBookIdAndMemberId(Long org_id, Long bookId, Long member_id);

    /**
     * 图书管理图书全部导出
     * @param org_id
     * @param book_cat_id
     * @return
     */
    public List<Map<String,Object>> getAllBooksBaseInfo(Long org_id, Long book_cat_id);

    /**
     * 查询图书数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    /**
     * 获取和指定图书相同作者的limit本图书
     * @param bookId
     * @param org_id
     * @param limit
     * @return
     */
    List<BookInfo> findSameAuthorBooks(Long bookId, Long org_id, Integer limit);
    /**
     * 获取和指定图书相同分类的limit本图书
     * @param bookId
     * @param org_id
     * @param limit
     * @return
     */
    List<BookInfo> findSameCatalogBooks(Long bookId, Long org_id, Long bookCatId, Integer limit,String bookIds);

    //查询当前机构图书的分类ID
    Long getCatId(Long book_id,Long org_id);

    //大众版app获取图书列表
    Object getAppBooks(ApiPublicBookBean bean);


    PublicBookBean findByMemberIdAndBookId(Long book_id, Long member_id);

    //出版图书排行
    Object findRankList(PageConditionBean bean);

    //热门图书
    List<BookBean> getHotList(Long org_id, Integer limit);

    //推荐图书
    List<BookBean> getRecommendList(Long org_id, Integer limit);
}
