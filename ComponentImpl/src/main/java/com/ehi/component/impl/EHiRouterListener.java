package com.ehi.component.impl;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * 路由监听器,目前就只有三种情况,其实还有 RxJava 支持的 EHiRxRouter 成功的和失败的
 * 比如 EHiRxRouter 获取 onActivityResult 中的数据成功了这种,有需要再说把
 *
 * @author xiaojinzi
 */
@MainThread
public interface EHiRouterListener {

    /**
     * 路由成功的时候回调
     *
     * @param successResult
     */
    @MainThread
    void onSuccess(@NonNull EHiRouterResult successResult) throws Exception;

    /**
     * 发生错误的时候的回调
     *
     * @param errorResult
     */
    @MainThread
    void onError(EHiRouterErrorResult errorResult) throws Exception;

    /**
     * 当被取消的时候回调
     *
     * @param originalRequest
     * @throws Exception
     */
    @MainThread
    void onCancel(@NonNull RouterRequest originalRequest) throws Exception;

}