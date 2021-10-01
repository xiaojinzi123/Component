package com.xiaojinzi.component.impl;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.SparseArray;

import com.xiaojinzi.component.impl.fragment.FragmentManager;
import com.xiaojinzi.component.support.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 一个为 {@link Fragment} 设计的导航器
 */
public class FragmentNavigator {

    @NonNull
    protected String fragmentFlag;

    @NonNull
    protected Bundle bundle = new Bundle();

    public FragmentNavigator(@NonNull String fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
    }

    /**
     * 此方法会在之后的版本删除. 请使用 {@link FragmentNavigator} 中的 putXXX 方法
     *
     * @see #putAll(Bundle)
     */
    @Deprecated
    public FragmentNavigator bundle(@NonNull Bundle bundle) {
        this.putAll(bundle);
        return this;
    }

    public FragmentNavigator putAll(@NonNull Bundle bundle) {
        Utils.checkNullPointer(bundle, "bundle");
        this.bundle.putAll(bundle);
        return this;
    }

    public FragmentNavigator putBundle(@NonNull String key, @Nullable Bundle bundle) {
        this.bundle.putBundle(key, bundle);
        return this;
    }

    public FragmentNavigator putCharSequence(@NonNull String key, @Nullable CharSequence value) {
        this.bundle.putCharSequence(key, value);
        return this;
    }

    public FragmentNavigator putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
        this.bundle.putCharSequenceArray(key, value);
        return this;
    }

    public FragmentNavigator putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
        this.bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public FragmentNavigator putByte(@NonNull String key, @Nullable byte value) {
        this.bundle.putByte(key, value);
        return this;
    }

    public FragmentNavigator putByteArray(@NonNull String key, @Nullable byte[] value) {
        this.bundle.putByteArray(key, value);
        return this;
    }

    public FragmentNavigator putChar(@NonNull String key, @Nullable char value) {
        this.bundle.putChar(key, value);
        return this;
    }

    public FragmentNavigator putCharArray(@NonNull String key, @Nullable char[] value) {
        this.bundle.putCharArray(key, value);
        return this;
    }

    public FragmentNavigator putBoolean(@NonNull String key, @Nullable boolean value) {
        this.bundle.putBoolean(key, value);
        return this;
    }

    public FragmentNavigator putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
        this.bundle.putBooleanArray(key, value);
        return this;
    }

    public FragmentNavigator putString(@NonNull String key, @Nullable String value) {
        this.bundle.putString(key, value);
        return this;
    }

    public FragmentNavigator putStringArray(@NonNull String key, @Nullable String[] value) {
        this.bundle.putStringArray(key, value);
        return this;
    }

    public FragmentNavigator putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
        this.bundle.putStringArrayList(key, value);
        return this;
    }

    public FragmentNavigator putShort(@NonNull String key, @Nullable short value) {
        this.bundle.putShort(key, value);
        return this;
    }

    public FragmentNavigator putShortArray(@NonNull String key, @Nullable short[] value) {
        this.bundle.putShortArray(key, value);
        return this;
    }

    public FragmentNavigator putInt(@NonNull String key, @Nullable int value) {
        this.bundle.putInt(key, value);
        return this;
    }

    public FragmentNavigator putIntArray(@NonNull String key, @Nullable int[] value) {
        this.bundle.putIntArray(key, value);
        return this;
    }

    public FragmentNavigator putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
        this.bundle.putIntegerArrayList(key, value);
        return this;
    }

    public FragmentNavigator putLong(@NonNull String key, @Nullable long value) {
        this.bundle.putLong(key, value);
        return this;
    }

    public FragmentNavigator putLongArray(@NonNull String key, @Nullable long[] value) {
        this.bundle.putLongArray(key, value);
        return this;
    }

    public FragmentNavigator putFloat(@NonNull String key, @Nullable float value) {
        this.bundle.putFloat(key, value);
        return this;
    }

    public FragmentNavigator putFloatArray(@NonNull String key, @Nullable float[] value) {
        this.bundle.putFloatArray(key, value);
        return this;
    }

    public FragmentNavigator putDouble(@NonNull String key, @Nullable double value) {
        this.bundle.putDouble(key, value);
        return this;
    }

    public FragmentNavigator putDoubleArray(@NonNull String key, @Nullable double[] value) {
        this.bundle.putDoubleArray(key, value);
        return this;
    }

    public FragmentNavigator putParcelable(@NonNull String key, @Nullable Parcelable value) {
        this.bundle.putParcelable(key, value);
        return this;
    }

    public FragmentNavigator putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
        this.bundle.putParcelableArray(key, value);
        return this;
    }

    public FragmentNavigator putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
        this.bundle.putParcelableArrayList(key, value);
        return this;
    }

    public FragmentNavigator putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
        this.bundle.putSparseParcelableArray(key, value);
        return this;
    }

    public FragmentNavigator putSerializable(@NonNull String key, @Nullable Serializable value) {
        this.bundle.putSerializable(key, value);
        return this;
    }

    @Nullable
    public Fragment navigate() {
        return FragmentManager.get(fragmentFlag, bundle);
    }

}
