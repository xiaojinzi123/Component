package com.xiaojinzi.componentdemo;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.service.inter.app.AnnoMethodService;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.ServiceAnno;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.support.ParameterSupport;

import java.lang.reflect.Proxy;

/**
 * 自定义路由实现的范例
 */
public class CustomerRouterImpl {

    /**
     * 自定义实现跳转到打电话的界面,并且自动完成打电话权限的申请
     */
    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.CALL_PHONE,
            interceptorNames = InterceptorConfig.HELP_CALLPHOEPERMISION
    )
    @NonNull
    public static Intent callPhoneIntent(@NonNull RouterRequest request, @NonNull RouterRequest request1) {
        String tel = ParameterSupport.getString(request.bundle, "tel");
        if (TextUtils.isEmpty(tel)) {
            throw new NullPointerException("the tel is empty");
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
        return intent;
    }

    /**
     * 拍照界面,需要拍照权限
     *
     * @param request
     * @return
     */
    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TAKE_PHONE,
            interceptorNames = InterceptorConfig.HELP_CAMERAPERMISION
    )
    @NonNull
    public static Intent takePictureIntent(@NonNull RouterRequest request) {
        Intent intent = new Intent();
        // 指定开启系统相机的Action
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    /**
     * 系统 App 详情
     *
     * @param request
     * @return
     */
    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.SYSTEM_APP_DETAIL
    )
    @NonNull
    public static Intent appDetail(@NonNull RouterRequest request) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + request.getRawContext().getPackageName()));
        return intent;
    }

    @ServiceAnno(value = AnnoMethodService.class)
    public static AnnoMethodService getTestService() {
        return (AnnoMethodService) Proxy.newProxyInstance(
                AnnoMethodService.class.getClassLoader(),
                new Class[]{AnnoMethodService.class},
                (proxy, method, args) -> "hello msg from proxy class"
        );
    }

    @ServiceAnno(value = AnnoMethodService.class, name = "service1")
    public static AnnoMethodService getTestService1(@NonNull Application application) {
        return (AnnoMethodService) Proxy.newProxyInstance(
                AnnoMethodService.class.getClassLoader(),
                new Class[]{AnnoMethodService.class},
                (proxy, method, args) -> "hello msg from proxy class"
        );
    }

}
