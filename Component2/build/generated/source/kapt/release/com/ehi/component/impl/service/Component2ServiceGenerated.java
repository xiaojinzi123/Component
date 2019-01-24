package com.ehi.component.impl.service;

import android.app.Application;
import com.ehi.base.service.inter.component2.Component2Service;
import com.ehi.component.service.IServiceLoad;
import com.ehi.component2.service.Component2ServiceImpl;
import java.lang.Override;
import java.lang.String;

final class Component2ServiceGenerated extends EHiMuduleServiceImpl {
    @Override
    public String getHost() {
        return "component2";
    }

    @Override
    public void onCreate(final Application application) {
        super.onCreate(application);
        IServiceLoad implName1 = new IServiceLoad<Component2ServiceImpl>() {
            @Override
            public Component2ServiceImpl get() {
                return new Component2ServiceImpl(application);
            }
        };
        EHiService.register(Component2Service.class,implName1);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        EHiService.unregister(Component2Service.class);
    }
}
