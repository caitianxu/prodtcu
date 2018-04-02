package com.cjsz.tech.utils.epub.domain.element;

import com.cjsz.tech.utils.epub.domain.Element;

/**
 * Created by Ethan on 16/3/5.
 * 字符元素
 */
public class ParagraphElement extends Element {

    public String getContent() {
        if(mTextIndent>0){
            return "\u3000\u3000"+content;
        }else{
            return content;
        }

    }

    private String content;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    private String chapter;


    private boolean mBold;
    private float mFontSize;
    private int mFontColor;

    private float mSpacingadd = 0.0F;
    private float mSpacingmult = 1.0F;

    public int getmCharHeight() {
        return mCharHeight;
    }

    private int mCharHeight;

    public int getmTextIndent() {
        return mTextIndent;
    }

    private int mTextIndent;


    public ParagraphElement(String content) {
        this.content = content;
    }

    public int disWidth;
    public int disHeight;

    private float margin_top = 0.0f;

}


