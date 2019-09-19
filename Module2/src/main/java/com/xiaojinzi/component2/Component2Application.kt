package com.xiaojinzi.component2

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IComponentApplication

@ModuleAppAnno()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Application) {
    }

    override fun onDestroy() {
    }

}
