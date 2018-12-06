package com.ehi.component2.service

import com.ehi.base.service.inter.component2.Component2Service
import com.ehi.component.anno.EHiServiceAnno

/**
 * time   : 2018/12/06
 *
 * @author : xiaojinzi 30212
 */
@EHiServiceAnno(value = [Component2Service::class])
class Component2ServiceImpl : Component2Service {

    override fun doSomeThing() {
    }

}
