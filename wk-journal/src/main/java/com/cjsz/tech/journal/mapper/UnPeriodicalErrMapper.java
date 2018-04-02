package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.UnPeriodicalErr;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public interface UnPeriodicalErrMapper extends BaseMapper<UnPeriodicalErr> {


    //通过文件名删除
    @Delete("DELETE FROM un_periodical_err where file_name = #{0}")
    void deleteByFileName(String file_name);
}
