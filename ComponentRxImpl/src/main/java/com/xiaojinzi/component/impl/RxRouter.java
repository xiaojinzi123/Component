package com.xiaojinzi.component.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CheckResult;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.error.UnknowException;
import com.xiaojinzi.component.error.ignore.ActivityResultException;
import com.xiaojinzi.component.error.ignore.InterceptorNotFoundException;
import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.error.ignore.TargetActivityNotFoundException;
import com.xiaojinzi.component.impl.fragment.RxFragmentManager;
import com.xiaojinzi.component.support.Action;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.component.support.NavigationDisposable;
import com.xiaojinzi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;

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
@CheckClassName
public class RxRouter extends Router {

    @NonNull
    public static RxFragmentNavigator with(@NonNull String fragmentFlag) {
        Utils.checkNullPointer(fragmentFlag, "fragmentFlag");
        return new RxFragmentNavigator(fragmentFlag);
    }

    public static RxNavigator with() {
        return new RxNavigator();
    }

    /**
     * 这个方法父类也有一个静态的,但是父类返回的是 {@link Navigator} 而这个返回的是
     * {@link RxNavigator}
     *
     * @param context
     * @return
     */
    public static RxNavigator with(@NonNull Context context) {
        return new RxNavigator(context);
    }

    /**
     * 这个方法父类也有一个静态的,但是父类返回的是 {@link Navigator} 而这个返回的是
     * {@link RxNavigator}
     *
     * @param fragment
     * @return
     */
    public static RxNavigator with(@NonNull Fragment fragment) {
        return new RxNavigator(fragment);
    }

    public static class RxFragmentNavigator extends FragmentNavigator {

        public RxFragmentNavigator(@NonNull String fragmentFlag) {
            super(fragmentFlag);
        }

        @NonNull
        public Single<Fragment> call() {
            return RxFragmentManager.with(fragmentFlag, bundle);
        }

    }

    public static class RxNavigator extends Navigator {

        private RxNavigator() {
            super();
        }

        private RxNavigator(@NonNull Context context) {
            super(context);
        }

        private RxNavigator(@NonNull Fragment fragment) {
            super(fragment);
        }

        @Override
        public RxNavigator beforJumpAction(@NonNull Action action) {
            return (RxNavigator) super.beforJumpAction(action);
        }

        @Override
        public RxNavigator afterJumpAction(@NonNull Action action) {
            return (RxNavigator) super.afterJumpAction(action);
        }

        @Override
        public RxNavigator afterErrorAction(@Nullable Action action) {
            return (RxNavigator) super.afterErrorAction(action);
        }

        @Override
        public RxNavigator afterEventAction(@Nullable Action action) {
            return (RxNavigator) super.afterEventAction(action);
        }

        @Override
        public RxNavigator intentConsumer(@NonNull com.xiaojinzi.component.support.Consumer<Intent> intentConsumer) {
            return (RxNavigator) super.intentConsumer(intentConsumer);
        }

        @Override
        public RxNavigator addIntentFlags(@Nullable Integer... flags) {
            return (RxNavigator) super.addIntentFlags(flags);
        }

        @Override
        public RxNavigator addIntentCategories(@Nullable String... categories) {
            return (RxNavigator) super.addIntentCategories(categories);
        }

        @Override
        public RxNavigator interceptors(@NonNull RouterInterceptor... interceptors) {
            return (RxNavigator) super.interceptors(interceptors);
        }

        @Override
        public RxNavigator interceptors(@NonNull Class<? extends RouterInterceptor>... interceptors) {
            return (RxNavigator) super.interceptors(interceptors);
        }

        @Override
        public RxNavigator interceptorNames(@NonNull String... interceptors) {
            return (RxNavigator) super.interceptorNames(interceptors);
        }

        @Override
        public RxNavigator url(@NonNull String url) {
            return (RxNavigator) super.url(url);
        }

        @Override
        public RxNavigator scheme(@NonNull String scheme) {
            return (RxNavigator) super.scheme(scheme);
        }

        @Override
        public RxNavigator hostAndPath(@NonNull String hostAndPath) {
            return (RxNavigator) super.hostAndPath(hostAndPath);
        }

        @Override
        public RxNavigator host(@NonNull String host) {
            return (RxNavigator) super.host(host);
        }

        @Override
        public RxNavigator path(@NonNull String path) {
            return (RxNavigator) super.path(path);
        }

        @Override
        public RxNavigator requestCode(@Nullable Integer requestCode) {
            return (RxNavigator) super.requestCode(requestCode);
        }

        @Override
        public RxNavigator options(@Nullable Bundle options) {
            return (RxNavigator) super.options(options);
        }

