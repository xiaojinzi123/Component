package com.xiaojinzi.component2.service

import android.app.Application
import android.widget.Toast
import com.xiaojinzi.base.service.inter.component2.Component2Service
import com.xiaojinzi.component.anno.ServiceAnno

/**
 * time   : 2018/12/06
 *
 * @author : xiaojinzi
 */
@ServiceAnno(value = [Component2Service::class], singleTon = false)
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
