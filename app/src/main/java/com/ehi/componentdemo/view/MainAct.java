package com.ehi.componentdemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ehi.base.ModuleConfig;
import com.ehi.component.anno.EHiRouterAnno;
import com.ehi.component.impl.EHiRouter;
import com.ehi.component.impl.application.EHiModuleManager;
import com.ehi.componentdemo.R;

@EHiRouterAnno(value = ModuleConfig.App.NAME, desc = "主界面")
public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getSupportActionBar().setTitle("组件化方案:(路由、服务、生命周期)");
    }

    public void loadAppModule(View v) {
        EHiModuleManager.getInstance().register(ModuleConfig.App.NAME);
    }

    public void unloadAppModule(View v) {
        EHiModuleManager.getInstance().unregister(ModuleConfig.App.NAME);
    }

    public void loadUserModule(View v) {
        EHiModuleManager.getInstance().register(ModuleConfig.User.NAME);
    }

    public void unloadUserModule(View v) {
        EHiModuleManager.getInstance().unregister(ModuleConfig.User.NAME);
    }

    public void loadHelpModule(View v) {
        EHiModuleManager.getInstance().register(ModuleConfig.Help.NAME);
    }

    public void unloadHelpModule(View v) {
        EHiModuleManager.getInstance().unregister(ModuleConfig.Help.NAME);
    }

    public void loadModule1Module(View v) {
        EHiModuleManager.getInstance().register(ModuleConfig.Module1.NAME);
    }

    public void unloadModule1Module(View v) {
        EHiModuleManager.getInstance().unregister(ModuleConfig.Module1.NAME);
    }

    public void loadModule2Module(View v) {
        EHiModuleManager.getInstance().register(ModuleConfig.Module2.NAME);
    }

    public void unloadModule2Module(View v) {
        EHiModuleManager.getInstance().unregister(ModuleConfig.Module2.NAME);
    }

    public void testRouter(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_ROUTER)
                .navigate();
    }

    public void testQuality(View view) {
        EHiRouter
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_QUALITY)
                .navigate();
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

}
