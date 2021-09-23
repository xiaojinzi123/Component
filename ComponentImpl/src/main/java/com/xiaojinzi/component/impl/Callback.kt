package com.xiaojinzi.component.impl

import androidx.annotation.UiThread
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.support.OnRouterCancel
import com.xiaojinzi.component.support.OnRouterError
import com.xiaojinzi.component.support.OnRouterSuccess

/**
 * 当路由完成的时候,回调这个接口,这时候的完成不一定是成功的,可能是失败的,成功和失败都表示完成
 * 所有的调用顺序整理：
 * 1.[.onSuccess] --> [.onEvent]
 * 2.[.onError] --> [.onEvent]
 * 3.被取消的时候：[.onCancel]
 *
 * @author xiaojinzi
 */
@UiThread
@CheckClassNameAnno
interface Callback : OnRouterSuccess, OnRouterError, OnRouterCancel {

    /**
     * 两个参数肯定有一个不会为空
     *
     * @param successResult 路由成功时候的返回对象,成功的对象
     * @param errorResult   发生的错误对象
     */
    @UiThread
    fun onEvent(successResult: RouterResult?, errorResult: RouterErrorResult?)

}