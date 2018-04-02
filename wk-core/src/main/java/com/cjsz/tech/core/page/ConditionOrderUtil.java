package com.cjsz.tech.core.page;

import org.springframework.data.domain.Sort;

/**
 * Created by yunke on 16/3/5.
 */
public class ConditionOrderUtil {


    public static  String prepareOrder(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return null;
        }
        return sort.toString().replace(":", " ");
    }
}
