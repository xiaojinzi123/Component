package com.ehi.component2.service

import android.app.Application
import android.widget.Toast
import com.ehi.base.service.inter.component2.Component2Service
import com.ehi.component.anno.EHiServiceAnno

/**
 * time   : 2018/12/06
 *
 * @author : xiaojinzi 30212
 */
@EHiServiceAnno(value = [Component2Service::class], singleTon = false)
class Component2ServiceImpl : Component2Service {

    var app: Application;

    constructor(app: Application) {
        this.app = app
        Toast.makeText(app, "创建服务 Component2ServiceImpl ", Toast.LENGTH_SHORT).show();
    }

    override fun doSomeThing() {
        Toast.makeText(app, "调用了服务 Component2Service的 doSomeThing 方法", Toast.LENGTH_SHORT).show();
    }

}
