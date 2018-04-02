package com.cjsz.tech.member.beans;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
public class MemberInfoJSONBean {
    private Long code;
    private BalanceBean data;
    private String msg;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public BalanceBean getData() {
        return data;
    }

    public void setData(BalanceBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
