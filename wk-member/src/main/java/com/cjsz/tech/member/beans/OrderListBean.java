package com.cjsz.tech.member.beans;

import com.cjsz.tech.beans.PageConditionBean;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/12 0012.
 */
public class OrderListBean extends PageConditionBean {

    private Date start_time;
    private Date end_time;

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }
}
