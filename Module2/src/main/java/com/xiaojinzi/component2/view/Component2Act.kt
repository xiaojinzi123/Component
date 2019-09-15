package com.xiaojinzi.component2.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xiaojinzi.base.ModuleConfig
import com.xiaojinzi.base.view.BaseAct
import com.xiaojinzi.component.anno.FiledAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.support.ParameterSupport
import com.xiaojinzi.component2.R
import kotlinx.android.synthetic.main.component2_act.*

@RouterAnno(
        path = ModuleConfig.Module2.MAIN,
        desc = "业务组件2的主界面"
)
class Component2Act : BaseAct() {

    @JvmField
    @FiledAutowiredAnno("name")
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_act)
        tv_data.text = ParameterSupport.getString(intent, "data")
    }

}
