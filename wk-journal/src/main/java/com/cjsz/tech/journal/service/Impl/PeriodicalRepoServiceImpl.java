package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.ImagesBean;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.beans.FindPeriodicalBean;
import com.cjsz.tech.journal.domain.PeriodicalChild;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodical;
import com.cjsz.tech.journal.mapper.PeriodicalCatMapper;
import com.cjsz.tech.journal.mapper.PeriodicalChildMapper;
import com.cjsz.tech.journal.mapper.PeriodicalRepoMapper;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.utils.PdfToImageUtils;
import com.cjsz.tech.utils.images.ImageHelper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Service
public class PeriodicalRepoServiceImpl implements PeriodicalRepoService {

    @Autowired
    PeriodicalRepoMapper periodicalRepoMapper;

    @Autowired
    PeriodicalChildMapper periodicalChildMapper;

    @Autowired
    PeriodicalCatMapper periodicalCatMapper;

    static Date date = new Date();

    @Override
    public void uploadPeriodicals(List<FileForm> fileForms) {
        for (FileForm fileForm : fileForms) {
            if (!saveRepo(fileForm)) {
                continue;
            }
        }
    }

    @Override
    @Transactional
    public boolean saveRepo(FileForm fileForm) {
        PeriodicalRepo periodical = periodicalRepoMapper.findByFileName(fileForm.getName());
        if (null == periodical) {
            periodical = savePeriodicalRepo(fileForm);
        } else {
            if (periodical.getPeriodical_status() != 2) {
                periodical.setPeriodical_name(StringUtils.substringBeforeLast(fileForm.getName(), "."));
                periodical.setFile_name(fileForm.getName());
                periodical.setPeriodical_url(fileForm.getUrl());
                periodical.setPeriodical_status(1);
                periodicalRepoMapper.updateByPrimaryKey(periodical);
            } else {
                return false;
            }
        }
        List<PeriodicalChild> list = new ArrayList<>();
        List<ImagesBean> child_list = PdfToImageUtils.pdfToImage(fileForm);
        if (null != child_list && child_list.size() > 0) {
            periodicalChildMapper.deleteByPeriodicalId(periodical.getPeriodical_id());
            for (ImagesBean bean : child_list) {
                PeriodicalChild child = new PeriodicalChild();
                child.setPeriodical_id(periodical.getPeriodical_id());
                child.setImage_page(bean.getPage());
                child.setImage_url(bean.getImg());
                child.setImage_small_url(bean.getImg_small());
                child.setCreate_time(date);
                list.add(child);
            }
        } else {
            return false;
        }
        if (list.size() > 0) {
            periodicalRepoMapper.updateRepoStatusAndPages(2, list.size(), periodical.getPeriodical_id());
            periodicalChildMapper.insertList(list);
        }
        return true;
    }

