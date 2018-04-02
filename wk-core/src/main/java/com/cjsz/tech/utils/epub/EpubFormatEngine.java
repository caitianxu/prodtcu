package com.cjsz.tech.utils.epub;

import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.utils.FileUtil;
import com.cjsz.tech.utils.MyXmlUtil;
import com.cjsz.tech.utils.epub.utils.DomUtil;
import com.cjsz.tech.utils.images.IThumbnailCreator;
import com.cjsz.tech.utils.images.ThumbnailCreatorFactory;
import com.cjsz.tech.utils.zip.ZipUtil;
//import com.hankcs.hanlp.HanLP;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.cjsz.tech.beans.ProxyEpubInfo;
import com.cjsz.tech.beans.WordOffset;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * EPUB规整解析
 * Created by shiaihua on 16/10/17.
 */
public class EpubFormatEngine {


    private String bookfile;

    private String bookname;

    private String saveroot;

    private String unziproot;

    private String bookdir;

    private String chapterdir;

    private String old_bookdir;

    private String old_chapterdir;

    private Long last_p_inx = 10000000L;
    /**
     * 书籍简要信息
     */
    private ProxyEpubInfo originEpubInfo;

    private Integer limitDirectoryLevel = 5;

    private String extCssfile;

    private String namespace = "";

    /**
     * 标注类元素
     */
    private List<Element> hideElement = new ArrayList<Element>();
    /**
     * 需忽略的章节关键字
     */
    private List<String> excludeContentKeys = new ArrayList<String>();

    /**
     * 忽略的章节
     * 说明：通过excludeContentKeys被忽略的章节信息
     *
     * @return
     */
    private List<String> excludeChapterHrefs = new ArrayList<String>();


