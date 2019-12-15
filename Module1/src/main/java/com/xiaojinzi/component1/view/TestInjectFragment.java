package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.FieldAutowiredAnno;

/**
 * 测试注入 Fragment 功能的
 */
public class TestInjectFragment extends Fragment {

    @FieldAutowiredAnno("name")
    String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Component.inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
