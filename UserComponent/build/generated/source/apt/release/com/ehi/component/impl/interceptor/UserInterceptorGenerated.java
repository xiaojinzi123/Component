package com.ehi.component.impl.interceptor;

import android.support.annotation.Nullable;
import com.ehi.user.interceptor.UserLoginInterceptor;
import java.lang.Override;
import java.lang.String;
import java.util.List;

final class UserInterceptorGenerated extends EHiMuduleInterceptorImpl {
    @Override
    public String getHost() {
        return "user";
    }

    @Override
    @Nullable
    public List<EHiInterceptorBean> globalInterceptorList() {
        return null;
    }

    @Override
    protected void initInterceptorMap() {
        super.initInterceptorMap();
        interceptorMap.put("user.login", UserLoginInterceptor.class);
    }
}
