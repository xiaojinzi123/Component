package com.xiaojinzi.component.service;

import com.xiaojinzi.component.support.IBaseLifecycle;
import com.xiaojinzi.component.support.IHost;

/**
 * 每个模块的接口,需要有生命周期的通知
 */
public interface IComponentHostService extends IBaseLifecycle, IHost {
}
