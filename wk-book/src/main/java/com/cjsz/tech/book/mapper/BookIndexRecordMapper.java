package com.cjsz.tech.book.mapper;

import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.provider.BookIndexRecordProvider;
import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.SearchBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/15 0015.
 */
public interface BookIndexRecordMapper extends BaseMapper<BookIndexRecord> {

    @SelectProvider(type = BookIndexRecordProvider.class, method = "pageQuery")
    List<BookIndexRecord> pageQuery(@Param("searchBean") SearchBean searchBean);

}
