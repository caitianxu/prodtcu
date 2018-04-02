package com.cjsz.tech.dev.beans;

import java.io.Serializable;

/**
 * Created by shiaihua on 16/12/24.
 */
public class MemberCountBean implements Serializable{

    private Long member_id;
    
    private String nick_name;
    
    private Long count;

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
