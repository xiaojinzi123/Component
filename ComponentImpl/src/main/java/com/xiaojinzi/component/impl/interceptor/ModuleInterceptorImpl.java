package com.xiaojinzi.component.impl.interceptor;

import android.app.Application;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.interceptor.IComponentHostInterceptor;
import com.xiaojinzi.component.support.RouterInterceptorCache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 拦截器的代码生成类的基本实现
 * time   : 2018/12/26
 *
 * @author : xiaojinzi
 */
abstract class ModuleInterceptorImpl implements IComponentHostInterceptor {

    protected Map<String, Class<? extends RouterInterceptor>> interceptorMap = new HashMap<>();

    private boolean isInitMap = false;

    /**
     * 用作销毁一些缓存
     *
     * @param app {@link Application}
     */
    @Override
    public void onCreate(@NonNull Application app) {
        // empty
    }

    /**
     * 用作销毁一些缓存
     */
    @Override
    public void onDestroy() {
        // empty
    }

    @Override
    @NonNull
    @UiThread
    public List<InterceptorBean> globalInterceptorList() {
        return Collections.emptyList();
    }

    /**
     * 初始化拦截器的集合
     */
    @CallSuper
    @UiThread
    protected void initInterceptorMap() {
        isInitMap = true;
    }

    @NonNull
    @Override
    public Set<String> getInterceptorNames() {
        if (!isInitMap) {
            initInterceptorMap();
        }
        return interceptorMap.keySet();
    }

    @NonNull
    @Override
    public Map<String, Class<? extends RouterInterceptor>> getInterceptorMap() {
        if (!isInitMap) {
            initInterceptorMap();
        }
        return new HashMap<>(interceptorMap);
    }

    @Override
    @Nullable
    @UiThread
    public RouterInterceptor getByName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        if (!isInitMap) {
            initInterceptorMap();
        }
        Class<? extends RouterInterceptor> interceptorClass = interceptorMap.get(name);
        if (interceptorClass == null) {
            return null;
        }
        return RouterInterceptorCache.getInterceptorByClass(interceptorClass);
    }

}
