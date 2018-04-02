package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.beans.TagBooksCount;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.BookTag;
import com.cjsz.tech.book.domain.BookTagRel;
import com.cjsz.tech.book.mapper.BookTagMapper;
import com.cjsz.tech.book.mapper.BookTagRelMapper;
import com.cjsz.tech.book.service.BookTagService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.MailSetting;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
@Service
public class BookTagServiceImpl implements BookTagService {

    @Autowired
    private BookTagMapper bookTagMapper;

    @Autowired
    private BookTagRelMapper bookTagRelMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        //条件查询图书仓库列表
        List<BookTag> result = bookTagMapper.getBookTagList(bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //获取全部标签
    @Override
    public List<BookTag> getAllTags() {
        return bookTagMapper.getAllTags();
    }

    //获取全部标签
    @Override
    public List<BookTag> getAllTagsBySearchText(String searchText) {
        return bookTagMapper.getBookTagList(searchText);
    }

    //新增标签
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "图书标签")
    public void saveTag(BookTag bookTag) {
        String tag_path = "";
        //如果有上级标签则path拼上级
        if (bookTag.getTag_pid() != null && bookTag.getTag_pid() != 0){
            BookTag tag = bookTagMapper.selectById(bookTag.getTag_pid());
            tag_path = tag.getTag_path();

        }else{
            bookTag.setTag_pid(0L);
            tag_path = "0|";
        }
        //如果没有排序值则自动赋值
        if (bookTag.getOrder_weight() == null){
            bookTag.setOrder_weight(System.currentTimeMillis());
        }
        bookTag.setIs_delete(2);
        bookTagMapper.insert(bookTag);
        List<BookTag> tag = bookTagMapper.findByCode(bookTag.getTag_code());
        if (tag != null && tag.size()>0) {
            tag_path = tag_path + tag.get(0).getTag_id() + "|";
            tag.get(0).setTag_path(tag_path);
            bookTagMapper.updateByPrimaryKey(tag.get(0));
        }


    }

    //修改标签
    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "图书标签")
    public void updateTag(BookTag bookTag) {
        BookTag tag = bookTagMapper.selectById(bookTag.getTag_id());
        if (!bookTag.getTag_pid().equals(tag.getTag_pid())){
            String tag_path ="";
            if (bookTag.getTag_pid() != null && bookTag.getTag_pid() != 0){
                BookTag tagByPid = bookTagMapper.selectById(bookTag.getTag_pid());
                if (tagByPid != null) {
                    tag_path = tagByPid.getTag_path();
                }

            }else {
                tag.setTag_pid(0L);
                tag_path = "0|";
            }
            tag.setTag_pid(bookTag.getTag_pid());
            tag.setTag_name(bookTag.getTag_name());
            if (bookTag.getOrder_weight() != null) {
                tag.setOrder_weight(bookTag.getOrder_weight());
            }
            tag.setTag_code(bookTag.getTag_code());
            bookTagMapper.updateByPrimaryKey(tag);
            String old_path = bookTag.getTag_path();
            String new_path = tag_path + bookTag.getTag_id() + "|";
            bookTagMapper.updatePath(old_path,new_path);
        }else {
            bookTagMapper.updateByPrimaryKey(bookTag);
        }

    }

    //根据标签Ids批量删除标签，同时删除与图书仓库的关联关系
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "图书标签")
    public void deleteTagByTagIds(List<Long> tagIds) {
        String tagIds_str = StringUtils.join(tagIds, ",");
        //删除数据包图书关系（标签下的图书）

        //删除图书标签关系
        bookTagRelMapper.deleteByTagIds(tagIds_str);
        //删除标签
        bookTagMapper.deleteByTagIds(tagIds_str);
    }

    @Override
    public List<String> getAllTagNames() {
        return bookTagMapper.getAllTagNames();
    }

    @Override
    public List<String> getOtherTagNames(Long tag_id) {
        return bookTagMapper.getOtherTagNames(tag_id);
    }

    @Override
    public List<BookTag> getTree(List<BookTag> allList){
        List<BookTag> newList = new ArrayList<>();
        while (allList.size()>0){
            BookTag bookTag = allList.get(0);
            if(!newList.contains(bookTag)){
                newList.add(bookTag);
            }

            allList.remove(0);
            List<BookTag> children = getChildren(allList,bookTag.getTag_id());
            bookTag.setChildren(children);
        }
        return newList;
    }

    public List<BookTag> getChildren(List<BookTag> allList,Long pid ){
        List<BookTag> children = new ArrayList<>();
        List<BookTag> copyList = new ArrayList<>();

        copyList.addAll(allList);
        for(BookTag bookTag : copyList){
            if (bookTag.getTag_pid().equals(pid)){
                bookTag.setChildren(getChildren(allList,bookTag.getTag_id()));
                children.add(bookTag);
                allList.remove(bookTag);
            }
        }
        return children;
    }

    @Override
    public BookTag selectByPid(Long tag_id) {
        return bookTagMapper.selectByPid(tag_id);
    }

    @Override
    public Object getNoTagList(PageConditionBean bean, Sort sort) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并将列表对象转换为dto对象传输到前台
        List<BookRepo> result = bookTagMapper.getNoTagList(bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public List<BookTag> selectByCode(String tag_code) {
        return bookTagMapper.selectByCode(tag_code);
    }

    @Override
    public List<BookTag> selectByCodeAndId(String tag_code, Long tag_id) {
        return bookTagMapper.selectByCodeAndId(tag_code,tag_id);
    }

    @Override
    public List<BookTag> findByCode(String tag_code) {
        return bookTagMapper.findByCode(tag_code);
    }

    @Override
    public List<TagBooksCount> selectCount() {
        return bookTagMapper.selectBooksCount();
    }



}
