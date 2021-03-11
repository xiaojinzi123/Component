package com.xiaojinzi.component2.service

import com.xiaojinzi.base.service.inter.component1.Component1Service
import com.xiaojinzi.component.anno.ConditionalAnno
import com.xiaojinzi.component.anno.ServiceDecoratorAnno
import com.xiaojinzi.component.support.Condition

@ConditionalAnno(conditions = [TestCondition::class])
@ServiceDecoratorAnno(Component1Service::class, priority = 100)
class Component1ServiceDecoratorImpl(private val target: Component1Service) : Component1Service by target {

    override fun doSomeThing() {
        target.doSomeThing()
        println("增强功能成功")
    }


}

class TestCondition : Condition {
    override fun matches(): Boolean {
        return false
    }
}