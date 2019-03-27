package com.xiaojinzi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * 路由监听器,目前就只有三种情况,其实还有 RxJava 支持的 RxRouter 成功的和失败的
 * 比如 RxRouter 获取 onActivityResult 中的数据成功了这种,有需要再说把
 *
 * @author xiaojinzi
 */
@MainThread
public interface RouterListener {

    /**
     * 路由成功的时候回调
     *
     * @param successResult
     */
    @MainThread
    void onSuccess(@NonNull RouterResult successResult) throws Exception;

    /**
     * 发生错误的时候的回调
     *
     * @param errorResult
     */
    @MainThread
    void onError(RouterErrorResult errorResult) throws Exception;

    /**
     * 当被取消的时候回调
     *
     * @param originalRequest
     * @throws Exception
     */
    @MainThread
    void onCancel(@NonNull RouterRequest originalRequest) throws Exception;

}