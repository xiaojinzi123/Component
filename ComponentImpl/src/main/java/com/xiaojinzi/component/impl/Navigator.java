package com.xiaojinzi.component.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
import com.xiaojinzi.component.impl.interceptor.OpenOnceInterceptor;
import com.xiaojinzi.component.impl.interceptor.RouterInterceptorCache;
import com.xiaojinzi.component.support.Action;
import com.xiaojinzi.component.support.Consumer;
import com.xiaojinzi.component.support.NavigationDisposable;
import com.xiaojinzi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 这个类一部分功能应该是 {@link Router} 的构建者对象的功能,但是这里面更多的为导航的功能
 * 写了很多代码,所以名字就不叫 Builder 了
 */
class Navigator extends RouterRequest.Builder {

    /**
     * 自定义的拦截器列表,为了保证顺序才用一个集合的
     * 1. RouterInterceptor 类型
     * 2. Class<RouterInterceptor> 类型
     * 3. String 类型
     * 其他类型会 debug 的时候报错
     */
    @Nullable
    private List<Object> customInterceptors;

    /**
     * 标记这个 builder 是否已经被使用了,使用过了就不能使用了
     */
    protected boolean isFinish = false;

    public Navigator(@NonNull Context context) {
        Utils.checkNullPointer(context, "context");
        context(context);
    }

    public Navigator(@NonNull Fragment fragment) {
        Utils.checkNullPointer(fragment, "fragment");
        fragment(fragment);
    }

    /**
     * 懒加载自定义拦截器列表
     *
     * @param size
     */
    private void lazyInitCustomInterceptors(int size) {
        if (customInterceptors == null) {
            customInterceptors = new ArrayList<>(size > 3 ? size : 3);
        }
    }

    public Navigator interceptors(RouterInterceptor... interceptorArr) {
        Utils.checkNullPointer(interceptorArr, "interceptorArr");
        if (interceptorArr != null) {
            lazyInitCustomInterceptors(interceptorArr.length);
            customInterceptors.addAll(Arrays.asList(interceptorArr));
        }
        return this;
    }

    public Navigator interceptors(Class<? extends RouterInterceptor>... interceptorClassArr) {
        Utils.checkNullPointer(interceptorClassArr, "interceptorClassArr");
        if (interceptorClassArr != null) {
            lazyInitCustomInterceptors(interceptorClassArr.length);
            customInterceptors.addAll(Arrays.asList(interceptorClassArr));
        }
        return this;
    }

    public Navigator interceptorNames(String... interceptorNameArr) {
        Utils.checkNullPointer(interceptorNameArr, "interceptorNameArr");
        if (interceptorNameArr != null) {
            lazyInitCustomInterceptors(interceptorNameArr.length);
            customInterceptors.addAll(Arrays.asList(interceptorNameArr));
        }
        return this;
    }

    @Override
    public Navigator intentConsumer(@Nullable Consumer<Intent> intentConsumer) {
        return (Navigator) super.intentConsumer(intentConsumer);
    }

    @Override
    public Navigator beforJumpAction(@Nullable Action action) {
        return (Navigator) super.beforJumpAction(action);
    }

    @Override
    public Navigator afterJumpAction(@Nullable Action action) {
        return (Navigator) super.afterJumpAction(action);
    }

    @Override
    public Navigator requestCode(@Nullable Integer requestCode) {
        return (Navigator) super.requestCode(requestCode);
    }

    @Override
    public Navigator url(@NonNull String url) {
        return (Navigator) super.url(url);
    }

    @Override
    public Navigator scheme(@NonNull String scheme) {
        return (Navigator) super.scheme(scheme);
    }

    @Override
    public Navigator host(@NonNull String host) {
        return (Navigator) super.host(host);
    }

    @Override
    public Navigator path(@Nullable String path) {
        return (Navigator) super.path(path);
    }

    @Override
    public Navigator putBundle(@NonNull String key, @Nullable Bundle bundle) {
        return (Navigator) super.putBundle(key, bundle);
    }

    @Override
    public Navigator putAll(@NonNull Bundle bundle) {
        return (Navigator) super.putAll(bundle);
    }

    @Override
    public Navigator putCharSequence(@NonNull String key, @Nullable CharSequence value) {
        return (Navigator) super.putCharSequence(key, value);
    }

