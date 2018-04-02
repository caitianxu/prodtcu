package com.cjsz.tech.journal.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.journal.domain.PeriodicalChild;
import org.apache.ibatis.annotations.Delete;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface PeriodicalChildMapper extends BaseMapper<PeriodicalChild> {

    @Delete("delete from periodical_child where periodical_id = #{0}")
    void deleteByPeriodicalId(Long periodical_id);
}

