package com.cjsz.tech.dev.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.MessageBean;
import com.cjsz.tech.dev.domain.DeviceFeedback;

import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceFeedbackService {
	
	//新增
	void saveDeviceFeedback(DeviceFeedback device);
	
	//更新
	void updateDeviceFeedback(DeviceFeedback device);
	
	//通过id查询
	DeviceFeedback selectById(Long deviceId);
	
	//分页查询
	Object pageQuery(Sort sort, PageConditionBean pageCondition, Long org_id);

	//回复
	Object updateFeedback(DeviceFeedback device,Long dept_id);

	//意见反馈列表
	Object getList(Integer pageNum,Integer pageSize,Sort sort,Long member_id);

	//获取会员留言列表
    Object getMemberFeedBackList(PageConditionBean bean, Long member_id);

    Object getMemberMessageList(Sort sort, MessageBean pageCondition, Long org_id);

    //会员删除意见反馈
    void deleteMessage(Long device_feedback_id);
}
