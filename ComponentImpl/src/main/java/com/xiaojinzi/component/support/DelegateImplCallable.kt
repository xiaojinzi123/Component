package com.xiaojinzi.component.support

interface DelegateImplCallable<T> {

    /**
     * 代理实现的获取
     */
    var delegateImplCallable: () -> T

    /*fun getDelegateImpl(): T {
        error("Not Support")
    }*/

}

class DelegateImplCallableImpl<T>: DelegateImplCallable<T> {

    override var delegateImplCallable: () -> T = {
        error("Not Support")
    }

}