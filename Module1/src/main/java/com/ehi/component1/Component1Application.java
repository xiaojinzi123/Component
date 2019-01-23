package com.ehi.component1;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@EHiModuleAppAnno()
public class Component1Application implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
        // 你可以做一些当前业务模块的一些初始化
    }

    @Override
    public void onDestory() {
        // 你可以销毁有关当前业务模块的东西
    }

}
