package com.cjsz.tech.beans;

/**
 * Created by shiaihua on 16/10/19.
 */
public class WordOffset {

    private Integer offset =0;

    public WordOffset() {

    }

    public WordOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }


    public void addStep(Integer i) {
        this.offset +=i;
    }
}
