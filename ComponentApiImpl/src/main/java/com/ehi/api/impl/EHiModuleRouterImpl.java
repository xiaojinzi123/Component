package com.ehi.api.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ehi.api.EHIComponentUtil;
import com.ehi.api.router.IComponentHostRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果名称更改了,请配置到 {@link EHIComponentUtil#IMPL_OUTPUT_PKG} 和 {@link EHIComponentUtil#UIROUTER_IMPL_CLASS_NAME} 上
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
abstract class EHiModuleRouterImpl implements IComponentHostRouter {

    /**
     * 保存映射关系的map集合
     */
    protected Map<String, Class> routerMap = new HashMap<>();

    /**
     * 是否初始化了map,懒加载
     */
    protected boolean hasInitMap = false;

    /**
     * 上一次跳转的界面的Class
     */
    @Nullable
    private Class preTargetClass;

    /**
     * 记录上一个界面跳转的时间
     */
    private long preTargetTime;

    protected void initMap() {
        hasInitMap = true;
    }

    @Override
    public boolean openUri(@NonNull Context context, @NonNull Uri uri) {
        return openUri(context, uri, null);
    }

    @Override
    public boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle) {
        return openUri(context, uri, null, null);
    }

    @Override
    public boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) {

        if (!hasInitMap) {
            initMap();
        }

        Class targetClass = getTargetClass(uri);

        if (targetClass == null) {

            return false;

        }

        // 防止重复跳转同一个界面
        if (preTargetClass == targetClass && (System.currentTimeMillis() - preTargetTime) < 1000) { // 如果跳转的是同一个界面
            Log.d("UiRouter", "you can't launch same Activity '" + preTargetClass.getName() + " in one second");
            return false;
        }

        // 保存目前跳转过去的界面
        preTargetClass = targetClass;
        preTargetTime = System.currentTimeMillis();

        if (bundle == null) {
            bundle = new Bundle();
        }

        Intent intent = new Intent(context, targetClass);
        intent.putExtras(bundle);

        if (requestCode == null) {

            context.startActivity(intent);
            return true;

        } else {

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
                return true;
            }

        }

        return false;


    }

    @Override
    public boolean isMatchUri(@NonNull Uri uri) {

        if (!hasInitMap) {
            initMap();
        }

        boolean isHostEqual = getHost().equals(uri.getHost());

        if (!isHostEqual) {
            return false;
        }

        return getTargetClass(uri) == null ? false : true;

    }

    @Nullable
    private Class<?> getTargetClass(@NonNull Uri uri) {

        // "/component1/test"
        String targetPath = uri.getEncodedPath();

        Class targetClass = null;

        for (String key : routerMap.keySet()) {

            if (key == null || "".equals(key)) continue;

            if (key.charAt(0) == '/') {
                if (key.equals(targetPath)) {
                    targetClass = routerMap.get(key);
                    break;
                }
            } else {
                if (("/" + key).equals(targetPath)) {
                    targetClass = routerMap.get(key);
                    break;
                }
            }

        }
        return targetClass;

    }

}
