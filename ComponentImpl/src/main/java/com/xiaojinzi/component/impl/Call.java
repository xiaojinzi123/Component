package com.xiaojinzi.component.impl;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.support.NavigationDisposable;

/**
 * 这个对象表示一个可调用的路由跳转
 */
public interface Call {

    /**
     * 普通跳转
     *
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigate();

    /**
     * 普通跳转
     *
     * @param callback 当跳转完毕或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigate(@Nullable final Callback callback);

    /**
     * 跳转拿到 {@link ActivityResult} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigateForResult(@NonNull final BiCallback<ActivityResult> callback);

    /**
     * 跳转拿到 {@link Intent} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigateForIntent(@NonNull final BiCallback<Intent> callback);

    /**
     * 跳转拿到 {@link Intent} 数据
     *
     * @param callback           当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigateForIntentAndResultCodeMatch(@NonNull final BiCallback<Intent> callback,
                                                             final int expectedResultCode);

    /**
     * 跳转为了匹配 {@link ActivityResult} 中的 {@link ActivityResult#resultCode}
     *
     * @param callback           当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @param expectedResultCode 会匹配的 resultCode
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigateForResultCodeMatch(@NonNull final Callback callback,
                                                    final int expectedResultCode);

    /**
     * 跳转拿到 {@link ActivityResult#resultCode} 数据
     *
     * @param callback 当 {@link ActivityResult} 拿到之后或者发生错误会回调
     * @return 可用于取消本次路由
     */
    @NonNull
    NavigationDisposable navigateForResultCode(@NonNull final BiCallback<Integer> callback);

}
