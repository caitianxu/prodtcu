package com.cjsz.tech.book.service;


import com.cjsz.tech.book.domain.PkgOrgRel;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface PkgOrgRelService {

    /**
     * 机构使用的数据包Id
     * @param org_id
     * @return
     */
    public PkgOrgRel selectByOrgId(Long org_id);
}
