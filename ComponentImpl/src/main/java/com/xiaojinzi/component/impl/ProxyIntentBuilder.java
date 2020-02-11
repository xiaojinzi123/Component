package com.xiaojinzi.component.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.SparseArray;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.support.ProxyIntentAct;
import com.xiaojinzi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于构建一个 {@link Intent}, 详情请看 {@link Navigator#proxyBundle(Bundle)}
 */
public class ProxyIntentBuilder extends RouterRequest.URIBuilder {

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

    @NonNull
    protected Class<? extends Activity> proxyActivity = ProxyIntentAct.class;

    public ProxyIntentBuilder proxyActivity(@NonNull Class<? extends Activity> clazz) {
        Utils.checkNullPointer(clazz, "clazz");
        this.proxyActivity = clazz;
        return this;
    }

    public ProxyIntentBuilder addIntentFlags(@Nullable Integer... flags) {
        if (flags != null) {
            this.intentFlags.addAll(Arrays.asList(flags));
        }
        return this;
    }

    public ProxyIntentBuilder addIntentCategories(@Nullable String... categories) {
        if (categories != null) {
            this.intentCategories.addAll(Arrays.asList(categories));
        }
        return this;
    }

    /**
     * 用于 API >= 16 的时候,调用 {@link Activity#startActivity(Intent, Bundle)}
     */
    public ProxyIntentBuilder options(@Nullable Bundle options) {
        this.options = options;
        return this;
    }


    @Override
    public ProxyIntentBuilder url(@NonNull String url) {
        super.url(url);
        return this;
    }

    @Override
    public ProxyIntentBuilder scheme(@NonNull String scheme) {
        super.scheme(scheme);
        return this;
    }

    @Override
    public ProxyIntentBuilder hostAndPath(@NonNull String hostAndPath) {
        super.hostAndPath(hostAndPath);
        return this;
    }

    @Override
    public ProxyIntentBuilder userInfo(@NonNull String userInfo) {
        super.userInfo(userInfo);
        return this;
    }

    @Override
    public ProxyIntentBuilder host(@NonNull String host) {
        super.host(host);
        return this;
    }

    @Override
    public ProxyIntentBuilder path(@Nullable String path) {
        super.path(path);
        return this;
    }

    public ProxyIntentBuilder putBundle(@NonNull String key, @Nullable Bundle bundle) {
        this.bundle.putBundle(key, bundle);
        return this;
    }

    public ProxyIntentBuilder putAll(@NonNull Bundle bundle) {
        Utils.checkNullPointer(bundle, "bundle");
        this.bundle.putAll(bundle);
        return this;
    }

    public ProxyIntentBuilder putCharSequence(@NonNull String key, @Nullable CharSequence value) {
        this.bundle.putCharSequence(key, value);
        return this;
    }

    public ProxyIntentBuilder putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
        this.bundle.putCharSequenceArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
        this.bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public ProxyIntentBuilder putByte(@NonNull String key, @Nullable byte value) {
        this.bundle.putByte(key, value);
        return this;
    }

    public ProxyIntentBuilder putByteArray(@NonNull String key, @Nullable byte[] value) {
        this.bundle.putByteArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putChar(@NonNull String key, @Nullable char value) {
        this.bundle.putChar(key, value);
        return this;
    }

    public ProxyIntentBuilder putCharArray(@NonNull String key, @Nullable char[] value) {
        this.bundle.putCharArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putBoolean(@NonNull String key, @Nullable boolean value) {
        this.bundle.putBoolean(key, value);
        return this;
    }

    public ProxyIntentBuilder putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
        this.bundle.putBooleanArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putString(@NonNull String key, @Nullable String value) {
        this.bundle.putString(key, value);
        return this;
    }

    public ProxyIntentBuilder putStringArray(@NonNull String key, @Nullable String[] value) {
        this.bundle.putStringArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
        this.bundle.putStringArrayList(key, value);
        return this;
    }

    public ProxyIntentBuilder putShort(@NonNull String key, @Nullable short value) {
        this.bundle.putShort(key, value);
        return this;
    }

    public ProxyIntentBuilder putShortArray(@NonNull String key, @Nullable short[] value) {
        this.bundle.putShortArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putInt(@NonNull String key, @Nullable int value) {
        this.bundle.putInt(key, value);
        return this;
    }

    public ProxyIntentBuilder putIntArray(@NonNull String key, @Nullable int[] value) {
        this.bundle.putIntArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
        this.bundle.putIntegerArrayList(key, value);
        return this;
    }

    public ProxyIntentBuilder putLong(@NonNull String key, @Nullable long value) {
        this.bundle.putLong(key, value);
        return this;
    }

    public ProxyIntentBuilder putLongArray(@NonNull String key, @Nullable long[] value) {
        this.bundle.putLongArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putFloat(@NonNull String key, @Nullable float value) {
        this.bundle.putFloat(key, value);
        return this;
    }

    public ProxyIntentBuilder putFloatArray(@NonNull String key, @Nullable float[] value) {
        this.bundle.putFloatArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putDouble(@NonNull String key, @Nullable double value) {
        this.bundle.putDouble(key, value);
        return this;
    }

    public ProxyIntentBuilder putDoubleArray(@NonNull String key, @Nullable double[] value) {
        this.bundle.putDoubleArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putParcelable(@NonNull String key, @Nullable Parcelable value) {
        this.bundle.putParcelable(key, value);
        return this;
    }

    public ProxyIntentBuilder putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
        this.bundle.putParcelableArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
        this.bundle.putParcelableArrayList(key, value);
        return this;
    }

    public ProxyIntentBuilder putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
        this.bundle.putSparseParcelableArray(key, value);
        return this;
    }

    public ProxyIntentBuilder putSerializable(@NonNull String key, @Nullable Serializable value) {
        this.bundle.putSerializable(key, value);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, @Nullable String queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, boolean queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, byte queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, int queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, float queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, long queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    @Override
    public ProxyIntentBuilder query(@NonNull String queryName, double queryValue) {
        super.query(queryName, queryValue);
        return this;
    }

    /**
     * 构建一个代理的 {@link Intent}.
     * 跳转的目标:
     * 1. 当你没有指定的时候, 默认是 {@link ProxyIntentAct}, 此界面会自动处理, 并帮助您跳转到目标界面. 全程无感知
     * 2. 当你指定了一个 {@link Activity} 的时候, 则此 {@link Intent} 跳转目标为你自定义的 {@link Activity}
     * 你可以使用 {@link Router#with(Context)} 方法获取一个 {@link Navigator}, 然后使用 {@link Navigator#proxyBundle(Bundle)}
     */
    @NonNull
    public Intent buildProxyIntent() {
        Intent intent = new Intent(Component.getApplication(), proxyActivity);
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT, true);
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_URL, buildURL());
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_BUNDLE, this.bundle);
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_OPTIONS, this.options);
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_FLAGS, new ArrayList<>(this.intentFlags));
        intent.putExtra(ProxyIntentAct.EXTRA_ROUTER_PROXY_INTENT_CATEGORIES, new ArrayList<>(this.intentCategories));
        return intent;
    }

}