        /**
         * requestCode 会随机的生成
         *
         * @return
         */
        public RxNavigator requestCodeRandom() {
            return requestCode(RANDOM_REQUSET_CODE);
        }

        @Override
        public RxNavigator putBundle(@NonNull String key, @Nullable Bundle bundle) {
            return (RxNavigator) super.putBundle(key, bundle);
        }

        @Override
        public RxNavigator putAll(@NonNull Bundle bundle) {
            return (RxNavigator) super.putAll(bundle);
        }

        @Override
        public RxNavigator putCharSequence(@NonNull String key, @Nullable CharSequence value) {
            return (RxNavigator) super.putCharSequence(key, value);
        }

        @Override
        public RxNavigator putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
            return (RxNavigator) super.putCharSequenceArray(key, value);
        }

        @Override
        public RxNavigator putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
            return (RxNavigator) super.putCharSequenceArrayList(key, value);
        }

        @Override
        public RxNavigator putByte(@NonNull String key, @Nullable byte value) {
            return (RxNavigator) super.putByte(key, value);
        }

        @Override
        public RxNavigator putByteArray(@NonNull String key, @Nullable byte[] value) {
            return (RxNavigator) super.putByteArray(key, value);
        }

        @Override
        public RxNavigator putChar(@NonNull String key, @Nullable char value) {
            return (RxNavigator) super.putChar(key, value);
        }

        @Override
        public RxNavigator putCharArray(@NonNull String key, @Nullable char[] value) {
            return (RxNavigator) super.putCharArray(key, value);
        }

        @Override
        public RxNavigator putBoolean(@NonNull String key, @Nullable boolean value) {
            return (RxNavigator) super.putBoolean(key, value);
        }

        @Override
        public RxNavigator putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
            return (RxNavigator) super.putBooleanArray(key, value);
        }

        @Override
        public RxNavigator putString(@NonNull String key, @Nullable String value) {
            return (RxNavigator) super.putString(key, value);
        }

        @Override
        public RxNavigator putStringArray(@NonNull String key, @Nullable String[] value) {
            return (RxNavigator) super.putStringArray(key, value);
        }

        @Override
        public RxNavigator putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
            return (RxNavigator) super.putStringArrayList(key, value);
        }

        @Override
        public RxNavigator putShort(@NonNull String key, @Nullable short value) {
            return (RxNavigator) super.putShort(key, value);
        }

        @Override
        public RxNavigator putShortArray(@NonNull String key, @Nullable short[] value) {
            return (RxNavigator) super.putShortArray(key, value);
        }

        @Override
        public RxNavigator putInt(@NonNull String key, @Nullable int value) {
            return (RxNavigator) super.putInt(key, value);
        }

        @Override
        public RxNavigator putIntArray(@NonNull String key, @Nullable int[] value) {
            return (RxNavigator) super.putIntArray(key, value);
        }

        @Override
        public RxNavigator putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
            return (RxNavigator) super.putIntegerArrayList(key, value);
        }

        @Override
        public RxNavigator putLong(@NonNull String key, @Nullable long value) {
            return (RxNavigator) super.putLong(key, value);
        }

        @Override
        public RxNavigator putLongArray(@NonNull String key, @Nullable long[] value) {
            return (RxNavigator) super.putLongArray(key, value);
        }

        @Override
        public RxNavigator putFloat(@NonNull String key, @Nullable float value) {
            return (RxNavigator) super.putFloat(key, value);
        }

        @Override
        public RxNavigator putFloatArray(@NonNull String key, @Nullable float[] value) {
            return (RxNavigator) super.putFloatArray(key, value);
        }

        @Override
        public RxNavigator putDouble(@NonNull String key, @Nullable double value) {
            return (RxNavigator) super.putDouble(key, value);
        }

        @Override
        public RxNavigator putDoubleArray(@NonNull String key, @Nullable double[] value) {
            return (RxNavigator) super.putDoubleArray(key, value);
        }

        @Override
        public RxNavigator putParcelable(@NonNull String key, @Nullable Parcelable value) {
            return (RxNavigator) super.putParcelable(key, value);
        }

        @Override
        public RxNavigator putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
            return (RxNavigator) super.putParcelableArray(key, value);
        }

        @Override
        public RxNavigator putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
            return (RxNavigator) super.putParcelableArrayList(key, value);
        }

        @Override
        public RxNavigator putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
            return (RxNavigator) super.putSparseParcelableArray(key, value);
        }

        @Override
        public RxNavigator putSerializable(@NonNull String key, @Nullable Serializable value) {
            return (RxNavigator) super.putSerializable(key, value);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, @Nullable String queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, boolean queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, byte queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, int queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, float queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, long queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        @Override
        public RxNavigator query(@NonNull String queryName, double queryValue) {
            return (RxNavigator) super.query(queryName, queryValue);
        }

        /**
         * 一个可以拿到 Intent 的 Observable
         *
         * @return
         * @see #activityResultCall()
         */
        @NonNull
        @CheckResult
        public Single<Intent> intentCall() {
            return activityResultCall()
                    .map(new Function<ActivityResult, Intent>() {
                        @Override
                        public Intent apply(ActivityResult activityResult) throws Exception {
                            return activityResult.intentCheckAndGet();
                        }
                    });
        }

        /**
         * 拿到 resultCode 的 Observable
         */
        @NonNull
        @CheckResult
        public Single<Integer> resultCodeCall() {
            return activityResultCall()
                    .map(new Function<ActivityResult, Integer>() {
                        @Override
                        public Integer apply(ActivityResult activityResult) throws Exception {
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
        @NonNull
        @CheckResult
        public Completable resultCodeMatchCall(final int expectedResultCode) {
            return activityResultCall()
                    .doOnSuccess(new Consumer<ActivityResult>() {
                        @Override
                        public void accept(ActivityResult activityResult) throws Exception {
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
        @NonNull
        @CheckResult
        public Single<Intent> intentResultCodeMatchCall(final int expectedResultCode) {
            return activityResultCall()
                    .map(new Function<ActivityResult, Intent>() {
                        @Override
                        public Intent apply(ActivityResult activityResult) throws Exception {
                            return activityResult.intentWithResultCodeCheckAndGet(expectedResultCode);
                        }
                    });
        }

        /**
         * 一个可以拿到 ActivityResult 的路由 Observable
         */
        @NonNull
        @CheckResult
        public Single<ActivityResult> activityResultCall() {
            return Single.create(new SingleOnSubscribe<ActivityResult>() {
                @Override
                public void subscribe(final SingleEmitter<ActivityResult> emitter) throws Exception {
                    if (emitter.isDisposed()) {
                        return;
                    }
                    final NavigationDisposable navigationDisposable = navigateForResult(new BiCallback.BiCallbackAdapter<ActivityResult>() {
                        @Override
                        public void onSuccess(@NonNull RouterResult result, @NonNull ActivityResult activityResult) {
                            super.onSuccess(result, activityResult);
                            if (emitter.isDisposed()) {
                                return;
                            }
                            emitter.onSuccess(activityResult);
                        }

                        @Override
                        public void onError(@NonNull RouterErrorResult errorResult) {
                            super.onError(errorResult);
                            if (emitter.isDisposed()) {
                                return;
                            }
                            RxHelp.onErrorSolve(emitter, errorResult.getError());
                        }

                        @Override
                        public void onCancel(@NonNull RouterRequest originalRequest) {
                            super.onCancel(originalRequest);
                        }
                    });
                    emitter.setCancellable(new Cancellable() {
                        @Override
                        public void cancel() throws Exception {
                            navigationDisposable.cancel();
                        }
                    });
                }
            });
        }

        /**
         * 一个完成状态的 Observable 的路由跳转
         */
        @NonNull
        @CheckResult
        public Completable call() {
            return Completable.create(new CompletableOnSubscribe() {
                @Override
                public void subscribe(final CompletableEmitter emitter) throws Exception {
                    if (emitter.isDisposed()) {
                        return;
                    }
                    // 导航拿到 NavigationDisposable 对象
                    // 可能是一个 空实现,这些个回调都是回调在主线程的
                    final NavigationDisposable navigationDisposable = navigate(new CallbackAdapter() {
                        @Override
                        @MainThread
                        public void onSuccess(@NonNull RouterResult routerResult) {
                            super.onSuccess(routerResult);
                            if (emitter != null && !emitter.isDisposed()) {
                                emitter.onComplete();
                            }
                        }

                        @Override
                        @MainThread
                        public void onError(@NonNull RouterErrorResult errorResult) {
                            super.onError(errorResult);
                            RxHelp.onErrorSolve(emitter, errorResult.getError());
                        }
                    });
                    // 设置取消
                    emitter.setCancellable(new Cancellable() {
                        @Override
                        public void cancel() throws Exception {
                            navigationDisposable.cancel();
                        }
                    });
                }
            });
        }

    }

    /**
     * 一些帮助方法
     */
    private static class RxHelp {

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
         * 发射错误,目前这些个发射错误都是为了 {@link RxRouter} 写的,发射的错误和正确的 item 被发射都应该
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
         * 发射错误,目前这些个发射错误都是为了 {@link RxRouter} 写的,发射的错误和正确的 item 被发射都应该
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
