package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.impl.RouterRequest
import android.content.Intent
import java.lang.Exception

/**
 * 跳转的 Intent 用户自定义,[RouterAnno] 注解标记在静态方法上的时候
 * 当返回值是 [Intent] 的时候,就会产生一个此接口的实现,实现获取目标界面跳转的 [Intent]
 */
@CheckClassNameAnno
interface CustomerIntentCall {

    /**
     * 获取创建的 Intent
     *
     * @param request 路由请求对象
     * @return
     */
    @Throws(Exception::class)
    operator fun get(request: RouterRequest): Intent

}