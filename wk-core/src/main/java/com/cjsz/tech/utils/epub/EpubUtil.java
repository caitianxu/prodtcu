package com.cjsz.tech.utils.epub;


import com.cjsz.tech.beans.ProxyEpubInfo;
import com.cjsz.tech.core.SpringContextUtil;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EPUB工具类
 * Created by shiaihua on 16/10/18.
 */
public class EpubUtil {

    /**
     * 默认的CSS文件
     */
    public static final String DEFAULT_CSSFILE = "extepub.css";

    /**
     * 排版输出生成目录
     */
    public static final String DEFAULT_OUTPUT = "attachment/outdistr";

    /**
     * 排版输出生成目录
     */
    public static final String DEFAULT_BASE = "attachment/";

    /**
     * 解压目录
     */
    public static final String DEFAULT_UNZIP = "unzip";

    /**
     * 解析EPUB
     *
     * @param file
     * @param rewrite
     * @return
     */
    public static ProxyEpubInfo parseEpubIndex(String unziproot, File file, Boolean rewrite) {
        EpubKernel kernel = new EpubKernel();

        try {
            boolean flag = kernel.openEpubFile(unziproot, file.getPath(), rewrite);
            if (flag) {
                List<EpubKernel.EpubCatalog> chapters = kernel.getCatalogList();
                ProxyEpubInfo epubinfo = new ProxyEpubInfo();
                epubinfo.setList(chapters);
                epubinfo.setChapterroot(kernel.getChapterRoot());
                epubinfo.setAuthor(kernel.getAuthor());
                epubinfo.setTitle(kernel.getTitle());
                epubinfo.setDescription(kernel.getDesc());
                epubinfo.setIdentifier(kernel.getIdentifier());
                epubinfo.setDate(kernel.getDate());
                epubinfo.setLanguage(kernel.getLanguage());
                epubinfo.setCover(kernel.getCoverPath());
                return epubinfo;
            }
        }  catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static List<EpubKernel.EpubCatalog> parserEpubFile(String bookdir) {
        try {
            EpubKernel kernel = new EpubKernel();
            kernel.parserEpubFile(bookdir);
            return kernel.getCatalogList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<EpubKernel.EpubCatalog>();
    }

    public static String parserEpubType(String bookdir) {
        try {
            EpubKernel kernel = new EpubKernel();
            return kernel.parseEpubContainer(bookdir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLayRoot() {
        try {
            String app_path = SpringContextUtil.getApplicationContext().getResource("").getFile() + File.separator;
            String path = app_path + EpubUtil.DEFAULT_OUTPUT;
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
