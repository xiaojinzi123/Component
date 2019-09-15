package com.xiaojinzi.componentdemo.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.router.AppApi;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.componentdemo.R;

/**
 * 启动界面
 */
public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getSupportActionBar().setTitle("组件化方案:(路由、服务、生命周期)");
    }

    public void loadAppModule(View v) {
        ModuleManager.getInstance().register(ModuleConfig.App.NAME);
    }

    public void unloadAppModule(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.App.NAME);
    }

    public void loadUserModule(View v) {
        ModuleManager.getInstance().register(ModuleConfig.User.NAME);
    }

    public void unloadUserModule(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.User.NAME);
    }

    public void loadHelpModule(View v) {
        ModuleManager.getInstance().register(ModuleConfig.Help.NAME);
    }

    public void unloadHelpModule(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.Help.NAME);
    }

    public void loadModule1Module(View v) {
        ModuleManager.getInstance().register(ModuleConfig.Module1.NAME);
    }

    public void unloadModule1Module(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.Module1.NAME);
    }

    public void loadModule2Module(View v) {
        ModuleManager.getInstance().register(ModuleConfig.Module2.NAME);
    }

    public void unloadModule2Module(View v) {
        ModuleManager.getInstance().unregister(ModuleConfig.Module2.NAME);
    }

    public void testRouter(View view) {
        Router.withApi(AppApi.class).goToTestRouter(
                () -> Toast.makeText(MainAct.this, "跳转后的提示", Toast.LENGTH_SHORT).show()
        );
    }

    public void testWebRouter(View view) {
        Router
                .with(this)
                .host(ModuleConfig.Help.NAME)
                .path(ModuleConfig.Help.TEST_WEB_ROUTER)
                .navigate();
    }

    public void testQuality(View view) {
        Router.withApi(AppApi.class)
                .goToTestQuality()
                .subscribe();
    }

    public void testService(View view) {
        Router
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_SERVICE)
                .navigate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Toast.makeText(this, "返回数据啦", Toast.LENGTH_SHORT).show();
        }
    }

}
