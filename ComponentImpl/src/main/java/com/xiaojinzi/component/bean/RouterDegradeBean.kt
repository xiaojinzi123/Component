package com.xiaojinzi.component.bean

import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.impl.RouterDegrade

@CheckClassNameAnno
data class RouterDegradeBean(
        /**
         * 优先级
         */
        var priority: Int = 0,
        /**
         * 这个目标 Activity Class,可能为空,因为可能标记在静态方法上
         */
        var targetClass: Class<out RouterDegrade>? = null,
)
