package com.ehi.component2.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ehi.base.ModuleConfig
import com.ehi.component.anno.EHiRouterAnno
import com.ehi.component2.R

@EHiRouterAnno(host = ModuleConfig.Component2.NAME, value = ModuleConfig.Component2.MAIN, desc = " 业务组件2的主界面 ")
class Component2Act : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_act)
    }

}
