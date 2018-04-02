package com.cjsz.tech.api.beans;

import com.cjsz.tech.book.beans.CJZWWPublicBookBean;

import java.util.List;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
public class RecommendBookJSONBean {

    private Long code;
    private Rows data;
    private String msg;

    public class Rows{
        private List<CJZWWPublicBookBean> rows;

        public List<CJZWWPublicBookBean> getRows() {
            return rows;
        }

        public void setRows(List<CJZWWPublicBookBean> rows) {
            this.rows = rows;
        }
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Rows getData() {
        return data;
    }

    public void setData(Rows data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
