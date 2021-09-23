package com.xiaojinzi.component.support

import androidx.annotation.UiThread

/**
 * 获取 Host 的接口
 */
interface IHost {

    /**
     * 获取模块的 host
     */
    @get:UiThread
    val host: String

}