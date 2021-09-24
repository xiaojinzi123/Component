package com.xiaojinzi.component.cache;

import androidx.annotation.NonNull;

import com.xiaojinzi.component.Component;

public class DefaultCacheFactory<K, V> implements Cache.Factory<K, V>{

    public static final DefaultCacheFactory<Object, Object> INSTANCE = new DefaultCacheFactory();

    @NonNull
    @Override
    public Cache<K, V> build(CacheType type) {
        return new LruCache<K, V>(type.calculateCacheSize(Component.getApplication()));
    }

}
