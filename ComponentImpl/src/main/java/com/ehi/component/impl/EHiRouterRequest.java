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

    /**
     * 这是一个很重要的参数,一定不可以为空,如果这个为空了,一定不能继续下去,因为很多地方直接使用这个参数的,不做空判断的
     * 而且这个参数不可以为空的
     */
    @NonNull
    public final Uri uri;

    @Nullable
    public final Integer requestCode;

    @NonNull
    public final Bundle bundle = new Bundle();

    @Nullable
    public final Consumer<Intent> intentConsumer;

    @Nullable
    public final Action beforJumpAction;

    @Nullable
    public final Action afterJumpAction;

    /**
     * 获取上下文
     *
     * @return 当 Activity 已经销毁了就返回 null
     */
    @Nullable
    public final Context getRawContext() {
        Context rawContext = null;
        if (context != null) {
            rawContext = context;
        } else if(fragment != null) {
            rawContext = fragment.getContext();
        }
        if (rawContext != null && rawContext instanceof Activity) {
            Activity activity = (Activity) rawContext;
            if (isActivityDestoryed(activity)) {
                return null;
            }
        }
        return rawContext;
    }

    /**
     * 获取 Activity
     *
     * @return 如果 Activity 销毁了就会返回 null
     */
    @Nullable
    public final Activity getActivity() {
        if (context == null) {
            return null;
        }
        if (!(context instanceof Activity)) {
            return null;
        }
        Activity rawActivity = (Activity) context;
        if (isActivityDestoryed(rawActivity)) {
            return null;
        }
        return rawActivity;
    }

    /**
     * 从参数 context 和 fragment 获取 Activity,
     *
     * @return 如果 activity 已经销毁并且 fragment 销毁了就会返回 null
     */
    @Nullable
    public final Activity getRawActivity() {

        Activity rawActivity = getActivity();
        if (rawActivity == null && fragment != null) {
            rawActivity = fragment.getActivity();
        }
        if (isActivityDestoryed(rawActivity)) {
            return null;
        }
        return rawActivity;
    }

    /**
     * Activity 是否被销毁了
     *
     * @param activity
     * @return
     */
    private boolean isActivityDestoryed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isFinishing() || activity.isDestroyed()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (activity.isFinishing()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.bundle = bundle;
        builder.requestCode = requestCode;
        builder.uri = uri;
        builder.fragment = fragment;
        builder.context = context;
        builder.intentConsumer = intentConsumer;
        builder.beforJumpAction = beforJumpAction;
        builder.afterJumpAction = afterJumpAction;
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
        beforJumpAction = builder.beforJumpAction;
        afterJumpAction = builder.afterJumpAction;
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
        private Action beforJumpAction;

        @Nullable
        private Action afterJumpAction;

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

        public Builder beforJumpAction(@Nullable Action action) {
            this.beforJumpAction = action;
            return this;
        }

        public Builder afterJumpAction(@Nullable Action action) {
            this.afterJumpAction = action;
            return this;
        }

        public EHiRouterRequest build() {
            return new EHiRouterRequest(this);
        }

    }

}
