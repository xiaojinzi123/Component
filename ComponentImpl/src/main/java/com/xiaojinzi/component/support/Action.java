package com.xiaojinzi.component.support;

import android.support.annotation.MainThread;

/**
 * time   : 2019/01/07
 *
 * @author : xiaojinzi 30212
 */
public interface Action {

    /**
     * 需要执行的动作
     *
     * @throws Exception 允许执行的时候抛出一个异常
     */
    @MainThread
    void run();

}
