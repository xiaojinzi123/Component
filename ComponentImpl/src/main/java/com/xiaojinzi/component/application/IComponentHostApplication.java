package com.xiaojinzi.component.application;

import com.xiaojinzi.component.support.IHost;

/**
 * 每一个实现类都必须返回对应的 Host
 */
public interface IComponentHostApplication extends IApplicationLifecycle, IHost, IModuleNotifyChanged {
}
