package com.ehi.api.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ehi.api.EHIComponentUtil;
import com.ehi.api.router.IComponentHostRouter;
import com.ehi.api.router.IComponentModuleRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类必须放在 {@link com.ehi.api.EHIComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class EHiUiRouter {

    private static String tag = "EHiUiRouter";

    private static boolean isDebug = false;

    public static void init(boolean isDebug) {
        EHiUiRouter.isDebug = isDebug;
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public static void register(IComponentHostRouter router) {
        EHiUiRouterCenter.getInstance().register(router);
    }

    public static void register(@NonNull String host) {
        EHiUiRouterCenter.getInstance().register(host);
    }

    public static void unregister(IComponentHostRouter router) {
        EHiUiRouterCenter.getInstance().unregister(router);
    }

    public static void unregister(@NonNull String host) {
        EHiUiRouterCenter.getInstance().unregister(host);
    }

    public static class Builder {

        private Builder(@NonNull Context context) {
            this.context = context;
            checkNullPointer(context, "context");
        }

        @NonNull
        private Context context;
        private Integer requestCode;
        private String host;
        private String path;
        private Map<String, String> queryMap = new HashMap<>();

        private Bundle bundle = new Bundle();

        public Builder requestCode(@NonNull int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder host(@NonNull String host) {
            checkStringNullPointer(host, "host");
            this.host = host;
            return this;
        }

        public Builder path(@NonNull String path) {
            checkStringNullPointer(path, "path");
            this.path = path;
            return this;
        }

        public Builder bundle(@NonNull Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder query(@NonNull String queryName, @NonNull String queryValue) {
            checkStringNullPointer(queryName, "queryName");
            checkStringNullPointer(queryValue, "queryValue");
            queryMap.put(queryName, queryValue);
            return this;
        }

        private void checkStringNullPointer(String value, @NonNull String parameterName) {
            if (EHiUiRouter.isDebug && (value == null || "".equals(value))) {
                throw new NullPointerException("parameter '" + parameterName + "' can;t be null");
            }
        }

        private void checkNullPointer(Object value, @NonNull String parameterName) {
            if (EHiUiRouter.isDebug && value == null) {
                throw new NullPointerException("parameter '" + parameterName + "' can;t be null");
            }
        }

        public void navigate() {

            try {

            }catch (Exception e) {

            }

            Uri.Builder uriBuilder = new Uri.Builder();

            uriBuilder
                    .scheme("EHi")
                    .authority(host)
                    .path(path);

            for (Map.Entry<String, String> entry: queryMap.entrySet()) {
                uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            Uri uri = uriBuilder.build();

            EHiUiRouterCenter.getInstance().openUri(context, uri, bundle, requestCode);

        }

    }

    private static class EHiUiRouterCenter implements IComponentModuleRouter {

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
        public boolean openUri(@NonNull Context context, @NonNull Uri uri) {
            return openUri(context, uri, null);
        }

        @Override
        public boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle) {
            return openUri(context, uri, null, null);
        }

        @Override
        public boolean openUri(@NonNull Context context, @NonNull Uri uri, @Nullable Bundle bundle, @Nullable Integer requestCode) {

            String host = uri.getHost();

            if (!routerMap.containsKey(host)) {

                Log.e(tag, "the '" + host + "' module is not exist");

            }

            for (String key: routerMap.keySet()) {

                IComponentHostRouter router = routerMap.get(key);

                if (router.isMatchUri(uri)) {

                    router.openUri(context, uri, bundle, requestCode);

                }

            }

            return false;
        }

        @Override
        public boolean isMatchUri(@NonNull Uri uri) {

            for (String key: routerMap.keySet()) {

                IComponentHostRouter router = routerMap.get(key);

                if (router.isMatchUri(uri)) {
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
                System.out.println("qweqwe");
            } catch (InstantiationException e) {
            }

            return null;

        }

    }

}
