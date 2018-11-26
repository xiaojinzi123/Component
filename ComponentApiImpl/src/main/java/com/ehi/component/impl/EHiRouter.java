package com.ehi.component.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.error.NavigationFailException;
import com.ehi.component.router.IComponentHostRouter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

    static Collection<EHiUiRouterInterceptor> uiRouterInterceptors = Collections.synchronizedCollection(new ArrayList<EHiUiRouterInterceptor>(0));

    static Collection<EHiErrorRouterInterceptor> errorRouterInterceptors = Collections.synchronizedCollection(new ArrayList<EHiErrorRouterInterceptor>(0));

    public static void clearUiRouterInterceptor() {
        uiRouterInterceptors.clear();
    }

    public static void addUiRouterInterceptor(@NonNull EHiUiRouterInterceptor interceptor) {

        if (uiRouterInterceptors.contains(interceptor)) {
            return;
        }
        uiRouterInterceptors.add(interceptor);

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
        return EHiUiRouterCenter.getInstance().isMatchUri(uri);
    }

    public static boolean isNeedLogin(@NonNull Uri uri) {
        return EHiUiRouterCenter.getInstance().isNeedLogin(uri);
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

        /**
         * 为什么会有这个东西,因为可能有时候我们传给实现的那边的东西有点多,而我们最好控制一些参数
         * 所以用这个对象来存储要传过去的额外的对象和数据
         */
        @Nullable
        private HashMap<String, Object> helpMap = null;

        /**
         * 标记这个 builder 是否已经被使用了,使用过了就不能使用了
         */
        protected boolean isFinish = false;

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
        protected RouterHolder generateHolder() throws Exception {

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
            holder.bundle = bundle;

            return holder;

        }

        @MainThread
        protected void addExtraInfo(@NonNull String key, @NonNull Object o) {
            if (helpMap == null) {
                helpMap = new HashMap();
            }
            helpMap.put(key, o);
        }

        public synchronized EHiRouterResult navigate() {
            return navigate(false);
        }

        /**
         * 执行跳转的具体逻辑
         *
         * @param isUsebuiltInFragment 是否使用 Context 或者 Fragment 内置的 Fragment 跳转
         *                             这个 Fragment 的 TAG 为 {@link ComponentUtil#FRAGMENT_TAG}
         * @return
         */
        protected synchronized EHiRouterResult navigate(boolean isUsebuiltInFragment) {

            if (isFinish) {
                return EHiRouterResult.error(new NavigationFailException("EHiRouter.Builder can't be used multiple times"));
            }

            try {

                RouterHolder holder = generateHolder();

                for (EHiUiRouterInterceptor interceptor : uiRouterInterceptors) {
                    interceptor.preIntercept(holder);
                }

                if (isUsebuiltInFragment) {
                    addExtraInfo("isUseBuildInFragment",true);
                }

                if (holder.context == null) {
                    EHiUiRouterCenter.getInstance().fopenUri(holder.fragment, holder.uri, holder.bundle, holder.requestCode, helpMap);
                } else {
                    EHiUiRouterCenter.getInstance().openUri(holder.context, holder.uri, holder.bundle, holder.requestCode, helpMap);
                }

                return EHiRouterResult.success(holder.uri);

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

    }

    /**
     * 当发起一个路由的时候,这个类会持有所有的信息,拦截器中可以拿到这个参数
     */
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
        public Bundle bundle = new Bundle();

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

    /**
     * 当发生错误的时候的错误拦截器
     */
    public interface EHiErrorRouterInterceptor {

        /**
         * 发生错误的时候的回调
         *
         * @param e
         */
        void onRouterError(Exception e) throws Exception;

    }

}
