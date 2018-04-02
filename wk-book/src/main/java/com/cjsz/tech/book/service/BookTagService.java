package com.cjsz.tech.book.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.BookReposBean;
import com.cjsz.tech.book.beans.TagBooksCount;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.BookTag;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface BookTagService {

    /**
     * 获取图书标签列表
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, PageConditionBean bean);

    /**
     * 获取全部图书标签
     * @return
     */
    public List<BookTag> getAllTags();

    /**
     * 获取全部图书标签
     * @param searchText
     * @return
     */
    public List<BookTag> getAllTagsBySearchText(String searchText);

    /**
     * 添加标签
     * @param bookTag
     */
    public void saveTag(BookTag bookTag);

    /**
     * 修改标签
     * @param bookTag
     */
    public void updateTag(BookTag bookTag);

    /**
     * 删除标签
     * @param tagIds
     */
    public void deleteTagByTagIds(List<Long> tagIds);

    /**
     * 获取所有标签名称
     * @return
     */
    public List<String> getAllTagNames();

    /**
     * 获取（除tag_id外的）所有标签名称
     * @param tag_id
     * @return
     */
    public List<String> getOtherTagNames(Long tag_id);

    /**
     * 标签列表转换为树形结构
     * @param allList
     * @return
     */
    List<BookTag> getTree(List<BookTag> allList);


    /**
     * 根据id查询标签
     * @param tag_id
     * @return
     */
    BookTag selectByPid(Long tag_id);

    /**
     * 获取所有没有标签的图书列表
     * @return
     */
    Object getNoTagList(PageConditionBean bean, Sort sort);

    /**
     * 根据标签编码查询标签判断是否编码重复
     * @param tag_code
     * @return
     */
    List<BookTag> selectByCode(String tag_code);

    /**
     * 查询除本身标签code外是否有重复编码
     * @param tag_code
     * @param tag_id
     * @return
     */
    List<BookTag> selectByCodeAndId(String tag_code, Long tag_id);

    /**
     * 查询编码是否重复
     * @param tag_code
     * @return
     */
    List<BookTag> findByCode(String tag_code);

    //查询每个标签下图书
    List<TagBooksCount> selectCount();


}
