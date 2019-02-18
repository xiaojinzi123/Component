package com.ehi.component.interceptor;

import android.support.annotation.Nullable;

import com.ehi.component.impl.interceptor.EHiInterceptorBean;

import java.util.List;
import java.util.Set;

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
     * 获取全局的拦截器列表
     *
     * @return
     */
    @Nullable
    List<EHiInterceptorBean> globalInterceptorList();

    /**
     * 获取普通拦截器的所有名称,然后后面会根据名称来寻找拦截器
     *
     * @return
     */
    @Nullable
    Set<String> getInterceptorNames();

}
