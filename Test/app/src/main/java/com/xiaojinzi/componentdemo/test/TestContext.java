package com.xiaojinzi.componentdemo.test;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.xiaojinzi.component.error.ignore.NavigationFailException;
import com.xiaojinzi.component.impl.BiCallback;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;
import com.xiaojinzi.component.support.CallbackAdapter;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;

public interface TestContext {

    interface TestBack {
        void run(CompletableEmitter emitter);
    }

    /**
     * 获取上下文
     *
     * @return
     */
    @NonNull
    Context context();

    /**
     * 获取Fragment
     *
     * @return
     */
    @NonNull
    Fragment fragment();

    @NonNull
    Dialog dialog();

    /**
     * 输出日志
     *
     * @param msg
     */
    void log(@NonNull String msg);

    Completable wrapTask(Completable completable);

    Completable testWrap(TestBack testBack);

    Completable testWrapWithChildThread(TestBack testBack);

    void addTaskPassMsg(String taskName);

    class CallbackSuccessIsSuccessful extends CallbackAdapter {

        @NonNull
        private CompletableEmitter emitter;

        public CallbackSuccessIsSuccessful(CompletableEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onComplete();
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(errorResult.getError());
        }

        @Override
        public void onCancel(@NonNull RouterRequest request) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be success"));
        }

    }

    class CallbackCancelIsSuccessful extends CallbackAdapter {

        @NonNull
        private CompletableEmitter emitter;

        public CallbackCancelIsSuccessful(CompletableEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be cancel"));
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(errorResult.getError());
        }

        @Override
        public void onCancel(@NonNull RouterRequest request) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onComplete();
        }

    }

    class CallbackErrorIsSuccessful extends CallbackAdapter {

        @NonNull
        private CompletableEmitter emitter;

        public CallbackErrorIsSuccessful(CompletableEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be error"));
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onComplete();
        }

        @Override
        public void onCancel(@NonNull RouterRequest request) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be error"));
        }

    }

    class BiCallbackSuccessIsSuccessful<T> extends BiCallback.BiCallbackAdapter<T> {

        @NonNull
        private CompletableEmitter emitter;

        public BiCallbackSuccessIsSuccessful(CompletableEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull T t) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onComplete();
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(errorResult.getError());
        }

        @Override
        public void onCancel(@NonNull RouterRequest request) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be success"));
        }

    }

    class BiCallbackCancelIsSuccessful<T> extends BiCallback.BiCallbackAdapter<T> {

        @NonNull
        private CompletableEmitter emitter;

        public BiCallbackCancelIsSuccessful(CompletableEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull T t) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be cancel"));
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(errorResult.getError());
        }

        @Override
        public void onCancel(@NonNull RouterRequest request) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onComplete();
        }

    }

    class BiCallbackErrorIsSuccessful<T> extends BiCallback.BiCallbackAdapter<T> {

        @NonNull
        private CompletableEmitter emitter;

        public BiCallbackErrorIsSuccessful(CompletableEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onSuccess(@NonNull RouterResult result, @NonNull T t) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be error"));
        }

        @Override
        public void onError(@NonNull RouterErrorResult errorResult) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onComplete();
        }

        @Override
        public void onCancel(@NonNull RouterRequest request) {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onError(new NavigationFailException("request should be error"));
        }

    }

}
