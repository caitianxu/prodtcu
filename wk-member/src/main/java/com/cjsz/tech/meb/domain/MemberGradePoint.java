package com.cjsz.tech.meb.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 会员等级积分记录
 * Created by Administrator on 2017/3/15 0015.
 */
@Entity
@Table(name = "member_grade_point")
public class MemberGradePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grade_point_id;//会员等级积分记录Id

    private Long member_id;//会员Id

    private Long book_id;//图书Id

    private String point_type_code;//积分类型编号

    private Long point_nums;//获取积分数量

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;//记录时间

    public Long getGrade_point_id() {
        return grade_point_id;
    }

    public void setGrade_point_id(Long grade_point_id) {
        this.grade_point_id = grade_point_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public String getPoint_type_code() {
        return point_type_code;
    }

    public void setPoint_type_code(String point_type_code) {
        this.point_type_code = point_type_code;
    }

    public Long getPoint_nums() {
        return point_nums;
    }

    public void setPoint_nums(Long point_nums) {
        this.point_nums = point_nums;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
