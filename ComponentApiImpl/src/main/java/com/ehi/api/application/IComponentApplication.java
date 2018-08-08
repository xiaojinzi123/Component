package com.ehi.api.application;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * 这是每一个模块中的生命周期类都需要实现的类
 * 如果这个类被移动了,一定要格外关注 {@link com.ehi.api.EHiConstants#EHIAPPLCATON} 的值
 */
public interface IComponentApplication {

    /**
     * 模块被加载的时候回被调用
     *
     * @param app
     */
    void onCreate(@NonNull Application app);

    /**
     * 模块被卸载的时候会被调用,该模块被卸载,对应的路由也会被卸载,也就是表现为跳转不过去了
     */
    void onDestory();

}
