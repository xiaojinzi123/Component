package com.ehi.component.service;

import android.app.Application;

public interface IComponentService {

    /**
     * 加载当前模块的所有服务
     *
     * @param app
     */
    void onCreate(Application app);

    /**
     * 销毁当前模块的所有服务对象
     */
    void onDestory();

}
