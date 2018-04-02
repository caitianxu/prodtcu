package com.cjsz.tech.utils.epub;

import com.cjsz.tech.utils.FileUtil;
import com.cjsz.tech.utils.MyXmlUtil;
import com.cjsz.tech.utils.epub.utils.DomUtil;
import com.cjsz.tech.utils.zip.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by henry on 2017/8/15.
 */
public class EpubAnalyze {
    // xml parser
    private DocumentBuilderFactory mDocumentBuilderFactory;
    private DocumentBuilder mDocumentBuilder;

    //epub根目录
    String epubBaseDir = null;
    private static final String CONTAINER_FILE = "/META-INF/container.xml";
    private static final String CONTAINER_FILE_MEDIA_TYPE = "application/oebps-package+xml";
    private String epubFormat = null;
    private String chapterroot = null;

    public String getChapterroot() {
        return chapterroot;
    }

    private String opsFullPath = null;
    private String dirPre = "";
    private String ncx = null;
    private String ncxHref = null;

    public EpubAnalyze() {
        try {
            init();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void init() throws ParserConfigurationException {
        if (mDocumentBuilderFactory == null) {
            mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
            mDocumentBuilder.setEntityResolver(
                    new EntityResolver() {
                        public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                            return new org.xml.sax.InputSource(new StringBufferInputStream(""));
                        }
                    }
            );
        }
    }

    public String openEpubFile(String unzipdir, String epubFilePath) {
        String message;
        try {
            File filepath = new File(epubFilePath);
            int inx = filepath.getName().lastIndexOf(".");
            epubBaseDir = unzipdir + File.separator + filepath.getName().substring(0, inx);
            File epubBaseFile = new File(epubBaseDir);
            if (epubBaseFile.exists()) {
                FileUtil.del(epubBaseFile);
            }
            //先删除，再解压
            if (unzipEpub(epubFilePath, epubBaseDir)) {
                return parserEpubFile(epubBaseDir);
            } else {
                message = "文件损坏或/目录排序错乱/目录在最后导致无法识别!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

    private String errMessage = null;

    // 解压epub到workDiectory目录workDiectory的命名规则为BOOKID)
    private boolean unzipEpub(String inputZip, String destinationDirectory) {
        try {
            destinationDirectory = destinationDirectory.trim();
            int BUFFER = 10240;
            List<String> zipFiles = new ArrayList<String>();
            File sourceZipFile = new File(inputZip);
            File unzipDestinationDirectory = new File(destinationDirectory);
            unzipDestinationDirectory.mkdir();


            ZipFile zipFile;
            // Open Zip file for reading
            zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
            // Create an enumeration of the entries in the zip file
            Enumeration<?> zipFileEntries = zipFile.entries();
            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();


                File destFile = new File(unzipDestinationDirectory, currentEntry);
                if (currentEntry.endsWith(".epub")) {
                    zipFiles.add(destFile.getAbsolutePath());
                }
                // grab file's parent directory structure
                File destinationParent = destFile.getParentFile();
                // create the parent directory structure if needed
                destinationParent.mkdirs();

                // extract file if not a directory
                if (!entry.isDirectory()) {
                    BufferedInputStream is =
                            new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];
                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest =
                            new BufferedOutputStream(fos, BUFFER);
                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }

                    dest.flush();
                    dest.close();
                    is.close();
                }

            }
            zipFile.close();
            for (Iterator<String> iter = zipFiles.iterator(); iter.hasNext(); ) {
                String zipName = iter.next();
                ZipUtil.unzipEpub(zipName, destinationDirectory + File.separatorChar +
                        zipName.substring(0, zipName.lastIndexOf(".epub")));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(inputZip + "==解压错误==" + e.getMessage());
            return false;
        }
    }

    // 解析解压后的epub目录
    public String parserEpubFile(String epubUnzipDiectory) {
        String message = null;
        try {
            epubUnzipDiectory = epubUnzipDiectory.trim();
            File file = new File(epubUnzipDiectory);
            if (!file.exists()) {
                return "解压后文件目录丢失!";
            }
            if (StringUtils.isEmpty(epubBaseDir)) {
                epubBaseDir = epubUnzipDiectory;
            }
            System.out.println("解压成功:" + epubBaseDir);
            if (!parseContainer()) {
                return errMessage != null ? errMessage : "找不到full-path路径描述";
            } else {
                System.out.println("找到full-path=" + opsFullPath);
            }
            if (!parseOpfFile() || ncxHref == null || ncxHref == "") {
                return errMessage != null ? errMessage : "未找到目录描述文件地址";
            } else {
                System.out.println("目录文件：" + ncx);
                System.out.println("目录文件地址：" + ncxHref);
            }
            if (!parseMyNcxFile() || mCatalogList == null || mCatalogList.size() == 0) {
                return errMessage != null ? errMessage : "未找到目录相关内容描述";
            } else {
                System.out.println("一级目录:" + mCatalogList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

    /**
     * xml中寻找 文件路径
     *
     * @return
     */
    public boolean parseContainer() {
        boolean rt = true;
        try {
            String containerFile = this.epubBaseDir.trim() + CONTAINER_FILE;
            File file = new File(containerFile);
            if (!file.exists()) {
                return false;
            }
            FileInputStream fis = new FileInputStream(file);
            Document document = mDocumentBuilder.parse(fis);
            if (null == document) {
                return false;
            }
            String fullPath;
            String mediaType;
            Element rootNode = (Element) document.getElementsByTagName("rootfiles").item(0);
            if (null == rootNode) {
                return false;
            }
            NodeList nodes = rootNode.getChildNodes();
            if (null == nodes) {
                return false;
            }
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    fullPath = DomUtil.getElementAttr(document, "rootfile", "full-path", i);
                    mediaType = DomUtil.getElementAttr(document, "rootfile", "media-type", i);
                    if (mediaType.equals(CONTAINER_FILE_MEDIA_TYPE)) {
                        int inx = fullPath.indexOf("/");
                        if (inx > 0) {
                            chapterroot = fullPath.substring(0, inx);
                        }
                        opsFullPath = fullPath;
                        if (fullPath.toLowerCase().startsWith("ops")) {
                            epubFormat = "ops";
                        } else if (fullPath.toLowerCase().startsWith("oebps")) {
                            epubFormat = "oebps";
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            rt = false;
            errMessage = e.getMessage();
            e.printStackTrace();
        }
        return rt;
    }

    /**
     * 查找目录文件
     *
     * @return
     */
    public boolean parseOpfFileNew() {
        boolean rt = true;
        try {
            if (opsFullPath == null || opsFullPath.equals("")) {
                return false;
            }
            int len = opsFullPath.lastIndexOf('/');
            if (len != -1) {
                dirPre = opsFullPath.substring(0, len + 1);
            }
            String opfFile = this.epubBaseDir.trim() + "/" + opsFullPath;
            //epub文件格式OPS、OPF、OEBPS
            String epubType = opsFullPath.split("/")[0];
            File file = new File(opfFile);
            InputStream is = new FileInputStream(file);
            Document document = mDocumentBuilder.parse(is);

            Element spineNode = (Element) document.getElementsByTagName(getNSTag("spine")).item(0);
            if (null == spineNode) {
                return false;
            }
            NodeList spine_nodes = spineNode.getChildNodes();
            if (null == spine_nodes) {
                return false;
            }

            Element manifest_Node = (Element) document.getElementsByTagName(getNSTag("manifest")).item(0);
            if (null == manifest_Node) {
                return false;
            }
            NodeList manifest_nodes = manifest_Node.getChildNodes();
            if (null == manifest_nodes) {
                return false;
            }
            if (manifest_nodes.getLength() > 0 && spine_nodes.getLength() > 0) {
                List<Element> manifest_nodes_els = new ArrayList<>();
                for (int i = 0; i < manifest_nodes.getLength(); i++) {
                    Node node = manifest_nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elManifestItem = (Element) node;
                        manifest_nodes_els.add(elManifestItem);
                    }
                }
                for (int i = 0; i < spine_nodes.getLength(); i++) {
                    Node node = spine_nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) node;
                        String idref = el.getAttribute("idref");
                        if (idref != null && idref != "" && idref.indexOf("cover") == -1 && idref.indexOf("title") == -1) {
                            for (int j = 0; j < manifest_nodes_els.size(); j++) {
                                if (manifest_nodes_els.get(j).getAttribute("id").equals(idref)) {
                                    Element e = manifest_nodes_els.get(j);
                                    EpubCatalog catalog = new EpubCatalog();
                                    catalog.id = e.getAttribute("id");
                                    catalog.order = String.valueOf(mCatalogList.size() + 1);
                                    catalog.title = catalog.id;
                                    String href = e.getAttribute("href");
                                    String tag = "";
                                    if (href.contains("#")) {
                                        tag = href.substring(href.indexOf("#") + 1);
                                        href = href.substring(0, href.indexOf("#"));
                                    }
                                    catalog.href = href;
                                    catalog.tag = tag;
                                    mCatalogList.add(catalog);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            rt = false;
            errMessage = e.getMessage();
            e.printStackTrace();
        }
        return rt;
    }

    /**
     * 查找目录文件
     *
     * @return
     */
    public boolean parseOpfFile() {
        boolean rt = true;
        try {
            if (opsFullPath == null || opsFullPath.equals("")) {
                return false;
            }
            int len = opsFullPath.lastIndexOf('/');
            if (len != -1) {
                dirPre = opsFullPath.substring(0, len + 1);
            }
            String opfFile = this.epubBaseDir.trim() + "/" + opsFullPath;
            //epub文件格式OPS、OPF、OEBPS
            File file = new File(opfFile);
            InputStream is = new FileInputStream(file);
            Document document = mDocumentBuilder.parse(is);
            NodeList tryEs = document.getElementsByTagName("opf:manifest");
            if (tryEs == null || tryEs.getLength() == 0) {
                tryEs = document.getElementsByTagName("manifest");
                namespace = "";
            } else {
                namespace = "opf";
            }
            Element spineNode = (Element) document.getElementsByTagName(getNSTag("spine")).item(0);
            if (null == spineNode) {
                return false;
            }
            ncx = spineNode.getAttribute("toc");
            Element manifestNode = (Element) tryEs.item(0);
            if (null == manifestNode) {
                return false;
            }
            NodeList nodes = manifestNode.getChildNodes();
            if (null == nodes) {
                return false;
            }
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elManifestItem = (Element) node;
                        String mid = elManifestItem.getAttribute("id");
                        String nav = elManifestItem.getAttribute("properties");
                        if (StringUtils.isEmpty(ncxHref) && (mid.equals(ncx) || mid.equals("toc") || mid.equals("ncx") || nav.equals("nav"))) {
                            ncxHref = elManifestItem.getAttribute("href");
                        }
                    }
                }
            }
        } catch (Exception e) {
            rt = false;
            errMessage = e.getMessage();
            e.printStackTrace();
        }
        return rt;
    }


    private ArrayList<EpubCatalog> mCatalogList;

    public ArrayList<EpubCatalog> getCatalogList() {
        return mCatalogList;
    }

    /**
     * @return
     */
    public boolean parseMyNcxFile() {
        boolean rt = true;
        try {
            epubBaseDir = epubBaseDir.trim();
            String resultHref = epubBaseDir + "/" + dirPre + ncxHref;
            File file = new File(resultHref);
            if (!file.exists()) {
                resultHref = epubBaseDir + "/" + ncxHref;
                file = new File(resultHref);
            }
            int inx = ncxHref.indexOf("/");
            if (inx > 0) {
                chapterroot = chapterroot + "/" + ncxHref.substring(0, inx);
            }
            System.out.println("purl:" + chapterroot);
            mCatalogList = new ArrayList<>();
            if (!file.exists()) {
                return false;
            }
            //xhtml中读取
            if (resultHref.endsWith("xhtml")) {
                org.jsoup.nodes.Document doc = Jsoup.parse(file, "utf-8");
                Elements rootop = doc.select("nav[epub:type$=toc]");
                if (rootop == null || rootop.size() == 0) {
                    return false;
                }
                Elements top = rootop.get(0).children();
                if (top == null || top.size() == 0) {
                    return false;
                }
                Elements level1List = null;
                for (int i = 0; i < top.size(); i++) {
                    if (top.get(i).tagName().equals("ol")) {
                        level1List = top.get(i).children();
                    }
                }
                if (level1List == null || level1List.size() == 0) {
                    return false;
                }
                for (int i = 0; i < level1List.size(); i++) {
                    org.jsoup.nodes.Element level1E = level1List.get(i);
                    EpubCatalog catalog = new EpubCatalog();
                    catalog.id = level1E.attr("id");
                    Elements hrefsE = level1E.select("a");
                    if (hrefsE.size() > 0) {
                        catalog.href = hrefsE.get(0).attr("href");
                        catalog.title = hrefsE.get(0).text();
                    }
                    catalog.order = "" + (i + 1);
                    if (StringUtils.isEmpty(catalog.href)) {
                        continue;
                    }
                    String href = catalog.href;
                    String tag = "";
                    if (href.contains("#")) {
                        tag = href.substring(href.indexOf("#") + 1);
                        href = href.substring(0, href.indexOf("#"));
                    }
                    catalog.href = href;
                    catalog.tag = tag;
                    mCatalogList.add(catalog);

                    Elements level2Root = level1E.select("ol");
                    if (level2Root.size() > 0) {
                        Elements level2List = level2Root.get(0).children();
                        for (int j = 0; j < level2List.size(); j++) {
                            org.jsoup.nodes.Element level2E = level2List.get(j);
                            EpubCatalog subcatalog = new EpubCatalog();
                            subcatalog.id = level2E.attr("id");
                            Elements subhrefsE = level2E.select("a");
                            if (subhrefsE.size() > 0) {
                                subcatalog.href = subhrefsE.get(0).attr("href");
                                subcatalog.title = subhrefsE.get(0).text();
                            }
                            subcatalog.order = "" + (j + 1);

                            if (StringUtils.isEmpty(catalog.href)) {
                                continue;
                            }
                            href = subcatalog.href;
                            tag = "";
                            if (href.contains("#")) {
                                tag = href.substring(href.indexOf("#") + 1);
                                href = href.substring(0, href.indexOf("#"));
                            }
                            subcatalog.href = href;
                            subcatalog.tag = tag;
                            catalog.subitem.add(subcatalog);
                        }
                    }
                }
            } else {
                Document document = mDocumentBuilder.parse(new FileInputStream(file));
                if (null == document) {
                    return false;
                }
                Node mapNode = document.getDocumentElement().getElementsByTagName("navMap").item(0);
                if (null == mapNode) {
                    return false;
                }
                List<Element> nodes = MyXmlUtil.findSubElements((Element) mapNode, "navPoint");
                for (int i = 0; i < nodes.size(); i++) {
                    Element e = nodes.get(i);
                    addCatalog(e, null);
                }
            }
            //如果只有一个目录
            if (mCatalogList.size() == 1 && mCatalogList.get(0).subitem.size() == 0) {
                parseOpfFileNew();
            }
        } catch (Exception e) {
            rt = false;
            errMessage = e.getMessage();
            e.printStackTrace();
        }
        return rt;
    }

    List<Map> epbList = new ArrayList<>();

    private void addCatalog(Element e, EpubCatalog catalogPar) {
        EpubCatalog catalog = new EpubCatalog();
        catalog.id = e.getAttribute("id");
        catalog.order = e.getAttribute("playOrder");
        catalog.title = e.getElementsByTagName("text").item(0).getTextContent();
        Element e1 = (Element) e.getElementsByTagName("content").item(0);
        String href = e1.getAttribute("src");
        String tag = "";
        if (href.contains("#")) {
            tag = href.substring(href.indexOf("#") + 1);
            href = href.substring(0, href.indexOf("#"));
        }
        catalog.href = href;
        catalog.tag = tag;
        Map<String, String> maps = new HashMap<>();
        maps.put("id", catalog.id);
        maps.put("href", catalog.href);
        maps.put("title", catalog.title);
        maps.put("order", catalog.order);
        maps.put("tag", catalog.tag);
        if (epbList.size() > 0 && epbList.contains(maps)) {
            return;
        }
        epbList.add(maps);
        NodeList nodes = e.getElementsByTagName("navPoint");
        if (nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Element ee = (Element) nodes.item(i);
                addCatalog(ee, catalog);
            }
        }
        if (catalogPar != null) {
            catalogPar.subitem.add(catalog);
        } else {
            mCatalogList.add(catalog);
        }
    }

    public class EpubCatalog {
        public String id;
        public String href;
        public String title;
        public String order;
        public String tag;
        public List<EpubCatalog> subitem = new ArrayList<EpubCatalog>();
    }

    private String namespace = "";

    private String getNSTag(String tag) {
        if (StringUtils.isEmpty(namespace)) {
            return tag;
        } else {
            return namespace + ":" + tag;
        }
    }
}
