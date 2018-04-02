package com.cjsz.tech.system.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/11/29 0029.
 */
public class ListUtils {

    /**
     * 求ls对ls2的差集,即ls中有，但ls2中没有的
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static List diff(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }

    /**
     * 求2个集合的交集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static List intersect(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.retainAll(ls2);
        return list;
    }

    /**
     * 求2个集合的并集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static List union(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);//将ls的值拷贝一份到list中
        list.removeAll(ls2);
        list.addAll(ls2);
        return list;
    }

}
