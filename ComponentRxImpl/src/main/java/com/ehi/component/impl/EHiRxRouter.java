package com.ehi.component.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import com.ehi.component.ComponentUtil;
import com.ehi.component.error.NavigationFailException;
import com.ehi.component.error.TargetActivityNotFoundException;
import com.ehi.component.error.UnknowException;
import com.ehi.component.support.EHiCallbackAdapter;

import java.io.Serializable;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * 使用这个可以结合 RxJava 中的{@link io.reactivex.Single} 使用,会很方便
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRxRouter {

    /**
     * 必须初始化的时候调用,错误就会自动抓住
     */
    public static void tryErrorCatch() {
        Consumer<Throwable> preErrorHandler = RxJavaPlugins.getErrorHandler();
        RxJavaPlugins.setErrorHandler(new RxRouterConsumer(preErrorHandler));
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context, null);
    }

    public static Builder withFragment(@NonNull Fragment fragment) {
        return new Builder(fragment, null);
    }

    public static class Builder extends EHiRouter.Builder {

        private Integer resultCode;

        private Builder(@NonNull Context context, String url) {
            super(context, url);
        }

        private Builder(@NonNull Fragment fragment, String url) {
            super(fragment, url);
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

        public Builder resultCode(@Nullable Integer resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        @Override
        public Builder putBundle(@NonNull String key, @NonNull Bundle bundle) {
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

        public <T> SingleTransformer<T, Intent> newIntentTransformer() {

            return new SingleTransformer<T, Intent>() {
                @Override
                public SingleSource<Intent> apply(Single<T> upstream) {
                    return upstream.flatMap(new Function<T, SingleSource<? extends Intent>>() {
                        @Override
                        public SingleSource<Intent> apply(T t) throws Exception {
                            return newIntentCall();
                        }
                    });
                }
            };

        }

        private void onErrorEmitter(@NonNull final SingleEmitter<Intent> emitter,
                                    @NonNull Exception e) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(e);
        }

        public Single<Intent> newIntentCall() {

            return Single.create(new SingleOnSubscribe<Intent>() {
                @Override
                public void subscribe(final SingleEmitter<Intent> emitter) throws Exception {

                    if (emitter.isDisposed()) {
                        return;
                    }

                    if (isFinish) {
                        onErrorEmitter(emitter, new NavigationFailException("EHiRouter.Builder can't be used multiple times"));
                        return;
                    }

                    if (EHiRouterUtil.isMainThread() == false) {
                        onErrorEmitter(emitter, new NavigationFailException("EHiRxRouter must run on main thread"));
                        return;
                    }

                    if (context == null && fragment == null) {
                        onErrorEmitter(emitter, new NavigationFailException(new NullPointerException("Context or Fragment is necessary for router")));
                        return;
                    }

                    if (context != null && (context instanceof FragmentActivity) == false) {
                        onErrorEmitter(emitter, new NavigationFailException(new IllegalArgumentException("Context must be FragmentActivity")));
                        return;
                    }

                    if (requestCode == null) {
                        onErrorEmitter(emitter, new NavigationFailException(new NullPointerException("requestCode must not be null for router")));
                        return;
                    }

                    try {

                        FragmentManager fm = null;
                        if (context == null) {
                            fm = fragment.getChildFragmentManager();
                        } else {
                            fm = ((FragmentActivity) context).getSupportFragmentManager();
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
                        final String mHost = host;
                        final String mPath = path;
                        final int mRequesetCode = requestCode;

                        if (rxFragment.isContainsSingleEmitter(mRequesetCode)) {
                            onErrorEmitter(emitter, new NavigationFailException("request&result code: " + requestCode + " can't be same"));
                        }

                        navigate(new EHiCallbackAdapter() {
                            @Override
                            public void onEvent(@Nullable EHiRouterResult routerResult, @Nullable Exception error) {
                                try {
                                    if (routerResult != null) {
                                        // 设置ActivityResult回调的发射器
                                        rxFragment.setSingleEmitter(emitter, mRequesetCode);
                                    } else {
                                        if (error != null) {
                                            throw error;
                                        } else {
                                            throw new NavigationFailException("host = " + mHost + ",path = " + mPath);
                                        }
                                    }
                                } catch (TargetActivityNotFoundException e) {
                                    onErrorEmitter(emitter, e);
                                } catch (NavigationFailException e) {
                                    onErrorEmitter(emitter, e);
                                } catch (Exception e) {
                                    onErrorEmitter(emitter, new UnknowException(e));
                                }

                            }
                        });

                    } catch (TargetActivityNotFoundException e) {
                        onErrorEmitter(emitter, e);
                    } catch (NavigationFailException e) {
                        onErrorEmitter(emitter, e);
                    } catch (Exception e) {
                        onErrorEmitter(emitter, new UnknowException(e));
                    }

                }
            });


        }

    }

}
