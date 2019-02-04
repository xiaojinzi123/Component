package com.ehi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.impl.EHiRouter;
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
     * 空实现,里头都是不能调用的方法
     * 这个对象只会在构建 {@link EHiRouterRequest} 对象失败或者构建之前就发生错误的情况才会被返回
     * 这里为什么会有这个类是因为在调用 {@link EHiRouter.Builder#navigate()} 的时候,会返回一个
     */
    NavigationDisposable EMPTY = new NavigationDisposable() {
        @Nullable
        @Override
        public EHiRouterRequest request() {
            return null;
        }

        @Override
        public void cancel() {
            // ignore
        }
    };

    /**
     * 拿到这个路由的请求对象,这个对象是最原始的那个,不会经过拦截器的修改而变化
     *
     * @return 当导航的参数构建阶段发生异常, 那么这个就会返回 null,因为这个时候根本连{@link EHiRouterRequest} 都没有构建出来
     */
    @Nullable
    EHiRouterRequest request();

    /**
     * 取消这个路由
     */
    void cancel();

}
