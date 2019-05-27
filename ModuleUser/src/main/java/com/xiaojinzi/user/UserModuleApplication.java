package com.xiaojinzi.user;


import android.app.Application;
import androidx.annotation.NonNull;

import com.xiaojinzi.component.anno.ModuleAppAnno;
import com.xiaojinzi.component.application.IComponentApplication;

@ModuleAppAnno()
public class UserModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
