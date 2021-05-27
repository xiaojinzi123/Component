package com.xiaojinzi.component.support

import android.support.annotation.UiThread
import com.xiaojinzi.component.impl.RouterResult

interface OnRouterSuccess {

    /**
     * 当路由成功的时候回调, 只页面跳转成功.
     * 但是对于 forwardForResult 这种, 此方法回调了仅仅完成了跳转的环节.
     * @param result 路由成功的对象
     */
    @UiThread
    fun onSuccess(result: RouterResult)

}