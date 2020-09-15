package com.xiaojinzi.component.application;

import android.support.annotation.UiThread;

import com.xiaojinzi.component.anno.support.CheckClassNameAnno;

/**
 * @see IApplicationLifecycle
 */
@UiThread
@CheckClassNameAnno
public interface IComponentApplication extends IApplicationLifecycle {
}
