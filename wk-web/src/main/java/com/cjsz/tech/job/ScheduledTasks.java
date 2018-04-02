package com.cjsz.tech.job;

import com.cjsz.tech.book.domain.*;
import com.cjsz.tech.book.mapper.BookNotAnalysisMapper;
import com.cjsz.tech.book.mapper.BookRepoMapper;
import com.cjsz.tech.book.service.*;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.count.service.SearchCountService;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.journal.mapper.PdfNotAnalysisMapper;
import com.cjsz.tech.journal.mapper.PeriodicalChildMapper;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.meb.service.MemberReadIndexService;
import com.cjsz.tech.system.service.MailSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Component
@Configurable
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private BookRepoMapper bookRepoMapper;

    @Autowired
    private BookDeviceRelService bookDeviceRelService;

    @Autowired
    private PkgBookRelService pkgBookRelService;

    @Autowired
    private PkgOrgRelService pkgOrgRelService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MemberReadIndexService memberReadIndexService;

    @Autowired
    private BookIndexService bookIndexService;

    @Autowired
    PeriodicalRepoService periodicalRepoService;

    @Autowired
    PeriodicalChildMapper periodicalChildMapper;

    @Autowired
    BookNotAnalysisMapper bookNotAnalysisMapper;

    @Autowired
    PdfNotAnalysisMapper pdfNotAnalysisMapper;

    @Autowired
    RecommendBooksService recommendBooksService;

    @Autowired
    SearchCountService searchCountService;

    @Autowired
    BookRepoService bookRepoService;


    //每天0点执行一次，到期图书下架
    // @Scheduled(cron = "0 0 0 * * ? ")
    public void bookEndTime() {
        bookRepoMapper.doBookEndTime();
    }

    //每天0点执行一次，取长江中文网的所有网文基本信息
    @Scheduled(cron = "0 0 0 * * ? ")
    public void saveBookByCJZWW() {
        Date date = getNextDay(new Date());
        Long time = date.getTime() / 1000;
        recommendBooksService.saveBookByCJZWW(time);
    }

    //获取当前时间前一天时间
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }


    //每天0点执行一次，取长江中文网前100条热搜
    @Scheduled(cron = "0 0 0 * * ? ")
    public void getSearchKeyByCJZWW() {
        searchCountService.saveKeyByCJZWW();
    }

    //每10分钟执行一次，新增设备后的数据变化(图书离线关系)
    @Scheduled(cron = "0 0/10 *  * * * ")
    public void addDevice() {
        try {
            //所有设备
            List<Device> devices = deviceService.findAll();

            //所有图书设备关系的设备Id(不重复)
            List<Long> deviceIdList = bookDeviceRelService.getAllDeviceIds();
            List<BookDeviceRel> deviceRelList = new ArrayList<BookDeviceRel>();
            for (Device device : devices) {
                if (!deviceIdList.contains(device.getDevice_id())) {
                    //设备的机构
                    Long org_id = device.getOrg_id();
                    //找到机构使用的数据包
                    PkgOrgRel pkgOrgRel = pkgOrgRelService.selectByOrgId(org_id);
                    if (pkgOrgRel != null) {
                        List<PkgBookRel> pkgBookRels = pkgBookRelService.findByPkgId(pkgOrgRel.getPkg_id());
                        for (PkgBookRel pkgBookRel : pkgBookRels) {
                            BookDeviceRel bookDeviceRel = new BookDeviceRel(device.getDevice_id(), pkgBookRel.getBook_id(), pkgBookRel.getBook_cat_id(),
                                    org_id, new Date(), pkgBookRel.getOffline_status(), new Date());
                            deviceRelList.add(bookDeviceRel);
                        }
                    }
                }
            }
            if (deviceRelList.size() > 0) {
                bookDeviceRelService.saveBookDeviceRels(deviceRelList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //每天0点执行一次，用户阅读指数表更新,图书指数记录表更新,图书指数表更新
    @Scheduled(cron = "0 0 0 * * ? ")
    public void UpdateMemberReadIndexData() {
        //用户阅读指数表更新
        memberReadIndexService.addReadIndex();
        //图书指数表更新（和图书机构关系表更新）
        bookIndexService.addBookIndex();
    }

    //重置邮箱发送次数
    @Scheduled(cron = "0 0 0 * * ?")
    public void syncRestEamil() {
        MailSettingService mailSettingService = (MailSettingService) SpringContextUtil.getBean("mailSettingServiceImpl");
        mailSettingService.restSendTimes();
    }

    private SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    // 补全已上架图书索引
    //@Scheduled(cron = "0 0 2 5 12 ? ")
    public void createBookIndex() {
        List<Long> bookIds = bookRepoMapper.getBookIdsByStatus(1);
        for (Long bookId : bookIds) {
            if (!bookRepoService.autoIndexForEpubByDBChapter(bookId)) {
                System.out.println("==============图书ID:" + bookId + "生成索引失败===============");
            }
        }
    }

}
