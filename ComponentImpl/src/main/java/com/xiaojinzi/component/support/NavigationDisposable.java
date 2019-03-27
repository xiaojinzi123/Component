package com.xiaojinzi.component.support;

import android.support.annotation.AnyThread;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 调用 {@link Router.Builder#navigate()}之后拿到的对象,可以在其中拿到请求对象 {@link RouterRequest}
 * 和 取消这个路由请求
 * note: 如果发起路由的时候由于参数不合格导致 {@link RouterRequest} 对象构建失败,
 * 这时候会返回一个空实现 {@link Router#emptyNavigationDisposable} 对象,
 * 这时候调用 {@link NavigationDisposable#originalRequest()} 会得到一个 null 值
 * <p>
 * time   : 2019/01/25
 *
 * @author : xiaojinzi 30212
 */
public interface NavigationDisposable {

    /**
     * 拿到这个路由的请求对象,这个对象是最原始的那个,不会经过拦截器的修改而变化
     * 使用这个方法需要注意的就是这个方法可能会返回一个 null 的对象
     *
     * @return 当导航的参数构建阶段发生异常, 那么这个就会返回 null,因为这个时候根本连{@link RouterRequest} 都没有构建出来
     */
    @Nullable
    @AnyThread
    RouterRequest originalRequest();

    /**
     * 取消这个路由,怎么调用都不会出问题
     */
    @AnyThread
    void cancel();

}
