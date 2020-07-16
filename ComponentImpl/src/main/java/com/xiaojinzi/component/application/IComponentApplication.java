package com.xiaojinzi.component.application;

import androidx.annotation.MainThread;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.IBaseLifecycle;

/**
 * @see IApplicationLifecycle
 */
@MainThread
@CheckClassNameAnno
public interface IComponentApplication extends IApplicationLifecycle {
}
