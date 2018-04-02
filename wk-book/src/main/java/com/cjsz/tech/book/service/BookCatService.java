package com.cjsz.tech.book.service;


import com.cjsz.tech.book.domain.BookCat;
import com.cjsz.tech.system.service.IOfflineService;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookCatService extends IOfflineService {

    /**
     * 获取机构图书分类
     * @param org_id
     * @return
     */
    public List<BookCat> getOrgCats(Long org_id);

    /**
     * 获取图书分类——树结构
     * @param cats
     * @return
     */
    public List<BookCat> selectTree(List<BookCat> cats);

    /**
     * 保存分类
     * @param cat
     * @param org_id
     */
    public BookCat saveCat(BookCat cat, Long org_id);

    /**
     * 修改分类
     * @param cat
     */
    public BookCat updateCat(BookCat cat);

    /**
     * 查找机构的分类集合
     * @param orgid
     * @param book_cat_ids
     * @return
     */
    public List<Long> getOrgAllCat(Long orgid, Long[] book_cat_ids);

    /**
     * 删除机构的图书分类和详情
     * @param orgid
     * @param catidList
     */
    public void deleteOrgAll(Long orgid, List<Long> catidList);

    /**
     * 获取当前分类本身和下级分类
     * @param org_id
     * @param book_cat_path
     * @return
     */
    public List<BookCat> getOwnerCats(Long org_id, String book_cat_path);

    /**
     * 获取机构第一层级图书分类
     * @param org_id
     * @return
     */
    public List<BookCat> selectFirstCatsByOrgId(Long org_id);

    /**
     * 获取机构第一层级有限图书分类
     * @param org_id
     * @param count
     * @return
     */
    List<BookCat> selectFirstCatsByOrgIdAndCount(Long org_id, Integer count);

    //获取长江中文网机构1级分类
    List<BookCat> getList();

    //获取长江中文网机构2级分类
    List<BookCat> getListByPid(Long pid);

    //获取机构所有图书分类转树形结构
    List<BookCat> selectAllCats(Long org_id);

    //根据分类id查询图书分类
    BookCat selectByCatId(Long book_cat_id,Long org_id);
}
