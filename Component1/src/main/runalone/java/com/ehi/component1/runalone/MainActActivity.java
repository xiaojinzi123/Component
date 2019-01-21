package com.ehi.component1.runalone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ehi.base.InterceptorConfig;
import com.ehi.base.ModuleConfig;
import com.ehi.base.view.BaseAct;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.support.EHiCallbackAdapter;
import com.ehi.component1.R;

public class MainActActivity extends BaseAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_debug_main_act);
    }

    public void click1(View view) {
        EHiRouter.with(mContext)
                .host(ModuleConfig.Component1.NAME)
                .path(ModuleConfig.Component1.TEST)
                .interceptorNames(InterceptorConfig.USER_LOGIN)
                .navigate(new EHiCallbackAdapter(){
                    @Override
                    public void onError(@NonNull Exception error) {
                        super.onError(error);
                    }
                });
    }

}
