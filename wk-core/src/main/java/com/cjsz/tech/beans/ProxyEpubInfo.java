package com.cjsz.tech.beans;



import com.cjsz.tech.utils.epub.EpubKernel;

import java.util.List;

/**
 * 代理Epub信息
 * 将EPUB的关键信息都放此类中
 * Created by shiaihua on 16/10/18.
 */
public class ProxyEpubInfo {

    private List<EpubKernel.EpubCatalog> list;

    private List<EpubKernel.EpubCatalog> relayDirectoryList;

    private String chapterroot;

    /**
     * 作者
     */
    private String author;

    /**
     * 书名
     */
    private String title;

    /**
     * 出版描述
     */
    private String description;
    /**
     * 出版语言
     */
    private String language;
    /**
     * 出版日期
     */
    private String date;
    /**
     * 出版标识
     */
    private String identifier;

    /**
     * 出版社
     * @return
     */
    private String publisher;

    /**
     * Cover封面
     * @return
     */
    private String cover;

    private String webcover;


    public List<EpubKernel.EpubCatalog> getList() {
        return list;
    }

    public void setList(List<EpubKernel.EpubCatalog> list) {
        this.list = list;
    }

    public List<EpubKernel.EpubCatalog> getRelayDirectoryList() {
        return relayDirectoryList;
    }

    public void setRelayDirectoryList(List<EpubKernel.EpubCatalog> relayDirectoryList) {
        this.relayDirectoryList = relayDirectoryList;
    }

    public String getChapterroot() {
        return chapterroot;
    }

    public void setChapterroot(String chapterroot) {
        this.chapterroot = chapterroot;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getWebcover() {
        return webcover;
    }

    public void setWebcover(String webcover) {
        this.webcover = webcover;
    }
}
