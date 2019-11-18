package com.xiaojinzi.component1.runalone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.xiaojinzi.base.InterceptorConfig;
import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.support.CallbackAdapter;
import com.xiaojinzi.component1.R;

public class MainActActivity extends BaseAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_debug_main_act);
    }

    public void click1(View view) {
        Router.with(mContext)
                .host(ModuleConfig.Component1.NAME)
                .path(ModuleConfig.Component1.TEST)
                .interceptorNames(InterceptorConfig.USER_LOGIN)
                .forward();
    }

}
