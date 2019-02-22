package com.ehi.component.interceptor;

import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiRouterInterceptor;
import com.ehi.component.impl.interceptor.EHiInterceptorBean;

import java.util.List;
import java.util.Map;
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
     * 获取全局的拦截器列表,外面使用的地方做了缓存
     * 然后 {@link EHiInterceptorBean#interceptor} 拦截器也可以是 Class 对象,
     * 让外面使用的地方去初始化也可以
     *
     * @return
     */
    @Nullable
    List<EHiInterceptorBean> globalInterceptorList();

    /**
     * 获取普通拦截器的所有名称,然后后面会根据名称来寻找拦截器
     * 注意：这个方法其实不是框架中正常设计的方法,这个方法目前完全就是为了帮助组装各个业务模块的时候是否有重复的
     * 拦截器做检查
     *
     * @return
     */
    @Nullable
    Set<String> getInterceptorNames();

    /**
     * 获取拦截器集合
     *
     * @return
     */
    @Nullable
    Map<String, Class<? extends EHiRouterInterceptor>> getInterceptorMap();

}
