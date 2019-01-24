package com.ehi.component.impl.service;

import android.app.Application;
import com.ehi.base.service.inter.user.UserService;
import com.ehi.component.service.IServiceLoad;
import com.ehi.component.service.SingletonService;
import com.ehi.user.service.UserServiceImpl;
import java.lang.Override;
import java.lang.String;

final class UserServiceGenerated extends EHiMuduleServiceImpl {
    @Override
    public String getHost() {
        return "user";
    }

    @Override
    public void onCreate(final Application application) {
        super.onCreate(application);
        IServiceLoad implName1 = new SingletonService<UserServiceImpl>() {
            @Override
            protected UserServiceImpl getRaw() {
                return new UserServiceImpl();
            }
        };
        EHiService.register(UserService.class,implName1);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        EHiService.unregister(UserService.class);
    }
}
