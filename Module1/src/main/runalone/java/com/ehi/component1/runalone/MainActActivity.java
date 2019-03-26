package com.ehi.component1.runalone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.ModuleConfig;
import com.ehi.base.view.BaseAct;
import com.ehi.component.impl.Router;
import com.ehi.component.support.CallbackAdapter;
import com.ehi.component1.R;

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
                .navigate(new CallbackAdapter(){
                    @Override
                    public void onError(@NonNull Exception error) {
                        super.onError(error);
                    }
                });
    }

}
