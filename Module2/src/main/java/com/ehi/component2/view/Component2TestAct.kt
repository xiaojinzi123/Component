package com.ehi.component2.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ehi.component.anno.EHiRouterAnno
import com.ehi.component2.R

@EHiRouterAnno("component2/test")
class Component2TestAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_test_act)
    }

}
