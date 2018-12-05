package com.ehi.component2

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.ehi.base.service.inter.component2.Component2Service
import com.ehi.component.anno.EHiModuleAppAnno
import com.ehi.component.application.IComponentApplication
import com.ehi.component.service.EHiService
import com.ehi.component.service.IServiceLoad

@EHiModuleAppAnno()
class Component2Application : IComponentApplication {

    override fun onCreate(app: Application) {
    }

    override fun onDestory() {
    }

}
