package com.ehi.component.cache;

import android.app.ActivityManager;
import android.content.Context;

import com.ehi.component.support.Utils;

/**
 * 构建 {@link Cache} 时,使用 {@link CacheType} 中声明的类型,来区分不同的模块
 * 从而为不同的模块构建不同的缓存策略
 */
public interface CacheType {
    int ROUTER_INTERCEPTOR_CACHE_TYPE_ID = 0;
    int CACHE_SERVICE_CACHE_TYPE_ID = 1;
    /**
     * 缓存 {@link com.ehi.component.impl.EHiRouterInterceptor} 的容器
     */
    CacheType ROUTER_INTERCEPTOR_CACHE = new CacheType() {
        private static final int MAX_SIZE = 25;

        @Override
        public int getCacheTypeId() {
            return ROUTER_INTERCEPTOR_CACHE_TYPE_ID;
        }

        @Override
        public int calculateCacheSize(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int targetMemoryCacheSize;
            if (Utils.isLowMemoryDevice(activityManager)) {
                targetMemoryCacheSize = activityManager.getMemoryClass() / 6;
            } else {
                targetMemoryCacheSize = activityManager.getMemoryClass() / 4;
            }
            if (targetMemoryCacheSize > MAX_SIZE) {
                return MAX_SIZE;
            }
            return targetMemoryCacheSize;
        }
    };


    /**
     * 返回框架内需要缓存的模块对应的 {@code id}
     *
     * @return
     */
    int getCacheTypeId();

    /**
     * 计算对应模块需要的缓存大小
     *
     * @return
     */
    int calculateCacheSize(Context context);
}
