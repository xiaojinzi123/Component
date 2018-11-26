package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ehi.component.ComponentUtil;
import com.ehi.component.error.TargetActivityNotFoundException;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.router.IComponentModuleRouter;

import java.util.HashMap;
import java.util.Map;

class RouterCenter implements IComponentModuleRouter {

    private static final String TAG = "RouterCenter";

    private static volatile RouterCenter instance;

    private static Map<String, IComponentHostRouter> routerMap = new HashMap<>();

    private RouterCenter() {
    }

    public static RouterCenter getInstance() {
        if (instance == null) {
            synchronized (RouterCenter.class) {
                if (instance == null) {
                    instance = new RouterCenter();
                }
            }
        }
        return instance;
    }

    @Override
    public void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri) throws Exception {
        fopenUri(fragment, uri, null);
    }

    @Override
    public void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle) throws Exception {
        fopenUri(fragment, uri, null, null, null);
    }

    @Override
    public void fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle
            , @Nullable Integer requestCode, @Nullable HashMap helpMap) throws Exception {

        for (Map.Entry<String, IComponentHostRouter> entry : routerMap.entrySet()) {
            if (entry.getValue().isMatchUri(uri)) {
                entry.getValue().fopenUri(fragment, uri, bundle, requestCode, helpMap);
                return;
            }
        }

        throw new TargetActivityNotFoundException(uri.toString());

    }

    @Override
    public void openUri(@NonNull Context context, @NonNull Uri uri) throws Exception {
        openUri(context, uri, null);
    }

    @Override
    public void openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle) throws Exception {
        openUri(context, uri, null, null, null);
    }

    @Override
    public void openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle
            , @Nullable Integer requestCode, @Nullable HashMap helpMap) throws Exception {

        for (Map.Entry<String, IComponentHostRouter> entry : routerMap.entrySet()) {
            if (entry.getValue().isMatchUri(uri)) {
                entry.getValue().openUri(context, uri, bundle, requestCode, helpMap);
                return;
            }
        }

        throw new TargetActivityNotFoundException(uri.toString());

    }

    @Override
    public boolean isMatchUri(@NonNull Uri uri) {

        for (String key : routerMap.keySet()) {

            IComponentHostRouter router = routerMap.get(key);

            if (router.isMatchUri(uri)) {
                return true;
            }

        }

        return false;

    }

    @Override
    public Boolean isNeedLogin(@NonNull Uri uri) {

        for (String key : routerMap.keySet()) {

            // 每一个子路由
            IComponentHostRouter router = routerMap.get(key);

            Boolean isNeedLogin = null;

            if ((isNeedLogin = router.isNeedLogin(uri)) != null) {
                return isNeedLogin;
            }

        }

        return null;
    }

    @Override
    public void register(@NonNull IComponentHostRouter router) {

        if (router == null) {
            return;
        }

        routerMap.put(router.getHost(), router);

    }

    @Override
    public void register(@NonNull String host) {
        IComponentHostRouter router = findUiRouter(host);
        register(router);
    }

    @Override
    public void unregister(IComponentHostRouter router) {
        routerMap.remove(router.getHost());
    }

    @Override
    public void unregister(@NonNull String host) {
        routerMap.remove(host);
    }

    @Nullable
    private IComponentHostRouter findUiRouter(String host) {

        String className = ComponentUtil.genHostUIRouterClassName(host);

        try {
            Class<?> clazz = Class.forName(className);

            IComponentHostRouter instance = (IComponentHostRouter) clazz.newInstance();

            return instance;

        } catch (ClassNotFoundException e) {
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }

        return null;

    }

}