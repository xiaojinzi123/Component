package com.xiaojinzi.componentdemo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.componentdemo.R;

/**
 * time   : 2018/12/27
 *
 * @author : xiaojinzi
 */
@RouterAnno(
        path = ModuleConfig.App.TEST_FRAGMENT_ROUTER,
        desc = "测试跳转的界面"
)
public class TestFragmentRouterAct extends BaseAct {

    private FrameLayout fl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_fragment_router_act);
        fl = findViewById(R.id.fl);

        TestFragmentRouterFragment fragment = new TestFragmentRouterFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl, fragment);
        ft.commit();

    }

}
