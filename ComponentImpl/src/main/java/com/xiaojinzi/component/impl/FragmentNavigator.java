package com.xiaojinzi.component.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xiaojinzi.component.impl.fragment.FragmentManager;

/**
 * 一个为 {@link Fragment} 设计的导航器
 */
public class FragmentNavigator {

    @NonNull
    protected String fragmentFlag;

    @NonNull
    protected Bundle bundle;

    public FragmentNavigator(@NonNull String fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
    }

    public FragmentNavigator bundle(@NonNull Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    @Nullable
    public Fragment navigate() {
        return FragmentManager.get(fragmentFlag, bundle);
    }

}
