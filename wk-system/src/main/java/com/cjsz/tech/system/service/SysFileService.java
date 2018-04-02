package com.cjsz.tech.system.service;

import com.cjsz.tech.system.conditions.PageAndSortCondition;
import com.cjsz.tech.system.domain.SysFile;

/**
 * Author:Jason
 * Date:2016/11/28
 */
public interface SysFileService {

    /**
     * 保存文件
     * @param file
     * @return
     */
  void addFile(SysFile file);


  /**
   * 更新文件
   * @param file
   * @return
     */
  int update(SysFile file);

    /**
     * 根据id获取对应的文件记录
     * @param id
     * @return
     */

  SysFile findById(Long id);

    /**
     * 删除文件
     * @param ids
     */
  void delFiles(String[] ids, String realBasePath);


    /**
     * 文件重命名
     * @param id 文件id
     * @param newFileName 新文件名
     */
  void renameFile(Long id, String newFileName);

  /**
   * 分页模糊搜索（排序）
   * @param pageCondition
   * @param root_org_id
   * @return
     */
    Object pageQuery(PageAndSortCondition pageCondition, Long root_org_id);


  /**
   * 分页查询某个目录文件下面的直接子级文件
   * @param dirId
   * @param root_org_id
   * @return
   */
  Object pageQueryOfDir(PageAndSortCondition condition, Long dirId, Long root_org_id);
}
