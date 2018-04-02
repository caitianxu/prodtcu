package com.cjsz.tech.cms.beans;

import java.io.Serializable;

/**
 * 图集ITEM
 * Created by shiaihua on 16/12/23.
 */
public class ArticlePictureProxyItem implements Serializable{

    private String title;

    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
