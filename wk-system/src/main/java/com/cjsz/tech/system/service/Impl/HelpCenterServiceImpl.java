package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.domain.HelpCenter;
import com.cjsz.tech.system.mapper.HelpCenterMapper;
import com.cjsz.tech.system.service.HelpCenterService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7 0007.
 */
@Service
public class HelpCenterServiceImpl implements HelpCenterService {

    @Autowired
    private HelpCenterMapper helpCenterMapper;

    @Override
    public Object getList(PageConditionBean bean,Sort sort){
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<HelpCenter> result = helpCenterMapper.getList(bean.getSearchText());
        for (HelpCenter helpCenter:result){
            if (helpCenter != null){
                String create_name = helpCenterMapper.selectNameById(helpCenter.getCreate_user());
                String reply_name = helpCenterMapper.selectNameById(helpCenter.getReply_user());
                helpCenter.setCreate_name(create_name);
                helpCenter.setReply_name(reply_name);
            }
        }
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public void saveHelp(HelpCenter bean){
        bean.setCreate_time(new Date());
        helpCenterMapper.insert(bean);
    }

    @Override
    public void saveReply(HelpCenter bean){
        HelpCenter helpCenter = helpCenterMapper.selectById(bean.getId());
        helpCenter.setReply_user(bean.getReply_user());
        helpCenter.setReply_content(bean.getReply_content());
        helpCenter.setReply_time(new Date());
        helpCenterMapper.updateByPrimaryKey(helpCenter);
    }

    @Override
    public void deleteHelp(String ids){
        helpCenterMapper.deleteHelp(ids);
    }


    @Override
    public HelpCenter selectById(Long id){
        return helpCenterMapper.selectById(id);
    }

    @Override
    public HelpCenter selectLastById(Long id){
        return helpCenterMapper.selectLastById(id);
    }

    @Override
    public HelpCenter selectNextById(Long id){
        return helpCenterMapper.selectNextById(id);
    }
}
