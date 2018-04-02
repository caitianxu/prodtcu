package com.cjsz.tech.member.service;

import com.cjsz.tech.utils.JsonResult;

/**
 * Author:Jason
 * Date:2017/6/26
 */
public interface MemberLoginService {

    JsonResult otherLogin(String token_type, String other_type, String openid, String nick_name, String cover);
}
