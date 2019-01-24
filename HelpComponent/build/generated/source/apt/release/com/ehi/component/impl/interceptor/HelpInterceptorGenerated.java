package com.ehi.component.impl.interceptor;

import android.support.annotation.Nullable;
import com.ehi.component.help.CallPhoePermisionInterceptor;
import java.lang.Override;
import java.lang.String;
import java.util.List;

final class HelpInterceptorGenerated extends EHiMuduleInterceptorImpl {
    @Override
    public String getHost() {
        return "help";
    }

    @Override
    @Nullable
    public List<EHiInterceptorBean> globalInterceptorList() {
        return null;
    }

    @Override
    protected void initInterceptorMap() {
        super.initInterceptorMap();
        interceptorMap.put("help.callPhoePermision", CallPhoePermisionInterceptor.class);
    }
}
