package com.cjsz.tech.journal.ctrl;


import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.journal.domain.UnPeriodical;
import com.cjsz.tech.journal.domain.PdfNotAnalysis;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodicalErr;
import com.cjsz.tech.journal.mapper.PdfNotAnalysisMapper;
import com.cjsz.tech.journal.mapper.PeriodicalChildMapper;
import com.cjsz.tech.journal.mapper.PeriodicalRepoMapper;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.journal.service.UnPeriodicalErrService;
import com.cjsz.tech.journal.service.UnPeriodicalService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * 期刊解析
 * Created by henry on 2017/08/08 0022.
 */
@Controller
public class UnPeriodicalController {

    @Autowired
    UnPeriodicalService unPeriodicalService;

    @Autowired
    UserService userService;

    @Autowired
    PeriodicalRepoService periodicalRepoService;

    @Autowired
    PdfNotAnalysisMapper pdfNotAnalysisMapper;

    @Autowired
    UnPeriodicalErrService unPeriodicalErrService;

    /**
     * 获取列表
     *
     * @param request
     * @param ben
     * @return
     */
    @RequestMapping(value = "/admin/unPeriodical/dataList", method = {RequestMethod.POST})
    @ResponseBody
    public Object unPeriodical_data_list(HttpServletRequest request, @RequestBody PageConditionBean ben) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            if (ben.getPageNum() == null || ben.getPageSize() == null) {
                return JsonResult.getException("参数错误!");
            }
            Object obj = unPeriodicalService.getUnPeriodicalAll(ben, sort);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(obj);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 创建解析任务
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/unPeriodical/save", method = {RequestMethod.POST})
    @ResponseBody
    public Object unPeriodical_add(HttpServletRequest request, @RequestBody UnPeriodical bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            bean.setUser_id(user.getUser_id());
            if (bean.getSid() == null) {
                unPeriodicalService.insertUnPeriodical(bean);
            } else if (bean.getS_status() == null || bean.getS_status() == 0 || bean.getS_status() == 3) {
                unPeriodicalService.saveUnPeriodical(bean);
            } else {
                return JsonResult.getException("该任务当前状态无法修改!");
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
            jsonResult.setData(bean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 开始解析
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/unPeriodical/unPDF", method = {RequestMethod.POST})
    @ResponseBody
    public Object unPeriodical_data_start(HttpServletRequest request, @RequestBody UnPeriodical bean) {
        try {
            if (bean.getSid() == null) {
                return JsonResult.getError("参数异常!");
            }
            if (bean.getS_path() == null) {
                return JsonResult.getError("解析路径异常!");
            }
            if (bean.getS_status() == 1) {
                return JsonResult.getError("解析中!请勿重复操作");
            }
            if (runPdfJob.containsKey(bean.getSid())) {
                return JsonResult.getError("解析中!请勿重复操作");
            }
            if (runPdfJob.size() >= 1) {
                return JsonResult.getError("服务器线程已满， 请等待其他解析任务完成后在继续执行!");
            }
            //立即修改状态
            bean.setS_status(1);
            unPeriodicalService.saveUnPeriodical(bean);
            //开启线程
            new Thread(new SplitPDFJob(bean)).start();

            JsonResult jsonResult = JsonResult.getSuccess("文件已开始解析!");
            jsonResult.setData(bean);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    Map runPdfJob = new HashMap<>();

    class SplitPDFJob implements Runnable {
        private UnPeriodical unPeriodical;

        /**
         * 期刊
         *
         * @param unPeriodical
         */
        public SplitPDFJob(UnPeriodical unPeriodical) {
            this.unPeriodical = unPeriodical;
        }

        /**
         * 线程错误
         *
         * @param message
         */
        public void errEnd(String message) {
            System.out.println(message);
            unPeriodical.setS_status(3);
            unPeriodicalService.saveUnPeriodical(unPeriodical);
            runPdfJob.remove(unPeriodical.getSid());
        }

        /**
         * 期刊错误
         *
         * @param message
         * @param file_name
         */
        public void addUnErr(String message, String file_name, String periodical_url) {
            unPeriodicalErrService.delUnPeriodicalErr(file_name);
            UnPeriodicalErr err = new UnPeriodicalErr();
            err.setPeriodical_id(unPeriodical.getSid());
            err.setPeriodical_url(periodical_url);
            err.setFile_name(file_name);
            err.setCreate_time(new Date());
            err.setMessage(message);
            unPeriodicalErrService.insertUnPeriodicalErr(err);
        }

        /**
         * 解析
         *
         * @param unPathDir 解压路径
         * @param pdf
         * @param perRepo
         * @return
         */
        public boolean unPdf(String unPathDir, File pdf, PeriodicalRepo perRepo) throws IOException {
            String perioical_name = pdf.getName();
            if (!pdf.isDirectory() && pdf.getPath().endsWith(".pdf")) {
                FileForm fileForm = new FileForm();
                fileForm.setName(perioical_name);
                fileForm.setUrl(unPathDir + "/" + perioical_name);
//                if (!periodicalRepoService.saveRepoNew(fileForm, unPeriodical)) {
                if (!periodicalRepoService.saveRepoToImg(fileForm, unPeriodical)) {
                    System.out.println("===========解析失败：" + perioical_name + "============");
                    addUnErr("解析失败", perioical_name, pdf.getPath());
                    return false;
                }
                else{
                    System.out.println("===========解析成功：" + perioical_name + "============");
                }
            } else {
                addUnErr("不是pdf文件", perioical_name, pdf.getPath());
                return false;
            }
            return true;
        }

        public void run() {
            if (runPdfJob.containsKey(unPeriodical.getSid())) {
                return;
            }
            runPdfJob.put(unPeriodical.getSid(), "run");
            System.out.println("开启线程：" + unPeriodical.getSid());
            try {
                File file = SpringContextUtil.getApplicationContext().getResource("").getFile();
                String rootName = file.getAbsolutePath() + "/szyun/journals/" + unPeriodical.getS_path();
                System.out.println(rootName);
                File pdfsDir = new File(rootName);
                //目录不存在
                if (!pdfsDir.exists()) {
                    errEnd("解析路径不存在!");
                    return;
                }
                File[] pdf_list = pdfsDir.listFiles();
                if (pdf_list == null || pdf_list.length < 1) {
                    errEnd("这个目录是空的!");
                    return;
                }
                List<File> pdfList = new ArrayList<>();
                for (File pdffile : pdf_list) {
                    if (pdffile.isDirectory()) {
                        System.out.println("不是pdf文件:" + pdffile.getName());
                    } else if (pdffile.getAbsolutePath().endsWith(".pdf")) {
                        pdfList.add(pdffile);
                    } else {
                        System.out.println("不是pdf文件:" + pdffile.getName());
                    }
                }
                System.out.println("总文件数量:" + pdf_list.length + "/pdf文件数量:" + pdfList.size());
                unPeriodical.setS_num(pdfList.size());
                unPeriodical.setS_status(1);
                unPeriodicalService.saveUnPeriodical(unPeriodical);

                //解压目录
                String unPath = file.getAbsolutePath() + "/szyun/journals/" + unPeriodical.getS_path() + "/unfile";
                File unPathDir = new File(unPath);
                if (!unPathDir.exists()) {
                    unPathDir.mkdirs();
                }
                int success_num = 0;
                int error_unm = 0;
                int for_index = 1;
                //开始循环解压
                for (File pdffile : pdfList) {
                    String pdffile_name = pdffile.getName();
                    System.out.println("=====================开始进程《" + unPeriodical.getS_title() + "》解析第" + (for_index) + "/" + pdfList.size() + "本:" + pdffile_name + "=====================");
                    //查找仓库期刊文件
                    PeriodicalRepo perRepo = unPeriodicalService.getPeriodicalByFileName(pdffile_name);
                    if (perRepo != null && perRepo.getPeriodical_status() == 2) {
                        //判断是否解析成功
                        System.out.println("=====================曾经解析成功！" + pdffile_name);
                        success_num += 1;
                    } else {
                        String perPath = "/szyun/journals/" + unPeriodical.getS_path();
                        if (unPdf(perPath, pdffile, perRepo)) {
                            success_num += 1;
                            //删除以前的错误记录
                            unPeriodicalErrService.delUnPeriodicalErr(pdffile_name);
                        } else {
                            error_unm += 1;
                        }
                    }
                    for_index++;
                    unPeriodical.setUn_success(success_num);
                    unPeriodical.setUn_error(error_unm);
                    unPeriodicalService.saveUnPeriodical(unPeriodical);
                }
                unPeriodical.setUn_success(success_num);
                unPeriodical.setUn_error(error_unm);
                unPeriodical.setS_status(2);
                unPeriodical.setEnd_time(new Date());
                unPeriodicalService.saveUnPeriodical(unPeriodical);
                System.out.println("结束线程：" + unPeriodical.getSid());
                runPdfJob.remove(unPeriodical.getSid());
            } catch (Exception e) {
                errEnd(e.getMessage());
                System.out.println("结束线程：" + unPeriodical.getSid());
                runPdfJob.remove(unPeriodical.getSid());
                e.printStackTrace();
            } finally {
                System.gc();
            }
        }
    }
}
