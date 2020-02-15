package com.xiaojinzi.component.support;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * 获取 Host 的接口
 */
public interface IHost {

    /**
     * 获取模块的 host
     */
    @NonNull
    @MainThread
    String getHost();

}
