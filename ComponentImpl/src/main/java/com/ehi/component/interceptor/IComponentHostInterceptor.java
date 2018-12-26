package com.ehi.component.interceptor;

import android.support.annotation.Nullable;

import com.ehi.component.impl.interceptor.EHiInterceptorBean;

import java.util.List;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentHostInterceptor extends IComponentInterceptor {

    /**
     * 获取当前的 host
     *
     * @return
     */
    String getHost();

    /**
     * 获取拦截器列表
     *
     * @return
     */
    @Nullable
    List<EHiInterceptorBean> interceptorList();

}
