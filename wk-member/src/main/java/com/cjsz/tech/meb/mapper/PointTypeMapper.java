package com.cjsz.tech.meb.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.domain.PointType;
import org.apache.ibatis.annotations.Select;

/**
 * 会员等级积分记录
 * Created by Administrator on 2017/3/15 0015.
 */
public interface PointTypeMapper extends BaseMapper<PointType> {

    @Select("select * from point_type where point_type_code = #{0}")
    PointType selectByCode(String point_type_code);
}
