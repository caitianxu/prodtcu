package com.cjsz.tech.book.service;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.book.domain.UnBook;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface UnBookService {

    /**
     * 创建任务
     * @param bean
     */
    public void insertUnBook(UnBook bean);

    /**
     * 修改任务
     * @param bean
     */
    public void saveUnBook(UnBook bean);

    /**
     * 获取列表
     * @param ben
     * @param sort
     * @return
     */
    public  Object getUnBookAll(PageConditionBean ben, Sort sort);

    /**
     * 通过文件名获取图书
     * @param file_name
     * @return
     */
    public BookRepo getBookByFileName(String file_name);
}
