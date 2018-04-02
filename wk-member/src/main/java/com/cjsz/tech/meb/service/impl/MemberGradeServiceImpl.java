package com.cjsz.tech.meb.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.domain.MemberGrade;
import com.cjsz.tech.meb.mapper.MemberGradeMapper;
import com.cjsz.tech.meb.service.MemberGradeService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员等级
 * Created by Administrator on 2017/3/15 0015.
 */
@Service
public class MemberGradeServiceImpl implements MemberGradeService {

    @Autowired
    private MemberGradeMapper memberGradeMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<MemberGrade> result = new ArrayList<MemberGrade>();
        if(StringUtils.isEmpty(bean.getSearchText())){
            result = memberGradeMapper.getAllList();
        }else {
            result = memberGradeMapper.getListBySearchText(bean.getSearchText());
        }
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public void updateMemberGrade(MemberGrade memberGrade) {
        MemberGrade grade = memberGradeMapper.selectById(memberGrade.getGrade_id());
        grade.setGrade_icon(memberGrade.getGrade_icon());
        memberGradeMapper.updateByPrimaryKey(grade);
    }

    @Override
    public MemberGrade selectByGradeName(Integer grade_name) {
        return memberGradeMapper.selectByGradeName(grade_name);
    }

    @Override
    public MemberGrade selectByGradeId(Long grade_id) {
        return memberGradeMapper.selectByGradeId(grade_id);
    }


}
