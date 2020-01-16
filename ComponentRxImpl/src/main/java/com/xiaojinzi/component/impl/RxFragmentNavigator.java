package com.xiaojinzi.component.impl;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.impl.fragment.RxFragmentManager;

import io.reactivex.Single;


public class RxFragmentNavigator extends FragmentNavigator {

    public RxFragmentNavigator(@NonNull String fragmentFlag) {
        super(fragmentFlag);
    }

    @NonNull
    public Single<Fragment> call() {
        return RxFragmentManager.with(fragmentFlag, bundle);
    }

}