package com.xiaojinzi.component2.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component2.R

@RouterAnno(
        path = "test",
        interceptorNames = ["testInterceptor"]
)
class Component2TestAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_test_act)
    }

}
