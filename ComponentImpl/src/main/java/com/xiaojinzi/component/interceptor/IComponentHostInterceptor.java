package com.xiaojinzi.component.interceptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.application.IComponentApplication;
import com.xiaojinzi.component.impl.RouterInterceptor;
import com.xiaojinzi.component.impl.interceptor.InterceptorBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * time   : 2018/12/26
 *
 * @author : xiaojinzi 30212
 */
public interface IComponentHostInterceptor extends IComponentInterceptor, IComponentApplication {

    /**
     * 获取当前的 host
     *
     * @return
     */
    String getHost();

    /**
     * 获取全局的拦截器列表,外面使用的地方做了缓存
     * 然后 {@link InterceptorBean#interceptor} 拦截器也可以是 Class 对象,
     * 让外面使用的地方去初始化也可以
     *
     * @return
     */
    @NonNull
    List<InterceptorBean> globalInterceptorList();

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
    Map<String, Class<? extends RouterInterceptor>> getInterceptorMap();

}
