package com.xiaojinzi.component.impl;


import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.SparseArray;

import com.xiaojinzi.component.impl.fragment.RxFragmentManager;

import java.io.Serializable;
import java.util.ArrayList;

import io.reactivex.Single;


public class RxFragmentNavigator extends FragmentNavigator {

    public RxFragmentNavigator(@NonNull String fragmentFlag) {
        super(fragmentFlag);
    }

    /**
     * 此方法会在之后的版本删除. 请使用 {@link RxFragmentNavigator} 中的 putXXX 方法
     *
     * @param bundle
     * @see #putAll(Bundle)
     */
    @Deprecated
    public RxFragmentNavigator bundle(@NonNull Bundle bundle) {
        super.bundle(bundle);
        return this;
    }

    public RxFragmentNavigator putAll(@NonNull Bundle bundle) {
        super.putAll(bundle);
        return this;
    }

    public RxFragmentNavigator putBundle(@NonNull String key, @Nullable Bundle bundle) {
        super.putBundle(key, bundle);
        return this;
    }

    public RxFragmentNavigator putCharSequence(@NonNull String key, @Nullable CharSequence value) {
        super.putCharSequence(key, value);
        return this;
    }

    public RxFragmentNavigator putCharSequenceArray(@NonNull String key, @Nullable CharSequence[] value) {
        super.putCharSequenceArray(key, value);
        return this;
    }

    public RxFragmentNavigator putCharSequenceArrayList(@NonNull String key, @Nullable ArrayList<CharSequence> value) {
        super.putCharSequenceArrayList(key, value);
        return this;
    }

    public RxFragmentNavigator putByte(@NonNull String key, @Nullable byte value) {
        super.putByte(key, value);
        return this;
    }

    public RxFragmentNavigator putByteArray(@NonNull String key, @Nullable byte[] value) {
        super.putByteArray(key, value);
        return this;
    }

    public RxFragmentNavigator putChar(@NonNull String key, @Nullable char value) {
        super.putChar(key, value);
        return this;
    }

    public RxFragmentNavigator putCharArray(@NonNull String key, @Nullable char[] value) {
        super.putCharArray(key, value);
        return this;
    }

    public RxFragmentNavigator putBoolean(@NonNull String key, @Nullable boolean value) {
        super.putBoolean(key, value);
        return this;
    }

    public RxFragmentNavigator putBooleanArray(@NonNull String key, @Nullable boolean[] value) {
        super.putBooleanArray(key, value);
        return this;
    }

    public RxFragmentNavigator putString(@NonNull String key, @Nullable String value) {
        super.putString(key, value);
        return this;
    }

    public RxFragmentNavigator putStringArray(@NonNull String key, @Nullable String[] value) {
        super.putStringArray(key, value);
        return this;
    }

    public RxFragmentNavigator putStringArrayList(@NonNull String key, @Nullable ArrayList<String> value) {
        super.putStringArrayList(key, value);
        return this;
    }

    public RxFragmentNavigator putShort(@NonNull String key, @Nullable short value) {
        super.putShort(key, value);
        return this;
    }

    public RxFragmentNavigator putShortArray(@NonNull String key, @Nullable short[] value) {
        super.putShortArray(key, value);
        return this;
    }

    public RxFragmentNavigator putInt(@NonNull String key, @Nullable int value) {
        super.putInt(key, value);
        return this;
    }

    public RxFragmentNavigator putIntArray(@NonNull String key, @Nullable int[] value) {
        super.putIntArray(key, value);
        return this;
    }

    public RxFragmentNavigator putIntegerArrayList(@NonNull String key, @Nullable ArrayList<Integer> value) {
        super.putIntegerArrayList(key, value);
        return this;
    }

    public RxFragmentNavigator putLong(@NonNull String key, @Nullable long value) {
        super.putLong(key, value);
        return this;
    }

    public RxFragmentNavigator putLongArray(@NonNull String key, @Nullable long[] value) {
        super.putLongArray(key, value);
        return this;
    }

    public RxFragmentNavigator putFloat(@NonNull String key, @Nullable float value) {
        super.putFloat(key, value);
        return this;
    }

    public RxFragmentNavigator putFloatArray(@NonNull String key, @Nullable float[] value) {
        super.putFloatArray(key, value);
        return this;
    }

    public RxFragmentNavigator putDouble(@NonNull String key, @Nullable double value) {
        super.putDouble(key, value);
        return this;
    }

    public RxFragmentNavigator putDoubleArray(@NonNull String key, @Nullable double[] value) {
        super.putDoubleArray(key, value);
        return this;
    }

    public RxFragmentNavigator putParcelable(@NonNull String key, @Nullable Parcelable value) {
        super.putParcelable(key, value);
        return this;
    }

    public RxFragmentNavigator putParcelableArray(@NonNull String key, @Nullable Parcelable[] value) {
        super.putParcelableArray(key, value);
        return this;
    }

    public RxFragmentNavigator putParcelableArrayList(@NonNull String key, @Nullable ArrayList<? extends Parcelable> value) {
        super.putParcelableArrayList(key, value);
        return this;
    }

    public RxFragmentNavigator putSparseParcelableArray(@NonNull String key, @Nullable SparseArray<? extends Parcelable> value) {
        super.putSparseParcelableArray(key, value);
        return this;
    }

    public RxFragmentNavigator putSerializable(@NonNull String key, @Nullable Serializable value) {
        super.putSerializable(key, value);
        return this;
    }

    @NonNull
    public Single<Fragment> call() {
        return RxFragmentManager.with(fragmentFlag, bundle);
    }

}