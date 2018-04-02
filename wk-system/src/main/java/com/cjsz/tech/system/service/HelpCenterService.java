package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.DelHelpBean;
import com.cjsz.tech.system.domain.HelpCenter;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2017/8/7 0007.
 */
public interface HelpCenterService {
    //获取帮助中心分页列表
    Object getList(PageConditionBean bean,Sort sort);

    //新增帮助中心问题
    void saveHelp(HelpCenter bean);

    //保存问题回复
    void saveReply(HelpCenter bean);

    //删除帮助（多选）
    void deleteHelp(String ids);

    //根据id查询
    HelpCenter selectById(Long id);

    //根据id查询上一个问题
    HelpCenter selectLastById(Long id);

    //根据id查询下一个问题
    HelpCenter selectNextById(Long id);
}
