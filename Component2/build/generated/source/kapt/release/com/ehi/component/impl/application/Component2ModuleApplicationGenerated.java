package com.ehi.component.impl.application;

import android.app.Application;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.interceptor.EHiCenterInterceptor;
import com.ehi.component.impl.service.EHiCenterService;
import com.ehi.component2.Component2Application;
import java.lang.Override;
import java.lang.String;

final class Component2ModuleApplicationGenerated extends EHiModuleApplicationImpl {
    @Override
    public String getHost() {
        return "component2";
    }

    @Override
    public void initList() {
        super.initList();
        moduleAppList.add(new Component2Application());
    }

    @Override
    public void onCreate(Application application) {
        super.onCreate(application);
        EHiRouter.register(getHost());
        EHiCenterService.getInstance().register(getHost());
        EHiCenterInterceptor.getInstance().register(getHost());
    }

    @Override
    public void onDestory() {
        super.onDestory();
        EHiRouter.unregister(getHost());
        EHiCenterService.getInstance().unregister(getHost());
        EHiCenterInterceptor.getInstance().unregister(getHost());
    }
}
