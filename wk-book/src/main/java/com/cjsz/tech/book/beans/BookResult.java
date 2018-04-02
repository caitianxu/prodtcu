package com.cjsz.tech.book.beans;

import java.io.Serializable;

/**
 * 搜索结果
 * Created by shiaihua on 16/9/19.
 */
public class BookResult implements Serializable {

    private Long time;

    private Page data;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Page getData() {
        return data;
    }

    public void setData(Page data) {
        this.data = data;
    }

}
