package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.error.NavigationFailException;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.support.EHiErrorRouterInterceptor;
import com.ehi.component.support.EHiRouterInterceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个类必须放在 {@link ComponentUtil#IMPL_OUTPUT_PKG} 包下面
 * 这个类作为框架对外的一个使用的类,里面会很多易用的方法
 * <p>
 * time   : 2018/07/26
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouter {

    /**
     * 类的标志
     */
    private static String TAG = "EHiRouter";

    static Collection<EHiRouterInterceptor> routerInterceptors = Collections.synchronizedCollection(new ArrayList<EHiRouterInterceptor>(0));

    static Collection<EHiErrorRouterInterceptor> errorRouterInterceptors = Collections.synchronizedCollection(new ArrayList<EHiErrorRouterInterceptor>(0));

    public static void clearUiRouterInterceptor() {
        routerInterceptors.clear();
    }

    public static void addRouterInterceptor(@NonNull EHiRouterInterceptor interceptor) {
        if (routerInterceptors.contains(interceptor)) {
            return;
        }
        routerInterceptors.add(interceptor);
    }

    public static void clearErrorRouterInterceptor() {
        errorRouterInterceptors.clear();
    }

    public static void addErrorRouterInterceptor(@NonNull EHiErrorRouterInterceptor interceptor) {

        if (errorRouterInterceptors.contains(interceptor)) {
            return;
        }
        errorRouterInterceptors.add(interceptor);

    }

    public static void register(IComponentHostRouter router) {
        RouterCenter.getInstance().register(router);
    }

    public static void register(@NonNull String host) {
        RouterCenter.getInstance().register(host);
    }

    public static void unregister(IComponentHostRouter router) {
        RouterCenter.getInstance().unregister(router);
    }

    public static void unregister(@NonNull String host) {
        RouterCenter.getInstance().unregister(host);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context, null);
    }

    public static Builder withFragment(@NonNull Fragment fragment) {
        return new Builder(fragment, null);
    }

    public static EHiRouterResult open(@NonNull Context context, @NonNull String url) {
        return new Builder(context, url).navigate();
    }

    public static EHiRouterResult open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode) {
        return new Builder(context, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static EHiRouterResult open(@NonNull Context context, @NonNull String url, @Nullable Bundle bundle) {
        return new Builder(context, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .navigate();
    }

    public static EHiRouterResult open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode, @Nullable Bundle bundle) {
        return new Builder(context, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .requestCode(requestCode)
                .navigate();
    }

    public static EHiRouterResult fopen(@NonNull Fragment fragment, @NonNull String url) {
        return new Builder(fragment, url).navigate();
    }

    public static EHiRouterResult fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Integer requestCode) {
        return new Builder(fragment, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static EHiRouterResult fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Bundle bundle) {
        return new Builder(fragment, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .navigate();
    }

    public static EHiRouterResult fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Bundle bundle, @Nullable Integer requestCode) {
        return new Builder(fragment, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .requestCode(requestCode)
                .navigate();
    }

    public static boolean isMatchUri(@NonNull Uri uri) {
        return RouterCenter.getInstance().isMatchUri(uri);
    }

    public static boolean isNeedLogin(@NonNull Uri uri) {
        return RouterCenter.getInstance().isNeedLogin(uri);
    }

    public static class Builder {

        protected Builder(@NonNull Context context, String url) {
            this.context = context;
            this.url = url;
            checkNullPointer(context, "context");
        }

        protected Builder(@NonNull Fragment fragment, String url) {
            this.fragment = fragment;
            this.url = url;
            checkNullPointer(fragment, "fragment");
        }

        @Nullable
        protected Context context;

        @Nullable
        protected Fragment fragment;

        @Nullable
        protected String url;

        @NonNull
        protected String host;

        @NonNull
        protected String path;

        @Nullable
        protected Integer requestCode;

        @NonNull
        protected Map<String, String> queryMap = new HashMap<>();

        @NonNull
        protected Bundle bundle = new Bundle();

        @Nullable
        private EHiRouterInterceptor onceInterceptor = null;

        /**
         * 标记这个 builder 是否已经被使用了,使用过了就不能使用了
         */
        protected boolean isFinish = false;

        /**
         * 拦截单个 路由的
         *
         * @param onceInterceptor
         * @return
         */
        public Builder doOnInterceptor(@Nullable EHiRouterInterceptor onceInterceptor) {
            this.onceInterceptor = onceInterceptor;
            return this;
        }

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

        public Builder putBundle(@NonNull String key, @NonNull Bundle bundle) {
            this.bundle.putBundle(key, bundle);
            return this;
        }

        public Builder putAll(@NonNull Bundle bundle) {
            this.bundle.putAll(bundle);
            return this;
        }

        /*public Builder putAll(@NonNull PersistableBundle bundle) {
            this.bundle.putAll(bundle);
            return this;
        }*/

        public Builder putCharSequence(@NonNull String key, @Nullable CharSequence value) {
            this.bundle.putCharSequence(key, value);
            return this;
        }

        public Builder putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
            this.bundle.putCharSequenceArray(key, value);
            return this;
        }

        public Builder putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
            this.bundle.putCharSequenceArrayList(key, value);
            return this;
        }

        public Builder putByte(@NonNull String key, @Nullable byte value) {
            this.bundle.putByte(key, value);
            return this;
        }

        public Builder putByteArray(@NonNull String key, @Nullable byte[] value) {
            this.bundle.putByteArray(key, value);
            return this;
        }

        public Builder putChar(@NonNull String key, @Nullable char value) {
            this.bundle.putChar(key, value);
            return this;
        }

        public Builder putCharArray(@NonNull String key, @Nullable char[] value) {
            this.bundle.putCharArray(key, value);
            return this;
        }

        public Builder putBoolean(@NonNull String key, @Nullable boolean value) {
            this.bundle.putBoolean(key, value);
            return this;
        }

        public Builder putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
            this.bundle.putBooleanArray(key, value);
            return this;
        }

        public Builder putString(@NonNull String key, @Nullable String value) {
            this.bundle.putString(key, value);
            return this;
        }

        public Builder putStringArray(@NonNull String key, @Nullable String[] value) {
            this.bundle.putStringArray(key, value);
            return this;
        }

        public Builder putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
            this.bundle.putStringArrayList(key, value);
            return this;
        }

        public Builder putShort(@NonNull String key, @Nullable short value) {
            this.bundle.putShort(key, value);
            return this;
        }

        public Builder putShortArray(@NonNull String key, @Nullable short[] value) {
            this.bundle.putShortArray(key, value);
            return this;
        }

        public Builder putInt(@NonNull String key, @Nullable int value) {
            this.bundle.putInt(key, value);
            return this;
        }

        public Builder putIntArray(@NonNull String key, @Nullable int[] value) {
            this.bundle.putIntArray(key, value);
            return this;
        }

        public Builder putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
            this.bundle.putIntegerArrayList(key, value);
            return this;
        }

        public Builder putLong(@NonNull String key, @Nullable long value) {
            this.bundle.putLong(key, value);
            return this;
        }

        public Builder putLongArray(@NonNull String key, @Nullable long[] value) {
            this.bundle.putLongArray(key, value);
            return this;
        }

        public Builder putFloat(@NonNull String key, @Nullable float value) {
            this.bundle.putFloat(key, value);
            return this;
        }

        public Builder putFloatArray(@NonNull String key, @Nullable float[] value) {
            this.bundle.putFloatArray(key, value);
            return this;
        }

        public Builder putDouble(@NonNull String key, @Nullable double value) {
            this.bundle.putDouble(key, value);
            return this;
        }

        public Builder putDoubleArray(@NonNull String key, @Nullable double[] value) {
            this.bundle.putDoubleArray(key, value);
            return this;
        }

        public Builder putParcelable(@NonNull String key, @Nullable Parcelable value) {
            this.bundle.putParcelable(key, value);
            return this;
        }

        public Builder putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
            this.bundle.putParcelableArray(key, value);
            return this;
        }

        public Builder putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
            this.bundle.putParcelableArrayList(key, value);
            return this;
        }

        public Builder putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
            this.bundle.putSparseParcelableArray(key, value);
            return this;
        }

        public Builder putSerializable(@NonNull String key, @Nullable Serializable value) {
            this.bundle.putSerializable(key, value);
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

        protected static String checkStringNullPointer(String value, @NonNull String parameterName) {
            if (ComponentConfig.isDebug() && (value == null || "".equals(value))) {
                throw new NullPointerException("parameter '" + parameterName + "' can't be null");
            }
            return value;
        }

        protected static String checkStringNullPointer(String value, @NonNull String parameterName, @Nullable String desc) {
            if (ComponentConfig.isDebug() && (value == null || "".equals(value))) {
                throw new NullPointerException("parameter '" + parameterName + "' can't be null" + (desc == null ? "" : "," + desc));
            }
            return value;
        }

        protected static <T> T checkNullPointer(T value, @NonNull String parameterName) {
            if (ComponentConfig.isDebug() && value == null) {
                throw new NullPointerException("parameter '" + parameterName + "' can't be null");
            }
            return value;
        }

        @NonNull
        protected EHiRouterRequest generateRouterRequest() throws Exception {

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

            EHiRouterRequest holder = new EHiRouterRequest.Builder()
                    .context(context)
                    .fragment(fragment)
                    .uri(uri)
                    .requestCode(requestCode)
                    .bundle(bundle)
                    .build();

            return holder;

        }

        /**
         * 执行跳转的具体逻辑
         *
         * @return
         */
        public synchronized EHiRouterResult navigate() {

            if (isFinish) {
                return EHiRouterResult.error(new NavigationFailException("EHiRouter.Builder can't be used multiple times"));
            }

            try {

                // 创建请求对象
                EHiRouterRequest routerRequest = generateRouterRequest();

                List<EHiRouterInterceptor> interceptors = new ArrayList();
                interceptors.addAll(routerInterceptors);
                interceptors.add(new RealInterceptor());

                EHiRouterInterceptor.Chain chain = new InterceptorChain(interceptors, 0, routerRequest);
                EHiRouterResult routerResult = chain.proceed(routerRequest);

                return routerResult;

            } catch (Exception e) { // 发生路由错误的时候

                for (EHiErrorRouterInterceptor interceptor : errorRouterInterceptors) {
                    try {
                        interceptor.onRouterError(e);
                    } catch (Exception ignore) {
                        // do nothing
                    }
                }

                return EHiRouterResult.error(e);

            } finally {

                // 释放资源
                url = null;
                host = null;
                path = null;
                requestCode = null;
                context = null;
                fragment = null;
                queryMap = null;
                bundle = null;
                isFinish = true;

            }


        }

        private class RealInterceptor implements EHiRouterInterceptor {

            @Override
            public EHiRouterResult intercept(Chain chain) throws Exception {
                RouterCenter.getInstance().openUri(chain.request());
                return EHiRouterResult.success(chain.request());
            }

        }

        private class InterceptorChain implements EHiRouterInterceptor.Chain {

            @NonNull
            private EHiRouterRequest originalRequest;
            private List<EHiRouterInterceptor> interceptors;
            private int index;
            private int calls;

            /**
             * @param interceptors
             * @param index
             * @param request      第一次这个对象是不需要的
             */
            public InterceptorChain(@NonNull List<EHiRouterInterceptor> interceptors, int index, @NonNull EHiRouterRequest request) {
                this.interceptors = interceptors;
                this.index = index;
                this.originalRequest = request;
            }

            @Override
            public EHiRouterRequest request() {
                // 第一个拦截器的
                return originalRequest;
            }

            @Override
            public EHiRouterResult proceed(EHiRouterRequest request) throws Exception {

                ++calls;
                if (this.index >= this.interceptors.size()) {
                    throw new NavigationFailException(new IndexOutOfBoundsException("size = " + this.interceptors.size() + ",index = " + index));
                } else if (calls > 1) { // 调用了两次
                    throw new NavigationFailException("interceptor " + this.interceptors.get(this.index - 1) + " must call proceed() exactly once");
                } else {
                    // 当拦截器最后一个的时候,就不是这个类了,是 RealInterceptor 了
                    InterceptorChain next = new InterceptorChain(this.interceptors, this.index + 1, request);
                    // current Interceptor
                    EHiRouterInterceptor interceptor = this.interceptors.get(this.index);
                    EHiRouterResult response = interceptor.intercept(next);
                    if (response == null) {
                        throw new NavigationFailException("call interceptor " + next + "' method intercept return null ");
                    }
                    return response;
                }

            }

        }

    }

}
