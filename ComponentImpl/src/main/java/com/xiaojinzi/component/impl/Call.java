package com.xiaojinzi.component.impl;

import android.content.Intent;
import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * 这个对象表示一个可调用的路由跳转
 */
@CheckClassNameAnno
public interface Call {

    /**
     * 普通跳转
     */
    @AnyThread
    void forward();

    /**
     * 普通跳转
     *
     * @return 可用于取消本次路由
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigate();

    /**
     * 普通跳转
     *
     * @param callback 当跳转完毕或者发生错误会回调
     */
    @AnyThread
    void forward(@Nullable final Callback callback);

    /**
     * 执行跳转的具体逻辑
     * 返回值不可以为空,是为了使用的时候更加的顺溜,不用判断空
     *
     * @param callback 当跳转完毕或者发生错误会回调,
     *                 回调给用户的 {@link Callback}, 回调中的各个方法, 每个方法至多有一个方法被调用, 并且只有一次
     * @return 返回的对象有可能是一个空实现对象 {@link Router#emptyNavigationDisposable},可以取消路由或者获取原始request对象
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigate(@Nullable final Callback callback);

    /**
     * 跳转拿到 {@link ActivityResult} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     */
    @AnyThread
    void forwardForResult(@NonNull final BiCallback<ActivityResult> callback);

    /**
     * 跳转拿到 {@link ActivityResult} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigateForResult(@NonNull final BiCallback<ActivityResult> callback);

    /**
     * 跳转拿到 {@link Intent} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     */
    @AnyThread
    void forwardForIntent(@NonNull final BiCallback<Intent> callback);

    /**
     * 跳转拿到 {@link Intent} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigateForIntent(@NonNull final BiCallback<Intent> callback);

    /**
     * 跳转拿到 {@link Intent} 数据
     *
     * @param callback           当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     */
    @AnyThread
    void forwardForIntentAndResultCodeMatch(@NonNull final BiCallback<Intent> callback,
                                            final int expectedResultCode);

    /**
     * 跳转拿到 {@link Intent} 数据
     *
     * @param callback           当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     * @return 可用于取消本次路由
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigateForIntentAndResultCodeMatch(@NonNull final BiCallback<Intent> callback,
                                                             final int expectedResultCode);

    /**
     * 跳转为了匹配 {@link ActivityResult} 中的 {@link ActivityResult#resultCode}
     *
     * @param callback           当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     */
    @AnyThread
    void forwardForResultCodeMatch(@NonNull final Callback callback,
                                   final int expectedResultCode);

    /**
     * 跳转为了匹配 {@link ActivityResult} 中的 {@link ActivityResult#resultCode}
     *
     * @param callback           当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     * @return 可用于取消本次路由
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigateForResultCodeMatch(@NonNull final Callback callback,
                                                    final int expectedResultCode);

    /**
     * 跳转拿到 {@link ActivityResult#resultCode} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     */
    @AnyThread
    void forwardForResultCode(@NonNull final BiCallback<Integer> callback);

    /**
     * 跳转拿到 {@link ActivityResult#resultCode} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    @AnyThread
    @CheckResult
    NavigationDisposable navigateForResultCode(@NonNull final BiCallback<Integer> callback);

}