    @Override
    public Navigator putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
        return (Navigator) super.putCharSequenceArray(key, value);
    }

    @Override
    public Navigator putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
        return (Navigator) super.putCharSequenceArrayList(key, value);
    }

    @Override
    public Navigator putByte(@NonNull String key, @Nullable byte value) {
        return (Navigator) super.putByte(key, value);
    }

    @Override
    public Navigator putByteArray(@NonNull String key, @Nullable byte[] value) {
        return (Navigator) super.putByteArray(key, value);
    }

    @Override
    public Navigator putChar(@NonNull String key, @Nullable char value) {
        return (Navigator) super.putChar(key, value);
    }

    @Override
    public Navigator putCharArray(@NonNull String key, @Nullable char[] value) {
        return (Navigator) super.putCharArray(key, value);
    }

    @Override
    public Navigator putBoolean(@NonNull String key, @Nullable boolean value) {
        return (Navigator) super.putBoolean(key, value);
    }

    @Override
    public Navigator putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
        return (Navigator) super.putBooleanArray(key, value);
    }

    @Override
    public Navigator putString(@NonNull String key, @Nullable String value) {
        return (Navigator) super.putString(key, value);
    }

    @Override
    public Navigator putStringArray(@NonNull String key, @Nullable String[] value) {
        return (Navigator) super.putStringArray(key, value);
    }

    @Override
    public Navigator putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
        return (Navigator) super.putStringArrayList(key, value);
    }

    @Override
    public Navigator putShort(@NonNull String key, @Nullable short value) {
        return (Navigator) super.putShort(key, value);
    }

    @Override
    public Navigator putShortArray(@NonNull String key, @Nullable short[] value) {
        return (Navigator) super.putShortArray(key, value);
    }

    @Override
    public Navigator putInt(@NonNull String key, @Nullable int value) {
        return (Navigator) super.putInt(key, value);
    }

    @Override
    public Navigator putIntArray(@NonNull String key, @Nullable int[] value) {
        return (Navigator) super.putIntArray(key, value);
    }

    @Override
    public Navigator putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
        return (Navigator) super.putIntegerArrayList(key, value);
    }

    @Override
    public Navigator putLong(@NonNull String key, @Nullable long value) {
        return (Navigator) super.putLong(key, value);
    }

    @Override
    public Navigator putLongArray(@NonNull String key, @Nullable long[] value) {
        return (Navigator) super.putLongArray(key, value);
    }

    @Override
    public Navigator putFloat(@NonNull String key, @Nullable float value) {
        return (Navigator) super.putFloat(key, value);
    }

    @Override
    public Navigator putFloatArray(@NonNull String key, @Nullable float[] value) {
        return (Navigator) super.putFloatArray(key, value);
    }

    @Override
    public Navigator putDouble(@NonNull String key, @Nullable double value) {
        return (Navigator) super.putDouble(key, value);
    }

    @Override
    public Navigator putDoubleArray(@NonNull String key, @Nullable double[] value) {
        return (Navigator) super.putDoubleArray(key, value);
    }

    @Override
    public Navigator putParcelable(@NonNull String key, @Nullable Parcelable value) {
        return (Navigator) super.putParcelable(key, value);
    }

    @Override
    public Navigator putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
        return (Navigator) super.putParcelableArray(key, value);
    }

    @Override
    public Navigator putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
        return (Navigator) super.putParcelableArrayList(key, value);
    }

    @Override
    public Navigator putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
        return (Navigator) super.putSparseParcelableArray(key, value);
    }

    @Override
    public Navigator putSerializable(@NonNull String key, @Nullable Serializable value) {
        return (Navigator) super.putSerializable(key, value);
    }

    @Override
    public Navigator query(@NonNull String queryName, @Nullable String queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    @Override
    public Navigator query(@NonNull String queryName, boolean queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    @Override
    public Navigator query(@NonNull String queryName, byte queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    @Override
    public Navigator query(@NonNull String queryName, int queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    @Override
    public Navigator query(@NonNull String queryName, float queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    @Override
    public Navigator query(@NonNull String queryName, long queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    @Override
    public Navigator query(@NonNull String queryName, double queryValue) {
        return (Navigator) super.query(queryName, queryValue);
    }

    /**
     * 路由前的检查
     *
     * @throws Exception
     */
    protected void onCheck() {
        // 一个 Builder 不能被使用多次
        if (isFinish) {
            throw new NavigationFailException("Builder can't be used multiple times");
        }
        // 检查上下文和fragment
        if (context == null && fragment == null) {
            throw new NullPointerException("the parameter 'context' or 'fragment' both are null");
        }
    }

    /**
     * @return 返回的对象有可能是一个空实现对象 {@link Router#emptyNavigationDisposable}
     */
    @NonNull
    public NavigationDisposable navigate() {
        return navigate(null);
    }

    /**
     * 执行跳转的具体逻辑
     * 返回值不可以为空,是为了使用的时候更加的顺溜,不用判断空
     *
     * @param callback 回调
     * @return 返回的对象有可能是一个空实现对象 {@link Router#emptyNavigationDisposable},可以取消路由或者获取原始request对象
     */
    @AnyThread
    @NonNull
    public synchronized NavigationDisposable navigate(@Nullable final Callback callback) {
        // 构建请求对象
        RouterRequest originalRequest = null;
        try {
            // 路由前的检查
            onCheck();
            // 标记这个 builder 已经不能使用了
            isFinish = true;
            // 生成路由请求对象
            originalRequest = build();
            // 创建整个拦截器到最终跳转需要使用的 Callback
            final InterceptorCallback interceptorCallback = new InterceptorCallback(originalRequest, callback);
            // Fragment 的销毁的自动取消
            if (originalRequest.fragment != null) {
                Router.mNavigationDisposableList.add(interceptorCallback);
            }
            // Activity 的自动取消
            if (Utils.getActivityFromContext(originalRequest.context) != null) {
                Router.mNavigationDisposableList.add(interceptorCallback);
            }
            // 真正的去执行路由
            realNavigate(originalRequest, customInterceptors, interceptorCallback);
            // 返回对象
            return interceptorCallback;
        } catch (Exception e) { // 发生路由错误的时候
            RouterErrorResult errorResult = new RouterErrorResult(originalRequest, e);
            RouterUtil.errorCallback(callback, errorResult);
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
        return Router.emptyNavigationDisposable;
    }

    /**
     * 真正的执行路由
     *
     * @param originalRequest    最原始的请求对象
     * @param customInterceptors 自定义的拦截器
     * @param callback           回调对象
     */
    @AnyThread
    private static void realNavigate(@NonNull final RouterRequest originalRequest,
                                     @Nullable List<Object> customInterceptors,
                                     @NonNull RouterInterceptor.Callback callback) {

        // 拿到共有的拦截器
        List<RouterInterceptor> publicInterceptors = InterceptorCenter.getInstance()
                .getGlobalInterceptorList();
        // 自定义拦截器,初始化拦截器的个数 8 个够用应该不会经常扩容
        final List<RouterInterceptor> currentInterceptors = new ArrayList(8);
        // 添加内置拦截器,目前就一个内置拦截器,而且必须在最前面,因为这个拦截器内部有一个时间的记录
        // 保证一秒内就只能打开一个相同的界面
        currentInterceptors.add(OpenOnceInterceptor.getInstance());
        // 添加共有拦截器
        currentInterceptors.addAll(publicInterceptors);
        // 添加自定义拦截器
        addCustomInterceptors(originalRequest, customInterceptors, currentInterceptors);
        // 扫尾拦截器,内部会添加目标要求执行的拦截器和真正执行跳转的拦截器
        currentInterceptors.add(new RouterInterceptor() {
            @Override
            public void intercept(Chain nextChain) throws Exception {
                // 这个地址要执行的拦截器,这里取的时候一定要注意了,不能拿最原始的那个 request,因为上面的拦截器都能更改 request,
                // 导致最终跳转的界面和你拿到的拦截器不匹配,所以这里一定是拿上一个拦截器传给你的 request 对象
                List<RouterInterceptor> targetInterceptors = RouterCenter.getInstance().interceptors(nextChain.request().uri);
                if (!targetInterceptors.isEmpty()) {
                    currentInterceptors.addAll(targetInterceptors);
                }
                // 真正的执行跳转的拦截器
                currentInterceptors.add(new RealInterceptor(originalRequest));
                // 执行下一个拦截器,正好是上面代码添加的拦截器
                nextChain.proceed(nextChain.request());
            }
        });
        // 创建执行器
        final RouterInterceptor.Chain chain = new InterceptorChain(currentInterceptors, 0, originalRequest,
                callback);
        // 执行
        chain.proceed(originalRequest);

    }

    /**
     * 添加自定义的拦截器
     *
     * @param originalRequest
     * @param customInterceptors
     * @param currentInterceptors
     */
    private static void addCustomInterceptors(@NonNull RouterRequest originalRequest,
                                              @Nullable List<Object> customInterceptors,
                                              List<RouterInterceptor> currentInterceptors) {
        if (customInterceptors == null) {
            return;
        }
        for (Object customInterceptor : customInterceptors) {
            if (customInterceptor instanceof RouterInterceptor) {
                currentInterceptors.add((RouterInterceptor) customInterceptor);
            } else if (customInterceptor instanceof Class) {
                RouterInterceptor interceptor = RouterInterceptorCache.getInterceptorByClass((Class<? extends RouterInterceptor>) customInterceptor);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is " + (Class) customInterceptor + ",target url is " + originalRequest.uri.toString());
                } else {
                    currentInterceptors.add(interceptor);
                }
            } else if (customInterceptor instanceof String) {
                RouterInterceptor interceptor = InterceptorCenter.getInstance().getByName((String) customInterceptor);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's name is " + (String) customInterceptor + ",target url is " + originalRequest.uri.toString());
                } else {
                    currentInterceptors.add(interceptor);
                }
            }
        }
    }

    /**
     * 这个拦截器的 Callback 是所有拦截器执行过程中会使用的一个 Callback,这是唯一的一个,每个拦截器对象拿到的此对象都是一样的
     */
    private static class InterceptorCallback implements RouterInterceptor.Callback, NavigationDisposable {

        /**
         * 用户的回调
         */
        @Nullable
        private Callback mCallback;

        /**
         * 最原始的请求,用户构建的,不会更改的
         */
        @NonNull
        private final RouterRequest mOriginalRequest;

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

        public InterceptorCallback(@NonNull RouterRequest originalRequest,
                                   @Nullable Callback callback) {
            this.mOriginalRequest = originalRequest;
            this.mCallback = callback;
        }

        @Override
        public void onSuccess(RouterResult result) {
            synchronized (this) {
                if (isEnd()) {
                    return;
                }
                isComplete = true;
                RouterUtil.successCallback(mCallback, result);
            }
        }

        @Override
        public void onError(Throwable error) {
            synchronized (this) {
                if (isEnd()) {
                    return;
                }
                isComplete = true;
                RouterErrorResult errorResult = new RouterErrorResult(mOriginalRequest, error);
                RouterUtil.errorCallback(mCallback, errorResult);
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
        public RouterRequest originalRequest() {
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
                RouterUtil.cancelCallback(mOriginalRequest, mCallback);
            }
        }
    }

    /**
     * 实现拦截器列表中的最后一环,内部去执行了跳转的代码,并且切换了线程执行,当前线程会停住
     */
    private static class RealInterceptor implements RouterInterceptor {

        @NonNull
        private final RouterRequest mOriginalRequest;

        public RealInterceptor(@NonNull RouterRequest originalRequest) {
            mOriginalRequest = originalRequest;
        }

        /**
         * @param chain 拦截器执行连接器
         * @throws Exception
         */
        @Override
        @MainThread
        public void intercept(final Chain chain) throws Exception {
            try {
                // 这个 request 对象已经不是最原始的了,但是可能是最原始的,就看拦截器是否更改了这个对象了
                RouterRequest finalRequest = chain.request();
                if (finalRequest.beforJumpAction != null) {
                    finalRequest.beforJumpAction.run();
                }
                // 真正执行跳转的逻辑
                RouterCenter.getInstance().openUri(finalRequest);
                if (finalRequest.afterJumpAction != null) {
                    finalRequest.afterJumpAction.run();
                }
                chain.callback().onSuccess(new RouterResult(mOriginalRequest, finalRequest));
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
    private static class InterceptorChain implements RouterInterceptor.Chain {

        /**
         * 每一个拦截器执行器 {@link RouterInterceptor.Chain}
         * 都会有上一个拦截器给的 request 对象或者初始化的一个 request,用于在下一个拦截器
         * 中获取到 request 对象,并且支持拦截器自定义修改 request 对象或者直接创建一个新的传给下一个拦截器执行器
         */
        @NonNull
        private final RouterRequest mRequest;

        /**
         * 这个是拦截器的回调,这个用户不能自定义,一直都是一个对象
         */
        @NonNull
        private final RouterInterceptor.Callback mCallback;

        /**
         * 拦截器列表,所有要执行的拦截器列表
         */
        @NonNull
        private final List<RouterInterceptor> mInterceptors;

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
        public InterceptorChain(@NonNull List<RouterInterceptor> interceptors, int index,
                                @NonNull RouterRequest request, @NonNull RouterInterceptor.Callback callback) {
            this.mInterceptors = interceptors;
            this.mIndex = index;
            this.mRequest = request;
            this.mCallback = callback;
        }

        @Override
        public RouterRequest request() {
            // 第一个拦截器的
            return mRequest;
        }

        @Override
        public RouterInterceptor.Callback callback() {
            return mCallback;
        }

        @Override
        public void proceed(final RouterRequest request) {
            proceed(request, mCallback);
        }

        private void proceed(@NonNull final RouterRequest request, @NonNull final RouterInterceptor.Callback callback) {
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
                            RouterInterceptor interceptor = mInterceptors.get(mIndex);
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