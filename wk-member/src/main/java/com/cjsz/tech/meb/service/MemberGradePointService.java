package com.cjsz.tech.meb.service;

/**
 * 会员等级积分记录
 * Created by Administrator on 2017/3/15 0015.
 */
public interface MemberGradePointService {

    /**
     * 相关操作产生会员等级积分记录
     * @param member_id
     * @param book_id
     * @param point_type_code
     */
    public void saveMemberGradePoint(Long member_id, Long book_id, String point_type_code);

}
