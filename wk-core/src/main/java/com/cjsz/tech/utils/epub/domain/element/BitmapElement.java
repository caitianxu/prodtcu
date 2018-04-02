package com.cjsz.tech.utils.epub.domain.element;

/**
 * Created by Ethan on 16/3/5.
 * 图像元素
 */
public class BitmapElement extends ParagraphElement {

    public String getSrc() {
        return src;
    }

    private String src;

    public BitmapElement(String src){
        super("图");
        this.src = src;
    }




}
