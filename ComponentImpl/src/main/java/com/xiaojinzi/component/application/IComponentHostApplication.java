package com.xiaojinzi.component.application;

import androidx.annotation.NonNull;

/**
 * 每一个实现类都必须返回对应的 Host
 */
public interface IComponentHostApplication extends IComponentApplication {

    /**
     * 获取host
     *
     * @return
     */
    @NonNull
    String getHost();

}
