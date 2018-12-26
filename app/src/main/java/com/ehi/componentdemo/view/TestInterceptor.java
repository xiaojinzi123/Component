package com.ehi.componentdemo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.componentdemo.R;

@EHiRouterAnno(
        host = ModuleConfig.App.NAME, value = ModuleConfig.App.TEST_INTERCEPTOR
)
public class TestInterceptor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_interceptor_act);
    }

    public void clearAllInterceptor(View view) {
    }

    public void registerAllBanInterceptor(View view) {

    }

    public void registerLoginInterceptor(View view) {
    }

}
