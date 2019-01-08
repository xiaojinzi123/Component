package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ehi.component.support.Action;
import com.ehi.component.support.Consumer;

/**
 * 表示路由的一个请求类
 * <p>
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
    public final Action beforAction;

    @Nullable
    public final Action afterAction;

    /**
     * get the Context
     *
     * @return return null if Activity is destoried
     */
    @Nullable
    public final Context getRawContext() {
        Context rawContext = null;
        if (context == null) {
            rawContext = fragment.getContext();
        } else {
            rawContext = context;
        }
        if (rawContext != null && rawContext instanceof Activity) {
            Activity activity = (Activity) rawContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return null;
                }
            } else {
                if (activity.isFinishing()) {
                    return null;
                }
            }
        }
        return rawContext;
    }

    /**
     * get the Activity
     *
     * @return return null if Activity is destoried
     */
    @Nullable
    public final Activity getRawActivity() {
        if (context == null) {
            return null;
        }
        if (!(context instanceof Activity)) {
            return null;
        }
        Activity rawActivity = (Activity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (rawActivity.isFinishing() || rawActivity.isDestroyed()) {
                return null;
            }
        } else {
            if (rawActivity.isFinishing()) {
                return null;
            }
        }
        return rawActivity;
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.bundle = bundle;
        builder.requestCode = requestCode;
        builder.uri = uri;
        builder.fragment = fragment;
        builder.context = context;
        builder.intentConsumer = intentConsumer;
        builder.befor = beforAction;
        builder.after = afterAction;
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
        beforAction = builder.befor;
        afterAction = builder.after;
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

        @Nullable
        private Action befor;

        @Nullable
        private Action after;

        public Builder context(@Nullable Context context) {
            this.context = context;
            return this;
        }

        public Builder fragment(@Nullable Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public Builder uri(@Nullable Uri uri) {
            this.uri = uri;
            return this;
        }

        public Builder requestCode(@Nullable Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder bundle(@Nullable Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder intentConsumer(@Nullable Consumer<Intent> intentConsumer) {
            this.intentConsumer = intentConsumer;
            return this;
        }

        public Builder befor(@Nullable Action action) {
            this.befor = action;
            return this;
        }

        public Builder after(@Nullable Action action) {
            this.after = action;
            return this;
        }

        public EHiRouterRequest build() {
            return new EHiRouterRequest(this);
        }

    }

}
