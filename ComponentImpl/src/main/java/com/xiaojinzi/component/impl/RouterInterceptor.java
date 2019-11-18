package com.xiaojinzi.component.impl;

import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.support.RouterInterceptorCache;

/**
 * 路由跳转的拦截器,设计是 callback 机制代替即时返回的,即时返回比如 OkHttp的拦截器
 * 和这类功能性库不同的是这个是组件路由的拦截器,大部分代码需要在主线程执行,所以设计的初衷：
 * {@link RouterInterceptor#intercept(Chain)} 方法内的代码会在主线程执行,需要用户调用的下面几个方法 {@link Chain#proceed(RouterRequest)}
 * {@link Callback#onError(Throwable)}  和 {@link Callback#onSuccess(RouterResult)} 可以在任何线程调用
 * 这么做的目的也是为了方便用户在任何时候都可以调用而不用关心线程
 * <p>
 * note: 每一个实现拦截器接口的拦截器,构造方法支持三种,其他的不支持
 * 1.空参数构造函数
 * 2.{@link android.app.Application} 构造函数
 * 3.{@link android.content.Context} 构造函数
 * <p>
 * 创建拦截器的过程是一个反射创建的过程,具体代码请看：{@link RouterInterceptorCache#getInterceptorByClass(Class)}
 *
 * @author xiaojinzi
 */
@CheckClassName
public interface RouterInterceptor {

    /**
     * 拦截器的拦截方法, 一定是在主线程中被执行的, 您不必担心此方法的线程安全问题
     *
     * @param chain 拦截器执行连接器
     */
    @MainThread
    void intercept(@NonNull Chain chain) throws Exception;

    /**
     * 执行器
     */
    interface Chain {

        /**
         * 拿到请求对象
         *
         * @return 返回当前的路由请求对象
         */
        @NonNull
        @AnyThread
        RouterRequest request();

        /**
         * 获取回调对象
         *
         * @return 返回一个回调对象, 您可以通过这个对象直接结束这个路由请求
         */
        @NonNull
        @AnyThread
        Callback callback();

        /**
         * 执行这个跳转
         *
         * @param request 继续路由下去的请求对象
         */
        @AnyThread
        void proceed(@NonNull RouterRequest request);

    }

    /**
     * 回调对象,错误和成功的方法均只能调用一次,多次调用只有第一次有用,其他会被忽略
     * 内部的方法可能会在任何线程被调用,
     * 虽然拦截器 {@link RouterInterceptor#intercept(Chain)} 的方法在主线程执行,
     * 但是内部可以在其他线程去调用 {@link RouterInterceptor.Callback} 类中的每一个方法
     * <p>
     * 此对象内部是对一次路由的内部回调对象, 保证了回调只会进行一次.
     * 此对象目前唯一的一个实现类为 {@link com.xiaojinzi.component.impl.Navigator.InterceptorCallback}
     * 它内部持有 {@link com.xiaojinzi.component.impl.Callback} 对象,
     * 间接的保证了 {@link com.xiaojinzi.component.impl.Callback} 对象中的方法只被执行一次
     */
    interface Callback {

        /**
         * 成功的回调
         * {@link #onSuccess(RouterResult)} 和 {@link #onError(Throwable)} 都有且只能调用一次,多次调用
         * 的时候第一次调用为准,其他的忽略
         *
         * @param result 路由成功的对象
         */
        @AnyThread
        void onSuccess(@NonNull RouterResult result);

        /**
         * 错误的回调
         * {@link #onSuccess(RouterResult)} 和 {@link #onError(Throwable)} 都有且只能调用一次,多次调用
         * 的时候第一次调用为准,其他的忽略
         *
         * @param error 路由失败的对象
         */
        @AnyThread
        void onError(@NonNull Throwable error);

        /**
         * 是否完成了
         *
         * @return 是否整个路由行程已经结束
         */
        @AnyThread
        boolean isComplete();

        /**
         * 是否已经取消了
         *
         * @return 是否整个路由行程已经被取消了
         */
        @AnyThread
        boolean isCanceled();

        /**
         * 是否结束了, 完成和取消都算结束了
         *
         * @return
         */
        @AnyThread
        boolean isEnd();

    }

}