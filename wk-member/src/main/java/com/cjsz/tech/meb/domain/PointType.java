package com.cjsz.tech.meb.domain;

import javax.persistence.*;

/**
 * 积分类型
 * Created by Administrator on 2017/3/15 0015.
 */
@Entity
@Table(name = "point_type")
public class PointType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long point_type_id;//积分类型Id
    private String point_type_code;//积分类型编号
    private String point_type_name;//积分类型名称
    private Long point_nums;//积分数

    public Long getPoint_type_id() {
        return point_type_id;
    }

    public void setPoint_type_id(Long point_type_id) {
        this.point_type_id = point_type_id;
    }

    public String getPoint_type_code() {
        return point_type_code;
    }

    public void setPoint_type_code(String point_type_code) {
        this.point_type_code = point_type_code;
    }

    public String getPoint_type_name() {
        return point_type_name;
    }

    public void setPoint_type_name(String point_type_name) {
        this.point_type_name = point_type_name;
    }

    public Long getPoint_nums() {
        return point_nums;
    }

    public void setPoint_nums(Long point_nums) {
        this.point_nums = point_nums;
    }
}
