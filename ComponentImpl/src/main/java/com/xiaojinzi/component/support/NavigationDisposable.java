package com.xiaojinzi.component.support;

import androidx.annotation.AnyThread;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.impl.Navigator;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterRequest;

/**
 * 调用 {@link Navigator#navigate()}之后拿到的对象,可以在其中拿到请求对象 {@link RouterRequest}
 * 和 取消这个路由请求
 * note: 如果发起路由的时候由于参数不合格导致 {@link RouterRequest} 对象构建失败,
 * 这时候会返回一个空实现 {@link Router#emptyNavigationDisposable} 对象,
 * 这时候调用 {@link NavigationDisposable#originalRequest()} 会得到一个 null 值
 * <p>
 * time   : 2019/01/25
 *
 * @author : xiaojinzi
 */
@CheckClassName
public interface NavigationDisposable {

    /**
     * 拿到这个路由的请求对象,这个对象是最原始的那个,不会经过拦截器的修改而变化
     * 使用这个方法需要注意的就是这个方法可能会返回一个 null 的对象,为 null 的时候由两种情况：
     * 1.还没来得及构建 {@link RouterRequest} 就调用此方法
     * 2.构建 {@link RouterRequest} 失败,那么这个方法也会返回 null 的
     *
     * @return {@link RouterRequest}
     */
    @Nullable
    @AnyThread
    RouterRequest originalRequest();

    /**
     * 取消这个路由,怎么调用都不会出问题
     */
    @AnyThread
    void cancel();

    /**
     * 是否已经取消
     *
     * @return
     */
    @AnyThread
    boolean isCanceled();

    interface Cancellable {
        void cancel();
    }

    final class ProxyNavigationDisposableImpl implements NavigationDisposable {

        @Nullable
        private NavigationDisposable proxyNavigationDisposable;

        private boolean isDisposabled = false;

        @Nullable
        @Override
        public synchronized RouterRequest originalRequest() {
            if (proxyNavigationDisposable != null) {
                return proxyNavigationDisposable.originalRequest();
            }
            return null;
        }

        @CallSuper
        @Override
        public synchronized void cancel() {
            if (proxyNavigationDisposable != null) {
                proxyNavigationDisposable.cancel();
            }
            isDisposabled = true;
        }

        public synchronized void setProxy(@NonNull NavigationDisposable navigationDisposable) {
            proxyNavigationDisposable = navigationDisposable;
        }

        @Override
        public synchronized final boolean isCanceled() {
            if (proxyNavigationDisposable != null) {
                return proxyNavigationDisposable.isCanceled();
            }
            return isDisposabled;
        }

    }

}
