package com.ehi.component.impl.service;

import android.app.Application;
import com.ehi.base.service.inter.component1.Component1Service;
import com.ehi.component.service.IServiceLoad;
import com.ehi.component.service.SingletonService;
import com.ehi.component1.service.Component1ServiceImpl;
import java.lang.Override;
import java.lang.String;

final class Component1ServiceGenerated extends EHiMuduleServiceImpl {
    @Override
    public String getHost() {
        return "component1";
    }

    @Override
    public void onCreate(final Application application) {
        super.onCreate(application);
        IServiceLoad implName1 = new SingletonService<Component1ServiceImpl>() {
            @Override
            protected Component1ServiceImpl getRaw() {
                return new Component1ServiceImpl(application);
            }
        };
        EHiService.register(Component1Service.class,implName1);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        EHiService.unregister(Component1Service.class);
    }
}
