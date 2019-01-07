package com.ehi.component.support;

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
     * @throws Exception
     */
    @MainThread
    void run() throws Exception;

}
