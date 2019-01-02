package com.ehi.componentdemo;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.base.ModuleConfig;
import com.ehi.base.interceptor.CallPhoePermisionInterceptor;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterRequest;

public class CustomerRouterImpl {

    @Nullable
    @EHiRouterAnno(
            host = ModuleConfig.System.NAME, value = ModuleConfig.System.CALL_PHONE,
            interceptors = CallPhoePermisionInterceptor.class
    )
    public static Intent callPhoneIntent(@NonNull EHiRouterRequest request) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "15857913627"));
        return intent;
    }

    /**
     * 系统 App 详情
     *
     * @param request
     * @return
     */
    @EHiRouterAnno(host = ModuleConfig.System.NAME, value = ModuleConfig.System.SYSTEM_APP_DETAIL)
    public static Intent appDetail(EHiRouterRequest request) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + request.getRawContext().getPackageName()));
        return intent;
    }

}
