package com.cjsz.tech.dev.beans;

/**
 * Created by Administrator on 2016/12/25 0025.
 */
public class DeviceInfoBean {

    private Long device_id;         //设备Id

    private String device_code;       //设备编号

    private String  off_line;       //是否离线，1：是，2：否

    private Long book_count;        //已离线图书数量

    private String memory;          //剩余内存

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getOff_line() {
        return off_line;
    }

    public void setOff_line(String off_line) {
        this.off_line = off_line;
    }

    public Long getBook_count() {
        return book_count;
    }

    public void setBook_count(Long book_count) {
        this.book_count = book_count;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }
}
