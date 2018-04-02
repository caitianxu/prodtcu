package com.cjsz.tech.member.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.PageConditionBean;

import java.util.Date;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
public class MemberSignListBean extends PageConditionBean{

    private String date_time;

    private Long member_id;


    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }
}
