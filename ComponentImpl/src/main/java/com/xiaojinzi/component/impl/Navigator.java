package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentConstants;
import com.xiaojinzi.component.anno.support.CheckClassNameAnno;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.error.ignore.ActivityResultException;
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.interceptor.InterceptorCenter;
import com.xiaojinzi.component.impl.interceptor.OpenOnceInterceptor;
import com.xiaojinzi.component.support.Action;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.component.support.Consumer;
import com.xiaojinzi.component.support.Consumer1;
import com.xiaojinzi.component.support.NavigationDisposable;
import com.xiaojinzi.component.support.ProxyIntentAct;
import com.xiaojinzi.component.support.RouterInterceptorCache;
import com.xiaojinzi.component.support.RouterRequestHelp;
import com.xiaojinzi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 这个类一部分功能应该是 {@link Router} 的构建者对象的功能,但是这里面更多的为导航的功能
 * 写了很多代码,所以名字就不叫 Builder 了
 */
@CheckClassNameAnno
public class Navigator extends RouterRequest.Builder implements Call {

    /**
     * requestCode 如果等于这个值,就表示是随机生成的
     * 从 1-256 中随机生成一个,如果生成的正好是目前正在用的,会重新生成一个
     */
    public static final Integer RANDOM_REQUSET_CODE = Integer.MIN_VALUE;

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

    /**
     * 是否自动取消
     */
    protected boolean autoCancel = true;

    /**
     * 是否检查路由是否重复, 默认是全局配置的开关
     */
    private boolean useRouteRepeatCheck = Component.getConfig().isUseRouteRepeatCheckInterceptor();

    public Navigator() {
    }

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
     */
    private void lazyInitCustomInterceptors(int size) {
        if (customInterceptors == null) {
            customInterceptors = new ArrayList<>(size > 3 ? size : 3);
        }
    }

    public Navigator interceptors(@Nullable RouterInterceptor... interceptorArr) {
        Utils.debugCheckNullPointer(interceptorArr, "interceptorArr");
        if (interceptorArr != null) {
            lazyInitCustomInterceptors(interceptorArr.length);
            customInterceptors.addAll(Arrays.asList(interceptorArr));
        }
        return this;
    }

    public Navigator interceptors(
            @Nullable Class<? extends RouterInterceptor>... interceptorClassArr) {
        Utils.debugCheckNullPointer(interceptorClassArr, "interceptorClassArr");
        if (interceptorClassArr != null) {
            lazyInitCustomInterceptors(interceptorClassArr.length);
            customInterceptors.addAll(Arrays.asList(interceptorClassArr));
        }
        return this;
    }

    public Navigator interceptorNames(@Nullable String... interceptorNameArr) {
        Utils.debugCheckNullPointer(interceptorNameArr, "interceptorNameArr");
        if (interceptorNameArr != null) {
            lazyInitCustomInterceptors(interceptorNameArr.length);
            customInterceptors.addAll(Arrays.asList(interceptorNameArr));
        }
        return this;
    }

    /**
     * requestCode 会随机的生成
     */
    public Navigator requestCodeRandom() {
        return requestCode(RANDOM_REQUSET_CODE);
    }

