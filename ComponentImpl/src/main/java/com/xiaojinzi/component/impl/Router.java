package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.cache.Cache;
import com.xiaojinzi.component.cache.CacheType;
import com.xiaojinzi.component.cache.DefaultCacheFactory;
import com.xiaojinzi.component.router.IComponentHostRouter;
import com.xiaojinzi.component.support.NavigationDisposable;
import com.xiaojinzi.component.support.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 整个路由框架,整体都是在主线程中执行的,在拦截器中提供了 callback 机制
 * 所以有耗时的操作可以在拦截器中去开子线程执行然后在回调中继续下一个拦截器
 * <p>
 * 这个类必须放在 {@link ComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * 这个类作为框架对外的一个使用的类,里面会很多易用的方法
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class Router {

    protected Router() {
    }

    /**
     * 类的标志
     */
    public static final String TAG = "Router";

    /**
     * 拦截器 Class --> RouterInterceptor 的缓存
     */
    private static final Cache<Class, Object> apiClassCache =
            DefaultCacheFactory.INSTANCE.build(CacheType.ROUTER_INTERCEPTOR_CACHE);

    /**
     * 空实现,里头都是不能调用的方法
     * 这个对象只会在构建 {@link RouterRequest} 对象失败或者构建之前就发生错误的情况才会被返回
     * 这里为什么会有这个类是因为在调用 {@link Navigator#navigate()} 的时候,会返回一个
     */
    public static final NavigationDisposable emptyNavigationDisposable = new NavigationDisposable() {

        @Nullable
        @Override
        public RouterRequest originalRequest() {
            return null;
        }

        @Override
        public void cancel() {
            // ignore
        }

        @Override
        public boolean isCanceled() {
            return true;
        }

    };

    /**
     * 路由的监听器
     */
    static Collection<RouterListener> routerListeners = Collections
            .synchronizedCollection(new ArrayList<RouterListener>(0));

    // 支持取消的一个 Callback 集合,需要线程安全
    static List<NavigationDisposable> mNavigationDisposableList = new CopyOnWriteArrayList<>();

    public static void clearRouterListeners() {
        routerListeners.clear();
    }

    public static void addRouterListener(@NonNull RouterListener listener) {
        if (routerListeners.contains(listener)) {
            return;
        }
        routerListeners.add(listener);
    }

    public static void removeRouterListener(RouterListener listener) {
        if (listener == null) {
            return;
        }
        routerListeners.remove(listener);
    }

    public static void register(IComponentHostRouter router) {
        RouterCenter.getInstance().register(router);
    }

    public static void register(@NonNull String host) {
        RouterCenter.getInstance().register(host);
    }

    public static void unregister(IComponentHostRouter router) {
        RouterCenter.getInstance().unregister(router);
    }

    public static void unregister(@NonNull String host) {
        RouterCenter.getInstance().unregister(host);
    }

    public static Navigator with(@NonNull Context context) {
        return new Navigator(context);
    }

    public static Navigator with(@NonNull Fragment fragment) {
        return new Navigator(fragment);
    }

    /**
     * 拿到一个接口的实现类
     *
     * @param apiClass
     * @param <T>
     * @return
     */
    public static <T> T withApi(@NonNull Class<T> apiClass) {
        T t = (T) apiClassCache.get(apiClass);
        if (t == null) {
            String className = ComponentUtil.genRouterApiImplClassName(apiClass);
            try {
                t = (T) Class.forName(className).newInstance();
                apiClassCache.put(apiClass, t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return t;
    }

    public static boolean isMatchUri(@NonNull Uri uri) {
        return RouterCenter.getInstance().isMatchUri(uri);
    }

    /**
     * 取消某一个 Activity的有关的路由任务
     *
     * @param act
     */
    @MainThread
    public static void cancel(@NonNull Activity act) {
        synchronized (mNavigationDisposableList) {
            for (int i = mNavigationDisposableList.size() - 1; i >= 0; i--) {
                NavigationDisposable disposable = mNavigationDisposableList.get(i);
                if (act == Utils.getActivityFromContext(disposable.originalRequest().context)) {
                    disposable.cancel();
                    mNavigationDisposableList.remove(i);
                }
            }
        }
    }

    /**
     * 取消一个 Fragment 的有关路由任务
     *
     * @param fragment
     */
    @MainThread
    public static void cancel(@NonNull Fragment fragment) {
        synchronized (mNavigationDisposableList) {
            for (int i = mNavigationDisposableList.size() - 1; i >= 0; i--) {
                NavigationDisposable disposable = mNavigationDisposableList.get(i);
                if (fragment == disposable.originalRequest().fragment) {
                    disposable.cancel();
                    mNavigationDisposableList.remove(i);
                }
            }
        }
    }

}
