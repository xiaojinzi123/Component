package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ehi.component.ComponentConfig;
import com.ehi.component.EHIComponentUtil;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.router.IComponentModuleRouter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    static Collection<EHiUiRouterInterceptor> uiRouterInterceptors = Collections.synchronizedCollection(new ArrayList<EHiUiRouterInterceptor>(0));

    public static void clearUiRouterInterceptor() {
        uiRouterInterceptors.clear();
    }

    public static void addUiRouterInterceptor(@NonNull EHiUiRouterInterceptor interceptor) {

        if (uiRouterInterceptors.contains(interceptor)) {
            return;
        }
        uiRouterInterceptors.add(interceptor);

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

    public static Builder with(@NonNull Context context) {
        return new Builder(context, null);
    }

    public static Builder withFragment(@NonNull Fragment fragment) {
        return new Builder(fragment, null);
    }

    public static boolean open(@NonNull Context context, @NonNull String url) {
        return new Builder(context, url).navigate();
    }

    public static boolean open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode) {
        return new Builder(context, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static boolean open(@NonNull Context context, @NonNull String url, @Nullable Bundle bundle) {
        return new Builder(context, url)
                .bundle(bundle == null ? new Bundle() : bundle)
                .navigate();
    }

    public static boolean open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode, @Nullable Bundle bundle) {
        return new Builder(context, url)
                .bundle(bundle == null ? new Bundle() : bundle)
                .requestCode(requestCode)
                .navigate();
    }

    public static boolean fopen(@NonNull Fragment fragment, @NonNull String url) {
        return new Builder(fragment, url).navigate();
    }

    public static boolean fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Integer requestCode) {
        return new Builder(fragment, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static boolean fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Bundle bundle) {
        return new Builder(fragment, url)
                .bundle(bundle == null ? new Bundle() : bundle)
                .navigate();
    }
    public static boolean fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Bundle bundle, @Nullable Integer requestCode) {
        return new Builder(fragment, url)
                .bundle(bundle == null ? new Bundle() : bundle)
                .requestCode(requestCode)
                .navigate();
    }

    public static boolean isMatchUri(@NonNull Uri uri) {
        return EHiUiRouterCenter.instance.isMatchUri(uri);
    }

    public static boolean isNeedLogin(@NonNull Uri uri) {
        return EHiUiRouterCenter.instance.isNeedLogin(uri);
    }

    public static class Builder {

        private Builder(@NonNull Context context, String url) {
            this.context = context;
            this.url = url;
            checkNullPointer(context, "context");
        }

        private Builder(@NonNull Fragment fragment, String url) {
            this.fragment = fragment;
            this.url = url;
            checkNullPointer(fragment, "fragment");
        }

        @Nullable
        private Context context;

        @Nullable
        private Fragment fragment;

        private String url;

        @NonNull
        private String host;

        @NonNull
        private String path;

        @Nullable
        private Integer requestCode;

        @NonNull
        private Map<String, String> queryMap = new HashMap<>();

        @NonNull
        private Bundle bundle = new Bundle();

        public Builder requestCode(@Nullable Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder host(@NonNull String host) {
            checkStringNullPointer(host, "host", "do you forget call host() to set host?");
            this.host = host;
            return this;
        }

        public Builder path(@NonNull String path) {
            checkStringNullPointer(path, "path", "do you forget call path() to set path?");
            this.path = path;
            return this;
        }

        public Builder bundle(@NonNull Bundle bundle) {
            if (bundle != null) {
                this.bundle = bundle;
            }
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
                            .authority(checkStringNullPointer(host, "host", "do you forget call host() to set host?"))
                            .path(checkStringNullPointer(path, "path", "do you forget call path() to set path?"));

                    for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
                    }

                    uri = uriBuilder.build();

                } else {
                    uri = Uri.parse(url);
                }

                RouterHolder holder = new RouterHolder();

                holder.context = context;
                holder.fragment = fragment;
                holder.uri = uri;
                holder.requestCode = requestCode;
                holder.queryMap = queryMap;
                holder.bundle = bundle;

                for (EHiUiRouterInterceptor interceptor : uiRouterInterceptors) {
                    interceptor.preIntercept(holder);
                }

                if (holder.context == null) {
                    return EHiUiRouterCenter.getInstance().fopenUri(holder.fragment, holder.uri, holder.bundle, holder.requestCode);
                } else {
                    return EHiUiRouterCenter.getInstance().openUri(holder.context, holder.uri, holder.bundle, holder.requestCode);
                }

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

    public static class RouterHolder {

        @Nullable
        public Context context;

        @Nullable
        public Fragment fragment;

        @NonNull
        public Uri uri;

        @Nullable
        public Integer requestCode;

        @NonNull
        public Map<String, String> queryMap = new HashMap<>();

        @NonNull
        public Bundle bundle = new Bundle();

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
                System.out.println("qweqwe");
            } catch (InstantiationException e) {
            }

            return null;

        }

    }

    public interface EHiUiRouterPreInterceptor {


    }

    /**
     * 路由跳转的拦截器
     */
    public interface EHiUiRouterInterceptor {

        /**
         * 路由之前的数据拦截
         *
         * @param holder
         * @return
         */
        void preIntercept(@NonNull RouterHolder holder);

        /**
         * 路由跳转的拦截器的实现,最后执行前的拦截
         *
         * @param uri
         * @return
         */
        boolean intercept(
                @Nullable Context context, @Nullable Fragment fragment, @NonNull Uri uri,
                @NonNull Class targetActivityClass, @Nullable Bundle bundle,
                @Nullable Integer requestCode, boolean isNeedLogin
        );

    }

}
