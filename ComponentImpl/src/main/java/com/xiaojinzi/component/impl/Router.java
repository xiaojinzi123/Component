package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentUtil;
import com.xiaojinzi.component.cache.Cache;
import com.xiaojinzi.component.cache.CacheType;
import com.xiaojinzi.component.cache.DefaultCacheFactory;
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
            DefaultCacheFactory.INSTANCE.build(CacheType.CLASS_CACHE);

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

    @NonNull
    public static FragmentNavigator with(@NonNull String fragmentFlag) {
        Utils.checkNullPointer(fragmentFlag, "fragmentFlag");
        return new FragmentNavigator(fragmentFlag);
    }

    /**
     * 空参数的默认会使用 {@link Component#getApplication()} 来跳转,
     * 所以空参数的这种不能够用来获取 {@link com.xiaojinzi.component.bean.ActivityResult}
     * 同时用户在自定义拦截器的时候, 也要注意 {@link Context} 未必是一个 {@link Activity}
     * 所以使用者请注意了, 此方法在明确有 {@link Activity} 可以拿到的时候请务必使用
     * {@link #with(Context)} 方法或者 {@link #with(Fragment)}
     * 此方法虽然你可以在任何时候用, 但是作者建议一定要在拿不到 {@link Activity} 和 {@link Fragment}
     * 的时候去用, 而不是随便用
     *
     * @return 返回一个路由的 Builder
     */
    @NonNull
    public static Navigator with() {
        return new Navigator();
    }

    @NonNull
    public static Navigator with(@NonNull Context context) {
        return new Navigator(context);
    }

    @NonNull
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
    @NonNull
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
     * @param act 要取消的 {@link Activity}
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
