package com.xiaojinzi.component.cache;

import android.support.annotation.NonNull;

import com.xiaojinzi.component.Component;

public class DefaultCacheFactory implements Cache.Factory{

    private DefaultCacheFactory() {
    }

    public static final DefaultCacheFactory INSTANCE = new DefaultCacheFactory();

    @NonNull
    @Override
    public Cache build(CacheType type) {
        return new LruCache(type.calculateCacheSize(Component.getApplication()));
    }

}
