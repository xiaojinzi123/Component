package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import com.ehi.component.ComponentUtil;
import com.ehi.component.bean.EHiActivityResult;
import com.ehi.component.error.ActivityResultException;
import com.ehi.component.error.UnknowException;
import com.ehi.component.error.ignore.InterceptorNotFoundException;
import com.ehi.component.error.ignore.NavigationFailException;
import com.ehi.component.error.ignore.TargetActivityNotFoundException;
import com.ehi.component.support.Action;
import com.ehi.component.support.EHiCallbackAdapter;
import com.ehi.component.support.LogUtil;
import com.ehi.component.support.NavigationDisposable;
import com.ehi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 使用这个可以结合 RxJava 中的{@link io.reactivex.Single} 使用,会很方便
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRxRouter extends Router {

    /**
     * requestCode 如果等于这个值,就表示是随机生成的
     * 从 1-256 中随机生成一个,如果生成的正好是目前正在用的,会重新生成一个
     */
    public static final Integer RANDOM_REQUSET_CODE = Integer.MIN_VALUE;

    public static final String TAG = "EHiRxRouter";

    /**
     * 这个方法父类也有一个静态的,但是父类返回的是 {@link Router.Builder} 而这个返回的是
     * {@link EHiRxRouter.Builder}
     *
     * @param context
     * @return
     */
    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    /**
     * 这个方法父类也有一个静态的,但是父类返回的是 {@link Router.Builder} 而这个返回的是
     * {@link EHiRxRouter.Builder}
     *
     * @param fragment
     * @return
     */
    public static Builder withFragment(@NonNull Fragment fragment) {
        return new Builder(fragment);
    }

    public static class Builder extends Router.Builder {

        private Builder(@NonNull Context context) {
            super(context);
        }

        private Builder(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        public Builder beforJumpAction(@NonNull Action action) {
            return (Builder) super.beforJumpAction(action);
        }

        @Override
        public Builder afterJumpAction(@NonNull Action action) {
            return (Builder) super.afterJumpAction(action);
        }

        @Override
        public Builder intentConsumer(@NonNull com.ehi.component.support.Consumer<Intent> intentConsumer) {
            return (Builder) super.intentConsumer(intentConsumer);
        }

        @Override
        public Builder interceptors(@NonNull EHiRouterInterceptor... interceptors) {
            return (Builder) super.interceptors(interceptors);
        }

        @Override
        public Builder interceptors(@NonNull Class<? extends EHiRouterInterceptor>... interceptors) {
            return (Builder) super.interceptors(interceptors);
        }

        @Override
        public Builder interceptorNames(@NonNull String... interceptors) {
            return (Builder) super.interceptorNames(interceptors);
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
        public Builder path(@NonNull String path) {
            return (Builder) super.path(path);
        }

        @Override
        public Builder requestCode(@Nullable Integer requestCode) {
            return (Builder) super.requestCode(requestCode);
        }

        /**
         * requestCode 会随机的生成
         *
         * @return
         */
        public Builder requestCodeRandom() {
            return requestCode(RANDOM_REQUSET_CODE);
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
         * 一个可以拿到 Intent 的 Observable
         *
         * @return
         * @see #activityResultCall()
         */
        public Single<Intent> intentCall() {
            return activityResultCall()
                    .doOnSuccess(new Consumer<EHiActivityResult>() {
                        @Override
                        public void accept(EHiActivityResult activityResult) throws Exception {
                            if (activityResult.data == null) {
                                throw new ActivityResultException("the intent result data is null");
                            }
                        }
                    })
                    .map(new Function<EHiActivityResult, Intent>() {
                        @Override
                        public Intent apply(EHiActivityResult activityResult) throws Exception {
                            return activityResult.data;
                        }
                    });
        }

        /**
         * 拿到 resultCode 的 Observable
         *
         * @return
         */
        public Single<Integer> resultCodeCall() {
            return activityResultCall()
                    .map(new Function<EHiActivityResult, Integer>() {
                        @Override
                        public Integer apply(EHiActivityResult activityResult) throws Exception {
                            return activityResult.resultCode;
                        }
                    });
        }

        /**
         * requestCode 一定是相同的,resultCode 如果匹配了就剩下 Intent 参数了
         * 这个方法不会给你 Intent 对象,只会给你是否 resultCode 匹配成功了
         * 那么
         *
         * @param expectedResultCode 期望的 resultCode 的值
         * @return 返回一个完成状态的 Observable
         * @see #activityResultCall()
         */
        public Completable resultCodeMatchCall(final int expectedResultCode) {
            return activityResultCall()
                    .doOnSuccess(new Consumer<EHiActivityResult>() {
                        @Override
                        public void accept(EHiActivityResult activityResult) throws Exception {
                            if (activityResult.resultCode != expectedResultCode) {
                                throw new ActivityResultException("the resultCode is not matching " + expectedResultCode);
                            }
                        }
                    })
                    .ignoreElement();
        }

        /**
         * 这个方法不仅可以匹配 resultCode,还可以拿到 Intent,当不匹配或者 Intent 为空的时候都会报错哦
         *
         * @param expectedResultCode 期望的 resultCode 的值
         * @return 返回一个发射 Single 的 Observable
         * @see #activityResultCall()
         */
        public Single<Intent> intentResultCodeMatchCall(final int expectedResultCode) {
            return activityResultCall()
                    .doOnSuccess(new Consumer<EHiActivityResult>() {
                        @Override
                        public void accept(EHiActivityResult activityResult) throws Exception {
                            if (activityResult.resultCode != expectedResultCode) {
                                throw new ActivityResultException("the resultCode is not matching " + expectedResultCode);
                            }
                            if (activityResult.data == null) {
                                throw new ActivityResultException("the intent result data is null");
                            }
                        }
                    })
                    .map(new Function<EHiActivityResult, Intent>() {
                        @Override
                        public Intent apply(EHiActivityResult activityResult) throws Exception {
                            return activityResult.data;
                        }
                    });
        }

        /**
         * 一个可以拿到 ActivityResult 的路由 Observable
         *
         * @return
         */
        public Single<EHiActivityResult> activityResultCall() {
            return Single.create(new SingleOnSubscribe<EHiActivityResult>() {
                @Override
                public void subscribe(final SingleEmitter<EHiActivityResult> emitter) throws Exception {
                    // 这里要运行在主线程的原因是因为这里要操作 Fragment,必须在主线程
                    Utils.postActionToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            doActivityResultCall(emitter);
                        }
                    });
                }
            });
        }

        /**
         * 拆分出的方法,降低方法复杂度
         *
         * @param emitter
         */
        private void doActivityResultCall(@NonNull final SingleEmitter<EHiActivityResult> emitter) {
            try {
                if (emitter.isDisposed()) {
                    return;
                }
                // 检查操作
                onCheck(true);
                // 声明fragment
                FragmentManager fm = null;
                if (context == null) {
                    fm = fragment.getChildFragmentManager();
                } else {
                    fm = ((FragmentActivity) Utils.getActivityFromContext(context)).getSupportFragmentManager();
                }
                // 寻找是否添加过 Fragment
                EHiRxFragment findRxFragment = (EHiRxFragment) fm.findFragmentByTag(ComponentUtil.FRAGMENT_TAG);
                if (findRxFragment == null) {
                    findRxFragment = new EHiRxFragment();
                    fm.beginTransaction()
                            .add(findRxFragment, ComponentUtil.FRAGMENT_TAG)
                            .commitNow();
                }
                final EHiRxFragment rxFragment = findRxFragment;
                // 导航方法执行完毕之后,内部的数据就会清空,所以之前必须缓存
                // 导航拿到 NavigationDisposable 对象
                // 可能是一个 空实现
                final NavigationDisposable navigationDisposable = navigate(new EHiCallbackAdapter() {
                    @Override
                    @MainThread
                    public void onSuccess(@NonNull final EHiRouterResult routerResult) {
                        super.onSuccess(routerResult);
                        // 设置ActivityResult回调的发射器,回调中一个路由拿数据的流程算是完毕了
                        rxFragment.setSingleEmitter(routerResult.getOriginalRequest(), new com.ehi.component.support.Consumer<EHiActivityResult>() {
                            @Override
                            public void accept(@NonNull EHiActivityResult result) throws Exception {
                                Help.removeRequestCode(routerResult.getOriginalRequest());
                                if (emitter != null && !emitter.isDisposed()) {
                                    emitter.onSuccess(result);
                                }
                            }
                        });
                    }

                    @Override
                    @MainThread
                    public void onError(@NonNull EHiRouterErrorResult errorResult) {
                        super.onError(errorResult);
                        Help.removeRequestCode(errorResult.getOriginalRequest());
                        Help.onErrorSolve(emitter, errorResult.getError());
                    }

                    @Override
                    @MainThread
                    public void onCancel(@NonNull RouterRequest originalRequest) {
                        super.onCancel(originalRequest);
                        rxFragment.cancal(originalRequest);
                        Help.removeRequestCode(originalRequest);
                    }

                });
                // 设置取消
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        navigationDisposable.cancel();
                    }
                });
                // 现在可以检测 requestCode 是否重复,除了 EHiRxRouter 之外的地方使用同一个 requestCode 是可以的
                // 因为 EHiRxRouter 的 requestCode 是直接配合 EHiRxFragment 使用的
                // 其他地方是用不到 EHiRxFragment,所以可以重复
                boolean isExist = Help.isExist(navigationDisposable.originalRequest());
                if (isExist) { // 如果存在直接取消这个路由任务,然后直接返回错误
                    navigationDisposable.cancel();
                    throw new NavigationFailException("request&result code is " +
                            navigationDisposable.originalRequest().requestCode + " is exist and " +
                            "uri is " + navigationDisposable.originalRequest().uri.toString());
                } else {
                    Help.addRequestCode(navigationDisposable.originalRequest());
                }

            } catch (Exception e) {
                LogUtil.log(TAG, "路由失败：" + Utils.getRealMessage(e));
                Help.onErrorSolve(emitter, e);
            }
        }

        /**
         * 生成路由请求对象
         * 如果有不满足生成的条件就会抛出异常
         *
         * @return
         */
        @Override
        public RouterRequest build() {
            return Help.randomlyGenerateRequestCode(super.build());
        }

        /**
         * 一个完成状态的 Observable 的路由跳转
         *
         * @return
         */
        public Completable call() {
            return Completable.create(new CompletableOnSubscribe() {
                @Override
                public void subscribe(final CompletableEmitter emitter) throws Exception {
                    try {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        // 参数检查
                        onCheck(false);
                        // 导航拿到 NavigationDisposable 对象
                        // 可能是一个 空实现,这些个回调都是回调在主线程的
                        final NavigationDisposable navigationDisposable = navigate(new EHiCallbackAdapter() {
                            @Override
                            @MainThread
                            public void onSuccess(@NonNull EHiRouterResult routerResult) {
                                super.onSuccess(routerResult);
                                if (emitter != null && !emitter.isDisposed()) {
                                    emitter.onComplete();
                                }
                            }

                            @Override
                            @MainThread
                            public void onError(@NonNull EHiRouterErrorResult errorResult) {
                                super.onError(errorResult);
                                Help.onErrorSolve(emitter, errorResult.getError());
                            }
                        });
                        // 设置取消
                        emitter.setCancellable(new Cancellable() {
                            @Override
                            public void cancel() throws Exception {
                                navigationDisposable.cancel();
                            }
                        });

                    } catch (Exception e) {
                        LogUtil.log(TAG, "路由失败：" + Utils.getRealMessage(e));
                        Help.onErrorSolve(emitter, e);
                    }
                }
            });
        }

        /**
         * 检查参数,这个方法和父类的 {@link #onCheck()} 很多项目都一样的,但是没办法
         * 这里的检查是需要提前检查的
         * 父类的检查是调用 {@link #navigate(EHiCallback)}方法的时候调用 {@link #onCheck()} 检查的
         * 这个类是调用 {@link #navigate(EHiCallback)} 方法之前检查的,而且检查的项目虽然基本一样,但是有所差别
         *
         * @throws RuntimeException
         */
        private void onCheck(boolean isForResult) {
            if (context == null && fragment == null) {
                throw new NavigationFailException(new NullPointerException("Context or Fragment is necessary for router"));
            }
            if (!(Utils.getActivityFromContext(context) instanceof FragmentActivity)) {
                throw new NavigationFailException(new IllegalArgumentException("Context must be FragmentActivity"));
            }
            if (isForResult && requestCode == null) {
                throw new NavigationFailException(new NullPointerException("requestCode must not be null for router"));
            }
        }

    }

    /**
     * 一些帮助方法
     */
    private static class Help {

        /**
         * 和{@link EHiRxFragment} 配套使用
         */
        private static Set<String> mRequestCodeSet = new HashSet<>();

        private static Random r = new Random();

        /**
         * 随机生成一个 requestCode,调用这个方法的 requestCode 是 {@link EHiRxRouter#RANDOM_REQUSET_CODE}
         *
         * @return [1, 256]
         */
        @NonNull
        public static RouterRequest randomlyGenerateRequestCode(@NonNull RouterRequest request) {
            Utils.checkNullPointer(request, "request");
            // 如果不是想要随机生成,就直接返回
            if (!EHiRxRouter.RANDOM_REQUSET_CODE.equals(request.requestCode)) {
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
         * @return
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

        public static boolean isExist(@Nullable Activity act, @Nullable Fragment fragment, @NonNull Integer requestCode) {
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

        /**
         * 错误处理
         */
        private static void onErrorSolve(@NonNull final SingleEmitter<? extends Object> emitter, @NonNull Throwable e) {
            if (e instanceof InterceptorNotFoundException) {
                onErrorEmitter(emitter, e);
            } else if (e instanceof NavigationFailException) {
                onErrorEmitter(emitter, e);
            } else if (e instanceof TargetActivityNotFoundException) {
                onErrorEmitter(emitter, e);
            } else if (e instanceof ActivityResultException) {
                onErrorEmitter(emitter, e);
            } else {
                onErrorEmitter(emitter, new UnknowException(e));
            }
        }

        /**
         * 错误处理
         */
        private static void onErrorSolve(@NonNull CompletableEmitter emitter, @NonNull Throwable e) {
            if (e instanceof InterceptorNotFoundException) {
                onErrorEmitter(emitter, e);
            } else if (e instanceof NavigationFailException) {
                onErrorEmitter(emitter, e);
            } else if (e instanceof TargetActivityNotFoundException) {
                onErrorEmitter(emitter, e);
            } else if (e instanceof ActivityResultException) {
                onErrorEmitter(emitter, e);
            } else {
                onErrorEmitter(emitter, new UnknowException(e));
            }
        }

        /**
         * 发射错误,目前这些个发射错误都是为了 {@link EHiRxRouter} 写的,发射的错误和正确的 item 被发射都应该
         * 最终发射在主线程
         *
         * @param emitter
         * @param e
         */
        private static void onErrorEmitter(@MainThread final SingleEmitter<? extends Object> emitter,
                                           @NonNull final Throwable e) {
            if (emitter == null || emitter.isDisposed()) {
                return;
            }
            if (Utils.isMainThread()) {
                emitter.onError(e);
            } else {
                Utils.postActionToMainThreadAnyway(new Runnable() {
                    @Override
                    public void run() {
                        emitter.onError(e);
                    }
                });
            }
        }

        /**
         * 发射错误,目前这些个发射错误都是为了 {@link EHiRxRouter} 写的,发射的错误和正确的 item 被发射都应该
         * 最终发射在主线程
         *
         * @param emitter
         * @param e
         */
        private static void onErrorEmitter(@MainThread final CompletableEmitter emitter,
                                           @NonNull final Throwable e) {
            if (emitter == null || emitter.isDisposed()) {
                return;
            }
            if (Utils.isMainThread()) {
                emitter.onError(e);
            } else {
                Utils.postActionToMainThreadAnyway(new Runnable() {
                    @Override
                    public void run() {
                        emitter.onError(e);
                    }
                });
            }
        }

    }

}
