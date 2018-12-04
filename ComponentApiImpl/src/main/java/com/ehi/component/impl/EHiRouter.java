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

    public static void clearRouterInterceptor() {
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
        EHiRouterCenter.getInstance().register(router);
    }

    public static void register(@NonNull String host) {
        EHiRouterCenter.getInstance().register(host);
    }

    public static void unregister(IComponentHostRouter router) {
        EHiRouterCenter.getInstance().unregister(router);
    }

    public static void unregister(@NonNull String host) {
        EHiRouterCenter.getInstance().unregister(host);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context, null);
    }

    public static Builder withFragment(@NonNull Fragment fragment) {
        return new Builder(fragment, null);
    }

    public static void open(@NonNull Context context, @NonNull String url) {
        new Builder(context, url).navigate();
    }

    public static void open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode) {
        new Builder(context, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static void open(@NonNull Context context, @NonNull String url, @Nullable Bundle bundle) {
        new Builder(context, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .navigate();
    }

    public static void open(@NonNull Context context, @NonNull String url, @Nullable Integer requestCode, @Nullable Bundle bundle) {
        new Builder(context, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .requestCode(requestCode)
                .navigate();
    }

    public static void fopen(@NonNull Fragment fragment, @NonNull String url) {
        new Builder(fragment, url).navigate();
    }

    public static void fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Integer requestCode) {
        new Builder(fragment, url)
                .requestCode(requestCode)
                .navigate();
    }

    public static void fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Bundle bundle) {
        new Builder(fragment, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .navigate();
    }

    public static void fopen(@NonNull Fragment fragment, @NonNull String url, @Nullable Bundle bundle, @Nullable Integer requestCode) {
        new Builder(fragment, url)
                .putAll(bundle == null ? new Bundle() : bundle)
                .requestCode(requestCode)
                .navigate();
    }

    public static boolean isMatchUri(@NonNull Uri uri) {
        return EHiRouterCenter.getInstance().isMatchUri(uri);
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
        private EHiRouterInterceptor[] interceptors;

        /**
         * 标记这个 builder 是否已经被使用了,使用过了就不能使用了
         */
        protected boolean isFinish = false;

        public Builder interceptors(@Nullable EHiRouterInterceptor... interceptors) {
            this.interceptors = interceptors;
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

        public void navigate() {
            navigate(null);
        }

        /**
         * 执行跳转的具体逻辑,必须在主线程中执行
         *
         * @return
         */
        @MainThread
        public synchronized void navigate(@Nullable final EHiCallback callback) {

            if (isFinish) {
                EHiRouterUtil.errorCallback(callback, new NavigationFailException("EHiRouter.Builder can't be used multiple times"));
                return;
            }
            // 标记这个 builder 已经不能使用了
            isFinish = true;

            if (EHiRouterUtil.isMainThread() == false) {
                EHiRouterUtil.errorCallback(callback, new NavigationFailException("EHiRouter must run on main thread"));
                return;
            }

            try {

                EHiRouterRequest originalRequest = generateRouterRequest();

                realNavigate(originalRequest, interceptors, new InterceptorCallbackImpl(callback));

            } catch (Exception e) { // 发生路由错误的时候
                EHiRouterUtil.deliveryError(e);
                EHiRouterUtil.errorCallback(callback, e);
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
            }


        }

        /**
         * 真正的执行路由
         *
         * @param originalRequest    最原始的请求对象
         * @param customInterceptors 自定义的拦截器
         * @param callback           回调对象
         * @throws Exception
         */
        @MainThread
        private void realNavigate(@NonNull EHiRouterRequest originalRequest,
                                  @Nullable EHiRouterInterceptor[] customInterceptors,
                                  @NonNull EHiRouterInterceptor.Callback callback) throws Exception {
            // 走拦截器
            final List<EHiRouterInterceptor> interceptors = new ArrayList(routerInterceptors);
            if (customInterceptors != null) {
                for (EHiRouterInterceptor customInterceptor : customInterceptors) {
                    interceptors.add(customInterceptor);
                }
            }
            interceptors.add(new TargetInterceptorsInterceptor());
            final EHiRouterInterceptor.Chain chain = new InterceptorChain(interceptors, 0, originalRequest, callback);
            chain.proceed(originalRequest);
        }

        private class InterceptorCallbackImpl implements EHiRouterInterceptor.Callback {

            private EHiCallback mCallback;

            /**
             * 标记是否完成
             */
            private boolean isComplete = false;

            public InterceptorCallbackImpl(EHiCallback callback) {
                this.mCallback = callback;
            }

            @Override
            public void onSuccess(EHiRouterExecuteResult result) {
                synchronized (this) {
                    if (isComplete) {
                        return;
                    }
                    isComplete = true;
                    // 会在主线程中回调接口
                    EHiRouterUtil.successCallback(mCallback, new EHiRouterResult(result.request));
                }
            }

            @Override
            public void onError(Exception error) {
                synchronized (this) {
                    if (isComplete) {
                        return;
                    }
                    isComplete = true;
                    EHiRouterUtil.deliveryError(error);
                    EHiRouterUtil.errorCallback(mCallback, error);
                }
            }

            @Override
            public boolean isComplete() {
                synchronized (this) {
                    return isComplete;
                }
            }

        }

        private class TargetInterceptorsInterceptor implements EHiRouterInterceptor {

            /**
             * @param nextChain 这个虽然名字叫这个,但是这个执行器里面有上一个拦截器传给你的 request
             * @return
             * @throws Exception
             */
            @Override
            public void intercept(final Chain nextChain) throws Exception {
                // 走拦截器
                final List<EHiRouterInterceptor> interceptors = new ArrayList();
                // 这个地址要执行的拦截器
                List<EHiRouterInterceptor> targetInterceptors = EHiRouterCenter.getInstance().interceptors(nextChain.request().uri);
                if (targetInterceptors != null && targetInterceptors.size() > 0) {
                    for (EHiRouterInterceptor interceptor : targetInterceptors) {
                        interceptors.add(interceptor);
                    }
                }
                interceptors.add(new RealInterceptor());
                final EHiRouterInterceptor.Chain chain = new InterceptorChain(interceptors, 0, nextChain.request(), nextChain.callback());
                chain.proceed(nextChain.request());

            }

        }

        /**
         * 实现拦截器列表中的最后一环,内部去执行了跳转的代码,并且切换了线程执行,当前线程会停住
         */
        private class RealInterceptor implements EHiRouterInterceptor {

            @Override
            public void intercept(final Chain chain) throws Exception {

                try {
                    EHiRouterCenter.getInstance().openUri(chain.request());
                    chain.callback().onSuccess(new EHiRouterExecuteResult(chain.request()));
                } catch (Exception e) {
                    chain.callback().onError(e);
                }

            }

        }

        /**
         * 拦截器多个连接着走的执行器,源代码来源于 OkHTTP
         * 这个原理就是,本身是一个 执行器 (Chain),当你调用 proceed 方法的时候,会创建下一个拦截器的执行对象
         * 然后调用当前拦截器的 intercept 方法
         */
        private class InterceptorChain implements EHiRouterInterceptor.Chain {

            @NonNull
            private EHiRouterRequest mOriginalRequest;
            @NonNull
            private EHiRouterInterceptor.Callback mCallback;
            // 拦截器列表
            @NonNull
            private List<EHiRouterInterceptor> mInterceptors;
            // 拦截器的下标
            private int mIndex;
            // 调用的次数
            private int calls;

            /**
             * @param interceptors
             * @param index
             * @param request      第一次这个对象是不需要的
             * @param callback
             */
            public InterceptorChain(@NonNull List<EHiRouterInterceptor> interceptors, int index, @NonNull EHiRouterRequest request, EHiRouterInterceptor.Callback callback) {
                this.mInterceptors = interceptors;
                this.mIndex = index;
                this.mOriginalRequest = request;
                this.mCallback = callback;
            }

            @Override
            public EHiRouterRequest request() {
                // 第一个拦截器的
                return mOriginalRequest;
            }

            @Override
            public EHiRouterInterceptor.Callback callback() {
                return mCallback;
            }

            @Override
            public void proceed(final EHiRouterRequest request) throws Exception {
                EHiRouterUtil.postActionToMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback().isComplete()) {
                            return;
                        }
                        ++calls;
                        if (mIndex >= mInterceptors.size()) {
                            callback().onError(new NavigationFailException(new IndexOutOfBoundsException("size = " + mInterceptors.size() + ",index = " + mIndex)));
                        } else if (calls > 1) { // 调用了两次
                            callback().onError(new NavigationFailException("interceptor " + mInterceptors.get(mIndex - 1) + " must call proceed() exactly once"));
                        } else {
                            // 当拦截器最后一个的时候,就不是这个类了,是 RealInterceptor 了
                            InterceptorChain next = new InterceptorChain(mInterceptors, mIndex + 1, request, mCallback);
                            // current Interceptor
                            EHiRouterInterceptor interceptor = mInterceptors.get(mIndex);
                            try {
                                interceptor.intercept(next);
                            } catch (Exception e) {
                                callback().onError(e);
                            }
                        }
                    }
                });
            }

        }

    }

}
