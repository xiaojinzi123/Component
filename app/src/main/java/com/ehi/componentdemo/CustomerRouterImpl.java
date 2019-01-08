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

/**
 * @TODO: 下一步的计划, 路由的跳转完全可以自定义, 这样子就可以完美的融合之前写的那些 startActivity 了
 * @TODO: 跳转出错的时候,RxFragment有没有从列表中删除
 * 现在这个是鸡肋的
 */
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
    public static void appDetail(@NonNull EHiRouterRequest request) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + request.getRawContext().getPackageName()));
        if (request.requestCode == null) {
            if (request.getRawActivity() == null) {
                request.fragment.startActivity(intent);
            }else {
                request.getRawActivity().startActivity(intent);
            }
        } else {
            if (request.getRawActivity() == null) {
                request.fragment.startActivityForResult(intent, request.requestCode);
            }else {
                request.getRawActivity().startActivityForResult(intent, request.requestCode);
            }
        }
    }

}
