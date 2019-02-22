package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.ehi.component.ComponentConfig;
import com.ehi.component.ComponentUtil;
import com.ehi.component.error.InterceptorNotFoundException;
import com.ehi.component.error.NavigationFailException;
import com.ehi.component.impl.interceptor.EHiInterceptorCenter;
import com.ehi.component.impl.interceptor.EHiOpenOnceInterceptor;
import com.ehi.component.impl.interceptor.EHiRouterInterceptorCache;
import com.ehi.component.router.IComponentHostRouter;
import com.ehi.component.support.Action;
import com.ehi.component.support.Consumer;
import com.ehi.component.support.NavigationDisposable;
import com.ehi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * 整个路由框架,整体都是在主线程中执行的,在拦截器中提供了 callback 机制
 * 所以有耗时的操作可以在拦截器中去开子线程执行然后在回调中继续下一个拦截器
 * <p>
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
    public static final String TAG = "EHiRouter";

    /**
     * 路由的监听器
     */
    static Collection<EHiRouterListener> routerListeners = Collections
            .synchronizedCollection(new ArrayList<EHiRouterListener>(0));

    // 支持取消的一个 Callback 集合,需要线程安全
    private static List<NavigationDisposable> mNavigationDisposableList = new Vector<>();

    public static void clearRouterListeners() {
        routerListeners.clear();
    }

    public static void addRouterListener(@NonNull EHiRouterListener listener) {
        if (routerListeners.contains(listener)) {
            return;
        }
        routerListeners.add(listener);
    }

    public static void removeRouterListener(EHiRouterListener listener) {
        if (listener == null) {
            return;
        }
        routerListeners.remove(listener);
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
        return new Builder(context);
    }

    public static Builder withFragment(@NonNull Fragment fragment) {
        return new Builder(fragment);
    }

    public static boolean isMatchUri(@NonNull Uri uri) {
        return EHiRouterCenter.getInstance().isMatchUri(uri);
    }

    public static class Builder extends EHiRouterRequest.Builder {

        @Nullable
        private List<EHiRouterInterceptor> routerInterceptors;

        /**
         * 标记这个 builder 是否已经被使用了,使用过了就不能使用了
         */
        protected boolean isFinish = false;

        public Builder(@NonNull Context context) {
            Utils.checkNullPointer(context, "context");
            context(context);
        }

        public Builder(@NonNull Fragment fragment) {
            Utils.checkNullPointer(fragment, "fragment");
            fragment(fragment);
        }

        public Builder interceptors(@NonNull EHiRouterInterceptor... interceptors) {
            Utils.checkNullPointer(interceptors, "interceptors");
            lazyInitEHiRouterInterceptors(interceptors.length);
            routerInterceptors.addAll(Arrays.asList(interceptors));
            return this;
        }

        public Builder interceptors(@NonNull List<EHiRouterInterceptor> interceptors) {
            Utils.checkNullPointer(interceptors, "interceptors");
            lazyInitEHiRouterInterceptors(interceptors.size());
            routerInterceptors.addAll(interceptors);
            return this;
        }

        public Builder interceptors(@NonNull Class<? extends EHiRouterInterceptor>... interceptors) {
            Utils.checkNullPointer(interceptors, "interceptors");
            lazyInitEHiRouterInterceptors(interceptors.length);
            for (Class<? extends EHiRouterInterceptor> interceptor : interceptors) {
                routerInterceptors.add(EHiRouterInterceptorCache.getInterceptorByClass(interceptor));
            }
            return this;
        }

        public Builder interceptorNames(@NonNull String... interceptors) {
            Utils.checkNullPointer(interceptors, "interceptors");
            lazyInitEHiRouterInterceptors(interceptors.length);
            for (String customNameInterceptor : interceptors) {
                EHiRouterInterceptor interceptor = EHiInterceptorCenter.getInstance()
                        .getByName(customNameInterceptor);
                if (interceptor != null) {
                    routerInterceptors.add(interceptor);
                } else if (ComponentConfig.isDebug()) {
                    throw new InterceptorNotFoundException(
                            "can't find the interceptor and it's name is " + customNameInterceptor);
                }
            }
            return this;
        }

        @Override
        public Builder intentConsumer(@Nullable Consumer<Intent> intentConsumer) {
            return (Builder) super.intentConsumer(intentConsumer);
        }

        @Override
        public Builder beforJumpAction(@Nullable Action action) {
            return (Builder) super.beforJumpAction(action);
        }

        @Override
        public Builder afterJumpAction(@Nullable Action action) {
            return (Builder) super.afterJumpAction(action);
        }

        private void lazyInitEHiRouterInterceptors(int size) {
            if (routerInterceptors == null) {
                routerInterceptors = new ArrayList<>(size > 3 ? size : 3);
            }
        }

        @Override
        public Builder requestCode(@Nullable Integer requestCode) {
            return (Builder) super.requestCode(requestCode);
        }

        @Override
        public Builder url(@NonNull String url) {
            return (Builder) super.url(url);
        }

        @Override
        public Builder scheme(@NonNull String scheme) {
            return (Builder) super.scheme(scheme);
        }

        @Override
        public Builder host(@NonNull String host) {
            return (Builder) super.host(host);
        }

        @Override
        public Builder path(@Nullable String path) {
            return (Builder) super.path(path);
        }

        @Override
        public Builder putBundle(@NonNull String key, @Nullable Bundle bundle) {
            return (Builder) super.putBundle(key, bundle);
        }

        @Override
        public Builder putAll(@NonNull Bundle bundle) {
            return (Builder) super.putAll(bundle);
        }

        @Override
        public Builder putCharSequence(@NonNull String key, @Nullable CharSequence value) {
            return (Builder) super.putCharSequence(key, value);
        }

        @Override
        public Builder putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
            return (Builder) super.putCharSequenceArray(key, value);
        }

        @Override
        public Builder putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
            return (Builder) super.putCharSequenceArrayList(key, value);
        }

        @Override
        public Builder putByte(@NonNull String key, @Nullable byte value) {
            return (Builder) super.putByte(key, value);
        }

        @Override
        public Builder putByteArray(@NonNull String key, @Nullable byte[] value) {
            return (Builder) super.putByteArray(key, value);
        }

        @Override
        public Builder putChar(@NonNull String key, @Nullable char value) {
            return (Builder) super.putChar(key, value);
        }

        @Override
        public Builder putCharArray(@NonNull String key, @Nullable char[] value) {
            return (Builder) super.putCharArray(key, value);
        }

        @Override
        public Builder putBoolean(@NonNull String key, @Nullable boolean value) {
            return (Builder) super.putBoolean(key, value);
        }

        @Override
        public Builder putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
            return (Builder) super.putBooleanArray(key, value);
        }

        @Override
        public Builder putString(@NonNull String key, @Nullable String value) {
            return (Builder) super.putString(key, value);
        }

        @Override
        public Builder putStringArray(@NonNull String key, @Nullable String[] value) {
            return (Builder) super.putStringArray(key, value);
        }

        @Override
        public Builder putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
            return (Builder) super.putStringArrayList(key, value);
        }

        @Override
        public Builder putShort(@NonNull String key, @Nullable short value) {
            return (Builder) super.putShort(key, value);
        }

        @Override
        public Builder putShortArray(@NonNull String key, @Nullable short[] value) {
            return (Builder) super.putShortArray(key, value);
        }

        @Override
        public Builder putInt(@NonNull String key, @Nullable int value) {
            return (Builder) super.putInt(key, value);
        }

        @Override
        public Builder putIntArray(@NonNull String key, @Nullable int[] value) {
            return (Builder) super.putIntArray(key, value);
        }

        @Override
        public Builder putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
            return (Builder) super.putIntegerArrayList(key, value);
        }

        @Override
        public Builder putLong(@NonNull String key, @Nullable long value) {
            return (Builder) super.putLong(key, value);
        }

        @Override
        public Builder putLongArray(@NonNull String key, @Nullable long[] value) {
            return (Builder) super.putLongArray(key, value);
        }

        @Override
        public Builder putFloat(@NonNull String key, @Nullable float value) {
            return (Builder) super.putFloat(key, value);
        }

        @Override
        public Builder putFloatArray(@NonNull String key, @Nullable float[] value) {
            return (Builder) super.putFloatArray(key, value);
        }

        @Override
        public Builder putDouble(@NonNull String key, @Nullable double value) {
            return (Builder) super.putDouble(key, value);
        }

        @Override
        public Builder putDoubleArray(@NonNull String key, @Nullable double[] value) {
            return (Builder) super.putDoubleArray(key, value);
        }

        @Override
        public Builder putParcelable(@NonNull String key, @Nullable Parcelable value) {
            return (Builder) super.putParcelable(key, value);
        }

        @Override
        public Builder putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
            return (Builder) super.putParcelableArray(key, value);
        }

        @Override
        public Builder putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
            return (Builder) super.putParcelableArrayList(key, value);
        }

        @Override
        public Builder putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
            return (Builder) super.putSparseParcelableArray(key, value);
        }

        @Override
        public Builder putSerializable(@NonNull String key, @Nullable Serializable value) {
            return (Builder) super.putSerializable(key, value);
        }

        @Override
        public Builder query(@NonNull String queryName, @Nullable String queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        @Override
        public Builder query(@NonNull String queryName, boolean queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        @Override
        public Builder query(@NonNull String queryName, byte queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        @Override
        public Builder query(@NonNull String queryName, int queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        @Override
        public Builder query(@NonNull String queryName, float queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        @Override
        public Builder query(@NonNull String queryName, long queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        @Override
        public Builder query(@NonNull String queryName, double queryValue) {
            return (Builder) super.query(queryName, queryValue);
        }

        /**
         * 路由前的检查
         *
         * @throws Exception
         */
        protected void onCheck() {
            // 一个 Builder 不能被使用多次
            if (isFinish) {
                throw new NavigationFailException("EHiRouter.Builder can't be used multiple times");
            }
            // 检查上下文和fragment
            if (context == null && fragment == null) {
                throw new NullPointerException("the parameter 'context' or 'fragment' both are null");
            }
        }

        /**
         * @return 返回的对象有可能是一个空实现对象 {@link NavigationDisposable#EMPTY}
         */
        @NonNull
        public NavigationDisposable navigate() {
            return navigate(null);
        }

        /**
         * 执行跳转的具体逻辑,必须在主线程中执行
         * 返回值不可以为空,是为了使用的时候更加的顺溜,不用判断空
         *
         * @param callback 回调
         * @return 返回的对象有可能是一个空实现对象 {@link NavigationDisposable#EMPTY},可以取消路由或者获取原始request对象
         */
        @MainThread
        @NonNull
        public synchronized NavigationDisposable navigate(@Nullable final EHiCallback callback) {
            // 构建请求对象
            EHiRouterRequest originalRequest = null;
            try {
                // 路由前的检查
                onCheck();
                // 标记这个 builder 已经不能使用了
                isFinish = true;
                // 构建请求对象
                originalRequest = build();
                // 创建整个拦截器到最终跳转需要使用的 Callback
                final InterceptorCallback interceptorCallback = new InterceptorCallback(originalRequest, callback);
                // Fragment 的销毁的自动取消
                if (originalRequest.fragment != null) {
                    mNavigationDisposableList.add(interceptorCallback);
                }
                // Activity 的自动取消
                if (originalRequest.context != null && originalRequest.context instanceof Activity) {
                    mNavigationDisposableList.add(interceptorCallback);
                }
                // 真正的去执行路由
                realNavigate(originalRequest, routerInterceptors, interceptorCallback);
                // 返回对象
                return interceptorCallback;
            } catch (Exception e) { // 发生路由错误的时候
                EHiRouterErrorResult errorResult = new EHiRouterErrorResult(originalRequest, e);
                EHiRouterUtil.errorCallback(callback, errorResult);
            } finally {
                // 释放资源
                context = null;
                fragment = null;
                scheme = null;
                url = null;
                host = null;
                path = null;
                requestCode = null;
                queryMap = null;
                bundle = null;
                intentConsumer = null;
                beforJumpAction = null;
                afterJumpAction = null;
            }
            return NavigationDisposable.EMPTY;
        }

        /**
         * 真正的执行路由
         *
         * @param originalRequest 最原始的请求对象
         * @param customInterceptors 自定义的拦截器
         * @param callback 回调对象
         */
        @AnyThread
        private void realNavigate(@NonNull final EHiRouterRequest originalRequest,
                @Nullable List<EHiRouterInterceptor> customInterceptors,
                @NonNull EHiRouterInterceptor.Callback callback) throws Exception {

            // 拿到共有的拦截器
            List<EHiRouterInterceptor> publicInterceptors = EHiInterceptorCenter.getInstance()
                    .getGlobalInterceptorList();
            // 自定义拦截器,初始化拦截器的个数 8 个够用应该不会经常扩容
            final List<EHiRouterInterceptor> currentInterceptors = new ArrayList(8);
            // 添加内置拦截器,目前就一个内置拦截器,而且必须在最前面,因为这个拦截器内部有一个时间的记录
            // 保证一秒内就只能打开一个相同的界面
            currentInterceptors.add(EHiOpenOnceInterceptor.getInstance());
            // 添加共有拦截器
            currentInterceptors.addAll(publicInterceptors);
            // 添加自定义拦截器
            if (customInterceptors != null) {
                currentInterceptors.addAll(customInterceptors);
            }

            // 扫尾拦截器,内部会添加目标要求执行的拦截器和真正执行跳转的拦截器
            currentInterceptors.add(new EHiRouterInterceptor() {
                @Override
                public void intercept(Chain nextChain) throws Exception {
                    // 这个地址要执行的拦截器,这里取的时候一定要注意了,不能拿最原始的那个 request,因为上面的拦截器都能更改 request,
                    // 导致最终跳转的界面和你拿到的拦截器不匹配,所以这里一定是拿上一个拦截器传给你的 request 对象
                    List<EHiRouterInterceptor> targetInterceptors = EHiRouterCenter.getInstance().interceptors(nextChain.request().uri);
                    if (targetInterceptors != null && !targetInterceptors.isEmpty()) {
                        currentInterceptors.addAll(targetInterceptors);
                    }
                    // 真正的执行跳转的拦截器
                    currentInterceptors.add(new RealInterceptor(originalRequest));
                    // 执行下一个拦截器,正好是上面代码添加的拦截器
                    nextChain.proceed(nextChain.request());
                }
            });
            // 创建执行器
            final EHiRouterInterceptor.Chain chain = new InterceptorChain(currentInterceptors, 0, originalRequest,
                    callback);
            // 执行
            chain.proceed(originalRequest);

        }

        /**
         * 这个拦截器的 Callback 是所有拦截器执行过程中会使用的一个 Callback,这是唯一的一个,每个拦截器对象拿到的此对象都是一样的
         */
        private class InterceptorCallback implements EHiRouterInterceptor.Callback, NavigationDisposable {

            /**
             * 用户的回调
             */
            @Nullable
            private EHiCallback mCallback;

            /**
             * 最原始的请求,用户构建的,不会更改的
             */
            @NonNull
            private final EHiRouterRequest mOriginalRequest;

            /**
             * 标记是否完成,出错或者成功都算是完成了,不能再继续调用了
             */
            private boolean isComplete = false;

            /**
             * 取消
             */
            private boolean isCanceled;

            /**
             * 标记这次路由请求是否完毕
             *
             * @return
             */
            private boolean isEnd() {
                return isComplete || isCanceled;
            }

            public InterceptorCallback(@NonNull EHiRouterRequest originalRequest,
                    @Nullable EHiCallback callback) {
                this.mOriginalRequest = originalRequest;
                this.mCallback = callback;
            }

            @Override
            public void onSuccess(EHiRouterResult result) {
                synchronized (this) {
                    if (isEnd()) {
                        return;
                    }
                    isComplete = true;
                    EHiRouterUtil.successCallback(mCallback, result);
                }
            }

            @Override
            public void onError(Throwable error) {
                synchronized (this) {
                    if (isEnd()) {
                        return;
                    }
                    isComplete = true;
                    EHiRouterErrorResult errorResult = new EHiRouterErrorResult(mOriginalRequest, error);
                    EHiRouterUtil.errorCallback(mCallback, errorResult);
                }
            }

            @Override
            public boolean isComplete() {
                synchronized (this) {
                    return isComplete;
                }
            }

            @Override
            public boolean isCanceled() {
                synchronized (this) {
                    return isCanceled;
                }
            }

            @NonNull
            @Override
            public EHiRouterRequest originalRequest() {
                return mOriginalRequest;
            }

            @Override
            @AnyThread
            public void cancel() {
                synchronized (this) {
                    if (isEnd()) {
                        return;
                    }
                    // 标记取消成功
                    isCanceled = true;
                    EHiRouterUtil.cancelCallback(mOriginalRequest, mCallback);
                }
            }
        }

        /**
         * 实现拦截器列表中的最后一环,内部去执行了跳转的代码,并且切换了线程执行,当前线程会停住
         */
        private class RealInterceptor implements EHiRouterInterceptor {

            @NonNull
            private final EHiRouterRequest mOriginalRequest;

            public RealInterceptor(@NonNull EHiRouterRequest originalRequest) {
                mOriginalRequest = originalRequest;
            }

            @Override
            public void intercept(final Chain chain) throws Exception {
                try {
                    // 这个 request 对象已经不是最原始的了,但是可能是最原始的,就看拦截器是否更改了这个对象了
                    EHiRouterRequest finalRequest = chain.request();
                    if (finalRequest.beforJumpAction != null) {
                        finalRequest.beforJumpAction.run();
                    }
                    // 真正执行跳转的逻辑
                    EHiRouterCenter.getInstance().openUri(finalRequest);
                    if (finalRequest.afterJumpAction != null) {
                        finalRequest.afterJumpAction.run();
                    }
                    chain.callback().onSuccess(new EHiRouterResult(mOriginalRequest, finalRequest));
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

            /**
             * 每一个拦截器执行器 {@link EHiRouterInterceptor.Chain}
             * 都会有上一个拦截器给的 request 对象或者初始化的一个 request,用于在下一个拦截器
             * 中获取到 request 对象,并且支持拦截器自定义修改 request 对象或者直接创建一个新的传给下一个拦截器执行器
             */
            @NonNull
            private final EHiRouterRequest mRequest;

            /**
             * 这个是拦截器的回调,这个用户不能自定义,一直都是一个对象
             */
            @NonNull
            private final EHiRouterInterceptor.Callback mCallback;

            /**
             * 拦截器列表,所有要执行的拦截器列表
             */
            @NonNull
            private final List<EHiRouterInterceptor> mInterceptors;

            /**
             * 拦截器的下标
             */
            private final int mIndex;

            /**
             * 调用的次数,如果超过1次就做相应的错误处理
             */
            private int calls;

            /**
             * @param interceptors
             * @param index
             * @param request      第一次这个对象是不需要的
             * @param callback
             */
            public InterceptorChain(@NonNull List<EHiRouterInterceptor> interceptors, int index,
                                    @NonNull EHiRouterRequest request, @NonNull EHiRouterInterceptor.Callback callback) {
                this.mInterceptors = interceptors;
                this.mIndex = index;
                this.mRequest = request;
                this.mCallback = callback;
            }

            @Override
            public EHiRouterRequest request() {
                // 第一个拦截器的
                return mRequest;
            }

            @Override
            public EHiRouterInterceptor.Callback callback() {
                return mCallback;
            }

            @Override
            public void proceed(final EHiRouterRequest request) {
                proceed(request, mCallback);
            }

            private void proceed(@NonNull final EHiRouterRequest request, @NonNull final EHiRouterInterceptor.Callback callback) {
                // ui 线程上执行
                Utils.postActionToMainThreadAnyway(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (callback().isComplete() || callback().isCanceled()) {
                                return;
                            }
                            if (request == null) {
                                callback().onError(new NavigationFailException("the reqest is null,you can't call 'proceed' method with null reqest,such as 'chain.proceed(null)'"));
                                return;
                            }
                            ++calls;
                            if (mIndex >= mInterceptors.size()) {
                                callback().onError(new NavigationFailException(new IndexOutOfBoundsException(
                                        "size = " + mInterceptors.size() + ",index = " + mIndex)));
                            } else if (calls > 1) { // 调用了两次
                                callback().onError(new NavigationFailException(
                                        "interceptor " + mInterceptors.get(mIndex - 1)
                                                + " must call proceed() exactly once"));
                            } else {
                                // 当拦截器最后一个的时候,就不是这个类了,是 RealInterceptor 了
                                InterceptorChain next = new InterceptorChain(mInterceptors, mIndex + 1,
                                        request, callback);
                                // current Interceptor
                                EHiRouterInterceptor interceptor = mInterceptors.get(mIndex);
                                // 用户自定义的部分,必须在主线程
                                interceptor.intercept(next);
                            }
                        } catch (Exception e) {
                            callback().onError(e);
                        }
                    }
                });
            }
        }

    }

    /**
     * 取消某一个 Activity的有关的路由任务
     *
     * @param act
     */
    @MainThread
    public static void cancel(@NonNull Activity act) {
        synchronized (mNavigationDisposableList) {
            for (int i = mNavigationDisposableList.size() - 1; i >= 0; i--) {
                NavigationDisposable disposable = mNavigationDisposableList.get(i);
                if (act == disposable.originalRequest().context) {
                    disposable.cancel();
                    mNavigationDisposableList.remove(i);
                }
            }
        }
    }

    /**
     * 取消一个 Fragment 的有关路由任务
     *
     * @param fragment
     */
    @MainThread
    public static void cancel(@NonNull Fragment fragment) {
        synchronized (mNavigationDisposableList) {
            for (int i = mNavigationDisposableList.size() - 1; i >= 0; i--) {
                NavigationDisposable disposable = mNavigationDisposableList.get(i);
                if (fragment == disposable.originalRequest().fragment) {
                    disposable.cancel();
                    mNavigationDisposableList.remove(i);
                }
            }
        }
    }

}
