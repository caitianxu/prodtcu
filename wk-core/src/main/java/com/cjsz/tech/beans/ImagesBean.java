package com.cjsz.tech.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class ImagesBean implements Serializable {

    private Integer page;
	private String  img;
	private String  img_small;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getImg_small() {
        return img_small;
    }

    public void setImg_small(String img_small) {
        this.img_small = img_small;
    }

    public String getImg() {

        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
