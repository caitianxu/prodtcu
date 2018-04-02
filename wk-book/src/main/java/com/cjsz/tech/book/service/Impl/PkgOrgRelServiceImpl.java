package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.domain.PkgOrgRel;
import com.cjsz.tech.book.mapper.PkgOrgRelMapper;
import com.cjsz.tech.book.service.PkgOrgRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
public class PkgOrgRelServiceImpl implements PkgOrgRelService {

    @Autowired
    private PkgOrgRelMapper pkgOrgRelMapper;

    @Override
    public PkgOrgRel selectByOrgId(Long org_id) {
        return pkgOrgRelMapper.selectByOrgId(org_id);
    }
}
