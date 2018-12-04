package com.ehi.componentdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiModuleManager;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.EHiRouterResult;
import com.ehi.component.support.EHiCallbackAdapter;
import com.ehi.componentdemo.R;

@EHiRouterAnno(value = "main", desc = "主界面")
public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getSupportActionBar().setTitle("组件化方案:(路由、服务、生命周期)");

        EHiModuleManager.getInstance().register(ModuleConfig.App.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.Component1.NAME);
        EHiModuleManager.getInstance().register(ModuleConfig.Component2.NAME);

    }

    public void testRouter(View view) {

        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_ROUTER)
                .navigate(new EHiCallbackAdapter(){
                    @Override
                    public void onSuccess(@NonNull EHiRouterResult result) {
                        super.onSuccess(result);
                    }

                    @Override
                    public void onError(@NonNull Exception error) {
                        super.onError(error);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Toast.makeText(this, "返回数据啦", Toast.LENGTH_SHORT).show();
        }
    }

    public void testService(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_SERVICE)
                .navigate();
    }

    public void testInterceptor(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_INTERCEPTOR)
                .navigate();
    }

    public void testError(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_ERROR)
                .navigate();
    }

}
