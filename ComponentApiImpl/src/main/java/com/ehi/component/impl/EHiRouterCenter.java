package com.ehi.component.impl;

import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentUtil;
import com.ehi.component.error.TargetActivityNotFoundException;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.router.IComponentModuleRouter;
import com.ehi.component.support.EHiRouterInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EHiRouterCenter implements IComponentModuleRouter {

    private static final String TAG = "EHiRouterCenter";

    private static volatile EHiRouterCenter instance;

    private static Map<String, IComponentHostRouter> routerMap = new HashMap<>();

    private EHiRouterCenter() {
    }

    public static EHiRouterCenter getInstance() {
        if (instance == null) {
            synchronized (EHiRouterCenter.class) {
                if (instance == null) {
                    instance = new EHiRouterCenter();
                }
            }
        }
        return instance;
    }

    @Override
    @MainThread
    public void openUri(@NonNull EHiRouterRequest routerRequest) throws Exception {
        for (Map.Entry<String, IComponentHostRouter> entry : routerMap.entrySet()) {
            if (entry.getValue().isMatchUri(routerRequest.uri)) {
                entry.getValue().openUri(routerRequest);
                return;
            }
        }
        throw new TargetActivityNotFoundException(routerRequest.uri == null ? "" : routerRequest.uri.toString());
    }

    @Override
    public synchronized boolean isMatchUri(@NonNull Uri uri) {
        for (String key : routerMap.keySet()) {
            IComponentHostRouter router = routerMap.get(key);
            if (router.isMatchUri(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized List<EHiRouterInterceptor> interceptors(@NonNull Uri uri) {
        for (String key : routerMap.keySet()) {
            // 每一个子路由
            IComponentHostRouter router = routerMap.get(key);
            if (router.isMatchUri(uri)) {
                return router.interceptors(uri);
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