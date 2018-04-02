package com.cjsz.tech.system.enterstore;

/**
 * Created by shiaihua on 16/7/3.
 */
public class EnterStoreApi {

    private Integer enternum =0;

    public Integer getEnternum() {
        return enternum;
    }

    public void setEnternum(Integer enternum) {
        this.enternum = enternum;
    }



    public static EnterStoreApi getAPI() {
        return new EnterStoreApi();
    }
}
