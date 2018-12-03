package com.ehi.component1.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ehi.base.ModuleConfig;
import com.ehi.base.interceptor.LoginInterceptor;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component1.R;

@EHiRouterAnno(
        host = ModuleConfig.Component1.NAME,
        value = ModuleConfig.Component1.TESTLOGIN,
        interceptors = LoginInterceptor.class
)
public class Component1TestLoginAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_login_act);
    }

}
