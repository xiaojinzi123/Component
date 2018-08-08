package com.ehi.component2

import android.app.Application
import com.ehi.api.application.IComponentApplication
import com.ehi.api.anno.EHiModuleApp
import com.ehi.api.impl.EHiUiRouter
import com.ehi.base.ComponentEnum

@EHiModuleApp()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Application) {
        EHiUiRouter.register(ComponentEnum.Component2.moduleName)
    }

    override fun onDestory() {
        EHiUiRouter.unregister(ComponentEnum.Component2.moduleName)
    }

}
