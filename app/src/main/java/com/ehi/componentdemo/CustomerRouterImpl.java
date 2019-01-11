package com.ehi.componentdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouterRequest;

/**
 * @TODO: 下一步的计划, 路由的跳转完全可以自定义, 这样子就可以完美的融合之前写的那些 startActivity 了
 * @TODO: 跳转出错的时候,RxFragment有没有从列表中删除,出错的时候不会添加进去的,所以不会有问题
 */
public class CustomerRouterImpl {

    @Nullable
    @EHiRouterAnno(
            host = ModuleConfig.System.NAME, value = ModuleConfig.System.CALL_PHONE,
            interceptorNames = InterceptorConfig.HELP_CALLPHOEPERMISION
    )
    public static Intent callPhoneIntent(@NonNull EHiRouterRequest request) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "15857913627"));
        if (true) {
            throw new NullPointerException();
        }
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
        Activity act = request.getActivity();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + request.getRawContext().getPackageName()));
        if (request.requestCode == null) {
            if (act == null) {
                request.fragment.startActivity(intent);
            }else {
                act.startActivity(intent);
            }
        } else {
            if (act == null) {
                request.fragment.startActivityForResult(intent, request.requestCode);
            }else {
                act.startActivityForResult(intent, request.requestCode);
            }
        }
    }

}
