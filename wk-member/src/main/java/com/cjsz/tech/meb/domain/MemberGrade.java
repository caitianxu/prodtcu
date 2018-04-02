package com.cjsz.tech.meb.domain;

import javax.persistence.*;

/**
 * 会员等级
 * Created by Administrator on 2017/3/15 0015.
 */
@Entity
@Table(name = "member_grade")
public class MemberGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grade_id;//会员等级Id

    private Integer grade_name;//会员等级(1,2...)

    private String grade_title;//等级头衔(书童、书生...)

    private Long grade_point;//等级积分

    private String grade_icon;//等级头像

    public Long getGrade_id() {
        return grade_id;
    }

    public void setGrade_id(Long grade_id) {
        this.grade_id = grade_id;
    }

    public Integer getGrade_name() {
        return grade_name;
    }

    public void setGrade_name(Integer grade_name) {
        this.grade_name = grade_name;
    }

    public String getGrade_title() {
        return grade_title;
    }

    public void setGrade_title(String grade_title) {
        this.grade_title = grade_title;
    }

    public Long getGrade_point() {
        return grade_point;
    }

    public void setGrade_point(Long grade_point) {
        this.grade_point = grade_point;
    }

    public String getGrade_icon() {
        return grade_icon;
    }

    public void setGrade_icon(String grade_icon) {
        this.grade_icon = grade_icon;
    }
}
