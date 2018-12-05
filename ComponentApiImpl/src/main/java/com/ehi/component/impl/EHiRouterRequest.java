package com.ehi.component.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ehi.component.support.Consumer;

/**
 * 表示路由的一个请求类
 * '
 * time   : 2018/11/29
 *
 * @author : xiaojinzi 30212
 */
public class EHiRouterRequest {

    @Nullable
    public final Context context;

    @Nullable
    public final Fragment fragment;

    @NonNull
    public final Uri uri;

    @Nullable
    public final Integer requestCode;

    @NonNull
    public final Bundle bundle = new Bundle();

    @Nullable
    public final Consumer<Intent> intentConsumer;

    @Nullable
    public final Context getRawContext(){
        if (context == null) {
            return fragment.getContext();
        }else {
            return context;
        }
    }

    public Builder toBuilder(){
        Builder builder = new Builder();
        builder.bundle = bundle;
        builder.requestCode = requestCode;
        builder.uri = uri;
        builder.fragment = fragment;
        builder.context = context;
        builder.intentConsumer = intentConsumer;
        return builder;
    }

    private EHiRouterRequest(@NonNull Builder builder) {
        context = builder.context;
        fragment = builder.fragment;
        uri = builder.uri;
        requestCode = builder.requestCode;
        if (builder.bundle != null) {
            this.bundle.putAll(builder.bundle);
        }
        intentConsumer = builder.intentConsumer;
    }

    public static class Builder {

        @Nullable
        private Context context;

        @Nullable
        private Fragment fragment;

        @NonNull
        private Uri uri;

        @Nullable
        private Integer requestCode;

        @Nullable
        private Bundle bundle;

        @Nullable
        private Consumer<Intent> intentConsumer;

        public Builder context(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder fragment(@NonNull Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public Builder uri(@NonNull Uri uri) {
            this.uri = uri;
            return this;
        }

        public Builder requestCode(@NonNull Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder bundle(@NonNull Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder intentConsumer(@NonNull Consumer<Intent> intentConsumer) {
            this.intentConsumer = intentConsumer;
            return this;
        }

        public EHiRouterRequest build() {
            return new EHiRouterRequest(this);
        }

    }


}
