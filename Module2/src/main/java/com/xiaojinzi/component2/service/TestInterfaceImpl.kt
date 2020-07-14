package com.xiaojinzi.component2.service

import android.app.Application
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.support.IModuleLifecycle

/**
 * time   : 2018/12/06
 *
 * @author : xiaojinzi
 */
@ServiceAnno(value = [TestInterface::class], singleTon = false)
class TestInterfaceImpl : TestInterface, IModuleLifecycle {

    override fun doSomeThing() {
        TODO("Not yet implemented")
    }

    override fun onModuleCreate(app: Application) {
    }

    override fun onModuleDestroy() {
    }

}
