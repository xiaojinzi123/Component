package com.xiaojinzi.component2.view

import android.os.Bundle
import com.xiaojinzi.base.ModuleConfig
import com.xiaojinzi.base.bean.UserWithParcelable
import com.xiaojinzi.base.view.BaseAct
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component2.R
import kotlinx.android.synthetic.main.component2_act.*

@RouterAnno(
        path = ModuleConfig.Module2.MAIN,
        desc = "业务组件2的主界面"
)
class Component2Act : BaseAct() {

    @AttrValueAutowiredAnno("isVip")
    var isVip: Boolean? = null

    @AttrValueAutowiredAnno("IsVip1")
    var IsVip1: Boolean? = null

    @AttrValueAutowiredAnno("iSVip2")
    var iSVip2: Boolean? = null

    @AttrValueAutowiredAnno("hasOne")
    var hasOne: Boolean? = null

    @AttrValueAutowiredAnno("name")
    var name: String? = null

    @AttrValueAutowiredAnno("data")
    var data: String? = null

    @AttrValueAutowiredAnno(value = ["test1", "test2"])
    var test_1: String? = null

    @AttrValueAutowiredAnno("test2")
    var test_2: Array<String>? = null

    @AttrValueAutowiredAnno("test3")
    var test_3: ArrayList<String>? = null

    @AttrValueAutowiredAnno("test4")
    var test_4: ArrayList<UserWithParcelable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_act)
        Component.inject(this);
        tv_data.text = data;

        val fragment = Router
                .with(ModuleConfig.Module1.TEST_FRAGMENT)
                .navigate()

        Router
                .with(this)
                .host(ModuleConfig.Module2.NAME)
                .path(ModuleConfig.Module2.MAIN)
                .forward()

        Router.with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .forward()

    }

}
