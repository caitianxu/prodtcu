package com.cjsz.tech.utils.epub;

import com.cjsz.tech.utils.epub.domain.element.ParagraphElement;

import java.util.List;

/**
 * Created by Ethan on 16/3/10.
 */

public class EpubChapter {
    public String chapterId;
    public String title;
    public String path;
    public String wpath;
    public String src;
    public List<ParagraphElement> content;
}
