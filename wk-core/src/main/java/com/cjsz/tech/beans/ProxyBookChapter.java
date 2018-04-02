package com.cjsz.tech.beans;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiaihua on 16/9/27.
 */
@Entity
public class ProxyBookChapter implements Serializable {

    private Integer id;
    private String title;

    private Integer inx;

    private Integer pid;

    private Integer showinx=0;


    @Transient
    private List<ProxyBookChapter> child =new ArrayList<ProxyBookChapter>();

    private String url;

    private String path;

    private Integer pdf_page;

    private String epubTarget;

    private String pdfIndex;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getInx() {
        return inx;
    }

    public void setInx(Integer inx) {
        this.inx = inx;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getShowinx() {
        return showinx;
    }

    public void setShowinx(Integer showinx) {
        this.showinx = showinx;
    }

    public List<ProxyBookChapter> getChild() {
        return child;
    }

    public void setChild(List<ProxyBookChapter> child) {
        this.child = child;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPdf_page() {
        return pdf_page;
    }

    public void setPdf_page(Integer pdf_page) {
        this.pdf_page = pdf_page;
    }

    public String getEpubTarget() {
        return epubTarget;
    }

    public void setEpubTarget(String epubTarget) {
        this.epubTarget = epubTarget;
    }

    public String getPdfIndex() {
        return pdfIndex;
    }

    public void setPdfIndex(String pdfIndex) {
        this.pdfIndex = pdfIndex;
    }
}
