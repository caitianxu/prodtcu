package com.cjsz.tech.cms.util;

import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.domain.ArticleTemp;
import com.cjsz.tech.cms.service.ArticleService;
import com.cjsz.tech.cms.service.ArticleTempService;
import com.cjsz.tech.cms.service.Impl.ArticleServiceImpl;
import com.cjsz.tech.cms.service.Impl.ArticleTempServiceImpl;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.utils.FileUtil;
import com.cjsz.tech.utils.zip.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
public class TempUnZip {

    private final static ArticleTempService articleTempService = (ArticleTempServiceImpl) SpringContextUtil.getBean("articleTempServiceImpl");

    private final static ArticleService articleService = (ArticleServiceImpl) SpringContextUtil.getBean("articleServiceImpl");

    //增加、修改（模板文件改变）模板，该机构下所有资讯生成html
    public static void runTempUnZip() {
        String path = "";
        try {
            //查询未解析成功的模板
            List<ArticleTemp> articleTemps = articleTempService.selectUnZip();
            for(ArticleTemp articleTemp : articleTemps){
                runParseTemp(articleTemp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //增加、修改（内容改变）资讯：根机构不变，其他机构解析生成新的html（或可直接修改html）（变更的资讯多模板，生成多条html）
    public static void runEditArticle() {
        String path = "";
        try {
            //查询未解析成功的资讯
            List<Article> articles = articleService.selectUnParse();
            for(Article article : articles){
                runParseArticle(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runParseTemp(ArticleTemp articleTemp) {
        try {
            articleTempService.updateArticleTempStatus(articleTemp.getArticle_temp_id(), 3);//1:解析;2:未解析;3:解析中
            String path = SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
            //模板根目录
            File tempdirFile = new File(path + "/templates");
            if(!tempdirFile.exists()) {
                tempdirFile.mkdirs();
            }
            //模板目录--模板
            File tempdirFile1 = new File(path + "/templates/" + articleTemp.getArticle_temp_id());
            if(!tempdirFile1.exists()) {
                tempdirFile1.mkdirs();
            }
            //缓存根目录
            File cachedirFile = new File(path + "/cachehtmls");
            if(!cachedirFile.exists()) {
                cachedirFile.mkdirs();
            }
            //缓存目录--机构
            File cachedirFile1 = new File(path + "/cachehtmls/" + articleTemp.getOrg_id());
            if(!cachedirFile1.exists()) {
                cachedirFile1.mkdirs();
            }
            //缓存目录--机构--模板
            File cachedirFile2 = new File(path + "/cachehtmls/" + articleTemp.getOrg_id() +"/" +articleTemp.getArticle_temp_id());
            if(!cachedirFile2.exists()) {
                cachedirFile2.mkdirs();
            }

            int len = articleTemp.getTemp_url().indexOf("/uploads");
            path += articleTemp.getTemp_url().substring(len);
            ZipUtil.unzip(path, tempdirFile1.getPath());
            ArticleService articleService = (ArticleServiceImpl) SpringContextUtil.getBean("articleServiceImpl");
            List<Article> beans = articleService.selectByOrgId(articleTemp.getOrg_id());

            //模板文件路径
            String temp_url = tempdirFile1.getPath() + "/html/model.html";
            //清空jsoup的转义表，会使jsoup失去转义能力
            Entities.EscapeMode.base.getMap().clear();
            byte[] htmlbytes = FileUtil.readFileByBytes(temp_url);
            String temp_html = new String(htmlbytes,3,htmlbytes.length-3);

            for(Article bean : beans){
                articleService.updateParseStatusByArticleId(bean.getArticle_id(), 3);//1:解析;2:未解析;3:解析中

                String article_url = cachedirFile2.getPath() + "/" + bean.getArticle_id() + ".html";
                article_url.replace("uploads","templates");
                Document articledoc = Jsoup.parse(temp_html);
                Element articleBody = articledoc.body();
                articleBody.text(bean.getArticle_content().toString());

                FileUtils.writeStringToFile(new File(article_url), articledoc.html());

                articleService.updateParseStatusByArticleId(bean.getArticle_id(), 1);//1:解析;2:未解析;3:解析中

                //类型(0:默认;1:图文;2:外链)
                if(bean.getArticle_type() == 0){
                    //TODO
                }
            }
            articleTemp.setTemp_status(1);
            articleTempService.updateArticleTemp(articleTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void runParseArticle(Article bean){
        try {
            articleService.updateParseStatusByArticleId(bean.getArticle_id(), 3);//1:解析;2:未解析;3:解析中
            String path = SpringContextUtil.getApplicationContext().getResource("").getFile().getPath();
            //机构已解析的模板
            List<ArticleTemp> articleTemps = articleTempService.selectZipByOrgId(bean.getOrg_id());
            //缓存根目录
            File cachedirFile = new File(path + "/cachehtmls");
            if(!cachedirFile.exists()) {
                cachedirFile.mkdirs();
            }
            for(ArticleTemp articleTemp : articleTemps){
                //缓存目录--机构
                File cachedirFile1 = new File(path + "/cachehtmls/" + articleTemp.getOrg_id());
                if(!cachedirFile1.exists()) {
                    cachedirFile1.mkdirs();
                }
                //缓存目录--机构--模板
                File cachedirFile2 = new File(path + "/cachehtmls/" + articleTemp.getOrg_id() +"/" +articleTemp.getArticle_temp_id());
                if(!cachedirFile2.exists()) {
                    cachedirFile2.mkdirs();
                }

                //模板文件路径
                String temp_url = path + "/templates/" + articleTemp.getArticle_temp_id() + "/html/model.html";
                //清空jsoup的转义表，会使jsoup失去转义能力
                Entities.EscapeMode.base.getMap().clear();
                byte[] htmlbytes = FileUtil.readFileByBytes(temp_url);
                String temp_html = new String(htmlbytes,3,htmlbytes.length-3);

                articleService.updateParseStatusByArticleId(bean.getArticle_id(), 3);//1:解析;2:未解析;3:解析中
                String article_url = cachedirFile2.getPath() + "/" + bean.getArticle_id() + ".html";
                article_url.replace("uploads","templates");
                Document articledoc = Jsoup.parse(temp_html);
                Element articleBody = articledoc.body();
                articleBody.text(bean.getArticle_content().toString());

                FileUtils.writeStringToFile(new File(article_url), articledoc.html());

                articleService.updateParseStatusByArticleId(bean.getArticle_id(), 1);//1:解析;2:未解析;3:解析中

                //类型(0:默认;1:图文;2:外链)
                if(bean.getArticle_type() == 0){
                    //TODO
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