    public Navigator autoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
        return this;
    }

    public Navigator useRouteRepeatCheck(boolean useRouteRepeatCheck) {
        this.useRouteRepeatCheck = useRouteRepeatCheck;
        return this;
    }

    /**
     * 当您使用 {@link ProxyIntentBuilder} 构建了一个 {@link Intent} 之后.
     * 此 {@link Intent} 的跳转目标是一个代理的界面. 具体是
     * {@link ProxyIntentAct} 或者是用户你自己自定义的 {@link Class<Activity>}
     * 携带的参数是是真正的目标的信息. 比如：
     * {@link ProxyIntentAct#EXTRA_ROUTER_PROXY_INTENT_URL} 表示目标的 url
     * {@link ProxyIntentAct#EXTRA_ROUTER_PROXY_INTENT_BUNDLE} 表示跳转到真正的目标的 {@link Bundle} 数据
     * ......
     * 当你自定义了代理界面, 那你可以使用{@link Router#with()} 或者  {@link Router#with(Context)} 或者
     * {@link Router#with(Fragment)} 得到一个 {@link Navigator}
     * 然后你就可以使用{@link Navigator#proxyBundle(Bundle)} 直接导入跳转到真正目标所需的各种参数, 然后
     * 直接发起跳转, 通过条用 {@link Navigator#forward()} 等方法
     * 示例代码：
     * <pre class="prettyprint">
     * public class XXXProxyActivity extends Activity {
     *     ...
     *     protected void onCreate(Bundle savedInstanceState) {
     *         super.onCreate(savedInstanceState);
     *         Router.with(this)
     *               .proxyBundle(getIntent().getExtras())
     *               .forward();
     *     }
     *     ...
     * }
     * </pre>
     *
     * @see ProxyIntentAct
     */
    public Navigator proxyBundle(@NonNull Bundle bundle) {
        Utils.checkNullPointer(bundle, "bundle");
        String reqUrl = bundle.getString(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_URL);
        Bundle reqBundle = bundle.getBundle(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_BUNDLE);
        Bundle reqOptions = bundle.getBundle(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_OPTIONS);
        ArrayList<Integer> reqFlags = bundle.getIntegerArrayList(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_FLAGS);
        ArrayList<String> reqCategories = bundle.getStringArrayList(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_CATEGORIES);
        super.url(reqUrl);
        super.putAll(reqBundle);
        super.options(reqOptions);
        super.addIntentFlags(reqFlags.toArray(new Integer[0]));
        super.addIntentCategories(reqCategories.toArray(new String[0]));
        return this;
    }

    @Override
    public Navigator intentConsumer(@Nullable @UiThread Consumer<Intent> intentConsumer) {
        super.intentConsumer(intentConsumer);
        return this;
    }

    @Override
    public Navigator addIntentFlags(@Nullable Integer... flags) {
        super.addIntentFlags(flags);
        return this;
    }

    @Override
    public Navigator addIntentCategories(@Nullable String... categories) {
        super.addIntentCategories(categories);
        return this;
    }

    @Override
    public Navigator beforAction(@Nullable @UiThread Action action) {
        super.beforAction(action);
        return this;
    }

    @Override
    public Navigator beforStartAction(@Nullable Action action) {
        super.beforStartAction(action);
        return this;
    }

    @Override
    public Navigator afterStartAction(@Nullable Action action) {
        super.afterStartAction(action);
        return this;
    }

    @Override
    public Navigator afterAction(@Nullable @UiThread Action action) {
        super.afterAction(action);
        return this;
    }

    @Override
    public Navigator afterErrorAction(@Nullable @UiThread Action action) {
        super.afterErrorAction(action);
        return this;
    }

    @Override
    public Navigator afterEventAction(@Nullable @UiThread Action action) {
        super.afterEventAction(action);
        return this;
    }

    @Override
    public Navigator requestCode(@Nullable Integer requestCode) {
        super.requestCode(requestCode);
        return this;
    }

    @Override
    public Navigator options(@Nullable Bundle options) {
        super.options(options);
        return this;
    }

    @Override
    public Navigator url(@NonNull String url) {
        super.url(url);
        return this;
    }

    @Override
    public Navigator scheme(@NonNull String scheme) {
        super.scheme(scheme);
        return this;
    }

    @Override
    public Navigator hostAndPath(@NonNull String hostAndPath) {
        super.hostAndPath(hostAndPath);
        return this;
    }

    @Override
    public Navigator userInfo(@NonNull String userInfo) {
        super.userInfo(userInfo);
        return this;
    }

    @Override
    public Navigator host(@NonNull String host) {
        super.host(host);
        return this;
    }

    @Override
    public Navigator path(@NonNull String path) {
        super.path(path);
        return this;
    }

    @Override
    public Navigator putAll(@NonNull Bundle bundle) {
        super.putAll(bundle);
        return this;
    }

    @Override
    public Navigator putBundle(@NonNull String key, @Nullable Bundle bundle) {
        super.putBundle(key, bundle);
        return this;
    }

    @Override
    public Navigator putCharSequence(@NonNull String key, @Nullable CharSequence value) {
        super.putCharSequence(key, value);
        return this;
    }

    @Override
    public Navigator putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
        super.putCharSequenceArray(key, value);
        return this;
    }

    @Override
    public Navigator putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
        super.putCharSequenceArrayList(key, value);
        return this;
    }

    @Override
    public Navigator putByte(@NonNull String key, @Nullable byte value) {
        super.putByte(key, value);
        return this;
    }

    @Override
    public Navigator putByteArray(@NonNull String key, @Nullable byte[] value) {
        super.putByteArray(key, value);
        return this;
    }

    @Override
    public Navigator putChar(@NonNull String key, @Nullable char value) {
        super.putChar(key, value);
        return this;
    }

    @Override
    public Navigator putCharArray(@NonNull String key, @Nullable char[] value) {
        super.putCharArray(key, value);
        return this;
    }

    @Override
    public Navigator putBoolean(@NonNull String key, @Nullable boolean value) {
        super.putBoolean(key, value);
        return this;
    }

    @Override
    public Navigator putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
        super.putBooleanArray(key, value);
        return this;
    }

    @Override
    public Navigator putString(@NonNull String key, @Nullable String value) {
        super.putString(key, value);
        return this;
    }

    @Override
    public Navigator putStringArray(@NonNull String key, @Nullable String[] value) {
        super.putStringArray(key, value);
        return this;
    }

    @Override
    public Navigator putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
        super.putStringArrayList(key, value);
        return this;
    }

    @Override
    public Navigator putShort(@NonNull String key, @Nullable short value) {
        super.putShort(key, value);
        return this;
    }

    @Override
    public Navigator putShortArray(@NonNull String key, @Nullable short[] value) {
        super.putShortArray(key, value);
        return this;
    }

    @Override
    public Navigator putInt(@NonNull String key, @Nullable int value) {
        super.putInt(key, value);
        return this;
    }

    @Override
    public Navigator putIntArray(@NonNull String key, @Nullable int[] value) {
        super.putIntArray(key, value);
        return this;
    }

    @Override
    public Navigator putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
        super.putIntegerArrayList(key, value);
        return this;
    }

    @Override
    public Navigator putLong(@NonNull String key, @Nullable long value) {
        super.putLong(key, value);
        return this;
    }

    @Override
    public Navigator putLongArray(@NonNull String key, @Nullable long[] value) {
        super.putLongArray(key, value);
        return this;
    }

    @Override
    public Navigator putFloat(@NonNull String key, @Nullable float value) {
        super.putFloat(key, value);
        return this;
    }

    @Override
    public Navigator putFloatArray(@NonNull String key, @Nullable float[] value) {
        super.putFloatArray(key, value);
        return this;
    }

    @Override
    public Navigator putDouble(@NonNull String key, @Nullable double value) {
        super.putDouble(key, value);
        return this;
    }

    @Override
    public Navigator putDoubleArray(@NonNull String key, @Nullable double[] value) {
        super.putDoubleArray(key, value);
        return this;
    }

    @Override
    public Navigator putParcelable(@NonNull String key, @Nullable Parcelable value) {
        super.putParcelable(key, value);
        return this;
    }

    @Override
    public Navigator putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
        super.putParcelableArray(key, value);
        return this;
    }

    @Override
    public Navigator putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
        super.putParcelableArrayList(key, value);
        return this;
    }

    @Override
    public Navigator putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
        super.putSparseParcelableArray(key, value);
        return this;
    }

    @Override
    public Navigator putSerializable(@NonNull String key, @Nullable Serializable value) {
        super.putSerializable(key, value);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, @NonNull String queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, boolean queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, byte queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, int queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, float queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, long queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public Navigator query(@NonNull String queryName, double queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    @NonNull
    public RouterRequest build() {
        RouterRequest routerRequest = super.build();
        // 如果是随机的 requestCode, 则生成
        routerRequest = Help.randomlyGenerateRequestCode(routerRequest);
        // 现在可以检测 requestCode 是否重复
        boolean isExist = Help.isExist(routerRequest);
        if (isExist) { // 如果存在直接返回错误给 callback
            throw new NavigationFailException("request&result code is " +
                    routerRequest.requestCode + " is exist!");
        }
        return routerRequest;
    }

    /**
     * 使用默认的 {@link android.app.Application} 作为
     * {@link Context}. 使用默认的 {@link android.app.Application}
     * 会添加 {@link Intent#FLAG_ACTIVITY_NEW_TASK} 标记
     */
    private void useDefaultContext() {
        // 如果 Context 和 Fragment 都是空的,使用默认的 Application
        if (context == null && fragment == null) {
            context = Component.getApplication();
            // 配套加上 New_Task 的标志
            addIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    /**
     * 检查 forResult 的时候的各个参数是否合格
     */
    private void onCheckForResult() throws Exception {
        if (context == null && fragment == null) {
            throw new NavigationFailException(
                    new NullPointerException(
                            "Context or Fragment is necessary if you want get ActivityResult"
                    )
            );
        }
        // 如果是使用 Context 的,那么就必须是 FragmentActivity,需要操作 Fragment
        // 这里的 context != null 判断条件不能去掉,不然使用 Fragment 跳转的就过不去了
        if (context != null && !(Utils.getActivityFromContext(context) instanceof FragmentActivity)) {
            throw new NavigationFailException(
                    new IllegalArgumentException(
                            "context must be FragmentActivity or fragment must not be null " +
                                    "when you want get ActivityResult from target Activity"
                    )
            );
        }
        if (requestCode == null) {
            throw new NavigationFailException(
                    new NullPointerException(
                            "requestCode must not be null when you want get ActivityResult from target Activity, " +
                                    "if you use code, do you forget call requestCodeRandom() or requestCode(Integer). " +
                                    "if you use routerApi, do you forget mark method or parameter with @RequestCodeAnno() Annotation"
                    )
            );
        }
    }

    /**
     * 为了拿到 {@link ActivityResult#resultCode}
     *
     * @param callback 回调方法
     */
    @AnyThread
    public void forwardForResultCode(@NonNull @UiThread final BiCallback<Integer> callback) {
        navigateForResultCode(callback);
    }

    /**
     * 为了拿到 {@link ActivityResult#resultCode}
     *
     * @param callback 回调方法
     */
    @NonNull
    @AnyThread
    @CheckResult
    public NavigationDisposable navigateForResultCode(@NonNull @UiThread final BiCallback<Integer> callback) {
        return navigateForResult(new BiCallback.Map<ActivityResult, Integer>(callback) {
            @NonNull
            @Override
            public Integer apply(@NonNull ActivityResult activityResult) throws Exception {
                return activityResult.resultCode;
            }
        });
    }

    /**
     * 为了拿到 {@link ActivityResult#resultCode}
     *
     * @param callback 回调方法
     */
    @AnyThread
    public void forwardForResultCodeMatch(
            @NonNull @UiThread final Callback callback, final int expectedResultCode) {
        navigateForResultCodeMatch(callback, expectedResultCode);
    }

    /**
     * 为了拿到 {@link ActivityResult#resultCode}
     *
     * @param callback 回调方法
     */
    @NonNull
    @AnyThread
    @CheckResult
    public NavigationDisposable navigateForResultCodeMatch(
            @NonNull @UiThread final Callback callback, final int expectedResultCode) {
        return navigateForResult(new BiCallback<ActivityResult>() {
            @Override
            public void onSuccess(@NonNull RouterResult result, @NonNull ActivityResult activityResult) {
                if (expectedResultCode == activityResult.resultCode) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(new RouterErrorResult(result.getOriginalRequest(), new ActivityResultException("the resultCode is not matching " + expectedResultCode)));
                }
            }

            @Override
            public void onError(@NonNull RouterErrorResult errorResult) {
                callback.onError(errorResult);
            }

            @Override
            public void onCancel(@NonNull RouterRequest originalRequest) {
                callback.onCancel(originalRequest);
            }
        });
    }

    /**
     * 为了拿到 {@link Intent}
     *
     * @param callback 回调方法
     */
    @AnyThread
    public void forwardForIntentAndResultCodeMatch(
            @NonNull @UiThread final BiCallback<Intent> callback, final int expectedResultCode) {
        navigateForIntentAndResultCodeMatch(callback, expectedResultCode);
    }

    /**
     * 为了拿到 {@link Intent}
     *
     * @param callback 回调方法
     */
    @NonNull
    @AnyThread
    @CheckResult
    public NavigationDisposable navigateForIntentAndResultCodeMatch(
            @NonNull @UiThread final BiCallback<Intent> callback,
            final int expectedResultCode) {
        return navigateForResult(new BiCallback.Map<ActivityResult, Intent>(callback) {
            @NonNull
            @Override
            public Intent apply(@NonNull ActivityResult activityResult) throws Exception {
                return activityResult.intentWithResultCodeCheckAndGet(expectedResultCode);
            }
        });
    }

    /**
     * 为了拿到 {@link Intent}
     *
     * @param callback 回调方法
     */
    @AnyThread
    public void forwardForIntent(@NonNull @UiThread final BiCallback<Intent> callback) {
        navigateForIntent(callback);
    }

    /**
     * 为了拿到 {@link Intent}
     *
     * @param callback 回调方法
     */
    @NonNull
    @AnyThread
    @CheckResult
    public NavigationDisposable navigateForIntent(@NonNull @UiThread final BiCallback<Intent> callback) {
        return navigateForResult(new BiCallback.Map<ActivityResult, Intent>(callback) {
            @NonNull
            @Override
            public Intent apply(@NonNull ActivityResult activityResult) throws Exception {
                return activityResult.intentCheckAndGet();
            }
        });
    }

    /**
     * 为了拿 {@link ActivityResult}
     *
     * @param callback 这里是为了拿返回的东西是不可以为空的
     */
    @AnyThread
    public void forwardForResult(@NonNull @UiThread final BiCallback<ActivityResult> callback) {
        navigateForResult(callback);
    }


    /**
     * 为了拿 {@link ActivityResult}
     *
     * @param callback 这里是为了拿返回的东西是不可以为空的
     */
    @NonNull
    @AnyThread
    @CheckResult
    public NavigationDisposable navigateForResult(
            @NonNull @UiThread final BiCallback<ActivityResult> callback) {
        Utils.checkNullPointer(callback, "callback");
        return realNavigateForResult(callback);
    }

    /**
     * 没有返回值
     */
    @AnyThread
    public void forward() {
        navigate(null);
    }

    /**
     * @return 返回的对象有可能是一个空实现对象 {@link Router#emptyNavigationDisposable}
     */
    @NonNull
    @AnyThread
    @CheckResult
    public NavigationDisposable navigate() {
        return navigate(null);
    }

    /**
     * 没有返回值
     *
     * @param callback 路由的回调
     */
    @AnyThread
    public void forward(@Nullable @UiThread final Callback callback) {
        navigate(callback);
    }

    @NonNull
    @AnyThread
    @CheckResult
    public synchronized NavigationDisposable navigate(
            @Nullable @UiThread final Callback callback) {
        // 构建请求对象
        RouterRequest originalRequest = null;
        // 可取消对象
        InterceptorCallback interceptorCallback = null;
        try {
            // 如果用户没填写 Context 或者 Fragment 默认使用 Application
            useDefaultContext();
            // 路由前的检查
            if (isFinish) {
                // 一个 Builder 不能被使用多次
                throw new NavigationFailException("Builder can't be used multiple times");
            }
            if (context == null && fragment == null) {
                // 检查上下文和fragment
                throw new NullPointerException("the parameter 'context' or 'fragment' both are null");
            }
            // 标记这个 builder 已经不能使用了
            isFinish = true;
            // 生成路由请求对象
            originalRequest = build();
            // 创建整个拦截器到最终跳转需要使用的 Callback
            interceptorCallback = new InterceptorCallback(originalRequest, callback);
            // Fragment 的销毁的自动取消
            if (autoCancel && originalRequest.fragment != null) {
                Router.mNavigationDisposableList.add(interceptorCallback);
            }
            // Activity 的自动取消
            if (autoCancel && Utils.getActivityFromContext(originalRequest.context) != null) {
                Router.mNavigationDisposableList.add(interceptorCallback);
            }
            final RouterRequest finalOriginalRequest = originalRequest;
            final InterceptorCallback finalInterceptorCallback = interceptorCallback;
            Utils.postActionToMainThread(new Runnable() {
                @Override
                public void run() {
                    // 真正的去执行路由
                    realNavigate(finalOriginalRequest, customInterceptors, finalInterceptorCallback);
                }
            });
            // 返回对象
            return interceptorCallback;
        } catch (Exception e) { // 发生路由错误
            if (interceptorCallback == null) {
                RouterErrorResult errorResult = new RouterErrorResult(originalRequest, e);
                RouterUtil.errorCallback(callback, null, errorResult);
            } else {
                // 这里错误回调也会让 interceptorCallback 内部的 isEnd 是 true, 所以不用去特意取消
                // 也会让整个路由终止
                interceptorCallback.onError(e);
            }
        } finally {
            // 释放资源
            originalRequest = null;
            interceptorCallback = null;
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
            beforAction = null;
            beforStartAction = null;
            afterStartAction = null;
            afterAction = null;
            afterErrorAction = null;
            afterEventAction = null;
        }
        return Router.emptyNavigationDisposable;
    }

    @NonNull
    @AnyThread
    @CheckResult
    private NavigationDisposable realNavigateForResult(@NonNull final BiCallback<ActivityResult> callback) {
        Utils.checkNullPointer(callback, "callback");
        final NavigationDisposable.ProxyNavigationDisposableImpl proxyDisposable =
                new NavigationDisposable.ProxyNavigationDisposableImpl();
        // 主线程执行
        Utils.postActionToMainThread(new Runnable() {
            @Override
            public void run() {
                // 这里这个情况属于没开始就被取消了
                if (proxyDisposable.isCanceled()) {
                    RouterUtil.cancelCallback(null, callback);
                    return;
                }
                final NavigationDisposable realDisposable = doNavigateForResult(callback);
                proxyDisposable.setProxy(realDisposable);

            }
        });
        return proxyDisposable;
    }

    /**
     * 必须在主线程中调用,就这里可能会出现一种特殊的情况：
     * 用户收到的回调可能是 error,但是全局的监听可能是 cancel,其实这个问题也能解决,
     * 就是路由调用之前提前通过方法 {@link Navigator#build()} 提前构建一个 {@link RouterRequest} 出来判断
     * 但是没有那个必要去做这件事情了,等到有必要的时候再说,基本不会出现并且出现了也不是什么问题
     */
    @NonNull
    @UiThread
    @CheckResult
    private NavigationDisposable doNavigateForResult(@NonNull final BiCallback<ActivityResult> biCallback) {
        // 直接 gg
        Utils.checkNullPointer(biCallback, "biCallback");
        // 标记此次是需要框架帮助获取 ActivityResult 的
        this.isForResult = true;
        // 做一个包裹实现至多只能调用一次内部的其中一个方法
        final BiCallback<ActivityResult> biCallbackWrap = new BiCallbackWrap<>(biCallback);
        // disposable 对象
        NavigationDisposable finalNavigationDisposable = null;
        try {
            // 为了拿数据做的检查
            onCheckForResult();
            // 声明fragment
            FragmentManager fm = null;
            if (context == null) {
                fm = fragment.getChildFragmentManager();
            } else {
                fm = ((FragmentActivity) Utils.getActivityFromContext(context)).getSupportFragmentManager();
            }
            // 寻找是否添加过 Fragment
            RouterFragment findRxFragment = (RouterFragment) fm.findFragmentByTag(ComponentConstants.ACTIVITY_RESULT_FRAGMENT_TAG);
            if (findRxFragment == null) {
                findRxFragment = new RouterFragment();
                fm.beginTransaction()
                        .add(findRxFragment, ComponentConstants.ACTIVITY_RESULT_FRAGMENT_TAG)
                        // 这里必须使用 now 的形式, 否则连续的话立马就会new出来. 因为判断进来了
                        .commitNowAllowingStateLoss();
            }
            final RouterFragment rxFragment = findRxFragment;
            // 导航方法执行完毕之后,内部的数据就会清空,所以之前必须缓存
            // 导航拿到 NavigationDisposable 对象
            // 可能是一个 空实现
            finalNavigationDisposable = navigate(new CallbackAdapter() {
                @Override
                @UiThread
                public void onSuccess(@NonNull final RouterResult routerResult) {
                    super.onSuccess(routerResult);
                    // 设置ActivityResult回调的发射器,回调中一个路由拿数据的流程算是完毕了
                    rxFragment.setActivityResultConsumer(
                            routerResult.getOriginalRequest(),
                            new Consumer1<ActivityResult>() {
                                @Override
                                public void accept(@NonNull ActivityResult result) {
                                    Help.removeRequestCode(routerResult.getOriginalRequest());
                                    biCallbackWrap.onSuccess(routerResult, result);
                                }
                            });
                }

                @Override
                @UiThread
                public void onError(@NonNull RouterErrorResult errorResult) {
                    super.onError(errorResult);
                    Help.removeRequestCode(errorResult.getOriginalRequest());
                    // 这里为啥没有调用
                    biCallbackWrap.onError(errorResult);
                }

                @Override
                @UiThread
                public void onCancel(@NonNull RouterRequest originalRequest) {
                    super.onCancel(originalRequest);
                    rxFragment.removeActivityResultConsumer(originalRequest);
                    Help.removeRequestCode(originalRequest);
                    biCallbackWrap.onCancel(originalRequest);
                }

            });
            // 添加这个 requestCode 到 map, 重复的事情不用考虑了, 在 build RouterRequest 的时候已经处理了
            Help.addRequestCode(finalNavigationDisposable.originalRequest());
            return finalNavigationDisposable;
        } catch (Exception e) {
            if (finalNavigationDisposable == null) {
                RouterUtil.errorCallback(null, biCallbackWrap, new RouterErrorResult(e));
                // 就只会打印出一个错误信息: 路由失败信息
            } else {
                // 取消这个路由, 此时其实会输出两个信息
                // 第一个是打印出路由失败的信息
                // 第二个是路由被取消的信息
                // 因为上面路由发起了才能有 RouterRequest 对象, 然后这里检查到 requestCode 重复了
                // 回调给用户的是 requestCode 重复的错误, 但是上面发起的路由还是得取消的. 不然就跳过去了
                RouterUtil.errorCallback(
                        null, biCallbackWrap,
                        new RouterErrorResult(finalNavigationDisposable.originalRequest(), e)
                );
                // 取消上面执行的路由
                finalNavigationDisposable.cancel();
            }
            finalNavigationDisposable = null;
            return Router.emptyNavigationDisposable;
        }

    }

    /**
     * 真正的执行路由
     *
     * @param originalRequest           最原始的请求对象
     * @param customInterceptors        自定义的拦截器
     * @param routerInterceptorCallback 回调对象
     */
    @UiThread
    private void realNavigate(@NonNull final RouterRequest originalRequest,
                              @Nullable final List<Object> customInterceptors,
                              @NonNull final RouterInterceptor.Callback routerInterceptorCallback) {

        // 自定义拦截器,初始化拦截器的个数 8 个够用应该不会经常扩容
        final List<RouterInterceptor> allInterceptors = new ArrayList(10);

        // 此拦截器用于执行一些整个流程开始之前的事情
        allInterceptors.add(new RouterInterceptor() {
            @Override
            public void intercept(Chain chain) throws Exception {
                // 执行跳转前的 Callback
                RouterRequestHelp.executeBeforAction(chain.request());
                // 继续下一个拦截器
                chain.proceed(chain.request());
            }
        });

        // 添加路由检查拦截器
        if (useRouteRepeatCheck) {
            allInterceptors.add(OpenOnceInterceptor.getInstance());
        }
        // 添加共有拦截器
        allInterceptors.addAll(
                InterceptorCenter.getInstance().getGlobalInterceptorList()
        );
        // 添加用户自定义的拦截器
        allInterceptors.addAll(
                getCustomInterceptors(originalRequest, customInterceptors)
        );
        // 负责加载目标 Intent 的页面拦截器的拦截器. 此拦截器后不可再添加其他拦截器
        allInterceptors.add(new PageInterceptor(originalRequest, allInterceptors));

        // 创建执行器
        final RouterInterceptor.Chain chain = new InterceptorChain(
                allInterceptors, 0,
                originalRequest, routerInterceptorCallback
        );
        // 执行
        chain.proceed(originalRequest);

    }

    /**
     * 返回自定义的拦截器
     */
    @UiThread
    private static List<RouterInterceptor> getCustomInterceptors(@NonNull RouterRequest originalRequest,
                                                                 @Nullable List<Object> customInterceptors) throws InterceptorNotFoundException {
        if (customInterceptors == null || customInterceptors.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<RouterInterceptor> result = new ArrayList<>(customInterceptors.size());
        for (Object customInterceptor : customInterceptors) {
            if (customInterceptor instanceof RouterInterceptor) {
                result.add((RouterInterceptor) customInterceptor);
            } else if (customInterceptor instanceof Class) {
                RouterInterceptor interceptor = RouterInterceptorCache.getInterceptorByClass((Class<? extends RouterInterceptor>) customInterceptor);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's className is " + (Class) customInterceptor + ",target url is " + originalRequest.uri.toString());
                } else {
                    result.add(interceptor);
                }
            } else if (customInterceptor instanceof String) {
                RouterInterceptor interceptor = InterceptorCenter.getInstance().getByName((String) customInterceptor);
                if (interceptor == null) {
                    throw new InterceptorNotFoundException("can't find the interceptor and it's name is " + (String) customInterceptor + ",target url is " + originalRequest.uri.toString());
                } else {
                    result.add(interceptor);
                }
            }
        }
        return result;
    }

    /**
     * 这个拦截器的 Callback 是所有拦截器执行过程中会使用的一个 Callback,
     * 这是唯一的一个, 每个拦截器对象拿到的此对象都是一样的
     * 内部的错误成功额方法可以调用 N 次
     */
    private static class InterceptorCallback
            implements NavigationDisposable, RouterInterceptor.Callback {

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
         */
        public boolean isEnd() {
            return isComplete || isCanceled;
        }

        public InterceptorCallback(@NonNull RouterRequest originalRequest,
                                   @Nullable Callback callback) {
            this.mOriginalRequest = originalRequest;
            this.mCallback = callback;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result) {
            Utils.checkNullPointer(result);
            synchronized (this) {
                if (isEnd()) {
                    return;
                }
                isComplete = true;
                RouterUtil.successCallback(mCallback, result);
            }
        }

        @Override
        public void onError(@NonNull Throwable error) {
            Utils.checkNullPointer(error);
            synchronized (this) {
                if (isEnd()) {
                    return;
                }
                isComplete = true;
                // 创建错误的对象
                RouterErrorResult errorResult = new RouterErrorResult(mOriginalRequest, error);
                // 回调执行
                RouterUtil.errorCallback(mCallback, null, errorResult);
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
     * 拦截器多个连接着走的执行器,源代码来源于 OkHTTP
     * 这个原理就是, 本身是一个 执行器 (Chain),当你调用 proceed 方法的时候,会创建下一个拦截器的执行对象
     * 然后调用当前拦截器的 intercept 方法
     */
    public static class InterceptorChain implements RouterInterceptor.Chain {

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
         * @param interceptors 拦截器的即可
         * @param index        要执行的拦截器的下标
         * @param request      第一次这个对象是不需要的
         * @param callback     用户的 {@link Callback}
         */
        public InterceptorChain(@NonNull List<RouterInterceptor> interceptors, int index,
                                @NonNull RouterRequest request, @NonNull RouterInterceptor.Callback callback) {
            this.mInterceptors = interceptors;
            this.mIndex = index;
            this.mRequest = request;
            this.mCallback = callback;
        }

        /**
         * 拦截器是否是否已经走完
         */
        protected synchronized final boolean isCompletedProcess() {
            return mIndex >= mInterceptors.size();
        }

        protected final int index() {
            return mIndex;
        }

        @NonNull
        protected final List<RouterInterceptor> interceptors() {
            return mInterceptors;
        }

        @NonNull
        protected final RouterInterceptor.Callback rawCallback() {
            return mCallback;
        }

        @Override
        public final RouterRequest request() {
            // 第一个拦截器的
            return mRequest;
        }

        @Override
        public RouterInterceptor.Callback callback() {
            return rawCallback();
        }

        @Override
        public final void proceed(final RouterRequest request) {
            proceed(request, callback());
        }

        private final void proceed(@NonNull final RouterRequest request, @NonNull final RouterInterceptor.Callback callback) {
            // ui 线程上执行
            Utils.postActionToMainThreadAnyway(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 如果已经结束, 对不起就不执行了
                        if (callback().isEnd()) {
                            return;
                        }
                        if (request == null) {
                            callback().onError(new NavigationFailException("the reqest is null,you can't call 'proceed' method with null reqest,such as 'chain.proceed(null)'"));
                            return;
                        }
                        ++calls;
                        if (isCompletedProcess()) {
                            callback().onError(new NavigationFailException(new IndexOutOfBoundsException(
                                    "size = " + mInterceptors.size() + ",index = " + mIndex)));
                        } else if (calls > 1) { // 调用了两次
                            callback().onError(new NavigationFailException(
                                    "interceptor " + mInterceptors.get(mIndex - 1)
                                            + " must call proceed() exactly once"));
                        } else {
                            // current Interceptor
                            RouterInterceptor interceptor = mInterceptors.get(mIndex);
                            // 当拦截器最后一个的时候,就不是这个类了,是 DoActivityStartInterceptor 了
                            InterceptorChain next = new InterceptorChain(mInterceptors, mIndex + 1,
                                    request, callback);
                            // 提前同步 Query 到 Bundle
                            next.request().syncUriToBundle();
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

    /**
     * 处理页面拦截器的. 因为页面拦截器可能会更改 {@link Uri}. 导致目标改变.
     * 那么新的页面拦截器也应该被加载执行.
     * 最后确认 {@link Uri} 的目标没被改变的时候
     * 就可以加载 {@link DoActivityStartInterceptor} 执行跳转了.
     */
    @UiThread
    private static class PageInterceptor implements RouterInterceptor {

        @NonNull
        private RouterRequest mOriginalRequest;

        @NonNull
        private List<RouterInterceptor> mAllInterceptors;

        public PageInterceptor(@NonNull RouterRequest mOriginalRequest,
                               @NonNull List<RouterInterceptor> mAllInterceptors) {
            this.mOriginalRequest = mOriginalRequest;
            this.mAllInterceptors = mAllInterceptors;
        }

        @Override
        public void intercept(@NonNull Chain chain) throws Exception {
            Uri currentUri = chain.request().uri;
            // 这个地址要执行的页面拦截器,这里取的时候一定要注意了,不能拿最原始的那个 request,因为上面的拦截器都能更改 request,
            // 导致最终跳转的界面和你拿到的页面拦截器不匹配,所以这里一定是拿上一个拦截器传给你的 request 对象
            List<RouterInterceptor> targetPageInterceptors =
                    RouterCenter.getInstance().listPageInterceptors(currentUri);
            mAllInterceptors.add(new PageInterceptorUriCheckInterceptor(
                            mOriginalRequest,
                            mAllInterceptors,
                            currentUri,
                            targetPageInterceptors,
                            0
                    )
            );
            // 执行下一个拦截器,正好是上面代码添加的拦截器
            chain.proceed(chain.request());
        }
    }

    /**
     * 处理页面拦截器的. 因为页面拦截器可能会更改 {@link Uri}. 导致目标改变.
     * 那么新的页面拦截器也应该被加载执行.
     * 最后确认 {@link Uri} 的目标没被改变的时候
     * 就可以加载 {@link DoActivityStartInterceptor} 执行跳转了.
     */
    @UiThread
    private static class PageInterceptorUriCheckInterceptor implements RouterInterceptor {

        @NonNull
        private RouterRequest mOriginalRequest;

        @NonNull
        private List<RouterInterceptor> mAllInterceptors;

        /**
         * 进入页面拦截器之前的 {@link Uri}
         */
        @Nullable
        private Uri mBeforPageInterceptorUri;

        @Nullable
        private List<RouterInterceptor> mPageInterceptors;

        private int mPageIndex;

        public PageInterceptorUriCheckInterceptor(@NonNull RouterRequest mOriginalRequest,
                                                  @NonNull List<RouterInterceptor> mAllInterceptors,
                                                  @Nullable Uri mBeforPageInterceptorUri,
                                                  @Nullable List<RouterInterceptor> mPageInterceptors,
                                                  int mPageIndex) {
            this.mOriginalRequest = mOriginalRequest;
            this.mAllInterceptors = mAllInterceptors;
            this.mBeforPageInterceptorUri = mBeforPageInterceptorUri;
            this.mPageInterceptors = mPageInterceptors;
            this.mPageIndex = mPageIndex;
        }

        @Override
        public void intercept(@NonNull Chain chain) throws Exception {

            if (mPageIndex < 0) {
                throw new NavigationFailException(new IndexOutOfBoundsException(
                        "size = " + mPageInterceptors.size() + ",index = " + mPageIndex));
            }

            Uri currentUri = chain.request().uri;
            boolean isSameTarget;
            if (mBeforPageInterceptorUri != null) {
                isSameTarget = RouterCenter
                        .getInstance()
                        .isSameTarget(mBeforPageInterceptorUri, currentUri);
            } else {
                isSameTarget = false;
            }

            // 如果目标是相同的, 说明页面拦截器并没有改变跳转的目标
            if (isSameTarget) {
                // 没有下一个了
                if (mPageInterceptors == null || mPageIndex >= mPageInterceptors.size()) {
                    // 真正的执行跳转的拦截器, 如果正常跳转了 DoActivityStartInterceptor 拦截器就直接返回了
                    // 如果没有正常跳转过去, 内部会继续走拦截器, 会执行到后面的这个
                    mAllInterceptors.add(new DoActivityStartInterceptor(mOriginalRequest));
                } else {
                    mAllInterceptors.add(mPageInterceptors.get(mPageIndex));
                    mAllInterceptors.add(
                            new PageInterceptorUriCheckInterceptor(
                                    mOriginalRequest, mAllInterceptors, mBeforPageInterceptorUri,
                                    mPageInterceptors, ++mPageIndex
                            )
                    );
                }
            } else {
                mAllInterceptors.add(new PageInterceptor(mOriginalRequest, mAllInterceptors));
            }
            // 执行下一个拦截器,正好是上面代码添加的拦截器
            chain.proceed(chain.request());

        }
    }

    /**
     * 这是拦截器的最后一个拦截器了
     * 实现拦截器列表中的最后一环, 内部去执行了跳转的代码
     * 1.如果跳转的时候没有发生异常, 说明可以跳转过去
     * 如果失败了进行降级处理
     */
    @UiThread
    private static class DoActivityStartInterceptor implements RouterInterceptor {

        @NonNull
        private final RouterRequest mOriginalRequest;

        public DoActivityStartInterceptor(@NonNull RouterRequest originalRequest) {
            mOriginalRequest = originalRequest;
        }

        /**
         * @param chain 拦截器执行连接器
         * @throws Exception
         */
        @Override
        @UiThread
        public void intercept(final Chain chain) throws Exception {
            // 这个 request 对象已经不是最原始的了,但是可能是最原始的,就看拦截器是否更改了这个对象了
            RouterRequest finalRequest = chain.request();
            // 执行真正路由跳转回出现的异常
            Exception routeException = null;
            try {
                // 真正执行跳转的逻辑, 失败的话, 备用计划就会启动
                RouterCenter.getInstance().openUri(finalRequest);
            } catch (Exception e) { // 错误的话继续下一个拦截器
                routeException = e;
                // 继续下一个拦截器
                chain.proceed(finalRequest);
            }
            // 如果正常跳转成功需要执行下面的代码
            if (routeException == null) {
                // 成功的回调
                chain.callback().onSuccess(new RouterResult(mOriginalRequest, finalRequest));
            } else {
                try {
                    // 获取路由的降级处理类
                    RouterDegrade routerDegrade = getRouterDegrade(finalRequest);
                    if (routerDegrade == null) {
                        // 抛出异常走 try catch 的逻辑
                        throw new NavigationFailException("degrade route fail, it's url is " + mOriginalRequest.uri.toString());
                    }
                    // 降级跳转
                    RouterCenter.getInstance().routerDegrade(
                            finalRequest,
                            routerDegrade.onDegrade(finalRequest)
                    );
                    // 成功的回调
                    chain.callback().onSuccess(new RouterResult(mOriginalRequest, finalRequest));
                } catch (Exception ignore) {
                    // 如果版本足够就添加到异常堆中, 否则忽略降级路由的错误
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        routeException.addSuppressed(ignore);
                    }
                    throw routeException;
                }
            }
        }

        /**
         * 获取降级的处理类
         *
         * @param finalRequest 最终的路由请求
         */
        @Nullable
        private RouterDegrade getRouterDegrade(@NonNull RouterRequest finalRequest) {
            // 获取所有降级类
            List<RouterDegrade> routerDegradeList = RouterDegradeCenter.getInstance()
                    .getGlobalRouterDegradeList();
            RouterDegrade result = null;
            for (int i = 0; i < routerDegradeList.size(); i++) {
                RouterDegrade routerDegrade = routerDegradeList.get(i);
                // 如果匹配
                boolean isMatch = routerDegrade.isMatch(finalRequest);
                if (isMatch) {
                    result = routerDegrade;
                    break;
                }
            }
            return result;
        }

    }

    /**
     * 一些帮助方法
     */
    private static class Help {

        /**
         * 和{@link RouterFragment} 配套使用
         */
        private static Set<String> mRequestCodeSet = new HashSet<>();

        private static Random r = new Random();

        /**
         * 如果 requestCode 是 {@link Navigator#RANDOM_REQUSET_CODE}.
         * 则随机生成一个 requestCode
         *
         * @return [1, 256]
         */
        @NonNull
        public static RouterRequest randomlyGenerateRequestCode(@NonNull RouterRequest request) {
            Utils.checkNullPointer(request, "request");
            // 如果不是想要随机生成,就直接返回
            if (!Navigator.RANDOM_REQUSET_CODE.equals(request.requestCode)) {
                return request;
            }
            // 转化为构建对象
            RouterRequest.Builder requestBuilder = request.toBuilder();
            int generateRequestCode = r.nextInt(256) + 1;
            // 如果生成的这个 requestCode 存在,就重新生成
            while (isExist(Utils.getActivityFromContext(requestBuilder.context), requestBuilder.fragment, generateRequestCode)) {
                generateRequestCode = r.nextInt(256) + 1;
            }
            return requestBuilder.requestCode(generateRequestCode).build();
        }

        /**
         * 检测同一个 Fragment 或者 Activity 发起的多个路由 request 中的 requestCode 是否存在了
         *
         * @param request 路由请求对象
         */
        public static boolean isExist(@Nullable RouterRequest request) {
            if (request == null || request.requestCode == null) {
                return false;
            }
            // 这个 Context 关联的 Activity,用requestCode 去拿数据的情况下
            // Context 必须是一个 Activity 或者 内部的 baseContext 是 Activity
            Activity act = Utils.getActivityFromContext(request.context);
            // 这个requestCode不会为空, 用这个方法的地方是必须填写 requestCode 的
            return isExist(act, request.fragment, request.requestCode);
        }

        /**
         * 这里分别检测 {@link Activity}、{@link Fragment} 和 requestCode 的重复.
         * 即使一个路由使用了 {@link Activity} + 123, 另一个用 {@link Fragment} + 123 也没问题是因为
         * 这两个分别会被预埋一个 {@link RouterFragment}.
         * 所以他们共享一个{@link RouterFragment} 接受 {@link ActivityResult} 的
         */
        public static boolean isExist(@Nullable Activity act, @Nullable Fragment fragment,
                                      @NonNull Integer requestCode) {
            if (act != null) {
                return mRequestCodeSet.contains(act.getClass().getName() + requestCode);
            } else if (fragment != null) {
                return mRequestCodeSet.contains(fragment.getClass().getName() + requestCode);
            }
            return false;
        }

        /**
         * 添加一个路由请求的 requestCode
         *
         * @param request 路由请求对象
         */
        public static void addRequestCode(@Nullable RouterRequest request) {
            if (request == null || request.requestCode == null) {
                return;
            }
            Integer requestCode = request.requestCode;
            // 这个 Context 关联的 Activity,用requestCode 去拿数据的情况下
            // Context 必须是一个 Activity 或者 内部的 baseContext 是 Activity
            Activity act = Utils.getActivityFromContext(request.context);
            if (act != null) {
                mRequestCodeSet.add(act.getClass().getName() + requestCode);
            } else if (request.fragment != null) {
                mRequestCodeSet.add(request.fragment.getClass().getName() + requestCode);
            }
        }

        /**
         * 移除一个路由请求的 requestCode
         *
         * @param request 路由请求对象
         */
        public static void removeRequestCode(@Nullable RouterRequest request) {
            if (request == null || request.requestCode == null) {
                return;
            }
            Integer requestCode = request.requestCode;
            // 这个 Context 关联的 Activity,用requestCode 去拿数据的情况下
            // Context 必须是一个 Activity 或者 内部的 baseContext 是 Activity
            Activity act = Utils.getActivityFromContext(request.context);
            if (act != null) {
                mRequestCodeSet.remove(act.getClass().getName() + requestCode);
            } else if (request.fragment != null) {
                mRequestCodeSet.remove(request.fragment.getClass().getName() + requestCode);
            }
        }

    }


}