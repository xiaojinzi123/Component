package com.xiaojinzi.component.support;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * 获取 Host 的接口
 */
public interface IHost {

    /**
     * 获取模块的 host
     */
    @NonNull
    @UiThread
    String getHost();

}
