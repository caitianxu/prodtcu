package com.cjsz.tech.meb.ctrl;

import com.cjsz.tech.meb.service.MemberGradePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 会员等级积分记录
 * Created by Administrator on 2017/3/15 0015.
 */
@Controller
public class MemberGradePointController {

    @Autowired
    private MemberGradePointService memberGradePointService;



}
