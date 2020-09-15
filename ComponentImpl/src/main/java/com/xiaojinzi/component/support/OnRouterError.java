package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.xiaojinzi.component.impl.RouterErrorResult;

/**
 * 被取消了回调接口
 */
public interface OnRouterError {

    /**
     * 当路由错误的时候回调
     *
     * @param errorResult 路由失败的对象
     */
    @UiThread
    void onError(@NonNull RouterErrorResult errorResult);

}
