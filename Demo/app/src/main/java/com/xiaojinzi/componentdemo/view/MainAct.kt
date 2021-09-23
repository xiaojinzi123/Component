package com.xiaojinzi.componentdemo.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.xiaojinzi.base.ModuleConfig
import com.xiaojinzi.base.router.AppApi
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.forwardForTargetIntent
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.impl.application.ModuleManager
import com.xiaojinzi.componentdemo.R

/**
 * 启动界面
 */
@RouterAnno(path = "main")
class MainAct : AppCompatActivity() {

    @AttrValueAutowiredAnno("name")
    lateinit var nameTestTest: String

    @AttrValueAutowiredAnno("name1")
    var nameTestTest1: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)
        intent.putExtra("name", "testName")
        Component.inject(this)
        supportActionBar!!.title = "组件化方案:(路由、服务、生命周期)"
        createNotificationChannel()
        startProxyRouter(intent.extras)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        startProxyRouter(intent.extras)
    }

    private fun startProxyRouter(bundle: Bundle?) {
        if (Router.isProxyIntentExist(bundle)) {
            Router.with(this)
                .proxyBundle(bundle!!)
                .forward()
        }
    }

    fun loadAppModule(v: View?) {
        ModuleManager.register(ModuleConfig.App.NAME)
    }

    fun unloadAppModule(v: View?) {
        ModuleManager.unregister(ModuleConfig.App.NAME)
    }

    fun loadUserModule(v: View?) {
        ModuleManager.register(ModuleConfig.User.NAME)
    }

    fun unloadUserModule(v: View?) {
        ModuleManager.unregister(ModuleConfig.User.NAME)
    }

    fun loadHelpModule(v: View?) {
        ModuleManager.register(ModuleConfig.Help.NAME)
    }

    fun unloadHelpModule(v: View?) {
        ModuleManager.unregister(ModuleConfig.Help.NAME)
    }

    fun loadModule1Module(v: View?) {
        ModuleManager.register(ModuleConfig.Module1.NAME)
    }

    fun unloadModule1Module(v: View?) {
        ModuleManager.unregister(ModuleConfig.Module1.NAME)
    }

    fun loadModule2Module(v: View?) {
        ModuleManager.register(ModuleConfig.Module2.NAME)
    }

    fun unloadModule2Module(v: View?) {
        ModuleManager.unregister(ModuleConfig.Module2.NAME)
    }

    fun testRouter(view: View?) {
        Router.withApi(AppApi::class.java)
            .goToTestRouter { Toast.makeText(this@MainAct, "跳转后的提示", Toast.LENGTH_SHORT).show() }
    }

    fun testRouterForFragment(view: View?) {
        val bundle = Bundle()
        bundle.putInt("age", 26)
        val fragment = Router
            .with("component1.fragment")
            .navigate()
        if (fragment == null) {
            Toast.makeText(this, "没有找到 'component1.fragment' 对应的 Fragment", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "找到了 'component1.fragment' 对应的 Fragment：：$fragment",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun testWebRouter(view: View?) {

        Router
            .with(this@MainAct)
            .host(ModuleConfig.Help.NAME)
            .path(ModuleConfig.Help.TEST_WEB_ROUTER)
            .afterStartAction {
                overridePendingTransition(
                    android.R.anim.fade_in,
                    0
                )
            }
            .forwardForTargetIntent { targetIntent ->
                startActivity(targetIntent)
                overridePendingTransition(
                    android.R.anim.fade_in,
                    0
                )
            }

        /*GlobalScope.launch {
            val targetIntent = Router
                .with(this@MainAct)
                .host(ModuleConfig.Help.NAME)
                .path(ModuleConfig.Help.TEST_WEB_ROUTER)
                .afterStartAction {
                    overridePendingTransition(
                        android.R.anim.fade_in,
                        0
                    )
                }
                .targetIntentAwait()
            startActivity(targetIntent)
            overridePendingTransition(
                android.R.anim.fade_in,
                0
            )
        }*/

    }

    @Throws(Exception::class)
    fun testQuality(view: View?) {
        Router.withApi(AppApi::class.java)
            .goToTestQuality()
            .subscribe()
    }

    fun intentRouter1(view: View?) {
        val intent = Router.newProxyIntentBuilder()
            .host(ModuleConfig.User.NAME)
            .path(ModuleConfig.User.PERSON_CENTER + "1")
            .buildProxyIntent()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.app_icon)
            .setContentTitle("测试点击跳转")
            .setContentText("使用默认代理Activity, 点我跳转到用户中心, 框架自动完成登陆过程")
            .setContentIntent(PendingIntent.getActivity(application, 0, intent, 0))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(1, builder.build())
    }

    fun intentRouter2(view: View?) {
        val intent = Router.newProxyIntentBuilder()
            .host(ModuleConfig.User.NAME)
            .path(ModuleConfig.User.PERSON_CENTER + "1")
            .proxyActivity(this.javaClass)
            .buildProxyIntent()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.app_icon)
            .setContentTitle("测试点击跳转")
            .setContentText("使用 MainAct 为代理界面, 点我跳转到用户中心, 框架自动完成登陆过程")
            .setContentIntent(PendingIntent.getActivity(application, 0, intent, 0))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(2, builder.build())
    }

    fun testService(view: View?) {
        Router
            .with(this)
            .host(ModuleConfig.App.NAME)
            .path(ModuleConfig.App.TEST_SERVICE)
            .forward()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Component"
            val description = "ComponentDesc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Toast.makeText(this, "返回数据啦", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val CHANNEL_ID = "ComponentChannel"
    }
}