package com.xiaojinzi.component.application;

import android.support.annotation.MainThread;

import com.xiaojinzi.component.ComponentConstants;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.IBaseLifecycle;

/**
 * 这是生命周期接口
 * 如果这个类被移动了,一定要格外关注 {@link ComponentConstants#APPLICATION_INTERFACE_CLASS_NAME} 的值
 * 每一个自定义的模块 Application 都需要实现此类, 其实实现 {@link IBaseLifecycle} 也可以.
 * 但是一个用户自定义的模块 Application 不应该是实现一个看起来不合适的接口. IComponentApplication 名字就好得多
 */
@MainThread
@CheckClassNameAnno
public interface IComponentApplication extends IApplicationLifecycle {
}
