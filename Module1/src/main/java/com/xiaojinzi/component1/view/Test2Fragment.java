package com.xiaojinzi.component1.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.anno.FragmentAnno;

public class Test2Fragment extends Fragment {

    @FragmentAnno(value = "test2Fragment", singleTon = true)
    public static Test2Fragment newInstance(@NonNull Bundle bundle) {
        Bundle args = new Bundle();
        args.putAll(bundle);
        Test2Fragment fragment = new Test2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

}

