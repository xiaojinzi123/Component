package com.xiaojinzi.component.impl.application

import android.app.Application
import androidx.annotation.CallSuper
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IComponentHostApplication
import com.xiaojinzi.component.application.IModuleNotifyChanged
import java.util.*

/**
 * 这个类是生成的 Module Application 类的父类
 * 如果名称更改了,请配置到
 * [com.xiaojinzi.component.ComponentUtil.IMPL_OUTPUT_PKG]
 * 和 [com.xiaojinzi.component.ComponentUtil.MODULE_APPLICATION_IMPL_CLASS_NAME] 上
 *
 * @author xiaojinzi
 */
internal abstract class ModuleApplicationImpl : IComponentHostApplication {

    /**
     * Application的集合
     */
    @JvmField
    protected val moduleAppList: List<IApplicationLifecycle> = ArrayList()

    /**
     * 是否初始化了list,懒加载
     */
    private var hasInitList = false

    @CallSuper
    protected open fun initList() {
        hasInitList = true
    }

    @CallSuper
    override fun onCreate(app: Application) {
        if (!hasInitList) {
            initList()
        }
        for (application in moduleAppList) {
            application.onCreate(app)
        }
    }

    @CallSuper
    override fun onDestroy() {
        if (!hasInitList) {
            initList()
        }
        for (application in moduleAppList) {
            application.onDestroy()
        }
    }

    override fun onModuleChanged(app: Application) {
        for (applicationLifecycle in moduleAppList) {
            if (applicationLifecycle is IModuleNotifyChanged) {
                (applicationLifecycle as IModuleNotifyChanged).onModuleChanged(app)
            }
        }
    }

}