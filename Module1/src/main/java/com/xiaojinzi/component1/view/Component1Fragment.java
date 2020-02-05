package com.xiaojinzi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno;
import com.xiaojinzi.component.anno.FragmentAnno;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RxRouter;
import com.xiaojinzi.component1.R;

import io.reactivex.functions.Consumer;

@FragmentAnno(value = "component1.fragment")
public class Component1Fragment extends Fragment {

    private Button bt_go_component2;
    private Button bt_rx_get;

    @AttrValueAutowiredAnno("age")
    int age;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Component.inject(this);

        View contentView = inflater.inflate(R.layout.component1_fragment, null);
        bt_go_component2 = contentView.findViewById(R.id.bt_go_component2);
        bt_rx_get = contentView.findViewById(R.id.bt_rx_get);
        bt_go_component2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.with(Component1Fragment.this)
                        .host(ModuleConfig.Module2.NAME)
                        .path(ModuleConfig.Module2.MAIN)
                        .forward();
            }
        });
        bt_rx_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxRouter
                        .with(Component1Fragment.this)
                        .host(ModuleConfig.Module1.NAME)
                        .path(ModuleConfig.Module1.TEST)
                        .query("data", "rxJumpGetData")
                        .requestCode(456)
                        .intentCall()
                        .subscribe(new Consumer<Intent>() {
                            @Override
                            public void accept(Intent intent) throws Exception {
                                Toast.makeText(getContext(), "成功拿到数据啦" + intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return contentView;

    }

}
