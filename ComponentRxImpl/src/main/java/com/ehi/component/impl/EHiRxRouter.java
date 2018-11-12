package com.ehi.component.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.ehi.component.EHiComponentUtil;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

/**
 * 使用这个可以结合 RxJava 中的{@link io.reactivex.Single} 使用,会很方便
 * <p>
 * time   : 2018/11/03
 *
 * @author : xiaojinzi 30212
 */
public class EHiRxRouter {

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
        public Builder bundle(@NonNull Bundle bundle) {
            return (Builder) super.bundle(bundle);
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

        public Single<Intent> newIntentCall() {

            return Single.create(new SingleOnSubscribe<Intent>() {
                @Override
                public void subscribe(SingleEmitter<Intent> emitter) throws Exception {

                    if (emitter.isDisposed()) {
                        return;
                    }

                    if (isFinish) {
                        emitter.onError(new RuntimeException("EHiRouter.Builder can't be used multiple times"));
                        return;
                    }

                    Thread currentThread = Thread.currentThread();
                    if (currentThread != Looper.getMainLooper().getThread()) {
                        emitter.onError(new RuntimeException("EHiRxRouter must run on main thread"));
                        return;
                    }

                    if (context == null && fragment == null) {
                        emitter.onError(new NullPointerException("Context or Fragment is necessary for router"));
                        return;
                    }

                    if (context != null && (context instanceof FragmentActivity) == false) {
                        emitter.onError(new IllegalArgumentException("Context is be FragmentActivity"));
                        return;
                    }

                    if (requestCode == null) {
                        emitter.onError(new NullPointerException("requestCode must not be null for router"));
                        return;
                    }

                    FragmentManager fm = null;
                    if (context == null) {
                        fm = fragment.getChildFragmentManager();
                    } else {
                        fm = ((FragmentActivity) context).getSupportFragmentManager();
                    }

                    // 寻找是否添加过 Fragment
                    EHiRxFragment rxFragment = (EHiRxFragment) fm.findFragmentByTag(EHiComponentUtil.FRAGMENT_TAG);
                    if (rxFragment == null) {
                        rxFragment = new EHiRxFragment();
                        fm.beginTransaction()
                                .add(rxFragment, EHiComponentUtil.FRAGMENT_TAG)
                                .commitNow();
                    }

                    // 导航方法执行完毕之后,内部的数据就会清空,所以之前必须缓存
                    final String mHost = host;
                    final String mPath = path;
                    final int mRequesetCode = requestCode;

                    try {
                        if (rxFragment.isContainsSingleEmitter(mRequesetCode)) {
                            throw new RuntimeException("request&result code: " + requestCode + " can't be same");
                        }
                        EHiRouterResult routerResult = navigate(true);
                        if (routerResult.isSuccess()) {
                            // 设置ActivityResult回调的发射器
                            rxFragment.setSingleEmitter(emitter, mRequesetCode);
                        } else {
                            if (routerResult.getError() != null) {
                                throw routerResult.getError();
                            } else {
                                throw new EHiNavigationFailException("host = " + mHost + ",path = " + mPath);
                            }
                        }
                    } catch (Exception e) {
                        emitter.onError(e);
                    }

                }
            });


        }

    }

}
