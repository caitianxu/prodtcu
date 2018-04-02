package com.cjsz.tech.book.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public class BookTypeBean extends PageConditionBean{

    private Integer channel_type;

    public Integer getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(Integer channel_type) {
        this.channel_type = channel_type;
    }
}
