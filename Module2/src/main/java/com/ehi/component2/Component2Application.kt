package com.ehi.component2

import android.app.Application
import com.ehi.component.anno.EHiModuleAppAnno
import com.ehi.component.application.IComponentApplication

@EHiModuleAppAnno()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Application) {
    }

    override fun onDestory() {
    }

}
