package com.ehi.component2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ehi.component.anno.EHiRouter

@EHiRouter("component2/test")
class Component2TestAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_test_act)
    }

}
