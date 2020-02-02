package com.xiaojinzi.component.impl;

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

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.ComponentActivityStack;
import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.bean.ActivityResult;
import com.xiaojinzi.component.support.Action;
import com.xiaojinzi.component.support.Consumer;
import com.xiaojinzi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示路由的一个请求类,构建时候如果参数不对是有异常会发生的,使用的时候注意这一点
 * 但是在拦截器 {@link RouterInterceptor} 中构建是不用关心错误的,
 * 因为拦截器的 {@link RouterInterceptor#intercept(RouterInterceptor.Chain)} 方法
 * 允许抛出异常
 * <p>
 * time   : 2018/11/29
 *
 * @author xiaojinzi
 */
@CheckClassName
public class RouterRequest {

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

    /**
     * requestCode
     */
    @Nullable
    public final Integer requestCode;

    /**
     * 框架是否帮助用户跳转拿 {@link ActivityResult}
     * 有 requestCode 只能说明用户使用了某一个 requestCode,
     * 会调用 {@link Activity#startActivityForResult(Intent, int)}.
     * 但是不代表需要框架去帮你获取到 {@link ActivityResult}.
     * 所以这个值就是标记是否需要框架帮助您去获取 {@link ActivityResult}
     */
    public final boolean isForResult;

    /**
     * 跳转的时候 options 参数
     */
    @Nullable
    public final Bundle options;

    /**
     * Intent 的 flag
     */
    @NonNull
    public final List<Integer> intentFlags;

    /**
     * Intent 的 类别
     */
    @NonNull
    public final List<String> intentCategories;

    @NonNull
    public final Bundle bundle = new Bundle();

    @Nullable
    public final Consumer<Intent> intentConsumer;

    @Nullable
    public final Action beforJumpAction;

    @Nullable
    public final Action afterJumpAction;

    @Nullable
    public final Action afterErrorAction;

    @Nullable
    public final Action afterEventAction;

    /**
     * 从 {@link Fragment} 和 {@link Context} 中获取上下文
     * <p>
     * 参数中的 {@link RouterRequest#context} 可能是一个 {@link android.app.Application} 或者是一个
     * {@link android.content.ContextWrapper} 或者是一个 {@link Activity}
     * 无论参数的类型是哪种, 此方法的返回值就只有两种类型：
     * 1. {@link android.app.Application}
     * 2. {@link Activity}
     * <p>
     * 如果返回的是 {@link Activity} 的 {@link Context},
     * 当 {@link Activity} 销毁了就会返回 null
     * 另外就是返回 {@link android.app.Application}
     *
     * @return {@link Context}, 可能为 null, null 就只有一种情况就是界面销毁了.
     * 构建 {@link RouterRequest} 的时候已经保证了
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
        // 如果不是 Activity 可能是 Application,所以直接返回
        if (rawAct == null) {
            return rawContext;
        } else {
            // 如果是 Activity 并且已经销毁了返回 null
            if (isActivityDestoryed(rawAct)) {
                return null;
            } else {
                return rawContext;
            }
        }
    }

    /**
     * 从 {@link Context} 中获取 {@link Activity}, {@link Context} 可能是 {@link android.content.ContextWrapper}
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
     * 从参数 {@link Fragment} 和 {@link Context} 获取 Activity,
     *
     * @return 如果 activity 已经销毁并且 fragment 销毁了就会返回 null
     */
    @Nullable
    public final Activity getRawActivity() {
        Activity rawActivity = getActivity();
        if (rawActivity == null) {
            if (fragment != null) {
                rawActivity = fragment.getActivity();
            }
        }
        if (rawActivity == null) {
            return null;
        }
        if (isActivityDestoryed(rawActivity)) {
            return null;
        }
        return rawActivity;
    }

    /**
     * 首先调用 {@link #getRawActivity()} 尝试获取此次用户传入的 Context 中是否有关联的 Activity
     * 如果为空, 则尝试获取运行中的所有 Activity 中顶层的那个
     *
     * @return
     */
    @Nullable
    public final Activity getRawOrTopActivity() {
        Activity result = getRawActivity();
        if (result == null) {
            // 如果不是为空返回的, 那么必定不是销毁的
            result = ComponentActivityStack.getInstance().getTopActivity();
        }
        return result;
    }

    /**
     * Activity 是否被销毁了
     *
     * @param activity
     * @return
     */
    private boolean isActivityDestoryed(@NonNull Activity activity) {
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
        // 有关界面的两个
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
        builder.isForResult = isForResult;
        builder.options = options;
        // 这里需要新创建一个是因为不可修改的集合不可以给别人
        builder.intentCategories = new ArrayList<>(intentCategories);
        builder.intentFlags = new ArrayList<>(intentFlags);

        builder.intentConsumer = intentConsumer;
        builder.beforJumpAction = beforJumpAction;
        builder.afterJumpAction = afterJumpAction;
        builder.afterErrorAction = afterErrorAction;
        builder.afterEventAction = afterEventAction;
        return builder;
    }

    private RouterRequest(@NonNull Builder builder) {
        Uri result = builder.buildURI();
        this.uri = result;
        context = builder.context;
        fragment = builder.fragment;
        requestCode = builder.requestCode;
        isForResult = builder.isForResult;
        options = builder.options;
        // 这两个集合是不可以更改的
        intentCategories = Collections.unmodifiableList(builder.intentCategories);
        intentFlags = Collections.unmodifiableList(builder.intentFlags);
        if (builder.bundle != null) {
            this.bundle.putAll(builder.bundle);
        }
        intentConsumer = builder.intentConsumer;
        beforJumpAction = builder.beforJumpAction;
        afterJumpAction = builder.afterJumpAction;
        afterErrorAction = builder.afterErrorAction;
        afterEventAction = builder.afterEventAction;
    }

    /**
     * 构建一个路由请求对象 {@link RouterRequest} 对象的 Builder
     *
     * @author xiaojinzi
     */
    public static class Builder extends URIBuilder {

        @Nullable
        protected Bundle options;

        /**
         * Intent 的 flag,允许修改的
         */
        @NonNull
        protected List<Integer> intentFlags = new ArrayList<>(2);

        /**
         * Intent 的 类别,允许修改的
         */
        @NonNull
        protected List<String> intentCategories = new ArrayList<>(2);

        @NonNull
        protected Bundle bundle = new Bundle();

        @Nullable
        protected Context context;

        @Nullable
        protected Fragment fragment;

        @Nullable
        protected Integer requestCode;

        protected boolean isForResult;

        @Nullable
        protected Consumer<Intent> intentConsumer;

        /**
         * 跳转前的 Callback
         */
        @Nullable
        protected Action beforJumpAction;

        /**
         * 跳转成功之后的 Callback
         * 此时的跳转成功仅代表目标界面启动成功, 不代表跳转拿数据的回调被回调了
         * 假如你是跳转拿数据的, 当你跳转到 A 界面, 此回调就会回调了,
         * 当你拿到 Intent 的回调了, 和此回调已经没关系了
         */
        @Nullable
        protected Action afterJumpAction;

        /**
         * 跳转失败之后的 Callback
         */
        @Nullable
        protected Action afterErrorAction;

        /**
         * 跳转成功和失败之后的 Callback
         */
        @Nullable
        protected Action afterEventAction;

        public Builder context(@Nullable Context context) {
            this.context = context;
            return this;
        }

        public Builder fragment(@Nullable Fragment fragment) {
            this.fragment = fragment;
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

        public Builder afterErrorAction(@Nullable Action action) {
            this.afterErrorAction = action;
            return this;
        }

        public Builder afterEventAction(@Nullable Action action) {
            this.afterEventAction = action;
            return this;
        }

        public Builder requestCode(@Nullable Integer requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        /**
         * 当不是自定义跳转的时候, Intent 由框架生成,所以可以回调这个接口
         * 当自定义跳转,这个回调不会回调的,这是需要注意的点
         * <p>
         * 其实目标界面可以完全的自定义路由,这个功能实际上没有存在的必要,因为你可以为同一个界面添加上多个 {@link com.xiaojinzi.component.anno.RouterAnno}
         * 然后每一个 {@link com.xiaojinzi.component.anno.RouterAnno} 都可以有不同的行为.是可以完全的代替 {@link RouterRequest#intentConsumer} 方法的
         *
         * @param intentConsumer Intent 是框架自动构建完成的,里面有跳转需要的所有参数和数据,这里就是给用户一个
         *                       更改的机会,最好别更改内部的参数等的信息,这里提供出来其实主要是可以让你调用Intent
         *                       的 {@link Intent#addFlags(int)} 等方法,并不是给你修改内部的 bundle 的
         * @return
         */
        public Builder intentConsumer(@Nullable Consumer<Intent> intentConsumer) {
            this.intentConsumer = intentConsumer;
            return this;
        }

        public Builder addIntentFlags(@Nullable Integer... flags) {
            if (flags != null) {
                this.intentFlags.addAll(Arrays.asList(flags));
            }
            return this;
        }

        public Builder addIntentCategories(@Nullable String... categories) {
            if (categories != null) {
                this.intentCategories.addAll(Arrays.asList(categories));
            }
            return this;
        }

        /**
         * 用于 API >= 16 的时候,调用 {@link Activity#startActivity(Intent, Bundle)}
         */
        public Builder options(@Nullable Bundle options) {
            this.options = options;
            return this;
        }

        @Override
        public Builder url(@NonNull String url) {
            super.url(url);
            return this;
        }

        @Override
        public Builder scheme(@NonNull String scheme) {
            super.scheme(scheme);
            return this;
        }

        @Override
        public Builder hostAndPath(@NonNull String hostAndPath) {
            super.hostAndPath(hostAndPath);
            return this;
        }

        @Override
        public Builder userInfo(@NonNull String userInfo) {
            super.userInfo(userInfo);
            return this;
        }

        @Override
        public Builder host(@NonNull String host) {
            super.host(host);
            return this;
        }

        @Override
        public Builder path(@Nullable String path) {
            super.path(path);
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

        @Override
        public Builder query(@NonNull String queryName, @Nullable String queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        @Override
        public Builder query(@NonNull String queryName, boolean queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        @Override
        public Builder query(@NonNull String queryName, byte queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        @Override
        public Builder query(@NonNull String queryName, int queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        @Override
        public Builder query(@NonNull String queryName, float queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        @Override
        public Builder query(@NonNull String queryName, long queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        @Override
        public Builder query(@NonNull String queryName, double queryValue) {
            super.query(queryName, queryValue);
            return this;
        }

        /**
         * 构建请求对象,这个构建是必须的,不能错误的,如果出错了,直接崩溃掉,因为连最基本的信息都不全没法进行下一步的操作
         *
         * @return 可能会抛出一个运行时异常, 由于您的参数在构建 uri 的时候出现的异常
         */
        public RouterRequest build() {
            return new RouterRequest(this);
        }

    }

    /**
     * 构造 URI 和 URL 的Builder
     *
     * @author xiaojinzi
     */
    public static class URIBuilder {

        @Nullable
        protected String url;

        @Nullable
        protected String scheme;

        @Nullable
        protected String userInfo;

        @NonNull
        protected String host;

        @Nullable
        protected String path;

        @NonNull
        protected Map<String, String> queryMap = new HashMap<>();

        public URIBuilder url(@NonNull String url) {
            this.url = url;
            return this;
        }

        public URIBuilder scheme(@NonNull String scheme) {
            Utils.checkStringNullPointer(scheme, "scheme");
            this.scheme = scheme;
            return this;
        }

        /**
         * xxx/xxx
         *
         * @param hostAndPath
         * @return
         */
        public URIBuilder hostAndPath(@NonNull String hostAndPath) {
            Utils.checkNullPointer(hostAndPath, "hostAndPath");
            int index = hostAndPath.indexOf("/");
            if (index > 0) {
                host(hostAndPath.substring(0, index));
                path(hostAndPath.substring(index + 1));
            } else {
                Utils.debugThrowException(new IllegalArgumentException(hostAndPath + " is invalid"));
            }
            return this;
        }

        public URIBuilder userInfo(@NonNull String userInfo) {
            Utils.checkStringNullPointer(userInfo, "userInfo");
            this.userInfo = userInfo;
            return this;
        }

        public URIBuilder host(@NonNull String host) {
            Utils.checkStringNullPointer(host, "host");
            this.host = host;
            return this;
        }

        public URIBuilder path(@Nullable String path) {
            this.path = path;
            return this;
        }

        public URIBuilder query(@NonNull String queryName, @Nullable String queryValue) {
            Utils.checkStringNullPointer(queryName, "queryName");
            queryMap.put(queryName, queryValue);
            return this;
        }

        public URIBuilder query(@NonNull String queryName, boolean queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public URIBuilder query(@NonNull String queryName, byte queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public URIBuilder query(@NonNull String queryName, int queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public URIBuilder query(@NonNull String queryName, float queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public URIBuilder query(@NonNull String queryName, long queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        public URIBuilder query(@NonNull String queryName, double queryValue) {
            return query(queryName, String.valueOf(queryValue));
        }

        /**
         * 构建一个 {@link Uri},如果构建失败会抛出异常
         */
        @NonNull
        public Uri buildURI() {
            URIBuilder builder = this;
            Uri result = null;
            if (builder.url == null) {
                Uri.Builder uriBuilder = new Uri.Builder();
                StringBuffer authoritySB = new StringBuffer();
                if (userInfo != null || !"".equals(userInfo)) {
                    authoritySB
                            .append(Uri.encode(userInfo))
                            .append("@");
                }
                authoritySB.append(
                        Uri.encode(
                                Utils.checkStringNullPointer(
                                        builder.host, "host",
                                        "do you forget call host() to set host?"
                                )
                        )
                );
                uriBuilder
                        .scheme(TextUtils.isEmpty(builder.scheme) ?
                                Component.getConfig().getDefaultScheme() : builder.scheme)
                        // host 一定不能为空
                        .encodedAuthority(authoritySB.toString())
                        .path(
                                Utils.checkStringNullPointer(
                                        builder.path, "path",
                                        "do you forget call path() to set path?"
                                )
                        );
                for (Map.Entry<String, String> entry : builder.queryMap.entrySet()) {
                    uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
                result = uriBuilder.build();
            } else {
                result = Uri.parse(builder.url);
                if (builder.queryMap.size() > 0) {
                    Uri.Builder uriBuilder = result.buildUpon();
                    for (Map.Entry<String, String> entry : builder.queryMap.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
                    }
                    result = uriBuilder.build();
                }
            }
            return result;
        }

        /**
         * 构建一个URL,如果构建失败会抛出异常
         */
        @NonNull
        public String buildURL() {
            return buildURI().toString();
        }

    }

}
