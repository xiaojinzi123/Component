package com.xiaojinzi.component.fragment;

import com.xiaojinzi.component.application.IComponentApplication;

/**
 * 每个模块的接口,需要有生命周期的通知
 */
public interface IComponentHostFragment extends IComponentApplication {

    /**
     * 获取当前的 host
     *
     * @return
     */
    String getHost();

}
