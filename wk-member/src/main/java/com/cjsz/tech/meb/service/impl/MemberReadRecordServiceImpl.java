package com.cjsz.tech.meb.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.book.domain.BookIndex;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.beans.ReadingRecord;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.meb.mapper.MemberReadRecordMapper;
import com.cjsz.tech.meb.service.MemberReadRecordService;
import com.cjsz.tech.system.beans.SearchBean;
import com.cjsz.tech.utils.DateUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;

/**
 * Created by LuoLi on 2017/4/13 0013.
 */
@Service
public class MemberReadRecordServiceImpl implements MemberReadRecordService {

    @Autowired
    private MemberReadRecordMapper memberReadRecordMapper;
    @Autowired
    private BookIndexRecordService bookIndexRecordService;

    @Override
    public void updateRecord(Long org_id, MemberReadRecord memberReadRecord, String device_type) {

        //开始读书，或更换章节时
        //获取当日最新的记录（member_id、book_id），没有：添加；有：30min以内更新，否则添加
        //注意：30秒以内不增加
        MemberReadRecord record = memberReadRecordMapper.selectCurDayNearestRecord(memberReadRecord.getMember_id(), memberReadRecord.getBook_id(), device_type);

        boolean flag = true;
        if(record != null){
            String end_time = DateUtils.getSecond(record.getEnd_time(), 30);
            String cur_time = DateUtils.getSecond(new Date(), 0);
            //30秒
            if(end_time.compareTo(cur_time) > 0){
                return;
            }
            //30分钟
            end_time = DateUtils.getSecond(record.getEnd_time(), 30 * 60);
            if(end_time.compareTo(cur_time) <= 0){
                flag = false;
            }
            //阅读图书，图书指数记录，超过30s换章节
            //record_type(1:点击详情;2:收藏;3:分享;4:评论;5:阅读);
            BookIndexRecord bookIndexRecord = new BookIndexRecord(org_id, memberReadRecord.getBook_id(),2, memberReadRecord.getMember_id(), 5, new Date(),device_type);
            bookIndexRecordService.addRecord(bookIndexRecord);
        }
        if(record != null && flag){
            record.setChapter_name(memberReadRecord.getChapter_name());
            record.setBook_cover(memberReadRecord.getBook_cover());
            record.setBook_name(memberReadRecord.getBook_name());
            record.setLast_chapter_id(memberReadRecord.getLast_chapter_id());
            record.setEnd_time(new Date());
            Long min = (DateUtils.getMinuteTime(new Date(), 0).getTime() - DateUtils.getMinuteTime(record.getBegin_time(), 0).getTime())/1000/60;
            if (min.longValue() < 1){
                record.setTotal_time(1L);
            }else {
                record.setTotal_time(min);
            }
            record.setTotal_chapter(record.getTotal_chapter() + 1L);
            record.setPlan(memberReadRecord.getPlan());
            record.setIs_delete(2);
            memberReadRecordMapper.updateByPrimaryKey(record);
        }else{
            memberReadRecord.setDevice_type_code(device_type);
            memberReadRecord.setTotal_time(1L);
            memberReadRecord.setTotal_chapter(1L);
            memberReadRecord.setBegin_time(new Date());
            memberReadRecord.setEnd_time(new Date());
            memberReadRecord.setIs_delete(2);
            memberReadRecordMapper.insert(memberReadRecord);
        }

    }

    @Override
    public Integer getTodayReadTime(Long member_id) {
        return memberReadRecordMapper.getTodayReadTime(member_id);
    }

    @Override
    public Integer getReadBookCount(Long member_id) {
        return memberReadRecordMapper.getReadBookCount(member_id);
    }

    @Override
    public Object pageQuery(SearchBean bean ,Long org_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        List<MemberReadRecord> list = memberReadRecordMapper.getList(bean,org_id);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public void updateRecord_v2(Long org_id, MemberReadRecord memberReadRecord ,String token_type) {
        //开始读书，或更换章节时
        //获取当日最新的记录（member_id、book_id），没有：添加；有：30min以内更新，否则添加
        //注意：30秒以内不增加
        MemberReadRecord readRecord = memberReadRecordMapper.selectCurDayNearestRecord_v2(memberReadRecord.getMember_id(),memberReadRecord.getBook_id());
        boolean flag = true;
        if (readRecord !=null){
            String end_time = DateUtils.getSecond(readRecord.getBegin_time(),30);
            String cur_time = DateUtils.getSecond(new Date(),0);
            //30秒
            if (end_time.compareTo(cur_time)>0){
                return;
            }
            //30分钟
            end_time = DateUtils.getSecond(readRecord.getEnd_time(),30*60);
            if (end_time.compareTo(cur_time)<=0){
                flag=false;
            }
        }
        if(readRecord != null && flag){
            readRecord.setChapter_name(memberReadRecord.getChapter_name());
            readRecord.setBook_cover(memberReadRecord.getBook_cover());
            readRecord.setBook_name(memberReadRecord.getBook_name());
            readRecord.setLast_chapter_id(memberReadRecord.getLast_chapter_id());
            readRecord.setEnd_time(new Date());
            Long min = (DateUtils.getMinuteTime(new Date(), 0).getTime() - DateUtils.getMinuteTime(readRecord.getBegin_time(), 0).getTime())/1000/60;
            if (min.longValue() < 1){
                readRecord.setTotal_time(1L);
            }else {
                readRecord.setTotal_time(min);
            }
            readRecord.setTotal_chapter(readRecord.getTotal_chapter() + 1L);
            readRecord.setPlan(memberReadRecord.getPlan());
            readRecord.setIs_delete(2);
            memberReadRecordMapper.updateByPrimaryKey(readRecord);
        }else{
            memberReadRecord.setTotal_time(1L);
            memberReadRecord.setTotal_chapter(1L);
            memberReadRecord.setBegin_time(new Date());
            memberReadRecord.setEnd_time(new Date());
            memberReadRecord.setDevice_type_code(token_type);
            memberReadRecord.setIs_delete(2);
            memberReadRecordMapper.insert(memberReadRecord);
        }
    }

    @Override
    public Object queryList(Sort sort, PageConditionBean bean, Long member_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<ReadingRecord> result= memberReadRecordMapper.getMemberReadListV2(member_id);
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public void deleteReadRecord_v2(Long member_id) {
        memberReadRecordMapper.deleteReadRecord_v2(member_id);
    }

    //获取图书点击详情统计记录
    @Override
    public Object clickList(Sort sort , SearchBean bean,Long org_id){
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<BookIndex> list = memberReadRecordMapper.clickList(bean,org_id);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public Object getMemberReadList(Sort sort, PageConditionBean bean, Long member_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
//        String order = ConditionOrderUtil.prepareOrder(sort);
//        if(order != null){
//            PageHelper.orderBy(order);
//        }
        List<ReadingRecord> result= memberReadRecordMapper.getMemberReadList(member_id);
        PageList pageList = new PageList(result,null);
        return pageList;
    }
}
