package com.cjsz.tech.meb.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.meb.domain.MemberGrade;
import org.springframework.data.domain.Sort;

/**
 * 会员等级
 * Created by Administrator on 2017/3/15 0015.
 */
public interface MemberGradeService {

    /**
     * 会员等级列表
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, PageConditionBean bean);

    /**
     * 修改会员等级（只能修改图片）
     * @param memberGrade
     */
    public void updateMemberGrade(MemberGrade memberGrade);

    /**
     * 查询等级
     * @param grade_name
     */
    public MemberGrade selectByGradeName(Integer grade_name);

    /**
     * 查询等级
     * @param grade_id
     * @return
     */
    public MemberGrade selectByGradeId(Long grade_id);
}
