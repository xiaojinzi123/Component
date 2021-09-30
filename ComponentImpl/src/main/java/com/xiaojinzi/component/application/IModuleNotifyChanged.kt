package com.xiaojinzi.component.application

import android.app.Application
import androidx.annotation.UiThread

@UiThread
interface IModuleNotifyChanged {

    /**
     * 当加载的模块发生变化的时候会调用
     * 并且多个模块如果被加载
     *
     * @param app [Application]
     */
    @UiThread
    fun onModuleChanged(app: Application)

}