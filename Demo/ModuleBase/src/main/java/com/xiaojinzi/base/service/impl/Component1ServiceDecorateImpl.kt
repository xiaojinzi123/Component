package com.xiaojinzi.base.service.impl

import com.xiaojinzi.base.service.inter.component1.Component1Service
import com.xiaojinzi.component.anno.ServiceDecorateAnno

@ServiceDecorateAnno(Component1Service::class)
class Component1ServiceDecorateImpl(private val target: Component1Service) : Component1Service by target {

    override fun doSomeThing() {
        target.doSomeThing()
        println("增强功能成功")
    }

}