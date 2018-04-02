package com.cjsz.tech.system.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户角色关系
 * Created by Administrator on 2016/11/8 0008.
 */
@Entity
@Table(name = "sys_user_role_rel")
public class SysUserRoleRel {
    @Id
    private Long user_role_rel_id;
    
    private Long user_id;

    private Long role_id;

    private Integer enabled;

    private Date create_time;
    
    private Integer is_delete; //是否删除（1：是  2：否）
    
    private Date update_time;

    public Long getUser_role_rel_id() {
		return user_role_rel_id;
	}

	public void setUser_role_rel_id(Long user_role_rel_id) {
		this.user_role_rel_id = user_role_rel_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

	public Integer getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Integer is_delete) {
		this.is_delete = is_delete;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
}
