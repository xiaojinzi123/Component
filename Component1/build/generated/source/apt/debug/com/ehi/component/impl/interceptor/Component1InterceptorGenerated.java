package com.ehi.component.impl.interceptor;

import android.support.annotation.Nullable;
import com.ehi.component1.interceptor.Component1Interceptor1;
import com.ehi.component1.interceptor.Component1Interceptor2;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

final class Component1InterceptorGenerated extends EHiMuduleInterceptorImpl {
    @Override
    public String getHost() {
        return "component1";
    }

    @Override
    @Nullable
    public List<EHiInterceptorBean> globalInterceptorList() {
        List<EHiInterceptorBean> list = new ArrayList<>();
        list.add(new EHiInterceptorBean(EHiRouterInterceptorUtil.get(Component1Interceptor2.class),4));
        return list;
    }

    @Override
    protected void initInterceptorMap() {
        super.initInterceptorMap();
        interceptorMap.put("component1.test", Component1Interceptor1.class);
    }
}
