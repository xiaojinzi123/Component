package com.xiaojinzi.component2.service

import com.xiaojinzi.component.anno.ServiceAnno

/**
 * time   : 2018/12/06
 *
 * @author : xiaojinzi
 */
@ServiceAnno(value = [TestInterface::class], singleTon = false)
class TestInterfaceImpl : TestInterface {
}
