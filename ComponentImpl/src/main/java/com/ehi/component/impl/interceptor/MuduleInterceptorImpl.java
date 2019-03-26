package com.ehi.component.impl.interceptor;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.impl.RouterInterceptor;
import com.ehi.component.interceptor.IComponentHostInterceptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 拦截器的代码生成类的基本实现
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
abstract class MuduleInterceptorImpl implements IComponentHostInterceptor {

    protected Map<String, Class<? extends RouterInterceptor>> interceptorMap = new HashMap<>();

    private boolean isInitMap = false;

    @Override
    @NonNull
    public List<InterceptorBean> globalInterceptorList() {
        return Collections.emptyList();
    }

    /**
     * 初始化拦截器的集合
     */
    @CallSuper
    protected void initInterceptorMap(){
        isInitMap = true;
    }

    @Nullable
    @Override
    public Set<String> getInterceptorNames() {
        if (!isInitMap) {
            initInterceptorMap();
        }
        return interceptorMap.keySet();
    }

    @Nullable
    @Override
    public Map<String, Class<? extends RouterInterceptor>> getInterceptorMap() {
        if (!isInitMap) {
            initInterceptorMap();
        }
        return interceptorMap;
    }

    @Override
    @Nullable
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
