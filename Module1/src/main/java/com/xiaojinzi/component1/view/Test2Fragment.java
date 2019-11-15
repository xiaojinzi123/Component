package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.xiaojinzi.component.anno.FragmentAnno;

public class Test2Fragment extends Fragment {

    @FragmentAnno(value = "test2Fragment")
    public static Test2Fragment newInstance(@NonNull Bundle bundle) {
        Bundle args = new Bundle();
        args.putAll(bundle);
        Test2Fragment fragment = new Test2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

}

