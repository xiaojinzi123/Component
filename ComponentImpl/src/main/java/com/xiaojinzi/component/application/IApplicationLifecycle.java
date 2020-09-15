package com.xiaojinzi.component.application;

import android.support.annotation.UiThread;

import com.xiaojinzi.component.anno.ModuleAppAnno;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.support.IBaseLifecycle;

/**
 * 用 {@link ModuleAppAnno} 的类
 * 是用户自定义的模块生命周期类. 必须实现此接口. 当你依赖的版本 <= 1.8.2.3
 * 你需要使用老的 {@link IComponentApplication} 接口. 版本 > 1.8.2.3
 * 你可以实现此接口或者{@link IComponentApplication} 接口, 但是建议实现此接口.
 * 老接口 {@link IComponentApplication} 会在将来某个版本删除
 */
@UiThread
@CheckClassNameAnno
public interface IApplicationLifecycle extends IBaseLifecycle {
}
