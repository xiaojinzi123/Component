package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.component.ComponentConfig;
import com.ehi.component.EHIComponentUtil;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.router.IComponentModuleRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类必须放在 {@link com.ehi.component.EHIComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class EHiUiRouter {

    private static String tag = "EHiUiRouter";

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

    public static Builder with(@NonNull Context context) {
        return new Builder(context, null);
    }

    public static boolean open(@NonNull Context context, @NonNull String url) {
        return new Builder(context, url).navigate();
    }

    public static boolean open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode) {
        return new Builder(context, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static class Builder {

        private Builder(@NonNull Context context, String url) {
            this.context = context;
            this.url = url;
            checkNullPointer(context, "context");
        }

        @NonNull
        private Context context;
        private String url;
        private Integer requestCode;
        private String host;
        private String path;
        private Map<String, String> queryMap = new HashMap<>();

        private Bundle bundle = new Bundle();

        public Builder requestCode(@Nullable Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder host(@NonNull String host) {
            checkStringNullPointer(host, "host","do you forget call host() to set host?");
            this.host = host;
            return this;
        }

        public Builder path(@NonNull String path) {
            checkStringNullPointer(path, "path","do you forget call path() to set path?");
            this.path = path;
            return this;
        }

        public Builder bundle(@NonNull Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder query(@NonNull String queryName, @Nullable String queryValue) {
            checkStringNullPointer(queryName, "queryName");
            if (queryValue == null) {
                queryValue = "";
            }
            queryMap.put(queryName, queryValue);
            return this;
        }

        public Builder query(@NonNull String queryName, boolean queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, byte queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, int queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, float queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, long queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, double queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        private static String checkStringNullPointer(String value, @NonNull String parameterName) {
            if (ComponentConfig.isDebug() && (value == null || "".equals(value))) {
                throw new NullPointerException("parameter '" + parameterName + "' can't be null");
            }
            return value;
        }

        private static String checkStringNullPointer(String value, @NonNull String parameterName, @Nullable String desc) {
            if (ComponentConfig.isDebug() && (value == null || "".equals(value))) {
                throw new NullPointerException("parameter '" + parameterName + "' can't be null" + (desc == null ? "" : "," + desc));
            }
            return value;
        }

        private static <T> T checkNullPointer(T value, @NonNull String parameterName) {
            if (ComponentConfig.isDebug() && value == null) {
                throw new NullPointerException("parameter '" + parameterName + "' can't be null");
            }
            return value;
        }

        public boolean navigate() {

            try {

                Uri uri = null;

                if (url == null) {

                    Uri.Builder uriBuilder = new Uri.Builder();

                    uriBuilder
                            .scheme("EHi")
                            .authority(checkStringNullPointer(host, "host","do you forget call host() to set host?"))
                            .path(checkStringNullPointer(path, "path","do you forget call path() to set path?"));

                    for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
                    }

                    uri = uriBuilder.build();

                } else {
                    uri = Uri.parse(url);
                }

                return EHiUiRouterCenter.getInstance().openUri(context, uri, bundle, requestCode);

            } catch (Exception ignore) {
                if (ComponentConfig.isDebug()) {
                    throw ignore;
                }
                return false;
            } finally {
                context = null;
                queryMap = null;
            }

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
