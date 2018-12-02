package com.ehi.component2

import android.content.Context
import android.widget.Toast
import com.ehi.base.ModuleConfig
import com.ehi.base.service.EHiServiceContainer
import com.ehi.base.service.IServiceLoad
import com.ehi.base.service.impl.component2.Component2Service
import com.ehi.component.anno.EHiModuleApp
import com.ehi.component.application.IComponentApplication
import com.ehi.component.impl.EHiRouter

@EHiModuleApp()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Context) {
        EHiRouter.register(ModuleConfig.Component2.NAME)
        EHiServiceContainer.register(Component2Service::class.java, IServiceLoad {
            Toast.makeText(app,"Component2Service 被加载了",Toast.LENGTH_SHORT).show();
            Component2Service {
                Toast.makeText(app, "Component2Service 方法被调用了", Toast.LENGTH_SHORT).show();
            }
        })
    }

    override fun onDestory() {
        EHiRouter.unregister(ModuleConfig.Component2.NAME)
    }

}
