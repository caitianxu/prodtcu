package com.cjsz.tech.meb.service.impl;

import com.cjsz.tech.meb.domain.Member;
import com.cjsz.tech.meb.domain.MemberGrade;
import com.cjsz.tech.meb.domain.MemberGradePoint;
import com.cjsz.tech.meb.domain.PointType;
import com.cjsz.tech.meb.mapper.MemberGradeMapper;
import com.cjsz.tech.meb.mapper.MemberGradePointMapper;
import com.cjsz.tech.meb.mapper.PointTypeMapper;
import com.cjsz.tech.meb.service.MemberGradePointService;
import com.cjsz.tech.meb.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 会员等级积分记录
 * Created by Administrator on 2017/3/15 0015.
 */
@Service
public class MemberGradePointServiceImpl implements MemberGradePointService {

    @Autowired
    private MemberGradePointMapper memberGradePointMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PointTypeMapper pointTypeMapper;

    @Autowired
    private MemberGradeMapper memberGradeMapper;

    @Override
    @Transactional
    public void saveMemberGradePoint(Long member_id, Long book_id, String point_type_code) {
        //查找对应的积分类型point_type_code
        PointType pointType = pointTypeMapper.selectByCode(point_type_code);
        //积分记录产生
        MemberGradePoint gradePoint = new MemberGradePoint();
        gradePoint.setMember_id(member_id);
        gradePoint.setBook_id(book_id);
        gradePoint.setPoint_type_code(point_type_code);
        gradePoint.setPoint_nums(pointType.getPoint_nums());
        gradePoint.setCreate_time(new Date());
        memberGradePointMapper.insert(gradePoint);
        //会员积分变更，等级等相关信息变更
        Member member = memberService.selectById(member_id);
        Long org_grade = member.getGrade_id();//原始等级Id
        Long org_points = member.getGrade_point();//原始积分

        Long cur_points = org_points + pointType.getPoint_nums();//操作后积分
        MemberGrade memberGrade = memberGradeMapper.selectByPoints(cur_points);
        MemberGrade next_memberGrade = memberGradeMapper.selectByGradeName(memberGrade.getGrade_name() + 1);
        if(!memberGrade.getGrade_id().equals(org_grade)){
            //等级变更
            member.setGrade_id(memberGrade.getGrade_id());
            if(next_memberGrade != null){
                member.setNext_grade_point(next_memberGrade.getGrade_point());
            }
        }
        member.setGrade_point(cur_points);
        member.setUpdate_time(new Date());
        memberService.updateMember(member);
    }
}
