package com.xiaojinzi.componentdemo.view;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.router.AppApi;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.componentdemo.R;

/**
 * 启动界面
 */
@RouterAnno(
        path = "main"
)
public class MainAct extends AppCompatActivity {

    public static final String CHANNEL_ID = "ComponentChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getSupportActionBar().setTitle("组件化方案:(路由、服务、生命周期)");
        createNotificationChannel();
        startProxyRouter(getIntent().getExtras());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startProxyRouter(intent.getExtras());
    }

    private void startProxyRouter(@Nullable Bundle bundle) {
        if (Router.isProxyIntentExist(bundle)) {
            Router.with(this)
                    .proxyBundle(bundle)
                    .forward();
        }
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
        Router.withApi(AppApi.class)
                .goToTestRouter(
                        () -> Toast.makeText(MainAct.this, "跳转后的提示", Toast.LENGTH_SHORT).show()
                );
    }

    public void testRouterForFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("age", 26);
        Fragment fragment = Router
                .with("component1.fragment")
                .navigate();
        if (fragment == null) {
            Toast.makeText(this, "没有找到 'component1.fragment' 对应的 Fragment", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "找到了 'component1.fragment' 对应的 Fragment：：" + fragment, Toast.LENGTH_SHORT).show();
        }
    }

    public void testWebRouter(View view) {
        Router
                .with(this)
                .host(ModuleConfig.Help.NAME)
                .path(ModuleConfig.Help.TEST_WEB_ROUTER)
                .afterStartAction(() -> {
                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    );
                })
                .forward();
    }

    public void testQuality(View view) throws Exception {
        Router.withApi(AppApi.class)
                .goToTestQuality()
                .subscribe();
    }

    public void intentRouter1(View view) {
        Intent intent = Router.newProxyIntentBuilder()
                .host(ModuleConfig.User.NAME)
                .path(ModuleConfig.User.PERSON_CENTER)
                .buildProxyIntent();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("测试点击跳转")
                .setContentText("使用默认代理Activity, 点我跳转到用户中心, 框架自动完成登陆过程")
                .setContentIntent(PendingIntent.getActivity(getApplication(), 0, intent, 0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(1, builder.build());
    }

    public void intentRouter2(View view) {
        Intent intent = Router.newProxyIntentBuilder()
                .host(ModuleConfig.User.NAME)
                .path(ModuleConfig.User.PERSON_CENTER)
                .proxyActivity(this.getClass())
                .buildProxyIntent();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("测试点击跳转")
                .setContentText("使用 MainAct 为代理界面, 点我跳转到用户中心, 框架自动完成登陆过程")
                .setContentIntent(PendingIntent.getActivity(getApplication(), 0, intent, 0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(2, builder.build());
    }

    public void testService(View view) {
        Router
                .with(this)
                .host(ModuleConfig.App.NAME)
                .path(ModuleConfig.App.TEST_SERVICE)
                .forward();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Component";
            String description = "ComponentDesc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Toast.makeText(this, "返回数据啦", Toast.LENGTH_SHORT).show();
        }
    }

}
