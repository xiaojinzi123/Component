package com.xiaojinzi.component2.service

import android.widget.Toast
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.support.Utils

/**
 * time   : 2018/12/06
 *
 * @author : xiaojinzi
 */
@ServiceAnno(value = [TestInterface::class], singleTon = false, autoInit = true)
class TestInterfaceImpl : TestInterface {

    constructor() {
        Utils.postActionToMainThread {
            Toast.makeText(Component.getApplication(), "TestInterface Service 自动初始化", Toast.LENGTH_SHORT).show()
        }
    }


    override fun doSomeThing() {
        TODO("Not yet implemented")
    }

}
