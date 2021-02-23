package com.xiaojinzi.componentdemo;

import android.app.Application;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.Config;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.LogUtil;
import com.xiaojinzi.component.support.RxErrorIgnoreUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        long startTime = System.currentTimeMillis();
        // 初始化组件化相关
        Component.init(
                BuildConfig.DEBUG,
                Config.with(this)
                        .defaultScheme("router")
                        // 使用内置的路由重复检查的拦截器, 如果为 true,
                        // 那么当两个相同的路由发生在指定的时间内后一个路由就会被拦截
                        .useRouteRepeatCheckInterceptor(true)
                        // 1000 是默认的, 表示相同路由拦截的时间间隔
                        .routeRepeatCheckDuration(1000)
                        // 是否打印日志提醒你哪些路由使用了 Application 为 Context 进行跳转
                        .tipWhenUseApplication(true)
                        // 通知模块变化的延迟时间间隔
                        .notifyModuleChangedDelayTime(200)
                        // 开启启动优化, 必须配套使用 Gradle 插件
                        .optimizeInit(true)
                        // 自动加载所有模块, 依赖上面的 optimizeInit(true)
                        .autoRegisterModule(true)
                        // demo 测试, 线上并没有, 请勿配置
                        // .objectToJsonConverter(obj -> new Gson().toJson(obj))
                        // 执行构建
                        .build()
        );
        long endTime = System.currentTimeMillis();
        LogUtil.log("---------------------------------耗时：" + (endTime - startTime));
        // 如果你依赖了 rx 版本, 请加上这句配置. 忽略一些不想处理的错误
        // 如果不是 rx 的版本, 请忽略
        RxErrorIgnoreUtil.ignoreError();
        Component.check();
    }

}
