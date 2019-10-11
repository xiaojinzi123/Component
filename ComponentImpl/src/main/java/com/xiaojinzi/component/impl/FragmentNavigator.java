package com.xiaojinzi.component.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xiaojinzi.component.impl.fragment.FragmentManager;

public class FragmentNavigator {

    @NonNull
    private String fragmentFlag;

    public FragmentNavigator(@NonNull String fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
    }

    @Nullable
    public Fragment navigate(){
        return navigate(null);
    }

    @Nullable
    public Fragment navigate(@Nullable Bundle bundle){
        return FragmentManager.get(fragmentFlag, bundle);
    }

}
