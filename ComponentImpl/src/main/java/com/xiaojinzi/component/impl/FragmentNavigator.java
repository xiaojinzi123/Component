package com.xiaojinzi.component.impl;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.impl.fragment.FragmentManager;

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
    public Fragment navigate(){
        return FragmentManager.get(fragmentFlag, bundle);
    }

}
