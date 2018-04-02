package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.BaseEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/11/9 0009.
 */
public class SysRoleResRelBean  extends BaseEntity {

    private Long role_id;

    private List<RelIds> rel_ids;

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public List<RelIds> getRel_ids() {
        return rel_ids;
    }

    public void setRel_ids(List<RelIds> rel_ids) {
        this.rel_ids = rel_ids;
    }

    public class RelIds{
        public Long res_id;
        public Integer data_type_id;
    }



}