    public EpubFormatEngine() {
        try {
            initXmlParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getBookfile() {
        return bookfile;
    }

    public void setBookfile(String bookfile) {
        this.bookfile = bookfile;
    }

    public String getSaveroot() {
        return saveroot;
    }

    public void setSaveroot(String saveroot) {
        this.saveroot = saveroot;
    }

    public String getChapterdir() {
        return chapterdir;
    }


    public String getBookdir() {
        return bookdir;
    }


    public void setExtCssfile(String extCssfile) {
        this.extCssfile = extCssfile;
    }

    public String getBookname() {
        return bookname;
    }

    public Integer getLimitDirectoryLevel() {
        return limitDirectoryLevel;
    }

    public void setLimitDirectoryLevel(Integer limitDirectoryLevel) {
        this.limitDirectoryLevel = limitDirectoryLevel;
    }

    /**
     * 增加忽略的章节信息
     *
     * @param key
     */
    public void addExcludeContentKey(String key) {
        if (excludeContentKeys.contains(key)) {
            return;
        }
        excludeContentKeys.add(key);
//        String fantiKey = HanLP.convertToTraditionalChinese(key);
//        if(!fantiKey.equals(key)) {
//            excludeContentKeys.add(fantiKey);
//        }
    }

    /**
     * EPUB解析
     *
     * @return
     */
    public Boolean parseEpubBook() {
        checkRoot();
        if (StringUtils.isEmpty(bookfile)) {
            return false;
        }
        //解压与解析目录
        File file = new File(bookfile);
        bookname = file.getName().substring(0, file.getName().lastIndexOf("."));
        originEpubInfo = EpubUtil.parseEpubIndex(unziproot, file, true);
        if (null == originEpubInfo) {
            return false;
        }
        return true;
    }

    /**
     * EPUB格式规整解析
     *
     * @return
     */
    public Boolean layEpubBook() {

        //解压文件夹
        old_bookdir = unziproot + File.separator + bookname + File.separator;
        old_chapterdir = old_bookdir + originEpubInfo.getChapterroot() + File.separator;

        //输出目的文件夹
        bookdir = saveroot + File.separator + bookname + File.separator;
        chapterdir = bookdir + originEpubInfo.getChapterroot() + File.separator;

        File bookdirFile = new File(bookdir);
        if (bookdirFile.exists()) {
            FileUtil.del(bookdirFile);
        }
        bookdirFile.mkdirs();

        new File(chapterdir).mkdirs();

        // 输出新格式--文章内容处理
        List<EpubKernel.EpubCatalog> catalogList = originEpubInfo.getList();
        for (int i = 0; i < catalogList.size(); i++) {
            hideElement.clear();//重置排除的--目前就是sup书签链接的节点需要排除
            EpubKernel.EpubCatalog catalog = catalogList.get(i);
            Boolean isExclude = checkExclude(catalog);
            if (isExclude) {
                continue;
            }
            outputBookChapter(catalog);
        }
        //拷贝container.xml和mimetype
        outputMetaInf();
        //拷贝图片和图书描述文件
        try {
            outputBookOthers();
            //更新目录信息
            refreshDirectory();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 压缩EPUB格式目录 成 EPUB文件
     * 执行layEpubBook后调用
     */
    public File compressEpub() {
        //压缩成新的epub
        File zipFile = ZipUtil.zipDirectory(bookdir, ".epub");
        System.out.println("zip over");
        return zipFile;
    }


    /**
     * 获取图书信息
     * 执行layEpubBook后调用
     *
     * @return
     */
    public ProxyEpubInfo getOriginEpubInfo() {
        return originEpubInfo;
    }

    /**
     * 获取目录列表
     * 执行layEpubBook后调用
     *
     * @return
     */
    public List<EpubKernel.EpubCatalog> getCatalogList() {
        return originEpubInfo.getRelayDirectoryList();
    }

    /**
     * 获取目录列表
     * 利用排版后的目录资源，重新解析新生成的目录描述文件，生成目录列表(无须重新解析排版)
     *
     * @return
     */
    public List<EpubKernel.EpubCatalog> parseNewBook(String bookdir) {
        return EpubUtil.parserEpubFile(bookdir);
    }


    /**
     * 输出CSS文件到目标位置
     *
     * @param srcfile
     */
    private void outputcssfile(File srcfile) {
        if (StringUtils.isEmpty(extCssfile)) {
            extCssfile = saveroot + File.separator + EpubUtil.DEFAULT_CSSFILE;
        }
        File extFile = new File(extCssfile);
        File newFile = new File(chapterdir + File.separator + srcfile.getParentFile().getName() + File.separator + srcfile.getName());
        File cssDir = newFile.getParentFile();
        if (!cssDir.exists()) {
            cssDir.mkdirs();
        }
        if (extFile.exists()) {
            try {
                String csscontent = FileUtils.readFileToString(srcfile);
                String extCsscontent = FileUtils.readFileToString(extFile);
                String allcssContent = csscontent + "\n" + extCsscontent;
                FileUtils.writeByteArrayToFile(newFile, allcssContent.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileUtil.copyFile(srcfile, newFile);
        }

    }

    private void outputOtherFile(File otherfile) {
        File newFileDir = new File(chapterdir + File.separator + otherfile.getParentFile().getName() + File.separator);
        File newFile = new File(chapterdir + File.separator + otherfile.getParentFile().getName() + File.separator + otherfile.getName());
        if (!newFileDir.exists()) {
            newFileDir.mkdirs();
        }
        if (!newFile.exists()) {
            FileUtil.copyFile(otherfile, newFile);
        }
    }


    private Boolean checkExclude(EpubKernel.EpubCatalog catalog) {
        String title = StringUtils.remove(catalog.title, " ");
        for (String excludekey : excludeContentKeys) {
            if (title.contains(excludekey)) {
                excludeChapterHrefs.add(catalog.href);
                return true;
            }
        }
        return false;
    }

    private void checkRoot() {
        if (saveroot == null) {
            try {
                String app_path = SpringContextUtil.getApplicationContext().getResource("").getFile() + File.separator;
                saveroot = app_path + EpubUtil.DEFAULT_OUTPUT;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File saveRootDir = new File(saveroot);
        if (!saveRootDir.exists()) {
            saveRootDir.mkdirs();
        }
        unziproot = saveRootDir.getPath() + File.separator + EpubUtil.DEFAULT_UNZIP;
        File unziprootFile = new File(unziproot);
        if (!unziprootFile.exists()) {
            unziprootFile.mkdirs();
        }
    }

    /**
     * 拷贝container.xml和mimetype
     */
    private void outputMetaInf() {
        File infDir = new File(bookdir + File.separator + "META-INF");
        if (infDir.exists()) {
            infDir.delete();
        }
        infDir.mkdirs();
        //拷贝container.xml
        File old_infDir = new File(old_bookdir + File.separator + "META-INF");
        File old_contain = new File(old_infDir.getPath() + File.separator + "container.xml");
        File new_contain = new File(infDir.getPath() + File.separator + "container.xml");
        FileUtil.copyFile(old_contain, new_contain);

        //拷贝mimetype
        File old_mime = new File(old_bookdir + File.separator + "mimetype");
        File new_mime = new File(bookdir + File.separator + "mimetype");
        FileUtil.copyFile(old_mime, new_mime);
    }

    /**
     * 输出图书其他内容
     */
    private void outputBookOthers() throws IOException {
        File old_chapterdirFile = new File(old_chapterdir);
        if (!old_chapterdirFile.exists()) {
            return;
        }
        File[] subfiles = old_chapterdirFile.listFiles();
        for (int si = 0; si < subfiles.length; si++) {
            File subitem = subfiles[si];
            if (subitem.isDirectory()) {
                String subname = subitem.getName();
                if (subname.equals("css") || subname.equals("Styles")) {
                    if (subitem.isDirectory()) {
                        File[] cssfiles = subitem.listFiles();
                        for (File cssfile : cssfiles) {
                            outputcssfile(cssfile);
                        }
                    }

                } else if (subname.equals("images") || subname.equals("Images")) {
                    outputBookImage(subitem.getName());
                } else {
                    if (subitem.isDirectory()) {
                        File[] extraFiles = subitem.listFiles();
                        for (File extraFile : extraFiles) {
                            outputOtherFile(extraFile);
                        }
                    }
                }
            } else {
                String subname = subitem.getName();
                if (subname.endsWith(".opf") || subname.endsWith(".ncx") || subname.endsWith(".jpg")) {
                    File newFile = new File(chapterdir + File.separator + subitem.getName());
                    //TODO 处理描述格式
//                    FileUtils.copyFile(subitem,newFile);
                    if (subname.endsWith(".opf")) {
                        rewriteOpf(subitem, newFile);
                    } else if (subname.endsWith(".ncx")) {
                        rewriteNcx(subitem, newFile);
                    } else {
                        FileUtils.copyFile(subitem, newFile);
                    }

                }
            }

        }
    }

    private void rewriteNcx(File ncxfile, File targetFile) {
        if (!ncxfile.exists()) {
            return;
        }
        System.out.println(ncxfile.getPath());
        try {
            InputStream is = new FileInputStream(ncxfile);
            org.w3c.dom.Document document = getXmlBuilder().parse(is);
//            org.w3c.dom.Document document = mDocumentBuilder.parse(ncxfile);
            org.w3c.dom.Node mapNode = document.getDocumentElement().getElementsByTagName("navMap").item(0);
            List<org.w3c.dom.Element> nodes = MyXmlUtil.findSubElements((org.w3c.dom.Element) mapNode, "navPoint");
            if (nodes == null || nodes.size() == 0) {
                return;
            }
            Integer directoryLevel = 0;
            for (int i = 0; i < nodes.size(); i++) {
                org.w3c.dom.Element e = (org.w3c.dom.Element) nodes.get(i);
//                String nodetitle = e.getElementsByTagName("text").item(0).getTextContent();
                org.w3c.dom.Element e1 = (org.w3c.dom.Element) e.getElementsByTagName("content").item(0);
                String href = e1.getAttribute("src");

                directoryLevel = 1;

                if (excludeChapterHrefs.contains(href)) {
                    mapNode.removeChild(e);
                } else {
                    rewriteNcxSubCata(e, directoryLevel);
                }

            }
            DomUtil.writeXml(targetFile.getPath(), document, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rewriteNcxSubCata(org.w3c.dom.Element parentE, Integer directoryLevel) {
        List<org.w3c.dom.Element> subnodes = MyXmlUtil.findSubElements((org.w3c.dom.Element) parentE, "navPoint");
        for (int si = 0; si < subnodes.size(); si++) {

            Integer currentLevel = directoryLevel + 1;

            org.w3c.dom.Element sube = (org.w3c.dom.Element) subnodes.get(si);

//            String nodetitle = sube.getElementsByTagName("text").item(0).getTextContent();

            org.w3c.dom.Element sube1 = (org.w3c.dom.Element) sube.getElementsByTagName("content").item(0);

            String subhref = sube1.getAttribute("src");
            if (excludeChapterHrefs.contains(subhref)) {
                parentE.removeChild(sube);
            } else {
                if (currentLevel > limitDirectoryLevel) {
                    parentE.removeChild(sube);
//                    if(parentE.getNextSibling()!=null) {
//                        parentE.insertBefore(sube,parentE.getNextSibling());
//                    }else {
//                        parentE.getParentNode().appendChild(sube);
//                    }
                } else {
                    rewriteNcxSubCata(sube, currentLevel);
                }
            }
        }
    }

    private ArrayList<EpubKernel.EpubCatalog> mCatalogList = new ArrayList<EpubKernel.EpubCatalog>();

    private void refreshDirectory() {
        List<EpubKernel.EpubCatalog> catalogList = EpubUtil.parserEpubFile(bookdir);
        originEpubInfo.setRelayDirectoryList(catalogList);
    }


    /**
     * 解析OPF文件，去掉忽略的章节，并重新保存
     *
     * @param opfile
     */
    private void rewriteOpf(File opfile, File targetFile) {
        if (!opfile.exists()) {
            return;
        }
        List<String> excludeOpfKeys = new ArrayList<String>();
        String epubType = originEpubInfo.getChapterroot();
        try {
            InputStream is = new FileInputStream(opfile);
            org.w3c.dom.Document document = getXmlBuilder().parse(is);
            document.getDocumentElement().setAttribute("xmlns", "http://www.idpf.org/2007/opf");
            // 获取opf文件中metadata相关数据(图书基础信息)
            if (epubType.equalsIgnoreCase("ops")) {
                // 获取opf文件中manifest相关数据(资源清单)
                org.w3c.dom.Element manifestNode = (org.w3c.dom.Element) document.getElementsByTagName("manifest").item(0);
                NodeList nodes = manifestNode.getChildNodes();
                if (nodes.getLength() > 0) {
                    for (int i = 0; i < nodes.getLength(); i++) {
                        org.w3c.dom.Node node = nodes.item(i);
                        if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            org.w3c.dom.Element elManifestItem = (org.w3c.dom.Element) node;
                            String href = elManifestItem.getAttribute("href");
                            if (StringUtils.isEmpty(href)) {
                                continue;
                            }
                            if (excludeChapterHrefs.contains(href)) {
                                excludeOpfKeys.add(elManifestItem.getAttribute("id"));
                                manifestNode.removeChild(node);
                            }
                        }
                    }
                }
                // 获取opf文件中spine相关数据(阅读顺序)
                org.w3c.dom.Element spineNode = (org.w3c.dom.Element) document.getElementsByTagName("spine").item(0);

                String ncx = spineNode.getAttribute("toc");

                NodeList spines = document.getElementsByTagName("itemref");
                if (spines.getLength() > 0) {
                    List<org.w3c.dom.Node> myspine = new ArrayList<org.w3c.dom.Node>();
                    for (int i = 0; i < spines.getLength(); i++) {
                        myspine.add(spines.item(i));
                    }

                    for (int i = 0; i < myspine.size(); i++) {
                        org.w3c.dom.Node spineItem = myspine.get(i);
                        String idref = DomUtil.getElementAttr(spineItem, "idref");
                        if (excludeOpfKeys.contains(idref)) {
                            spineNode.removeChild(spineItem);
                        }
                    }
                }
            } else if (epubType.equalsIgnoreCase("oebps")) {
                // 获取opf文件中manifest相关数据(资源清单)
                NodeList tryEs = document.getElementsByTagName("opf:manifest");
                if (tryEs == null || tryEs.getLength() == 0) {
                    tryEs = document.getElementsByTagName("manifest");
                    namespace = "";
                } else {
                    namespace = "opf";
                }
                org.w3c.dom.Element manifestNode = (org.w3c.dom.Element) tryEs.item(0);
//                org.w3c.dom.Element manifestNode = (org.w3c.dom.Element) document.getElementsByTagName("opf:manifest").item(0);
                NodeList nodes = manifestNode.getChildNodes();
                if (nodes.getLength() > 0) {
                    for (int i = 0; i < nodes.getLength(); i++) {
                        org.w3c.dom.Node node = nodes.item(i);
                        if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            org.w3c.dom.Element elManifestItem = (org.w3c.dom.Element) node;

                            String href = elManifestItem.getAttribute("href");
                            if (StringUtils.isEmpty(href)) {
                                continue;
                            }
                            if (excludeChapterHrefs.contains(href)) {
                                excludeOpfKeys.add(elManifestItem.getAttribute("id"));
                                manifestNode.removeChild(node);
                            }
                        }
                    }
                }
                // 获取opf文件中spine相关数据(阅读顺序)
                org.w3c.dom.Element spineNode = (org.w3c.dom.Element) document.getElementsByTagName(getNSTag("spine")).item(0);

                String ncx = spineNode.getAttribute("toc");

                NodeList spines = document.getElementsByTagName(getNSTag("itemref"));
                if (spines.getLength() > 0) {
                    List<org.w3c.dom.Node> myspine = new ArrayList<org.w3c.dom.Node>();
                    for (int i = 0; i < spines.getLength(); i++) {
                        myspine.add(spines.item(i));
                    }
                    for (int i = 0; i < myspine.size(); i++) {
                        org.w3c.dom.Node spineItem = myspine.get(i);
                        String idref = DomUtil.getElementAttr(spineItem, "idref");
                        if (excludeOpfKeys.contains(idref)) {
                            spineNode.removeChild(spineItem);
                        }
                    }
                }
            }
            DomUtil.writeXml(targetFile.getPath(), document, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝图片
     */
    private void outputBookImage(String imagedirname) {
        File old_imagedir = new File(old_chapterdir + File.separator + imagedirname);
        File imagedir = new File(chapterdir + File.separator + imagedirname);
        if (old_imagedir.exists()) {
            imagedir.mkdirs();
            File[] imgList = old_imagedir.listFiles();
            for (int i = 0; i < imgList.length; i++) {
                File imageitem = imgList[i];
                if (imageitem.getName().equalsIgnoreCase(".DS_Store")) {
                    continue;
                }
                File newImage = new File(imagedir + File.separator + imageitem.getName());
                try {
                    float imagesize = imageitem.length() / 1024;
                    //处理缩略图--图片缩小处理
                    float scale = 1;
                    if (imagesize > 100) {
                        //超过100K就处理
                        scale = 100 / imagesize;
                    }
                    int inx = imageitem.getName().lastIndexOf(".");
                    File small = new File(imagedir + File.separator + imageitem.getName().substring(0, inx) + "_small" + imageitem.getName().substring(inx));
                    IThumbnailCreator imageCreator = ThumbnailCreatorFactory.getCreator(imageitem.getPath(), small.getPath());
                    imageCreator.scale(scale);
                    FileUtil.copyFile(imageitem, newImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void outputBookChapter(EpubKernel.EpubCatalog catalog) {
        String old_chapterurl = old_chapterdir + File.separator + catalog.href;
        String new_chapterurl = chapterdir + catalog.href;
        File newChapter = new File(new_chapterurl);
        if (!newChapter.exists()) {
            //处理内容
            try {
                //清空jsoup的转义表，会使jsoup失去转义能力
                Entities.EscapeMode.base.getMap().clear();
                byte[] htmlbytes = FileUtil.readFileByBytes(old_chapterurl);
                String html = new String(htmlbytes, 3, htmlbytes.length - 3);

                Document srcdoc = Jsoup.parse(html);
                Document dstdoc = Jsoup.parse(getEmtpyChapter(catalog.title));
                Elements srcHeads = srcdoc.getElementsByTag("head");
                if (srcHeads != null && srcHeads.size() > 0) {
                    Elements dstHeads = dstdoc.getElementsByTag("head");
                    dstHeads.get(0).html(srcHeads.get(0).html());
                }

                Element srcBody = srcdoc.body();
                Element dstBody = dstdoc.body();
                Elements src_body_chlid = srcBody.children();
                Integer data_self_inx = 0;
//                for (int i = 0; i < src_body_chlid.size(); i++) {
                //由于批注在章节尾部，且批注在引用处已处理输出，也已从列表删除，所以改成次循环模式。
                while (src_body_chlid.size() > 0) {
                    Element src_element = src_body_chlid.get(0);
                    try {
                        Element dst_element = dstBody.appendElement(src_element.tagName());
                        copyElementAttribute(catalog, srcdoc, dstdoc, src_element, dst_element);
                        //书籍里面的标注
                        if (hideElement.contains(src_element)) {
                            dst_element.addClass("extnote_p_hide");
                        }
                        dst_element.attr("data-p-inx", last_p_inx + "");
                        dst_element.attr("data-self-inx", data_self_inx + "");
                        last_p_inx++;
                        data_self_inx++;


                        List<Node> childNode = src_element.childNodes();
                        WordOffset data_offset = new WordOffset(0);
                        for (int ni = 0; ni < childNode.size(); ni++) {
                            Node nodeitem = childNode.get(ni);
                            outputChapterNode(catalog, srcdoc, dstdoc, dst_element, data_offset, nodeitem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    src_body_chlid.remove(0);
                }
                System.out.println(new_chapterurl);
                FileUtils.writeStringToFile(new File(new_chapterurl), dstdoc.html());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //处理子节点
        if (catalog.subitem != null && catalog.subitem.size() > 0) {
            for (int si = 0; si < catalog.subitem.size(); si++) {
                //检查是否忽略
                Boolean isExclude = checkExclude(catalog.subitem.get(si));
                if (isExclude) {
                    continue;
                }
                outputBookChapter(catalog.subitem.get(si));
            }
        }
    }

    private void outputChapterNode(EpubKernel.EpubCatalog catalog, Document srcdoc, Document dstdoc, Element dst_element, WordOffset data_offset, Node nodeitem) {
        if (nodeitem instanceof TextNode) {
            TextNode textNode = (TextNode) nodeitem;
            String text = textNode.text();
            for (int j = 0; j < text.length(); j++) {
                Element oneWordElement = dst_element.appendElement("span");
                oneWordElement.addClass("word");
                oneWordElement.attr("data-offset", data_offset.getOffset() + "");
//                data_offset++;
                data_offset.addStep(1);
                oneWordElement.text(text.substring(j, j + 1));
            }
        } else if (nodeitem instanceof Element) {
            Element element = (Element) nodeitem;
            Element copyElement = dst_element.appendElement(element.tagName());
            //复制属性到新的目标节点
            copyElementAttribute(catalog, srcdoc, dstdoc, element, copyElement);
            if (element.childNodes().size() > 0) {
                for (int i = 0; i < element.childNodes().size(); i++) {
                    outputChapterNode(catalog, srcdoc, dstdoc, copyElement, data_offset, element.childNodes().get(i));
                }
            }
        }
    }

    /**
     * 复制Element属性
     *
     * @param src
     * @param dst
     */
    private void copyElementAttribute(EpubKernel.EpubCatalog catalog, Document srcdoc, Document dstdoc, Element src, Element dst) {
        List<Attribute> attrs = src.attributes().asList();
        if (attrs.size() > 0) {
            for (int ai = 0; ai < attrs.size(); ai++) {
                dst.attr(attrs.get(ai).getKey(), attrs.get(ai).getValue());
            }
        }
        if (hideElement.contains(src)) {
            return;
        }
        //处理标注
        String attrid = src.attr("id");
        if (!StringUtils.isEmpty(attrid) && src.tagName().equals("a")) {
            Elements selEs = srcdoc.body().select("a[href=" + catalog.href + "#" + attrid + "]");
            if (selEs != null && selEs.size() > 0) {
                Elements nextSelEs = selEs.get(0).parents();
                if (nextSelEs != null && nextSelEs.size() > 0) {
                    for (int ni = nextSelEs.size() - 1; ni >= 0; ni--) {
                        Element e = nextSelEs.get(ni);
                        hideElement.add(e);
                    }
                }
            }
        }
    }

    private String getEmtpyChapter(String title) {
        if (StringUtils.isEmpty(title)) {
            title = "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
        sb.append("<!DOCTYPE html PUBLIC \" -//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">");
        sb.append("<head>");
        sb.append("<meta charset=\"utf-8\"/>");
//        sb.append("<link href=\"css/stylesheet.css\" type=\"text/css\" rel=\"stylesheet\" />");
        sb.append("<title>" + title + "</title>");
        sb.append("</head>");
        sb.append("<body></body></html>");
        return sb.toString();
    }

    private String getNSTag(String tag) {
        if (StringUtils.isEmpty(namespace)) {
            return tag;
        } else {
            return namespace + ":" + tag;
        }
    }


    /*********************** xml ***************************/


    // xml parser
    private DocumentBuilderFactory mDocumentBuilderFactory;
    private DocumentBuilder mDocumentBuilder;


    // 初始化APP的epub工作目录
    private void initXmlParser() throws ParserConfigurationException {
        if (mDocumentBuilderFactory == null) {
            mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        }
    }


    private DocumentBuilder getXmlBuilder() {
        try {
            return mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
