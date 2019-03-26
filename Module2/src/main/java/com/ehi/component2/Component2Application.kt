package com.ehi.component2

import android.app.Application
import com.ehi.component.anno.ModuleAppAnno
import com.ehi.component.application.IComponentApplication

@ModuleAppAnno()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Application) {
    }

    override fun onDestory() {
    }

}
