package com.cjsz.tech.utils.epub;


import com.cjsz.tech.utils.FileUtil;
import com.cjsz.tech.utils.MyXmlUtil;
import com.cjsz.tech.utils.epub.domain.element.BitmapElement;
import com.cjsz.tech.utils.epub.domain.element.ParagraphElement;
import com.cjsz.tech.utils.epub.utils.DomUtil;
import com.cjsz.tech.utils.epub.utils.SparseArray;
import com.cjsz.tech.utils.epub.utils.TextUtil;
import com.cjsz.tech.utils.zip.ZipUtil;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EpubKernel {

    // xml parser
    private DocumentBuilderFactory mDocumentBuilderFactory;
    private DocumentBuilder mDocumentBuilder;

    // epub基本目录(每个epub解压后的基本目录)
    private String epubBaseDir = "";

    public String getEpubBaseDir() {
        return epubBaseDir;
    }

    // opf
    private List<String> opfList;

    // metadata
    private Map<String, String> metadataMap;
    // manifestitme
    private Map<String, EpubManifestItem> manifestItemMap;
    // spine
    private SparseArray<String> spineMap;

    /**
     * 1: OPS 2: OCF
     */
    private String epubFormat = "ops";

    private String chapterroot;

    private String ncx;

    private String namespace = "";

    private static final String CONTAINER_FILE = "/META-INF/container.xml";
    private static final String CONTAINER_FILE_MEDIA_TYPE = "application/oebps-package+xml";


//    private static EpubKernel THIS = new EpubKernel();

    public EpubKernel() {
        // TODO Auto-generated constructor stub
        try {
            init();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    public static EpubKernel getInstance() {
//        return THIS;
//    }
//
//    public static EpubKernel newInstance() {
//        THIS = new EpubKernel();
//        return THIS;
//    }

    // 初始化APP的epub工作目录
    private void init() throws ParserConfigurationException {
        if (mDocumentBuilderFactory == null) {
            mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
            mDocumentBuilder.setEntityResolver(
                    new EntityResolver() {
                        public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                            return new org.xml.sax.InputSource(new StringBufferInputStream(""));
//                          return null;//这个的效果仍然是从网络来抓取DTD来验证
                        }
                    }
            );
        }
    }

    public boolean openEpubFile(String unzipdir, String epubFilePath, Boolean overwrite) {
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
                System.out.println("解压环节出错:"+ epubFilePath);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String openEpubFileNew(String unzipdir, String epubFilePath, Boolean overwrite) {
        String message = null;
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
                return parserEpubFileNew(epubBaseDir);
            } else {
                System.out.println("解压环节出错:"+ epubFilePath);
                message = "文件损坏或/目录排序错乱/目录在最后导致无法识别!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return  message;
    }

    public static void UnzipPubFile(String epubFilePath) {
        String tmp = getSavePath(epubFilePath);
        try {
            ZipUtil.unzipEpub(epubFilePath, tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 解压epub到workDiectory目录�?workDiectory的命名规则为BOOKID)
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
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(inputZip + "==解压错误==" + e.getMessage());
            return  false;
        }
    }
    // 解析解压后的epub目录
    public String parserEpubFileNew(String epubUnzipDiectory){
        String message = null;
        try{
            epubUnzipDiectory = epubUnzipDiectory.trim();
            File file = new File(epubUnzipDiectory);
            if (!file.exists()) {
                return "解压目录丢失!";
            }
            if (StringUtils.isEmpty(epubBaseDir)) {
                epubBaseDir = epubUnzipDiectory;
            }
            if (!parseContainer()) {
                return "暂不支持!";
            }
            if (!parseOpfFile()) {
                return "暂不支持!";
            }
            if (!parseMyNcxFile()) {
                return "暂不支持!";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }
    // 解析解压后的epub目录
    public boolean parserEpubFile(String epubUnzipDiectory) throws IOException, SAXException {
        epubUnzipDiectory = epubUnzipDiectory.trim();
        File file = new File(epubUnzipDiectory);
        if (!file.exists()) {
            return false;
        }
        if (StringUtils.isEmpty(epubBaseDir)) {
            epubBaseDir = epubUnzipDiectory;
        }
        if (!parseContainer()) {
            return false;
        }
        if (!parseOpfFile()) {
            return false;
        }
        if (!parseMyNcxFile()) {
            return false;
        }
        return true;
    }

    /**
     * 解析EPUB类型
     *
     * @param epubUnzipDiectory
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public String parseEpubContainer(String epubUnzipDiectory) throws IOException, SAXException {
        epubUnzipDiectory = epubUnzipDiectory.trim();
        File file = new File(epubUnzipDiectory);
        if (!file.exists()) {
            return "";
        }
        if (StringUtils.isEmpty(epubBaseDir)) {
            epubBaseDir = epubUnzipDiectory;
        }
        parseContainer();
        return chapterroot;
    }

    // 解析解压后的epub目录
    public void parserEpubFile() throws IOException, SAXException {
        epubBaseDir = epubBaseDir.trim();
        File file = new File(epubBaseDir);
        if (!file.exists()) {
            return;
        }
        parseContainer();
        parseOpfFile();
//        parseNcxFile();
        parseMyNcxFile();
    }

    public SortedMap<String, EpubChapter> getChapters() {
        SortedMap<String, EpubChapter> sortedMap = new TreeMap();
        sortedMap.putAll(mChaptersMap);
        return sortedMap;
    }


    private String coverPath = "";

    public String getCoverPath() {
        if (!TextUtil.isEmpty(coverPath)) {
            File f = new File(coverPath);
            if (f.exists()) {
                return coverPath;
            }
            coverPath = "";
        }
        String checkey = null;
        if (manifestItemMap.containsKey("cover")) {
            checkey = "cover";
        } else if (manifestItemMap.containsKey("cover-image")) {
            checkey = "cover-image";
        } else if (manifestItemMap.containsKey("Cover")) {
            checkey = "Cover";
        } else if (manifestItemMap.containsKey("Cover-image")) {
            checkey = "Cover-image";
        } else if (manifestItemMap.containsKey("cover_jpg")) {
            checkey = "cover_jpg";
        } else if (manifestItemMap.containsKey("Cover_jpg")) {
            checkey = "Cover_jpg";
        } else if (manifestItemMap.containsKey("Coverjpg")) {
            checkey = "Coverjpg";
        } else if (manifestItemMap.containsKey("Coverimage")) {
            checkey = "Coverimage";
        } else if (manifestItemMap.containsKey("cover.jpg")) {
            checkey = "cover.jpg";
        } else if (metadataMap.containsKey("cover")) {
            checkey = metadataMap.get("cover");
        } else {
            Iterator<EpubManifestItem> iter = manifestItemMap.values().iterator();
            while (iter.hasNext()) {
                EpubManifestItem tmpitem = iter.next();
                if (StringUtils.isNotEmpty(tmpitem.properties) && (tmpitem.properties.contains("cover"))) {
                    checkey = tmpitem.itemId;
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(checkey)) {
            return "";
        }
        EpubManifestItem item = manifestItemMap.get(checkey);
        if (item != null) {
            if (item.href.contains("%")) {
                try {
                    item.href = URLDecoder.decode(item.href, "utf-8");
                } catch (Exception e) {

                }
            }
            coverPath = epubBaseDir.trim() + "/" + this.dirPre + item.href;
        }
        return coverPath;
    }

    public boolean parseContainer() throws IOException, SAXException {
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
            opfList = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                fullPath = DomUtil.getElementAttr(document, "rootfile", "full-path", i);
                mediaType = DomUtil.getElementAttr(document, "rootfile", "media-type", i);
                if (mediaType.equals(CONTAINER_FILE_MEDIA_TYPE)) {
                    int inx = fullPath.indexOf("/");
                    if (inx > 0) {
                        chapterroot = fullPath.substring(0, inx);
                    }
                    opfList.add(fullPath);
                    if (fullPath.toLowerCase().startsWith("ops")) {
                        this.setEpubFormat("ops");
                    } else if (fullPath.toLowerCase().startsWith("oebps")) {
                        this.setEpubFormat("oebps");
                    }
                    break;
                }
            }
        }
        return true;
    }

    public String getDirPre() {
        return dirPre;
    }

    private String dirPre = "";

    public boolean parseOpfFile() throws IOException, SAXException {
        if (opfList == null || opfList.size() <= 0) {
            return false;
        }
        int len = opfList.get(0).lastIndexOf('/');
        if (len != -1) {
            dirPre = opfList.get(0).substring(0, len + 1);
        }
        String opfFile = this.epubBaseDir.trim() + "/" + opfList.get(0);
        //epub文件格式OPS、OPF、OEBPS
        String epubType = opfList.get(0).split("/")[0];
        File file = new File(opfFile);
        InputStream is = new FileInputStream(file);
        Document document = mDocumentBuilder.parse(is);
        // 获取opf文件中metadata相关数据(图书基础信息)
        metadataMap = new HashMap<String, String>();
        metadataMap.put("title", DomUtil.getElementValue(document, "dc:title", 0));
        metadataMap.put("creator", DomUtil.getElementValue(document, "dc:creator", 0));
        metadataMap.put("description", DomUtil.getElementValue(document, "dc:description", 0));
        metadataMap.put("publisher", DomUtil.getElementValue(document, "dc:publisher", 0));
        metadataMap.put("date", DomUtil.getElementValue(document, "dc:date", 0));
        metadataMap.put("identifier", DomUtil.getElementValue(document, "dc:identifier", 0).replace("-", ""));
        metadataMap.put("language", DomUtil.getElementValue(document, "dc:language", 0));
        //补全获取属性规则
        try {
            String cover = DomUtil.getCoverElementValue(document, "meta", 0);
            if (StringUtils.isEmpty(cover)) {
                cover = DomUtil.getCoverElementValue(document, "opf:meta", 0);
            }
            if (StringUtils.isNotEmpty(cover)) {
                metadataMap.put("cover", cover);
            }
        } catch (Exception e) {
            System.out.println("封面读取异常!");
            metadataMap.put("cover", "");
        }

        if (epubType.equalsIgnoreCase("ops") || epubType.contains(".opf")) {
            // 获取opf文件中manifest相关数据(资源清单)
            Element manifestNode = (Element) document.getElementsByTagName("manifest").item(0);
            if (null == manifestNode) {
                return false;
            }
            NodeList nodes = manifestNode.getChildNodes();
            if (null == nodes) {
                return false;
            }
            if (nodes.getLength() > 0) {
                manifestItemMap = new HashMap<String, EpubManifestItem>();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elManifestItem = (Element) node;
                        EpubManifestItem item = new EpubManifestItem();
                        item.itemId = elManifestItem.getAttribute("id");
                        item.href = elManifestItem.getAttribute("href");
                        item.mediaType = elManifestItem.getAttribute("media_type");
                        item.properties = elManifestItem.getAttribute("properties");
                        manifestItemMap.put(item.itemId, item);
                    }
                }
            }
            // 获取opf文件中spine相关数据(阅读顺序)
            Element spineNode = (Element) document.getElementsByTagName("spine").item(0);
            if (null == spineNode) {
                return false;
            }
            ncx = spineNode.getAttribute("toc");
            NodeList spines = document.getElementsByTagName("itemref");
            if (null == spines) {
                return false;
            }
            if (spines.getLength() > 0) {
                spineMap = new SparseArray<>();
                for (int i = 0; i < spines.getLength(); i++) {
                    String idref = DomUtil.getElementAttr(document, "itemref", "idref", i);
                    spineMap.put(i, idref);
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
            Element manifestNode = (Element) tryEs.item(0);
            if (null == manifestNode) {
                return false;
            }
            NodeList nodes = manifestNode.getChildNodes();
            if (null == nodes) {
                return false;
            }
            if (nodes.getLength() > 0) {
                manifestItemMap = new HashMap<String, EpubManifestItem>();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elManifestItem = (Element) node;
                        EpubManifestItem item = new EpubManifestItem();
                        item.itemId = elManifestItem.getAttribute("id");
                        item.href = elManifestItem.getAttribute("href");
                        item.mediaType = elManifestItem.getAttribute("media_type");
                        item.properties = elManifestItem.getAttribute("properties");
                        manifestItemMap.put(item.itemId, item);
                    }
                }
            }
            // 获取opf文件中spine相关数据(阅读顺序)
            Element spineNode = (Element) document.getElementsByTagName(getNSTag("spine")).item(0);
            if (null == spineNode) {
                return false;
            }
            ncx = spineNode.getAttribute("toc");
            NodeList spines = document.getElementsByTagName(getNSTag("itemref"));
            if (null == spines) {
                return false;
            }
            if (spines.getLength() > 0) {
                spineMap = new SparseArray<>();
                for (int i = 0; i < spines.getLength(); i++) {
                    String idref = DomUtil.getElementAttr(document, getNSTag("itemref"), "idref", i);
                    spineMap.put(i, idref);
                }
            }
        }
        else if(epubType.equalsIgnoreCase("META-INF")){
            //需要补全规则
        }
        return true;
    }

    private String getNSTag(String tag) {
        if (StringUtils.isEmpty(namespace)) {
            return tag;
        } else {
            return namespace + ":" + tag;
        }
    }

    private String findEpubManifestItemID(String href) {
        Iterator<EpubManifestItem> items = manifestItemMap.values().iterator();
        while (items.hasNext()) {
            EpubManifestItem item = items.next();
            if (item.href.equalsIgnoreCase(href)) {
                return item.itemId;
            }
        }
        return null;
    }

    private ArrayList<EpubCatalog> mCatalogList;

    public void parseNcxFile() throws IOException, SAXException {
        EpubManifestItem item = manifestItemMap.get("ncx");
        String ncxHref = null;
        if (item != null) {
            ncxHref = item.href;
        }
        if (TextUtil.isEmpty(ncxHref)) {
            ncxHref = "/toc.ncx";
        }
        ncxHref = epubBaseDir + "/" + dirPre + ncxHref;
        File file = new File(ncxHref);
        if (file.exists()) {
            Document document = mDocumentBuilder.parse(new FileInputStream(file));
            NodeList list = document.getElementsByTagName("navPoint");
            if (mCatalogList == null) {
                mCatalogList = new ArrayList<EpubCatalog>();
            }
            for (int i = 0; i < list.getLength(); i++) {
                EpubCatalog catalog = new EpubCatalog();
                Element e = (Element) list.item(i);
                catalog.id = e.getAttribute("id");
                catalog.title = e.getElementsByTagName("text").item(0).getTextContent();
                if (catalog.title != null && catalog.title.equals("")) {
                    EpubManifestItem item1 = manifestItemMap.get(catalog.id);
                    if (item1 != null) {
                        item1.title = catalog.title;
                    }
                }
                Element e1 = (Element) e.getElementsByTagName("content").item(0);

                String href = e1.getAttribute("src");
                if (!href.contains("#")) {
                    //href = href.substring(0, href.indexOf("#"));
                    //catalog.href = href;
                    catalog.href = href;
                    String item_id = findEpubManifestItemID(href);
                    if (item_id != null) {
                        catalog.id = item_id;
                    }
                    mCatalogList.add(catalog);
                } else {
                    href = href.substring(0, href.indexOf("#"));
                    if (!isContains(href)) {
                        catalog.href = href;
                        String item_id = findEpubManifestItemID(href);
                        if (item_id != null) {
                            catalog.id = item_id;
                        }
                        mCatalogList.add(catalog);
                    }
                }
            }
        } else {
            if (mCatalogList == null) {
                mCatalogList = new ArrayList<EpubCatalog>();
            }

        }
    }


    public boolean parseMyNcxFile() throws IOException, SAXException {
        if (null == manifestItemMap) {
            return false;
        }
        EpubManifestItem item = manifestItemMap.get("ncx") == null ? manifestItemMap.get(ncx) : manifestItemMap.get("ncx");
        String ncxHref = null;
        if (item == null) {
            item = manifestItemMap.get("toc") == null ? manifestItemMap.get("navid") : manifestItemMap.get("toc");
            if (item == null) {
                Iterator<EpubManifestItem> iter = manifestItemMap.values().iterator();
                while (iter.hasNext()) {
                    EpubManifestItem tmpitem = iter.next();
                    if (StringUtils.isNotEmpty(tmpitem.properties) && (tmpitem.properties.equals("nav") || tmpitem.properties.equals("toc"))) {
                        item = tmpitem;
                        ncxHref = item.href;
                        break;
                    }
                }
            } else {
                ncxHref = item.href;
            }
        } else {
            ncxHref = item.href;
        }
        if (TextUtil.isEmpty(ncxHref)) {
            ncxHref = "/toc.ncx";
        }
        epubBaseDir = epubBaseDir.trim();
        String resultHref = epubBaseDir + "/" + dirPre + ncxHref;
        File file = new File(resultHref);
        if (!file.exists()) {
            resultHref = epubBaseDir + "/" + ncxHref;
            file = new File(resultHref);
        }
        if (file.exists()) {
            if (mCatalogList == null) {
                mCatalogList = new ArrayList<>();
            }
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
                Elements level1List = top.get(0).children();
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
                    if (manifestItemMap.containsKey(catalog.id)) {
                        catalog.href = manifestItemMap.get(catalog.id).href;
                        mCatalogList.add(catalog);
                    } else {
                        String checkey = null;
                        if (catalog.href.contains("#")) {
                            int inx = catalog.href.lastIndexOf("#");
                            catalog.href = catalog.href.substring(0, inx);
                        }
                        if (manifestItemMap.containsKey(catalog.href)) {
                            checkey = catalog.href;
                        } else {
                            String tmpkey = catalog.href.replace(".", "_");
                            if (manifestItemMap.containsKey(tmpkey)) {
                                checkey = tmpkey;
                            }
                        }
                        if (StringUtils.isEmpty(checkey)) {
                            continue;
                        }
                        catalog.href = manifestItemMap.get(checkey).href;
                        mCatalogList.add(catalog);
                    }

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

                            if (manifestItemMap.containsKey(subcatalog.id)) {
                                subcatalog.href = manifestItemMap.get(subcatalog.id).href;
                                catalog.subitem.add(subcatalog);
                            } else {
                                if (subcatalog.href.contains("#")) {
                                    continue;
                                }
                                String checkey = null;
                                if (manifestItemMap.containsKey(subcatalog.href)) {
                                    checkey = subcatalog.href;
                                } else {
                                    String tmpkey = subcatalog.href.replace(".", "_");
                                    if (manifestItemMap.containsKey(tmpkey)) {
                                        checkey = tmpkey;
                                    }
                                }
                                if (StringUtils.isEmpty(checkey)) {
                                    continue;
                                }
                                subcatalog.href = manifestItemMap.get(checkey).href;
                                catalog.subitem.add(subcatalog);
                            }

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
                    EpubCatalog catalog = new EpubCatalog();
                    Element e = (Element) nodes.get(i);
                    catalog.id = e.getAttribute("id");
                    catalog.order = e.getAttribute("playOrder");
                    catalog.title = e.getElementsByTagName("text").item(0).getTextContent();
                    if (catalog.title != null && catalog.title.equals("")) {
                        EpubManifestItem item1 = manifestItemMap.get(catalog.id);
                        if (item1 != null) {
                            item1.title = catalog.title;
                        }
                    }
                    Element e1 = (Element) e.getElementsByTagName("content").item(0);

                    String href = e1.getAttribute("src");
                    String tag = "";
                    if (href.contains("#")) {
                        tag = href.substring(href.indexOf("#") + 1);
                        href = href.substring(0, href.indexOf("#"));
                        if (!isContains(href)) {
                            catalog.href = href;
                            String item_id = findEpubManifestItemID(href);
                            if (item_id != null) {
                                catalog.id = item_id;
                            }
                        }
                    }
                    catalog.href = href;
                    catalog.tag = tag;
                    String item_id = findEpubManifestItemID(href);
                    if (item_id != null) {
                        catalog.id = item_id;
                    }
                    mCatalogList.add(catalog);
                    parseSubCata(catalog, e);
                }
                //如果目录描述不等于 ncx目录描述 重新按ncx获取一遍
                if (false && spineMap.size() > mCatalogList.size()) {
                    mCatalogList.clear();
                    for (int i = 0; i < spineMap.size(); i++) {
                        String sid = spineMap.get(i);
                        EpubManifestItem item1 = manifestItemMap.get(sid);
                        if (item1 != null) {
                            EpubCatalog catalog = new EpubCatalog();
                            catalog.id = sid;
                            catalog.order = String.valueOf(i + 1);
                            catalog.title = sid;
                            String href = item1.href;
                            String tag = "";
                            if (href.contains("#")) {
                                tag = href.substring(href.indexOf("#") + 1);
                                href = href.substring(0, href.indexOf("#"));
                                if (!isContains(href)) {
                                    catalog.href = href;
                                    String item_id = findEpubManifestItemID(href);
                                    if (item_id != null) {
                                        catalog.id = item_id;
                                    }
                                }
                            }
                            catalog.href = href;
                            catalog.tag = tag;
                            mCatalogList.add(catalog);
                        }
                    }
                }//other if
            }

        } else {
            if (mCatalogList == null) {
                mCatalogList = new ArrayList<>();
            }
        }
        return true;
    }

    private void parseSubCata(EpubCatalog parent, Element parentE) {
        List<Element> subnodes = MyXmlUtil.findSubElements((Element) parentE, "navPoint");
        for (int si = 0; si < subnodes.size(); si++) {
            EpubCatalog subcatalog = new EpubCatalog();
            Element sube = (Element) subnodes.get(si);
            subcatalog.id = sube.getAttribute("id");
            subcatalog.order = sube.getAttribute("playOrder");
            subcatalog.title = sube.getElementsByTagName("text").item(0).getTextContent();
            if (subcatalog.title != null && subcatalog.title.equals("")) {
                EpubManifestItem item1 = manifestItemMap.get(subcatalog.id);
                if (item1 != null) {
                    item1.title = subcatalog.title;
                }
            }
            Element sube1 = (Element) sube.getElementsByTagName("content").item(0);

            String subhref = sube1.getAttribute("src");
            String subtag = "";
            if (subhref.contains("#")) {
                subtag = subhref.substring(subhref.indexOf("#") + 1);
                subhref = subhref.substring(0, subhref.indexOf("#"));
                if (!isContains(subhref)) {
                    subcatalog.href = subhref;
                    String subitem_id = findEpubManifestItemID(subhref);
                    if (subitem_id != null) {
                        subcatalog.id = subitem_id;
                    }
                }
            }
            subcatalog.href = subhref;
            subcatalog.tag = subtag;
            String subitem_id = findEpubManifestItemID(subhref);
            if (subitem_id != null) {
                subcatalog.id = subitem_id;
            }
            parent.subitem.add(subcatalog);
            parseSubCata(subcatalog, sube);
        }
    }

    private boolean isContains(String href) {
        for (EpubCatalog catalog : mCatalogList) {
            if (catalog.href.equalsIgnoreCase(href)) {
                return true;
            }

        }
        return false;
    }

    public ArrayList<EpubCatalog> getCatalogList() {
        return mCatalogList;
    }

    public SparseArray<String> getSpineMap() {
        return spineMap;
    }

    /**
     * 获取标题
     *
     * @return
     */
    public String getTitle() {
        return metadataMap.get("title");
    }

    /**
     * 获取作者
     *
     * @return
     */
    public String getAuthor() {
        return metadataMap.get("creator");
    }

    /**
     * 获取描述
     *
     * @return
     */
    public String getDesc() {
        return metadataMap.get("description");
    }

    /**
     * 获取出版日期
     *
     * @return
     */
    public String getDate() {
        return metadataMap.get("date");
    }

    /**
     * 获取出版社
     *
     * @return
     */
    public String getPublisher() {
        return metadataMap.get("publisher");
    }

    /**
     * 获取书籍标识
     *
     * @return
     */
    public String getIdentifier() {
        return metadataMap.get("identifier");
    }

    /**
     * 获取语言
     *
     * @return
     */
    public String getLanguage() {
        return metadataMap.get("language");
    }


//
//    public String getHtmlUrlByIndex(int index) {
//        String pageIndex = spineMap.get(index);
//        EpubManifestItem item = manifestItemMap.get(pageIndex);
//        if (item != null)
//            return "file:///" + this.epubBaseDir + "/" + item.href;
//        else
//            return "";
//    }

    public class EpubManifestItem {
        public String itemId;
        public String href;
        public String mediaType;
        public String title;
        public String properties;
    }

    public class EpubCatalog {
        public String id;
        public String href;
        public String title;
        public String order;
        public String tag;
        public List<EpubCatalog> subitem = new ArrayList<EpubCatalog>();
    }

    private static String EPUB_PATH = "";

    private static String getSavePath(String path) {
//        if (TextUtil.isEmpty(EPUB_PATH))
//            EPUB_PATH = BaseApplication.getInstance().getStorePath() + "/epub/";
//        return EPUB_PATH + MD5Util.string2MD5(path);
        return "WEB-INF/books";
//        ClassPathResource cr = new ClassPathResource("static/json/"+fileName+".json");
//        try {
//          String fileContent = InputStreamTOString(cr.getInputStream(),"utf-8");
//          JSONObject result = JSONObject.parseObject(fileContent);
//          return result;
//        } catch (Exception e) {
//          e.printStackTrace();
//        }

    }


    HashMap<String, EpubChapter> mChaptersMap = new HashMap<String, EpubChapter>();

    /**
     * 解析电子书epub
     *
     * @param chapterId
     * @return map0--"CHAPTERID"--strChapterId <br>
     * map1--"CHAPTERTITLE"--strChapterTitle <br>
     * map2--"CHAPTERCONTENT"--listContent <br>
     * 返回null则表示解析失败
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public EpubChapter parseChapter(String chapterId) {
        //定义一本书
        EpubChapter chapter = mChaptersMap.get(chapterId);
        if (chapter != null) {
            return chapter;
        }
        chapter = new EpubChapter();
        EpubManifestItem item = manifestItemMap.get(chapterId);
        if (item == null) {
            return null;
        }
        String chapterPath = epubBaseDir + "/" + dirPre + item.href;
        try {
            long time = System.currentTimeMillis();
            chapter.path = chapterPath;
            chapter.content = this.parse(chapterPath, chapterId);
            chapter.chapterId = chapterId;
            if (TextUtil.isEmpty(chapter.title) || chapter.title.equalsIgnoreCase("unknown")) {
                chapter.title = getTitleFromNcx(item.href);
            }
            mChaptersMap.put(chapterId, chapter);
            return chapter;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解析电子书epub
     *
     * @param chapterurl
     * @return map0--"CHAPTERID"--strChapterId <br>
     * map1--"CHAPTERTITLE"--strChapterTitle <br>
     * map2--"CHAPTERCONTENT"--listContent <br>
     * 返回null则表示解析失败
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public EpubChapter parseChapterContent(String chapterurl) {
        //定义一本书
        EpubChapter chapter = new EpubChapter();
        EpubManifestItem item = null;

        Iterator<EpubManifestItem> iter = manifestItemMap.values().iterator();
        while (iter.hasNext()) {
            EpubManifestItem item1 = iter.next();
            if ((dirPre + item1.href).equals(chapterurl)) {
                item = item1;
                break;
            }
        }
        if (item == null) {
            return null;
        }
        String chapterPath = epubBaseDir + "/" + dirPre + item.href;

        try {
            long time = System.currentTimeMillis();
            chapter.path = chapterPath;
            chapter.src = FileUtils.readFileToString(new File(chapter.path));
            chapter.chapterId = item.href;
            if (TextUtil.isEmpty(chapter.title) || chapter.title.equalsIgnoreCase("unknown")) {
                chapter.title = getTitleFromNcx(item.href);
            }
            return chapter;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

//	public EpubChapter parseChapter2(String chapterId ) {
//		//定义一本书
//		EpubChapter chapter = mChaptersMap.get(chapterId);
//		if(chapter!=null)
//			return chapter;
//
//
//		chapter = new EpubChapter();
//		String chapterPath = epubBaseDir + "/" +dirPre+  itemhref;
//		try {
//			long time = System.currentTimeMillis();
//			chapter.content = this.parse(chapterPath,chapterId+"");
//			chapter.chapterId = chapterId+"";
//			if(TextUtil.isEmpty(chapter.title) || chapter.title.equalsIgnoreCase("unknown")){
//				chapter.title = getTitleFromNcx(itemhref);
//			}
//			mChaptersMap.put(chapterId+"", chapter);
//			return chapter;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}

    private String getTitleFromNcx(String href) {
        // TODO Auto-generated method stub
        for (EpubCatalog ec : mCatalogList) {
            if (ec.href.contains(href)) {
                return ec.title;
            }
        }
        return "unknown";
    }

    CSSOMParser parser = new CSSOMParser(new SACParserCSS3());

    public List<ParagraphElement> parse(String chapterPath, String chapterId) {


        org.jsoup.nodes.Document document = null;
        try {
//			if(chapterPath.contains("#")){
//				chapterPath = chapterPath.substring(0,chapterPath.indexOf("#"));
//			}
            document = Jsoup.parse(new File(chapterPath), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Elements links = document.select("link[href]");
        StringBuilder sbBuilder = new StringBuilder();
        for (org.jsoup.nodes.Element link : links) {
            sbBuilder.append(readFile(this.epubBaseDir + "/" + dirPre + "/" + link.attr("href")));
        }
        /**
         * 处理css中的非法字符如下：
         * 1. @charset "UTF-8"
         * 2. /--注释--/
         */
        String regex = "\\@charset\\s.*\".*\";|\\/\\-\\-(.+?)\\-\\-\\/";
        String str = sbBuilder.toString();
        Pattern pat = Pattern.compile(regex);
        Matcher matcher = pat.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);

        InputSource source = new InputSource(new StringReader(sb.toString()));
        try {
            CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);
            final CSSRuleList rules = stylesheet.getCssRules();

            for (int i = 0; i < rules.getLength(); i++) {
                final CSSRule rule = rules.item(i);
                if (rule instanceof CSSStyleRule) {

                    CSSStyleRule styleRule = (CSSStyleRule) rule;
                    String selector = styleRule.getSelectorText();

                    Elements selectedElements = document.select(selector);
                    for (org.jsoup.nodes.Element selected : selectedElements) {

                        CSSStyleDeclaration styleDeclaration = styleRule.getStyle();
                        StringBuilder builder = new StringBuilder();
                        for (int j = 0; j < styleDeclaration.getLength(); j++) {
                            String propertyName = styleDeclaration.item(j);
                            String propertyValue = styleDeclaration.getPropertyValue(propertyName);
                            builder.append(propertyName).append(":").append(propertyValue).append(";");
                        }
                        if (selected.attr("style").length() > 0) {
                            CSSStyleDeclaration old_styleDeclaration = parser.parseStyleDeclaration(new InputSource(new StringReader(selected.attr("style"))));
                            for (int j = 0; j < old_styleDeclaration.getLength(); j++) {
                                String propertyName = old_styleDeclaration.item(j);
                                String propertyValue = old_styleDeclaration.getPropertyValue(propertyName);
                                boolean exist = false;
                                CSSStyleDeclaration new_styleDeclaration = styleRule.getStyle();
                                for (int k = 0; k < new_styleDeclaration.getLength(); k++) {
                                    String new_propertyName = new_styleDeclaration.item(k);
                                    if (propertyName.equalsIgnoreCase(new_propertyName)) {
                                        exist = true;
                                        break;
                                    }
                                }
                                if (!exist) {
                                    builder.append(propertyName).append(":").append(propertyValue).append(";");
                                }
                            }


                        }
                        selected.attr("style", builder.toString());


                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ParagraphElement> paragraphElements = new ArrayList<>();

        ArrayList<HashMap<String, String>> list = parseContent(document.getAllElements());
        for (HashMap<String, String> map : list) {
            if (map.get("type").equals("text")) {
                if (map.get("nodename").equals("p") || map.get("nodename").startsWith("h") || map.get("nodename").equals("li")) {

                    String text = map.get("text");

                    ParagraphElement ce = new ParagraphElement(text);
                    ce.setChapter(chapterId);
//                    ce.setCSSMap(new CSSMap(map.get("style")));
                    paragraphElements.add(ce);
                }
            } else if (map.get("type").equals("img")) {
                String src = map.get("img");
                BitmapElement be = new BitmapElement(src);
                be.setChapter(chapterId);
                paragraphElements.add(be);

            }
        }
        return paragraphElements;


    }

    /**
     * 递归解析html
     *
     * @param nodeList
     * @return
     */
    public ArrayList<HashMap<String, String>> parseContent(Elements nodeList) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < nodeList.size(); i++) {
            org.jsoup.nodes.Element node = nodeList.get(i);

            if (node.nodeName().equals("div")) {
//                Elements nodeListDiv = node.children();
//                list.addAll(parseContent(nodeListDiv));
            } else if (node.nodeName().equals("img")) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("type", "img");
                map.put("img", node.attr("src"));
                map.put("style", node.attr("style"));
                //map.put("class", node.className());
                list.add(map);
            } else if (node.nodeName().equals("p")) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("type", "text");
                map.put("text", node.text());
                map.put("nodename", node.nodeName());
                map.put("style", node.attr("style"));
                list.add(map);
//                if (node.childNodes().size() > 1) {
//                    Elements nodeListDiv = node.children();
//                    list.addAll(parseContent(nodeListDiv));
//                }
            } else if (node.nodeName().equals("h") || node.nodeName().equals("h1") || node.nodeName().equals("h2") || node.nodeName().equals("h3")) {
                if (!node.text().equals("")) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("type", "text");
                    map.put("text", node.text());
                    map.put("nodename", node.nodeName());
                    map.put("style", node.attr("style"));
                    list.add(map);
                }
            } else if (node.nodeName().equals("ul")) {
            } else if (node.nodeName().equals("li")) {
                if (node.childNodes().size() > 1) {
                    String content = ((org.jsoup.nodes.Element) node.childNodes().get(0)).text();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("type", "text");
                    map.put("text", content);
                    map.put("style", node.attr("style"));
                    map.put("nodename", node.nodeName());
                    list.add(map);
                } else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("type", "text");
                    map.put("text", node.text());
                    map.put("style", node.attr("style"));
                    map.put("nodename", node.nodeName());
                    list.add(map);
                }
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("type", "text");
//                map.put("text", node.text());
//                map.put("style", node.attr("style"));
//                map.put("nodename", node.nodeName());
//                list.add(map);
            }
        }
        return list;
    }

    public String getChapterRoot() {
        return chapterroot;
    }

    private StringBuffer readFile(String filePath) {

        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {


            String line;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            line = reader.readLine();
            while (line != null) {
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                    line = line.replace("\uFEFF", "");
                }
                buffer.append(line);
                buffer.append("\n");
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return buffer;

    }

    public String getEpubFormat() {
        return epubFormat;
    }

    public void setEpubFormat(String epubFormat) {
        this.epubFormat = epubFormat;
    }
}
