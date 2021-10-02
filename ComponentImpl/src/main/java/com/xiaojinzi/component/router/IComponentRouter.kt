package com.xiaojinzi.component.router

import androidx.annotation.UiThread
import com.xiaojinzi.component.impl.RouterRequest
import android.content.Intent
import android.net.Uri
import com.xiaojinzi.component.impl.RouterInterceptor
import java.lang.Exception

/**
 * 组件之间实现跳转的接口
 *
 *
 * time   : 2018/07/26
 *
 * @author : xiaojinzi
 */
interface IComponentRouter {

    /**
     * Note: 不对外公开使用！
     * 打开一个链接实现跳转
     *
     * @throws android.content.ActivityNotFoundException
     */
    @UiThread
    @Throws(Exception::class)
    fun openUri(routerRequest: RouterRequest): Intent?

    /**
     * Note: 不对外公开使用！
     *
     *
     * 是否有匹配的 uri
     * 此方法仅供框架内部使用, 请勿在自己项目中根据此 Api 来判断一个 [Uri] 是否有匹配的路由目标
     * 原因如下：
     * 当发生真正的路由的时候, 一个路由请求对象 [RouterRequest] 会经过若干个拦截器 [RouterInterceptor]
     * 所以路由请求对象 [RouterRequest] 内部的 [Uri] 有可能会发生变化. 所以就会有以下的情况发生.
     * 如果你在项目中使用此 Api 进行一个 [Uri] 的判断是否有匹配的路由目标.
     * 不管你当时拿到的结果是 true 还是 false, 这都不能代表你最终路由的时候, 是否确定能找到目标. 原因如上已经解释了
     * 总结一句话就是：根据一个 [Uri] 无法得知是否一定有路由的目标, 因为路由的过程 [RouterRequest]
     * 会发生变化
     *
     * @param uri 要打开的界面 Uri
     * @return 返回的值表示是否路由表中有匹配的目标界面
     */
    fun isMatchUri(uri: Uri): Boolean

    /**
     * 比较两个 [Uri] 是否是同一个目标.
     * 这两个 [Uri.equals] 不一定相等的.
     * 因为这里比较的是内部的信息是否指向了同一个目标
     * 当两个都为null的时候, 内部也会返回 true 的, 这点请注意
     *
     * @param uri1 第一个 [Uri]
     * @param uri2 第二个 [Uri]
     * @return 返回两个 [Uri] 是否指向同一个目标
     */
    fun isSameTarget(uri1: Uri, uri2: Uri): Boolean

    /**
     * Note: 不对外公开使用！
     * 获取这个目标要执行的页面拦截器
     *
     * @return 返回此界面的页面拦截器
     * @throws Exception
     */
    @UiThread
    @Throws(Exception::class)
    fun listPageInterceptors(uri: Uri): List<RouterInterceptor>

    /**
     * Note: 不对外公开使用！
     * 获取该 URI 对应的降级需要执行的拦截器
     */
    @UiThread
    @Throws(Exception::class)
    fun listDegradeInterceptors(uri: Uri): List<RouterInterceptor>

}