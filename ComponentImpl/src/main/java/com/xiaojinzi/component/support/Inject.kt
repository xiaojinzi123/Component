package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import androidx.annotation.UiThread
import android.os.Bundle

/**
 * 每一个生成的类都应该实现这个接口
 */
@CheckClassNameAnno
interface Inject<T> {

    /**
     * 注入属性值, Bundle 从 [android.app.Activity.getIntent] 和 [androidx.fragment.app.Fragment.getArguments] 中来
     *
     * @param target 目标
     */
    @UiThread
    fun injectAttrValue(target: T)

    /**
     * 注入属性值
     *
     * @param target 目标
     * @param bundle 数据源的 bundle
     */
    @UiThread
    fun injectAttrValue(target: T, bundle: Bundle)

    /**
     * 注入 Service
     *
     * @param target 目标
     */
    @UiThread
    fun injectService(target: T)

}