    @Override
    public boolean saveRepoToImg(FileForm fileForm, UnPeriodical unper) {
        try {
            PeriodicalRepo periodical = periodicalRepoMapper.findByFileName(fileForm.getName());
            if (null == periodical) {
                periodical = savePeriodicalRepoNew(fileForm, unper);
            } else {
                periodical.setPeriodical_name(StringUtils.substringBeforeLast(fileForm.getName(), "."));
                periodical.setSeries_name(unper.getS_title());
                periodical.setFile_name(fileForm.getName());
                periodical.setPeriodical_url(fileForm.getUrl());
                periodicalRepoMapper.updateByPrimaryKey(periodical);
            }
            //查找是否有解压之后的文件
            String child_file_name = periodical.getChild_file_name();
            List<ImagesBean> list_sift = new ArrayList<>();
            if (child_file_name != null && child_file_name != "") {
                //根目录
                File rootFile = SpringContextUtil.getApplicationContext().getResource("").getFile();
                String dirPath = rootFile.getPath() + fileForm.getUrl().replace(fileForm.getName(), "") + "unfile/" + child_file_name;
                File dirFile = new File(dirPath);
                if (!dirFile.exists()) {
                    System.out.print("文件夹目录不存在：" + dirPath);
                    return false;
                }
                File[] img_list = dirFile.listFiles();
                if (img_list == null || img_list.length < 1) {
                    System.out.println(dirFile.getPath() + "======这个目录是空的!");
                    return false;
                }
                Integer total_page = periodical.getTotal_page();
                if (total_page == null || total_page == 0) {
                    System.out.println(child_file_name + "======" + periodical.getFile_name() + "======没有设置total_page数量!");
                    return false;
                }
                ImagesBean lastBean = null;
                for (int i = 1; i <= total_page; i++) {
                    String imgUrl = dirFile.getPath() + "/img" + i + ".jpg";
                    System.out.println("开始解析第" + i + "张图片:" + imgUrl);
                    File imgFile = new File(imgUrl);
                    if (!imgFile.exists()) {
                        System.out.println("图片丢失解析中断:" + imgUrl);
                        return false;
                    }
                    BufferedImage image = ImageIO.read(imgFile);
                    //判断是否需要拆分
                    if (image.getWidth() >= image.getHeight()) {
                        int rows = 1;
                        int cols = 2;
                        int chunks = rows * cols;
                        int chunkWidth = image.getWidth() / cols;
                        int chunkHeight = image.getHeight() / rows;
                        int count = 0;
                        BufferedImage imgs[] = new BufferedImage[chunks];
                        for (int x = 0; x < rows; x++) {
                            for (int y = 0; y < cols; y++) {
                                //设置小图的大小和类型
                                imgs[count] = new BufferedImage(chunkWidth,
                                        chunkHeight, image.getType());
                                //写入图像内容
                                Graphics2D gr = imgs[count++].createGraphics();
                                gr.drawImage(image, 0, 0,
                                        chunkWidth, chunkHeight,
                                        chunkWidth * y, chunkHeight * x,
                                        chunkWidth * y + chunkWidth,
                                        chunkHeight * x + chunkHeight, null);
                                gr.dispose();
                            }
                        }
                        // 输出小图
                        for (int ii = 0; ii < imgs.length; ii++) {
                            String imgName = "img" + i + "_sp" + (ii + 1) + ".jpg";
                            String srcPath = dirPath + "/" + imgName;
                            System.out.println(srcPath);
                            File imgfile = new File(srcPath);
                            //验证是否已经存在拆分过的图片
                            if (!imgfile.exists()) {
                                ImageIO.write(imgs[ii], "jpg", imgfile);
                                ImageHelper.getTransferImageByScale(srcPath, "small", 0.2); // 将图片的尺寸大小缩小一半
                            } else {
                                System.out.println("曾经已拆分：" + imgfile.getName());
                            }
                            ImagesBean bean = new ImagesBean();
                            bean.setPage(list_sift.size() + 1);
                            bean.setImg((dirPath + "/" + imgName).replace(rootFile.getPath(), "").replaceAll("\\\\", "/"));
                            bean.setImg_small((dirPath + "/img" + i + "_sp" + (ii + 1) + "_small.jpg").replace(rootFile.getPath(), "").replaceAll("\\\\", "/"));
                            if (i == 1 && ii == 0) {
                                System.out.println("封面只保留一半");
                                lastBean = bean;
                            } else {
                                list_sift.add(bean);
                            }
                        }
                    } else {
                        ImagesBean bean = new ImagesBean();
                        String imgUrlSmall = dirFile.getPath() + "/img" + i + "_small.jpg";
                        File imgFileSmall = new File(imgUrlSmall);
                        //验证是否已经存在压缩图
                        if (!imgFileSmall.exists()) {
                            ImageHelper.getTransferImageByScale(imgUrl, "small", 0.2);
                        }
                        bean.setPage(list_sift.size() + 1);
                        bean.setImg(imgFile.getPath().replace(rootFile.getPath(), "").replaceAll("\\\\", "/"));
                        bean.setImg_small(imgFileSmall.getPath().replace(rootFile.getPath(), "").replaceAll("\\\\", "/"));
                        list_sift.add(bean);
                    }
                }
                //判断最后是否需要 追加底面
                if (list_sift.size() % 2 != 0 && lastBean != null) {
                    System.out.println("补全期刊底面图");
                    list_sift.add(lastBean);
                }
            } else {
                System.out.println(periodical.getFile_name() + "=====缺少解压文件目录，转程序自动解析!=====");
                periodicalChildMapper.deleteByPeriodicalId(periodical.getPeriodical_id());
                List<PeriodicalChild> list = new ArrayList<>();
                list_sift = PdfToImageUtils.pdfToImage(fileForm);
                //字目录
                if (list_sift.size() > 0) {
                    ImagesBean beanItem = list_sift.get(0);
                    String img_url = StringUtils.substringBeforeLast(beanItem.getImg(), "/");
                    String[] mm = img_url.split("/");
                    child_file_name = mm[mm.length - 1];
                }
            }
            System.out.println("总页数:" + list_sift.size());
            //删除原有解析内容
            periodicalChildMapper.deleteByPeriodicalId(periodical.getPeriodical_id());
            List<PeriodicalChild> list = new ArrayList<>();
            for (ImagesBean beanItem : list_sift) {
                PeriodicalChild child = new PeriodicalChild();
                child.setPeriodical_id(periodical.getPeriodical_id());
                child.setImage_page(list.size() + 1);
                child.setImage_url(beanItem.getImg());
                child.setImage_small_url(beanItem.getImg_small());
                child.setCreate_time(new Date());
                list.add(child);
            }
            //期刊信息完善
            periodical.setPeriodical_cover(list.get(0).getImage_url());
            periodical.setPeriodical_cover_small(list.get(0).getImage_small_url());
            periodical.setPeriodical_status(2);
            periodical.setTotal_page(list.size());
            periodical.setChild_file_name(child_file_name);
            periodical.setUpdate_time(new Date());
            periodical.setOrder_weight(new Date().getTime());
            periodicalRepoMapper.updateByPrimaryKey(periodical);
            periodicalChildMapper.insertList(list);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean saveRepoNew(FileForm fileForm, UnPeriodical unper) {
        PeriodicalRepo periodical = periodicalRepoMapper.findByFileName(fileForm.getName());
        if (null == periodical) {
            periodical = savePeriodicalRepoNew(fileForm, unper);
        } else {
            periodical.setPeriodical_name(StringUtils.substringBeforeLast(fileForm.getName(), "."));
            periodical.setSeries_name(unper.getS_title());
            periodical.setFile_name(fileForm.getName());
            periodical.setPeriodical_url(fileForm.getUrl());
            periodical.setPeriodical_status(1);
            periodicalRepoMapper.updateByPrimaryKey(periodical);
        }
        List<PeriodicalChild> list = new ArrayList<>();
        List<ImagesBean> child_list = PdfToImageUtils.pdfToImage(fileForm);
        if (null != child_list && child_list.size() > 0) {
            periodicalChildMapper.deleteByPeriodicalId(periodical.getPeriodical_id());
            for (ImagesBean bean : child_list) {
                PeriodicalChild child = new PeriodicalChild();
                child.setPeriodical_id(periodical.getPeriodical_id());
                child.setImage_page(bean.getPage());
                child.setImage_url(bean.getImg());
                child.setImage_small_url(bean.getImg_small());
                child.setCreate_time(date);
                list.add(child);
            }
        } else {
            return false;
        }
        if (list.size() > 0) {
            //期刊信息完善
            periodical.setPeriodical_cover(list.get(0).getImage_url());
            periodical.setPeriodical_cover_small(list.get(0).getImage_small_url());
            periodical.setPeriodical_status(2);
            periodical.setTotal_page(list.size());
            periodicalRepoMapper.updateByPrimaryKey(periodical);
            periodicalChildMapper.insertList(list);
        }
        return true;
    }

    @Override
    public List<PeriodicalRepo> selectByCatIds(String catIds) {
        return periodicalRepoMapper.selectByCatIds(catIds);
    }

    @Override
    public PeriodicalRepo selectById(Long journal_id) {
        return periodicalRepoMapper.selectByPrimaryKey(journal_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊")
    public void updateRepo(PeriodicalRepo repo) {
        PeriodicalRepo periodical = periodicalRepoMapper.selectByPrimaryKey(repo.getPeriodical_id());
        BeanUtils.copyProperties(repo, periodical);
        repo.setUpdate_time(new Date());
        periodicalRepoMapper.updateByPrimaryKey(repo);
    }

    @Override
    public JsonResult updateStatus(PeriodicalRepo repo) {
        PeriodicalRepo art = periodicalRepoMapper.selectByPrimaryKey(repo.getPeriodical_id());
        if (null == art) {
            return JsonResult.getError(Constants.ACTION_ERROR);
        }
        Integer enabled = repo.getEnabled();
        if (null == enabled || null == art.getRecommend() || null == art.getHeadline()) {
            return JsonResult.getError(Constants.ACTION_ERROR);
        }
        //发布状态改变
        if (art.getEnabled().intValue() != enabled.intValue()) {
            if (enabled.intValue() == 2) {
                //修改为不发布
                art.setPublish_time(null);
                art.setHeadline(2);
                art.setRecommend(2);
            } else {
                //修改为发布
                art.setPublish_time(date);
            }
            art.setEnabled(enabled);
        } else {
            //如果当前发布状态为未发布，提示需要先发布
            if (enabled.intValue() == 2) {
                return JsonResult.getError(Constants.JOURNAL_NOT_PUBLISH);
            }
            //推荐
            if (art.getRecommend() != repo.getRecommend()) {
                art.setRecommend(repo.getRecommend());
            }
            //头条
            else if (art.getHeadline() != repo.getHeadline()) {
                //仅一条头条：更新该条期刊头条状态，将其他设为非头条
                if (repo.getHeadline() == 1) {
                    //不是推荐，则修改所有该分类下的为非
                    periodicalRepoMapper.changeNotHeadLineByCatId(repo.getPeriodical_cat_id());
                }
                art.setHeadline(repo.getHeadline());
            }
        }
        art.setUpdate_time(new Date());
        periodicalRepoMapper.updateByPrimaryKey(art);
        return JsonResult.getSuccess(Constants.ACTION_UPDATE);
    }

    @Override
    public Object pageQuery(FindPeriodicalBean bean) {
        String catPath = "";
        if (bean.getPeriodical_cat_id() == -1) {
            catPath = "-1";
        } else {
            List<String> catPaths = periodicalCatMapper.getFullPathsByCatIds(bean.getPeriodical_cat_id().toString());
            if (catPaths.size() > 0) {
                catPath = catPaths.get(0);
            }
        }
        List<PeriodicalRepo> result;
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        if (bean.getOrg_id().intValue() == 1) {
            result = periodicalRepoMapper.getAllPeriodicalList(catPath, bean.getSearchText());
        } else {
            result = periodicalRepoMapper.getOrgPeriodicalList(bean.getOrg_id(), catPath, bean.getSearchText());
        }
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public PeriodicalRepo findByFileName(String perioical_name) {
        return periodicalRepoMapper.findByFileName(perioical_name);
    }

    @Override
    public Integer hasOffLine(Long orgid, String time, Object... otherparam) {
        Integer checknum = periodicalRepoMapper.checkOffLineNum(orgid, time);
        if (checknum == null) {
            checknum = 0;
        }
        return checknum;
    }

    @Override
    public List<PeriodicalRepo> getOffLineNumList(Long orgid, String timev, Object... otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return periodicalRepoMapper.getOffLineNumList(orgid, timev, num, size);
    }

    @Override
    public List<PeriodicalChild> getImg(Long periodical_id) {
        return periodicalRepoMapper.getImg(periodical_id);
    }

    @Override
    public Long getCatId(Long periodical_id) {
        return periodicalRepoMapper.getCatId(periodical_id);
    }

    @Override
    public List<PeriodicalRepo> findSameCatperiodicals(Long periodical_id, Long org_id, Long periodical_cat_id, Integer limit) {
        return periodicalRepoMapper.findSameCatperiodicals(periodical_id, org_id, periodical_cat_id, limit);
    }

    //期刊系列列表（同系列取最新一期）
    @Override
    public Object getList(FindPeriodicalBean bean, Sort sort) {
        String catPath = "";
        if (bean.getPeriodical_cat_id() == -1) {
            catPath = "-1";
        } else {
            List<String> catPaths = periodicalCatMapper.getFullPathsByCatIds(bean.getPeriodical_cat_id().toString());
            if (catPaths.size() > 0) {
                catPath = catPaths.get(0);
            }
        }
        List<PeriodicalRepo> result;
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        result = periodicalRepoMapper.getPeriodicalList(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    //期刊系列列表（同系列取最新一期）
    @Override
    public Object webGetList(FindPeriodicalBean bean, Sort sort) {
        String catPath = "";
        List<String> catPaths = periodicalCatMapper.getFullPathsByCatIds(bean.getPeriodical_cat_id().toString());
        if (catPaths.size() > 0) {
            catPath = catPaths.get(0);
        }
        List<PeriodicalRepo> result;
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        result = periodicalRepoMapper.getWebPeriodicalList(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public List<PeriodicalRepo> getSuggestList(Long periodical_id, Long org_id) {
        String series_name = periodicalRepoMapper.selectSeriesNameById(periodical_id);
        return periodicalRepoMapper.getSuggestList(series_name, org_id);
    }

    @Override
    public List<PeriodicalRepo> getSameList(String series_name, Long org_id) {
        return periodicalRepoMapper.getSameList(series_name, org_id);
    }
    @Override
    public  List<PeriodicalRepo>  getSameBySNameAll(String serises_name){
        return periodicalRepoMapper.getSameBySNameAll(serises_name);
    }

    //分页查询期刊列表（前台）
    @Override
    public Object stiePageQuery(Sort sort, FindPeriodicalBean bean) {
        String catPath = "";
        if (bean.getPeriodical_cat_id() == -1) {
            catPath = "-1";
        } else {
            List<String> catPaths = periodicalCatMapper.getFullPathsByCatIds(bean.getPeriodical_cat_id().toString());
            if (catPaths.size() > 0) {
                catPath = catPaths.get(0);
            }
        }
        List<PeriodicalRepo> result;
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        if (bean.getOrg_id().intValue() == 1) {
            result = periodicalRepoMapper.getAllPeriodicalList(catPath, bean.getSearchText());
        } else {
            result = periodicalRepoMapper.getOrgPeriodicalList(bean.getOrg_id(), catPath, bean.getSearchText());
        }
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public PeriodicalRepo savePeriodicalRepo(FileForm fileForm) {
        PeriodicalRepo periodical = new PeriodicalRepo();
        periodical.setPeriodical_name(StringUtils.substringBeforeLast(fileForm.getName(), "."));
        periodical.setFile_name(fileForm.getName());
        periodical.setPeriodical_url(fileForm.getUrl());
        periodical.setEnabled(Constants.DISABLE);
        periodical.setHeadline(Constants.DISABLE);
        periodical.setRecommend(Constants.DISABLE);
        periodical.setPeriodical_status(1);//未解析
        periodical.setCreate_time(date);
        periodical.setUpdate_time(date);
        periodical.setUpload_time(date);
        periodicalRepoMapper.insert(periodical);
        return periodical;
    }

    @Override
    public PeriodicalRepo savePeriodicalRepoNew(FileForm fileForm, UnPeriodical unper) {
        PeriodicalRepo periodical = new PeriodicalRepo();
        periodical.setPeriodical_name(StringUtils.substringBeforeLast(fileForm.getName(), "."));
        periodical.setSeries_name(unper.getS_title());
        periodical.setFile_name(fileForm.getName());
        periodical.setPeriodical_url(fileForm.getUrl());
        periodical.setEnabled(Constants.DISABLE);
        periodical.setHeadline(Constants.DISABLE);
        periodical.setRecommend(Constants.DISABLE);
        periodical.setTotal_page(0);//数量
        periodical.setPeriodical_status(1);//未解析
        periodical.setCreate_time(date);
        periodical.setUpdate_time(date);
        periodical.setUpload_time(date);

        //查询是否有同系列期刊
        PeriodicalRepo ppold = periodicalRepoMapper.selectBySeriesName(unper.getS_title());
        if (ppold != null) {
            //设置分类
            periodical.setPeriodical_cat_id(ppold.getPeriodical_cat_id());
            periodical.setPeriodical_cat_name(ppold.getPeriodical_cat_name());
            periodical.setPeriodical_cat_path(ppold.getPeriodical_cat_path());
            //设置简介
            periodical.setPeriodical_remark(ppold.getPeriodical_remark());
        }

        periodicalRepoMapper.insert(periodical);
        return periodical;
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        return periodicalRepoMapper.getCountByOrgId(org_id);
    }

    @Override
    public void delectPeriodicalChild(Long periodical_id) {
        periodicalChildMapper.deleteByPeriodicalId(periodical_id);
    }

    @Override
    public void updateRepoStatusAndPages(int Status, int num, Long periodical_id) {
        periodicalRepoMapper.updateRepoStatusAndPages(Status, num, periodical_id);
    }

    @Override
    public void insertList(List<PeriodicalChild> list) {
        periodicalChildMapper.insertList(list);
    }

    @Override
    public void setPeriodicalAllToCat(Long periodical_cat_id, String serises_name) {
        periodicalRepoMapper.setPeriodicalAllToCat(periodical_cat_id, serises_name);
    }

    @Override
    public void setPeriodicalAllToRemark(String periodical_remark, String serises_name) {
        periodicalRepoMapper.setPeriodicalAllToRemark(periodical_remark, serises_name);
    }

    //修改期刊系列发布状态
    @Override
    public void updateEnabled(Integer enabled, String series_name) {
        PeriodicalRepo periodicalRepo = periodicalRepoMapper.selectBySeriesName(series_name);
        if (periodicalRepo.getEnabled().intValue() == 1) {
            periodicalRepoMapper.updateEnabledAndTime(enabled, series_name);
        } else {
            periodicalRepoMapper.updateEnabled(enabled, series_name);
        }
    }
}
