package com.ehi.component.help;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.ModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@ModuleAppAnno()
public class HelpModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
