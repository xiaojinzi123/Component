package com.ehi.component2

import android.content.Context
import com.ehi.base.ComponentEnum
import com.ehi.component.anno.EHiModuleApp
import com.ehi.component.application.IComponentApplication
import com.ehi.component.impl.EHiUiRouter

@EHiModuleApp()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Context) {
        EHiUiRouter.register(ComponentEnum.Component2.moduleName)
    }

    override fun onDestory() {
        EHiUiRouter.unregister(ComponentEnum.Component2.moduleName)
    }

}
