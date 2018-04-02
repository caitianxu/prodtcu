package com.cjsz.tech.system.utils;

import com.waspring.wacache.ICache;
import com.waspring.wacache.ICacheFactory;
import com.waspring.wacache.factory.CacheFactoryImpl;

/**
 * CACHE工具类
 * Created by shiaihua on 17/4/18.
 */
public class CacheUtil {

    private static ICache cache  = null;

    static  {
        ICacheFactory cf = new CacheFactoryImpl();
        ///这里用于决定使用的缓存类型，目前有：OSCache\JCS\EHCache\Redis
        try {
            cache = cf.getCache(ICacheFactory.EHCache);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void set(String key,Object value) {
        cache.set(key, value);//写入
    }

    public static void set(String key,Object value,Integer expire) {
        cache.set(key, value,expire);///带超期的写入
    }

    public static Object get(String key) {
        return cache.get(key);
    }

    public static void del(String key) {
        cache.del(key);///清除缓存对象
    }
}
