package com.ehi.component.support;

import android.support.annotation.AnyThread;
import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiRouterRequest;

/**
 * 导航之后拿到的对象,可以在其中拿到请求对象 {@link EHiRouterRequest}
 * 和 取消这个路由请求
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public interface NavigationDisposable {

    /**
     * 拿到这个路由的请求对象,这个对象是最原始的那个,不会经过拦截器的修改而变化
     * 使用这个方法需要注意的就是这个方法可能会返回一个 null 的对象
     *
     * @return 当导航的参数构建阶段发生异常, 那么这个就会返回 null,因为这个时候根本连{@link EHiRouterRequest} 都没有构建出来
     */
    @Nullable
    EHiRouterRequest originalRequest();

    /**
     * 取消这个路由,怎么调用都不会出问题
     */
    @AnyThread
    void cancel();

}
