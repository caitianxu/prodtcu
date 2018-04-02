package com.cjsz.tech.system.service;

import java.util.Date;
import java.util.List;

/**
 * 离线同步接口
 * Created by shiaihua on 16/12/22.
 */
public interface IOfflineService<T> {

    /**
     * 返回更新列表
     * @param oldtimestamp
     * @return
     */
    public List<T> getOffLineNumList(Long orgid, String oldtimestamp, Object... otherparam);

    /**
     * 检查是否存在离线数据
     * @param oldtimestamp
     * @return
     */
    public Integer hasOffLine(Long orgid, String oldtimestamp, Object... otherparam);
}
