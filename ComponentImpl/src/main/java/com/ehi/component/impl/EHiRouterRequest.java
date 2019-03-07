package com.ehi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.ehi.component.ComponentConfig;
import com.ehi.component.support.Action;
import com.ehi.component.support.Consumer;
import com.ehi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        } else if (fragment != null) {
            rawContext = fragment.getContext();
        }
        Activity rawAct = Utils.getActivityFromContext(rawContext);
        if (rawAct != null) {
            if (isActivityDestoryed(rawAct)) {
                return null;
            }
        }
        return rawContext;
    }

    /**
     * 获取 Activity, {@link Context} 可能是 {@link android.content.ContextWrapper}
     *
     * @return 如果 Activity 销毁了就会返回 null
     */
    @Nullable
    public final Activity getActivity() {
        if (context == null) {
            return null;
        }
        Activity realActivity = Utils.getActivityFromContext(context);
        if (realActivity == null) {
            return null;
        }
        if (isActivityDestoryed(realActivity)) {
            return null;
        }
        return realActivity;
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
            return activity.isFinishing() || activity.isDestroyed();
        } else {
            return activity.isFinishing();
        }
    }

    /**
     * 这里转化的对象会比 {@link Builder} 对象中的参数少一个 {@link Builder#url}
     * 因为 uri 转化为了 scheme,host,path,queryMap 那么这时候就不需要 url 了
     *
     * @return
     */
    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.fragment = fragment;
        builder.context = context;

        // 还原一个 Uri 为各个零散的参数
        builder.scheme = uri.getScheme();
        builder.host = uri.getHost();
        builder.path = uri.getPath();
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        if (queryParameterNames != null) {
            for (String queryParameterName : queryParameterNames) {
                builder.queryMap.put(queryParameterName, uri.getQueryParameter(queryParameterName));
            }
        }

        builder.bundle = bundle;
        builder.requestCode = requestCode;

        builder.intentConsumer = intentConsumer;
        builder.beforJumpAction = beforJumpAction;
        builder.afterJumpAction = afterJumpAction;
        return builder;
    }

    private EHiRouterRequest(@NonNull Builder builder) {
        Uri result = null;
        if (builder.url == null) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder
                    .scheme(TextUtils.isEmpty(builder.scheme) ? ComponentConfig.getDefaultScheme() : builder.scheme)
                    .authority(Utils.checkStringNullPointer(builder.host, "host", "do you forget call host() to set host?"));
            if (!TextUtils.isEmpty(builder.path)) {
                uriBuilder.path(builder.path);
            }
            for (Map.Entry<String, String> entry : builder.queryMap.entrySet()) {
                uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
            result = uriBuilder.build();
        } else {
            result = Uri.parse(builder.url);
        }
        if (result == null) {
            throw new NullPointerException("the parameter 'uri' is null");
        }
        this.uri = result;
        context = builder.context;
        fragment = builder.fragment;
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
        protected Context context;

        @Nullable
        protected Fragment fragment;

        @Nullable
        protected String url;

        @Nullable
        protected String scheme;

        @NonNull
        protected String host;

        @Nullable
        protected String path;

        @Nullable
        protected Integer requestCode;

        @NonNull
        protected Map<String, String> queryMap = new HashMap<>();

        @NonNull
        protected Bundle bundle = new Bundle();

        @Nullable
        protected Consumer<Intent> intentConsumer;

        @Nullable
        protected Action beforJumpAction;

        @Nullable
        protected Action afterJumpAction;

        public Builder context(@Nullable Context context) {
            this.context = context;
            return this;
        }

        public Builder fragment(@Nullable Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        /**
         * 当不是自定义跳转的时候, Intent 由框架生成,所以可以回调这个接口
         * 当自定义跳转,这个回调不会回调的
         *
         * @param intentConsumer 这个参数是框架自动构建的,里面有跳转需要的所有参数和数据,这里就是给用户一个
         *                       更改的机会,但是最好别改参数之类的信息,这里提供出来其实是可以让你调用Intent
         *                       的 {@link Intent#addFlags(int)} 等方法,并不是给你修改内部的 bundle 的
         * @return
         */
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

        public Builder requestCode(@Nullable Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder url(@NonNull String url) {
            this.url = url;
            return this;
        }

        public Builder scheme(@NonNull String scheme) {
            Utils.checkStringNullPointer(scheme, "scheme");
            this.scheme = scheme;
            return this;
        }

        public Builder host(@NonNull String host) {
            Utils.checkStringNullPointer(host, "host");
            this.host = host;
            return this;
        }

        public Builder path(@Nullable String path) {
            this.path = path;
            return this;
        }

        public Builder putBundle(@NonNull String key, @Nullable Bundle bundle) {
            this.bundle.putBundle(key, bundle);
            return this;
        }

        public Builder putAll(@NonNull Bundle bundle) {
            Utils.checkNullPointer(bundle, "bundle");
            this.bundle.putAll(bundle);
            return this;
        }

        public Builder putCharSequence(@NonNull String key, @Nullable CharSequence value) {
            this.bundle.putCharSequence(key, value);
            return this;
        }

        public Builder putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
            this.bundle.putCharSequenceArray(key, value);
            return this;
        }

        public Builder putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
            this.bundle.putCharSequenceArrayList(key, value);
            return this;
        }

        public Builder putByte(@NonNull String key, @Nullable byte value) {
            this.bundle.putByte(key, value);
            return this;
        }

        public Builder putByteArray(@NonNull String key, @Nullable byte[] value) {
            this.bundle.putByteArray(key, value);
            return this;
        }

        public Builder putChar(@NonNull String key, @Nullable char value) {
            this.bundle.putChar(key, value);
            return this;
        }

        public Builder putCharArray(@NonNull String key, @Nullable char[] value) {
            this.bundle.putCharArray(key, value);
            return this;
        }

        public Builder putBoolean(@NonNull String key, @Nullable boolean value) {
            this.bundle.putBoolean(key, value);
            return this;
        }

        public Builder putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
            this.bundle.putBooleanArray(key, value);
            return this;
        }

        public Builder putString(@NonNull String key, @Nullable String value) {
            this.bundle.putString(key, value);
            return this;
        }

        public Builder putStringArray(@NonNull String key, @Nullable String[] value) {
            this.bundle.putStringArray(key, value);
            return this;
        }

        public Builder putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
            this.bundle.putStringArrayList(key, value);
            return this;
        }

        public Builder putShort(@NonNull String key, @Nullable short value) {
            this.bundle.putShort(key, value);
            return this;
        }

        public Builder putShortArray(@NonNull String key, @Nullable short[] value) {
            this.bundle.putShortArray(key, value);
            return this;
        }

        public Builder putInt(@NonNull String key, @Nullable int value) {
            this.bundle.putInt(key, value);
            return this;
        }

        public Builder putIntArray(@NonNull String key, @Nullable int[] value) {
            this.bundle.putIntArray(key, value);
            return this;
        }

        public Builder putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
            this.bundle.putIntegerArrayList(key, value);
            return this;
        }

        public Builder putLong(@NonNull String key, @Nullable long value) {
            this.bundle.putLong(key, value);
            return this;
        }

        public Builder putLongArray(@NonNull String key, @Nullable long[] value) {
            this.bundle.putLongArray(key, value);
            return this;
        }

        public Builder putFloat(@NonNull String key, @Nullable float value) {
            this.bundle.putFloat(key, value);
            return this;
        }

        public Builder putFloatArray(@NonNull String key, @Nullable float[] value) {
            this.bundle.putFloatArray(key, value);
            return this;
        }

        public Builder putDouble(@NonNull String key, @Nullable double value) {
            this.bundle.putDouble(key, value);
            return this;
        }

        public Builder putDoubleArray(@NonNull String key, @Nullable double[] value) {
            this.bundle.putDoubleArray(key, value);
            return this;
        }

        public Builder putParcelable(@NonNull String key, @Nullable Parcelable value) {
            this.bundle.putParcelable(key, value);
            return this;
        }

        public Builder putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
            this.bundle.putParcelableArray(key, value);
            return this;
        }

        public Builder putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
            this.bundle.putParcelableArrayList(key, value);
            return this;
        }

        public Builder putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
            this.bundle.putSparseParcelableArray(key, value);
            return this;
        }

        public Builder putSerializable(@NonNull String key, @Nullable Serializable value) {
            this.bundle.putSerializable(key, value);
            return this;
        }

        public Builder query(@NonNull String queryName, @Nullable String queryValue) {
            Utils.checkStringNullPointer(queryName, "queryName");
            if (queryValue == null) {
                queryValue = "";
            }
            queryMap.put(queryName, queryValue);
            return this;
        }

        public Builder query(@NonNull String queryName, boolean queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, byte queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, int queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, float queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, long queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public Builder query(@NonNull String queryName, double queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        /**
         * 构建请求对象,这个构建是必须的,不能错误的,如果出错了,直接崩溃掉,因为连最基本的信息都不全没法进行下一步的操作
         *
         * @return 可能会抛出一个运行时异常, 由于您的参数在构建 uri 的时候出现的异常
         */
        public EHiRouterRequest build() {
            return new EHiRouterRequest(this);
        }

    }

}
