package com.xiaojinzi.component2.view

import android.content.Intent
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.impl.RouterRequest

@RouterAnno(path = "test11")
fun getIntent(request: RouterRequest): Intent {
    return Intent(request.rawContext, Component2Act::class.java)
}

