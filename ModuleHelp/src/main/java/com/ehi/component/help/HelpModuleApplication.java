package com.ehi.component.help;


import android.app.Application;
import android.support.annotation.NonNull;

import com.ehi.component.anno.EHiModuleAppAnno;
import com.ehi.component.application.IComponentApplication;

@EHiModuleAppAnno()
public class HelpModuleApplication implements IComponentApplication {

    @Override
    public void onCreate(@NonNull final Application app) {
    }

    @Override
    public void onDestory() {
    }

}
