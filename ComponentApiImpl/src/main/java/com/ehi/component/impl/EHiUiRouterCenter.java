package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ehi.component.EHIComponentUtil;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.router.IComponentModuleRouter;

import java.util.HashMap;
import java.util.Map;

class EHiUiRouterCenter implements IComponentModuleRouter {

        private static String tag = "EHiUiRouterCenter";

        private static volatile EHiUiRouterCenter instance;

        private static Map<String, IComponentHostRouter> routerMap = new HashMap<>();

        private EHiUiRouterCenter() {
        }

        public static EHiUiRouterCenter getInstance() {
            if (instance == null) {
                synchronized (EHiUiRouterCenter.class) {
                    if (instance == null) {
                        instance = new EHiUiRouterCenter();
                    }
                }
            }
            return instance;
        }

        @Override
        public boolean fopenUri(@NonNull Fragment fragment, @NonNull Uri uri) {
            return fopenUri(fragment, uri, null);
        }

        @Override
        public boolean fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle) {
            return fopenUri(fragment, uri, null, null);
        }

        @Override
        public boolean fopenUri(@NonNull Fragment fragment, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) {

            for (Map.Entry<String, IComponentHostRouter> entry : routerMap.entrySet()) {
                if (entry.getValue().fopenUri(fragment, uri, bundle, requestCode)) {
                    return true;
                }
            }

            return false;
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

            for (Map.Entry<String, IComponentHostRouter> entry : routerMap.entrySet()) {
                if (entry.getValue().openUri(context, uri, bundle, requestCode)) {
                    return true;
                }
            }

            return false;

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
        public boolean isNeedLogin(@NonNull Uri uri) {

            for (String key : routerMap.keySet()) {

                IComponentHostRouter router = routerMap.get(key);

                if (router.isNeedLogin(uri)) {
                    return true;
                }

            }

            return false;
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

            String className = EHIComponentUtil.genHostUIRouterClassName(host);

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