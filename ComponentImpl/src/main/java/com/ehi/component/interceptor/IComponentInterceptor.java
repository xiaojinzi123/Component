package com.ehi.component.interceptor;

import android.support.annotation.Nullable;

import com.ehi.component.impl.RouterInterceptor;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentInterceptor {

    /**
     * 根据拦截器的唯一名字获取拦截器
     *
     * @param name if name is null,return null
     * @return
     */
    @Nullable
    RouterInterceptor getByName(@Nullable String name);

}
