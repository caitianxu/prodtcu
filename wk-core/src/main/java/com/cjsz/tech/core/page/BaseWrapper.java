package com.cjsz.tech.core.page;

import java.util.Collection;
import java.util.List;


/**
 * 该类主要提供数据包装功能
 * @param <S>
 * @param <T>
 */
public interface BaseWrapper<S,T> {
  /**
   * 将对象列表转换为dto列表
   * @param objectList
   * @return
   */
  List<S> toDTOList(Collection<T> objectList);

  /**
   * 将对象转换为dto对象
   * @param object
   * @return
   */
  S toDTO(T object);
}
