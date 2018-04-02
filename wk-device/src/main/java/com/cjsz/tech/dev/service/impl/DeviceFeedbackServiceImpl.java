package com.cjsz.tech.dev.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.dev.beans.DeviceFeedbackBean;
import com.cjsz.tech.dev.beans.MessageBean;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.dev.domain.DeviceFeedback;
import com.cjsz.tech.dev.mapper.DeviceFeedbackMapper;
import com.cjsz.tech.dev.service.DeviceFeedbackService;
import com.github.pagehelper.PageHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class DeviceFeedbackServiceImpl implements DeviceFeedbackService{
	
    @Autowired
    private DeviceFeedbackMapper deviceFeedbackMapper;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "用户反馈")
	public void saveDeviceFeedback(DeviceFeedback device) {
    	device.setReply_status(Constants.REPLY_STATUS_NO);
		device.setSend_time(new Date());
		deviceFeedbackMapper.insert(device);
	}

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户反馈")
	public void updateDeviceFeedback(DeviceFeedback device) {
    	device.setReply_status(Constants.REPLY_STATUS_YES);
		device.setReply_time(new Date());
		deviceFeedbackMapper.updateByPrimaryKey(device);		
	}

	@Override
	public Object pageQuery(Sort sort, PageConditionBean pageCondition, Long org_id) {
		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<DeviceFeedback> result = deviceFeedbackMapper.getList(pageCondition.getSearchText(), org_id);
  		PageList pageList = new PageList(result, null);
  		return pageList;
	}

	@Override
	public DeviceFeedback selectById(Long deviceId) {
		return deviceFeedbackMapper.selectByPrimaryKey(deviceId);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户反馈")
	public Object updateFeedback(DeviceFeedback device,Long dept_id) {
		DeviceFeedback back = selectById(device.getDevice_feedback_id());
		if(back == null){
			return JsonResult.getError(Constants.FEEDBACK_NOT_EXIST);
		}
		if (back.getOrg_id().intValue() == dept_id.intValue() || ( back.getDept_id() !=null && dept_id.intValue() == back.getDept_id().intValue() ) ){
            back.setReply(device.getReply());
            updateDeviceFeedback(back);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(back);
            return result;
        }else {
            return JsonResult.getError("无权限");
        }

	}

	@Override
	public Object getList(Integer pageNum,Integer pageSize,Sort sort,Long member_id){
    	PageHelper.startPage(pageNum,pageSize);
    	String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<DeviceFeedback> result = deviceFeedbackMapper.getMemberList(member_id);
		PageList pageList = new PageList(result, null);
		return pageList;
	}

    @Override
    public Object getMemberFeedBackList(PageConditionBean bean, Long member_id) {
		PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
		List<DeviceFeedbackBean> result = deviceFeedbackMapper.getMemberMessageList(member_id);
		PageList pageList = new PageList(result, null);
		return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "留言咨询")
    public Object getMemberMessageList(Sort sort, MessageBean pageCondition, Long org_id) {
        List<DeviceFeedback> result = new ArrayList<>();
        PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
		result = deviceFeedbackMapper.getListByOrg(pageCondition, org_id);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public void deleteMessage(Long device_feedback_id) {
        deviceFeedbackMapper.deleteByPrimaryKey(device_feedback_id);
    }
}
